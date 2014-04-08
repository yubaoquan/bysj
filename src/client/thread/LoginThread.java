package client.thread;

import beans.UserBean;
import client.UI.LoginUI;

public class LoginThread extends Thread{

	private LoginUI loginUI;
	public LoginThread(LoginUI loginUI) {
		this.loginUI = loginUI;
	}
	@Override
	public void run() {
		loginUI.initUI();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoginUI loginUI = new LoginUI();
		UserBean li = new UserBean();
		li.setUserName("18080922587@163.com");
		li.setPassword("wocao123");
		li.setSmtpServerName("smtp.163.com");
		loginUI.userNameTextField.setText("18080922587@163.com");
		loginUI.passwordTextField.setText("wocao123");
		loginUI.configureOthercomponents();
		loginUI.serverNameSelector.setSelectedIndex(1);
		//loginUI.monitor.onConfirmButtonClick();
		
		
		LoginThread thread1 = new LoginThread(loginUI);
		thread1.start();
	}
}
