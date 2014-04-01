package server.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import util.Util;
import beans.MailBean;

public class DAO {

	ResultSet rs = null;
	Connection conn = null;

	public DAO() {
		connect();
	}

	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/bysj?user=root&password=root");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void insertMailIntoMailbox(MailBean mailBean) {
		// TODO Auto-generated method stub
	}

	public void insertNewUser(String username, String password) {
		String sql = "insert into user (username, password) values (?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(sql);){
			stmt.setString(1,username);
			stmt.setString(2, password);
			stmt.executeUpdate();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public Map<Integer, String> listMails(String addressee) {
		Map<Integer, String> mails = new HashMap<>();
		String sql = "select id, subject from mail where addressee = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql);){
			stmt.setString(1, addressee);
			rs = stmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				String subject = rs.getString("subject");
				mails.put(id, subject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mails;
	}

	public int countMails(String username) {
		int count = 0;
		String sql = "select count(1) from mail";
		try (Statement stmt = conn.createStatement()) {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	public MailBean findMailDetail(int id) {
		MailBean result = new MailBean();
		String sql = "select * from mail where id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				result.setSender(rs.getString("sender"));
				result.setAddressee(rs.getString("addressee"));
				result.setSubject(rs.getString("subject"));
				result.setText(rs.getString("content"));
				result.setSendTime(rs.getTimestamp("sendtime"));
				result.setAttachment1Name(rs.getString("attachment_1"));
				result.setAttachment2Name(rs.getString("attachment_3"));
				result.setAttachment2Name(rs.getString("attachment_3"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return result;
	}

	public String findAttachmentPositionByID(int id) {
		String result = null;
		String sql = "select position from attachment where id = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql);){
			stmt.setInt(1, id);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String findAttachmentPositionByOffset(int mailID, int offset) {
		String result = null;
		String sql = "select position from attachment where mail_id = ? and offset = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1,mailID);
			stmt.setInt(2, offset);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean userExists(String username) {
		String sql = "select count(1) from user where username = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setString(1, username);
			rs = stmt.executeQuery();
			rs.next();
			if (rs.getInt(1) > 0) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean usernameAndPasswordExists(String username, String password) {
		String sql = "select password from user where username = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql);){
			stmt.setString(1, username);
			rs = stmt.executeQuery();
			if (rs.next()) {
				String realPassword = rs.getString(1);
				if (realPassword.equals(password)) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void main(String[] args) {
		DAO dao = new DAO();
		/*List<String> result = dao.listMailSubjects("admin");
		for (String temp : result) {
			System.out.println(temp);
		}
		String position = dao.findAttachmentPositionByOffset(1,0);
	
		System.out.println(position);*/
		//dao.insertNewUser("abc", "def");
		/*System.out.println(dao.userExists("abc") + " " + dao.usernameAndPasswordExists("abc", "def"));
		int count = dao.countMails("admin");
		Util.println("admin has " + count + " mails");
		HashMap<Integer, String> result = (HashMap<Integer, String>) dao.listMails("admin");
		for (Map.Entry<Integer, String> m : result.entrySet()) {
			Util.println("id: " + m.getKey() + " subject: " + m.getValue());
		}*/
		MailBean mb = dao.findMailDetail(1);
		mb.showPropertiesForLocalServer();
	}

}
