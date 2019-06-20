package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import src.Artist;

/**
 * This test class is used to ensure an Artist object can successfully be created, and the 'name' attribute can be 
 * successfully retrieved from the getName method.
 * @author Jacob Moseley
 *
 */
public class ArtistTest {
	
	@Test
	public void testArtistObjectCreation() {
		Artist artist = new Artist("Jacob Moseley");
		assertEquals("Jacob Moseley", artist.getName());
	}
	
}
