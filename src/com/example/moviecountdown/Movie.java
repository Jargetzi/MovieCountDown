package com.example.moviecountdown;

public class Movie {
	//this is a move
	public String title;
	public String release_date;
	public String poster_path;
	public int id;
	
	public String GetTitle() {
		return title;
	}
	public String GetReleaseDate() {
		return release_date;
	}
	
	public int GetId() {
		return id;
	}
}
