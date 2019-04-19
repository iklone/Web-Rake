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

/**
 * This class consists of methods which service all of the scrapes (from the 
 * Scrape table) associated with a specific task (from the Task table). This 
 * class implements the Runnable interface, hence the instances of this class 
 * are intended to be executed by a thread.
 * 
 * @author psyhh1
 * @author psyjct
 * @see Runnable
 * @see TaskScheduler
 */
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
	
	/**
	 * Constructor
	 * 
	 * @param taskID the taskID of the task to service
	 * @param urlStr the URL of the website associated with the task
	 */
	public ElementSearchThread(int taskID, String urlStr) {
		this.taskID = taskID;
		this.urlStr = urlStr;
		this.page = null;
		this.result = new ScrapeResult();
		this.webClient = createWebClient();
	}

	/**
	 * Attempts to get the page using the WebClient object and urlStr String 
	 * (both stored as member variables in the constructor). If that fails, 
	 * sets the flag to 4 (indicating that the page failed to download) for 
	 * all scrapes associated with the current task in the Scrape table.
	 * 
	 * @throws Exception if the getPage method failed to get the page.
	 * @throws SQLException if there are any database errors when setting the flag.
	 * @throws Exception if there are any Class.forName errors when setting the flag.
	 */
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
	
	/**
	 * Disable annoying HtmlUnit warnings then create and configure the HtmlUnit 
	 * WebClient object.
	 * 
	 * @return webClient the newly created WebClient object.
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
	
	/**
	 * Finds all the scrapes for the task and for each scrape, call scrapeElement()
	 * if the flag is not equal to 2. Otherwise, print an informative message.
	 * 
	 * @throws SQLException if there are any database errors when selecting all 
	 * 		   the scrapes for the task.
	 * @throws Exception if there are any Class.forName errors when selecting all 
	 * 		   the scrapes for the task.
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
	
	/**
	 * Attempt to scrape the element from the page, setting the flag field (in 
	 * Scrape table) and result field (in Result table) for the scrape accordingly.
	 * Firstly, try to call the regularScrapeFind method. If that fails, call 
	 * the searchAIFind() method.
	 * 
	 * If the regularScrapeFind method succeeded, the flag field will be set 
	 * to 0 and the result will be whatever the method found. If the 
	 * regularScrapeFind method failed and the searchAIFind method succeeded, 
	 * the flag field will be set to 1 and the result will be set to whatever the 
	 * AI had found. Otherwise, if both functions failed, the flag field will be 
	 * set to 2 and the result will be a message indicating failure.
	 * 
	 * @throws SQLException if there are any database errors when setting the 
	 * 		   flag or result fields.
	 * @throws Exception if there are any Class.forName errors when setting the 
	 * 		   flag or result fields.
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

	/**
	 * Searches the page for the element and attempts to bypass any consent forms. 
	 * 
	 * If the element isn't found to begin with, this method presumes a consent 
	 * page might be the culprit. It attempts to click past any buttons with 
	 * text that contains a few keywords which commonly appear in accept buttons. 
	 * If it finds one, it clicks the button and searches for the element on the 
	 * page resulting from the click. If an accept button wasn't found, the method 
	 * tries clicking on a specific number of submit buttons and searching the following 
	 * pages.
	 * 
	 * By the end of this, if the element was found, the Result member variable 
	 * is set accordingly to contain the current found element text and a 0 flag.
	 * 
	 * @return true if the element was found, otherwise return false.
	 * @throws Exception if there is an error searching the page.
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
					this.page = tmpPage; // set current page to clicked page
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
	
	/**
	 * Tests the type of the element that is passed in against the type of the 
	 * sample data from the database for the current scrape. This method 
	 * only tests for numeric, date/time and currency types.
	 * 
	 * @param element the element that is being tested.
	 * @return true if the type of the element has not changed otherwise returns
	 * 		    false.
	 */
	public Boolean unchangedType(String element) {
		if (unchangedNumericness(element) && unchangedDateTimeness(element) && unchangedCurrencyness(element)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Tests the type of the element that is passed in using regex matching.
	 * 
	 * @param element the element that is being tested.
	 * @return true if the given element is numeric, otherwise returns false.
	 */
	public Boolean isNumeric(String element) {
		if (Pattern.matches("^[ ]*(\\d+|\\d{1,3}(,\\d{3})*)(\\.\\d+)?[ ]*$", element)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Tests that the type of the element that is passed in hasn't changed from/to 
	 * a numeric type.
	 * 
	 * @param element the element that is being tested.
	 * @return true if the type of the element has not changed it's type from/to 
	 * 			numeric otherwise returns false.
	 */
	public Boolean unchangedNumericness(String scrapedElement) {
		Boolean hasNotChanged = true;
		
		// check type and neighbours
		if (isNumeric(this.sampleData) ) { // scrapedElement should be numeric
			if (!isNumeric(scrapedElement) ) { // scrapedElement is not numeric
				hasNotChanged = false;
			}
		} else { // scrapedElement should not be numeric
			if (isNumeric(scrapedElement) ) { // scrapedElement is numeric
				hasNotChanged = false;
			}
		}

		return hasNotChanged;
	}
	
	/**
	 * Tests the type of the element that is passed in using regex matching.
	 * 
	 * @param element the element that is being tested.
	 * @return true if the type of the given element is date/time, otherwise 
	 * 		   returns false.
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
		} // Start of regular expressions matching examples from websites provided by James Millen
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
	
	/**
	 * Tests that the type of the element that is passed in hasn't changed from/to 
	 * a date/time type.
	 * 
	 * @param element the element that is being tested.
	 * @return true if the type of the element has not changed it's type from/to 
	 * 			date/time otherwise returns false.
	 */
	public Boolean unchangedDateTimeness(String scrapedElement) {
		Boolean hasNotChanged = true;
		
		// check type and neighbours
		if (isDateTime(this.sampleData) ) { // scrapedElement should be date/time
			if (!isDateTime(scrapedElement) ) { // scrapedElement is not date/time
				hasNotChanged = false;
			}
		} else { // scrapedElement should not be date/time
			if (isDateTime(scrapedElement) ) { // scrapedElement is date/time
				hasNotChanged = false;
			}
		}

		return hasNotChanged;
	}
	
	/**
	 * Tests the type of the element that is passed in using regex matching.
	 * 
	 * @param element the element that is being tested.
	 * @return true if the given element is a currency, otherwise returns false.
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
	
	/**
	 * Tests that the type of the element that is passed in hasn't changed from/to 
	 * a currency type.
	 * 
	 * @param element the element that is being tested.
	 * @return true if the type of the element has not changed it's type from/to 
	 * 			currency otherwise returns false.
	 */
	public Boolean unchangedCurrencyness(String scrapedElement) {
		Boolean hasNotChanged = true;
		
		// check type and neighbours
		if (isCurrency(this.sampleData) ) { // scrapedElement should be currency
			if (!isCurrency(scrapedElement) ) { // scrapedElement is not currency
				hasNotChanged = false;
			}
		} else { // scrapedElement should not be currency
			if (isCurrency(scrapedElement) ) { // scrapedElement is currency
				hasNotChanged = false;
			}
		}

		return hasNotChanged;
	}
	
	/**
	 * Truncates the XPath of the element, removing everything from and including 
	 * the last forward slash "/", if the forward slash existed.
	 * 
	 * @param element the XPath of the child.
	 * @return the XPath of the parent if one existed, otherwise returns the 
	 * 		   XPath of the child.
	 */
	public String findParent(String element) {
		int index = element.lastIndexOf("/");
		if (index != 0) {
			return element.substring(0, index);
		}
		return element;
	}
	
	/**
	 * Find the average depth from the Intervention table and increment it by 1.
	 * 
	 * @throws SQLException if there are any database errors when getting the 
	 * 		   average depth.
	 * @throws Exception if there are any Class.forName errors when getting the 
	 * 		   average depth.
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

	/**
	 * Traverses the tree from the element, searching for the nearest element 
	 * with the same HTML id attribute or same type. Traversal takes place by 
	 * incrementally finding the parent and exploring all of its descendants 
	 * until an element is found matching the above criteria or the number of 
	 * parents visited exceeds the average depth from the intervention table.
	 * 
	 * @return true if an element with the same HTML id attribute or same type
	 * 		   found, otherwise return false.
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
	
	/**
	 * Tests whether or not the passed element has the same HTML id attribute or 
	 * type as the element currently being scraped (stored in the member variables).
	 * 
	 * @param e the element to be compared with the current scrape.
	 * @depth the number of parents that have been visited to get to this element.
	 * @return true if the passed element has the same HTML id attribute or type 
	 * 		   as the element currently being scraped.
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
	
	/**
	 * Updates the XPath for the current scrape (in the Scrape table) and 
	 * adds a new record to the Intervention table. This method is called upon 
	 * the searchAIFind method finding a suitable candidate element.
	 * 
	 * @param updatedXPath the new XPath that the scrape will be set to.
	 * @depth the number of parents that have been visited to get to this element.
	 * @throws SQLException if there are any database errors when updating the XPath 
	 * 		   or inserting the new intervention.
	 * @throws Exception if there are any Class.forName errors when updating the 
	 * 		   XPath or inserting the new intervention.
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
