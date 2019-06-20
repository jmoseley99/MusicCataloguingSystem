package src;

import java.awt.TextArea;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mysql.jdbc.PreparedStatement;

/**
 * This is the ArtistQuery class which interfaces the database layer to the UI layer by executing queries and taking in user inputs.
 * @author Jacob Moseley
 *
 */
public class ArtistQuery implements QueryI{
	/*
	 * dbConnection: This classes connection to the database passed in through a constructor, limiting the number of connections the system is making 
	 * to the database. 
	 * stmt: A statement object that can be used throughout the class in order to execute a query, without having to make multiple Statement objects
	 * whenever they are needed. 
	 */
	private DatabaseConnection dbConnection;
	private Statement stmt;
	
	public ArtistQuery(DatabaseConnection connection) {
		this.dbConnection = connection;
		try {
			this.stmt = dbConnection.getConnection().createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is used when the user wishes to see all Artist's in their collection. The method makes use of the ALL_ARTISTS string
	 * in order to query the database for all artist's within their collection. If no artist's are found in the collection, the text box
	 * on the UI will display 'Artists could not be retrieved!'.
	 */
	@Override
	public String viewAll() {
		try {
			String query = "SELECT name FROM artist";
			Statement stmt = dbConnection.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			return fetchResults(rs);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		return "Artists could not be retrieved!";
	}
	
	/**
	 * This method is used when the user wishes to remove an Artist from their collection. The method uses three queries in order to avoid
	 * any foreign constraint issues by working down the line of the tables, removing any previous data, meaning songs and albums must be removed
	 * before the artist itself can be removed.
	 * 
	 * @param artist
	 * 			The string stored within the JComboBox on the UI.
	 */
	@Override
	public boolean remove(String artist) {
		PreparedStatement removeInformation;
		int aId = getArtistID(artist);
		String removeArtist = "DELETE FROM artist WHERE artistid = ?;";
		try {
			removeInformation = (PreparedStatement) dbConnection.getConnection().prepareStatement(removeArtist);
			removeInformation.setInt(1, aId);
			removeInformation.execute();
			System.out.println(artist + "  :: ARTIST DELETED"); 
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;	
		}
	}
	
	/**
	 * This method allows the user to add an artist to their collection. First, the method checks whether or not the artist already exists
	 * within their collection by using the helper method checkExistanceOfArtist. If they do not exist, the artist is added. Else, 
	 * the user is told on the UI that the artist could not be added.
	 * 
	 * @param artist
	 * 			The string stored within the Text Area on the UI.
	 * @return 
	 * 			True if the artist was added, False otherwise.
	 */
	public boolean add(String artist) {
		String artistName = artist;
		try {
			if(!checkExistanceOfArtist(artistName)) {
				stmt.executeUpdate("INSERT INTO artist(name) VALUES('" + artistName + "');");
				return true;
			}
			else {
				System.out.println("Duplicate");
				return false;
		}
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * This private method uses the ResultSet object and creates a list of Artist objects, each with their own name. This method can be reused
	 * across the class whenever information needs to be retrieved. 
	 * The artists list is then passed in to convertResults and then returned.
	 * 
	 * @param rs
	 * 			ResultSet containing the information retrieved from the database.
	 * @return
	 * 			A string of the artist's names. 
	 */
	private String fetchResults(ResultSet rs){
		ArrayList<Artist> artists = new ArrayList<Artist>();
		try {
			while(rs.next()) {
				artists.add(new Artist(rs.getString("name")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return convertResults(artists);
	}
	
	/**
	 * This helper method converts the list of Artist objects into a string that can then be shown to the user on the UI. 
	 * 
	 * @param artists
	 * 			The list of Artist objects.
	 * @return
	 * 			A string of formatted names.
	 */
	private String convertResults(ArrayList<Artist> artists) {
		int counter = 1;
		StringBuilder allArtists = new StringBuilder();
		
		for (Artist artist : artists) {
			allArtists.append(counter + ". " + artist.getName() + "\n");
			counter++;
		}
		return allArtists.toString();
	}
	
	/**
	 * This helper method checks whether or not an artist exists within the user's music catalogue. 
	 * 
	 * @param name
	 * 			The name of the artist that needs to be checked.
	 * @return 
	 * 			True if the artist is found, False otherwise.	
	 */
	private boolean checkExistanceOfArtist(String name) {
		String query = "SELECT name FROM artist WHERE name = '" + name + "';";
		ResultSet rs;
		try {
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * This method is used with the UI thread in order to populate JComboBoxes that show the artist names. It selects the 
	 * names from the artist table, and adds the name to a new list which will be returned to the ui class.
	 * 
	 * @return
	 * 			A list of artist names.
	 */
	public ArrayList<String> getArtists(){
		String query = "SELECT name FROM artist";
		ArrayList<String> artistList = new ArrayList<String>();
		try {
			ResultSet artists = stmt.executeQuery(query);
			while (artists.next()) {
				artistList.add(artists.getString("name"));
			}
			return artistList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * This private helper method returns the artistID of a given artist. It is used when removing an artist as the ID is needed.
	 * 
	 * @param artist
	 * 			The name of the artist of which the ID needs to be found.
	 * @return id
	 * 			The ID of that artist.
	 */
	private int getArtistID(String artist) {
		String query = "SELECT artistid from artist where name = '" + artist +"';";
		int id = 0;
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()) {
				id = rs.getInt("artistid");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(id + " : ALBUMID");
		return id;
	}
	
	/**
	 * This method is used to order the artists alphabetically. It queries the database for all of the names of artists,
	 * with the additional 'ORDER BY' command.
	 * 
	 * @param tArea
	 * 			The text area used to check if the user has first chosen to view all of their artists
	 * @return
	 * 			The formatted string of artists
	 */
	@Override
	public String sortAlphabetical(TextArea tArea) {
		if(!tArea.getText().isEmpty()) {
			String query = "SELECT name FROM artist ORDER BY name ASC";
			try {
				ResultSet rs = stmt.executeQuery(query);
				return fetchResults(rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return "Must click 'View All'!";
	}

}
