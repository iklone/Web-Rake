package com.Finrake;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class contains the infinite loop where the program sits. In this loop, 
 * any tasks that are due are submitted to a thread pool so that their scrapes 
 * can be serviced using the ElementSearchThread class. This class is also 
 * responsible for freeing up space (using the max space specified in the 
 * settings.txt file.
 * 
 * @author psyhh1
 * @author psyjct
 * @see ElementSearchThread
 */

public class TaskScheduler {
	
	private static int MAX_RESULT_STORAGE_IN_MB = -1;
	
	/**
	 * This method reads the settings file to find the max storage size and 
	 * initialises the thread pool before sitting in an infinite loop where all 
	 * of the tasks from the database are selected and if any are due, they are 
	 * submitted to the thread pool. Tasks are considered due when their 
	 * schedules (records in the Schedule table, each associated with a specific 
	 * task) match with the current system time. This method also calls the 
	 * freeSpace method.
	 * 
	 * @param args unused.
	 * @throws IOException if the settings.txt file could not be read.
	 * @throws SQLException if there are any errors when selecting the tasks 
	 * 		   and their schedules from the database.
	 */
    public static void main(String[] args) {
    	System.out.println("Scheduler booted.");
    	
    	try(BufferedReader br = new BufferedReader(new FileReader("settings.txt"))) {
    		String[] lineSplit = new String[2];
    		
	        String line = br.readLine();
	        lineSplit = line.split(" ");

    	    MAX_RESULT_STORAGE_IN_MB = Integer.parseInt(lineSplit[1]);
    	    System.out.println("The max storage size is: " + MAX_RESULT_STORAGE_IN_MB + "MB");
    	}
    	catch (IOException e) {
			System.out.println("Failed to read settings.txt file.");
			e.printStackTrace();
			return;
		}
    	
    	// Thread pool
    	ExecutorService tp = Executors.newFixedThreadPool(1);
    	int currentMin = LocalDateTime.now().getMinute();
	   
    	while(true) {
    		if (currentMin != LocalDateTime.now().getMinute()) {
    			currentMin = LocalDateTime.now().getMinute();
    			int currentHour = LocalDateTime.now().getHour();
    			int currentDotW = LocalDateTime.now().getDayOfWeek().getValue();
    			int currentDotM = LocalDateTime.now().getDayOfMonth();
   
    			Connection conn = null;
    			Statement stmt = null;
    			
    			try {
    				System.out.println("Connecting to database (to check if any tasks are scheduled)...");
    				conn = ConnectionManager.getConnection();

    				//Execute query
    				System.out.println("Creating statement (to check if any tasks are scheduled)...");
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
							case "Minutely":
								tp.submit(new ElementSearchThread(taskID, urlStr));
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
    							if (resultDotM == currentDotM && resultHour == currentHour && resultMin == currentMin) {
    								tp.submit(new ElementSearchThread(taskID, urlStr));
    							}
    						default:		 
    					} // end switch
    				} // end while rs.next
    				
    				if (MAX_RESULT_STORAGE_IN_MB != -1)
    					freeSpace(stmt);
    				
					//Clean-up environment
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
    		} // end if
    	} // end while
    }
    
    /**
     * Deletes rows from the Result table if the size of the table exceeds the MAX_RESULT_STORAGE_IN_MB
     * 
     * @param stmt the Statement object which is used to execute queries.
     * @throws SQLException if there are any database errors with any SQL queries.
     */
    public static void freeSpace(Statement stmt) {
    	//Execute a query
    	try {
			String sql = "SELECT table_name AS `Result`, round(((data_length + index_length) / 1024 / 1024), 2) `Size in MB` FROM information_schema.TABLES WHERE table_schema = \"psyjct\" AND table_name = \"Result\"";
			ResultSet rs = stmt.executeQuery(sql);
			
			int tableSizeInMB = 0;
			while(rs.next()) {
				tableSizeInMB = rs.getInt("Size in MB");
			}
			
			sql = "SELECT COUNT(*) FROM Result";
			rs = stmt.executeQuery(sql);
			
    		int nTotalResults = 0;
			while(rs.next()) {
				nTotalResults = rs.getInt("COUNT(*)");
			}
			
			int nResultsToDelete = 0;
			nResultsToDelete = nTotalResults / 5;
			
			if (tableSizeInMB > MAX_RESULT_STORAGE_IN_MB) {
				sql = "DELETE FROM Result WHERE resultID IN ( SELECT resultID FROM ( SELECT resultID FROM Result ORDER BY resultTime LIMIT " + nResultsToDelete + " ) a )";
			}
    	}
		catch(SQLException se) { // which of these catches do we need here?
			//Handle errors for JDBC
			System.out.println("Failed to free space. Exception in freeSpace().");
			se.printStackTrace();
		}
    }

}
