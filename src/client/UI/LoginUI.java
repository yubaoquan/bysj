package client.UI;

import static java.lang.System.out;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import util.Util;
import beans.Constant;
import beans.UserBean;
import client.net.up.Transmitter;

public class LoginUI {

	public JFrame frame = new JFrame("邮件代理系统");
	private JPanel centerPanel = new JPanel();
	private JPanel southPanel = new JPanel();
	private JPanel serverNamePanel = new JPanel();
	private JPanel userNamePanel = new JPanel();
	private JPanel passwordPanel = new JPanel();

	private BorderLayout frameLayout = new BorderLayout();
	private GridLayout centerLayout = new GridLayout(3, 1);
	private FlowLayout dataLayout = new FlowLayout();
	private GridLayout southLayout = new GridLayout(1, 3);

	private JLabel serverNameLabel = new JLabel("server name:");
	private JLabel userNameLabel = new JLabel("user name:");
	private JLabel passwordLabel = new JLabel("password:");

	public JComboBox<String> serverNameSelector = new JComboBox<String>();
	public JTextField userNameTextField = new JTextField(20);
	public JPasswordField passwordTextField = new JPasswordField(20);

	private JButton registerButton = new JButton("register");
	private JButton confirmButton = new JButton("login");
	private JButton resetButton = new JButton("reset");

	public LoginUIMonitor monitor = new LoginUIMonitor();

	private UserBean loginInformation = new UserBean();
	private Transmitter transmitter;

	public Transmitter getTransmitter() {
		return transmitter;
	}

	public void setTransmitter(Transmitter transmitter) {
		this.transmitter = transmitter;
	}

	public void setLoginInformation(UserBean loginInformation) {
		this.loginInformation = loginInformation;
	}

	public void launch() {
		initUI();
		frame.setVisible(true);
	}

	public void initUI() {
		configure();
		addComponents();
		frame.setVisible(true);
	}

	private void configure() {
		configureFrame();
		configurePanels();
		configureOthercomponents();
	}

	private void configureFrame() {
		frame.setLayout(frameLayout);
		frame.setLocation(400, 200);
		frame.setSize(500, 350);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void configurePanels() {
		centerPanel.setLayout(centerLayout);
		centerPanel.setBorder(BorderFactory.createEtchedBorder());
		southPanel.setLayout(southLayout);
		southLayout.setHgap(60);

		serverNamePanel.setLayout(dataLayout);
		userNamePanel.setLayout(dataLayout);
		passwordPanel.setLayout(dataLayout);
	}

	public void configureOthercomponents() {
		// waitingWindow.setVisible(false);
		serverNameSelector.addItem("");
		serverNameSelector.addItem("163");
		serverNameSelector.addItem("QQ");
		serverNameSelector.addItem("box");
		registerButton.setActionCommand(Constant.REGISTER);
		confirmButton.setActionCommand(Constant.CONFIRM);
		resetButton.setActionCommand(Constant.RESET);
	}

	private void addComponents() {
		serverNamePanel.add(serverNameLabel);
		serverNamePanel.add(serverNameSelector);
		userNamePanel.add(userNameLabel);
		userNamePanel.add(userNameTextField);
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordTextField);

		registerButton.addActionListener(monitor);
		confirmButton.addActionListener(monitor);
		resetButton.addActionListener(monitor);

		centerPanel.add(serverNamePanel);
		centerPanel.add(userNamePanel);
		centerPanel.add(passwordPanel);

		southPanel.add(registerButton);
		southPanel.add(confirmButton);
		southPanel.add(resetButton);

		frame.add(centerPanel, BorderLayout.CENTER);
		frame.add(southPanel, BorderLayout.SOUTH);
	}

	private class LoginUIMonitor implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			switch (command) {
				case Constant.REGISTER:
					onRegisterButtonClick();
					break;
				case Constant.CONFIRM:
					onConfirmButtonClick();
					break;
				case Constant.RESET:
					onResetButtonClick();
					break;
				default:
					System.exit(-1);
			}
		}

		private void onRegisterButtonClick() {
			out.println("注册新用户！");
			new RegisterUI(LoginUI.this);
		}

		private void onResetButtonClick() {
			cleanTextField();
		}

		public void onConfirmButtonClick() {
			fillLoginInformation();
			if (!informationValid()) {
				return;
			}
			transmitter = new Transmitter(loginInformation);
			boolean loginSucceed = transmitter.loginToServer();
			if (loginSucceed) {
				Util.selectSendOrReceive(LoginUI.this);
			} else {
				JOptionPane.showMessageDialog(frame, (String) "登录失败.请确认用户名和密码填写正确并且网络连接正常.", "错误", JOptionPane.WARNING_MESSAGE);
			}
		}

		private boolean informationValid() {
			if (serverNameSelector.getSelectedIndex() == Constant.NULL_INDEX) {
				JOptionPane.showMessageDialog(frame, (String) "请选择邮件服务器.", "错误", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			if (!Util.loginInformationValid(loginInformation)) {
				JOptionPane.showMessageDialog(frame, (String) "用户名和密码不能为空,请检查后重新输入.", "错误", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			return true;
		}
	}

	private void fillLoginInformation() {
		if (serverNameSelector.getSelectedIndex() == Constant.BOX_INDEX) {
			loginInformation.setLocalServerEnabled(true);
		} else {
			loginInformation.setSmtpServerName(getSmtpServerNameFromSelector());
			loginInformation.setPop3ServerName(getPop3ServerNameFromSelector());
		}
		loginInformation.setUserName(getUserNameFromTextField());
		loginInformation.setPassword(getPasswordFromTextField());
	}

	public void cleanTextField() {
		serverNameSelector.setSelectedIndex(Constant.NULL_INDEX);
		userNameTextField.setText("");
		passwordTextField.setText("");
	}

	public String getSmtpServerNameFromSelector() {
		String selection = (String) serverNameSelector.getSelectedItem();
		if (selection.equals("")) {
			return "";
		} else {
			return "smtp." + selection + ".com";
		}
	}

	public String getPop3ServerNameFromSelector() {
		String selection = (String) serverNameSelector.getSelectedItem();
		if (selection.equals("")) {
			return "";
		} else {
			return "pop." + selection + ".com";
		}
	}

	public String getUserNameFromTextField() {
		return userNameTextField.getText();
	}

	public String getPasswordFromTextField() {
		return new String(passwordTextField.getPassword());
	}

	public void dispose() {
		frame.dispose();
	}

	public UserBean getLoginInformation() {
		return loginInformation;
	}
	
	public static void main(String[] args) {
		new LoginUI().initUI();
	}
}
