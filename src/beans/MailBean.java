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

public class MailBean {

	//this fields below are used for mail servers on the internet
	private Multipart multipart;
	private int extraItemCounter = 0;
	public final int EXTRA_ITEM_CAPACITY = 3;
	private MimeBodyPart[] extraItems = new MimeBodyPart[EXTRA_ITEM_CAPACITY];
	private String subject;
	private String text;
	private InternetAddress[] receiverAddresses = new InternetAddress[10];

	//the fields below are used for this system's own mail server
	private String sender;
	private String addressee;
	private Timestamp sendTime;
	
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
		return extraItems;
	}

	public void setExtraItems(MimeBodyPart[] extraItems) {
		this.extraItems = extraItems;
	}

	public String getText() {
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
				extraItems[extraItemCounter] = new MimeBodyPart();
				initMIMEBodyPart(extraItems[extraItemCounter], file);
				if (extraItems[extraItemCounter] == null) {
					System.out.println("null pointer");
				}
				this.extraItemCounter++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
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
		return this.extraItemCounter >= EXTRA_ITEM_CAPACITY;
	}

	public void removeAllExtraItems() {
		this.extraItems = new MimeBodyPart[EXTRA_ITEM_CAPACITY];
		this.extraItemCounter = 0;
	}

	public int getExtraItemsAmount() {
		return extraItemCounter;
	}

	public void addExtraItemsToMultipart() {
		for (int i = 0; i < this.extraItemCounter; i++) {
			try {
				multipart.addBodyPart(this.extraItems[i]);
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
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
}
