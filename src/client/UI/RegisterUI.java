package client.UI;

import static java.lang.System.out;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import beans.Constant;

public class RegisterUI extends JFrame {

	private JLabel usernameLabel = new JLabel("username: ", Label.LEFT);
	private JLabel usernameWarningLabel = new JLabel("", Label.LEFT);
	
	private JLabel passwordLabel = new JLabel("password: ", Label.LEFT);
	private JLabel passwordWarningLabel = new JLabel("", Label.LEFT);
	
	private JLabel passwordAgainLabel = new JLabel("password again: ", Label.LEFT);
	private JLabel passwordAgainWarningLabel = new JLabel("", Label.LEFT);
	
	private JTextField usernameTextField = new JTextField(20);
	private JTextField passwordTextField = new JTextField(20);
	private JTextField passwordAgainTextField = new JTextField(20);

	private JButton confirmButton = new JButton("confirm");
	private JButton resetButton = new JButton("reset");

	private JPanel centerPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();

	private Panel usernamePanel = new Panel();
	private Panel passwordPanel = new Panel();
	private Panel passwordAgainPanel = new Panel();

	public RegisterUIMonitor monitor = new RegisterUIMonitor();
	
	public RegisterUI() {

		configure();
		addComponents();
		setVisible(true);
	}

	private void configure() {
		configureFrame();
		configurePanels();
		configureButtons();
	}

	private void configureFrame() {
		setLayout(new BorderLayout());
		setLocation(400, 200);
		setSize(500, 350);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	private void configurePanels() {
		centerPanel.setLayout(new GridLayout(3, 1));
		centerPanel.setBorder(BorderFactory.createEtchedBorder());
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.setBorder(BorderFactory.createEtchedBorder());

		usernamePanel.setLayout(new FlowLayout());
		passwordPanel.setLayout(new FlowLayout());
		passwordAgainPanel.setLayout(new FlowLayout());
	}

	private void configureButtons() {
		confirmButton.addActionListener(monitor);
		confirmButton.setActionCommand(Constant.CONFIRM);
		resetButton.addActionListener(monitor);
		resetButton.setActionCommand(Constant.RESET);
	}
	
	private void addComponents() {
		//usernameLabel.setForeground(Color.RED);
		usernamePanel.add(usernameLabel);
		usernamePanel.add(usernameTextField);
		usernamePanel.add(usernameWarningLabel);
		
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordTextField);
		passwordPanel.add(passwordWarningLabel);
		
		passwordAgainPanel.add(passwordAgainLabel);
		passwordAgainPanel.add(passwordAgainTextField);
		passwordAgainPanel.add(passwordAgainWarningLabel);
		
		centerPanel.add(usernamePanel);
		centerPanel.add(passwordPanel);
		centerPanel.add(passwordAgainPanel);

		buttonPanel.add(confirmButton, BorderLayout.WEST);
		buttonPanel.add(resetButton, BorderLayout.EAST);

		add(centerPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private class RegisterUIMonitor implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			switch (command) {
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

		private void onResetButtonClick() {
			cleanTextField();
		}

		public void cleanTextField() {
			usernameTextField.setText("");
			passwordTextField.setText("");
			passwordAgainTextField.setText("");
		}
		
		public void onConfirmButtonClick() {
		/*	if (!informationValid()) {
				return;
			}
			transmitter = new Transmitter(loginInformation);
			boolean loginSucceed = transmitter.loginToServer();
			if (loginSucceed) {
				Util.selectSendOrReceive(LoginUI.this);
			} else {
				JOptionPane.showMessageDialog(frame, (String) "登录失败.请确认用户名和密码填写正确并且网络连接正常.", "错误", JOptionPane.WARNING_MESSAGE);
			}*/
		}

		private boolean informationValid() {
			/*if (!Util.loginInformationValid(loginInformation)) {
				JOptionPane.showMessageDialog(frame, (String) "用户名和密码不能为空,请检查后重新输入.", "错误", JOptionPane.WARNING_MESSAGE);
				return false;
			}*/
			return true;
		}
	}

	public static void main(String[] args) {
		new RegisterUI();
	}
}
