package client.UI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import beans.MailBean;

public class MailListUI extends JFrame  implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int MAILS_EACH_PAGE = 10;
	private JPanel mainPanel = new JPanel();
	private JPanel northPanel = new JPanel();
	private JPanel centerPanel = new JPanel();
	private JPanel southPanel = new JPanel();
	private List<MailURLLabel> mails;
	private int mailCount = 0;
	private JLabel northLabel = new JLabel("", JLabel.CENTER);
	private JButton prePageButton = new JButton("上一页");
	private JButton nextPageButton = new JButton("下一页");

	public MailListUI(List<MailURLLabel> mails) {
		this.mails = mails;
		this.mailCount = mails.size();
		this.setTitle("邮件列表");

		configureFrame();
		northLabel.setText("共有 " + mailCount + " 封邮件");
		setLayouts();
		configurePanels(mails);
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

	private void configurePanels(List<MailURLLabel> mails) {
		this.add(mainPanel, BorderLayout.CENTER);

		mainPanel.setBorder(BorderFactory.createEtchedBorder());
		northPanel.setBorder(BorderFactory.createEtchedBorder());
		southPanel.setBorder(BorderFactory.createEtchedBorder());
		
		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		northPanel.add(northLabel, BorderLayout.CENTER);
		southPanel.add(prePageButton, BorderLayout.WEST);
		southPanel.add(nextPageButton, BorderLayout.EAST);
		
		

		addMailsToCenterPanel(mails);
	}

	private void addMailsToCenterPanel(List<MailURLLabel> mailLabels) {

		for (int i = 0; i < mailCount; i++) {
			if (i == MAILS_EACH_PAGE) {
				break;
			}
			centerPanel.add(mailLabels.get(i));
		}
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
		new MailListUI(list);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
