import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class ElementScraper {
	
	private URL url;
	private InputStream is;
	BufferedReader br;
	String line;
	
	//Double array to store scrape ID AND scrape name so that we can store the result using the scrape ID
	public ElementScraper(String urlStr, String[][] elements) {
		try {
			url = new URL(urlStr);
			is = url.openStream();
			br = new BufferedReader(new InputStreamReader(is));
			
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		}
		catch (MalformedURLException mue) {
			mue.printStackTrace();
		}
		catch (IOException ioe) {
			
		}
		finally {
			try {
				if (is != null) is.close();
			}
			catch (IOException ioe) {
				// nothing to see here
			}
		}
	}
}
