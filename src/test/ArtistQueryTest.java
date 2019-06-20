package test;

import org.junit.Test;

import src.ArtistQuery;
import src.DatabaseConnection;


/**
 * This class is used to test the ArtistQuery class when a new ArtistQuery object is created. If passed, I know the object
 * is available to be used whenever needed across the application. 
 * @author Jacob Moseley
 *
 */
public class ArtistQueryTest {
	
	@Test
	public void testArtistQueryObjectCreation() {
		DatabaseConnection conn = DatabaseConnection.getInstance();
		ArtistQuery artist = new ArtistQuery(conn);	
	}
	
}
