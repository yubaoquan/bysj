package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import beans.MailBean;

public class DAO {

	ResultSet rs = null;
	PreparedStatement stmt = null;
	Connection conn = null;
	
	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/bysj?user=root&password=root");
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void insertMailIntoMailbox(MailBean mailBean) {
		
	}
	
	public void insertNewUser(String username, String password) {
		
	}
	
	public List<MailBean> listMails(String username) {
		return null;
	}
	
	public int countMails(String username) {
		return 0;
	}
	
	public MailBean findMailDetail(int id) {
		return null;
	}
	
	public String findAttachmentPosition(int id) {
		return null;
	}
	
	public String findAttachmentPosition(int mailId, int offset) {
		return null;
	}
	
	public boolean userExists(String username) {
		String countUserByUsername = "select count(1) from user where username = ?";
		try {
			stmt = conn.prepareStatement(countUserByUsername);
			stmt.setString(1, username);
			rs = stmt.executeQuery();
			rs.next();
			if(rs.getInt(1) > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean usernameAndPasswordValid(String username, String password) {
		String findPasswordByUsername = "select password from user where username = ?";
		try {
			stmt = conn.prepareStatement(findPasswordByUsername);
			stmt.setString(1, username);
			rs = stmt.executeQuery();
			if(rs.next()) {
				String realPassword = rs.getString(1);
				if(realPassword.equals(password)) {
					return true;
				}
			} 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
}
