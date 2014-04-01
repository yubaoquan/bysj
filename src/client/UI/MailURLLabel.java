package client.UI;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import beans.MailBean;

public class MailURLLabel extends JLabel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mailID;
	private String mailSubject;
	private MailBean mail;
	private MailListUI parent;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public MailURLLabel(MailBean mail, MailListUI parent) {
		this(mail);
		this.parent = parent;
	}
	
	public MailURLLabel(MailBean mail) {
		this(mail.getId(), mail.getSubject());
		this.mail = mail;
	}
	
	public MailURLLabel(int id, String subject) {
		this.mailID = id;
		this.mailSubject = subject;
		this.setText(mailSubject);
		this.setForeground(Color.blue);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));// 设置鼠标样式
		this.setToolTipText("点击打开邮件");// 设置提示文字
		this.addMouseListener(this);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("id: " + mailID + " subject: " + mailSubject);
		new MailDetailViewUI(mail, parent).launch();
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

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public int getMailID() {
		return mailID;
	}

	public void setMailID(int mailID) {
		this.mailID = mailID;
	}

}
