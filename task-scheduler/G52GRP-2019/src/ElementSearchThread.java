import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ElementSearchThread implements Runnable {
	
	private int taskID;
	private String urlStr;
	private int scrapeID;
	private String element;
	private ScrapeResult result;
	private String value;
   
	public ElementSearchThread(int taskID, String urlStr) {
		this.taskID = taskID;
		this.urlStr = urlStr;
	}
	
	@Override
	public void run() {
	   	long scrapeStartTime = System.currentTimeMillis();

		WebClient webClient = getWebClient();
		insertResultIntoDatabase(webClient);
	   	
	    long scrapeEndTime = System.currentTimeMillis();
	   	System.out.println("Time taken: " + (scrapeEndTime - scrapeStartTime) + " milliseconds");
	}
	
	public WebClient getWebClient() {
	   	WebClient webClient = new WebClient();
	   	webClient.getOptions().setTimeout(60000); // ??
		webClient.waitForBackgroundJavaScript(10000);
	    webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
	    webClient.getOptions().setThrowExceptionOnScriptError(false);
	    webClient.getOptions().setCssEnabled(false);
	    webClient.getOptions().setUseInsecureSSL(true);
	    return webClient;
	}
	
	public void insertResultIntoDatabase(WebClient webClient) {
		Connection conn = null;
		Statement stmt = null;
		
		try {
			conn = ConnectionManager.getConnection();
			stmt = conn.createStatement();
			String sql = "SELECT * FROM Scrape WHERE Scrape.taskID = " + taskID;
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				scrapeID = rs.getInt("scrapeID");
				element = rs.getString("Element");
			}
			
			result = getHTMLUnitResult(element, webClient);
			
			if (result.getElement() == null) {
				System.out.println("An element was not found.");
				value = "Element not found.";
			}
			else {
				System.out.println("An element was found.");
				value = result.getElement();
			}
			
			System.out.println("Inserting result into the Result table.");
			sql = "INSERT INTO Result (scrapeID, resultTime, resultValue) VALUES (" + scrapeID + ", CURRENT_TIMESTAMP, \"" + value + "\")";
		    stmt.executeUpdate(sql);
			System.out.println("Inserted result into the Result table.");
			
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
				if(stmt != null)
					stmt.close();
			}
			catch(SQLException se2) {
				// nothing we can do
			}
			try {
				if(conn != null)
					conn.close();
			}
			catch(SQLException se) {
				se.printStackTrace();
			}//end finally try
	   }//end try 
	}
	
	public ScrapeResult getHTMLUnitResult(String element, WebClient webClient) throws Exception {
		ScrapeResult result = new ScrapeResult();
		
		System.out.println("Getting result from " + urlStr);
		
		try {    
			// turn off HtmlUnit warnings
			java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		    java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
			
		    final HtmlPage page = webClient.getPage(urlStr);    
		    
			List list = page.getByXPath(element);

			for (Object o : list) {
				System.out.println("An object o is present in XPath list");
				if(o instanceof HtmlElement) {
					HtmlElement e = (HtmlElement)o;
					System.out.println("The content of the element is: " + e.getTextContent());
					result.setElement(e.getTextContent());
					result.setFlag(0);
				}
			}
		}
		finally {
		   	webClient.close(); // plug memory leaks
		}
		
		return result;
	}
	
}
