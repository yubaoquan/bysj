package client.thread;

import beans.UserBean;
import client.UI.LoginUI;

public class LoginThread extends Thread {

	private LoginUI loginUI;

	public LoginThread(LoginUI loginUI) {
		this.loginUI = loginUI;
	}

	@Override
	public void run() {
		loginUI.initUI();
	}

	public static void main(String[] args) {
		LoginUI loginUI = new LoginUI();
		UserBean li = new UserBean();
		li.setUserName("admin");
		li.setPassword("admin");
		li.setSmtpServerName("smtp.163.com");
		loginUI.userNameTextField.setText("admin");
		loginUI.passwordTextField.setText("admin");
		loginUI.configureOthercomponents();
		loginUI.serverNameSelector.setSelectedIndex(3);

		LoginThread thread1 = new LoginThread(loginUI);
		thread1.start();
	}
}
