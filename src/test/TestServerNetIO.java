package test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import server.Server;

public class TestServerNetIO {

	private Server server = null;
	
	@Before
	public void initServer() {
		server = new Server();
	}
	
	@Test
	public void testNetIO() {
		Thread serverThread = new Thread() {
			public void run() {
				server.start();
				System.out.println("running");
			}
		};
		
		serverThread.start();
		clientRun();
	}
	
	public void clientRun() {
		try {
			System.out.println("this is client");
			Socket socket = new Socket("127.0.0.1", 8888);
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			dos.writeInt(98);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
