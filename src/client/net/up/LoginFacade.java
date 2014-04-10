package client.net.up;

import client.UI.*;
import beans.UserBean;

public class LoginFacade {

	public static boolean loginInformationValid(UserBean loginInformation) {
		if (loginInformation == null) {
			System.out.println("loginInformation == null");
		}
		if (loginInformation.getUserName().trim().equals("")) {
			return false;
		}
		if (loginInformation.getPassword().trim().equals("")) {
			return false;
		}
		return true;
	}

	public static void selectSendOrReceive(LoginUI parent) {
		parent.dispose();
		UserBean user = parent.getLoginInformation();
		Transmitter tra = parent.getTransmitter();
		new MainTargetSelectingUI(user, tra).initUI();
	}

	public static boolean loginToServer(UserBean li) {
		return Transmitter.getInstance(li).loginToServer();
	}
	
	public static boolean loginServerSucceed(UserBean li) {
		return Transmitter.getInstance(li).loginServerSucceed();
	}
	
}
