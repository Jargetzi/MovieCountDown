package com.example.moviecountdown;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface MovieInfoAPI {
		@GET("/3/movie/{id}")
		void GetMovieInfo(@Path("id") int id, @Query("api_key") String api_key, Callback<MovieInfoResponse> cb);
}
