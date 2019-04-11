import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlHeading1;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

public class TestHTMLUnit {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		try (final WebClient webClient = new WebClient()) {
		    webClient.getOptions().setThrowExceptionOnScriptError(false);
		    
			// turn off htmlunit warnings
			java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		    java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
		    
	        HtmlPage page = webClient.getPage("https://finance.yahoo.com/");
			page.getEnclosingWindow().getJobManager().waitForJobs(1000);
	        
	        System.out.println(page.asXml());
	        
	        HtmlElement htmlElement;
	        if ((htmlElement = (HtmlElement) page.getFirstByXPath("//*[@id=\"market-summary\"]/div/div[1]/div[1]/ul/li[2]/h3/span")) == null) {
		        List<HtmlElement> submitButtons = page.getByXPath("//button[@type='submit']");
		        submitButtons.addAll(page.getByXPath("//input[@type='submit']"));
		        
				for (HtmlElement e : submitButtons) {
					System.out.println(e.asText());
					// Can we check each button innerHTML for 'OK' or 'I accept' or 'Accept' or ... ?
					page = e.click();
					if ((htmlElement = (HtmlElement) page.getFirstByXPath("//*[@id=\"market-summary\"]/div/div[1]/div[1]/ul/li[2]/h3/span")) != null) {
						break;
					}
				}
	        }
	        
	        if ((htmlElement = (HtmlElement) page.getFirstByXPath("//*[@id=\"market-summary\"]/div/div[1]/div[1]/ul/li[2]/h3/span")) != null) {
	        	System.out.println(htmlElement.asText());
	        }

		}
	}

}
