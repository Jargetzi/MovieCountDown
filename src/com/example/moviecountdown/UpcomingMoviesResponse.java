package com.example.moviecountdown;

import java.util.List;


public class UpcomingMoviesResponse {
	public int page;
	public List<Movie> results;		//	Not sure if that is the right way to have the list
	public int total_pages;
	
	public int getPage() {
		return page;
	}
	
	public List<Movie> getMovies() {
		return results;
	}
	
	public int getTotalPages() {
		return total_pages;
	}
}
