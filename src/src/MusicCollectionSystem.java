package src;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.MatteBorder;

import com.mysql.jdbc.Statement;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.TextArea;
import javax.swing.JTextField;
import javax.print.DocFlavor.URL;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;

/**
 * This is the main UI class for the project. It is responsible for holding the user side functions, initiating database queries
 * and updating JComboBoxes. 
 * @author Jacob Moseley
 *
 */
public class MusicCollectionSystem {
	// systemFrame is the JFrame that encapsulates the whole project and its various functions
	private JFrame systemFrame;
	
	/*
	 * These JComboBoxes specify the drop-down menus used across the application. They are updated with the three methods 'updateSongDropDown',
	 * 'updateAlbumDropDown' and 'updateArtistDropDown'. These hold artist, album and song names that can be used to easily allow the user
	 * to choose an item of data.
	 */
	private JComboBox<String> removeArtistDropDown = new JComboBox<String>();
	private JComboBox<String> artistDropDown = new JComboBox<String>();
	private JComboBox<String> viewByArtistCBox = new JComboBox<String>();
	private JComboBox<String> viewByArtistDropDown = new JComboBox<String>();
	private JComboBox<String> listOfSongs = new JComboBox<String>();
	private JComboBox<String> songByAlbumDropDown = new JComboBox<String>();
	private JComboBox<String> removeAlbumDropDown = new JComboBox<String>();
	private JComboBox<String> removeAlbumCBox = new JComboBox<String>();

	/**
	 * 
	 * Creating the application in a runnable thread, which creates an instance of itself and sets the systemFrame to visible.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MusicCollectionSystem musicCatalogue = new MusicCollectionSystem();
					musicCatalogue.systemFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	/**
	 * Create the application.
	 */
	public MusicCollectionSystem() {
		/*
		 * Setting the look and feel of the application and ignoring any errors as it is not essential for the program to run.
		 */
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException ignored) {}
		initialize();
	}

	/**
	 * This method creates the User Interface for the application. It has methods within which creates each of the management tabs: Artist, Album and Song.
	 */
	private void initialize() {
		//Creating the initial database connection object which will be passed into the query classes as their method to interact with the database.
		DatabaseConnection connection = DatabaseConnection.getInstance();
		
		//Creating the icon that will be displayed in the corner of the system frame and toolbar. 
		ImageIcon vinyl = new ImageIcon("C:\\Users\\aj\\Documents\\University\\COM0127\\Summartive Assignment\\jm01295_project_com1028\\vinyl-cartoon-5.png");
		/*
		 * Setting the details for the system frame such as the colour scheme, icon image and bounds.
		 */
		systemFrame = new JFrame();
		systemFrame.setTitle("Music Cataloguing System");
		systemFrame.setBackground(Color.PINK);
		systemFrame.setBounds(100, 100, 825, 585);
		systemFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		systemFrame.setIconImage(vinyl.getImage());
		
		/*
		 * This WindowListener listens for when the user has closed the system frame. If so, the connection is closed
		 * to the database.
		 */
		systemFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(connection.close()) {
					System.out.println("Closed");
				}
			}
		});
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		/*
		 * These three methods initiate the setup of the three management tabs, Artist, Album and Song.
		 */
		setArtistManagement(tabbedPane, connection);
	
		setAlbumManagement(tabbedPane, connection);
		
		setSongManagement(tabbedPane, connection);
		
		/*
		 * Creating the group layout of the application with the locations of each component
		 */
		GroupLayout groupLayout = new GroupLayout(systemFrame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 801, GroupLayout.PREFERRED_SIZE)
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		);
		systemFrame.getContentPane().setLayout(groupLayout);
		
	}
	
	/*
	 * This method is responsible for creating all of the components to do with song management
	 */
	public void setSongManagement(JTabbedPane tabbedPane, DatabaseConnection connection) {
		SongQuery song = new SongQuery(connection);
		JTabbedPane songPanel = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Song", null, songPanel, null);
		
		/*
		 * These JPanels define the areas of which each tab will live on. They hold the core components of each function the 
		 * system is capable of doing
		 */
		JPanel removeSongPanel = new JPanel();
		JPanel addPanel = new JPanel();
		JPanel songByAlbumPanel = new JPanel();
		JPanel songByArtistPanel = new JPanel();
		JPanel viewAllPanel = new JPanel();
		
		/*
		 * These text field attributes specify where the user can type in certain values about a song: Its name and runtime
		 */
		JTextField songName;
		JTextField songRuntime;
		
		songPanel.addTab("View All", null, viewAllPanel, null);
		
		/*
		 * These TextAreas are the areas of space which can be updated with text, such as being filled with all songs.
		 */
		TextArea songsByAlbumTextArea = new TextArea();
		songsByAlbumTextArea.setEditable(false);
		TextArea songByArtistTextArea = new TextArea();
		songByArtistTextArea.setEditable(false);
		TextArea viewAllTextArea = new TextArea();
		viewAllTextArea.setEditable(false);
		
		songName = new JTextField();
		songName.setColumns(10);
		
		songRuntime = new JTextField();
		songRuntime.setColumns(10);
		
		/*
		 * These buttons are used to initiate certain functions, in this case, allowing the user to view all the songs, and to then sort them 
		 * alphabetically. 
		 */
		
		JButton viewAllButton = new JButton("Click To View All Songs");
		JButton sortAZbtn = new JButton("Sort A-Z");
		
	
		songPanel.addTab("Remove Song", null, removeSongPanel, null);
		songPanel.addTab("Add Song", null, addPanel, null);
		songPanel.addTab("View Songs By Album", null, songByAlbumPanel, null);
		songPanel.addTab("View Songs By Artist", null, songByArtistPanel, null);
		
		/*
		 * These JLabels are used to prompt the user into selecting certain things or typing certain values into the correct text boxes.
		 * They can also be used to update the user as to whether or not a function has been completed.
		 */
		JLabel updateUserAdd = new JLabel("");
		JLabel lblSongName_1 = new JLabel("Song Name: ");
		JLabel deleteSongLabel = new JLabel("");
		JLabel lblSongName = new JLabel("Song Name:");
		JLabel lblSongRuntimemins = new JLabel("Song Runtime (mins): ");
		JLabel lblAlbumName = new JLabel("Album Name: ");
		JLabel lblAlbumName_1 = new JLabel("Album Name: ");
		JLabel lblArtistName_1 = new JLabel("Artist Name:");
		
		/*
		 * These JButtons are responsible for initiating a function, such as deleting a song or adding a song. 
		 * The action listeners for these buttons are created below.
		 */
		JButton deleteButton = new JButton("Delete");
		JButton submitNewSong = new JButton("Submit!");
		JButton songByAlbumButton = new JButton("Submit!");
		JButton songByArtistButton = new JButton("Submit!");
		
		ArtistQuery artistQuery = new ArtistQuery(connection);
		AlbumQuery albumQuery = new AlbumQuery(connection);
		
		/*
		 * These methods populate the various drop down boxes with data.
		 */
		updateArtistDropDown(artistQuery);
		updateSongDropDown(song);
		updateAlbumDropDown(albumQuery);
		
		sortAZbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewAllTextArea.setText(song.sortAlphabetical(viewAllTextArea));
			}
		});
		
		/*
		 * This action listener initiates the viewAllTextArea to be updated with a string containing all of the songs
		 * the user currently has stored in their collection. 
		 */
		viewAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewAllTextArea.setText(song.viewAll());
			}
		});
		
		/*
		 * This action listener waits for a button press, and once it has been clicked, the text area will be updated
		 * with the songs created by a certain artist.
		 */
		songByArtistButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String artist = String.valueOf(viewByArtistDropDown.getSelectedItem());
				songByArtistTextArea.setText(song.viewSongByArtist(artist));
				updateArtistDropDown(artistQuery);
			}
		});
		
		/*
		 * This action listener updates the text area with songs that reside on a certain album that the user chose
		 * from the drop down menu.
		 */
		songByAlbumButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String album = String.valueOf(songByAlbumDropDown.getSelectedItem());
				songsByAlbumTextArea.setText(song.viewSongByAlbum(album));
			}
		});

		/*
		 * This action listener initiates the removal of a song. It is done by getting the song name stored in the drop down list and removing it.
		 * The user is then told whether or not the removal has been successful by updating the 'deleteSongLabel' text.
		 */
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = String.valueOf(listOfSongs.getSelectedItem());
				if (song.remove(s)) {
					deleteSongLabel.setText("Song removed!");
				}
				else {
					deleteSongLabel.setText("Song could not be removed!");
				}
				updateSongDropDown(song);
			}
		});
		
		/*
		 * This action listener adds a new song, collecting information that has been typed in from the user and converting the strings
		 * into their appropriate data types, ready to be added to the database. Once complete, the text fields are refreshed. 
		 */
		submitNewSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String albumName = String.valueOf(removeAlbumDropDown.getSelectedItem());
				try {  
					double runtime = Double.parseDouble(songRuntime.getText());
					if(song.add(songName.getText(), runtime, albumName)) {
						updateUserAdd.setText("Song added!");
					}
					else {
						updateUserAdd.setText("Song could not be added!");
					}
				}
				catch(NumberFormatException incorrectInput) {
					updateUserAdd.setText("Runtime must be a number!");
				}
				songName.setText("");
				songRuntime.setText("");
				updateSongDropDown(song);
			}
		});
		
		
		/*
		 * These group layout objects are responsible for organising the layout of the components on the UI.
		 */
		GroupLayout gl_addPanel = new GroupLayout(addPanel);
		gl_addPanel.setHorizontalGroup(
			gl_addPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_addPanel.createSequentialGroup()
					.addGap(73)
					.addGroup(gl_addPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblSongName)
						.addComponent(lblAlbumName)
						.addComponent(lblSongRuntimemins))
					.addGap(51)
					.addGroup(gl_addPanel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(songName)
						.addComponent(songRuntime)
						.addComponent(removeAlbumDropDown, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap(310, Short.MAX_VALUE))
				.addGroup(gl_addPanel.createSequentialGroup()
					.addContainerGap(349, Short.MAX_VALUE)
					.addGroup(gl_addPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(updateUserAdd)
						.addComponent(submitNewSong))
					.addGap(345))
		);
		gl_addPanel.setVerticalGroup(
			gl_addPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_addPanel.createSequentialGroup()
					.addGap(47)
					.addGroup(gl_addPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSongName)
						.addComponent(songName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_addPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_addPanel.createSequentialGroup()
							.addComponent(lblSongRuntimemins)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblAlbumName))
						.addGroup(gl_addPanel.createSequentialGroup()
							.addComponent(songRuntime, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(removeAlbumDropDown, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(submitNewSong)
					.addGap(29)
					.addComponent(updateUserAdd)
					.addContainerGap(217, Short.MAX_VALUE))
		);
		addPanel.setLayout(gl_addPanel);
		
		GroupLayout gl_viewAllPanel = new GroupLayout(viewAllPanel);
		gl_viewAllPanel.setHorizontalGroup(
			gl_viewAllPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_viewAllPanel.createSequentialGroup()
					.addGroup(gl_viewAllPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_viewAllPanel.createSequentialGroup()
							.addGap(255)
							.addComponent(viewAllButton)
							.addPreferredGap(ComponentPlacement.RELATED, 165, Short.MAX_VALUE)
							.addComponent(sortAZbtn))
						.addGroup(gl_viewAllPanel.createSequentialGroup()
							.addGap(171)
							.addComponent(viewAllTextArea, GroupLayout.PREFERRED_SIZE, 389, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_viewAllPanel.setVerticalGroup(
			gl_viewAllPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_viewAllPanel.createSequentialGroup()
					.addGroup(gl_viewAllPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_viewAllPanel.createSequentialGroup()
							.addContainerGap()
							.addComponent(viewAllButton)
							.addGap(29)
							.addComponent(viewAllTextArea, GroupLayout.PREFERRED_SIZE, 337, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_viewAllPanel.createSequentialGroup()
							.addGap(10)
							.addComponent(sortAZbtn)))
					.addContainerGap(86, Short.MAX_VALUE))
		);
		viewAllPanel.setLayout(gl_viewAllPanel);
		
		GroupLayout gl_removeSongPanel = new GroupLayout(removeSongPanel);
		gl_removeSongPanel.setHorizontalGroup(
			gl_removeSongPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_removeSongPanel.createSequentialGroup()
					.addGroup(gl_removeSongPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_removeSongPanel.createSequentialGroup()
							.addGap(154)
							.addComponent(lblSongName_1)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(listOfSongs, GroupLayout.PREFERRED_SIZE, 256, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_removeSongPanel.createSequentialGroup()
							.addGap(351)
							.addGroup(gl_removeSongPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(deleteSongLabel)
								.addComponent(deleteButton))))
					.addContainerGap(263, Short.MAX_VALUE))
		);
		gl_removeSongPanel.setVerticalGroup(
			gl_removeSongPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_removeSongPanel.createSequentialGroup()
					.addGap(56)
					.addGroup(gl_removeSongPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSongName_1)
						.addComponent(listOfSongs, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(deleteButton)
					.addGap(39)
					.addComponent(deleteSongLabel)
					.addContainerGap(279, Short.MAX_VALUE))
		);
		removeSongPanel.setLayout(gl_removeSongPanel);
		
		
		GroupLayout gl_songByAlbumPanel = new GroupLayout(songByAlbumPanel);
		gl_songByAlbumPanel.setHorizontalGroup(
			gl_songByAlbumPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_songByAlbumPanel.createSequentialGroup()
					.addGroup(gl_songByAlbumPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_songByAlbumPanel.createSequentialGroup()
							.addGap(207)
							.addComponent(songsByAlbumTextArea, GroupLayout.PREFERRED_SIZE, 355, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_songByAlbumPanel.createSequentialGroup()
							.addGap(169)
							.addComponent(lblAlbumName_1)
							.addGap(39)
							.addGroup(gl_songByAlbumPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(songByAlbumButton)
								.addComponent(songByAlbumDropDown, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap(229, Short.MAX_VALUE))
		);
		gl_songByAlbumPanel.setVerticalGroup(
			gl_songByAlbumPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_songByAlbumPanel.createSequentialGroup()
					.addGap(40)
					.addGroup(gl_songByAlbumPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAlbumName_1)
						.addComponent(songByAlbumDropDown, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(songByAlbumButton)
					.addGap(28)
					.addComponent(songsByAlbumTextArea, GroupLayout.PREFERRED_SIZE, 233, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(86, Short.MAX_VALUE))
		);
		songByAlbumPanel.setLayout(gl_songByAlbumPanel);
		
		
		
		GroupLayout gl_songByArtistPanel = new GroupLayout(songByArtistPanel);
		gl_songByArtistPanel.setHorizontalGroup(
			gl_songByArtistPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_songByArtistPanel.createSequentialGroup()
					.addGap(214)
					.addComponent(lblArtistName_1)
					.addGap(31)
					.addComponent(viewByArtistDropDown, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(253, Short.MAX_VALUE))
				.addGroup(gl_songByArtistPanel.createSequentialGroup()
					.addContainerGap(369, Short.MAX_VALUE)
					.addComponent(songByArtistButton)
					.addGap(325))
				.addGroup(Alignment.LEADING, gl_songByArtistPanel.createSequentialGroup()
					.addGap(138)
					.addComponent(songByArtistTextArea, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(253, Short.MAX_VALUE))
		);
		gl_songByArtistPanel.setVerticalGroup(
			gl_songByArtistPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_songByArtistPanel.createSequentialGroup()
					.addGap(32)
					.addGroup(gl_songByArtistPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(viewByArtistDropDown, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblArtistName_1))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(songByArtistButton)
					.addPreferredGap(ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
					.addComponent(songByArtistTextArea, GroupLayout.PREFERRED_SIZE, 321, GroupLayout.PREFERRED_SIZE)
					.addGap(57))
		);
		songByArtistPanel.setLayout(gl_songByArtistPanel);
		
	}
	
	/*
	 * This method is responsible for creating all of the UI regarding album management, including the tabs, action listeners and text boxes.
	 */
	public void setAlbumManagement(JTabbedPane tabbedPane, DatabaseConnection connection) {
		AlbumQuery album = new AlbumQuery(connection);  //The AlbumQuery object used to connect to the database and run all operations
		ArtistQuery artistQuery = new ArtistQuery(connection);
		SongQuery song = new SongQuery(connection);
		JTabbedPane albumTab = new JTabbedPane(JTabbedPane.TOP);
		albumTab.setBorder(new MatteBorder(1, 1, 1, 1, (Color) Color.BLACK));
		tabbedPane.addTab("Album", null, albumTab, null);
		
		JTextField nameInput;
		JTextField yrOfReleaseInput;
		JTextField genreInput;
		JTextField runtimeInput;
		JTextField formatInput;
		
		/*
		 * These JLabels are for what the user sees, prompting user inputs into text boxes or being used to update the user about whether operations have been successful or not
		 */
		JLabel albumName = new JLabel("Album Name:");
		JLabel yrOfRelease = new JLabel("Year of Release:");
		JLabel genre = new JLabel("Genre:");
		JLabel runtime = new JLabel("Runtime (mins):");
		JLabel format = new JLabel("Format:");
		JLabel artistLabel = new JLabel("Artist of Album:");
		JLabel addComplete = new JLabel("");
		JLabel albumRemoveIndicator = new JLabel("");
		JLabel removeAlbumLabel = new JLabel("Album's Name:");
		JLabel artistNameLabel = new JLabel("Artist's Name:");
		JLabel lblReminder = new JLabel("Artist MUST be in your collection already!");
		
		/*
		 * These JPanel objects are responsible for holding the different tabs within the Album management, allowing the user to make use of the 
		 * different functions of the application.
		 */
		JPanel viewAllPanel = new JPanel();
		JPanel addAlbumPanel = new JPanel();
		JPanel removePanel = new JPanel();
		JPanel viewByArtistPanel = new JPanel();
		
		albumTab.addTab("View All", null, viewAllPanel, null);
		albumTab.addTab("Add Album", null, addAlbumPanel, null);
		albumTab.addTab("Remove Album", null, removePanel, null);
		albumTab.addTab("View By Artist", null, viewByArtistPanel, null);
		
		/*
		 * These JButton objects are used to initiate functions that the AlbumQuery object will undergo 
		 */
		JButton btnViewAll= new JButton("Click To View All Albums");
		JButton albumSubmit = new JButton("Submit!");
		JButton removeAlbumButton = new JButton("Delete!");
		JButton submitArtistButton = new JButton("Submit!");
		JButton sortAZbtn = new JButton("Sort A - Z");
		
		
		TextArea allAlbumTextArea1 = new TextArea();
		allAlbumTextArea1.setEditable(false);
		TextArea albumByArtistText = new TextArea();
		albumByArtistText.setEditable(false);

		
		removeAlbumDropDown.removeAllItems();
		artistDropDown.removeAllItems();
		viewByArtistCBox.removeAllItems();
		
		updateAlbumDropDown(album);
		updateArtistDropDown(artistQuery);
		
		nameInput = new JTextField();
		nameInput.setColumns(10);
		
		yrOfReleaseInput = new JTextField();
		yrOfReleaseInput.setColumns(10);
		
		genreInput = new JTextField();
		genreInput.setColumns(10);
		
		runtimeInput = new JTextField();
		runtimeInput.setColumns(10);
		
		formatInput = new JTextField();
		formatInput.setColumns(10);
		
		
		sortAZbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				allAlbumTextArea1.setText(album.sortAlphabetical(allAlbumTextArea1));
			}
		});
		
		/*
		 * This action listener triggers the program to retrieve all the albums created by a certain artist
		 */
		submitArtistButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String artistName = String.valueOf(viewByArtistCBox.getSelectedItem());
				albumByArtistText.setText(album.viewAlbumByArtist(artistName));
			}
		});
		
		/*
		 * This action listener triggers the program to remove an album specified by the user, updating the user whether or not the function
		 * has been successful or not
		 */
		removeAlbumButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String a = String.valueOf(removeAlbumDropDown.getSelectedItem());
				if (album.remove(a)) {
					albumRemoveIndicator.setText("Album removed!");
				}
				else {
					albumRemoveIndicator.setText("Album could not be removed!");
				}
				updateAlbumDropDown(album);
				updateSongDropDown(song);
				updateArtistDropDown(artistQuery);
			}
		});
		
		/*
		 * This action listener triggers the program to add a new album. It also updates the user whether or not it has been successful or not
		 */
		albumSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String artist = String.valueOf(artistDropDown.getSelectedItem()); 
				if (nameInput.getText().equals("") || nameInput.getText() == null) {
					addComplete.setText("You must enter the album's name!");
				}
				else {
					try {
						double runtime = Double.parseDouble(runtimeInput.getText());   //Check for illegal inputs in text fields
						int year = Integer.parseInt(yrOfReleaseInput.getText());	
						if (album.add(nameInput.getText(), genreInput.getText() ,formatInput.getText(), runtime, year, artist)) {
							addComplete.setText("Album Added!");
						}
						else {
							addComplete.setText("Album could not be added!");
						}
					}
					catch(NumberFormatException illegalInput) {
						addComplete.setText("Runtime and/or Year must be a number!");
					}	
				}
				addComplete.setVisible(true);
				nameInput.setText("");
				genreInput.setText("");
				formatInput.setText("");
				runtimeInput.setText("");
				yrOfReleaseInput.setText("");
				updateArtistDropDown(artistQuery);
				updateAlbumDropDown(album);
			}
		});
		
		/*
		 * This action listener updates a text area to show all of the albums stored in the user's collection
		 */
		btnViewAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				allAlbumTextArea1.setText(album.viewAll());
			}
		});
		
		
		GroupLayout gl_viewAllPanel = new GroupLayout(viewAllPanel);
		gl_viewAllPanel.setHorizontalGroup(
			gl_viewAllPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_viewAllPanel.createSequentialGroup()
					.addGap(278)
					.addComponent(btnViewAll)
					.addPreferredGap(ComponentPlacement.RELATED, 139, Short.MAX_VALUE)
					.addComponent(sortAZbtn)
					.addGap(8))
				.addGroup(gl_viewAllPanel.createSequentialGroup()
					.addGap(139)
					.addComponent(allAlbumTextArea1, GroupLayout.PREFERRED_SIZE, 493, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(157, Short.MAX_VALUE))
		);
		gl_viewAllPanel.setVerticalGroup(
			gl_viewAllPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_viewAllPanel.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_viewAllPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnViewAll)
						.addComponent(sortAZbtn))
					.addGap(5)
					.addComponent(allAlbumTextArea1, GroupLayout.PREFERRED_SIZE, 342, GroupLayout.PREFERRED_SIZE)
					.addGap(62))
		);
		viewAllPanel.setLayout(gl_viewAllPanel);
		
		/*
		 * These GroupLayout object is responsible for making the panel for adding artists not so strict when it comes to creating the layout of the UI. 
		 */
		
		GroupLayout gl_addAlbumPanel = new GroupLayout(addAlbumPanel);
		gl_addAlbumPanel.setHorizontalGroup(
			gl_addAlbumPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_addAlbumPanel.createSequentialGroup()
					.addGroup(gl_addAlbumPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_addAlbumPanel.createSequentialGroup()
							.addGap(69)
							.addGroup(gl_addAlbumPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(runtime)
								.addComponent(genre)
								.addComponent(albumName)
								.addComponent(yrOfRelease)
								.addComponent(format)
								.addComponent(artistLabel))
							.addGap(46)
							.addGroup(gl_addAlbumPanel.createParallelGroup(Alignment.LEADING, false)
								.addComponent(nameInput)
								.addComponent(yrOfReleaseInput)
								.addComponent(genreInput)
								.addComponent(runtimeInput)
								.addComponent(formatInput)
								.addComponent(artistDropDown, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
						.addGroup(gl_addAlbumPanel.createSequentialGroup()
							.addGap(285)
							.addGroup(gl_addAlbumPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(addComplete)
								.addComponent(albumSubmit)))
						.addComponent(lblReminder))
					.addContainerGap(371, Short.MAX_VALUE))
		);
		gl_addAlbumPanel.setVerticalGroup(
			gl_addAlbumPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_addAlbumPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_addAlbumPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(albumName)
						.addComponent(nameInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_addAlbumPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_addAlbumPanel.createSequentialGroup()
							.addComponent(yrOfRelease)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(genre)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(runtime))
						.addGroup(gl_addAlbumPanel.createSequentialGroup()
							.addComponent(yrOfReleaseInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(genreInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(runtimeInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_addAlbumPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(formatInput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(format))))
					.addGroup(gl_addAlbumPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_addAlbumPanel.createSequentialGroup()
							.addGap(18)
							.addComponent(artistLabel))
						.addGroup(gl_addAlbumPanel.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(artistDropDown, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(39)
					.addComponent(albumSubmit)
					.addGap(18)
					.addComponent(addComplete)
					.addPreferredGap(ComponentPlacement.RELATED, 75, Short.MAX_VALUE)
					.addComponent(lblReminder))
		);
		addAlbumPanel.setLayout(gl_addAlbumPanel);
		
		
	
		GroupLayout gl_removePanel = new GroupLayout(removePanel);
		gl_removePanel.setHorizontalGroup(
			gl_removePanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_removePanel.createSequentialGroup()
					.addGroup(gl_removePanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_removePanel.createSequentialGroup()
							.addGap(95)
							.addComponent(removeAlbumLabel)
							.addPreferredGap(ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
							.addGroup(gl_removePanel.createParallelGroup(Alignment.LEADING)
								.addComponent(removeAlbumCBox, GroupLayout.PREFERRED_SIZE, 239, GroupLayout.PREFERRED_SIZE)
								.addComponent(removeAlbumDropDown, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_removePanel.createSequentialGroup()
							.addContainerGap(554, Short.MAX_VALUE)
							.addComponent(albumRemoveIndicator)))
					.addGap(235))
				.addGroup(gl_removePanel.createSequentialGroup()
					.addContainerGap(413, Short.MAX_VALUE)
					.addComponent(removeAlbumButton)
					.addGap(285))
		);
		gl_removePanel.setVerticalGroup(
			gl_removePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_removePanel.createSequentialGroup()
					.addGap(33)
					.addGroup(gl_removePanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(removeAlbumLabel)
						.addGroup(gl_removePanel.createSequentialGroup()
							.addComponent(removeAlbumCBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(35)
							.addComponent(removeAlbumButton)
							.addGap(14)
							.addComponent(removeAlbumDropDown, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(0)
					.addComponent(albumRemoveIndicator)
					.addContainerGap(329, Short.MAX_VALUE))
		);
		removePanel.setLayout(gl_removePanel);
		
		GroupLayout gl_viewByArtistPanel = new GroupLayout(viewByArtistPanel);
		gl_viewByArtistPanel.setHorizontalGroup(
			gl_viewByArtistPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_viewByArtistPanel.createSequentialGroup()
					.addGroup(gl_viewByArtistPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_viewByArtistPanel.createSequentialGroup()
							.addGap(182)
							.addComponent(albumByArtistText, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_viewByArtistPanel.createSequentialGroup()
							.addGap(143)
							.addComponent(artistNameLabel)
							.addGap(43)
							.addComponent(viewByArtistCBox, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_viewByArtistPanel.createSequentialGroup()
							.addGap(340)
							.addComponent(submitArtistButton)))
					.addContainerGap(207, Short.MAX_VALUE))
		);
		gl_viewByArtistPanel.setVerticalGroup(
			gl_viewByArtistPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_viewByArtistPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_viewByArtistPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(artistNameLabel)
						.addComponent(viewByArtistCBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(24)
					.addComponent(submitArtistButton)
					.addPreferredGap(ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
					.addComponent(albumByArtistText, GroupLayout.PREFERRED_SIZE, 243, GroupLayout.PREFERRED_SIZE)
					.addGap(36))
		);
		viewByArtistPanel.setLayout(gl_viewByArtistPanel);

	}
	
	/*
	 * This method creates all of the elements associated with managing the user's Artists. Within this method,
	 * the tabs and action listeners are created in order to interact with the QueryI interface in order to carry out the methods
	 * of managing and cataloguing a music collection.
	 */
	
	public void setArtistManagement(JTabbedPane tabbedPane, DatabaseConnection connection) {
		ArtistQuery artist = new ArtistQuery(connection);  //The ArtistQuery class responsible for interacting with the database
		
		//The top tab used to encapsulate all operations regarding artists
		JTabbedPane artistPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Artist", null, artistPane, null);
		
		TextArea artistTextArea = new TextArea();
		artistTextArea.setEditable(false);
		
		JTextField artistNameText;
		
		/*
		 * These JPanels are held within the artistTab and the user can switch between these panels using the tabs order to update or view their collection
		 */
		JPanel viewAllPanel = new JPanel();
		JPanel addArtistPanel = new JPanel();
		JPanel removeArtistPanel = new JPanel();
	
		/*
		 * These JLabels are used to guide the user when typing text as well as telling the user once an operation has been finished.
		 */
		JLabel lblArtistName = new JLabel("Artist Name:");
		JLabel lblArtistadded = new JLabel("");
		JLabel lblArtistsName = new JLabel("Artist's Name:");
		JLabel artistRemovedLabel = new JLabel("");
		
		/*
		 * These JButtons are used to submit information or to initiating the viewing of their collection.
		 * Their event listeners are created further down.
		 */
		JButton btnSubmit = new JButton("Submit!");
		JButton btnDelete = new JButton("Delete!");
		JButton btnClickToView = new JButton("Click to View All Artists");
		JButton sortAZbtn = new JButton("Sort A - Z");
		
		artistRemovedLabel.setVisible(false);
		lblArtistadded.setVisible(false);
		/*
		 * Populating the JComboBox with artist names.
		 */
		updateArtistDropDown(artist);
		/*
		 * Adding the tabs to the Artist pane.
		 */
		artistPane.addTab("View All", null, viewAllPanel, null);
		artistPane.addTab("Add Artist", null, addArtistPanel, null);
		artistPane.addTab("Remove Artist", null, removeArtistPanel, null);
		
		artistNameText = new JTextField();
		artistNameText.setColumns(10);
		
		sortAZbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				artistTextArea.setText(artist.sortAlphabetical(artistTextArea));
			}
		});
		/*
		 * This action listener creates a new artist with the text written inside artistNameText. The UI is updated according to if the
		 * process has been successful or not.
		 */
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String artistName = artistNameText.getText();
				if (!artistName.isEmpty()) {
					if(artist.add(artistNameText.getText())) {
						lblArtistadded.setText("Artist Added!");
					}
					else {
						lblArtistadded.setText(artistName + " is already in your collection!");
					}
				}
				else {
					lblArtistadded.setText("You must enter a name before submitting!");
				}
				lblArtistadded.setVisible(true);
				artistNameText.setText("");
				updateArtistDropDown(artist);
			}
		});
		

		sortAZbtn.setVerticalAlignment(SwingConstants.TOP);
		GroupLayout gl_viewAllPanel = new GroupLayout(viewAllPanel);
		gl_viewAllPanel.setHorizontalGroup(
			gl_viewAllPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_viewAllPanel.createSequentialGroup()
					.addGroup(gl_viewAllPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_viewAllPanel.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnClickToView)
							.addGap(57)
							.addComponent(sortAZbtn))
						.addGroup(gl_viewAllPanel.createSequentialGroup()
							.addGap(118)
							.addComponent(artistTextArea, GroupLayout.PREFERRED_SIZE, 531, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_viewAllPanel.setVerticalGroup(
			gl_viewAllPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_viewAllPanel.createSequentialGroup()
					.addGap(4)
					.addGroup(gl_viewAllPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(sortAZbtn)
						.addComponent(btnClickToView))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(artistTextArea, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(207))
		);
		viewAllPanel.setLayout(gl_viewAllPanel);
		btnClickToView.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				artistTextArea.setText(artist.viewAll());
			}
		});
		
		/*
		 * This action listener is responsible for initiating the removal of an artist from the user's collection, updating the UI if the
		 * removal has been successful.
		 */
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String a = String.valueOf(removeArtistDropDown.getSelectedItem());
				if (a.isEmpty() || a == null) {
					artistRemovedLabel.setText("You must enter a name to remove!");
				}
				else {
					if(artist.remove(a)) {
						artistRemovedLabel.setText("Artist Removed!");
					}
					else {
						artistRemovedLabel.setText("Artist could not be removed!");
					}
					updateArtistDropDown(artist);	
				}
				artistRemovedLabel.setVisible(true);
			}
		});
		
		//These GroupLayout objects are responsible for making the panel for adding artists not so strict when it comes to creating the layout of the UI. 
		GroupLayout gl_addArtistPanel = new GroupLayout(addArtistPanel);
		gl_addArtistPanel.setHorizontalGroup(
			gl_addArtistPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_addArtistPanel.createSequentialGroup()
					.addGroup(gl_addArtistPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_addArtistPanel.createSequentialGroup()
							.addGap(311)
							.addGroup(gl_addArtistPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(lblArtistadded)
								.addComponent(btnSubmit)))
						.addGroup(gl_addArtistPanel.createSequentialGroup()
							.addGap(57)
							.addComponent(lblArtistName)
							.addGap(108)
							.addComponent(artistNameText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(356, Short.MAX_VALUE))
		);
		gl_addArtistPanel.setVerticalGroup(
			gl_addArtistPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_addArtistPanel.createSequentialGroup()
					.addGap(50)
					.addGroup(gl_addArtistPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblArtistName)
						.addComponent(artistNameText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(60)
					.addComponent(btnSubmit)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblArtistadded)
					.addContainerGap(288, Short.MAX_VALUE))
		);
		addArtistPanel.setLayout(gl_addArtistPanel);
		
		
		GroupLayout gl_removeArtistPanel = new GroupLayout(removeArtistPanel);
		gl_removeArtistPanel.setHorizontalGroup(
			gl_removeArtistPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_removeArtistPanel.createSequentialGroup()
					.addGroup(gl_removeArtistPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_removeArtistPanel.createSequentialGroup()
							.addGap(322)
							.addGroup(gl_removeArtistPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(artistRemovedLabel)
								.addComponent(btnDelete)))
						.addGroup(gl_removeArtistPanel.createSequentialGroup()
							.addGap(82)
							.addComponent(lblArtistsName)
							.addGap(84)
							.addComponent(removeArtistDropDown, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(365, Short.MAX_VALUE))
		);
		gl_removeArtistPanel.setVerticalGroup(
			gl_removeArtistPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_removeArtistPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_removeArtistPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(removeArtistDropDown, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblArtistsName))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnDelete)
					.addGap(38)
					.addComponent(artistRemovedLabel)
					.addContainerGap(291, Short.MAX_VALUE))
		);
		removeArtistPanel.setLayout(gl_removeArtistPanel);
		
	}
	
	/*
	 * These three methods are responsible for updating the JComboBoxes across the application. The methods are called after an update has been made to either an Artist,
	 * Album or Song, to ensure the correct information is maintained across the platform. Firstly, the boxes are cleared to ensure no duplicates, then
	 * re-populated, using the 'get...' methods in the respective Query classes. 
	 */
	
	/**
	 * This method is used to update the JComboBoxes used across the application that hold artist names. 
	 * @param artist
	 * 			The ArtistQuery object, allowing the method to access the getArtists method in order to populate the combo boxes.
	 */
	private void updateArtistDropDown(ArtistQuery artist) {
		removeArtistDropDown.removeAllItems();
		artistDropDown.removeAllItems();
		viewByArtistCBox.removeAllItems();
		viewByArtistDropDown.removeAllItems();
		
		for (String artistName : artist.getArtists()) {
			artistDropDown.addItem(artistName);
			viewByArtistCBox.addItem(artistName);
			viewByArtistDropDown.addItem(artistName);
			removeArtistDropDown.addItem(artistName);
		}
	}
	
	/**
	 * This method is used to update the JComboBoxes used across the application that hold album names. 
	 * @param album
	 * 			The AlbumQuery object, allowing the method to access the getAlbums method in order to populate the combo boxes.
	 */
	private void updateAlbumDropDown(AlbumQuery album) {
		removeAlbumDropDown.removeAllItems();
		songByAlbumDropDown.removeAllItems();
		removeAlbumCBox.removeAllItems();
		for (String albumName : album.getAlbums()) {
			removeAlbumDropDown.addItem(albumName);
			songByAlbumDropDown.addItem(albumName);
			removeAlbumCBox.addItem(albumName);
		}
	}
	
	/**
	 * This method is used to update the JComboBoxes used to hold song names.
	 * @param song
	 * 			The SongQuery object that allows the method to call the getSongs method in order to populate the combo boxes.
	 */
	private void updateSongDropDown(SongQuery song) {
		listOfSongs.removeAllItems();
		for (String songName : song.getSongs()) {
			listOfSongs.addItem(songName);
		}
	}
}
