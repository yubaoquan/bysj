package client.UI;

import java.awt.BorderLayout;
import java.awt.GridLayout;
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
import beans.AttachmentBean;
import beans.MailBean;
import client.net.down.ReceiveMail;

public class ItemListUI extends JFrame {
	// [
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

	private ReceiveMail receiveMail;

	public ReceiveMail getReceiveMail() {
		return receiveMail;
	}

	public void setReceiveMail(ReceiveMail parent) {
		this.receiveMail = parent;
	}

	private List<URLLabel> labels = new ArrayList<>();
	private int labelCount = 0;
	private int currentPage = 0;
	private int lastPage = 0;
	public int currentIndex = 0;// the position in the mail URL list of the
								// first link in the center panel

	// ]
	public ItemListUI() {
		// this.setTitle("邮件列表");
		configureFrame();
		setLayouts();
	}

	@SuppressWarnings("rawtypes")
	public ItemListUI(List items, ReceiveMail parent) {
		this(items, URLLabel.FOR_MAIL);
		this.receiveMail = parent;
	}

	@SuppressWarnings("rawtypes")
	public ItemListUI(List items, int type) {
		this();
		switch (type) {
			case URLLabel.FOR_MAIL:
				this.setTitle("邮件列表");
				for (int i = items.size(); i > 0; i--) {
					URLLabel mailURLLabel = new URLLabel((MailBean) items.get(i - 1), this, URLLabel.FOR_MAIL);
					// Util.println("add " + mailURLLabel.getSubject());
					labels.add(mailURLLabel);
				}
				break;
			case URLLabel.FOR_ATTACHMENT:
				this.setTitle("附件列表");
				for (int i = items.size(); i > 0; i--) {
					URLLabel attachmentURLLabel = new URLLabel((AttachmentBean) items.get(i - 1), this, URLLabel.FOR_ATTACHMENT);
					// Util.println("add " + mailURLLabel.getSubject());
					labels.add(attachmentURLLabel);
				}
				break;
		}
		this.setMailURLLabels(labels);
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
		centerPanel.setLayout(new GridLayout(10, 1));
		southPanel.setLayout(new BorderLayout());
	}

	private void configurePanels(List<URLLabel> mailLabels) {
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
		if (this.labelCount < MAILS_EACH_PAGE) {
			nextPageButton.setEnabled(false);
		}
	}

	private void addMailsToCenterPanel(List<URLLabel> itemLabels, int startIndex) {
		if (startIndex < itemLabels.size()) {
			centerPanel.removeAll();
			centerPanel.setLayout(new GridLayout(10, 1));
			int currentIndex = 0;
			for (int i = 0; i < MAILS_EACH_PAGE; i++) {
				currentIndex = i + startIndex;
				if (currentIndex == labelCount) {
					break;
				}
				centerPanel.add(itemLabels.get(currentIndex), i);
				Util.println("add " + i + (itemLabels.get(currentIndex)).getSubject());
			}
			centerPanel.validate();
			this.repaint();
		}
	}

	public static void main(String[] args) {
		ItemListUI mailListUI = new ItemListUI();

		MailBean mail = new MailBean();
		mail.setId(1);
		mail.setSender("发信人");
		mail.setAddressee("收信人");
		mail.setSubject("标题");
		mail.setText("正文");
		/*
		 * mail.setAttachment1Name("系统提示");
		 * mail.setAttachment2Name("很抱歉, 操作执行不成功！");
		 * mail.setAttachment3Name("现网问题跟踪报告模板更改通知");
		 */

		URLLabel mailLabel = new URLLabel(mail, mailListUI, URLLabel.FOR_MAIL);
		List<URLLabel> list = new ArrayList<>();
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
				currentPage++;
				previousButton.setEnabled(true);
				currentIndex += MAILS_EACH_PAGE;
				addMailsToCenterPanel(labels, currentIndex);
				if (currentPage == lastPage - 1) {
					nextPageButton.setEnabled(false);
				}
			}
		}

		private void onPreviousButtonPressed() {
			Util.println("上一页");
			if (currentPage > 0) {
				currentPage--;
				nextPageButton.setEnabled(true);
				currentIndex -= MAILS_EACH_PAGE;
				addMailsToCenterPanel(labels, currentIndex);
				if (currentPage == 0) {
					previousButton.setEnabled(false);
				}
			}
		}

	}

	public List<URLLabel> getMailLabels() {
		return labels;
	}

	public void setMailURLLabels(List<URLLabel> itemLbels) {
		labels = itemLbels;
		labelCount = itemLbels.size();
		lastPage = calculateTotalPages(labelCount);
		northLabel.setText("共有 " + labelCount + " 封邮件");
		configurePanels(itemLbels);
		configureButtons();
	}

	private int calculateTotalPages(int records) {
		int totalPages = 0;
		if (records <= 10) {
			totalPages = 1;
		} else {
			totalPages = records / 10;
			if (records % 10 != 0) {
				totalPages++;
			}
		}
		Util.println("records: " + records + " pages: " + totalPages);
		return totalPages;
	}
}
