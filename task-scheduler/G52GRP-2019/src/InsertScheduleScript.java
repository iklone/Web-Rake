import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class InsertScheduleScript {
	
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
   static final String DB_URL = "jdbc:mysql://mysql.cs.nott.ac.uk/psyjct";

   //  Database credentials
   static final String USER = "psyjct";
   static final String PASS = "1234Fred";
	
   public static void main(String[] args) { /*
		Connection conn = null;
		Statement stmt = null;
		Statement stmt2 = null;
			try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			stmt = conn.createStatement();
			
			for (int i = 0; i <= 59; i++) {
				String sql = "INSERT INTO Schedule (taskID, Type, Min) VALUES ( 1, 'Hourly', " + i + ")";
				stmt.executeUpdate(sql);
			}
			stmt.close();
			conn.close();
			}
			catch(SQLException se) {
				//Handle errors for JDBC
				se.printStackTrace();
			}
			catch(Exception e) {
				//Handle errors for Class.forName
				e.printStackTrace();
			}
			finally {
				//finally block used to close resources
				try {
					if(stmt!=null)
						stmt.close();
				}
				catch(SQLException se2) {
				}// nothing we can do
				try {
					if(conn!=null)
						conn.close();
				}
				catch(SQLException se) {
					se.printStackTrace();
				}//end finally try
		   }//end try
	*/}
}
