package src;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;
import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;

/**
 * This singleton class is responsible for holding the methods and attributes that are used to create a DatabaseConnection object. This class
 * is passed through multiple query classes, allowing them to execute queries and let the user interact with the database.
 * @author Jacob Moseley
 *
 */
public class DatabaseConnection {
	private Connection connection = null;
	private static DatabaseConnection dbConnection = null;	
/**
 *	This method holds the database connection that is passed throughout the application. When the software is started,
 * a new database is created. The three tables: Artist, Album and Song are created in order as to avoid any foreign key problems. 
 * 
 * The command 'IF NOT EXISTS' stops multiple databases being created when there are new instances of this class. 'DELETE ON CASCADE' is also
 * used when creating foreign keys to allow the easy removal of any data. Finally, the 'connection' attriubte is set to the name
 * of the new database. 
 */
	private DatabaseConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/","root","");
			String createDB = "CREATE DATABASE IF NOT EXISTS musiccatalogue";   //Create the database
			String useDB = "USE musiccatalogue;";   //Use this database for all operations
			//Create the Artist table with the attributes Name and ArtistID
			String createArtist = "CREATE TABLE IF NOT EXISTS Artist(ArtistID INT NOT NULL AUTO_INCREMENT,Name VARCHAR(100) NOT NULL,PRIMARY KEY (ArtistID));";
			//Create the Album table with the attributes AlbumID, Name, Genre, Runtime, Year, ArtistID and Format 
			String createAlbum = "CREATE TABLE IF NOT EXISTS Album(ArtistID INT NOT NULL,AlbumID INT NOT NULL AUTO_INCREMENT,Name VARCHAR(100) NOT NULL," +
					" Genre VARCHAR(100), Runtime FLOAT,Format VARCHAR(100),Year INT,PRIMARY KEY (AlbumID), FOREIGN KEY (ArtistID) REFERENCES Artist(ArtistID) ON DELETE CASCADE);";
			//Create the Song table with the attributes SongID, ArtistID, Name and Length 
			String createSong = "CREATE TABLE IF NOT EXISTS Song(SongID INT NOT NULL AUTO_INCREMENT,AlbumID INT NOT NULL, Length FLOAT,"+ 
					" Name VARCHAR(100) NOT NULL,PRIMARY KEY (SongID),FOREIGN KEY (AlbumID) REFERENCES Album(AlbumID) ON DELETE CASCADE);";
			Statement statement;
			try {
				statement = (Statement) connection.createStatement();
				statement.execute(createDB);
				statement.execute(useDB);
				statement.execute(createArtist);
				statement.execute(createAlbum);
				statement.execute(createSong);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			//Set the connection to the name of the database just created
			this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/musiccatalogue","root","");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}finally {
			System.out.println("Connected");
		}
	}
	
	/**
	 * This  method is used to return a singleton type of a DatabaseConnection object. 
	 * @return
	 * 			The database connection object
	 */
	public static DatabaseConnection getInstance() {
		if (dbConnection == null) {
			dbConnection = new DatabaseConnection();
		}
		return dbConnection;
	}
	
	/**
	 * This method returns the connection object of the class.
	 * @return
	 * 			The connection object.
	 */
	public Connection getConnection() {
		return this.connection;
	}
	
	/**
	 * This method is used to close the database connection.
	 * 
	 * @return
	 * 			True if closed without error, False otherwise 
	 */
	public boolean close() {
		try {
			this.connection.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
