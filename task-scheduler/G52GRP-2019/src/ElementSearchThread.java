import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
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
	private String sampleData;
	private String scrapeName;
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
	    try {
			page = webClient.getPage(urlStr);
		}
	    catch (Exception e) {
	    	System.out.println("Couldn't download the page for task: " + this.taskID + ", scrapeId: " + this.scrapeID);
			Connection conn = null;
			Statement stmt = null;
			
			try {
				conn = ConnectionManager.getConnection();
				stmt = conn.createStatement();
			    
			    String sql = "UPDATE Scrape SET flag = " + 4 + " WHERE TaskID = " + this.taskID;
			    stmt.executeUpdate(sql);
				System.out.println("Updated flag in Scrape table for scrapeId:" + this.scrapeID + ", taskID: " + this.taskID);
				
				stmt.close();
				conn.close();
			}
			catch(SQLException se) {
				//Handle errors for JDBC
				se.printStackTrace();
			}
			catch(Exception e2) {
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
		   }
			return;
		}  
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
				sampleData = rs.getString("sampleData");
				scrapeName = rs.getString("scrapeName");
				System.out.println("Scraping element: " + element + " from: " + this.urlStr + "for scrapeId:" + this.scrapeID + ", taskID: " + this.taskID);
				
				if (flag != 2)
					scrapeElement();
				else
					System.out.println("Did not scrape - flag is 2 for scrapeId:" + this.scrapeID + ", taskID: " + this.taskID);
				
			    long scrapeEndTime = System.currentTimeMillis();
			   	System.out.println("Time taken: " + (scrapeEndTime - scrapeStartTime) + " milliseconds, scrapeID: " + this.scrapeID);
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
			
			System.out.println("Getting result from " + urlStr);

			if (!scrape()) {
				if (searchAI(element, elementID, page)) {
					scrape(); // On AI success, scrape again
					result.setFlag(1);
				}
				else {
					result.setFlag(2);
				}
			}
			
			if (result.getElement() == null) {
				value = "Element not found.";
				System.out.println("The element was not found." + "for scrapeId:" + this.scrapeID + ", taskID: " + this.taskID + ", New flag: " + result.getFlag());
			}
			else {
				value = result.getElement();
				System.out.println("The element was found, it's innerHTML is: " + value + "for scrapeId:" + this.scrapeID + ", taskID: " + this.taskID);

			}
			
			System.out.println("Inserting result into the Result table for scrapeId:" + this.scrapeID + ", taskID: " + this.taskID);
			String sql = "INSERT INTO Result (scrapeID, resultTime, resultValue) VALUES (" + scrapeID + ", CURRENT_TIMESTAMP, \"" + value + "\")";
		    stmt.executeUpdate(sql);
		    
		    sql = "UPDATE Scrape SET flag = " + result.getFlag() + " WHERE ScrapeID = " + this.scrapeID;
		    stmt.executeUpdate(sql);
			System.out.println("Inserted result into the Result table for scrapeId:" + this.scrapeID + ", taskID: " + this.taskID);
			
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
	
	public Boolean scrape() {
		Boolean foundElement = false;
		
		try {    
			HtmlElement e = page.getFirstByXPath(element);
			
			if (e == null) {
				int i = 0;
				int clickedPages = 0;
				HtmlElement acceptButton = null;
				Boolean acceptButtonFound = false;
				System.out.println("Couldn't find element for scrape with ID: " + this.scrapeID + ". Trying the buttons.");
				
				List<HtmlElement> buttons = page.getByXPath("//button");
				for (HtmlElement e2: buttons) {
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
					System.out.println("Accept button was found: " + acceptButton.asText()  + ", scrapeId:" + this.scrapeID + ", taskID: " + this.taskID);
					page = acceptButton.click();
					e = (HtmlElement) page.getFirstByXPath(element);
				}
				else {
			        List<HtmlElement> submitButtons = page.getByXPath("//button[@type='submit']");
			        submitButtons.addAll(page.getByXPath("//input[@type='submit']"));
			        
					for (HtmlElement e2 : submitButtons) {
						System.out.println("Submit button text: " + e2.asText() + ", scrapeId: " + this.scrapeID + ", taskID: " + this.taskID);
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
				// check the type and neighbours to ensure the element is still the same, if not return false
				if (isStillNumeric(e.asText()) && isStillDateTime(e.asText()))
					return foundElement;
				result.setResult(e.getTextContent());
				result.setFlag(0);
				foundElement = true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();

		}
		
		return foundElement;
	}
	
	public Boolean isNumeric(String element) {
		if (Pattern.matches("^(\\d+|\\d{1,3}(,\\d{3})*)(\\.\\d+)?$", element)) {
			return true;
		}
		return false;
	}
	
	public Boolean isStillNumeric(String scrapedElement) {
		Boolean hasChanged = false;
		
		// check type and neighbours
		if (isNumeric(this.sampleData) ) { // number
			if (!isNumeric(scrapedElement) ) {
				hasChanged = true;
			}
		}

		return hasChanged;
	}
	
	public Boolean isDateTime(String element) {
		if (Pattern.matches("[0-9]{2}[\\/\\-,.][0-9]{2}[\\/\\-,.](19|20)[0-9]{2}", element)) { //01/01/1998
			return true;
		}
		else if (Pattern.matches("(19|20)[0-9]{2}[\\/\\-,.][0-9]{2}[\\/\\-,.][0-9]{2}", element)) { // 1998/01/01
			return true;
		}
		else if (Pattern.matches("[0-9]{2}[\\/\\-,.][0-9]{2}[\\/\\-,.][0-9]{2}", element)) { // 01/01/98
			return true;
		}
		else if (Pattern.matches("[0-9]{2}[\\/\\-,.][A-Z|a-z]{3}[\\/\\-,.][0-9]{2}", element)) { // 01-JAN-98
			return true;
		}
		else if (Pattern.matches("[0-9]{2}[\\/\\-,.][A-Z|a-z]{3}[\\/\\-,.](19|20)[0-9]{2}", element)) { // 01-JAN-1998
			return true;
		}
		else if (Pattern.matches("[0-9]{2}[\\/\\-,.][A-Z|a-z]{3}[\\/\\-,.](19|20)[0-9]{2} [0-2][0-9]:[0-5][0-9]", element)) { // 01-JAN-1998 08:59
			return true;
		}
		else if (Pattern.matches("[0-9]{2}[\\/\\-,.][A-Z|a-z]{3}[\\/\\-,.](19|20)[0-9]{2} [0-2][0-9]:[0-5][0-9]:[0-5][0-9]", element)) { // 01-JAN-1998 08:59:00
			return true;
		} //START OF REGEXS MATCHING EXAMPLE WEBSITES
		else if (Pattern.matches("[[0-3]{0,1}[0-9]([s|n|r|t][t|d|h]){0,1}[\\/\\-,. ][A-Z|a-z]{3}.{0,1}[\\/\\-,. ](19|20){0,1}[0-9][0-9]", element)) { // 1st Jan 2019
			return true;
		}
		else if (Pattern.matches("[0-9]{2}[\\/\\-,.][0-9]{2}[\\/\\-,.](19|20)[0-9]{2}[ ]{0,1}[-]{1}[ ]{0,1}[0-9]{2}[\\/\\-,.][0-9]{2}[\\/\\-,.](19|20)[0-9]{2}", element)) { // 01/01/1999 - 01/01/1999
			return true;
		}
		else if (Pattern.matches("[0-9]{2}[\\/\\-,.][A-Z|a-z]{3}[\\/\\-,.](19|20)[0-9]{2} [0-2][0-9]:[0-5][0-9]:[0-5][0-9].[0-9]*", element)) { // 19-Feb-2019 08:05:33.000000000000000
			return true;
		}
		
		
		
		
		return false;
	}
	
	public Boolean isStillDateTime(String scrapedElement) {
		Boolean hasChanged = false;
		
		// check type and neighbours
		if (isDateTime(this.sampleData) ) { // number
			if (!isDateTime(scrapedElement) ) {
				hasChanged = true;
			}
		}

		return hasChanged;
	}
	
	public Boolean isCurrency(String element) {
		if (Pattern.matches("^(\\d+|\\d{1,3}(,\\d{3})*)(\\.\\d+)?$", element)) {
			return true;
		}
		return false;
	}
	
	public Boolean isStillCurrency(String scrapedElement) {
		Boolean hasChanged = false;
		
		// check type and neighbours
		if (isCurrency(this.sampleData) ) { // number
			if (!isCurrency(scrapedElement) ) {
				hasChanged = true;
			}
		}

		return hasChanged;
	}
	
	public String findParent(String element) {
		int index = element.lastIndexOf("/");
		if (index != 0) {
			return element.substring(0, index);
		}
		return element;
	}
	
	public Boolean searchAI(String element, String elementID, HtmlPage page) {
		Connection conn = null;
		Statement stmt = null;
		
		int depth = 0;
		int index = 0;
		int i;
		boolean passed;
		
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

		System.out.println("AI init" + ", scrapeId:" + this.scrapeID + ", taskID: " + this.taskID);
		for (i = 0; i < depth + 1; i++) {
			System.out.println("depth attempt " + i + ", scrapeId:" + this.scrapeID + ", taskID: " + this.taskID);
			element = findParent(element);
			System.out.println("parent  " + element + ", scrapeId:" + this.scrapeID + ", taskID: " + this.taskID);
			HtmlElement parent = page.getFirstByXPath(element);
			System.out.println("testing parent  " + parent.asText() + ", scrapeId:" + this.scrapeID + ", taskID: " + this.taskID);
			Iterable<DomElement> childList = parent.getChildElements();
			

			System.out.println("this has cousins " + parent.getChildElementCount() + ", scrapeId:" + this.scrapeID + ", taskID: " + this.taskID);
			for (DomElement e : childList) {

				System.out.println("testing family " + e.asText() + ", scrapeId:" + this.scrapeID + ", taskID: " + this.taskID);
				System.out.println("!");
				passed = false;
				if (elementID != "") {
					if (e.getId() == elementID && isNumeric(e.asText()) ) {
						passed = true;
					}
				} else {
					if (isNumeric(e.asText())) {
						passed = true;
					}
				}
				
				if (passed) {
					updateXPath(e.getCanonicalXPath(), i);
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
			System.out.println("Updated Scrape table with new Element XPath." + ", scrapeId:" + this.scrapeID + ", taskID: " + this.taskID);
			
			sql = "INSERT INTO Intervention (scrapeID, Depth) VALUES (" + scrapeID + "," + depth + ")";
			stmt.executeUpdate(sql);
			System.out.println("Inserted AI intervention into Intervention table." + ", scrapeId:" + this.scrapeID + ", taskID: " + this.taskID);
			
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
