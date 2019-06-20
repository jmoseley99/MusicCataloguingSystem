package src;

import java.awt.TextArea;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import com.mysql.jdbc.PreparedStatement;

/**
 * This is the ArtistQuery class which interfaces the database layer to the UI layer by executing queries and taking in user inputs.
 * @author Jacob Moseley
 *
 */
public class SongQuery implements QueryI {
	
	/*
	 * dbConnection: This classes connection to the database passed in through a constructor, limiting the number of connections the system is making 
	 * to the database. 
	 * stmt: A statement object that can be used throughout the class in order to execute a query, without having to make multiple Statement objects
	 * whenever they are needed. 
	 */
	private DatabaseConnection dbConnection;
	private Statement stmt;
	
	public SongQuery(DatabaseConnection connection) {
		this.dbConnection = connection;
		try {
			this.stmt = dbConnection.getConnection().createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method allows the user to add a song to their collection using a PreparedStatemnt object in order to stop potential injection
	 * hacks. 
	 * @param name, runtime
	 * 			The details required to insert a new song within the user's collection
	 * @param album
	 * 			The string stored within the JComboBox populated with albums.
	 * @return 
	 * 			True if the song was added, False otherwise.
	 */
	public boolean add(String name, double runtime, String album) {
		Song newSong = new Song(name, runtime);
		if (!name.matches("^[a-zA-Z0-9 -]+$") || !(runtime > 0)) {
			return false;
		}
		String query = "INSERT INTO song(name, length, albumid) VALUES(?, ?,?)";
		PreparedStatement insertStatement;
		try {
			insertStatement = (PreparedStatement) dbConnection.getConnection().prepareStatement(query);
			insertStatement.setString(1, newSong.getName());
			insertStatement.setDouble(2, newSong.getRuntime());
			insertStatement.setDouble(3, getAlbumID(album));
			insertStatement.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * This method allows the user to remove a song from their collection. As a song is at the bottom of the table stack, nothing
	 * else needs to be removed before removing a song as there will be no foreign key restraints.
	 *  
	 * @param userInput
	 * 			The song name that is to be removed. 
	 * @return 
	 * 			True if the song is removed, False otherwise.
	 */
	@Override
	public boolean remove(String userInput) {
		String songName = userInput, query = null;
		query = "DELETE FROM song WHERE name = '" + songName + "';";
		
		if(!checkIfSongExists(songName)) {
			return false;
		}
		try {
			PreparedStatement delete = (PreparedStatement) dbConnection.getConnection().prepareStatement(query);
			delete.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This method allows the user to view all of the songs stored within their collection. 
	 * The ResultSet object created from the query is passed into fetchResults where it is then converted and formatted to be returned
	 * to the UI. 
	 * 
	 * @return
	 * 			The formatted string of songs the user has stored in their collection. If the songs could not be retrieved, the return
	 * 			string will say so.
	 */
	@Override
	public String viewAll() {
		try {
			String query = "SELECT name, length FROM song;";
			ResultSet rs = stmt.executeQuery(query);
			return fetchResults(rs, null);

		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		return "Songs could not be retrieved!";
	}
	
	/**
	 * This private helper method converts the ResultSet filled with queried data into a final string that can be printed on the UI.
	 * New Song objects are created with the information from the database and is passed into convertResults to properly format. 
	 * If Song objects cannot be created, the console will be told. 
	 * 
	 * @param rs
	 * 			The result set of song information
	 * @param artistName
	 * 			The optional input of the artist's name who created the songs.
	 * @return
	 * 			If all successful, the formatted string of songs. Otherwise the user will be told the song could not be retrieved.
	 */
	private String fetchResults(ResultSet rs, String artistName){
		ArrayList<Song> songs = new ArrayList<Song>();
		try {
			while(rs.next()) {
				try {
					songs.add(new Song(rs.getString("name"), rs.getDouble("length")));	
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println("Could not add albums");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return convertResults(songs, artistName);
	}
	
	/**
	 * This private method converts a list of Song objects and an optional artist name into a readable string which can be put onto the
	 * UI for the user to see. It checks to see if the artist's name is null as it is not , mainly being used for 
	 * searching songs by a particular artist. If not, the extra line is added. With the help of the getter methods in the Song class, 
	 * a easy to read string can be created.
	 * 
	 * @param songs
	 * 			The list of Song objects for the method to convert to a string
	 * @param optionalInput
	 * 			The optional string indicating which artist has created each song, or which album the songs reside on, depending in which
	 * 			method it is called.
	 * @return songString.toString
	 * 			The formatted string to then be printed on the UI.
	 */
	private String convertResults(ArrayList<Song> songs, String optionalInput) {
		StringBuilder songString = new StringBuilder();
		int counter = 1;
		
		if (optionalInput!=null) {
			songString.append("_______" + optionalInput + "_______ \n\n");
		}
		for (Song song : songs) {
			songString.append(counter+"\n");
			songString.append("Name: " + song.getName() + "\n");
			songString.append("Runtime: " + song.getRuntime()+ "\n\n");
			counter++;
		}
		return songString.toString();
	}
	
	/**
	 * This private helper method quickly returns the albumID attribute from a row of data given the album's name, returning the first AlbumID
	 * integer the ResultSet rs contains.
	 * 
	 * @param album
	 * 			The name of the album that ID is needed.
	 * @return albumId
	 * 			The found ID of that album.
	 */
	private int getAlbumID(String album) {
		int albumID = 0;
		try {
			String query = "SELECT albumid FROM album WHERE name = '" + album + "';";
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()) {
				albumID = rs.getInt("albumid");
				return albumID;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * This method is used to allow the user to see all of the songs stored in the collection made by a certain artist. A ResultSet object
	 * is passed into fetchResults and returned in a formatted string. This artist string is then passed into fetchResults with the result set
	 * so the user can see the artist's name that they have chosen.
	 * 
	 * @param artist
	 * 				The string stored within the JComboBox which allows the user to choose an artist.
	 * @return 
	 * 				The formatted string if available, otherwise a string saying the songs could not be retrieved.
	 */
	public String viewSongByArtist(String artist) {
		String songQuery = "SELECT name, length FROM song WHERE albumID IN (SELECT albumID from album WHERE ArtistID = (SELECT artistid FROM artist WHERE name = '" + artist + "'));";
		ResultSet rs;
		try {
			rs = stmt.executeQuery(songQuery);
			return fetchResults(rs, artist);
		}catch(SQLException e) {
			e.printStackTrace();
			return "Could not retrieve songs!";
		}
	}
	
	/**
	 * This method is used to allow the user to see all of the songs stored in the collection that are on a certain album. A ResultSet object
	 * is passed into fetchResults and returned in a formatted string. This album string is then passed into fetchResults with the result set
	 * so the user can see the album's name that they have chosen.
	 * 
	 * @param album
	 * 				The string stored within the JComboBox which allows the user to choose an album.
	 * @return 
	 * 				The formatted string if available, otherwise a string saying the songs could not be retrieved.
	 */
	public String viewSongByAlbum(String album) {
		String songQuery = "SELECT name, length FROM song WHERE albumID =(SELECT albumID FROM album WHERE name = '"+  album  +"');";
		ResultSet rs;
		try {
			rs = stmt.executeQuery(songQuery);
			return fetchResults(rs, album);
		} catch (SQLException e) {
			e.printStackTrace();
			return "Could not retrieve songs!";
		}
		
	}
	
	/**
	 * This method is used with the UI thread in order to populate JComboBoxes that show the song names.
	 * 
	 * @return
	 * 			A list of song names.
	 */
	public ArrayList<String> getSongs(){
		String query = "SELECT name FROM song";
		ArrayList<String> songList = new ArrayList<String>();
		try {
			ResultSet songs = stmt.executeQuery(query);
			while (songs.next()) {
				songList.add(songs.getString("name"));
			}
			return songList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * This method is used when another method needs to know if a song currently exists within the collection. If 
	 * the query has a result from selecting the songs name, the song exists. If not, the method will return false.
	 * 
	 * @param song
	 * 			The song name to check.
	 * @return
	 * 			True if found, False otherwise.
	 */
	private boolean checkIfSongExists(String song) {
		ResultSet rs;
		try {
			rs = stmt.executeQuery("SELECT name FROM Song WHERE name = '" + song + "';");
			if(rs.next()) {
				return true;
			}
		}catch(SQLException e) {
			return false;
		}
		return false;
	}
	
	/**
	 * This method is used when the user chooses to sort the songs stored in their collection alphabetically. It queries
	 * the database with an additional 'ORDER BY' command.
	 * 
	 * @param tArea
	 * 			The text area where the artists will be displayed, used to check if is empty before trying to sort.
	 * @return 
	 * 			The string of ordered songs.
	 */
	@Override
	public String sortAlphabetical(TextArea tArea) {
		if (!tArea.getText().isEmpty()) {
			String query = "SELECT name, length FROM song ORDER BY name ASC";
			ResultSet rs;
			try {
				rs = stmt.executeQuery(query);
				return fetchResults(rs, null);
			} catch (SQLException e) {
				return "Songs could not be sorted!";
			}
			
		}
		return "Click the 'View All' button first!";
	}
}
