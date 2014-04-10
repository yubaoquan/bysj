package client.UI;

import static java.lang.System.out;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.InputStream;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import util.Util;
import beans.AttachmentBean;
import beans.LabelBean;
import beans.MailBean;
import beans.UserBean;
import client.io.FileStreamSaver;
import client.net.down.ReceiveMail;
import client.net.up.Transmitter;

public class URLLabel extends JLabel implements MouseListener {

	public static final int FOR_MAIL = 0;
	public static final int FOR_ATTACHMENT = 1;
	private int type = FOR_MAIL;
	private static final long serialVersionUID = 1L;
	private int id;
	private String subject;
	private LabelBean labelBean;
	private ItemListUI itemListUI;
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	

	public ItemListUI getItemListUI() {
		return itemListUI;
	}

	public void setItemListUI(ItemListUI itemListUI) {
		this.itemListUI = itemListUI;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public URLLabel(LabelBean label, ItemListUI parent, int type) {
		this(label);
		this.itemListUI = parent;
		this.type = type;
	}

	public URLLabel(LabelBean label) {
		this(label.getID(), label.getSubject());
		this.labelBean = label;
	}

	public URLLabel(AttachmentBean attachment) {
		type = FOR_ATTACHMENT;
		this.id = attachment.getID();
		this.setSubject(attachment.getSubject());
		this.setForeground(Color.blue);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));// 设置鼠标样式
		this.setToolTipText("点击下载附件");// 设置提示文字
		this.addMouseListener(this);
	}

	public URLLabel(int id, String subject) {
		this.id = id;
		this.setSubject(subject);
		this.setForeground(Color.blue);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));// 设置鼠标样式
		this.setToolTipText("点击打开邮件");// 设置提示文字
		this.addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		switch (type) {
			case FOR_MAIL:
				System.out.println("id: " + id + " subject: " + subject);
				new MailDetailUI((MailBean) labelBean, itemListUI).launch();
				break;
			case FOR_ATTACHMENT:
				itemListUI.dispose();

				((AttachmentBean) labelBean).getReceiveMail().downloadAttachment((AttachmentBean) labelBean, itemListUI);
				break;
			default:
				break;
		}

	}

	/*
	 * private void downloadAttachment(AttachmentBean attachment) {
	 * out.println("download attachment"); String fileName =
	 * Util.replaceIllegalCharacters(attachment.getSubject()); String folderPath
	 * = ReceiveMail.getAttachmentFolderPath(); String filePath = folderPath +
	 * File.separator + fileName; File file = new File(filePath);
	 * 
	 * try { file.createNewFile(); InputStream is = null; if
	 * (attachment.getMailID() != -1) {//From local mail. UserBean owner = new
	 * UserBean(); owner.setUserName(attachment.getOwner());
	 * Transmitter.getInstance(owner); } else {// From Internet mail. is=
	 * attachment.getPart().getInputStream(); }
	 * 
	 * FileStreamSaver fileStreamSaver = new FileStreamSaver(file, is);
	 * fileStreamSaver.storeFile(); fileStreamSaver.closeStream();
	 * JOptionPane.showMessageDialog(itemListUI, (String) "附件下载完成.文件位置： "
	 * +filePath + ".", "完成", JOptionPane.INFORMATION_MESSAGE); } catch
	 * (Exception e1) { e1.printStackTrace(); } }
	 */

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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String mailSubject) {
		this.subject = mailSubject;
		this.setText(subject);
	}

	public int getMailID() {
		return id;
	}

	public void setMailID(int mailID) {
		this.id = mailID;
	}

}
