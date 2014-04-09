package beans;

import java.io.File;

public class Constant {

	public static final int USER_AUTHENTICATION = 0;
    public static final int LIST_MAILS = 1;
    public static final int MAIL_DETAIL = 2;
    public static final int DOWNLOAD_ATTACHMENT = 3;
    public static final int SEND_MAIL = 4;
    public static final int EXIT = 5;
    
    public static final String LOGIN_SUCCEED = "LOGIN_SUCCEED";
    public static final String LOGIN_FAILED = "LOGIN_FAILED";
    public static final String SERVER_HOSTNAME = "localhost";
    public static final int MAIN_SERVER_PORT = 8888;
    public static final int FILE_SERVER_PORT = 8866;
    public static final String LOCAL_ATTACHMENTS_ROOT_PATH = "E:/boxMail/attachments/";
    public static final String INTERNET_ATTACHMENTS_ROOT_PATH = "E:/boxMail/InternetAttachments/";
	public static final String ATTACHMENTS_SEPARATOR = "\n";
}
