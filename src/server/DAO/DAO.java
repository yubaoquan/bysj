package server.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import beans.AttachmentBean;
import beans.LocalMailBean;
import beans.MailBean;

public class DAO {

	public static void main(String[] args) {
		DAO dao = new DAO();
		
		MailBean mb = new MailBean();
		mb.setSender("admin");
		mb.setAddressee("receiver");
		//mb.setSentTime(new Timestamp(System.currentTimeMillis()));
		mb.setSubject("from JDBC");
		mb.setText("This is from JDBC");
	/*	mb.setAttachment1Name("attchment1");
		mb.setAttachment2Name("attchment2");
		mb.setAttachment3Name("attchment3");*/
		dao.insertMailIntoMailbox(mb);
		mb.showPropertiesForLocalServer();
		System.out.println(dao.userExists("admin"));
		System.out.println(dao.userExists("admin2"));
	}
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

	public LocalMailBean findAttachment(int id) {
		//String sql = ""
		return null;
	}
	
	public String findAttachmentLocationByOffset(int mailID, int offset) {
		String locations = null;
		String resultLocation = null;
		String sql = "select attachment_location from mail where id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setInt(1,mailID);
			rs = stmt.executeQuery();
			if (rs.next()) {
				locations = rs.getString(1);
			}
			String[] locationArray = locations.split("\n");
			resultLocation = locationArray[offset];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultLocation;
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void insertAttachment(AttachmentBean ab) {
		//TODO
		String sql = "insert into attachment (location, mail_id, offset) select ?, max(id) + 1, ? from mail";
		try (PreparedStatement stmt = conn.prepareStatement(sql);){
			stmt.setString(1, ab.getLocation());
			stmt.setInt(2, ab.getOffset());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insertMailIntoMailbox(MailBean mail) {
		String sql = "insert into mail (sender, addressee, sendtime, subject, content, attachment_name, attachment_location) "
				+ "values (?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = conn.prepareStatement(sql);){
			stmt.setString(1, mail.getSender());
			stmt.setString(2, mail.getAddressee());
			stmt.setTimestamp(3, mail.getSendTime());
			stmt.setString(4, mail.getSubject());
			stmt.setString(5, mail.getContent());
			stmt.setString(6, mail.getAttachmentNames());
			stmt.setString(7, mail.getAttachmentLocations());
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public List<LocalMailBean> listMails(String addressee) {
		List<LocalMailBean> mails = new ArrayList<>();
		String sql = "select * from mail where addressee = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql);){
			stmt.setString(1, addressee);
			rs = stmt.executeQuery();
			while (rs.next()) {
				LocalMailBean mail = new LocalMailBean();
				mail.setId(rs.getInt("id"));
				mail.setSender(rs.getString("sender"));
				mail.setSendTime(rs.getTimestamp("sendtime"));
				mail.setSubject(rs.getString("subject"));
				mail.setContent(rs.getString("content"));
				mail.setAttachmentNames(rs.getString("attachment_name"));
				//mail.setAttachmentLocations(rs.getString("attachment_location"));
				mails.add(mail);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mails;
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

}
