package server.communicate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Set;

import server.DAO.DAO;
import server.DAO.DAOFactory;
import util.Util;
import beans.Constant;
import beans.MailBean;

public class RequestThread implements Runnable {
	private Selector selectorForRead = null;
	private Selector selectorForWrite = null;
	private SocketChannel socketChannel = null;
	private ByteBuffer buffer = null;
	private DAO dao = DAOFactory.getDAOInstance();

	private boolean userConnected = false;
	private boolean threadAlive = true;

	public RequestThread(SocketChannel channel) {
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
			e.printStackTrace();
		}
		return request;
	}

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
			case Constant.USER_AUTHENTICATION:
				handleUserLogin(requestBody);
				break;
			case Constant.LIST_MAILS:
				handleListMails();
				break;
			case Constant.SEND_MAIL:
				handleSendMail(requestBody);
				break;
			case Constant.EXIT:
				handleExit();
			default:
				break;
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
				userConnected = true;
			} else {
				sendResponse(Constant.LOGIN_FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void handleListMails() {
		if (userConnected) {
			System.out.println("List mails.");
		} else {
			System.err.println("Not connected!");
		}
	}

	private void handleSendMail(String mailStrng) {
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
			MailBean mail = new MailBean();
			mail.setSender(sender);
			mail.setAddressee(addressee);
			mail.setSentTime(sentTime);

			sendResponse("OK");

			String mailSubject = receiveRequest();
			System.out.println("Subject: " + mailSubject);
			sendResponse("OK");

			String mailContent = receiveRequest();
			System.out.println("Mail content: " + mailContent);
			sendResponse("OK");

			System.out.println("Previous step execute OK.\n Now receiving attachment[s]...");
			receiveAttachments(mail);
		} else {
			System.err.println("Not connected!");
		}
	}

	private void receiveAttachments(MailBean mail) {
		String countString = receiveRequest();
		int attachmentsCount = Integer.parseInt(countString);
		sendResponse("OK");

		if (attachmentsCount == 0) {
			System.out.println("0 attachments.");
			return;
		} else {
			System.out.println(attachmentsCount + " attachments.");
			for (int i = 0; i < attachmentsCount; i++) {
				try {
					String attachmentName = receiveRequest();
					sendResponse("OK");
					String fileLengthString = receiveRequest();
					sendResponse("OK");
					System.out.println("File lentgh: " + fileLengthString);
					long fileLength = Long.parseLong(fileLengthString);

					receiveFile(mail, i, attachmentName, fileLength);
					sendResponse("OK");

					// TODO
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void receiveFile(MailBean mail, int offset, String attachmentName, long fileLength) throws IOException, FileNotFoundException {
		File attachment = makeFile(mail, offset, attachmentName);
		writeToFile(attachment, fileLength);
	}

	private File makeFile(MailBean mail, int offset, String attachmentName) throws IOException {
		String attachmentFolderName = "E:/boxMail/attachments/" + mail.getAddressee() + "/" + mail.getSendTime().toString().replace(":", "-") + "/" + mail.getSender() + "/" + offset;
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
		PrintStream ps = null;
		try {
			// 一次创建PrintStream输出流
			ps = new PrintStream(new FileOutputStream("E:/log.txt"));
			// 将标准输出重定向到ps输出流
			System.setOut(ps);
			// 向标准输出一个字符串

			while (selectorForRead.select() > 0) {
				Set<SelectionKey> selectionKeys = selectorForRead.selectedKeys();
				Iterator<SelectionKey> it = selectionKeys.iterator();
				while (it.hasNext()) {
					System.out.println("it.hasNext()");
					SelectionKey selectionKey = it.next();
					it.remove();
					if (selectionKey.isReadable()) {
						FileOutputStream fis = new FileOutputStream(attachment);
						FileChannel fc = fis.getChannel();
						ByteBuffer fileBuffer = ByteBuffer.allocate(1024);
						SocketChannel channel = (SocketChannel) selectionKey.channel();
						int size = 0;
						int totalReadSize = 0;
						while (true) {
							size = channel.read(fileBuffer);
							totalReadSize += size;
							if (size != 0) {
								System.out.println("total read " + totalReadSize);
							}
							fileBuffer.flip();
							fc.write(fileBuffer);
							fileBuffer.clear();
							if (totalReadSize == fileLength) {
								break;
							}
						}
						fc.close();
						fis.close();
						System.out.println("Receive file finish.");
						return;
					}
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (ps != null) {
				ps.close();
			}
		}
	}

	private void handleExit() throws IOException {
		threadAlive = false;
		socketChannel.close();
	}

	private boolean userLoginPermitted(String username, String password) {
		return getDAO().usernameAndPasswordExists(username, password);
	}

	public DAO getDAO() {
		return dao;
	}
}
