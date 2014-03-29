package client.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;

import client.net.down.ReceiveMail;


public class FileWriterSaver {

	private FileWriter fileWriter = null;
	private BufferedWriter bufferedWriter = null;
	
	public FileWriterSaver(String path) throws Exception{
		File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			fileWriter = new FileWriter(file);
			bufferedWriter = new BufferedWriter(fileWriter);
	}
	
	public void saveMailContents(Message[] message, ReceiveMail receiveMailBean, int i) throws MessagingException, Exception {
		writeLine("subject: " + receiveMailBean.getSubject() + "\n");
		System.out.println(receiveMailBean.getSubject());
		writeLine("sentdate: " + receiveMailBean.getSentDate() + "\n");
		writeLine("form: " + receiveMailBean.getFrom() + "\n");
		writeLine("to: " + receiveMailBean.getMailAddress("to") + "\n");
		receiveMailBean.setDateFormat("yy年MM月dd日 HH:mm");
		writeLine("sentdata: " + receiveMailBean.getSentDate() + "\n");
		receiveMailBean.getMailContent((Part) message[i]);
		writeLine("content: " + receiveMailBean.getBodyText() + "\n");
		
		MailSaver mailSaver = receiveMailBean.getMailSaver();
		mailSaver.saveAttachment((Part) message[i]);
	}
	
	
	public void writeLine(String line) {
		try {
			bufferedWriter.write(line, 0, line.length());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeWriter() {
		try {
			bufferedWriter.close();
			bufferedWriter = null;
			fileWriter.close();
			fileWriter = null;
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
