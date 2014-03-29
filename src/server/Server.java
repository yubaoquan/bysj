package server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import util.Util;
import beans.MsgBean;

public class Server {

	private boolean started = false;
	private DAO dao = new DAO();
	private static Selector selector = null;
	private static ServerSocketChannel serverSocketChannel = null;
	private final static Logger logger = Logger.getLogger(Server.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Server().start();

	}

	public void start() {
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.bind(new InetSocketAddress(8888));
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().setReuseAddress(true);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("bind ok");
			listen();

		} catch (BindException e) {
			System.out.println("端口被占用!\n请关闭相关程序并重新运行");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * 监听的方法
	 * 
	 * 
	 * 
	 * @throws Exception
	 */
	private void listen() throws Exception {
		Util.println("listening");
		while (selector.select() > 0) {
			
			Set<SelectionKey> selectionKeys = selector.selectedKeys();
			// Util.println("size: " + selectionKeys.size());
			Iterator<SelectionKey> it = selectionKeys.iterator();
			while (it.hasNext()) {
				SelectionKey selectionKey = it.next();
				it.remove();
				// handleKey(selectionKey);
				if (selectionKey.isAcceptable()) {
					System.out.println("client connect");
					serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
					SocketChannel clientSocketChannel = serverSocketChannel.accept();
					clientSocketChannel.configureBlocking(false);
					System.out.println("完成连接!");
					if (clientSocketChannel != null) {
						new Thread(new RequestThread(clientSocketChannel)).start();
					}
					
				}
			}
			
		}
	}

	private static void handleKey(SelectionKey selectionKey) throws Exception {
		SocketChannel clientSocketChannel = null;
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
		if (selectionKey.isAcceptable()) {
			System.out.println("client connect");
			serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
			clientSocketChannel = serverSocketChannel.accept();
			clientSocketChannel.configureBlocking(false);
			// clientSocketChannel = (SocketChannel) selectionKey.channel();
			// 判断此通道上是否正在进行连接操作。
			// 完成套接字通道的连接过程。
			if (clientSocketChannel.isConnectionPending()) {
				clientSocketChannel.finishConnect();
				System.out.println("完成连接!");
				clientSocketChannel.register(selector, SelectionKey.OP_READ);
			}
		}
		// ---------------------
		else if (selectionKey.isReadable()) {
			Util.println("readable");
			clientSocketChannel = (SocketChannel) selectionKey.channel();
			StringBuffer stringBuffer = new StringBuffer();
			int size = 0;
			byteBuffer = ByteBuffer.allocateDirect(1024);
			while ((size = clientSocketChannel.read(byteBuffer)) > 0) {
				System.out.println("size: " + size);
				byteBuffer.flip();
				byte[] array = new byte[1024];
				byteBuffer.get(array, 0, byteBuffer.remaining());
				stringBuffer.append(new String(array).trim());
				Util.print(stringBuffer);

				byteBuffer.clear();
				byteBuffer = ByteBuffer.wrap("true".getBytes());
				// --------------------------------
				clientSocketChannel.socket().shutdownOutput();
				clientSocketChannel.close();
				System.exit(0);
				// --------------------------
			}
			clientSocketChannel.register(selector, SelectionKey.OP_WRITE);
		} else if (selectionKey.isWritable()) {
			Util.println("writable");
			byteBuffer.clear();
			byteBuffer = ByteBuffer.wrap("收到消息!".getBytes("UTF-8"));
			clientSocketChannel.write(byteBuffer);
			byteBuffer.clear();
			clientSocketChannel.register(selector, SelectionKey.OP_READ);

		} else {
			Util.println("wtf");
		}
	}

	private MsgBean receiveMsg() {
		return null;
	}

}
