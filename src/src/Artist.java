package src;

/**
 * This class is used when artists are retrieved from the database. This means there is a relationship between the user interface layer and the 
 *  database layer. 
 * @author Jacob Moseley
 *
 */
public class Artist {
	private String name = null;
	
	public Artist(String name) {
		this.name = name;
	}
	
	/**
	 * This method returns the artists name
	 * 
	 * @return this.name
	 * 			The artist's name.
	 */
	public String getName() {
		return this.name;
	}
}