package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.Test;

import src.AlbumQuery;
import src.ArtistQuery;
import src.DatabaseConnection;
import src.SongQuery;

/**
 * This test class is in charge of thoroughly testing all of the methods in the SongQuery class. Edge case tests are also made
 * to ensure errors can be dealt with in correct ways. 
 * 
 * PLEASE READ:
 * 	These tests must be tested on an empty database. If not, the assert statements checking text will not pass as there will
 * 	be other items of data included with the return strings when testing the songs, artists and albums. Thank you. 
 * @author Jacob Moseley
 *
 */

public class SongQueryTest {
	/**
	 * These four attributes are used across the class in order to access the methods stored within the classes. As object creation
	 * has already been tested on DatabaseConnection, AlbumQuery and ArtistQuery, I can assume they will work as expected
	 * throughout this test class.
	 */
	DatabaseConnection conn = DatabaseConnection.getInstance();
	SongQuery song = new SongQuery(conn);
	AlbumQuery album = new AlbumQuery(conn);
	ArtistQuery artist = new ArtistQuery(conn);
	
	/*
	 * This tests the add() method within the SongQuery class. It creates a new artist, album and song, then asserts the method
	 * which adds a song returns true, indicating the song has been added. The artist is then removed from the database, along with
	 * the album and song in order to keep the database neutral.
	 */
	@Test
	public void testAdd() {
		String albumName = String.valueOf(UUID.randomUUID());
		String artistName = String.valueOf(UUID.randomUUID());
		int year = (int) (Math.random()*1000);
		double AlbumRuntime = (double)Math.random()*100, songRuntime = (double)Math.random()*100;
		String genre = "Dance", format = "MP3";
		String songName = String.valueOf(UUID.randomUUID());
		
		artist.add(artistName);
		album.add(albumName, genre, format, AlbumRuntime, year, artistName);
		assertTrue(song.add(songName, songRuntime, albumName));
		artist.remove(artistName);
	}
	
	/*
	 * This tests the remove() method within the SongQuery class. It creates a new artist, album and song, then adds the song
	 * to the database. It then asserts the method which removes the song returns true, indicating the song has been removed
	 * correctly. The artist is then removed, removing the album too.
	 */
	@Test
	public void testRemove() {
		String albumName = String.valueOf(UUID.randomUUID());
		String artistName = String.valueOf(UUID.randomUUID());
		int year = (int) (Math.random()*1000);
		double AlbumRuntime = (double)Math.random()*100, songRuntime = (double)Math.random()*100;
		String genre = "Country", format = "CD";
		String songName = String.valueOf(UUID.randomUUID());
		
		artist.add(artistName);
		album.add(albumName, genre, format, AlbumRuntime, year, artistName);
		song.add(songName, songRuntime, albumName);
		
		assertTrue(song.remove(songName));
		artist.remove(artistName);
	}
	
	/*
	 * THis tests the viewAll() method within the SongQuery class. THis test ensures
	 * that the correct songs are shown to the user when choosing to view all. It creates a new artist, album and two songs. They are all
	 * then added to the database, and an assertion is made to ensure the correct output string matches the details of the songs. 
	 * Finally, the artist is removed, removing the album and songs with it.
	 */
	@Test
	public void testViewAll() {
		String albumName = String.valueOf(UUID.randomUUID());
		String artistName = String.valueOf(UUID.randomUUID());
		int year = (int) (Math.random()*1000);
		double AlbumRuntime = (double)Math.random()*100, songRuntime = 3.14;
		String genre = "Country", format = "CD";
		String songName = String.valueOf(UUID.randomUUID()), songName2 = String.valueOf(UUID.randomUUID());
		
		artist.add(artistName);
		album.add(albumName, genre, format, AlbumRuntime, year, artistName);
		song.add(songName, songRuntime, albumName);
		song.add(songName2, songRuntime, albumName);
		
		assertEquals("1\nName: " + songName + "\nRuntime: " + songRuntime + "\n\n2\nName: " + songName2 + 
				"\nRuntime: " + songRuntime+"\n\n", song.viewAll());
		artist.remove(artistName);
	}
	
	/*
	 * This tests the viewByArtist() method in the SongQuery class. This test ensures that a user can view the songs created by a 
	 * specific artist. It creates a new artist, album and two songs. Then the artist is specified to the method to ensure 
	 * the two song are correctly displayed. 
	 */
	@Test
	public void testViewSongByArtist() {
		String albumName = String.valueOf(UUID.randomUUID());
		String artistName = String.valueOf(UUID.randomUUID());
		int year = (int) (Math.random()*1000);
		double AlbumRuntime = (double)Math.random()*100, songRuntime = 3.14;
		String genre = "Country", format = "CD";
		String songName = String.valueOf(UUID.randomUUID()), songName2 = String.valueOf(UUID.randomUUID());
		
		artist.add(artistName);
		album.add(albumName, genre, format, AlbumRuntime, year, artistName);
		song.add(songName, songRuntime, albumName);
		song.add(songName2, songRuntime, albumName);
		
		assertEquals("_______"+artistName+"_______ \n\n1\nName: " + songName + "\nRuntime: " + songRuntime + "\n\n2\nName: " + songName2 + 
				"\nRuntime: " + songRuntime+"\n\n", song.viewSongByArtist(artistName));
		artist.remove(artistName);
	}
	
	/*
	 * This tests the viewByAlbum() method in the SongQuery class. This test ensures that a user can view the songs on a 
	 * specific album. It creates a new artist, album and two songs. Then the album is specified to the method to ensure 
	 * the two song are correctly displayed. An assertion is made to ensure the returned string matches the expected string
	 * to be shown to the user. 
	 */
	@Test
	public void testViewSongByAlbum() {
		String albumName = String.valueOf(UUID.randomUUID());
		String artistName = String.valueOf(UUID.randomUUID());
		int year = (int) (Math.random()*1000);
		double AlbumRuntime = (double)Math.random()*100, songRuntime = 3.14;
		String genre = "Country", format = "CD";
		String songName = String.valueOf(UUID.randomUUID()), songName2 = String.valueOf(UUID.randomUUID());
		
		artist.add(artistName);
		album.add(albumName, genre, format, AlbumRuntime, year, artistName);
		song.add(songName, songRuntime, albumName);
		song.add(songName2, songRuntime, albumName);
		
		assertEquals("_______"+albumName+"_______ \n\n1\nName: " + songName + "\nRuntime: " + songRuntime + "\n\n2\nName: " + songName2 + 
				"\nRuntime: " + songRuntime+"\n\n", song.viewSongByAlbum(albumName));
		artist.remove(artistName);
	}
	
	/*
	 * This tests the getSongs() method in the SongQuery class. This test ensures the JComboBoxes on the UI are able to be populated
	 * correctly in order to show the user the songs stored in their collection when choosing to remove one. This test creates an artist,
	 * album and two songs. The method is then tested against a new list with the same song names to ensure they can be retrieved
	 * correctly. 
	 */
	@Test
	public void testGetSongs() {
		String albumName = String.valueOf(UUID.randomUUID());
		String artistName = String.valueOf(UUID.randomUUID());
		int year = (int) (Math.random()*1000);
		double AlbumRuntime = (double)Math.random()*100, songRuntime = 3.14;
		String genre = "Country", format = "CD";
		String songName = String.valueOf(UUID.randomUUID()), songName2 = String.valueOf(UUID.randomUUID());
		ArrayList<String> songList = new ArrayList<String>();
		
		artist.add(artistName);
		album.add(albumName, genre, format, AlbumRuntime, year, artistName);
		song.add(songName, songRuntime, albumName);
		song.add(songName2, songRuntime, albumName);
		songList.add(songName);
		songList.add(songName2);
		
		assertEquals(songList, song.getSongs());
		
		artist.remove(artistName);
	}
	
	/*
	 * This tests the add() method in the SongQuery class, but attempts to add a song to an album that does not exist within
	 * the collection. An album name is created along with a song, but the album is not previously added. The method
	 * should return false as a valid album is needed to create a song.
	 */
	@Test
	public void testAddNoAlbum() {
		String albumName = String.valueOf(UUID.randomUUID());
		String songName = String.valueOf(UUID.randomUUID());
		double songRuntime = 3.14;
		
		assertFalse(song.add(songName, songRuntime, albumName));
	}
	
	/*
	 * This tests the add() method in the SongQuery class, but attempts to add a song without a name and with
	 * a negative runtime. A valid album and artist is added to isolate the false return to the song.
	 */
	@Test
	public void testAddIncorrectInput() {
		String albumName = String.valueOf(UUID.randomUUID());
		String songName = "";
		double songRuntime = -3.2;
		String artistName = String.valueOf(UUID.randomUUID());
		int year = (int) (Math.random()*1000);
		double AlbumRuntime = (double)Math.random()*100;
		String genre = "Country", format = "CD";
		
		artist.add(artistName);
		album.add(albumName, genre, format, AlbumRuntime, year, artistName);
		assertFalse(song.add(songName, songRuntime, albumName));
		
		artist.remove(artistName);
	}
	
	/*
	 * This tests the remove() method in the SongQuery class, but attempts to remove a song that isn't in the collection.
	 * In the real application, the user can only choose from songs that do exists, but still important to test.
	 */
	@Test
	public void testRemoveIncorrectSong() {
		String songName = String.valueOf(UUID.randomUUID());
		assertFalse(song.remove(songName));
	}
	
	/*
	 * This tests the getSongs() method in the SongQuery class, but expects the list of names to be empty when the database
	 * is empty. This test is made to ensure no errors occur when the user first loads the application. An empty list is created
	 * and compared against the getSongs() method with no songs being added.
	 */
	@Test
	public void testGetSongsNoSongs() {
		ArrayList<String> noSongList = new ArrayList<String>();
		assertEquals(noSongList, song.getSongs());
	}
}
