package client.net.up;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
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
import beans.Constant;
import beans.MailBean;
import beans.UserBean;

/**
 * Class for sending mail to mail server
 * 
 * @author yubaoquan
 *
 */
public class Transmitter {

	private Properties props;
	private Session session;
	private Transport transport = null;
	private static final int PORT = 25;
	private boolean loginSucceed = false;
	private UserBean user = null;
	private static Transmitter transmitter = null;
	private boolean sendSucceed = false;
	private MimeMessage msg;
	private Selector initSelector = null;
	private Selector selectorForRead = null;
	private Selector selectorForWrite = null;
	private SocketChannel socketChannel = null;
	private ByteBuffer buffer = null;

	public static Transmitter getInstance(UserBean user) {
		if (Transmitter.transmitter == null) {
			transmitter = new Transmitter(user);
		}
		return transmitter;
	}

	private Transmitter(UserBean user) {
		this.user = user;
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
		if (user.isLocalServerEnabled()) {
			loginSucceed = loginToLocalServer();
		} else {
			try {
				this.transport.connect(user.getSmtpServerName(), user.getUserName(), user.getPassword());
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
			initConnect();
			Set<SelectionKey> selectionKeys = initSelector.selectedKeys();
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
						initReadAndWriteSelector();

						String requestString = Constant.USER_AUTHENTICATION + " " + user.getUserName().trim() + " " + user.getPassword().trim();
						sendRequest(requestString);

						System.out.println("send finished!");
						String responseString = receiveResponse();
						if (responseString.equals(Constant.LOGIN_SUCCEED)) {
							loginSucceed = true;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return loginSucceed;
		}
		return loginSucceed;
	}

	private void initConnect() throws IOException, ClosedChannelException {
		SocketAddress socketAddress = new InetSocketAddress(Constant.SERVER_HOSTNAME, Constant.SERVER_PORT);
		socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		initSelector = Selector.open();
		socketChannel.register(initSelector, SelectionKey.OP_CONNECT);

		socketChannel.connect(socketAddress);
		initSelector.select();
	}

	private void initReadAndWriteSelector() throws IOException, ClosedChannelException {
		selectorForRead = Selector.open();
		selectorForWrite = Selector.open();

		socketChannel.register(selectorForRead, SelectionKey.OP_READ);
		socketChannel.register(selectorForWrite, SelectionKey.OP_WRITE);
	}

	private void sendRequest(String requestString) {
		Util.println("send request: " + requestString);
		try {
			while (selectorForWrite.select() > 0) {
				Set<SelectionKey> selectionKeys = selectorForWrite.selectedKeys();
				Iterator<SelectionKey> it = selectionKeys.iterator();
				while (it.hasNext()) {
					SelectionKey selectionKey = it.next();
					it.remove();
					if (selectionKey.isWritable()) {
						SocketChannel channel = (SocketChannel) selectionKey.channel();
						buffer = ByteBuffer.wrap(requestString.getBytes());
						System.out.println("write...");
						channel.write(buffer);
						System.out.println("write:" + requestString);
						return;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String receiveResponse() {
		String responseString = null;
		try {
			while (selectorForRead.select() > 0) {
				Set<SelectionKey> selectionKeys = selectorForRead.selectedKeys();
				Iterator<SelectionKey> it = selectionKeys.iterator();
				while (it.hasNext()) {
					SelectionKey selectionKey = it.next();
					it.remove();
					if (selectionKey.isReadable()) {
						SocketChannel channel = (SocketChannel) selectionKey.channel();
						buffer = ByteBuffer.allocate(50);
						System.out.println("read response...");

						channel.read(buffer);

						buffer.flip();
						byte[] array = new byte[1024];
						buffer.get(array, 0, buffer.remaining());
						responseString = new String(array).trim();
						System.out.println("response:" + responseString);
						return responseString;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseString;
	}

	public boolean sendMail(MailBean mail) {
		if (user.isLocalServerEnabled()) {
			sendMailToLocalServer(mail);
		} else {
			fillMsg(mail);
			sendMailToInternetServer();
		}
		return true;
	}

	public void fillMsg(MailBean mailBean) {
		try {
			InternetAddress fromAddress = new InternetAddress(user.getUserName());
			System.out.println("fillMsg, username: " + user.getUserName());
			msg.setFrom(fromAddress);
			msg.setSubject(mailBean.getSubject());
			msg.setText(mailBean.getText());
			if (mailBean.getAttachmentsAmount() > 0) {
				msg.setContent(mailBean.getMutipart());
			}
			msg.setSentDate(new Date());
			msg.setRecipients(Message.RecipientType.TO, mailBean.getInternetAddressees());
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public void sendMailToInternetServer() {
		try {
			transport.sendMessage(msg, msg.getAllRecipients());
		} catch (MessagingException e) {
			this.sendSucceed = false;
			e.printStackTrace();
			closeConnection();
			return;
		}
		this.sendSucceed = true;
	}

	private void sendMailToLocalServer(MailBean mail) {
		System.out.println("Send mail to local server");
		StringBuffer request = new StringBuffer();
		request.append(Constant.SEND_MAIL + " ");
		request.append(mail.getSender() + " ");
		request.append(mail.getAddressee() + " ");
		request.append(mail.getSendTime().toString());
		request.trimToSize();
		sendRequest(request.toString());
		receiveResponse();
		
		request = new StringBuffer();
		request.append(mail.getText());
		request.trimToSize();
		sendRequest(request.toString());
		receiveResponse();
		
		request = new StringBuffer();
		int attachmentsAmount = mail.getAttachmentsAmount();
		if (attachmentsAmount <= 0) {
			request.append("0");
			request.trimToSize();
			sendRequest(request.toString());
			receiveResponse();
			return;
		} else {
			request.append("" + attachmentsAmount);
			request.trimToSize();
			sendRequest(request.toString());
			receiveResponse();
			//TODO
		}
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

	public static void main(String[] args) {
		UserBean li = new UserBean();
		boolean result = false;
		/*
		 * li.setUserName("username2"); li.setPassword("password2"); boolean
		 * result = new Transmitter(li).loginToLocalServer();
		 * Util.println("result: " + result);
		 */
		li.setUserName("admin");
		li.setPassword("admin");
		result = new Transmitter(li).loginToLocalServer();
		Util.println("result: " + result);

	}
}
