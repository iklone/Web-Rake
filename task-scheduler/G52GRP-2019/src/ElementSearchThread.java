import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ElementSearchThread extends Thread {

   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
   static final String DB_URL = "jdbc:mysql://mysql.cs.nott.ac.uk/psyjct";

   //  Database credentials
   static final String USER = "psyjct";
   static final String PASS = "1234Fred";
   
   int taskID;
   private URL url;
   private InputStream is;
   BufferedReader br;
   FileWriter fw;
   BufferedWriter bw;
   String line;
   String inputLine;
   int scrapeID;
   String element;
   String elements[][];
	   
   public static void main(String[] args) {
	   System.out.println("TOP OF MAIN\n");
	   ElementSearchThread est = new ElementSearchThread(2, "https://www.nottingham.ac.uk");
   }
   
	public ElementSearchThread(int taskID, String urlStr) {
		this.taskID = taskID;

		/*
		 * Downloading the page and storing it in the instance variable
		 */
		try {
			url = new URL(urlStr);
			is = url.openStream();
			br = new BufferedReader(new InputStreamReader(is));

			//save to this filename

			// Could use TaskID as unique file name
			String fileName = this.taskID + ".html";
			File file = new File(fileName);

			if (!file.exists()) {
				file.createNewFile();
			}

			//use FileWriter to write file
			fw = new FileWriter(file.getAbsoluteFile(), false);
			bw = new BufferedWriter(fw);

			while ((inputLine = br.readLine()) != null) {
				bw.write(inputLine + "\n");
			}
			
			bw.close();
			br.close();
		}
		catch (MalformedURLException mue) {
			mue.printStackTrace();
		}
		catch (IOException ioe) {
			
		}
		finally {
			try {
				if (is != null) is.close();
				bw.close();
				br.close();
			}
			catch (IOException ioe) {
				// nothing to see here
			}
		}
	   
		Connection conn = null;
		Statement stmt = null;
		Statement stmt2 = null;
		try {
			Class.forName(JDBC_DRIVER);
		  
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
		  
			stmt = conn.createStatement();
			stmt2 = conn.createStatement();
			String sql;
			sql = "SELECT * FROM Scrape WHERE Scrape.taskID = " + taskID;
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				
				scrapeID = rs.getInt("scrapeID");
				element = rs.getString("Element");

				sql = "INSERT INTO Result (scrapeID, resultTime, resultValue) VALUES (" + scrapeID + ", CURRENT_TIMESTAMP, \"" + getValue(element) + "\")";
				System.out.println(stmt2.executeUpdate(sql));
			}

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
	
	public String getValue(String element) {
		try {			
			//obtain Document somehow, doesn't matter how
			Document doc = Jsoup.connect("http://nottingham.ac.uk/").get();
			System.out.println(doc.title());
			Elements elements = doc.select(element);
			for (Element currentElement : elements) {
				System.out.println(currentElement.html());
				return currentElement.html();
			}
			
		}
		catch (Exception f) {
			System.out.println("Exception caught\n");
		}
		finally {
			
		}
		return "Exception";
	}
}
