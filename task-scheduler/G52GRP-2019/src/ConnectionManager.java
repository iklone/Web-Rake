import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://mysql.cs.nott.ac.uk/psyjct";
	
	//  Database credentials
	private static final String USER = "psyjct";
	private static final String PASS = "1234Fred";
	
	private static Connection conn = null;
	
    public static Connection getConnection() {
    	try {
			Class.forName(JDBC_DRIVER);
			try {
				conn = DriverManager.getConnection(DB_URL,USER,PASS);
			}
			catch (SQLException ex) {
				System.out.println("Failed to get the database connection.");
			}
    	}
    	catch (ClassNotFoundException ex) {
    		System.out.println("Driver not found.");
    	}
    	return conn;
    }
}
