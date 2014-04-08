package util;

import java.util.ArrayList;
import java.util.List;

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
	
	public static MailBean convertLocalMailToMail(LocalMailBean localMail) {
		MailBean mail = new MailBean();
		mail.setSender(localMail.getSender());
		mail.setAddressee(localMail.getAddressee());
		mail.setSendTime(localMail.getSendTime());
		
		mail.setSubject(localMail.getSubject());
		mail.setContent(localMail.getContent());
		mail.setAttachments(localMail.getAttachments());
		return mail;
	}
	
	public static List<MailBean> convertLocalMaisToMails(List<LocalMailBean> localMails) {
		List<MailBean> mails = new ArrayList<>();
		for (LocalMailBean lmb : localMails) {
			MailBean mb = convertLocalMailToMail(lmb);
			mails.add(mb);
		}
		return mails;
	}
}
