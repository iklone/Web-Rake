import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
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
			
		    final HtmlPage page = webClient.getPage("https://nottingham.ac.uk");
			
			page.getEnclosingWindow().getJobManager().waitForJobs(1000);
			
			List list = page.getByXPath("//*[@id=\"form1\"]/div[2]/div[2]/footer/div[1]/div[1]/address/p[1]/span[3]/span");
			
			for (Object o : list) {
				if(o instanceof HtmlElement) {
					HtmlElement e = (HtmlElement)o;
					System.out.println(e.getTextContent());
				}
			}

		}
	}

}
