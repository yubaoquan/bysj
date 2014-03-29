package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import util.Util;

public class RequestThread implements Runnable{
    private Selector selector = null;
	private SocketChannel socketChannel = null;
    private ByteBuffer buffer = null;
    private DAO dao = null;
    
    private boolean userConnected = false;
    private static final int USER_AUTHENTICATION = 0;
    private static final int LIST_MAILS = 1;
    private static final int MAIL_DETAIL = 2;
    private static final int DOWNLOAD_ATTACHMENT = 3;
    private static final int SEND_MAIL = 4;
    //private static final int 
   // private static final int 
   // private static final int 
   // private static final int 
   // private static final int 
   // private static final int 
	public RequestThread(SocketChannel channel) {
		socketChannel = channel;
		buffer = ByteBuffer.allocateDirect(1024);
		try {
			selector = Selector.open();
			
			//key.interestOps(SelectionKey.OP_READ);
			socketChannel.register(selector, SelectionKey.OP_READ);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public RequestThread(SelectionKey key) {
		try {
			selector = Selector.open();
			
			//key.interestOps(SelectionKey.OP_READ);
			socketChannel.register(selector, SelectionKey.OP_READ);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		try {
			while (selector.select() > 0) {
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Util.println("size: " + selectionKeys.size());
				Iterator<SelectionKey> it = selectionKeys.iterator();
				while (it.hasNext()) {
					SelectionKey selectionKey = it.next();
					it.remove();
					if (selectionKey.isReadable()) {
						SocketChannel channel = (SocketChannel) selectionKey.channel();
						ByteBuffer buffer = ByteBuffer.allocate(50);
						System.out.println("read...");

						channel.read(buffer);

						buffer.flip();
						byte[] array = new byte[1024];
						buffer.get(array, 0, buffer.remaining());
						System.out.println("read:" + new String(array));
						//channel.socket().close();
						return;
					}
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void operateByRequestString(String[] requestString) throws Exception {
		int operationCode = Integer.parseInt(requestString[0]);
		System.out.println("operationCode: " + operationCode);
		switch(operationCode) {
			case USER_AUTHENTICATION:
					userLogin(requestString[1], requestString[2]);
				break;
			case LIST_MAILS:
				if(userConnected) {
					
				}
				break;
			default:
				break;
		}
	}

	private void userLogin(String username, String password) {
		try {
			buffer.clear();
			if(userLoginOK(username, password)) {
				buffer.put("true".getBytes());
				userConnected = true;
			} else {
				buffer.put("false".getBytes());
			}
			socketChannel.write(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private boolean userLoginOK(String username, String password) {
		return dao.usernameAndPasswordValid(username, password);
	}
}
