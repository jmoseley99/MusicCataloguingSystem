package src;

import java.awt.TextArea;

public interface QueryI {
	/*
	 * This is the interface used by the ArtistQuery, AlbumQuery and SongQuery classes. It holds the remove method, viewAll method 
	 * and sortAlphabetical method. By using an interface, the three classes can implement these three methods in different ways.
	 */
	public boolean remove(String userInput);
	
	public String viewAll();
	
	public String sortAlphabetical(TextArea tArea);
}
