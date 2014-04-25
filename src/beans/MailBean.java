package beans;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import util.Util;

public class MailBean implements Serializable, LabelBean{

	private static final long serialVersionUID = 1L;
	
	//common fields 
	private int attachmentCounter = 0;
	private String subject;
	private String text;
	
	// the fields below are used for mail servers on the Internet
	private Multipart multipart;
	private MimeBodyPart[] attachmentsForInternetServer = new MimeBodyPart[Constant.ATTACHMENTS_CAPACITY];
	private InternetAddress[] internetAddressees = new InternetAddress[10];
	private ArrayList<AttachmentBean> attachmentBeans;

	// the fields below are used for this system's own mail server
	private String sender;
	private String addressee;
	private Timestamp sendTime;
	private File[] attachmentsForLocalServer = new File[Constant.ATTACHMENTS_CAPACITY];
	private String attachmentNames;
	private String attachmentLocations;
	private int id;

	public MailBean() {
		this.multipart = new MimeMultipart();
	}
	public void addAttachment(File attachment, int mailType) {
		if (attachmentsAreFull()) {
			return;
		}
		if (mailType == Constant.FOR_INTERNET_SERVER) {
			addAttachmentForInternetServer(attachment);
		} else if (mailType == Constant.FOR_LOCAL_SERVER) {
			addAttachmentForLocalServer(attachment);
		}
		this.attachmentCounter++;
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

	public void addAttachmentsToMultipart() {
		for (int i = 0; i < attachmentCounter; i++) {
			try {
				multipart.addBodyPart(attachmentsForInternetServer[i]);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean attachmentsAreFull() {
		return this.attachmentCounter >= Constant.ATTACHMENTS_CAPACITY;
	}

	public String getAddressee() {
		return addressee;
	}

	public File getAttachment(int offset) {
		return attachmentsForLocalServer[offset];
	}

	public int getAttachmentAmount() {
		return attachmentCounter;
	}

	public ArrayList<AttachmentBean> getAttachmentBeans() {
		return attachmentBeans;
	}

	public String getAttachmentLocations() {
		return attachmentLocations;
	}

	public String getAttachmentNames() {
		return attachmentNames;
	}

	public String getContent() {
		return text;
	}

	public MimeBodyPart[] getExtraItems() {
		return attachmentsForInternetServer;
	}
	
	@Override
	public int getID() {
		return id;
	}

	public InternetAddress[] getInternetAddressees() {
		return internetAddressees;
	}

	public Multipart getMutipart() {
		return multipart;
	}

	public String getSender() {
		return sender;
	}

	public Timestamp getSendTime() {
		return sendTime;
	}

	@Override
	public String getSubject() {
		return subject;
	}

	public String getText() {
		return text;
	}
	
	private void initMIMEBodyPart(MimeBodyPart mbp, File file) throws IOException, MessagingException {
		mbp.setText("text");// 没有这一句的话,发送的附件会显示成一堆文本.
		FileDataSource fds = new FileDataSource(file.getAbsolutePath());
		mbp.setDataHandler(new DataHandler(fds));
		mbp.setFileName(fds.getName());
	}

	public void removeAllExtraItems() {
		attachmentsForInternetServer = new MimeBodyPart[Constant.ATTACHMENTS_CAPACITY];
		attachmentsForLocalServer = new File[Constant.ATTACHMENTS_CAPACITY];
		attachmentCounter = 0;
	}
	
	public void setAddressee(String addressee) {
		this.addressee = addressee;
	}

	public void setAttachmentAmount(int num) {
		attachmentCounter = num;
	}

	public void setAttachmentBeans(ArrayList<AttachmentBean> attachmentBeans) {
		this.attachmentBeans = attachmentBeans;
	}

	public void setAttachmentLocations(String attachmentLocations) {
		this.attachmentLocations = attachmentLocations;
	}
	
	public void setAttachmentNames(String names) {
		this.attachmentNames = names;
	}

	public void setContent(String content) {
		setText(content);
	}

	public void setExtraItems(MimeBodyPart[] extraItems) {
		this.attachmentsForInternetServer = extraItems;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setInternetAddressees(InternetAddress[] receiverAddresses) {
		this.internetAddressees = receiverAddresses;
	}

	public void setMutipary(Multipart mutipary) {
		this.multipart = mutipary;
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
