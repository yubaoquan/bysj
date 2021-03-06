package client.net.up;

import beans.Constant;
import beans.MailBean;
import beans.UserBean;
import util.Util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

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
	private UserBean user = null;
	private boolean sendSucceed = false;
	private MimeMessage msg;
	private Selector initSelector = null;
	private Selector selectorForRead = null;
	private Selector selectorForWrite = null;
	private SocketChannel socketChannel = null;
	private ByteBuffer buffer = null;
	private Socket socket = null;
	private OutputStream os = null;

	public Transmitter() {
		try {
			initConnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Transmitter(UserBean user) {
		this.user = user;
		if (!user.isLocalServerEnabled()) {
			props = new Properties();
			props.setProperty("mail.smtp.auth", "true");
			props.setProperty("mail.transport.protocol", "smtp");
			session = Session.getDefaultInstance(props);
			msg = new MimeMessage(session);
		}
		try {
			if (user.isLocalServerEnabled()) {
				initConnect();
			} else {
				transport = session.getTransport();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean loginToServer() {
		if (user.isLocalServerEnabled()) {
			return loginToLocalServer();
		} else {
            return loginToInternetServer();
        }
	}

	private boolean loginToInternetServer() {
		try {
		    transport.connect(user.getSmtpServerName(), user.getUserName(), user.getPassword());
		} catch (AuthenticationFailedException e) {
		    System.out.println("认证失败!用户名或密码错误");
		    e.printStackTrace();
		    return false;
		} catch (MessagingException e) {
		    e.printStackTrace();
		    return false;
		}
		return true;
	}

	private boolean loginToLocalServer() {
		String requestString = Constant.USER_AUTHENTICATION + " " + user.getUserName().trim() + " " + user.getPassword().trim();
		sendRequest(requestString);
		System.out.println("send finished!");
		String responseString = receiveResponse();
		if (responseString.equals(Constant.LOGIN_SUCCEED)) {
			return true;
		} else {
			return false;
		}
	}

	private void initConnect() throws IOException {
		SocketAddress socketAddress = new InetSocketAddress(Constant.SERVER_HOSTNAME, Constant.MAIN_SERVER_PORT);
		socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		initSelector = Selector.open();
		socketChannel.register(initSelector, SelectionKey.OP_CONNECT);

		socketChannel.connect(socketAddress);
		initSelector.select();
		
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
				}
			}
		}
	}

	private void initReadAndWriteSelector() throws IOException {
		selectorForRead = Selector.open();
		selectorForWrite = Selector.open();

		socketChannel.register(selectorForRead, SelectionKey.OP_READ);
		socketChannel.register(selectorForWrite, SelectionKey.OP_WRITE);
	}

	public void sendRequest(String requestString) {
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
						buffer = ByteBuffer.wrap(requestString.getBytes("UTF-8"));
					//	System.out.println("write...");
						channel.write(buffer);
						// channel.close();
						System.out.println("write:" + requestString);
						return;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String receiveResponse() {
		String responseString;
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
		return null;
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
			if (mailBean.getAttachmentAmount() > 0) {
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
		msg = new MimeMessage(session);
		this.sendSucceed = true;
	}

	private void sendMailToLocalServer(MailBean mail) {
		System.out.println("Send mail to local server");
		StringBuffer request = new StringBuffer();
        request.append(Constant.SEND_MAIL);
        request.append(" ");
        request.append(mail.getSender());
        request.append(" ");
        request.append(mail.getAddressee());
        request.append(" ");
        request.append(mail.getSendTime().toString());
        request.trimToSize();
		sendRequest(request.toString());
		receiveResponse();

		request = new StringBuffer();
		request.append(mail.getSubject());
		request.trimToSize();
		sendRequest(request.toString());
		receiveResponse();

		request = new StringBuffer();
		request.append(mail.getText());
		request.trimToSize();
		sendRequest(request.toString());
		receiveResponse();
		System.out.println("Previous step execute OK.\n Now sending attachment[s]...");
		sendAttachment(mail);
	}

	private void sendAttachment(MailBean mail) {
		System.out.println("client connected");
		StringBuffer request;
		request = new StringBuffer();
		int attachmentsAmount = mail.getAttachmentAmount();
		if (attachmentsAmount <= 0) {
			request.append("0");
			request.trimToSize();
			sendRequest(request.toString());
			receiveResponse();
        } else {
			request.append(attachmentsAmount);
			request.trimToSize();
			sendRequest(request.toString());
			receiveResponse();

			for (int i = 0; i < mail.getAttachmentAmount(); i++) {
				File attachment = mail.getAttachment(i);
				System.out.println("Attachment[" + i + "]: " + attachment.getName());
				sendRequest(attachment.getName());
				receiveResponse();
				sendRequest("" + attachment.length());
				receiveResponse();
				sendFile(attachment);
				System.out.println("sent a file");
				receiveResponse();
			}
			closeResources();
		}
	}

	@SuppressWarnings("unused")
	private void sendFile(File file) throws AssertionError {
		PrintStream ps = null;
		
		FileInputStream fis = null;
		try {
			//ps = new PrintStream(new FileOutputStream("E:/TransmitterLog.txt"));
			//System.setOut(ps);

			fis = new FileInputStream(file);
			int readSize;
			long totalRead = 0;
			System.out.println("File length: " + file.length());
				connectToFileServer();
			byte[] buffer = new byte[1024];
			while ((readSize = fis.read(buffer)) > 0) {
				totalRead += readSize;
				//System.out.println("total read " + totalRead);
				os.write(buffer, 0, readSize);
				buffer = new byte[1024];
			}
			System.out.println("Send file finish.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
                os.close();
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
				e.printStackTrace();
			}
           /* if (ps != null) {
                ps.close();
            }*/
        }
	}

	private void closeResources() {
		try {
			if (os != null) {
			    os.close();
			}
			if (socket != null) {
			    socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void connectToFileServer() {
			try {
				socket = new Socket("127.0.0.1", Constant.FILE_SERVER_PORT);
				os = socket.getOutputStream();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
	}

	public void closeConnection() {
		if (user.isLocalServerEnabled()) {
			sendRequest(Constant.EXIT + " ");
			try {
				socketChannel.close();
				System.out.println("Socket channel closed!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (transport.isConnected()) {
				try {
					transport.close();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean sendSucceed() {
		return sendSucceed;
	}

	public static void main(String[] args) {
		UserBean li = new UserBean();
		boolean result;
		li.setUserName("admin");
		li.setPassword("admin");
		result = new Transmitter(li).loginToLocalServer();
		Util.println("result: " + result);

	}
}
