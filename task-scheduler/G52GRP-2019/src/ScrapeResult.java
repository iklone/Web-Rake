/**
 * This class is intended to act as a c-like struct, consisting of two member 
 * variables which align with each of our scrapes in the Scrape table - the value 
 * and the flag.
 * 
 * @author psyhh1
 * @author psyjct
 * @see Runnable
 * @see ElementSearchThread
 */

public class ScrapeResult {
	int flag;
	String element;
	
	/**
	 * Constructor automatically sets the element member variable to null.
	 */
	public ScrapeResult() {
		this.element = null;
	}

	/**
	 * @return the value of the flag member variable.
	 */
	public int getFlag() {
		return flag;
	}

	/**
	 * Sets the flag member variable to the new value that's passed in.
	 * 
	 * @param flag the new flag.
	 */
	public void setFlag(int flag) {
		this.flag = flag;
	}

	/**
	 * @return the value of the element member variable.
	 */
	public String getElement() {
		return element;
	}

	/**
	 * Sets the element member variable to the new value that's passed in.
	 * 
	 * @param element the new element.
	 */
	public void setResult(String element) {
		this.element = element;
	}
}
