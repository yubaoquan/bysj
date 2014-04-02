package client.UI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import beans.UserLoginBean;
import client.net.up.LoginTool;
import client.thread.LoginThread;

public class LoginUI {
	public static enum LoginCommandCode {CONFIRM,REST}
	public JFrame frame = new JFrame("邮件代理系统");
	private JPanel centerPanel = new JPanel();
	private JPanel southPanel = new JPanel();
	private JPanel serverNamePanel = new JPanel();
	private JPanel userNamePanel = new JPanel();
	private JPanel passwordPanel = new JPanel();

	private BorderLayout frameLayout = new BorderLayout();
	private GridLayout centerLayout = new GridLayout(3, 1);
	private FlowLayout dataLayout = new FlowLayout();
	private GridLayout southLayout = new GridLayout(1, 2);

	private JLabel serverNameLabel = new JLabel("server name:");
	private JLabel userNameLabel = new JLabel("user name:");
	private JLabel passwordLabel = new JLabel("password:");

	public JComboBox<String> serverNameSelector = new JComboBox<String>();
	public JTextField userNameTextField = new JTextField(20);
	public JPasswordField passwordTextField = new JPasswordField(20);

	private JButton confirmButton = new JButton("login");
	private JButton resetButton = new JButton("reset");

	public LoginUIMonitor monitor = new LoginUIMonitor();

	private UserLoginBean loginInformation = new UserLoginBean();
	
	public void setLoginInformation(UserLoginBean loginInformation) {
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
	//	waitingWindow.setVisible(false);
		serverNameSelector.addItem("");
		serverNameSelector.addItem("163");
		serverNameSelector.addItem("QQ");
		serverNameSelector.addItem("box");
		confirmButton.setActionCommand(LoginCommandCode.CONFIRM.toString());
		resetButton.setActionCommand(LoginCommandCode.REST.toString());
	}

	private void addComponents() {
		serverNamePanel.add(serverNameLabel);
		serverNamePanel.add(serverNameSelector);
		userNamePanel.add(userNameLabel);
		userNamePanel.add(userNameTextField);
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordTextField);
		
		confirmButton.addActionListener(monitor);
		resetButton.addActionListener(monitor);
		
		centerPanel.add(serverNamePanel);
		centerPanel.add(userNamePanel);
		centerPanel.add(passwordPanel);
		
		southPanel.add(confirmButton);
		southPanel.add(resetButton);
		
		frame.add(centerPanel, BorderLayout.CENTER);
		frame.add(southPanel, BorderLayout.SOUTH);
	}

	

	public class LoginUIMonitor implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			LoginCommandCode commandCode = LoginCommandCode.valueOf(e.getActionCommand());
			switch (commandCode) {
				case CONFIRM:
					onConfirmButtonClick();
					break;
				case REST:
					onResetButtonClick();
					break;
				default:
					System.exit(-1);
			}
		}

		private void onResetButtonClick() {
			cleanTextField();
		}

		public void onConfirmButtonClick() {
			fillLoginInformation();
			if (!informationValid()) {
				return;
			}
			boolean loginSucceed = LoginTool.loginToServer(loginInformation);
			if (loginSucceed) {
				LoginTool.selectSendOrReceive(LoginUI.this);
			} else {
				JOptionPane.showMessageDialog(frame, (String)"登录失败.请确认用户名和密码填写正确并且网络连接正常.", "错误", JOptionPane.WARNING_MESSAGE);
			}
		}
		
		private boolean informationValid() {
			if (serverNameSelector.getSelectedIndex() == 0) {
				JOptionPane.showMessageDialog(frame, (String)"请选择邮件服务器.", "错误", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			if (!LoginTool.loginInformationValid(loginInformation)) {
				JOptionPane.showMessageDialog(frame, (String)"用户名和密码不能为空,请检查后重新输入.", "错误", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			return true;
		}
	}

	private void fillLoginInformation() {
		if (((String)serverNameSelector.getSelectedItem()).equalsIgnoreCase("box")) {
			loginInformation.setLocalServerEnabled(true);
		} else {
			loginInformation.setSmtpServerName(getSmtpServerNameFromSelector());
			loginInformation.setPop3ServerName(getPop3ServerNameFromSelector());
		}
		loginInformation.setUserName(getUserNameFromTextField());
		loginInformation.setPassword(getPasswordFromTextField());
	}
	
	public void cleanTextField() {
		serverNameSelector.setSelectedIndex(0);
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

	public UserLoginBean getLoginInformation() {
		return loginInformation;
	}
}
