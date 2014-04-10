package util;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import client.UI.LoginUI;
import client.UI.MainTargetSelectingUI;
import client.net.down.ReceiveMail;
import client.net.up.Transmitter;
import beans.AttachmentBean;
import beans.LocalMailBean;
import beans.MailBean;
import beans.UserBean;

public class Util {
	public static void println(Object obj) {
		System.out.println(obj);
		return;
	}

	public static void print(Object obj) {
		System.out.print(obj);
	}

	public static String getShortStringWithEllipsis(String input, int limit) {
		int ellipsisLength = 3;
		int limitWithoutEllipsisLength = limit - ellipsisLength;
		int preLength = input.length();

		if (preLength > limitWithoutEllipsisLength) {
			input = input.substring(0, limitWithoutEllipsisLength) + "...";
		}
		return input;
	}

	public static List<MailBean> convertLocalMaisToMails(List<LocalMailBean> localMails, ReceiveMail receiveMail) {
		List<MailBean> mails = new ArrayList<>();
		for (LocalMailBean lmb : localMails) {
			MailBean mb = convertLocalMailToMail(lmb, receiveMail);
			mails.add(mb);
		}
		return mails;
	}

	public static MailBean convertLocalMailToMail(LocalMailBean localMail, ReceiveMail receiveMail) {
		MailBean mail = new MailBean();
		mail.setId(localMail.getID());
		mail.setSender(localMail.getSender());
		mail.setAddressee(localMail.getAddressee());
		mail.setSendTime(localMail.getSendTime());

		mail.setSubject(localMail.getSubject());
		mail.setContent(localMail.getContent());

		if (localMail.getAttachmentNames() != null) {
			mail.setAttachmentNames(localMail.getAttachmentNames());
			String[] attachmentNameArray = localMail.getAttachmentNames().split("\n");
			//String[] attachmentLocationArray = localMail.getAttachmentLocations().split("\n");
			mail.setAttachmentAmount(attachmentNameArray.length);
			ArrayList<AttachmentBean> attachments = new ArrayList<>();
			for (int i = 0; i < attachmentNameArray.length; i++) {
				AttachmentBean ab = new AttachmentBean();
				ab.setMailID(localMail.getID());
				ab.setOffset(i);
				ab.setOwner(localMail.getAddressee());
				ab.setTitle(attachmentNameArray[i]);
				ab.setReceiveMail(receiveMail);
				attachments.add(ab);
			}
			mail.setAttachmentBeans(attachments);
		}

		return mail;
	}

	public static String replaceIllegalCharacters(String subjectName) {
		char[] illegalCharacters = { ':', '/', '\\', '?', '*', '<', '>', '|', '\"', ' ', ',' };
		for (char ch : illegalCharacters) {
			subjectName = subjectName.replace(ch, '_');
		}
		return subjectName;
	}

	public static String cutStringIfTooLong(String subject, int limit) throws MessagingException {
		String subjectName = subject;
		int subjectNameLength = limit;
		subjectName = Util.replaceIllegalCharacters(subjectName);
		subjectName = subjectName.length() < subjectNameLength ? subjectName : subjectName.substring(0, subjectNameLength);
		return subjectName;
	}
	

	public static boolean loginInformationValid(UserBean loginInformation) {
		if (loginInformation == null) {
			System.out.println("loginInformation == null");
			return false;
		}
		if (loginInformation.getUserName().trim().equals("")) {
			return false;
		}
		if (loginInformation.getPassword().trim().equals("")) {
			return false;
		}
		return true;
	}

	public static void selectSendOrReceive(LoginUI parent) {
		parent.dispose();
		UserBean user = parent.getLoginInformation();
		Transmitter tra = parent.getTransmitter();
		new MainTargetSelectingUI(user, tra).initUI();
	}

}
