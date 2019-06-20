package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This test class is responsible for testing all of the previous test classes, ensuring they all run together. 
 * @author Jacob Moseley
 *
 */
public class TestAll {
	
	@RunWith(Suite.class)
	@Suite.SuiteClasses({ AlbumQueryTest.class, AlbumTest.class, ArtistQueryTest.class, ArtistTest.class, DatabaseConnectionTest.class,
		SongQueryTest.class, SongTest.class})
	public class AllTests {
	}
}
