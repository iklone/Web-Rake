import java.sql.*;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskScheduler {
	
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
   static final String DB_URL = "jdbc:mysql://mysql.cs.nott.ac.uk/psyjct";

   //  Database credentials
   static final String USER = "psyjct";
   static final String PASS = "1234Fred";
	   
   public static void main(String[] args) {
	// Thread pool
	   ExecutorService tp = Executors.newFixedThreadPool(1);
	   int currentMin = LocalDateTime.now().getMinute();
		
	   while(true) {
		   if (currentMin != LocalDateTime.now().getMinute()) {
			   currentMin = LocalDateTime.now().getMinute();
			   int currentHour = LocalDateTime.now().getHour();
			   int currentDotW = LocalDateTime.now().getDayOfWeek().getValue();

		    	   
			   Connection conn = null;
			   Statement stmt = null;
			   try {
				  //STEP 2: Register JDBC driver
				  Class.forName(JDBC_DRIVER);
				
				  //STEP 3: Open a connection
				  System.out.println("Connecting to database...");
				  conn = DriverManager.getConnection(DB_URL,USER,PASS);
				
				  //STEP 4: Execute a query
				  System.out.println("Creating statement...");
				  stmt = conn.createStatement();
				  String sql;
				  sql = "SELECT * FROM (Task, Schedule) WHERE (Task.taskID = Schedule.taskID)";
				  ResultSet rs = stmt.executeQuery(sql);
				
				  //STEP 5: Extract data from result set
				  while(rs.next()) {
					 //Retrieve by column name
					 int taskID = rs.getInt("taskID");
					 String type  = rs.getString("Type");
					 int resultMin = rs.getInt("Min");
					 String urlStr = rs.getString("taskURL");
					 
					 switch(type) {
						 case "Hourly":
							 if (resultMin == currentMin) {
								 tp.submit(new ElementSearchThread(taskID, urlStr));
							 }
						 case "Daily":
							 int resultHour = rs.getInt("Hour");
						     if (resultHour == currentHour && resultMin == currentMin) {
						    	 tp.submit(new ElementSearchThread(taskID, urlStr));
						     }
						 case "Weekly":
							 resultHour = rs.getInt("Hour");
							 int resultDotW = rs.getInt("DotW");
							 if (resultDotW == currentDotW && resultHour == currentHour && resultMin == currentMin) {
								 tp.submit(new ElementSearchThread(taskID, urlStr));
							 }
						 case "Monthly":
							 resultHour = rs.getInt("Hour");
							 int resultDotM = rs.getInt("DotM");
							 if (resultDotM == currentDotW && resultHour == currentHour && resultMin == currentMin) {
								 tp.submit(new ElementSearchThread(taskID, urlStr));
							 }
						 default:
							 
					 }
				  }
				  //STEP 6: Clean-up environment
			      rs.close();
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
		   }
	   }
   }
	
}
