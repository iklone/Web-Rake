import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.regex.Pattern;

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
	private ScrapeResult result;
	private String value;
	private WebClient webClient;
	private HtmlPage page;
	
	/*
	 * Constructor
	 */
	public ElementSearchThread(int taskID, String urlStr) {
		this.taskID = taskID;
		this.urlStr = urlStr;
		this.page = null;
		this.result = new ScrapeResult();
		this.webClient = createWebClient();
	}

	@Override
	public void run() {
	    try {
			page = webClient.getPage(urlStr);
		}
	    catch (Exception e) {
	    	System.out.println("taskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", Couldn't download the page with url: " + this.urlStr);
			Connection conn = null;
			Statement stmt = null;
			
			try {
				conn = ConnectionManager.getConnection();
				stmt = conn.createStatement();
			    
			    String sql = "UPDATE Scrape SET flag = " + 4 + " WHERE TaskID = " + this.taskID;
			    stmt.executeUpdate(sql);
				System.out.println("taskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", Updated flag to 4 in Scrape table");
				
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
	    
		serviceAllScrapesForTask();
	   	webClient.close(); // plug memory leaks
	}
	
	/*
	 * Create and configure the HtmlUnit WebClient
	 */
	public WebClient createWebClient() {
		// Turn off HtmlUnit warnings
		java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
	    java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
	    
	    // create WebClient and configure settings
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
	
	/*
	 * Finds all the scrapes for the task and for each scrape, if scrape flag is 
	 * not equal to 2, call scrapeElement(), otherwise just print a message
	 */
	public void serviceAllScrapesForTask() {
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
				elementID = rs.getString("elementID");
				flag = rs.getInt("flag");
				sampleData = rs.getString("sampleData");
				System.out.println("taskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", Scraping element: " + element + " from: " + this.urlStr);
				
				if (flag != 2)
					scrapeElement();
				else
					System.out.println("taskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", Did not scrape because scrape flag is 2");
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
	
	/*
	 * Try a regular scrape by calling regularScrapeFind(), if that fails, call 
	 * searchAIFind().
	 * Also set Result flag to: 
	 * 		1 if regularScrapeFind() failed and searchAIFind() succeeded
	 * 		2 if regularScrapeFind() and searchAIFind() both failed
	 * Also Insert result into database and update the flag } extract method
	 */
	public void scrapeElement() {
		Connection conn = null;
		Statement stmt = null;
		
		try {
			conn = ConnectionManager.getConnection();
			stmt = conn.createStatement();

			if (!regularScrapeFind()) {
				if (searchAIFind(element, elementID, page)) {
					regularScrapeFind(); // On AI success, scrape again
					result.setFlag(1);
				}
				else {
					result.setFlag(2);
				}
			}
			
			if (result.getElement() == null) {
				value = "Element not found.";
				System.out.println("taskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", After regular and AI scrape attempts, the element was not found, New flag: " + result.getFlag());
			}
			else {
				value = result.getElement();
				System.out.println("taskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", The element was found, it's innerHTML is: " + value);

			}
			
			System.out.println("taskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", Inserting result into the Result table");
			String sql = "INSERT INTO Result (scrapeID, resultTime, resultValue) VALUES (" + scrapeID + ", CURRENT_TIMESTAMP, \"" + value + "\")";
		    stmt.executeUpdate(sql);
		    
		    sql = "UPDATE Scrape SET flag = " + result.getFlag() + " WHERE ScrapeID = " + this.scrapeID;
		    stmt.executeUpdate(sql);
			System.out.println("taskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", Inserted result into the Result table");
			
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

	/*
	 * Attempts to find the element on the page. In the first case, when that 
	 * fails, attempts to bypass any consent forms and scrape the following pages. 
	 * If the element was found, set the result value and flag to 0 and return true.
	 * Otherwise, return false.
	 */
	public Boolean regularScrapeFind() {
		
		try {    
			HtmlElement e = page.getFirstByXPath(element);
			
			if (e == null) {
				int i = 0;
				int clickedPages = 0;
				HtmlElement acceptButton = null;
				Boolean acceptButtonFound = false;
				System.out.println("taskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", regularScrapeFind failed to find element. Trying the accept buttons.");
				
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
					
					if (acceptButtonFound) { // Unnecessary since we do this at 268?
						break;
					}
				}
				
				HtmlPage tmpPage = page;
				if (acceptButtonFound) {
					System.out.println("taskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", Accept button led to a page that had the element. Accept button text: " + acceptButton.asText());
					tmpPage = acceptButton.click();
					e = (HtmlElement) tmpPage.getFirstByXPath(element);
				}
				else {
					System.out.println("taskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", Couldn't find an accept button, trying submit buttons.");
			        List<HtmlElement> submitButtons = tmpPage.getByXPath("//button[@type='submit']");
			        submitButtons.addAll(tmpPage.getByXPath("//input[@type='submit']"));
			        
					for (HtmlElement e2 : submitButtons) {
						System.out.println("taskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", Checking submit button with text: " + e2.asText());
						tmpPage = e2.click();
						if ((e = (HtmlElement) tmpPage.getFirstByXPath(element)) != null) {
							System.out.println("taskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", Submit button led to a page that had the element.");
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
				// check the type and neighbours to ensure the element is still the same type, if not return false
				if (unchangedType(e.asText())) {
					result.setResult(e.getTextContent());
					result.setFlag(0);
					return true;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/*
	 * Returns true if the (scraped) element's type has not changed (compared to 
	 * the sample data from the database), otherwise return false. This method 
	 * only checks for numeric, date/time and currency types.
	 */
	public Boolean unchangedType(String element) {
		if (unchangedNumericness(element) && unchangedDateTimeness(element) && unchangedCurrencyness(element)) {
			return true;
		}
		return false;
	}
	
	/*
	 * Returns true if the element is numeric, otherwise returns false.
	 */
	public Boolean isNumeric(String element) {
		if (Pattern.matches("^[ ]*(\\d+|\\d{1,3}(,\\d{3})*)(\\.\\d+)?[ ]*$", element)) {
			return true;
		}
		return false;
	}
	
	/*
 	 * Returns true if the (scraped) element's type has not changed (compared to 
	 * the sample data from the database), otherwise return false. This method 
	 * only checks for numeric types.
	 */
	public Boolean unchangedNumericness(String scrapedElement) {
		Boolean hasNotChanged = true;
		
		// check type and neighbours
		if (isNumeric(this.sampleData) ) { // should be num
			if (!isNumeric(scrapedElement) ) { //is not num
				hasNotChanged = false;
			}
		} else { // should not be num
			if (isNumeric(scrapedElement) ) { // is num
				hasNotChanged = false;
			}
		}

		return hasNotChanged;
	}
	
	/*
	 * Returns true if the element's data type is date/time, otherwise returns 
	 * false.
	 */
	public Boolean isDateTime(String element) {
		if (Pattern.matches("^[ ]*[0-9]{2}[\\/\\-,.][0-9]{2}[\\/\\-,.](19|20)[0-9]{2}[ ]*$", element)) { //01/01/1998
			return true;
		}
		else if (Pattern.matches("^[ ]*(19|20)[0-9]{2}[\\/\\-,.][0-9]{2}[\\/\\-,.][0-9]{2}[ ]*$", element)) { // 1998/01/01
			return true;
		}
		else if (Pattern.matches("^[ ]*[0-9]{2}[\\/\\-,.][0-9]{2}[\\/\\-,.][0-9]{2}[ ]*$", element)) { // 01/01/98
			return true;
		}
		else if (Pattern.matches("^[ ]*[0-9]{2}[\\/\\-,.][A-Z|a-z]{3}[\\/\\-,.][0-9]{2}[ ]*$", element)) { // 01-JAN-98
			return true;
		}
		else if (Pattern.matches("^[ ]*[0-9]{2}[\\/\\-,.][A-Z|a-z]{3}[\\/\\-,.](19|20)[0-9]{2}[ ]*$", element)) { // 01-JAN-1998
			return true;
		}
		else if (Pattern.matches("^[ ]*[0-9]{2}[\\/\\-,.][A-Z|a-z]{3}[\\/\\-,.](19|20)[0-9]{2} [0-2][0-9]:[0-5][0-9][ ]*$", element)) { // 01-JAN-1998 08:59
			return true;
		}
		else if (Pattern.matches("^[ ]*[0-9]{2}[\\/\\-,.][A-Z|a-z]{3}[\\/\\-,.](19|20)[0-9]{2} [0-2][0-9]:[0-5][0-9]:[0-5][0-9][ ]*$", element)) { // 01-JAN-1998 08:59:00
			return true;
		} //START OF REGEXS MATCHING EXAMPLE WEBSITES
		else if (Pattern.matches("^[ ]*[0-3]{0,1}[0-9]([s|n|r|t][t|d|h]){0,1}[\\/\\-,. ][A-Z|a-z]{3}.{0,1}[\\/\\-,. ](19|20){0,1}[0-9][0-9][ ]*$", element)) { // 1st Jan 2019
			return true;
		}
		else if (Pattern.matches("^[ ]*[0-9]{2}[\\/\\-,.][0-9]{2}[\\/\\-,.](19|20)[0-9]{2}[ ]{0,1}[-]{1}[ ]{0,1}[0-9]{2}[\\/\\-,.][0-9]{2}[\\/\\-,.](19|20)[0-9]{2}[ ]*$", element)) { // 01/01/1999 - 01/01/1999
			return true;
		}
		else if (Pattern.matches("^[ ]*[0-9]{2}[\\/\\-,.][A-Z|a-z]{3}[\\/\\-,.](19|20)[0-9]{2} [0-2][0-9]:[0-5][0-9]:[0-5][0-9].[0-9]*[ ]*$", element)) { // 19-Feb-2019 08:05:33.000000000000000
			return true;
		}
		return false;
	}
	
	/*
 	 * Returns true if the (scraped) element's type has not changed (compared to 
	 * the sample data from the database), otherwise return false. This method 
	 * only checks for date/time types.
	 */
	public Boolean unchangedDateTimeness(String scrapedElement) {
		Boolean hasNotChanged = true;
		
		// check type and neighbours
		if (isDateTime(this.sampleData) ) { // should be num
			if (!isDateTime(scrapedElement) ) { //is not num
				hasNotChanged = false;
			}
		} else { // should not be num
			if (isDateTime(scrapedElement) ) { // is num
				hasNotChanged = false;
			}
		}

		return hasNotChanged;
	}
	
	/*
	 * Returns true if the element is a currency, otherwise returns false.
	 */
	public Boolean isCurrency(String element) {
		String currSymbols = "\\$|US\\$|\\€|\\¥|\\£|A\\$|C\\$|Fr|\\?|kr|NZ\\$|S\\$|HK\\$|R\\$|R";
		String currAbbs = "USD|EUR|JPY|GBP|AUD|CAD|CHF|CNY|SEK|NZD|MXN|SGD|HKD|NOK|KRW|TRY|RUB|INR|BRL|ZAR";
		
		if (Pattern.matches("^[ ]*(" + currSymbols + "|" + currAbbs + "){0,1} {0,1}(\\d+|\\d{1,3}(,\\d{3})*)(\\.\\d+)?[ ]*$", element)) {
			return true;
		}
		if (Pattern.matches("^[ ]*(\\d+|\\d{1,3}(,\\d{3})*)(\\.\\d+)? {0,1}(" + currAbbs + "){0,1}[ ]*$", element)) {
			return true;
		}
		return false;
	}
	
	/*
 	 * Returns true if the (scraped) element's type has not changed (compared to 
	 * the sample data from the database), otherwise return false. This method 
	 * only checks for currency types.
	 */
	public Boolean unchangedCurrencyness(String scrapedElement) {
		Boolean hasNotChanged = true;
		
		// check type and neighbours
		if (isCurrency(this.sampleData) ) { // should be num
			if (!isCurrency(scrapedElement) ) { //is not num
				hasNotChanged = false;
			}
		} else { // should not be num
			if (isCurrency(scrapedElement) ) { // is num
				hasNotChanged = false;
			}
		}

		return hasNotChanged;
	}
	
	/*
	 * Truncates the xpath of the element, removing everything from and including 
	 * the last forward slash "/".
	 */
	public String findParent(String element) {
		int index = element.lastIndexOf("/");
		if (index != 0) {
			return element.substring(0, index);
		}
		return element;
	}
	
	/*
	 * Returns the average depth from the intervention table
	 */
	public int getAverageDepth() {
		Connection conn = null;
		Statement stmt = null;
		int depth = 0;
		try {
			conn = ConnectionManager.getConnection();
			stmt = conn.createStatement();
			String sql = "SELECT AVG(Depth) FROM Intervention";
			
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				depth = rs.getInt("AVG(Depth)") + 1; //Adds 1 to avoid a 0 depth search
			}
			System.out.println("\ttaskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", Average depth from Intervention table: " + depth);
			
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
		return depth;
	}

	/*
	 * Traverse the tree from element, return true if element is found with same 
	 * type, otherwise return false if element not found
	 */
	public Boolean searchAIFind(String element, String elementID, HtmlPage page) {	
		int depth = getAverageDepth();
		int i;
		
		System.out.println("taskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", searchAIFind() called.");

		for (i = 0; i < depth + 1; i++) {
			System.out.println("\ttaskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", depth attempt " + i);
			
			element = findParent(element);
			System.out.println("\ttaskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", parent xpath: " + element);
			
			HtmlElement parent = page.getFirstByXPath(element);
			
			if (parent == null) {
				System.out.println("\ttaskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", Parent not found. Continue.");
				continue;
			}
			
			if (checkElements(parent, i)) {
				return true;
			}
			
			System.out.println("\ttaskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", parent asText(): " + parent.asText());
			System.out.println("\ttaskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", parent has: " + parent.getChildElementCount() + " children");
			
			Iterable<HtmlElement> childList = parent.getHtmlElementDescendants();
			for (DomElement e : childList) {
				System.out.println("\ttaskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", Testing child with asText(): " + e.asText() + ", child id: " + e.getId());
				if (checkElements(e, i)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * Return true if the element is the ID or type we're looking for, otherwise 
	 * return false.
	 */
	public boolean checkElements(DomElement e, int depth) {
		boolean passed = false;
		
		if (elementID != "") {
			if (e.getId().toString().equals(elementID) && unchangedType(e.asText())) {
				passed = true;
			}
		}
		else if (unchangedType(e.asText())) {
				passed = true;
		}
		
		if (passed) {
			System.out.println("\ttaskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", A child element passed, updating XPath and adding intervention.");
			updateXPath(e.getCanonicalXPath(), depth);
			result.setResult(e.asText());
		}
		return passed;
	}
	
	/*
	 * Upon successful AI,
	 * update the xpath in the database,
	 * add the new intervention to the database
	 */
	public void updateXPath(String updatedXPath, int depth) {
		Connection conn = null;
		Statement stmt = null;
		
		this.element = updatedXPath;
		
		try {
			conn = ConnectionManager.getConnection();
			stmt = conn.createStatement();
			
			String sql = "UPDATE Scrape SET Element = \"" + updatedXPath + "\" WHERE ScrapeID = " + this.scrapeID;
			stmt.executeUpdate(sql);
			System.out.println("\ttaskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", Updated Scrape table with new Element XPath.");
			
			sql = "INSERT INTO Intervention (scrapeID, Depth) VALUES (" + scrapeID + "," + depth + ")";
			stmt.executeUpdate(sql);
			System.out.println("\ttaskID: " + this.taskID + ", scrapeID: " + this.scrapeID + ", Inserted AI intervention into Intervention table.");
			
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
