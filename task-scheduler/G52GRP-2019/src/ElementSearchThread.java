import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ElementSearchThread implements Runnable {
	
	private static final int MAX_CLICKED_PAGES = 8;
	
	private int taskID;
	private String urlStr;
	private int scrapeID;
	private String element;
	private String elementID;
	private int flag;
	private ScrapeResult result;
	private String value;
	private WebClient webClient;
	private HtmlPage page;
   
	public ElementSearchThread(int taskID, String urlStr) {
		this.taskID = taskID;
		this.urlStr = urlStr;
		this.page = null;
		this.result = new ScrapeResult();
		createWebClient();
	}
	
	@Override
	public void run() {
		insertResultIntoDatabase();
	   	webClient.close(); // plug memory leaks
	}
	
	public void createWebClient() {
		// turn off HtmlUnit warnings
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
	    java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
	    
	    // create webclient
	   	WebClient webClient = new WebClient();
	   	webClient.getOptions().setTimeout(60000); // ??
		webClient.waitForBackgroundJavaScript(10000);
	    webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
	    webClient.getOptions().setThrowExceptionOnScriptError(false);
	    webClient.getOptions().setCssEnabled(false);
	    webClient.getOptions().setUseInsecureSSL(true);
	    this.webClient = webClient;
	}
	
	public void insertResultIntoDatabase() {
		Connection conn = null;
		Statement stmt = null;
		
		try {
			conn = ConnectionManager.getConnection();
			stmt = conn.createStatement();
			String sql = "SELECT * FROM Scrape WHERE Scrape.taskID = " + taskID;
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
			   	long scrapeStartTime = System.currentTimeMillis();
			   	
				scrapeID = rs.getInt("scrapeID");
				element = rs.getString("Element");
				elementID = rs.getString("elementID");
				flag = rs.getInt("flag");
				System.out.println("Scraping element: " + element + " from: " + this.urlStr);
				
				if (flag != 2)
					scrapeElement();
				else
					System.out.println("Didn't scrape, flag was 2");
				
			    long scrapeEndTime = System.currentTimeMillis();
			   	System.out.println("Time taken: " + (scrapeEndTime - scrapeStartTime) + " milliseconds");
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
	
	public void scrapeElement() {
		Connection conn = null;
		Statement stmt = null;
		
		try {
			conn = ConnectionManager.getConnection();
			stmt = conn.createStatement();
			
			getHTMLUnitResult(element, elementID);
			
			if (result.getElement() == null) {
				value = "Element not found.";
				System.out.println("The element was not found. Flag: " + result.getFlag());
			}
			else {
				value = result.getElement();
				System.out.println("The element was found, it's innerHTML is: " + value);

			}
			
			System.out.println("Inserting result into the Result table.");
			String sql = "INSERT INTO Result (scrapeID, resultTime, resultValue) VALUES (" + scrapeID + ", CURRENT_TIMESTAMP, \"" + value + "\")";
		    stmt.executeUpdate(sql);
		    
		    sql = "UPDATE Scrape SET flag = " + result.getFlag() + " WHERE ScrapeID = " + this.scrapeID;
		    stmt.executeUpdate(sql);
			System.out.println("Inserted result into the Result table.");
			
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
	
	public void getHTMLUnitResult(String element, String elementID) {
		System.out.println("Getting result from " + urlStr);

		if (!scrape() && (result.getFlag() != 4)) {
			/*if (searchAI(element, elementID, page)) {
				scrape(); // On AI success, scrape again
				result.setFlag(1);
			}
			else {*/
				result.setFlag(2);
			//}
		}
	}
	
	public Boolean scrape() {
		Boolean foundElement = false;
		
		try {  
		    page = webClient.getPage(urlStr);    
			HtmlElement e = page.getFirstByXPath(element);
			
			if (e == null) {
				int i = 0;
				int clickedPages = 0;
				HtmlElement acceptButton = null;
				Boolean acceptButtonFound = false;
				System.out.println("Couldn't find element. Trying the buttons.");
				
				List<HtmlElement> buttons = page.getByXPath("//button");
				for (HtmlElement e2: buttons) {
					System.out.println("GETS HERE Button text: " + e2.asText());
					String[] acceptSynonyms = {"accept", "submit", "ok", "agree"};
					for (i = 0; i < acceptSynonyms.length; i++) {
						if (e2.asText().toLowerCase().equals(acceptSynonyms[i])) {
							acceptButtonFound = true;
							acceptButton = e2;
							break;
						}
					}
					
					if (acceptButtonFound) {
						break;
					}
				}
				
				if (acceptButtonFound) {
					System.out.println("Accept button was found: " + acceptButton.asText());
					page = acceptButton.click();
					e = (HtmlElement) page.getFirstByXPath(element);
				}
				else {
			        List<HtmlElement> submitButtons = page.getByXPath("//button[@type='submit']");
			        submitButtons.addAll(page.getByXPath("//input[@type='submit']"));
			        
					for (HtmlElement e2 : submitButtons) {
						System.out.println("Button text: " + e2.asText());
						// Can we check each button innerHTML for 'OK' or 'I accept' or 'Accept' or ... 'Agree' ?
						page = e2.click();
						if ((e = (HtmlElement) page.getFirstByXPath(element)) != null) {
							break;
						}
						clickedPages++;
						if (clickedPages == MAX_CLICKED_PAGES) {
							break;
						}
					}
				}
			}
			
			if (e != null) {
				result.setResult(e.getTextContent());
				result.setFlag(0);
				foundElement = true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			result.setFlag(4); // couldn't even download the webpage
		}
		
		return foundElement;
	}
	
	public Boolean searchAI(String element, String elementID, HtmlPage page) {
		Connection conn = null;
		Statement stmt = null;
		
		int depth = 0;
		String[] parts = element.split("/");
		parts = parts[parts.length].split("[");
		String tag = parts[0];
		HtmlElement htmlElement;
		
		try {
			conn = ConnectionManager.getConnection();
			stmt = conn.createStatement();
			String sql = "SELECT AVG(Depth) FROM Intervention";
			
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				depth = rs.getInt("AVG(Depth)");
			}
			
			if (depth == 0) {
				return false;
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
		
		for (int i = 0; i < depth; i++) {
			// for every parent, completely evaluate whether any of their descendants are the element
			// by checking their ids
			element += "/..";

			for (Object o : page.getByXPath(".//" + tag)) {
				htmlElement = (HtmlElement) o;
				if (htmlElement.getId() == elementID) {
					updateXPath(htmlElement.getCanonicalXPath(), i);
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void updateXPath(String updatedXPath, int depth) {
		Connection conn = null;
		Statement stmt = null;
		
		this.element = updatedXPath;
		
		try {
			conn = ConnectionManager.getConnection();
			stmt = conn.createStatement();
			
			String sql = "UPDATE Scrape SET Element = " + updatedXPath + " WHERE ScrapeID = " + this.scrapeID;
			stmt.executeUpdate(sql);
			System.out.println("Updated Scrape table with new Element XPath.");
			
			sql = "INSERT INTO Intervention (scrapeID, Depth) VALUES (" + scrapeID + "," + depth + ")";
			stmt.executeUpdate(sql);
			System.out.println("Inserted intervention into Intervention table.");
			
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
	
}
