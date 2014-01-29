package com.example.moviecountdown;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

//This is a test to work with retrofit
public interface myAPI {
	@GET("/3/movie/upcoming")		// query	?page={page}&api_key=af5072ae51691cc5dd87d905a9f32680
	void getMovies(@Query("page") int page, @Query("api_key") String api_key, Callback<UpcomingMoviesResponse> cb);
	
	  //@GET("/3/movie/upcoming?page={pageNumber}&api_key=af5072ae51691cc5dd87d905a9f32680")
	  //void getMovies(@Path("pageNumber") String page, Callback<UpcomingMoviesResponse> cb);
	  
	//	This is working for a hard-coded page
	/*
	 * 		@GET("/3/movie/upcoming?page=1&api_key=af5072ae51691cc5dd87d905a9f32680")
	 *		void getMovies(Callback<UpcomingMoviesResponse> cb);
	 * 
	 */
	  
}

