package client.net.down;

import static java.lang.System.getProperties;
import static java.lang.System.out;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
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
import javax.swing.JOptionPane;

import util.Util;
import beans.AttachmentBean;
import beans.Constant;
import beans.LocalMailBean;
import beans.MailBean;
import beans.UserBean;
import client.UI.ItemListUI;
import client.io.FileStreamSaver;
import client.net.up.Transmitter;

public class ReceiveMail {
	private MimeMessage mimeMessage = null;

	private StringBuffer bodyText = new StringBuffer();// 存放邮件内容
	private String dateFormat = "yyyy-MM-dd hh:mm:ss"; // 默认的日前显示格式
	private static String filePathPrefix;
	private Transmitter transmitter;
	private ObjectInputStream ois;
	private Socket socket;

	public ReceiveMail(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
		try {
			this.getMailContent(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ReceiveMail() {

	}

	public void setMimeMessage(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}

	public String getFrom() throws Exception {
		InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
		String from = address[0].getAddress();
		if (from == null)
			from = "";
		String personal = address[0].getPersonal();
		if (personal == null)
			personal = "";
		return personal + "<" + from + ">";
	}

	/**
	 * 获得邮件的收件人，抄送，和密送的地址和姓名，根据所传递的参数的不同 "to"----收件人 "cc"---抄送人地址 "bcc"---密送人地址
	 */
	public String getMailAddress(String type) throws Exception {
		String mailAddress = "";
		String sendType = type.toUpperCase();
		InternetAddress[] address = null;
		if (sendType.equals("TO") || sendType.equals("CC") || sendType.equals("BCC")) {
			switch (sendType) {
				case "TO":
					address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.TO);
					break;
				case "CC":
					address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.CC);
					break;
				default:
					address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC);
					break;
			}
			if (address != null) {
				for (InternetAddress tempAddress : address) {
					String email = tempAddress.getAddress();
					if (email == null)
						email = "";
					else {
						email = MimeUtility.decodeText(email);
					}
					String personal = tempAddress.getPersonal();
					if (personal == null)
						personal = "";
					else {
						personal = MimeUtility.decodeText(personal);
					}
					String compositeto = personal + "<" + email + ">";
					mailAddress += "," + compositeto;
				}
				mailAddress = mailAddress.substring(1);
			}
		} else {
			throw new Exception("Error email address type!");
		}
		return mailAddress;
	}

	public String getSubject() throws MessagingException {
		String subject = "";
		try {
			subject = MimeUtility.decodeText(mimeMessage.getSubject());
			if (subject == null)
				subject = "";
		} catch (Exception exce) {
			exce.printStackTrace();
		}
		return subject;
	}

	public String getSentDate() {
		Date sentdate = null;
		try {
			// 如果这个message没有被擦除，确保不抛出
			if (!mimeMessage.isExpunged()) {
				sentdate = mimeMessage.getSentDate();
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		return format.format(sentdate);
	}

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
		out.println("CONTENTTYPE: " + contentType);

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
		Flags flags = mimeMessage.getFlags();
		Flags.Flag[] flag = flags.getSystemFlags();
		out.println("flags' length: " + flag.length);
		for (Flags.Flag aFlag : flag) {
			if (aFlag == Flags.Flag.SEEN) {
				isOld = true;
				out.println("seen Message.......");
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
	public void loginAndReceiveMail(UserBean user, Transmitter tra) throws Exception {
		transmitter = tra;
		if (user.isLocalServerEnabled()) {
			setAttachmentFolderPath(Constant.LOCAL_ATTACHMENTS_ROOT_PATH_FOR_CLIENT + user.getUserName() + File.separator);
			receiveLocalMail(user);
		} else {
			setAttachmentFolderPath(Constant.INTERNET_ATTACHMENTS_ROOT_PATH_FOR_CLIENT + user.getUserName() + File.separator);
			receiveInternetMail(user);
		}

	}

	private void receiveLocalMail(UserBean user) {
		transmitter.sendRequest(Constant.LIST_MAILS + " ");
		try {
			socket = new Socket("127.0.0.1", Constant.FILE_SERVER_PORT);
			ois = new ObjectInputStream(socket.getInputStream());
			@SuppressWarnings("unchecked")
			ArrayList<LocalMailBean> localMails = (ArrayList<LocalMailBean>) ois.readObject();
			ArrayList<MailBean> mails = (ArrayList<MailBean>) Util.convertLocalMaisToMails(localMails, this);
			new ItemListUI(mails, this).launch();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ois.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		out.println("local receive mail");
	}

	private void receiveInternetMail(UserBean user) throws NoSuchProviderException, MessagingException, Exception {
		String smtpServerAddress = user.getSmtpServerName();
		String pop3ServerAddress = user.getPop3ServerName();
		String userName = user.getUserName();
		String password = user.getPassword();

		Store store = initStore(smtpServerAddress, pop3ServerAddress, userName, password);
		store.connect();

		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		Message[] messages = folder.getMessages();
		out.println("Messages' length: " + messages.length);
		List<MailBean> mails = receiveAndSaveAsMailBeans(messages);
		new ItemListUI(mails, this);
	}

	private List<MailBean> receiveAndSaveAsMailBeans(Message[] messages) throws Exception {
		ReceiveMail pmm = null;
		List<MailBean> mails = new ArrayList<>();
		int mailID = 0;
		for (Message message : messages) {
			// 如果这个message被擦除了，则跳过
			if (!message.isExpunged()) {
				pmm = new ReceiveMail((MimeMessage) message);
				MailBean mail = new MailBean();
				mail.setId(mailID);
				mailID++;
				fillMailBean(pmm, mail);
				mails.add(mail);
			}
		}
		return mails;
	}

	private void fillMailBean(ReceiveMail pmm, MailBean mail) throws Exception {
		mail.setSender(pmm.getFrom());
		mail.setAddressee(pmm.getMailAddress("to"));
		mail.setSubject(pmm.getSubject());
		mail.setText(pmm.getBodyText());
		mail.setSendTime(Timestamp.valueOf(pmm.getSentDate()));
		ArrayList<AttachmentBean> attachments = collectAttachmentBeans(pmm.mimeMessage);
		mail.setAttachmentBeans(attachments);
		mail.setAttachmentAmount(attachments.size());
		StringBuffer attachmentNames = new StringBuffer();
		for (AttachmentBean ab : attachments) {
			attachmentNames.append(ab.getSubject());
		}
		String attachmentNameString = Util.getShortStringWithEllipsis(attachmentNames.toString(), 20);
		mail.setAttachmentNames(attachmentNameString);
	}

	public ArrayList<AttachmentBean> collectAttachmentBeans(Part part) throws Exception {
		ArrayList<AttachmentBean> attachments = new ArrayList<>();
		String attachmentName = "";
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart bodyPart = mp.getBodyPart(i);
				String disposition = bodyPart.getDisposition();
				if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {
					attachmentName = bodyPart.getFileName();
					if (attachmentName.toLowerCase().indexOf("gb2312") != -1 || attachmentName.toLowerCase().indexOf("gb18030") != -1 || attachmentName.toLowerCase().indexOf("gbk") != -1) {
						attachmentName = MimeUtility.decodeText(attachmentName);
					}
					attachments.add(new AttachmentBean(i, attachmentName, bodyPart, this));
				} else if (bodyPart.isMimeType("multipart/*")) {
					collectAttachmentBeans(bodyPart);
				} else {
					attachmentName = bodyPart.getFileName();
					if ((attachmentName != null) && (attachmentName.toLowerCase().indexOf("gb2312") != -1)) {
						attachmentName = MimeUtility.decodeText(attachmentName);
						attachments.add(new AttachmentBean(i, attachmentName, bodyPart, this));
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			collectAttachmentBeans((Part) part.getContent());
		}
		return attachments;
	}

	private static Store initStore(String smtpServerAddress, String pop3ServerAddress, String userName, String password) throws NoSuchProviderException {
		Properties props = getProperties();
		props.put("mail.smtp.host", smtpServerAddress);
		props.put("mail.smtp.auth", "true");
		Session session = Session.getDefaultInstance(props, null);
		URLName urlName = new URLName("pop3", pop3ServerAddress, Constant.POP_3_SERVER_PORT, null, userName, password);
		return session.getStore(urlName);
	}

	public static void setAttachmentFolderPath(String filePathPrefix) {
		ReceiveMail.filePathPrefix = filePathPrefix;
		File folder = new File(filePathPrefix);
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}

	public void downloadAttachment(AttachmentBean attachment, ItemListUI itemListUI) {
		out.println("download attachment");
		String fileName = Util.replaceIllegalCharacters(attachment.getSubject());
		String folderPath = filePathPrefix;
		String filePath = folderPath + File.separator + fileName;
		File file = new File(filePath);
		InputStream is = null;
		try {
			file.createNewFile();
			if (attachment.getMailID() != -1) {// From local mail.
				is = prepareForLocalAttachment(attachment);
			} else {// From Internet mail.
				is = attachment.getPart().getInputStream();
			}
			FileStreamSaver fileStreamSaver = new FileStreamSaver(file, is);
			fileStreamSaver.storeFile();
			fileStreamSaver.closeStream();
			JOptionPane.showMessageDialog(itemListUI, (String) "附件下载完成.文件位置： " + filePath + ".", "完成", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private InputStream prepareForLocalAttachment(AttachmentBean attachment) throws UnknownHostException, IOException {
		InputStream is;
		UserBean owner = new UserBean();
		owner.setUserName(attachment.getOwner());
		int mailID = attachment.getMailID();
		int offset = attachment.getOffset();
		StringBuffer request = new StringBuffer();
		request.append(Constant.DOWNLOAD_ATTACHMENT).append(" ");
		request.append(mailID).append(" ");
		request.append(offset);
		out.println("request: " + request.toString());
		transmitter.sendRequest(request.toString());
		socket = new Socket("127.0.0.1", Constant.FILE_SERVER_PORT);
		is = socket.getInputStream();
		return is;
	}
}