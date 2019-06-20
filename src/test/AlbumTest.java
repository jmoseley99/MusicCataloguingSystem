package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import src.Album;

/**
 * This class tests that an Album object can be created, and all information successful retrieved.
 * @author Jacob Moseley
 *
 */
public class AlbumTest {
	@Test
	public void testAlbumObjectCreation() {
		Album album = new Album("Let's Dance", "MP3", "Dance", 18.30, 2019);
		assertEquals("Let's Dance", album.getName());
		assertEquals("Dance", album.getGenre());
		assertEquals("MP3", album.getFormat());
		assertEquals(18.30, album.getRuntime(),.00);
		assertEquals(2019, album.getYear());
	}
	
}
