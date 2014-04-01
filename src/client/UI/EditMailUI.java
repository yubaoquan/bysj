package client.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

import beans.UserLoginBean;
import beans.MailBean;
import client.net.up.LoginTool;
import client.net.up.Transmitter;




public class EditMailUI {
	public static enum EditMailUICommandCode {
		SEND, ADD_EXTRA_ITEM, REMOVE_EXTRA_ITEM
	}

	private MailBean mail = new MailBean();
	private EditMailUIMonitor editMailUIMonitor = new EditMailUIMonitor();
	private UserLoginBean loginInformation;
	private JFrame frame = new JFrame("编辑邮件内容");

	private JPanel mainPanel = new JPanel();
	private JPanel senderNamePanel = new JPanel();
	private JPanel receiverNamePanel = new JPanel();
	private JPanel northPanel = new JPanel();
	private JPanel southPanel = new JPanel();
	private JPanel centerPanel = new JPanel();

	private JPanel centerNorthPanel = new JPanel();

	private JPanel centerSouthPanel = new JPanel();
	private JPanel centerSouthWestPanel = new JPanel();
	private JPanel centerSouthCenterPanel = new JPanel();
	private JPanel centerSouthEastPanel = new JPanel();

	private JLabel senderLabel = new JLabel("发件人:             ");
	private JTextField senderNameTextField = new JTextField();
	private JLabel receiverLabel = new JLabel("收件人:             ");
	private JTextField receiverAddressTextField = new JTextField();

	private JLabel subjectLabel = new JLabel("标题");
	private JTextField subjectTextField = new JTextField();
	private JLabel mainTextLabel = new JLabel("正文");
	private JTextArea mainTextArea = new JTextArea();
	private JButton addExtraItemButton = new JButton("添加附件");
	private JButton removeExtraItemButton = new JButton("移除附件");
	private JLabel extraItemNameLabel = new JLabel("附件:");
	private JButton sendButton = new JButton("发送");

	public EditMailUI(UserLoginBean li) {
		loginInformation = li;
	}

	public void launch() {
		intiUI();
		frame.setVisible(true);
	}

	private void intiUI() {
		setAttributes();
		addComponents();
		
	}

	private void setAttributes() {
		configureFrame();
		setLayouts();
		configureOtherComponents();
	}

	private void configureFrame() {
		frame.setLocation(400, 100);
		frame.setSize(600, 450);
		frame.setLayout(new BorderLayout());
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	private void setLayouts() {
		mainPanel.setLayout(new BorderLayout());
		senderNamePanel.setLayout(new BorderLayout());
		receiverNamePanel.setLayout(new BorderLayout());
		northPanel.setLayout(new GridLayout(2, 1));
		southPanel.setLayout(new FlowLayout());

		centerPanel.setLayout(new BorderLayout());
		centerNorthPanel.setLayout(new BorderLayout());
		centerSouthPanel.setLayout(new BorderLayout());
		centerSouthWestPanel.setLayout(new FlowLayout());
		centerSouthCenterPanel.setLayout(new FlowLayout());
		centerSouthEastPanel.setLayout(new FlowLayout());
		centerPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.BLUE));
	}
	
	private void configureOtherComponents() {
		mainTextArea.setBorder(new MatteBorder(1, 1, 1, 1, Color.GREEN));
		senderNameTextField.setText(loginInformation.getUserName());
		senderNameTextField.setEditable(false);

		addExtraItemButton.addActionListener(editMailUIMonitor);
		addExtraItemButton.setActionCommand(EditMailUICommandCode.ADD_EXTRA_ITEM.toString());

		removeExtraItemButton.addActionListener(editMailUIMonitor);
		removeExtraItemButton.setActionCommand(EditMailUICommandCode.REMOVE_EXTRA_ITEM.toString());

		sendButton.addActionListener(editMailUIMonitor);
		sendButton.setActionCommand(EditMailUICommandCode.SEND.toString());
	}

	private void addComponents() {
		senderNamePanel.add(senderLabel, BorderLayout.WEST);
		senderNamePanel.add(senderNameTextField, BorderLayout.CENTER);
		receiverNamePanel.add(receiverLabel, BorderLayout.WEST);
		receiverNamePanel.add(receiverAddressTextField, BorderLayout.CENTER);

		centerSouthWestPanel.add(addExtraItemButton);
		centerSouthCenterPanel.add(extraItemNameLabel);
		centerSouthEastPanel.add(removeExtraItemButton);

		centerSouthPanel.add(centerSouthWestPanel, BorderLayout.WEST);
		centerSouthPanel.add(centerSouthCenterPanel, BorderLayout.CENTER);
		centerSouthPanel.add(centerSouthEastPanel, BorderLayout.EAST);

		centerNorthPanel.add(subjectLabel, BorderLayout.WEST);
		centerNorthPanel.add(subjectTextField, BorderLayout.CENTER);
		centerNorthPanel.add(mainTextLabel, BorderLayout.SOUTH);

		centerPanel.add(centerNorthPanel, BorderLayout.NORTH);
		centerPanel.add(mainTextArea, BorderLayout.CENTER);
		centerPanel.add(centerSouthPanel, BorderLayout.SOUTH);

		northPanel.add(senderNamePanel);
		northPanel.add(receiverNamePanel);
		southPanel.add(sendButton);

		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);

		frame.add(mainPanel);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UserLoginBean loginBean = new UserLoginBean();
		loginBean.setUserName("user");
		new EditMailUI(loginBean).launch();

	}

	private class EditMailUIMonitor implements ActionListener {
		private EditMailUICommandCode commandCode;

		@Override
		public void actionPerformed(ActionEvent e) {
			commandCode = EditMailUICommandCode.valueOf(e.getActionCommand());
			switch (commandCode) {
				case ADD_EXTRA_ITEM:
					onAddExtraItemButtonClick();
					break;
				case REMOVE_EXTRA_ITEM:
					onRemoveExtraItemButtonClick();
					break;
				case SEND:
					onSendButtonClick();
					break;
				default:
					System.out.println("error");
					System.exit(-1);
			}
		}

		private void onAddExtraItemButtonClick() {
			JFileChooser fc = new JFileChooser();// fc.set
			fc.setDialogTitle("选择附件");
			fc.showDialog(frame, "选择");
			File extraItemFile = fc.getSelectedFile();
			if (extraItemFile != null) {
				System.out.println(extraItemFile.getName());
				changeExtraItemNameLabel(extraItemFile);
				mail.addExtraItem(extraItemFile);
				if (mail.extraItemsFull()) {
					addExtraItemButton.setEnabled(false);
				}
			}
		}

		private void changeExtraItemNameLabel(File extraItemFile) {
			StringBuffer text = new StringBuffer(extraItemNameLabel.getText());
			String extraItemFileName = extraItemFile.getName();
			int shortNameLength = extraItemFileName.length() > 10 ? 10 : extraItemFileName.length();
			String shortFileName = extraItemFile.getName().substring(0, shortNameLength) + "..,";

			if (mail.getAttachmentsAmount() == mail.ATTACHMENTS_CAPACITY - 1) {
				shortFileName = shortFileName.substring(0, shortFileName.length() - 1);
			}
			text.append(shortFileName);
			extraItemNameLabel.setText(new String(text));
		}

		private void onRemoveExtraItemButtonClick() {
			mail.removeAllExtraItems();
			extraItemNameLabel.setText("附件:");
			addExtraItemButton.setEnabled(true);
		}

		private void onSendButtonClick() {

			if (fillMail()) {

				Transmitter.getInstance(loginInformation).sendMail(mail);
				if (Transmitter.getInstance(loginInformation).sendSucceed()) {
					int option = JOptionPane.showConfirmDialog(frame, (String) "发送成功,是否再发一个.", "已发送", JOptionPane.YES_NO_OPTION);
					switch (option) {
						case 0:
							resend();
							break;
						case 1:
							terminate();
							break;
						default:
							System.out.println("error");
							break;
					}
				}
			}
		}

		private void resend() {
			cleanForm();
			cleanMailBean();
		}

		private void cleanForm() {
			EditMailUI.this.receiverAddressTextField.setText("");
			EditMailUI.this.subjectTextField.setText("");
			EditMailUI.this.mainTextArea.setText("");
			EditMailUI.this.extraItemNameLabel.setText("附件：");
		}
		
		private void cleanMailBean() {
			mail = new MailBean();
		}
		
		private void terminate() {
			Transmitter.getInstance(loginInformation).closeConnection();
			System.exit(0);
		}

		private boolean fillMail() {
			String receiverAddress = EditMailUI.this.receiverAddressTextField.getText();
			String subject = EditMailUI.this.subjectTextField.getText();
			String text = EditMailUI.this.mainTextArea.getText();
			InternetAddress[] receiversAddressArray = new InternetAddress[1];
			try {
				receiversAddressArray[0] = new InternetAddress(receiverAddress);
			} catch (AddressException e) {
				System.out.println("wrong address");
				JOptionPane.showMessageDialog(frame, (String) "收信人地址填写不正确,请检查后重新输入.", "错误", JOptionPane.WARNING_MESSAGE);
				mail = new MailBean();
				//extraItemNameLabel.setText("附件:");
				return false;
			}
			mail.setSubject(subject);
			mail.setText(text);
			if (mail.getAttachmentsAmount() > 0) {
				mail.addAttachmentsToMultipart();
			}
			mail.setReceiverAddresses(receiversAddressArray);
			return true;
		}
	}
}
