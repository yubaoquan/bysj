package client.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileStreamSaver {

	private BufferedOutputStream bos = null;
	private BufferedInputStream bis = null;

	public FileStreamSaver(String path, InputStream in) throws Exception {
		File storeFile = new File(path);
		bos = new BufferedOutputStream(new FileOutputStream(storeFile));
		bis = new BufferedInputStream(in);
		
	}
	
	public FileStreamSaver(File file, InputStream is) throws Exception{
		bos = new BufferedOutputStream(new FileOutputStream(file));
		bis = new BufferedInputStream(is);
	}
	
	public void storeFile() throws Exception {
		try {
			int c;
			while ((c = bis.read()) != -1) {
				bos.write(c);
				bos.flush();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new Exception("文件保存失败!");
		}
	}
	
	public void closeStream() {
		try {
			bos.close();
			bos = null;
			bis.close();
			bis = null;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
