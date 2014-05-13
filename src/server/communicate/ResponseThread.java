package server.communicate;

import static java.lang.System.out;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import server.DAO.DAO;
import server.DAO.DAOFactory;
import util.Util;
import beans.Constant;
import beans.LocalMailBean;
import beans.MailBean;
import beans.UserBean;

public class ResponseThread implements Runnable {
	
	private Selector selectorForRead = null;
	private Selector selectorForWrite = null;
	private SocketChannel socketChannel = null;
	private ByteBuffer buffer = null;
	private DAO dao = DAOFactory.getDAOInstance();
	private ServerSocket fileserverSocket;
	private Socket client;
	private InputStream is;
	private ObjectOutputStream oos;
	private boolean userConnected = false;
	private UserBean user = new UserBean();
	private boolean threadAlive = true;

	public ResponseThread(SocketChannel channel, ServerSocket serverSocket) {
		this.fileserverSocket = serverSocket;
		socketChannel = channel;
		buffer = ByteBuffer.allocateDirect(1024);
		try {
			selectorForRead = Selector.open();
			selectorForWrite = Selector.open();
			socketChannel.register(selectorForRead, SelectionKey.OP_READ);
			socketChannel.register(selectorForWrite, SelectionKey.OP_WRITE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (threadAlive) {
				String request = receiveRequest();
				operateByRequestParams(request);
				Util.println("here");
			}
			System.out.println("Thread terminated");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 此方法用于从客户端获取请求信息
	 * @return
	 */
	private String receiveRequest() {
		Util.println("receive request");
		String request = null;
		try {
			while (selectorForRead.select() > 0) {
				Set<SelectionKey> selectionKeys = selectorForRead.selectedKeys();
				Iterator<SelectionKey> it = selectionKeys.iterator();
				while (it.hasNext()) {
					SelectionKey selectionKey = it.next();
					it.remove();
					if (selectionKey.isReadable()) {
						SocketChannel channel = (SocketChannel) selectionKey.channel();
						ByteBuffer buffer = ByteBuffer.allocate(1024);

						channel.read(buffer);

						buffer.flip();
						byte[] array = new byte[buffer.remaining()];
						buffer.get(array, 0, buffer.remaining());
						request = new String(array, "UTF-8");
						System.out.println("read: " + request);
						return request;
					}
				}
			}
		} catch (IOException e) {
			out.println("连接断开");
			return Constant.EXIT + " ";
		}
		return request;
	}

	/**
	 * 此方法用于向客户端发送响应信息
	 * @param responseString 响应信息的报文
	 */
	private void sendResponse(String responseString) {
		Util.println("send response: " + responseString);
		try {
			while (selectorForWrite.select() > 0) {
				Set<SelectionKey> selectionKeys = selectorForWrite.selectedKeys();
				Iterator<SelectionKey> it = selectionKeys.iterator();
				while (it.hasNext()) {
					SelectionKey selectionKey = it.next();
					it.remove();
					if (selectionKey.isWritable()) {
						SocketChannel channel = (SocketChannel) selectionKey.channel();
						buffer = ByteBuffer.wrap(responseString.getBytes());
						channel.write(buffer);
						System.out.println("write:" + responseString);
						return;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void operateByRequestParams(String request) throws Exception {
		int index = request.indexOf(" ");
		System.out.println("Request: (" + request + ")");
		int operationCode = Integer.parseInt(request.substring(0, index));
		Util.println("operation code: " + operationCode);
		String requestBody = request.substring(index + 1);
		switch (operationCode) {
			case Constant.FIND_USER_NAME:
				handleFindUser(requestBody);
				break;
			case Constant.ADD_NEW_USER:
				handleAddNewUser(requestBody);
				break;
			case Constant.USER_AUTHENTICATION:
				handleUserLogin(requestBody);
				break;
			case Constant.LIST_MAILS:
				handleListMails();
				break;
			case Constant.SEND_MAIL:
				handleSendMail(requestBody);
				break;
			case Constant.DOWNLOAD_ATTACHMENT:
				handleDownloadAttachment(requestBody);
				break;
			case Constant.EXIT:
				handleExit();
			default:
				break;
		}
	}

	private void handleFindUser(String requestBody) {
		String username = requestBody;
		out.println("Username: " + username);
		if (dao.userExists(username)) {
			sendResponse(String.valueOf(Constant.FOUND));
		} else {
			sendResponse(String.valueOf(Constant.NOT_FOUND));
		}
		
	}

	private void handleAddNewUser(String requestBody) {
		int index = requestBody.indexOf(" ");
		String usernameLengthString = requestBody.substring(0, index);
		int usernameLength = Integer.parseInt(usernameLengthString);
		out.println("user name length: " + usernameLength);
		requestBody = requestBody.substring(index + 1);
		String username = requestBody.substring(0, usernameLength);
		String password = requestBody.substring(usernameLength);
		out.println("user name: " + username);
		out.println("password: " + password);
		
		if (dao.userExists(username)) {
			sendResponse(Constant.FAILED + "");
		} else {
			dao.insertNewUser(username, password);
			sendResponse(Constant.SUCCEED + "");
		}
		
	}
	
	private void handleUserLogin(String params) {
		Util.println("check username and password");
		String[] array = params.split(" ");
		String username = array[0];
		String password = array[1];
		System.out.println("Username: " + username + ", password: " + password);
		userLogin(username, password);
	}

	private void userLogin(String username, String password) {
		try {
			if (userLoginPermitted(username, password)) {
				sendResponse(Constant.LOGIN_SUCCEED);
				user.setUserName(username);
				userConnected = true;
			} else {
				sendResponse(Constant.LOGIN_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleListMails() {
		System.out.println("select mails in database... user: " + user.getUserName());
		if (userConnected) {
			List<LocalMailBean> mails = new ArrayList<>();
			//TODO
			getASocketFromClient();
			try {
				oos = new ObjectOutputStream(client.getOutputStream());
				mails = dao.listMails(user.getUserName()); 
				oos.writeObject(mails);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} else {
			System.err.println("Not connected!");
		}
	}

	private void handleSendMail(String mailStrng) {
		MailBean mail = new MailBean();
		if (userConnected) {
			System.out.println("Send mail");
			String[] params = mailStrng.split(" ");
			String sender = params[0];
			String addressee = params[1];
			String sentTimeString = params[2] + " " + params[3];
			Timestamp sentTime = Timestamp.valueOf(sentTimeString);
			System.out.println("Sender: " + sender);
			System.out.println("Addressee: " + addressee);
			System.out.println("Sent time: " + sentTime);
			sendResponse("OK");

			String mailSubject = receiveRequest();
			System.out.println("Subject: " + mailSubject);
			sendResponse("OK");

			String mailContent = receiveRequest();
			System.out.println("Mail content: " + mailContent);
			sendResponse("OK");

			System.out.println("Previous step execute OK.\n Now receiving attachment[s]...");
			
			fillMail(mail, sender, addressee, sentTime, mailSubject, mailContent);
			receiveAttachments(mail);
			dao.insertMailIntoMailbox(mail);
			System.out.println("邮件插入数据库成功");
		} else {
			System.err.println("Not connected!");
		}
	}

	private void fillMail(MailBean mail, String sender, String addressee, Timestamp sentTime, String mailSubject, String mailContent) {
		mail.setSender(sender);
		mail.setAddressee(addressee);
		mail.setSendTime(sentTime);
		mail.setSubject(mailSubject);
		mail.setContent(mailContent);
	}

	private void receiveAttachments(MailBean mail) {
		//startServerSocket();
		System.out.println("connected");
		StringBuffer attachmentNames = new StringBuffer();
		StringBuffer attachmentLocations = new StringBuffer("");
		String attachmentsCountString = receiveRequest();
		int attachmentsCount = Integer.parseInt(attachmentsCountString);
		sendResponse("OK");
		System.out.println(attachmentsCount + " attachments.");
		for (int i = 0; i < attachmentsCount; i++) {
			try {
				String attachmentName = receiveRequest();
				attachmentNames.append(attachmentName).append(Constant.ATTACHMENTS_SEPARATOR);
				sendResponse("OK");
				String fileLengthString = receiveRequest();
				sendResponse("OK");
				System.out.println("File lentgh: " + fileLengthString);
				long fileLength = Long.parseLong(fileLengthString);
				String attachmentLocation = receiveFile(mail, i, attachmentName, fileLength);
				System.out.println("receiveFile() called once");
				attachmentLocations.append(attachmentLocation).append(Constant.ATTACHMENTS_SEPARATOR);
				sendResponse("OK");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mail.setAttachmentNames(attachmentNames.toString());
		mail.setAttachmentLocations(attachmentLocations.toString());
	}

	private String receiveFile(MailBean mail, int offset, String attachmentName, long fileLength) throws IOException, FileNotFoundException {
		File attachment = makeFile(mail, offset, attachmentName);
		writeToFile(attachment, fileLength);
		return attachment.getAbsolutePath();
	}

	private File makeFile(MailBean mail, int offset, String attachmentName) throws IOException {
		String attachmentFolderName = Constant.LOCAL_ATTACHMENTS_ROOT_PATH_FOR_SERVER + mail.getAddressee() + "/" + mail.getSendTime().toString().replace(":", "-") + "/" + mail.getSender() + "/" + offset;
		File attachmentFolder = new File(attachmentFolderName);
		if (!attachmentFolder.exists()) {
			attachmentFolder.mkdirs();
		}
		String attachmentPath = attachmentFolderName + "/" + attachmentName;
		System.out.println("File path: " + attachmentPath);
		File attachment = new File(attachmentPath);
		if (!attachment.exists()) {
			attachment.createNewFile();
		}
		return attachment;
	}

	private void writeToFile(File attachment, long fileLength) throws IOException, FileNotFoundException {
		//PrintStream ps = null;
		FileOutputStream fos = null;
		try {
			//ps = new PrintStream(new FileOutputStream("E:/log.txt"));
			//System.setOut(ps);

			getASocketFromClient();
			is = client.getInputStream();
			fos = new FileOutputStream(attachment);
			byte[] buffer = new byte[1024];
			int readSize = 0;
			@SuppressWarnings("unused")
			int totalRead = 0;
			System.out.println("here1");
			while ((readSize = is.read(buffer)) > 0) {
				totalRead += readSize;
				//System.out.println(readSize);
				//System.out.println("Total read: " + totalRead);
				fos.write(buffer, 0, readSize);
				buffer = new byte[1024];
			}
			System.out.println("receive ok");

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			is.close();
			fos.close();
			//ps.close();
		}
	}

	private void getASocketFromClient() {
		try {
			client = fileserverSocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void handleDownloadAttachment(String requestBody) {
		//TODO
		FileInputStream fis = null;
		OutputStream os = null;
		String[] params = requestBody.split(" ");
		int mailID = Integer.parseInt(params[0]);
		int offset = Integer.parseInt(params[1]);
		out.println("download attachment: mail id-" + mailID + " offset-" + offset);
		String filePath = dao.findAttachmentLocationByOffset(mailID, offset);
		out.println("Attachment path: " + filePath);
		try {
			fis = new FileInputStream(new File(filePath));
			int readSize;
			@SuppressWarnings("unused")
			long totalRead = 0;
			this.getASocketFromClient();
			os = client.getOutputStream();
			byte[] buffer = new byte[1024];
			while ((readSize = fis.read(buffer)) > 0) {
				totalRead += readSize;
				os.write(buffer, 0, readSize);
				buffer = new byte[1024];
			}
			System.out.println("Send file finish.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void handleExit() throws IOException {
		socketChannel.close();
		if (client != null) {
			client.close();
		}
		threadAlive = false;
	}

	private boolean userLoginPermitted(String username, String password) {
		return getDAO().usernameAndPasswordExists(username, password);
	}

	public DAO getDAO() {
		return dao;
	}
}
