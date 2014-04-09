package client.io;

import java.io.File;
import java.io.InputStream;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeUtility;

import util.Util;
import client.net.down.ReceiveMail;


public class MailSaver {

	private FileStreamSaver fileStreamSaver;
	private FileWriterSaver fileWriterSaver;
	private String attachmentFolder = ""; // 附件下载后的存放目录
	
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
		String subjectName = Util.cutStringIfTooLong(receiveMailBean.getSubject(), 20);
		String singleMailFolderPath = getSingleMailFolderLocation(receiveMailBean);
		makeFolderForMail(singleMailFolderPath);
		String filePath = singleMailFolderPath + File.separator + subjectName + ".txt";
		setAttachFolder(singleMailFolderPath);
		System.out.println("set attachpath:" + singleMailFolderPath); 
		System.out.println(filePath);
		saveMailByWriter(message, receiveMailBean, i, filePath);
	}
	
	private String getSingleMailFolderLocation(ReceiveMail receiveMailBean) throws Exception {
		String subjectName = Util.cutStringIfTooLong(receiveMailBean.getSubject(), 20);
		String singleMailFolderPath = ReceiveMail.getAttachmentFolderPath() + subjectName;
		return singleMailFolderPath;
	}
	
	
	private void makeFolderForMail(String folderPath) {
		File folder = new File(folderPath);
		folder.mkdirs();
	}
	
	public void saveMailByWriter(Message[] messages, ReceiveMail receiveMailBean, int i, String filePath) throws Exception, MessagingException {
		fileWriterSaver = new FileWriterSaver(filePath);
		fileWriterSaver.saveMailToFile(messages, receiveMailBean, i);
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
				BodyPart bodyPart = mp.getBodyPart(i);
				String disposition = bodyPart.getDisposition();
				if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {
					fileName = bodyPart.getFileName();
					if (fileName.toLowerCase().indexOf("gb2312") != -1 || fileName.toLowerCase().indexOf("gb18030") != -1|| fileName.toLowerCase().indexOf("gbk") != -1) {
						fileName = MimeUtility.decodeText(fileName);
					}
					fileName = "附件" + fileName;
					fileName = Util.replaceIllegalCharacters(fileName);
					saveFile(fileName, bodyPart.getInputStream(),getAttachPath());
				} else if (bodyPart.isMimeType("multipart/*")) {
					saveAttachment(bodyPart);
				} else {
					fileName = bodyPart.getFileName();
					if ((fileName != null) && (fileName.toLowerCase().indexOf("gb2312") != -1)) {
						fileName = MimeUtility.decodeText(fileName);
						fileName = "附件" + fileName;
						fileName = Util.replaceIllegalCharacters(fileName);
						saveFile(fileName, bodyPart.getInputStream(),getAttachPath());
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
	public void setAttachFolder(String attachpath) {
		this.attachmentFolder = attachpath;
	}
	
	/**
	 * 【获得附件存放路径】
	 */
	public String getAttachPath() {
		return attachmentFolder;
	}
}
