package client.UI;

import static java.lang.System.out;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.mail.MessagingException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import client.io.FileStreamSaver;
import client.net.down.ReceiveMail;
import util.Util;
import beans.AttachmentBean;
import beans.Constant;
import beans.LabelBean;
import beans.MailBean;

public class URLLabel extends JLabel implements MouseListener {

	public static final int FOR_MAIL = 0;
	public static final int FOR_ATTACHMENT = 1;
	private int type = FOR_MAIL;
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	private static final long serialVersionUID = 1L;
	private int id;
	private String subject;
	private LabelBean labelBean;
	private ItemListUI parent;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public URLLabel(LabelBean label, ItemListUI parent, int type) {
		this(label);
		this.parent = parent;
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
				new MailDetailUI((MailBean)labelBean, parent).launch();
				break;
			case FOR_ATTACHMENT:
				parent.dispose();
				out.println("download attachment");
				AttachmentBean attachment = (AttachmentBean) labelBean;
				
				String fileName = Util.replaceIllegalCharacters(attachment.getSubject());
				String folderPath = ReceiveMail.getAttachmentFolderPath();
				String filePath = folderPath + File.separator  + fileName;
				File file = new File(filePath);
				
				try {
					file.createNewFile();
					InputStream is= attachment.getPart().getInputStream();
					FileStreamSaver fileStreamSaver = new FileStreamSaver(file, is);
					fileStreamSaver.storeFile();
					fileStreamSaver.closeStream();
					JOptionPane.showMessageDialog(parent, (String) "附件下载完成.文件位置： " +filePath + ".", "完成", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				break;
			default:
				break;
		}

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
