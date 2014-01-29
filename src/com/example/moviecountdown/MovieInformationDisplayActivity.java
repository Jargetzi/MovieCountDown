package com.example.moviecountdown;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MovieInformationDisplayActivity extends Activity {
	public static final String TAG = MovieInformationDisplayActivity.class.getSimpleName();
	private int mMovieId;
	public MovieInfoResponse mMovieInfoResponse;
	private TextView mOverview;
	private TextView mTitleView;
	private ImageView mPosterView;
	private ProgressBar mProgressBar;
	private TextView mReleaseDate;
	private TextView mImdbLink;
	private Button mMarkButton;
	private Button mUnmarkButton;
	

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_information_display);
		
//		This is for the aciton bar drop down nav
	     
        // Adapter
        SpinnerAdapter adapter =
                ArrayAdapter.createFromResource(getActionBar().getThemedContext(), R.array.movie_info_actions,
                android.R.layout.simple_spinner_dropdown_item);
        
        // Callback
        OnNavigationListener callback = new OnNavigationListener() {

            //String[] items = getResources().getStringArray(R.array.actions); // List items from res

            @SuppressLint({ "NewApi", "InlinedApi" })
			@Override
            public boolean onNavigationItemSelected(int position, long id) {
            	if(position==1) {
            		// this is to go to WatchListActivity
            		navMyMovies();
            	} else if(position == 2) {
            		//	This is to go to MainListActivity
            		navUpcomingMovies();
            	}
            	
                return true;

            }

        };

        // Action Bar
        ActionBar actions = getActionBar();
        actions.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actions.setDisplayShowTitleEnabled(false);
        actions.setListNavigationCallbacks(adapter, callback);
		
		//	This is for the asyc task
		if (android.os.Build.VERSION.SDK_INT > 9) {
		      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
		    }
		
		//	Get intent info
		Intent intent = getIntent();
        int id = intent.getIntExtra("id_num", 0);
        boolean mark_mode = intent.getBooleanExtra("mark_mode", true);
        mMovieId = id;
        
        //	Need to use the id to get the movie information
        if(isNetworkAvailable()) {
        	//	Get API info
        	mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        	mProgressBar.setVisibility(View.VISIBLE);
        	GetAPITask getAPITask = new GetAPITask();
        	getAPITask.execute();
        }
        else {
			Toast.makeText(this, "Cannot connect!", Toast.LENGTH_LONG).show();
		}
        
        mImdbLink = (TextView) findViewById(R.id.imdb_link);
        mImdbLink.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// if the imdb link was clicked go to a new page with that site
				
				//Toast.makeText(getBaseContext(), "imdb was clicked " + imdb_id, Toast.LENGTH_SHORT).show();
				CallImdb();
			}
		});
        
        mMarkButton = (Button) findViewById(R.id.mark_button);
        mUnmarkButton = (Button) findViewById(R.id.unmark_button);
        if(mark_mode) {
        	mUnmarkButton.setVisibility(View.GONE);
		    mMarkButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//	call my save function
						SaveMovie();
					}
			});
        } else {
        	mMarkButton.setVisibility(View.GONE);
        	mUnmarkButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//	call unmark fuction
					RemoveMovie();
				}
		});
        }
        
	}
	
	private void CallImdb() {
		String imdb_id = mMovieInfoResponse.GetImdbId();
		
		//Toast.makeText(getBaseContext(), "imdb was clicked " + imdb_id, Toast.LENGTH_SHORT).show();
		
		Intent intent = new Intent(this, ImdbActivity.class);
    	intent.putExtra("imdb_id", imdb_id);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
	}
	
	private boolean isNetworkAvailable() {
    	ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		
		boolean isAvailable = false;
		if (networkInfo != null && networkInfo.isConnected()) {
			isAvailable = true;
		}
		return isAvailable;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void MovieInfoAPICall() {
    	RestAdapter restAdapter = new RestAdapter.Builder()
        .setServer("http://api.themoviedb.org") // The base API
        .build();
        
        MovieInfoAPI movieInfoAPI = restAdapter.create(MovieInfoAPI.class);
        
        String api_key = "af5072ae51691cc5dd87d905a9f32680";
        movieInfoAPI.GetMovieInfo(mMovieId, api_key, new Callback() {
        	
			@Override
			public void failure(RetrofitError arg0) {
				//	If it failed to get API info
				Log.e(TAG,"Error getting API info" + arg0);
			}

			@Override
			public void success(Object object, Response response) {
				
				// If it was successful in getting the API info
				
				MovieInfoResponse response1 =  (MovieInfoResponse) object;
				
				//	Set the response to my class var
				mMovieInfoResponse = response1;
				SetDisplayObjects();
			}
			
        });
        
    }

	public void SetDisplayObjects() {
		mTitleView = (TextView) findViewById(R.id.movie_title);
		mTitleView.setText(mMovieInfoResponse.GetTitle());
		
		
		try {
			  //ImageView i = (ImageView)findViewById(R.id.poster_image);
			mPosterView = (ImageView) findViewById(R.id.poster_image);
			  String imageURL = "http://image.tmdb.org/t/p/w185" + mMovieInfoResponse.GetPosterPath();
			  Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageURL).getContent());
			  mPosterView.setImageBitmap(bitmap); 
			} catch (MalformedURLException e) {
			  e.printStackTrace();
			} catch (IOException e) {
			  e.printStackTrace();
			}
			
		
		mOverview = (TextView) findViewById(R.id.movie_overview);
		mOverview.setText(mMovieInfoResponse.GetOverview());
		
		mReleaseDate = (TextView) findViewById(R.id.movie_release_date);
		mReleaseDate.setText(mMovieInfoResponse.GetReleaseDate());
		
		mProgressBar.setVisibility(View.INVISIBLE);
	}
	
	//	This is to get an image and display it
	public static Drawable LoadImageFromWebOperations(String url) {
	    try {
	        InputStream is = (InputStream) new URL(url).getContent();
	        Drawable d = Drawable.createFromStream(is, "src name");
	        return d;
	    } catch (Exception e) {
	        return null;
	    }
	}
	
	
	private class GetAPITask extends AsyncTask<Void,Void,Void> {

		@Override
		protected Void doInBackground(Void... params) {
			MovieInfoAPICall();
			return null;
		}
		
	}
	
	private void RemoveMovie() {
		String filename = "mymovies";
		JSONObject jsonObject = new JSONObject();
		File dir = getFilesDir();
		File file = new File(dir,filename);
		if(file.exists()) {
			try {
				jsonObject = new JSONObject(readFromFile());
				Log.v(TAG,"This is jsonObject " + jsonObject.toString());
				if (jsonObject.has(mMovieInfoResponse.GetTitle())) {
					jsonObject.remove(mMovieInfoResponse.GetTitle());
					
					String saveMovieInfo = jsonObject.toString();
					FileOutputStream outputStream;
					try {
						outputStream = openFileOutput(filename, Context.MODE_PRIVATE);	//was append
						outputStream.write(saveMovieInfo.getBytes());
						outputStream.close();
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getBaseContext(), "Failed to save", Toast.LENGTH_SHORT).show();
					}
					
					Intent intent = new Intent(this, WatchListActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    	startActivity(intent);
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	//	This is a test to save movie id and title
	private void SaveMovie() {
		//	This is the start of json testing
		
		
		String filename = "mymovies";
		JSONObject jsonObject = new JSONObject();
		File dir = getFilesDir();
		File file = new File(dir,filename);
		if(file.exists()) {
			//Toast.makeText(getBaseContext(), "file exists, try to append", Toast.LENGTH_SHORT).show();
			try {
				jsonObject = new JSONObject(readFromFile());
				Log.v(TAG,"This is jsonObject " + jsonObject.toString());
				if (jsonObject.has(mMovieInfoResponse.GetTitle())) {	// was checking id
					//	this movie is already saved
					Toast.makeText(getBaseContext(), "Movie is already being watched", Toast.LENGTH_SHORT).show();
				} else {
					
					//jsonObject.put(mMovieId+"", mMovieInfoResponse.GetTitle());
					JSONObject movieJsonObject = new JSONObject();
					movieJsonObject.put("id",mMovieId);
					movieJsonObject.put("release_date", mMovieInfoResponse.GetReleaseDate());
					jsonObject.put(mMovieInfoResponse.GetTitle(), movieJsonObject);
					
					String saveMovieInfo = jsonObject.toString();
					FileOutputStream outputStream;
					try {
						outputStream = openFileOutput(filename, Context.MODE_PRIVATE);	//was append
						outputStream.write(saveMovieInfo.getBytes());
						outputStream.close();
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getBaseContext(), "Failed to save", Toast.LENGTH_SHORT).show();
					}
					
					Intent intent = new Intent(this, WatchListActivity.class); 
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    	startActivity(intent);
				}
				
			} catch (JSONException e1) {
				
				e1.printStackTrace();
			}
		}	//test if file exists
		else {
			//need to make the file
			// format for json: title: jsonObject, jsonObject has id and release date
			Toast.makeText(getBaseContext(), "making new file", Toast.LENGTH_SHORT).show();
			JSONObject movieJsonObject = new JSONObject();
			try {
				movieJsonObject.put("id",mMovieId);
				movieJsonObject.put("release_date", mMovieInfoResponse.GetReleaseDate());
				//jsonObject.putOpt(mMovieId+"", mMovieInfoResponse.GetTitle());
				jsonObject.put(mMovieInfoResponse.GetTitle(), movieJsonObject);
			} catch (JSONException e1) {
				Log.e(TAG,"failed to put data into jsonObject" + e1.toString());
			}
			
			String saveMovieInfo = jsonObject.toString();
			
			FileOutputStream outputStream;
			try {
				outputStream = openFileOutput(filename, Context.MODE_PRIVATE);	//was append
				
				
				
				outputStream.write(saveMovieInfo.getBytes());
				outputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getBaseContext(), "Failed to save", Toast.LENGTH_SHORT).show();
			}
			
			Intent intent = new Intent(this, WatchListActivity.class);    	
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	startActivity(intent);
		}
		
		/////////////////
		/*
		if(mode==1) {
		String filename = "mymovies";
		String saveMovieInfo = mMovieId + " " + mMovieInfoResponse.GetTitle() + "\n";
		FileOutputStream outputStream;
		//Toast.makeText(getBaseContext(), "Saving movie info" + saveMovieInfo, Toast.LENGTH_LONG).show();
		try {
			outputStream = openFileOutput(filename, Context.MODE_APPEND);
			outputStream.write(saveMovieInfo.getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getBaseContext(), "Failed to save", Toast.LENGTH_SHORT).show();
		}
		}
		*/
		
		
		//	used this to delete file from my phone
		/*
		String filename = "mymovies";
		File dir = getFilesDir();
		File file = new File(dir,filename);
		file.delete();
		
		
		
		Intent intent = new Intent(this, WatchListActivity.class);    	
    	startActivity(intent);
		*/
	}
	
	//	read from file
	public String readFromFile() {
		String ret="";
		try {
			InputStream inputStream = openFileInput("mymovies");
			if( inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);	
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();
				
				while ( (receiveString = bufferedReader.readLine()) != null ) {
					stringBuilder.append(receiveString);
				}
				
				inputStream.close();
				ret = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG,"File not found foob " + e.toString());
		} catch(Exception e) {
			Log.e(TAG, "Cannot read file: " + e.toString());
		}
		return ret;
	}
	
	private void navMyMovies() {
		Intent intent = new Intent(this, WatchListActivity.class);    	
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
	}
	
	private void navUpcomingMovies() {
		Intent intent = new Intent(this, MainListActivity.class);    
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.movie_information_display, menu);
		return true;
	}

}
