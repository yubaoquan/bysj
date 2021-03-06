package beans;

public class Constant {
	public static final String REGISTER = "RIGISTER";
	public static final String CONFIRM = "CONFIRM";
	public static final String RESET = "RESET";
	
	public static final int USER_AUTHENTICATION = 0;
    public static final int LIST_MAILS = 1;
    public static final int MAIL_DETAIL = 2;
    public static final int DOWNLOAD_ATTACHMENT = 3;
    public static final int SEND_MAIL = 4;
    public static final int EXIT = 5;
    public static final int FIND_USER_NAME = 6;
    public static final int ADD_NEW_USER = 7;
    
    public static final int NULL_INDEX = 0;
    public static final int ONE63_INDEX = 1;
    public static final int QQ_INDEX = 2;
    public static final int BOX_INDEX = 3;
    
    public static final int FOR_INTERNET_SERVER = 0;
	public static final int FOR_LOCAL_SERVER = 1;
	public static final int ATTACHMENTS_CAPACITY = 3;
    
    public static final String LOGIN_SUCCEED = "LOGIN_SUCCEED";
    public static final String LOGIN_FAILED = "LOGIN_FAILED";
    public static final String SERVER_HOSTNAME = "localhost";
    
    public static final int FOUND = 1;
    public static final int NOT_FOUND = 0;
    
    public static final int SUCCEED = 0;
    public static final int FAILED = 1;
    
    public static final int MAIN_SERVER_PORT = 8888;
    public static final int FILE_SERVER_PORT = 8866;
    public static final int POP_3_SERVER_PORT = 110;
    public static final int SMTP_SERVER_PORT = 25;
	
    public static final String LOCAL_ATTACHMENTS_ROOT_PATH_FOR_SERVER = "E:/boxMail/attachments/";
    public static final String LOCAL_ATTACHMENTS_ROOT_PATH_FOR_CLIENT = "E:/boxMail/attachments for client/";
    public static final String INTERNET_ATTACHMENTS_ROOT_PATH_FOR_CLIENT = "E:/boxMail/InternetAttachments/";
	public static final String ATTACHMENTS_SEPARATOR = "\n";
}
