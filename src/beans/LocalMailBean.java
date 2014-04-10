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
	private String attachmentNames;
	private String attachmentLocations;
	private int id;

	public void addAttachment(File attachment, int mailType) {
		if (attachmentsAreFull()) {
			return;
		}
		addAttachmentForLocalServer(attachment);
		this.attachmentCounter++;
	}

	private void addAttachmentForLocalServer(File attachment) {
		attachmentsForLocalServer[attachmentCounter] = attachment;
		System.out.println("Add attachment for local server");
	}

	public boolean attachmentsAreFull() {
		return this.attachmentCounter >= ATTACHMENTS_CAPACITY;
	}

	public String getAddressee() {
		return addressee;
	}

	public File getAttachment(int offset) {
		return attachmentsForLocalServer[offset];
	}

	public String getAttachmentLocations() {
		return attachmentLocations;
	}

	public String getAttachmentNames() {
		return attachmentNames;
	}

	public int getAttachmentsAmount() {
		return attachmentCounter;
	}

	public String getContent() {
		return text;
	}

	public int getID() {
		return id;
	}

	public String getSender() {
		return sender;
	}

	public Timestamp getSendTime() {
		return sendTime;
	}

	public String getSubject() {
		return subject;
	}

	public String getText() {
		return text;
	}

	public void removeAllExtraItems() {
		attachmentsForLocalServer = new File[ATTACHMENTS_CAPACITY];
		attachmentCounter = 0;
	}

	public void setAddressee(String addressee) {
		this.addressee = addressee;
	}

	public void setAttachmentLocations(String attachmentLocations) {
		this.attachmentLocations = attachmentLocations;
	}

	public void setAttachmentNames(String attachments) {
		this.attachmentNames = attachments;
	}

	public void setContent(String content) {
		setText(content);
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public void setSendTime(Timestamp sendTime) {
		this.sendTime = sendTime;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void showPropertiesForLocalServer() {
		Util.println("id: " + this.getID());
		Util.println("sender: " + this.getSender());
		Util.println("addressee: " + this.getAddressee());
		Util.println("subject: " + this.getSubject());
		Util.println("sender: " + this.getSender());
		Util.println("content: " + this.getText());

	}
}
