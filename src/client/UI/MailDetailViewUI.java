package client.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

import util.Util;
import client.UI.EditMailUI.EditMailUICommandCode;
import beans.MailBean;
import beans.UserLoginBean;

public class MailDetailViewUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private MailBean mail = null;
	private MailListUI parentUI = null;

	private JPanel mainPanel = new JPanel();
	private JPanel senderNamePanel = new JPanel();
	private JPanel sentTimePanel = new JPanel();
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
	
	private JLabel sentTimeLabel = new JLabel("发送于:             ");
	private JTextField sentTimeTextField = new JTextField();
	
	private JLabel addresseeLabel = new JLabel("收件人:             ");
	private JTextField addresseeTextField = new JTextField();
	private JLabel subjectLabel = new JLabel("标题");
	private JTextField subjectTextField = new JTextField();
	private JLabel mainTextLabel = new JLabel("正文");
	private JScrollPane scrollPane = new JScrollPane();
	private JTextArea contentTextArea = new JTextArea();
	private JLabel attachmentNameLabel = new JLabel("附件:");
	private JButton backButton = new JButton("返回邮件列表");
	private JButton downloadAttachmentButton = new JButton("下载附件");

	private MailDetailViewUIMonitor monitor = new MailDetailViewUIMonitor();

	public MailDetailViewUI(MailBean mail, MailListUI parent) {
		this.mail = mail;
		this.parentUI = parent;
	}
	
	public void launch() {
		intiUI();
		this.parentUI.setVisible(false);
		this.validate();
		setVisible(true);
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
		this.setTitle("邮件详情");
		setLocation(400, 100);
		setSize(600, 450);
		setLayout(new BorderLayout());
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void setLayouts() {
		mainPanel.setLayout(new BorderLayout());
		senderNamePanel.setLayout(new BorderLayout());
		sentTimePanel.setLayout(new BorderLayout());
		receiverNamePanel.setLayout(new BorderLayout());
		northPanel.setLayout(new GridLayout(3, 1));
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
		subjectTextField.setText(mail.getSubject());
		subjectTextField.setEditable(false);
		
		contentTextArea.setBorder(new MatteBorder(1, 1, 1, 1, Color.GREEN));
		contentTextArea.setText(mail.getText());
		contentTextArea.setLineWrap(true);
		contentTextArea.setWrapStyleWord(true);
		contentTextArea.setEditable(false);
		
		senderNameTextField.setText(mail.getSender());
		senderNameTextField.setEditable(false);
		sentTimeTextField.setText(mail.getSendTime().toString());
		sentTimeTextField.setEditable(false);
		addresseeTextField.setText(mail.getAddressee());
		addresseeTextField.setEditable(false);

		downloadAttachmentButton.setActionCommand(MailDetailViewUIMonitor.DOWNLOAD_ATTACHMENT);
		downloadAttachmentButton.addActionListener(monitor);
		backButton.setActionCommand(MailDetailViewUIMonitor.BACK);
		backButton.addActionListener(monitor);
		
		setAttachmentsNames();
	}

	private void setAttachmentsNames() {
		String shortAttachment1Name = "1." + Util.getShortStringWithEllipsis(mail.getAttachment1Name(), 10);
		String shortAttachment2Name = "2." + Util.getShortStringWithEllipsis(mail.getAttachment2Name(), 10);
		String shortAttachment3Name = "3." + Util.getShortStringWithEllipsis(mail.getAttachment3Name(), 10);
		String attachmentsNames = "附件: " + shortAttachment1Name + " " +  shortAttachment2Name + " " +shortAttachment3Name;
		attachmentNameLabel.setText(attachmentsNames);
	}

	private void addComponents() {
		senderNamePanel.add(senderLabel, BorderLayout.WEST);
		senderNamePanel.add(senderNameTextField, BorderLayout.CENTER);
//TODO
		sentTimePanel.add(sentTimeLabel, BorderLayout.WEST);
		sentTimePanel.add(sentTimeTextField, BorderLayout.CENTER);
		
		receiverNamePanel.add(addresseeLabel, BorderLayout.WEST);
		receiverNamePanel.add(addresseeTextField, BorderLayout.CENTER);

		centerSouthCenterPanel.add(attachmentNameLabel);
		centerSouthEastPanel.add(downloadAttachmentButton);

		centerSouthPanel.add(centerSouthWestPanel, BorderLayout.WEST);
		centerSouthPanel.add(centerSouthCenterPanel, BorderLayout.CENTER);
		centerSouthPanel.add(centerSouthEastPanel, BorderLayout.EAST);

		centerNorthPanel.add(subjectLabel, BorderLayout.WEST);
		centerNorthPanel.add(subjectTextField, BorderLayout.CENTER);
		centerNorthPanel.add(mainTextLabel, BorderLayout.SOUTH);

		centerPanel.add(centerNorthPanel, BorderLayout.NORTH);
		//TODO
		scrollPane = new JScrollPane(this.contentTextArea);
		centerPanel.add(scrollPane, BorderLayout.CENTER);
		
		centerPanel.add(centerSouthPanel, BorderLayout.SOUTH);

		northPanel.add(senderNamePanel);
		northPanel.add(sentTimePanel);
		northPanel.add(receiverNamePanel);
		southPanel.add(backButton);

		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);

		this.add(mainPanel);
	}

	public static void main(String[] args) {
		MailBean mail = new MailBean();
		mail.setId(1);
		mail.setSender("发信人");
		mail.setAddressee("收信人");
		mail.setSubject("标题");
		mail.setText("正文");
		mail.setAttachment1Name("系统提示");
		mail.setAttachment2Name("很抱歉, 操作执行不成功！");
		mail.setAttachment3Name("现网问题跟踪报告模板更改通知");
		
		MailURLLabel mailLabel = new MailURLLabel(mail);
		List<MailURLLabel> list = new ArrayList<>();
		list.add(mailLabel);
		//	TODO
		MailListUI parent = new MailListUI();
		new MailDetailViewUI(mail, parent).launch();

	}

	public MailBean getMail() {
		return mail;
	}

	public void setMail(MailBean mail) {
		this.mail = mail;
	}

	private class MailDetailViewUIMonitor implements ActionListener {
		private String command = null;
		private static final String BACK = "BACK";
		private static final String DOWNLOAD_ATTACHMENT = "DOWNLOAD_ATTACHMENT";

		@Override
		public void actionPerformed(ActionEvent e) {
			command = e.getActionCommand();
			switch (command) {
				case BACK:
					onBackButtonPressed();
					break;
				case DOWNLOAD_ATTACHMENT:
					onDownloadAttachmentButtonPressed();
					break;
				default:
					System.out.println("parse command error");
					System.exit(-1);
			}
		}

		private void onDownloadAttachmentButtonPressed() {
			System.out.println("download attachment");
		}

		private void onBackButtonPressed() {
			System.out.println("back");
			MailDetailViewUI.this.parentUI.setVisible(true);
			MailDetailViewUI.this.dispose();
		}
	}
}
