import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestElementSearchThread {

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
	private final PrintStream originalOut = System.out;
	private final PrintStream originalErr = System.err;

	@Before
	public void setUpStreams() {
	    System.setOut(new PrintStream(outContent));
	    System.setErr(new PrintStream(errContent));
	}
	
	@After
	public void restoreStreams() {
	    System.setOut(originalOut);
	    System.setErr(originalErr);
	}
	
	@Test
	public void testGetValueCorrectly() {
		String url = "http://avon.cs.nott.ac.uk/~psyjct/main.php";
		ElementSearchThread est = new ElementSearchThread(3, url);
		ScrapeResult result = est.getJSoupResult("html body h1");
		assertTrue(result.element.toString().contains("Web Scrape"));
		assertEquals(result.getFlag(), 0);
	}
	
	@Test
	public void testGetValueAIFlag() {
		String url = "http://avon.cs.nott.ac.uk/~psyjct/main.php";
		ElementSearchThread est = new ElementSearchThread(3, url);
		ScrapeResult result = est.getJSoupResult("html body h1");
		assertEquals(result.getFlag(), 1);	
	}
	
	@Test
	public void testGetValueHumanFlag() {
		String url = "http://avon.cs.nott.ac.uk/~psyjct/main.php";
		ElementSearchThread est = new ElementSearchThread(3, url);
		ScrapeResult result = est.getJSoupResult("html body h1");
		assertEquals(result.getFlag(), 2);	
	}
	
	
	
	
	
	

}
