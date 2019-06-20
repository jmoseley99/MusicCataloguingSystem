package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import src.Song;

/**
 * This test class ensures a Song object can successful be created and their getters are behaving as expected.
 * @author Jacob Moseley
 *
 */
public class SongTest {
	@Test
	public void testSongCreation() {
		Song song = new Song("Song 2", 3.24);
		assertEquals("Song 2", song.getName());
		assertEquals(3.24, song.getRuntime(), 0.00);
	}
	
}
