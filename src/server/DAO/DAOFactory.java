package server.DAO;


public class DAOFactory {
	public static DAO getDAOInstance() {
		return new DAO();
	}
}
