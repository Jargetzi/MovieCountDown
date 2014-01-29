package com.example.moviecountdown;

public class MovieInfoResponse {
	public String backdrop_path;
	public String imdb_id;
	public String title;
	public String overview;
	public String poster_path;
	public String release_date;
	
	public String GetBackDrop() {
		return backdrop_path;
	}
	
	public String GetImdbId() {
		return imdb_id;
	}
	
	public String GetTitle() {
		return title;
	}
	
	public String GetOverview() {
		return overview;
	}
	
	public String GetPosterPath() {
		return poster_path;
	}
	
	public String GetReleaseDate() {
		return release_date;
	}
}
