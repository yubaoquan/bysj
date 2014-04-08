package beans;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import util.Util;

public class LocalMailBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int FOR_INTERNET_SERVER = 0;
	public static final int FOR_LOCAL_SERVER = 1;
	public final int ATTACHMENTS_CAPACITY = 3;

	private int attachmentCounter = 0;
	private String subject;
	private String text;
	private String sender;
	private String addressee;
	private Timestamp sendTime;
	private File[] attachmentsForLocalServer = new File[ATTACHMENTS_CAPACITY];
	private String attachments;
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public String getContent() {
		return text;
	}

	public void setContent(String content) {
		setText(content);
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void addAttachment(File attachment, int mailType) {
		if (attachmentsAreFull()) {
			return;
		}
		addAttachmentForLocalServer(attachment);
		this.attachmentCounter++;
	}

	public boolean attachmentsAreFull() {
		return this.attachmentCounter >= ATTACHMENTS_CAPACITY;
	}

	private void addAttachmentForLocalServer(File attachment) {
		attachmentsForLocalServer[attachmentCounter] = attachment;
		System.out.println("Add attachment for local server");
	}

	public void removeAllExtraItems() {
		attachmentsForLocalServer = new File[ATTACHMENTS_CAPACITY];
		attachmentCounter = 0;
	}

	public int getAttachmentsAmount() {
		return attachmentCounter;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getAddressee() {
		return addressee;
	}

	public void setAddressee(String addressee) {
		this.addressee = addressee;
	}

	public Timestamp getSendTime() {
		return sendTime;
	}

	public void setSendTime(Timestamp sendTime) {
		this.sendTime = sendTime;
	}

	public File getAttachment(int offset) {
		return attachmentsForLocalServer[offset];
	}

	public void showPropertiesForLocalServer() {
		Util.println("id: " + this.getId());
		Util.println("sender: " + this.getSender());
		Util.println("addressee: " + this.getAddressee());
		Util.println("subject: " + this.getSubject());
		Util.println("sender: " + this.getSender());
		Util.println("content: " + this.getText());

	}

	public String getAttachments() {
		return attachments;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}
}
