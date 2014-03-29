package client.net.up;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import util.Util;
import beans.MailBean;
import beans.UserLoginBean;


public class Transmitter {

	private Properties props;
	private Session session;
	private Transport transport = null;
	private static final int PORT = 25;
	private boolean loginSucceed = false;
	private UserLoginBean loginInformation;
	private static Transmitter transmitter = null;
	private boolean sendSucceed = false;
	private MimeMessage msg;
	private Selector selector = null;
	private SocketChannel socketChannel = null;
	private ByteBuffer byteBuffer = null;
	
	public static Transmitter getInstance(UserLoginBean loginInformation) {
		if (Transmitter.transmitter == null) {
			transmitter = new Transmitter(loginInformation);
		}
		return transmitter;
	}
	
	private Transmitter(UserLoginBean loginInformation) {
		this.loginInformation = loginInformation;
		props = new Properties();
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.transport.protocol", "smtp");
		session = Session.getDefaultInstance(props);
		msg = new MimeMessage(session);
		try {
			transport = session.getTransport();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
	}
	public boolean loginServerSucceed() {
		return this.loginSucceed;
	}
	
	public boolean loginToServer() {
		boolean loginSucceed = false;
		if (loginInformation.isLocalServerEnabled()) {
			loginSucceed = loginToLocalServer();
		} else {
			try {
				this.transport.connect(loginInformation.getSmtpServerName(), loginInformation.getUserName(), loginInformation.getPassword());
				loginSucceed = true;
			} catch (AuthenticationFailedException e) {
				System.out.println("认证失败!用户名或密码错误");
				loginSucceed = false;
				return loginSucceed;
			} catch (MessagingException e) {
				e.printStackTrace();
				loginSucceed = false;
				return loginSucceed;
			}
		}
		return loginSucceed;
	}
	
	private boolean loginToLocalServer() {
		boolean loginSucceed = false;
		try {
			SocketAddress socketAddress = new InetSocketAddress("localhost", 8888);
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			selector = Selector.open();
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
			socketChannel.connect(socketAddress);
			selector.select();
			Set<SelectionKey> selectionKeys = selector.selectedKeys();
			Iterator<SelectionKey> it = selectionKeys.iterator();
			if (it.hasNext()) {
				SelectionKey selectionKey = it.next();
				if (selectionKey.isConnectable()) {
					Util.println("client connect");
					socketChannel = (SocketChannel) selectionKey.channel();
					// 判断此通道上是否正在进行连接操作。
					// 完成套接字通道的连接过程。
					if (socketChannel.isConnectionPending()) {
						socketChannel.finishConnect();
						System.out.println("完成连接!");

						ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
						String request = "1" + " " + loginInformation.getUserName().trim() + " " + loginInformation.getPassword().trim();
						buffer = ByteBuffer.wrap(request.getBytes());
						socketChannel.write(buffer);
						buffer.clear();
						
						System.out.println("send finished!");
						socketChannel.socket().close();
						socketChannel.close();
						System.exit(0);
						socketChannel.register(selector, SelectionKey.OP_READ);
					
					}
				}

			}
			selectionKeys.clear();
			
			
			/*System.out.println(loginResult);
			if (loginResult.equals("true")) {
				loginSucceed = true;
			}*/
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return loginSucceed;
		} catch (IOException e) {
			e.printStackTrace();
			return loginSucceed;
		}
		return loginSucceed;
	}

	public void sendMailToLocalServer(MailBean mailBean) {
		
	}
	
	public void sendMail(MailBean mailBean) {
		this.fillMsg(mailBean);
		this.sendMail();
	}

	public void fillMsg(MailBean mailBean) {
		try {
			InternetAddress fromAddress = new InternetAddress(transmitter.loginInformation.getUserName());
			msg.setFrom(fromAddress);
			msg.setSubject(mailBean.getSubject());
			msg.setText(mailBean.getText());
			if (mailBean.getExtraItemsAmount() > 0) {
				msg.setContent(mailBean.getMutipart());
			}
			msg.setSentDate(new Date());
			msg.setRecipients(Message.RecipientType.TO, mailBean.getReceiverAddresses());
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMail() {
		try {
			transport.sendMessage(msg,msg.getAllRecipients());
		} catch (MessagingException e) {
			this.sendSucceed = false;
			e.printStackTrace();
			closeConnection();
			return;
		}
		this.sendSucceed = true;
	}
	
	public void closeConnection() {
		if (transport.isConnected()) {
			try {
				transport.close();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean sendSucceed() {
		return sendSucceed;
	}
	
	/**
	 * @param args
	 */
	
	private void listen() throws Exception {
		while (true) {
			// 选择一组键，其相应的通道已为 I/O 操作准备就绪。
			// 此方法执行处于阻塞模式的选择操作。
			selector.select();
			Set<SelectionKey> selectionKeys = selector.selectedKeys();
			Iterator<SelectionKey> it = selectionKeys.iterator();
			while (it.hasNext()) {
				SelectionKey selectionKey = it.next();
				handleKey(selectionKey);

			}
			selectionKeys.clear();
		}
	}
	
	private void handleKey(SelectionKey selectionKey) throws Exception {
		if (selectionKey.isConnectable()) {
			System.out.println("client connect");
			socketChannel = (SocketChannel) selectionKey.channel();
			// 判断此通道上是否正在进行连接操作。
			// 完成套接字通道的连接过程。
			if (socketChannel.isConnectionPending()) {
				socketChannel.finishConnect();
				System.out.println("完成连接!");
				/*byteBuffer.clear();
				byteBuffer.put("Hello,Server".getBytes());
				byteBuffer.flip();
				socketChannel.write(byteBuffer);*/
				byteBuffer = ByteBuffer.wrap("你好，世界 | こんにちは、世界中のみなさん".getBytes("UTF-8"));
				socketChannel.write(byteBuffer);
				byteBuffer.clear();
				System.out.println("send finished!");
				socketChannel.register(selector, SelectionKey.OP_READ);
			}
		} else if (selectionKey.isReadable()) {
			socketChannel = (SocketChannel) selectionKey.channel();
			// 将缓冲区清空以备下次读取
			byteBuffer.clear();
			
			StringBuffer stringBuffer = new StringBuffer();
			int size = 0;
			while ((size = socketChannel.read(byteBuffer)) > 0) {
				byteBuffer.flip();
				byte[] array = new byte[1024];
				byteBuffer.get(array, 0, size);
				stringBuffer.append(new String(array));
				byteBuffer.clear();
			}
			System.out.println("receive: " + stringBuffer);
			socketChannel.register(selector, SelectionKey.OP_WRITE);
		} else if (selectionKey.isWritable()) {
			byteBuffer.clear();
			socketChannel = (SocketChannel) selectionKey.channel();
			byteBuffer = ByteBuffer.wrap("再次发送数据".getBytes("UTF-8"));
			int size = socketChannel.write(byteBuffer);
			byteBuffer.clear();
			byte[] array = new byte[1024];
			byteBuffer.get(array, 0, size);
			String string = new String(array).trim();
			System.out.println("客户端向服务器端发送数据--：" + string);
			socketChannel.register(selector, SelectionKey.OP_READ);
			socketChannel.socket().close();
			socketChannel.close();
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		UserLoginBean li = new UserLoginBean();
		li.setUserName("username2");
		li.setPassword("password2");
		new Transmitter(li).loginToLocalServer();

	}
}
