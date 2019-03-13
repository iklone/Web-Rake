import java.sql.*;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskScheduler {
	
	private static final int MAX_RESULT_STORAGE_IN_MB = 3;
	private static final int SIZE_PER_RESULT_IN_MB = 3;
	   
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
    				System.out.println("Connecting to database...");
    				conn = ConnectionManager.getConnection();

    				//Execute a query
    				System.out.println("Creating statement...");
    				stmt = conn.createStatement();

    				String sql = "SELECT * FROM (Task, Schedule) WHERE (Task.taskID = Schedule.taskID)";

    				ResultSet rs = stmt.executeQuery(sql);

    				//Extract data from result set
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
    				
    				//freeSpace(stmt);
    				
					//Clean-up environment, do we need these here if they're in finally?
					rs.close();
					stmt.close();
					conn.close();
    			}
				catch(SQLException se) { // which of these catches do we need here?
					//Handle errors for JDBC
					se.printStackTrace();
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
    
    public static void freeSpace(Statement stmt) {
		//Execute a query
    	try {
			String sql = "SELECT table_name AS `Result`, round(((data_length + index_length) / 1024 / 1024), 2) `Size in MB` FROM information_schema.TABLES WHERE table_schema = \"psyjct\" AND table_name = \"Result\"";
	
			ResultSet rs = stmt.executeQuery(sql);
			
			int sizeInMB = 0;
			//Extract data from result set
			while(rs.next()) {
				sizeInMB = rs.getInt("Size in MB");
			}
			
			if (sizeInMB > MAX_RESULT_STORAGE_IN_MB) {
				int excessSpace = sizeInMB - MAX_RESULT_STORAGE_IN_MB;
				int nResultsToDelete = excessSpace/SIZE_PER_RESULT_IN_MB;
				sql = "DELETE FROM Result WHERE resultID IN ( SELECT resultID FROM ( SELECT resultID FROM Result ORDER BY resultTime LIMIT " + nResultsToDelete + " ) a )";
			}
    	}
		catch(SQLException se) { // which of these catches do we need here?
			//Handle errors for JDBC
			se.printStackTrace();
		}
    }

}
