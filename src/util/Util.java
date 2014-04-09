package util;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import beans.AttachmentBean;
import beans.LocalMailBean;
import beans.MailBean;

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
	
	
	public static List<MailBean> convertLocalMaisToMails(List<LocalMailBean> localMails) {
		List<MailBean> mails = new ArrayList<>();
		for (LocalMailBean lmb : localMails) {
			MailBean mb = convertLocalMailToMail(lmb);
			mails.add(mb);
		}
		return mails;
	}
	

	public static MailBean convertLocalMailToMail(LocalMailBean localMail) {
		MailBean mail = new MailBean();
		mail.setId(localMail.getId());
		mail.setSender(localMail.getSender());
		mail.setAddressee(localMail.getAddressee());
		mail.setSendTime(localMail.getSendTime());
		
		mail.setSubject(localMail.getSubject());
		mail.setContent(localMail.getContent());
		mail.setAttachmentNames(localMail.getAttachments());
		String[] attachmentNameArray = localMail.getAttachments().split("\n");
		if (attachmentNameArray.length > 0) {
			ArrayList<AttachmentBean> attachments = new ArrayList<>();
			for (int i = 0; i < attachmentNameArray.length; i ++) {
				AttachmentBean ab = new AttachmentBean();
				ab.setOffset(i);
				ab.setTitle(attachmentNameArray[i]);
				attachments.add(ab);
			}
			mail.setAttachmentBeans(attachments);
		}
		return mail;
	}
	
	public static String replaceIllegalCharacters(String subjectName) {
		char[] illegalCharacters = { ':', '/', '\\', '?', '*', '<', '>', '|', '\"',' ',',' };
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
}
