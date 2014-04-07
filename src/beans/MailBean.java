package beans;

import java.io.File;
import java.io.IOException;
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

public class MailBean {

	public static final int FOR_INTERNET_SERVER = 0;
	public static final int FOR_LOCAL_SERVER = 1;
	public final int ATTACHMENTS_CAPACITY = 3;
	
	// this fields below are used for mail servers on the internet
	private Multipart multipart;
	private int attachmentCounter = 0;
	private MimeBodyPart[] attachmentsForInternetServer = new MimeBodyPart[ATTACHMENTS_CAPACITY];
	private String subject;
	private String text;
	private InternetAddress[] internetAddressees = new InternetAddress[10];

	// the fields below are used for this system's own mail server
	private String sender;
	private String addressee;
	private Timestamp sentTime;
	private File[] attachmentsForLocalServer = new File[ATTACHMENTS_CAPACITY];
	private String attachments;
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public MailBean() {
		this.multipart = new MimeMultipart();
	}

	public Multipart getMutipart() {
		return multipart;
	}

	public void setMutipary(Multipart mutipary) {
		this.multipart = mutipary;
	}

	public MimeBodyPart[] getExtraItems() {
		return attachmentsForInternetServer;
	}

	public void setExtraItems(MimeBodyPart[] extraItems) {
		this.attachmentsForInternetServer = extraItems;
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

	public InternetAddress[] getInternetAddressees() {
		return internetAddressees;
	}

	public void setInternetAddressees(InternetAddress[] receiverAddresses) {
		this.internetAddressees = receiverAddresses;
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
		if (mailType == FOR_INTERNET_SERVER) {
			addAttachmentForInternetServer(attachment);
		} else if (mailType == FOR_LOCAL_SERVER) {
			addAttachmentForLocalServer(attachment);
		}
		this.attachmentCounter++;
	}

	public boolean attachmentsAreFull() {
		return this.attachmentCounter >= ATTACHMENTS_CAPACITY;
	}
	
	private void addAttachmentForInternetServer(File attachment) {
		try {
			attachmentsForInternetServer[attachmentCounter] = new MimeBodyPart();
			initMIMEBodyPart(attachmentsForInternetServer[attachmentCounter], attachment);
			if (attachmentsForInternetServer[attachmentCounter] == null) {
				System.out.println("null pointer");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addAttachmentForLocalServer(File attachment) {
		attachmentsForLocalServer[attachmentCounter] = attachment;
		System.out.println("Add attachment for local server");
	}
	
	private void initMIMEBodyPart(MimeBodyPart mbp, File file) throws IOException, MessagingException {
		mbp.setText("text");// 没有这一句的话,发送的附件会显示成一堆文本.
		FileDataSource fds = new FileDataSource(file.getAbsolutePath());
		mbp.setDataHandler(new DataHandler(fds));
		mbp.setFileName(fds.getName());
	}

	public void removeAllExtraItems() {
		attachmentsForInternetServer = new MimeBodyPart[ATTACHMENTS_CAPACITY];
		attachmentsForLocalServer = new File[ATTACHMENTS_CAPACITY];
		attachmentCounter = 0;
	}

	public int getAttachmentsAmount() {
		return attachmentCounter;
	}

	public void addAttachmentsToMultipart() {
		for (int i = 0; i < attachmentCounter; i++) {
			try {
				multipart.addBodyPart(attachmentsForInternetServer[i]);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
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
		return sentTime;
	}

	public void setSentTime(Timestamp sendTime) {
		this.sentTime = sendTime;
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
		/*Util.println("attachment 1 : " + this.getAttachment1Name());
		Util.println("attachment 2 : " + this.getAttachment2Name());
		Util.println("attachment 3 : " + this.getAttachment3Name());*/

	}

	public String getAttachments() {
		return attachments;
	}

	public void setAttachments(String attachments) {
		this.attachments = attachments;
	}
}
