package src;

import java.awt.TextArea;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import com.mysql.jdbc.PreparedStatement;

/**
 * This is the AlbumQuery class which interfaces the database layer to the UI layer by executing queries and taking in user inputs.
 * @author Jacob Moseley
 *
 */
public class AlbumQuery implements QueryI{
	/*
	 * dbConnection: This classes connection to the database passed in through a constructor, limiting the number of connections the system is making 
	 * to the database. 
	 * stmt: A statement object that can be used throughout the class in order to execute a query, without having to make multiple Statement objects
	 * whenever they are needed. 
	 */
	private DatabaseConnection dbConnection;
	private Statement stmt;
	
	public AlbumQuery(DatabaseConnection connection) {
		this.dbConnection = connection;
		try {
			this.stmt = dbConnection.getConnection().createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method allows the user to view all of the albums created by a certain artist. The method queries the database, selecting the 
	 * details about all of the albums where the artistID attribute matches the artist's name. The helper method 'fetchResults' is then
	 * used to organise and format the albums.
	 * 
	 * @param artist
	 * 			The string stored in the JComboBox on the UI.
	 * @return noAlbumFound
	 * 			A string of albums by a certain artist if possible to retrieve.
	 */
	public String viewAlbumByArtist(String artist) {
		String noAlbumFound = "Albums could not be retrieved!";
		String query = "SELECT name, genre, format, runtime, year FROM album WHERE artistid IN"
				+ "(SELECT artistID FROM artist WHERE name = '" + artist + "');";
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
			return fetchResults(rs, artist);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Result Set could not be retrieved");
		}
		return noAlbumFound;
	}
	
	/**
	 * This method allows the user to add a new album into their music collection. It creates a PreparedStatement object in order
	 * to add the information. This is done to avoid any potential injection hacks. 
	 * 
	 * @param name, genre, format, runtime, year
	 * 			The details required in order to create a new album within the database.
	 * @param artist
	 * 			The artist's name who created the album in order to avoid maintain database consistency.
	 * @return 
	 * 			True if album is successfully added, False otherwise.
	 */
	public boolean add(String name, String genre, String format, double runtime, int year, String artist) {
		try {
			Album album = new Album(name, format, genre, runtime, year);
			String query = "INSERT INTO album(name, format, genre, runtime, year, artistid) VALUES(?, ?, ?, ?, ?, ?)";
			PreparedStatement insertStatement;
			insertStatement = (PreparedStatement) dbConnection.getConnection().prepareStatement(query);
			insertStatement.setString(1, album.getName());
			insertStatement.setString(2, album.getFormat());
			insertStatement.setString(3, album.getGenre());
			insertStatement.setDouble(4, album.getRuntime());
			insertStatement.setInt(5, album.getYear());
			insertStatement.setInt(6, getArtistID(artist));
			insertStatement.execute();
			insertStatement.close();
			return true;
		} catch (SQLException e1) {
			return false;
		}
	}
	
	/**
	 * This implemented method from the QueryI interface allows the user to remove an album from their collection. Two PreparedStatemnet
	 * objects are created to safely remove the data from the database. An album's songs must be removed before the album itself in order
	 * to avoid any foreign key restraints throwing errors. 
	 * 
	 * @param userInput
	 * 			The string stored in the JComboBox specifying which album is to be removed.
	 * @return 	
	 * 			True if successfully removed, False otherwise. 
	 */
	@Override
	public boolean remove(String userInput) {
		String removeAlbumQuery = "DELETE FROM album WHERE name =?;";
		try {
			PreparedStatement removeAlbum = (PreparedStatement) dbConnection.getConnection().prepareStatement(removeAlbumQuery);
			removeAlbum.setString(1, userInput);
			removeAlbum.execute();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * This method allows the user to view all of the Albums stored in their collection.
	 * 
	 * @return
	 * 			The formatted string of the user's albums.
	 */
	@Override
	public String viewAll() {
		try {
			String query = "SELECT name, format, genre, runtime, year FROM album;";
			ResultSet rs = stmt.executeQuery(query);
			return fetchResults(rs, null);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * This private method uses the ResultSet object and creates a list of Album objects, each with their own name, format, genre,
	 * runtime and year. This method can be reused across the class whenever information needs to be retrieved. 
	 * The albums list is then passed into convertResults and then returned.
	 * 
	 * @param rs
	 * 			ResultSet containing the information retrieved from the database.
	 * @param artist
	 * 			The album creator's name.
	 * @return
	 * 			A string of the albums. 
	 */
	public String fetchResults(ResultSet rs, String artist){
		ArrayList<Album> albums = new ArrayList<Album>();
		try {
			while(rs.next()) {
				try {
					albums.add(new Album(rs.getString("name"), rs.getString("format"), rs.getString("genre"), rs.getDouble("runtime"), rs.getInt("year")));
				} catch (SQLException e) {
					return "SQL Exception!";
				}					
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "SQL Exception";
		}
		
		return convertQuery(albums, artist);
	}
	
	/**
	 * This private helper method converts the list of Album objects into a readable and friendly string to then be shown on the UI. 
	 * The method loops through each Album artist and converts the details of that album into a block of words with the help of the getters
	 * within the Album class. If the artist parameter is null, the name will not be added to the string.
	 * 
	 * @param albums
	 * 			The list of album objects that are to be converted into a string.
	 * @param artist
	 * 			The optional parameter to show which artist created the album(s).
	 * @return albumText.toString
	 * 			The string of formatted album and details.
	 */
	private String convertQuery(ArrayList<Album> albums, String artist) {
		StringBuilder albumText = new StringBuilder();
		int counter = 1;
		
		if (artist != null) {
			albumText.append(artist + "\n\n");
		}
		for (Album album : albums) {
			albumText.append(counter + "\n");
			albumText.append("Name: " + album.getName() + "\n");
			albumText.append("Runtime: " + album.getRuntime() + "\n");
			albumText.append("Genre: " + album.getGenre() + "\n");
			albumText.append("Year: " + album.getYear() + "\n");
			albumText.append("Format: " + album.getFormat() + "\n\n");
			counter++;
		}
		return albumText.toString();
	}
	
	/**
	 * This private helper method quickly retrieves the ArtistID value stored in the database from a given artist's name. This method
	 * is needed when creating a new album and helps to keep the code neat.
	 * 
	 * @param artist
	 * 			The name of the artist thats ID needs to be found
	 * @return artistID
	 * 			The integer value of the artist's id. 
	 */
	private int getArtistID(String artist)  {
		int artistID = 0;
		String query = "SELECT artistid FROM artist WHERE name = '" + artist + "';";
		ResultSet rs;
		try {
			rs = stmt.executeQuery(query);
			while(rs.next()) {
				artistID = rs.getInt("artistid");
			}
		} catch (SQLException e) {
			System.out.println("ArtistID could not be retrieved");
		}
		return artistID;
	}
	
	/**
	 * This method is used with the UI in order to populate any JComboBox with album names.
	 * 
	 * @return albumList
	 * 			The list of album names.
	 */
	public ArrayList<String> getAlbums() {
		String query = "SELECT name FROM album";
		ArrayList<String> albumList = new ArrayList<String>();
		try {
			ResultSet albums = stmt.executeQuery(query);
			while (albums.next()) {
				albumList.add(albums.getString("name"));
			}
			for(String album : albumList) {
				System.out.println(album + " GET ALBUMS NAME");
			}
			return albumList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * This method is used to order the albums alphabetically. It queries the database for all of the details about an album,
	 * with the additional 'ORDER BY' command.
	 * 
	 * @param tArea
	 * 			The text area used to check if the user has first chosen to view all of their albums.
	 * @return
	 * 			The formatted string of albums
	 */
	@Override
	public String sortAlphabetical(TextArea tArea) {
		if (!tArea.getText().isEmpty()) {
			String query = "SELECT name, format, genre, runtime, year FROM album ORDER BY name ASC";
			try {
				ResultSet rs = stmt.executeQuery(query);
				return fetchResults(rs, null);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "Click 'View All' first!";
	}
	
}
