package client.UI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Panel;
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
	
	private JLabel northLabel = new JLabel("", JLabel.CENTER);
	private JButton previousButton = new JButton("上一页");
	private JButton nextPageButton = new JButton("下一页");

	MailListUIMonitor monitor = new MailListUIMonitor();

	private List<MailBean> mails;
	private List<MailURLLabel> mailURLLabels = new ArrayList<>();
	private int mailCount = 0;
	private int currentPage = 0;
	private int lastPage = 0;
	public int currentIndex = 0;//the position in the mail URL list of the first link in the center panel
	
	public MailListUI() {
		this.setTitle("邮件列表");
		configureFrame();
		setLayouts();
	}

	public MailListUI(List<MailBean> mails) {
		this();
		this.mails = mails;
		/*for (MailBean mail : mails) {
			MailURLLabel mailURLLabel = new MailURLLabel(mail, this);
			Util.println("add " + mailURLLabel.getMailSubject());
			mailURLLabels.add(mailURLLabel);
		}*/
		for (int i = mails.size(); i > 0; i --) {
			MailURLLabel mailURLLabel = new MailURLLabel(mails.get(i - 1), this);
			Util.println("add " + mailURLLabel.getMailSubject());
			mailURLLabels.add(mailURLLabel);
		}
		this.setMailURLLabels(mailURLLabels);
		launch();
	}
	
	public void launch() {
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
		centerPanel.setLayout(new GridLayout(10,1));
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

		addMailsToCenterPanel(mailLabels, 0);
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

	private void addMailsToCenterPanel(List<MailURLLabel> mailLabels, int startIndex) {
		if (startIndex < mailLabels.size()) {
			centerPanel.removeAll();
			centerPanel.setLayout(new GridLayout(10,1));
			int currentIndex = 0;
			for (int i = 0; i < MAILS_EACH_PAGE; i++) {
				currentIndex = i +startIndex;
				if (currentIndex == mailCount) {
					break;
				}
				centerPanel.add(mailLabels.get(currentIndex), i);
				Util.println("add " + i + (mailLabels.get(currentIndex)).getMailSubject());
			}
			centerPanel.validate();
			this.repaint();
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
		/*mail.setAttachment1Name("系统提示");
		mail.setAttachment2Name("很抱歉, 操作执行不成功！");
		mail.setAttachment3Name("现网问题跟踪报告模板更改通知");*/

		MailURLLabel mailLabel = new MailURLLabel(mail, mailListUI);
		List<MailURLLabel> list = new ArrayList<>();
		list.add(mailLabel);
		mailListUI.setMailURLLabels(list);
		mailListUI.launch();
	}

	private class MailListUIMonitor implements ActionListener {

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
			if (currentPage < lastPage) {
				currentPage ++;
				previousButton.setEnabled(true);
				currentIndex += MAILS_EACH_PAGE;
				addMailsToCenterPanel(mailURLLabels, currentIndex);
				if (currentPage == lastPage - 1) {
					nextPageButton.setEnabled(false);
				}
			}
		}

		private void onPreviousButtonPressed() {
			Util.println("上一页");
			if (currentPage > 0) {
				currentPage --;
				nextPageButton.setEnabled(true);
				currentIndex -= MAILS_EACH_PAGE;
				addMailsToCenterPanel(mailURLLabels, currentIndex);
				if (currentPage == 0) {
					previousButton.setEnabled(false);
				}
			}
		}

	}

	public List<MailURLLabel> getMailLabels() {
		return mailURLLabels;
	}

	public void setMailURLLabels(List<MailURLLabel> mailLabels) {
		mailURLLabels = mailLabels;
		mailCount = mailLabels.size();
		lastPage = calculateTotalPages(mailCount);
		northLabel.setText("共有 " + mailCount + " 封邮件");
		configurePanels(mailLabels);
		configureButtons();
	}
	
	private int  calculateTotalPages(int records) {
		int totalPages = 0;
		if (records <= 10) {
			totalPages = 1;
		} else {
			totalPages = records / 10;
			if (records % 10 != 0) {
				totalPages ++;
			}
		}
		Util.println("records: " + records + " pages: " + totalPages);
		return totalPages;
	}
}
