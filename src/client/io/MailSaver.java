package client.io;

import java.io.File;
import java.io.InputStream;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeUtility;

import client.net.down.ReceiveMail;


public class MailSaver {

	private FileStreamSaver fileStreamSaver;
	private FileWriterSaver fileWriterSaver;
	private String attachmentPath = ""; // 附件下载后的存放目录
	
	/**
	 * 【真正的保存附件到指定目录里】
	 */
	public void saveFile(String fileName, InputStream in, String rootFolderName) throws Exception {
		String osName = System.getProperty("os.name");
		if (osName == null)
			osName = "";
		if (osName.toLowerCase().indexOf("win") != -1) {
			if (rootFolderName == null || rootFolderName.equals(""))
				rootFolderName = "E:\\receive";
		} else {
			rootFolderName = "/tmp";
		}
		makeFolderForMail(rootFolderName);
		String storeFilePath = rootFolderName + File.separator + fileName;
		fileStreamSaver = new FileStreamSaver(storeFilePath,in);
		System.out.println("storefile's path: " + storeFilePath.toString());
		try {
			fileStreamSaver.storeFile();
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new Exception("文件保存失败!");
		} finally {
			fileStreamSaver.closeStream();
		}
	}
	
	public void saveMail(Message[] message, ReceiveMail receiveMailBean, int i) throws MessagingException, Exception {
		String subjectName = getSuitableLengthSubjectName(receiveMailBean);
		String singleMailFolderPath = getSingleMailFolderPath(receiveMailBean);
		makeFolderForMail(singleMailFolderPath);
		String filePath = singleMailFolderPath + File.separator + subjectName + ".txt";
		setAttachPath(singleMailFolderPath);
		System.out.println("set attachpath:" + singleMailFolderPath); 
		System.out.println(filePath);
		saveMailByWriter(message, receiveMailBean, i, filePath);
	}
	
	private String getSingleMailFolderPath(ReceiveMail receiveMailBean) throws Exception {
		String subjectName = getSuitableLengthSubjectName(receiveMailBean);
		String singleMailFolderPath = ReceiveMail.getFilePathPrefix() + subjectName;
		return singleMailFolderPath;
	}
	
	private static String getSuitableLengthSubjectName(ReceiveMail receiveMailBean) throws MessagingException {
		String subjectName = receiveMailBean.getSubject();
		int subjectNameLength = 20;
		subjectName = replaceIllegalCharacters(subjectName);
		subjectName = subjectName.length() < subjectNameLength ? subjectName : subjectName.substring(0, subjectNameLength);
		return subjectName;
	}
	
	private static String replaceIllegalCharacters(String subjectName) {
		char[] illegalCharacters = { ':', '/', '\\', '?', '*', '<', '>', '|', '\"',' ' };
		for (char ch : illegalCharacters) {
			subjectName = subjectName.replace(ch, '_');
		}
		return subjectName;
	}
	
	private void makeFolderForMail(String folderPath) {
		File folder = new File(folderPath);
		folder.mkdirs();
	}
	
	public void saveMailByWriter(Message[] message, ReceiveMail receiveMailBean, int i, String filePath) throws Exception, MessagingException {
		fileWriterSaver = new FileWriterSaver(filePath);
		fileWriterSaver.saveMailContents(message, receiveMailBean, i);
		fileWriterSaver.closeWriter();
	}
	
	/**
	 * 【保存附件】
	 */
	public void saveAttachment(Part part) throws Exception {
		System.out.println("saving attach... attachpath:" + getAttachPath());
		String fileName = "";
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {
					fileName = mpart.getFileName();
					if (fileName.toLowerCase().indexOf("gb2312") != -1 || fileName.toLowerCase().indexOf("gb18030") != -1|| fileName.toLowerCase().indexOf("gbk") != -1) {
						fileName = MimeUtility.decodeText(fileName);
					}
					fileName = "附件" + fileName;
					fileName = replaceIllegalCharacters(fileName);
					saveFile(fileName, mpart.getInputStream(),getAttachPath());
				} else if (mpart.isMimeType("multipart/*")) {
					saveAttachment(mpart);
				} else {
					fileName = mpart.getFileName();
					if ((fileName != null) && (fileName.toLowerCase().indexOf("GB2312") != -1)) {
						fileName = MimeUtility.decodeText(fileName);
						fileName = "附件" + fileName;
						fileName = replaceIllegalCharacters(fileName);
						saveFile(fileName, mpart.getInputStream(),getAttachPath());
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			saveAttachment((Part) part.getContent());
		}
	}
	
	/**
	 * 【设置附件存放路径】
	 */
	public void setAttachPath(String attachpath) {
		this.attachmentPath = attachpath;
	}
	
	/**
	 * 【获得附件存放路径】
	 */
	public String getAttachPath() {
		return attachmentPath;
	}
}
