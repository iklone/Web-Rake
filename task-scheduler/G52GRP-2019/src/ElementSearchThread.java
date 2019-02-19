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
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ElementSearchThread implements Runnable {

   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
   static final String DB_URL = "jdbc:mysql://mysql.cs.nott.ac.uk/psyjct";

   //  Database credentials
   static final String USER = "psyjct";
   static final String PASS = "1234Fred";
   
   int taskID;
   String urlStr;
   
   private InputStream is;
   BufferedReader br;
   FileWriter fw;
   BufferedWriter bw;
   String line;
   String inputLine;
   int scrapeID;
   String element;
   String elements[][];
   ScrapeResult result;
   String value;
   
	public ElementSearchThread(int taskID, String urlStr) {
		this.taskID = taskID;
		this.urlStr = urlStr;
	}
	
	@Override
	public void run() {
		insertResultIntoDatabase();
	}
	
	public void insertResultIntoDatabase() {
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

					result = getHTMLUnitResult(element);
					
					if (result.getElement() == null) {
						value = "ERROR";
					}
					else {
						value = result.getElement();
					}
					
					sql = "INSERT INTO Result (scrapeID, resultTime, resultValue) VALUES (" + scrapeID + ", CURRENT_TIMESTAMP, \"" + value + "\")";
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
	
	public ScrapeResult getHTMLUnitResult(String element) throws Exception {
		ScrapeResult result = new ScrapeResult();
		
		try (final WebClient webClient = new WebClient()) {
		    webClient.getOptions().setThrowExceptionOnScriptError(false);
		    
			// turn of htmlunit warnings
			java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		    java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
			
		    final HtmlPage page = webClient.getPage(urlStr);
			
			page.getEnclosingWindow().getJobManager().waitForJobs(1000);
			
			List list = page.getByXPath(element);
			
			for (Object o : list) {
				if(o instanceof HtmlElement) {
					HtmlElement e = (HtmlElement)o;
					result.setElement(e.getTextContent());
					result.setFlag(0);
				}
			}

		}
		return result;
	}
	
	public ScrapeResult getJSoupResult(String element) {
		ScrapeResult result = new ScrapeResult();
		
		System.out.println("Getting result from " + urlStr);
		try {			
			//obtain Document somehow, doesn't matter how
			Document doc = Jsoup.connect(urlStr).get();
			Elements elements = doc.select(element);
			for (Element currentElement : elements) {
				System.out.println(currentElement.html());
				result.setElement(currentElement.html());
				result.setFlag(0);
			}
			
		}
		catch (Exception f) {
			System.out.println("Exception caught\n");
		}
		finally {
			/*if (result.getElement() != null) {
				result.setFlag(0);;
			}
			else if (result.getElement() != null) {
				result.setFlag(0);
			}*/
		}
		return result;
	}
}
