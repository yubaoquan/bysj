package client.net;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import beans.UserLoginBean;
import client.io.MailSaver;


/**
 * 有一封邮件就需要建立一个ReciveMail对象
 */
public class ReceiveMail {
	private MimeMessage mimeMessage = null;
	
	private StringBuffer bodyText = new StringBuffer();// 存放邮件内容
	private String dateFormat = "yy-MM-dd HH:mm"; // 默认的日前显示格式
	private static String filePathPrefix;
	private MailSaver mailSaver = new MailSaver();
	
	public ReceiveMail(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}

	public ReceiveMail() {
		
	}
	
	public void setMimeMessage(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}

	/**
	 * 获得发件人的地址和姓名
	 */
	public String getFrom() throws Exception {
		InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
		String from = address[0].getAddress();
		if (from == null)
			from = "";
		String personal = address[0].getPersonal();
		if (personal == null)
			personal = "";
		String fromaddr = personal + "<" + from + ">";
		return fromaddr;
	}

	/**
	 * 获得邮件的收件人，抄送，和密送的地址和姓名，根据所传递的参数的不同 "to"----收件人 "cc"---抄送人地址 "bcc"---密送人地址
	 */
	public String getMailAddress(String type) throws Exception {
		String mailaddr = "";
		String addtype = type.toUpperCase();
		InternetAddress[] address = null;
		if (addtype.equals("TO") || addtype.equals("CC") || addtype.equals("BCC")) {
			if (addtype.equals("TO")) {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.TO);
			} else if (addtype.equals("CC")) {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.CC);
			} else {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC);
			}
			if (address != null) {
				for (int i = 0; i < address.length; i++) {
					String email = address[i].getAddress();
					if (email == null)
						email = "";
					else {
						email = MimeUtility.decodeText(email);
					}
					String personal = address[i].getPersonal();
					if (personal == null)
						personal = "";
					else {
						personal = MimeUtility.decodeText(personal);
					}
					String compositeto = personal + "<" + email + ">";
					mailaddr += "," + compositeto;
				}
				mailaddr = mailaddr.substring(1);
			}
		} else {
			throw new Exception("Error emailaddr type!");
		}
		return mailaddr;
	}

	/**
	 * 获得邮件主题
	 */
	public String getSubject() throws MessagingException {
		String subject = "";
		try {
			subject = MimeUtility.decodeText(mimeMessage.getSubject());
			if (subject == null)
				subject = "";
		} catch (Exception exce) {
		}
		return subject;
	}

	/**
	 * 获得邮件发送日期
	 */
	public String getSentDate() {
		Date sentdate = null;
		try {
			//如果这个message没有被擦除，确保不抛出
			if (!mimeMessage.isExpunged()) {
				sentdate = mimeMessage.getSentDate();
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		return format.format(sentdate);
	}

	/**
	 * 获得邮件正文内容
	 */
	public String getBodyText() {
		return bodyText.toString();
	}

	/**
	 * 解析邮件，把得到的邮件内容保存到一个StringBuffer对象中，解析邮件 主要是根据MimeType类型的不同执行不同的操作，一步一步的解析
	 */
	public void getMailContent(Part part) throws Exception {
		String contentType = part.getContentType();
		int nameIndex = contentType.indexOf("name");
		boolean conname = false;
		if (nameIndex != -1)
			conname = true;
		System.out.println("CONTENTTYPE: " + contentType);
		
		if (part.isMimeType("text/plain") && !conname) {
			bodyText.append((String) part.getContent());
			
		} else if (part.isMimeType("text/html") && !conname) {
			bodyText.append((String) part.getContent());
			
		} else if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int counts = multipart.getCount();
			
			for (int i = 0; i < counts; i++) {
				getMailContent(multipart.getBodyPart(i));
			}
		} else if (part.isMimeType("message/rfc822")) {
			getMailContent((Part) part.getContent());
		} else {
		}
	}

	/**
	 * 判断此邮件是否需要回执，如果需要回执返回"true",否则返回"false"
	 */
	public boolean getReplySign() throws MessagingException {
		boolean replySign = false;
		String needReply[] = mimeMessage.getHeader("Disposition-Notification-To");
		if (needReply != null) {
			replySign = true;
		}
		return replySign;
	}

	/**
	 * 获得此邮件的Message-ID
	 */
	public String getMessageId() throws MessagingException {
		return mimeMessage.getMessageID();
	}

	/**
	 * 【判断此邮件是否已读，如果未读返回返回false,反之返回true】 pop3不提供flag功能
	 */
	public boolean isOld() throws MessagingException {
		boolean isOld = false;
		Flags flags = ((Message) mimeMessage).getFlags();
		Flags.Flag[] flag = flags.getSystemFlags();
		System.out.println("flags's length: " + flag.length);
		for (int i = 0; i < flag.length; i++) {
			if (flag[i] == Flags.Flag.SEEN) {
				isOld = true;
				System.out.println("seen Message.......");
				break;
			}
		}
		return isOld;
	}

	/**
	 * 【设置日期显示格式】
	 */
	public void setDateFormat(String format) throws Exception {
		this.dateFormat = format;
	}

	/**
	 * 本类入口
	 */
	public void loginAndReceiveMail(UserLoginBean li) throws Exception {
		String smtpServerAddress = li.getSmtpServerName();
		String pop3ServerAddress = li.getPop3ServerName();
		String userName = li.getUserName();
		String password = li.getPassword();
		
		Store store = initStore(smtpServerAddress, pop3ServerAddress, userName, password);
		store.connect();

		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		Message[] messages = folder.getMessages();
		System.out.println("Messages's length: " + messages.length);
		receiveAndSaveMails(messages);
	}

	private void receiveAndSaveMails(Message[] message) throws MessagingException, Exception {
		ReceiveMail pmm = null;
		for (int i = 0; i < message.length; i++) {
			//如果这个message被擦除了，则跳过
			if (message[i].isExpunged()) {
				continue;
			}
			pmm = new ReceiveMail((MimeMessage) message[i]);
			pmm.mailSaver.saveMail(message, pmm, i);
		}
	}

	private static Store initStore(String smtpServerAddress, String pop3ServerAddress, String userName, String password) throws NoSuchProviderException {
		int pop3ServerPort = 110;
		int smtpServerPort = 25;
		Properties props = System.getProperties();
		props.put("mail.smtp.host", smtpServerAddress);
		props.put("mail.smtp.auth", "true");
		Session session = Session.getDefaultInstance(props, null);
		URLName urlName = new URLName("pop3", pop3ServerAddress, pop3ServerPort, null, userName, password);
		setFilePathPrefix("E:\\receive\\" + userName + File.separator);
		Store store = session.getStore(urlName);
		return store;
	}
	
	public static void setFilePathPrefix(String filePathPrefix) {
		ReceiveMail.filePathPrefix = filePathPrefix;
	}

	public static String getFilePathPrefix() {
		return filePathPrefix;
	}

	public MailSaver getMailSaver() {
		return mailSaver;
	}
}