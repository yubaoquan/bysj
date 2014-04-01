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

	//this fields below are used for mail servers on the internet
	private Multipart multipart;
	private int attachmentCounter = 0;
	public final int ATTACHMENTS_CAPACITY = 3;
	private MimeBodyPart[] attachments = new MimeBodyPart[ATTACHMENTS_CAPACITY];
	private String subject;
	private String text;
	private InternetAddress[] receiverAddresses = new InternetAddress[10];

	//the fields below are used for this system's own mail server
	private String sender;
	private String addressee;
	private Timestamp sendTime;
	private String attachment1Name;
	private String attachment2Name;
	private String attachment3Name;
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
		return attachments;
	}

	public void setExtraItems(MimeBodyPart[] extraItems) {
		this.attachments = extraItems;
	}

	public String getText() {
		return text;
	}

	public String getContent() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public InternetAddress[] getReceiverAddresses() {
		return receiverAddresses;
	}

	public void setReceiverAddresses(InternetAddress[] receiverAddresses) {
		this.receiverAddresses = receiverAddresses;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void addExtraItem(File file) {
		if (extraItemsFull()) {
			return;
		} else {
			try {
				attachments[attachmentCounter] = new MimeBodyPart();
				initMIMEBodyPart(attachments[attachmentCounter], file);
				if (attachments[attachmentCounter] == null) {
					System.out.println("null pointer");
				}
				this.attachmentCounter++;
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void initMIMEBodyPart(MimeBodyPart mbp, File file) throws IOException, MessagingException {
		mbp.setText("text");// 没有这一句的话,发送的附件会显示成一堆文本.
		// add--------------
		FileDataSource fds = new FileDataSource(file.getAbsolutePath());
		mbp.setDataHandler(new DataHandler(fds));
		mbp.setFileName(fds.getName());
		// delete--------------
		//mbp.attachFile(file);
		//String encodedFileName = MimeUtility.encodeText(file.getName());
		//mbp.setFileName(encodedFileName);
	}

	public boolean extraItemsFull() {
		return this.attachmentCounter >= ATTACHMENTS_CAPACITY;
	}

	public void removeAllExtraItems() {
		this.attachments = new MimeBodyPart[ATTACHMENTS_CAPACITY];
		this.attachmentCounter = 0;
	}

	public int getAttachmentsAmount() {
		return attachmentCounter;
	}

	public void addAttachmentsToMultipart() {
		for (int i = 0; i < this.attachmentCounter; i++) {
			try {
				multipart.addBodyPart(this.attachments[i]);
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
		return sendTime;
	}

	public void setSendTime(Timestamp sendTime) {
		this.sendTime = sendTime;
	}

	public String getAttachment1Name() {
		return attachment1Name;
	}

	public void setAttachment1Name(String attachment1) {
		this.attachment1Name = attachment1;
	}

	public String getAttachment2Name() {
		return attachment2Name;
	}

	public void setAttachment2Name(String attachment2) {
		this.attachment2Name = attachment2;
	}

	public String getAttachment3Name() {
		return attachment3Name;
	}

	public void setAttachment3Name(String attachment3) {
		this.attachment3Name = attachment3;
	}
	
	public void showPropertiesForLocalServer() {
		Util.println("sender: " + this.getSender());
		Util.println("addressee: " + this.getAddressee());
		Util.println("subject: " + this.getSubject());
		Util.println("sender: " + this.getSender());
		Util.println("content: " + this.getText());
		Util.println("attachment 1 : " + this.getAttachment1Name());
		Util.println("attachment 2 : " + this.getAttachment2Name());
		Util.println("attachment 3 : " + this.getAttachment3Name());
		
	}
}
