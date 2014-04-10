package client.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Timestamp;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

import beans.MailBean;
import beans.UserBean;
import client.net.up.Transmitter;

/**
 * Class for editing new mail.
 * After editing the new mail, user use the UI to call the Transmitter to send the mail.
 * 
 * @author yubaoquan
 *
 */
public class EditMailUI {
	public static enum EditMailUICommandCode {
		SEND, ADD_EXTRA_ITEM, REMOVE_EXTRA_ITEM
	}

	private MailBean mail = new MailBean();
	private EditMailUIMonitor editMailUIMonitor = new EditMailUIMonitor();
	private UserBean user;
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
	private JTextField addresseeTextField = new JTextField();

	private JLabel subjectLabel = new JLabel("标题");
	private JTextField subjectTextField = new JTextField();
	private JLabel mainTextLabel = new JLabel("正文");
	private JTextArea mainTextArea = new JTextArea();
	private JButton addExtraItemButton = new JButton("添加附件");
	private JButton removeExtraItemButton = new JButton("移除附件");
	private JLabel extraItemNameLabel = new JLabel("附件:");
	private JButton sendButton = new JButton("发送");
	private Transmitter transmitter;

	public Transmitter getTransmitter() {
		return transmitter;
	}

	public void setTransmitter(Transmitter transmitter) {
		this.transmitter = transmitter;
	}

	public EditMailUI(UserBean li, Transmitter transmitter) {
		user = li;
		this.transmitter = transmitter;
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
		senderNameTextField.setText(user.getUserName());
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
		receiverNamePanel.add(addresseeTextField, BorderLayout.CENTER);

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
		UserBean loginBean = new UserBean();
		loginBean.setUserName("user");
		new EditMailUI(loginBean, null).launch();

	}

	private class EditMailUIMonitor implements ActionListener {
		private EditMailUICommandCode commandCode;

		@Override
		public void actionPerformed(ActionEvent e) {
			commandCode = EditMailUICommandCode.valueOf(e.getActionCommand());
			switch (commandCode) {
				case ADD_EXTRA_ITEM:
					onAddAttachmentButtonClick();
					break;
				case REMOVE_EXTRA_ITEM:
					onRemoveAttachmentButtonClick();
					break;
				case SEND:
					onSendButtonClick();
					break;
				default:
					System.out.println("error");
					System.exit(-1);
			}
		}

		private void onAddAttachmentButtonClick() {
			File attachment = null;
			FileDialog fd = new FileDialog(frame, "选择附件");
			fd.setVisible(true);
			File[] files = fd.getFiles();
			if (files.length > 0) {
				attachment = files[0];
			}
			if (attachment != null) {
				System.out.println(attachment.getName());
				changeAttachmentLabel(attachment.getName());
				if (user.isLocalServerEnabled()) {
					mail.addAttachment(attachment, MailBean.FOR_LOCAL_SERVER);
				} else {
					mail.addAttachment(attachment, MailBean.FOR_INTERNET_SERVER);
				}
				if (mail.attachmentsAreFull()) {
					addExtraItemButton.setEnabled(false);
				}
			}
		}

		private void onRemoveAttachmentButtonClick() {
			mail.removeAllExtraItems();
			extraItemNameLabel.setText("附件:");
			addExtraItemButton.setEnabled(true);
		}

		private void onSendButtonClick() {
			if (fillMail()) {
				boolean sendSucceed = transmitter.sendMail(mail);
				if (sendSucceed) {
					int option = JOptionPane.showConfirmDialog(frame, (String) "发送成功,是否再发一个.", "已发送", JOptionPane.YES_NO_OPTION);
					switch (option) {
						case 0:
							prepareForNextSend();
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

	}

	private boolean fillMail() {
		if (EditMailUI.this.addresseeTextField.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(frame, (String) "收信人地址不能为空,请检查后重新输入.", "错误", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		try {
			fillRespectiveProperties();
		} catch (AddressException e) {
			System.out.println("wrong address");
			JOptionPane.showMessageDialog(frame, (String) "收信人地址填写不正确,请检查后重新输入.", "错误", JOptionPane.WARNING_MESSAGE);
			mail = new MailBean();
			return false;
		}
		fillCommonProperties();
		return true;
	}

	private void fillRespectiveProperties() throws AddressException {
		if (user.isLocalServerEnabled()) {
			fillMailForLocalServer();
		} else {
			fillMailForInternetServer();
		}
	}

	private void fillCommonProperties() {
		String subject = EditMailUI.this.subjectTextField.getText();
		if (subject.length() == 0) {
			subject = "[无标题]";
		}
		String text = EditMailUI.this.mainTextArea.getText();
		if (text.length() == 0) {
			text = "[无内容]";
		}
		Timestamp sentTime = new Timestamp(System.currentTimeMillis());
		mail.setSubject(subject);
		mail.setText(text);
		mail.setSendTime(sentTime);
	}

	private void fillMailForLocalServer() {
		// TODO Auto-generated method stub
		System.out.println("Fill mail for local server");
		mail.setSender(user.getUserName());
		mail.setAddressee(addresseeTextField.getText());
	}

	private void fillMailForInternetServer() throws AddressException {
		// TODO Auto-generated method stub
		System.out.println("Fill mail for Internet server");
		fillMailAddressees();
		if (mail.getAttachmentAmount() > 0) {
			mail.addAttachmentsToMultipart();
		}
	}

	private void fillMailAddressees() throws AddressException {
		String addressee = EditMailUI.this.addresseeTextField.getText();
		InternetAddress[] addresseeArray = new InternetAddress[1];
		addresseeArray[0] = new InternetAddress(addressee);
		mail.setInternetAddressees(addresseeArray);
	}

	private void changeAttachmentLabel(String attachmentName) {
		StringBuffer text = new StringBuffer(extraItemNameLabel.getText());
		int shortNameLength = attachmentName.length() > 10 ? 10 : attachmentName.length();
		String shortFileName = attachmentName.substring(0, shortNameLength) + ".., ";

		if (mail.getAttachmentAmount() == mail.ATTACHMENTS_CAPACITY - 1) {
			shortFileName = shortFileName.substring(0, shortFileName.length() - 1);
		}
		text.append(shortFileName);
		extraItemNameLabel.setText(new String(text));
	}

	private void prepareForNextSend() {
		cleanForm();
		cleanMailBean();
	}

	private void cleanForm() {
		EditMailUI.this.addresseeTextField.setText("");
		EditMailUI.this.subjectTextField.setText("");
		EditMailUI.this.mainTextArea.setText("");
		EditMailUI.this.extraItemNameLabel.setText("附件：");
	}

	private void cleanMailBean() {
		mail = new MailBean();
	}

	private void terminate() {
		transmitter.closeConnection();
		System.exit(0);
	}
}
