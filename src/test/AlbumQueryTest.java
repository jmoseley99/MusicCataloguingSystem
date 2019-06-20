package test;
import org.junit.Test;

import src.AlbumQuery;
import src.DatabaseConnection;

/**
 * This class tests object creation for an AlbumQuery object. This ensures that an AlbumQuery object can be used created
 * successfully and used across the applicaiton. 
 * @author Jacob Moseley
 *
 */
public class AlbumQueryTest {
	
	@Test
	public void testAlbumQueryCreation() {
		DatabaseConnection connection = DatabaseConnection.getInstance();
		AlbumQuery album = new AlbumQuery(connection);
	}
}
