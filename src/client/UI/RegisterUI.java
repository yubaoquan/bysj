package client.UI;

import static java.lang.System.out;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.mysql.jdbc.StringUtils;

import beans.Constant;
import client.net.up.Transmitter;

public class RegisterUI extends JFrame {

	// [
	private static final long serialVersionUID = 1L;

	private LoginUI parent;
	private JLabel usernameLabel = new JLabel("username: ", Label.LEFT);
	private JLabel usernameWarningLabel = new JLabel("", Label.LEFT);

	private JLabel passwordLabel = new JLabel("password: ", Label.LEFT);
	private JLabel passwordWarningLabel = new JLabel("", Label.LEFT);

	private JLabel passwordAgainLabel = new JLabel("password again: ", Label.LEFT);
	private JLabel passwordAgainWarningLabel = new JLabel("", Label.LEFT);

	private JTextField usernameTextField = new JTextField(20);
	private JPasswordField passwordTextField = new JPasswordField(20);
	private JPasswordField passwordAgainTextField = new JPasswordField(20);

	private JButton confirmButton = new JButton("confirm");
	private JButton resetButton = new JButton("reset");

	private JPanel centerPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();

	private Panel usernamePanel = new Panel();
	private Panel passwordPanel = new Panel();
	private Panel passwordAgainPanel = new Panel();
	// ]
	
	private RegisterUIMonitor monitor = new RegisterUIMonitor();
	private String username;
	private String password;
	private boolean usernameOK = false;
	private boolean passwordOK = false;
	private boolean passwordAgainOK = false;
	
	private static String standardPasswordRegex = "\\p{ASCII}+";
	
	private Transmitter transmitter;
	
	public RegisterUI(LoginUI parent) {
		this.parent = parent;
		configure();
		addComponents();
		transmitter = new Transmitter();
		parent.frame.setVisible(false);
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
		// usernameLabel.setForeground(Color.RED);
		usernameTextField.addFocusListener(new UsernameTextFieldMonitor());
		passwordTextField.addFocusListener(new PasswordTextFieldFocusMonitor());
		passwordAgainTextField.addFocusListener(new passwordAgainTextFieldFocusMonitor());

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

		public void onConfirmButtonClick() {
			out.println("onConfirmButtonClick");
			if (usernameOK && passwordOK && passwordAgainOK) {
				StringBuffer request = new StringBuffer();
				request.append(Constant.ADD_NEW_USER).append(" ");
				request.append(username.length() + " ");
				request.append(username);
				request.append(password);
				transmitter.sendRequest(request.toString());
				String response = transmitter.receiveResponse();
				int result = Integer.parseInt(response);
				if (result == Constant.SUCCEED) {
					out.println("Congratulations! Register new account SUCCEED!");
					int choose = JOptionPane.showConfirmDialog(null, (String) "Congratulations! Register new account SUCCEED!", "Succeed", JOptionPane.YES_OPTION);
					if (choose == 0) {
						RegisterUI.this.setVisible(false);
						parent.serverNameSelector.setSelectedIndex(Constant.BOX_INDEX);
						parent.userNameTextField.setText(username);
						parent.passwordTextField.setText(password);
						parent.frame.setVisible(true);
						RegisterUI.this.dispose();
					} else {
						System.exit(0);
					}
				}else {
					out.println("We are sorry to tell you that your registration has failed.Please try again.");
					JOptionPane.showMessageDialog(RegisterUI.this, (String) "We are sorry to tell you that your registration has failed.Please try again.", "Fail", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				out.println(usernameOK);
				out.println(passwordOK);
				out.println(passwordAgainOK);
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
	}

	private class UsernameTextFieldMonitor implements FocusListener {

		private JLabel warningLabel;

		UsernameTextFieldMonitor() {
			this.warningLabel = usernameWarningLabel;
		}

		@Override
		public void focusGained(FocusEvent e) {
			
		}

		@Override
		public void focusLost(FocusEvent e) {
			username = ((JTextField) e.getComponent()).getText();
			out.println("username: " + username);
			if (!StringUtils.isEmptyOrWhitespaceOnly(username)) {
				StringBuffer request = new StringBuffer();
				request.append(Constant.FIND_USER_NAME);
				request.append(" ");
				request.append(username);
				transmitter.sendRequest(request.toString());
				String response = transmitter.receiveResponse();
				if (Integer.valueOf(response) == Constant.FOUND) {
					out.println("user name exists.");
					warningLabel.setText("Sorry,User name already exists.");
					warningLabel.setForeground(Color.RED);
					usernameOK = false;
				} else {
					out.println("user name not exist.");
					warningLabel.setText("Congratulations! You can use this user name");
					warningLabel.setForeground(Color.GREEN);
					usernameOK = true;
				}
			}
		}
	}

	private class PasswordTextFieldFocusMonitor implements FocusListener {

		private JLabel warningLabel;
		
		public PasswordTextFieldFocusMonitor() {
			super();
			this.warningLabel = passwordWarningLabel;
		}

		@Override
		public void focusGained(FocusEvent e) {
			
		}

		@Override
		public void focusLost(FocusEvent e) {
			password = ((JTextField)e.getComponent()).getText();
			if (validatePassword(password)) {
				out.println("password ok.");
				warningLabel.setForeground(Color.GREEN);
				warningLabel.setText("Password OK!");
				passwordOK = true;
			} else {
				warningLabel.setForeground(Color.RED);
				warningLabel.setText("Password format not OK,please change your password");
				passwordOK = false;
			}

		}

		private boolean validatePassword(String password) {
			passwordAgainOK = false;
			if (password == null || password.length() < 6) {
				return false;
			} 
			if (Pattern.matches(standardPasswordRegex, password)) {
				out.println("password again ok");
				passwordAgainOK = true;
				return true;
			} else {
				return false;
			}
		}

	}

	private class passwordAgainTextFieldFocusMonitor implements FocusListener {

		private JLabel warningLabel;

		public passwordAgainTextFieldFocusMonitor() {
			super();
			this.warningLabel = passwordAgainWarningLabel;
		}

		@Override
		public void focusGained(FocusEvent e) {
			
		}

		@Override
		public void focusLost(FocusEvent e) {
			String password = passwordTextField.getText();
			String passwordAgain = passwordAgainTextField.getText();
			if (password.equals(passwordAgain)) {
				warningLabel.setForeground(Color.GREEN);
				warningLabel.setText("OK");
				passwordAgainOK = true;
			} else {
				warningLabel.setForeground(Color.RED);
				warningLabel.setText("Password and passwordAgain not the same.Please check.");
				passwordAgainOK = false;
			}

		}

	}

	public static void main(String[] args) {
		while (true) {
			int result = JOptionPane.showConfirmDialog(null, (String) "Congratulations! Register new account SUCCEED!", "Succeed", JOptionPane.YES_OPTION);
			out.println(result);
		}
		
	}
}
