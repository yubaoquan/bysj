package client.net.up;

import client.UI.*;
import beans.UserLoginBean;

public class LoginTool {

	public static boolean loginInformationValid(UserLoginBean loginInformation) {
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

	public static void selectSendOrReceive(LoginUI parentUI) {
		parentUI.dispose();
		UserLoginBean li = parentUI.getLoginInformation();
		new MainTargetSelectingUI(li).initUI();
	}

	public static boolean loginToServer(UserLoginBean li) {
		return Transmitter.getInstance(li).loginToServer();
	}
	
	public static boolean loginServerSucceed(UserLoginBean li) {
		return Transmitter.getInstance(li).loginServerSucceed();
	}
	
}
