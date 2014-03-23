package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import server.DAO;

public class TestDAO {

	private DAO dao;
	@Before
	public void initDAOInstance() {
		dao = new DAO();
		dao.connect();
	}
	
	@Test
	public void testUserExists() {
		String username = "admin";
		boolean result = dao.userExists(username);
		assertTrue(result);
	}
}
