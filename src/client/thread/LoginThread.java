package client.thread;

import beans.UserBean;
import client.UI.LoginUI;

public class LoginThread extends Thread {

	private LoginUI loginUI = new LoginUI();

	public LoginThread() {
		
	}
	
	public LoginThread(LoginUI loginUI) {
		this.loginUI = loginUI;
	}

	@Override
	public void run() {
		loginUI.initUI();
	}

	public static void main(String[] args) {
	//	LoginUI loginUI = methodForTest(false);
	//	LoginThread thread1 = new LoginThread(loginUI);
		
		LoginThread thread1 = new LoginThread();
		thread1.start();
	}

	//This method is for test.
	@SuppressWarnings("unused")
	private static LoginUI methodForTest(boolean localTest) {
		LoginUI loginUI = new LoginUI();
		UserBean li = new UserBean();
		if (localTest) {
			//Use for local test;
			li.setUserName("admin");
			li.setPassword("admin");
			li.setSmtpServerName("smtp.163.com");
			loginUI.userNameTextField.setText("admin");
			loginUI.passwordTextField.setText("admin");
			loginUI.configureOthercomponents();
			loginUI.serverNameSelector.setSelectedIndex(3);
		} else {
			//Use for internet test;
			li.setUserName("18080922587@163.com");
			li.setPassword("wocao123");
			li.setSmtpServerName("smtp.163.com");
			loginUI.userNameTextField.setText("18080922587@163.com");
			loginUI.passwordTextField.setText("wocao123");
			loginUI.configureOthercomponents();
			loginUI.serverNameSelector.setSelectedIndex(1);
		}
		return loginUI;
	}
}
