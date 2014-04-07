package server;

import beans.Constant;
import server.communicate.RequestThread;
import util.Util;

import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * The mail server running  on the local machine.
 * @author yubaoquan
 *
 */
public class Server {

	private boolean started = false;
	private static Selector selector = null;
	private static ServerSocketChannel serverSocketChannel = null;
	private final static Logger logger = Logger.getLogger(Server.class.getName());

	public static void main(String[] args) {
		new Server().start();

	}

	public void start() {
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.bind(new InetSocketAddress(Constant.SERVER_PORT));
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().setReuseAddress(true);
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("bind ok");
			listen();

		} catch (BindException e) {
			System.out.println("端口被占用!\n请关闭相关程序并重新运行");
			System.exit(0);
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

}
