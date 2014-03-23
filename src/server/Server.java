package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import beans.MsgBean;

public class Server {

	private ServerSocket ss = null;
	private boolean started = false;
	private DAO dao = new DAO();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void start() {
		  try {
	            ss = new ServerSocket(8888);
	            started = true;
	            System.out.println("Server is running!");
	        } catch (BindException e) {
	            System.out.println("端口被占用!\n请关闭相关程序并重新运行");
	            System.exit(0);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        try {
	            while (started) {
	                Socket requestSocket = ss.accept();
	                System.out.println("A client connected!");
	                InputStream is = requestSocket.getInputStream();
	                OutputStream os = requestSocket.getOutputStream();
	                RequestThread requestThread = new RequestThread(is, os, dao);
	                new Thread(requestThread).start();
	                
	               
	            }
	        } catch (EOFException e) {
	            System.out.println("start 连接断开!");
	        } catch (IOException ioe) {
	            ioe.printStackTrace();
	        } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private MsgBean receiveMsg() {
		return null;
	}
	
	
}
