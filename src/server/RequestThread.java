package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RequestThread implements Runnable{
	private InputStream is = null;
	private OutputStream os = null;
	
	private DataInputStream dis = null;
    private DataOutputStream dos = null;
    
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
	public RequestThread(InputStream is, OutputStream os, DAO dao) {
		this.is = is;
		this.dis = new DataInputStream(is);
		this.os = os;
		this.dos = new DataOutputStream(os);
		this.dao = dao;
	}

	@Override
	public void run() {
		 int operationCode;
		try {
			operationCode = dis.readInt();
			System.out.println("operationCode: " + operationCode);
            operateByOperationCode(operationCode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void operateByOperationCode(int opationCode) throws Exception {
		switch(opationCode) {
			case USER_AUTHENTICATION:
					userLogin();
				break;
			case LIST_MAILS:
				if(userConnected) {
					
				}
				break;
			default:
				break;
		}
	}

	private void userLogin() {
		try {
			String username = dis.readUTF();
			String password = dis.readUTF();
			if(userLoginOK(username, password)) {
				dos.writeBoolean(true);
				userConnected = true;
			} else {
				dos.writeBoolean(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private boolean userLoginOK(String username, String password) {
		return dao.usernameAndPasswordValid(username, password);
	}
}
