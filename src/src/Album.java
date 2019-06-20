package src;

/**
 * his class is used when creating new Album objects to then be shown on the user interface. 
 * @author Jacob Moseley
 *
 */
public class Album {
	private String name;
	private double runtime;
	private String genre;
	private int year;
	private String format;
	
	public Album(String name, String format, String genre, double runtime, int year) {
		this.name = name;
		this.format = format;
		this.genre = genre;
		this.runtime = runtime;
		this.year = year;
	}
	
	/*
	 * These methods provide simple getters for when the method convertResults formats the information from the database into a readable
	 * string to be placed onto the user interface.
	 */
	public String getName() {
		return name;
	}

	public double getRuntime() {
		return runtime;
	}

	public String getGenre() {
		return genre;
	}

	public int getYear() {
		return year;
	}

	public String getFormat() {
		return format;
	}
	
}
