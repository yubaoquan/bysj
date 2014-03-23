package beans;

public class UserLoginBean {

	private String pop3ServerName;
	private String smtpServerName;
	private String userName;
	private String password;
	
	public UserLoginBean() {}
	
	public UserLoginBean(String serverName, String userName, String password) {
		this.smtpServerName = serverName;
		this.userName = userName;
		this.password = password;
	}
	public String getSmtpServerName() {
		return smtpServerName;
	}
	public String getUserName() {
		return userName;
	}
	public String getPassword() {
		return password;
	}
	public void setSmtpServerName(String serverName) {
		this.smtpServerName = serverName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getPop3ServerName() {
		return pop3ServerName;
	}

	public void setPop3ServerName(String pop3ServerName) {
		this.pop3ServerName = pop3ServerName;
	}
	
}
