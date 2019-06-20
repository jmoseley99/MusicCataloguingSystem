package src;

public class Song {
	private String name;
	private double runtime;
	
	/*
	 * This class is used when creating new Songs when retrieving songs from the database. 
	 */
	public Song(String name, double runtime) {
		
		this.name = name;
		this.runtime = runtime;
	}
	
	/**
	 * These two getters return the information about a song object, taken from the database.
	 * @return
	 * 			The song title.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * This method returns the songs runtime
	 * @return
	 * 			The song runtime.
	 */
	public double getRuntime() {
		return runtime;
	}
}
