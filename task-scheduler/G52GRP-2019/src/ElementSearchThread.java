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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
	   new ElementSearchThread(2, "https://www.nottingham.ac.uk");
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
		try {
			Class.forName(JDBC_DRIVER);
		  
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
		  
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT * FROM Scrape WHERE Scrape.taskID = " + taskID;
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				scrapeID = rs.getInt("scrapeID");
				element = rs.getString("Element");
				
				String value = getValue(element);
				
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
	
	public void getValue(String element) {
		try {
			//obtain Document somehow, doesn't matter how
			DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			org.w3c.dom.Document doc = b.parse(new FileInputStream(this.taskID + ".html"));
	
			//Evaluate XPath against Document itself
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodes = (NodeList)xPath.evaluate(element,
			        doc, XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); ++i) {
			    Element e = (Element) nodes.item(i);
			}
		}
		catch (Exception e) {
			
		}
		finally {
			
		}
	}
}
