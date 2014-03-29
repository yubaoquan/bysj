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

	private String senderName;
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

	private BorderLayout frameLayout = new BorderLayout();
	private BorderLayout mainPanelLayout = new BorderLayout();

	private BorderLayout senderPanelLayout = new BorderLayout();
	private BorderLayout receiverPanelLayout = new BorderLayout();

	private BorderLayout centerPanelLayout = new BorderLayout();
	private BorderLayout centerNorthPanelLayout = new BorderLayout();
	private FlowLayout southPanelLayout = new FlowLayout();
	private BorderLayout centerSouthPanelLayout = new BorderLayout();
	private FlowLayout centerSouthWestPanelLayout = new FlowLayout();
	private FlowLayout centerSouthCenterPanelLayout = new FlowLayout();
	private FlowLayout centerSouthEastPanelLayout = new FlowLayout();
	private GridLayout northPanelLayout = new GridLayout(2, 1);

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

	public EditMailUI(String senderName) {
		this.senderName = senderName;
	}

	public EditMailUI(UserLoginBean li) {
		this.loginInformation = li;
	}

	public void launch() {
		this.intiUI();
		this.frame.setVisible(true);
	}

	private void intiUI() {
		this.setAttributes();
		this.addComponents();
	}

	private void setAttributes() {
		configureFrame();
		configurePanels();
		configureOtherComponents();
	}

	private void configureFrame() {
		this.frame.setLocation(400, 100);
		this.frame.setSize(600, 450);
		this.frame.setLayout(this.frameLayout);
		this.frame.setResizable(false);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void configurePanels() {
		this.mainPanel.setLayout(this.mainPanelLayout);
		this.senderNamePanel.setLayout(this.senderPanelLayout);
		this.receiverNamePanel.setLayout(this.receiverPanelLayout);
		this.northPanel.setLayout(this.northPanelLayout);
		this.southPanel.setLayout(this.southPanelLayout);

		this.centerPanel.setLayout(this.centerPanelLayout);
		this.centerNorthPanel.setLayout(this.centerNorthPanelLayout);
		this.centerSouthPanel.setLayout(this.centerSouthPanelLayout);
		this.centerSouthWestPanel.setLayout(this.centerSouthWestPanelLayout);
		this.centerSouthCenterPanel.setLayout(this.centerSouthCenterPanelLayout);
		this.centerSouthEastPanel.setLayout(this.centerSouthEastPanelLayout);
		this.centerPanel.setBorder(new MatteBorder(1, 1, 1, 1, Color.BLUE));
	}

	private void configureOtherComponents() {
		this.mainTextArea.setBorder(new MatteBorder(1, 1, 1, 1, Color.GREEN));
		this.senderNameTextField.setText(this.loginInformation.getUserName());
		this.senderNameTextField.setEditable(false);

		this.addExtraItemButton.addActionListener(this.editMailUIMonitor);
		this.addExtraItemButton.setActionCommand(EditMailUICommandCode.ADD_EXTRA_ITEM.toString());

		this.removeExtraItemButton.addActionListener(this.editMailUIMonitor);
		this.removeExtraItemButton.setActionCommand(EditMailUICommandCode.REMOVE_EXTRA_ITEM.toString());

		this.sendButton.addActionListener(this.editMailUIMonitor);
		this.sendButton.setActionCommand(EditMailUICommandCode.SEND.toString());
	}

	private void addComponents() {
		this.senderNamePanel.add(this.senderLabel, BorderLayout.WEST);
		this.senderNamePanel.add(this.senderNameTextField, BorderLayout.CENTER);
		this.receiverNamePanel.add(this.receiverLabel, BorderLayout.WEST);
		this.receiverNamePanel.add(this.receiverAddressTextField, BorderLayout.CENTER);

		this.centerSouthWestPanel.add(this.addExtraItemButton);
		this.centerSouthCenterPanel.add(this.extraItemNameLabel);
		this.centerSouthEastPanel.add(this.removeExtraItemButton);

		this.centerSouthPanel.add(this.centerSouthWestPanel, BorderLayout.WEST);
		this.centerSouthPanel.add(this.centerSouthCenterPanel, BorderLayout.CENTER);
		this.centerSouthPanel.add(this.centerSouthEastPanel, BorderLayout.EAST);

		this.centerNorthPanel.add(this.subjectLabel, BorderLayout.WEST);
		this.centerNorthPanel.add(this.subjectTextField, BorderLayout.CENTER);
		this.centerNorthPanel.add(this.mainTextLabel, BorderLayout.SOUTH);

		this.centerPanel.add(this.centerNorthPanel, BorderLayout.NORTH);
		this.centerPanel.add(this.mainTextArea, BorderLayout.CENTER);
		this.centerPanel.add(this.centerSouthPanel, BorderLayout.SOUTH);

		this.northPanel.add(this.senderNamePanel);
		this.northPanel.add(this.receiverNamePanel);
		this.southPanel.add(this.sendButton);

		this.mainPanel.add(this.northPanel, BorderLayout.NORTH);
		this.mainPanel.add(this.centerPanel, BorderLayout.CENTER);
		this.mainPanel.add(this.southPanel, BorderLayout.SOUTH);

		this.frame.add(this.mainPanel);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// new EditMailUI("发信人").intiUI();

	}

	private class EditMailUIMonitor implements ActionListener {
		private EditMailUICommandCode commandCode;

		@Override
		public void actionPerformed(ActionEvent e) {
			this.commandCode = EditMailUICommandCode.valueOf(e.getActionCommand());
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

			if (mail.getExtraItemsAmount() == mail.EXTRA_ITEM_CAPACITY - 1) {
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
			if (mail.getExtraItemsAmount() > 0) {
				mail.addExtraItemsToMultipart();
			}
			mail.setReceiverAddresses(receiversAddressArray);
			return true;
		}
	}
}
