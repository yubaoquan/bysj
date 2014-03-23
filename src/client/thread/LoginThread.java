package client.thread;

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
		LoginThread thread1 = new LoginThread(loginUI);
		thread1.start();
	}
}
