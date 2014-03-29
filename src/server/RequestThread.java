package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import beans.Constant;
import util.Util;

public class RequestThread implements Runnable {
	private Selector selectorForRead = null;
	private Selector selectorForWrite = null;
	private SocketChannel socketChannel = null;
	private ByteBuffer buffer = null;
	private DAO dao = null;

	private boolean userConnected = false;

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
			String[] requestParameters = receiveRequest();
			operateByRequestParams(requestParameters);
			Util.println("here");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String[] receiveRequest() throws IOException, Exception {
		Util.println("receive request");
		String[] requestParams = null;
		while (selectorForRead.select() > 0) {
			Set<SelectionKey> selectionKeys = selectorForRead.selectedKeys();
			Iterator<SelectionKey> it = selectionKeys.iterator();
			while (it.hasNext()) {
				SelectionKey selectionKey = it.next();
				it.remove();
				if (selectionKey.isReadable()) {
					SocketChannel channel = (SocketChannel) selectionKey.channel();
					ByteBuffer buffer = ByteBuffer.allocate(50);

					channel.read(buffer);

					buffer.flip();
					byte[] array = new byte[1024];
					buffer.get(array, 0, buffer.remaining());
					String requestString = new String(array).trim();
					System.out.println("read: " + requestString);
					requestParams = requestString.split(" ");
					return requestParams;
					
				}
			}
		}
		return requestParams;
	}

	private void sendResponse(String responseString) throws Exception {
		Util.println("send response: " + responseString);
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
	}
	
	private void operateByRequestParams(String[] requestParams) throws Exception {
		int operationCode = Integer.parseInt(requestParams[0]);
		Util.println("operation code: " + operationCode);
		switch (operationCode) {
			case Constant.USER_AUTHENTICATION:
				Util.println("check username and password");
				userLogin(requestParams[1], requestParams[2]);
				break;
			case Constant.LIST_MAILS:
				if (userConnected) {

				}
				break;
			default:
				break;
		}
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

	
	private boolean userLoginPermitted(String username, String password) {
		return getDAO().usernameAndPasswordExists(username, password);
	}

	public DAO getDAO() {
		return new DAO();
	}
}
