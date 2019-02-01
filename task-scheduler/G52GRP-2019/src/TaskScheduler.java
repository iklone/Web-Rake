package g52grp;
import java.time.LocalDateTime;
import java.sql.*;
import java.util.concurrent.TimeUnit;

public class TaskScheduler {
	
	   // JDBC driver name and database URL
	   static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://mysql.cs.nott.ac.uk/psyjct";

	   //  Database credentials
	   static final String USER = "psyjct";
	   static final String PASS = "1234Fred";

	public static void main(String[] args) {
	    while(true) {
			   Connection conn = null;
			   Statement stmt = null;
			   try{
			      //STEP 2: Register JDBC driver
			      Class.forName(JDBC_DRIVER);

			      //STEP 3: Open a connection
			      System.out.println("Connecting to database...");
			      conn = DriverManager.getConnection(DB_URL,USER,PASS);

			      //STEP 4: Execute a query
			      System.out.println("Creating statement...");
			      stmt = conn.createStatement();
			      String sql;
			      sql = "SELECT * FROM Task";
			      ResultSet rs = stmt.executeQuery(sql);

			      //STEP 5: Extract data from result set
			      while(rs.next()){
			         //Retrieve by column name
			         String id  = rs.getString("taskName");

			         //Display values
			         System.out.print("ID: " + id + "\n");
			      }
			      //STEP 6: Clean-up environment
			      rs.close();
			      stmt.close();
			      conn.close();
			   }catch(SQLException se){
			      //Handle errors for JDBC
			      se.printStackTrace();
			   }catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			   }finally{
			      //finally block used to close resources
			      try{
			         if(stmt!=null)
			            stmt.close();
			      }catch(SQLException se2){
			      }// nothing we can do
			      try{
			         if(conn!=null)
			            conn.close();
			      }catch(SQLException se){
			         se.printStackTrace();
			      }//end finally try
			   }//end try
			   
	    	/*if (LocalDateTime.now() == MYSQL_GET_TIME) {
	    		new Thread(service_scrape)
	    		// also need to call the query intermittently to ensure any changes to intervals are recorded ASAP
	    	}*/
	    }
	}
	
}
