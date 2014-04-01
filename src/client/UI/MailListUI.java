package client.UI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import util.Util;
import beans.MailBean;

public class MailListUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int MAILS_EACH_PAGE = 10;
	private static final String NEXT_PAGE = "NEXT_PAGE";
	private static final String PREVIOUS_PAGE = "PREVIOUS_PAGE";
	
	private JPanel mainPanel = new JPanel();
	private JPanel northPanel = new JPanel();
	private JPanel centerPanel = new JPanel();
	private JPanel southPanel = new JPanel();
	private List<MailURLLabel> mailURLLabels;
	public List<MailURLLabel> getMailLabels() {
		return mailURLLabels;
	}

	public void setMailURLLabels(List<MailURLLabel> mailLabels) {
		this.mailURLLabels = mailLabels;
		this.mailCount = mailLabels.size();
		northLabel.setText("共有 " + mailCount + " 封邮件");
		configurePanels(mailLabels);
		configureButtons();
	}

	private int mailCount = 0;
	private boolean atFirstPage = true;
	private boolean atLastPage = false;
	
	private JLabel northLabel = new JLabel("", JLabel.CENTER);
	private JButton previousButton = new JButton("上一页");
	private JButton nextPageButton = new JButton("下一页");

	MailListUIMonitor monitor = new MailListUIMonitor();
	
	public MailListUI() {
		this.setTitle("邮件列表");
		configureFrame();
		setLayouts();
	}
	
	public MailListUI(List<MailURLLabel> mailURLLabels) {
		this();
		this.setMailURLLabels(mailURLLabels);
	}

	private void launch() {
		this.setVisible(true);
	}

	

	private void configureFrame() {
		this.setLocation(400, 250);
		this.setSize(400, 250);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void setLayouts() {
		this.setLayout(new BorderLayout());
		mainPanel.setLayout(new BorderLayout());
		northPanel.setLayout(new BorderLayout());
		centerPanel.setLayout(new FlowLayout());
		southPanel.setLayout(new BorderLayout());
	}

	private void configurePanels(List<MailURLLabel> mailLabels) {
		this.add(mainPanel, BorderLayout.CENTER);

		mainPanel.setBorder(BorderFactory.createEtchedBorder());
		northPanel.setBorder(BorderFactory.createEtchedBorder());
		southPanel.setBorder(BorderFactory.createEtchedBorder());
		
		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		northPanel.add(northLabel, BorderLayout.CENTER);
		
		southPanel.add(previousButton, BorderLayout.WEST);
		southPanel.add(nextPageButton, BorderLayout.EAST);
		
		addMailsToCenterPanel(mailLabels);
	}

	private void configureButtons() {
		previousButton.setActionCommand(PREVIOUS_PAGE);
		previousButton.addActionListener(monitor);
		previousButton.setEnabled(false);
		
		nextPageButton.setActionCommand(NEXT_PAGE);
		nextPageButton.addActionListener(monitor);
		if (this.mailCount < MAILS_EACH_PAGE) {
			nextPageButton.setEnabled(false);
		}
	}

	private void addMailsToCenterPanel(List<MailURLLabel> mailLabels) {
		for (int i = 0; i < mailCount; i++) {
			if (i == MAILS_EACH_PAGE) {
				break;
			}
			MailURLLabel tempMailURLLabel = mailLabels.get(i);
			centerPanel.add(mailLabels.get(i));
		}
	}

	public static void main(String[] args) {
		MailListUI mailListUI = new MailListUI();
		
		MailBean mail = new MailBean();
		mail.setId(1);
		mail.setSender("发信人");
		mail.setAddressee("收信人");
		mail.setSubject("标题");
		mail.setText("正文");
		mail.setAttachment1Name("系统提示");
		mail.setAttachment2Name("很抱歉, 操作执行不成功！");
		mail.setAttachment3Name("现网问题跟踪报告模板更改通知");
		
		
		MailURLLabel mailLabel = new MailURLLabel(mail,mailListUI);
		List<MailURLLabel> list = new ArrayList<>();
		list.add(mailLabel);
		mailListUI.setMailURLLabels(list);
		mailListUI.launch();
	}

	private class MailListUIMonitor  implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			switch (command) {
				case PREVIOUS_PAGE:
					onPreviousButtonPressed();
					break;
				case NEXT_PAGE:
					onNextButtonPressed();
					break;
					default:
						Util.println("error in MailListUIMonitor");
						System.exit(-1);
			}
			
		}

		private void onNextButtonPressed() {
			Util.println("下一页");
		}

		private void onPreviousButtonPressed() {
			Util.println("上一页");
		}
		
	}

}
