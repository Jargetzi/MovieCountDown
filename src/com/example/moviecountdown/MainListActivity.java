package com.example.moviecountdown;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
//import com.michaellee.blogreader.BlogWebViewActivity;

@SuppressLint("NewApi")
public class MainListActivity extends ListActivity {
	protected ProgressBar mProgressBar;
	public static final String TAG = MainListActivity.class.getSimpleName();
	protected SearchPageData mSearchPageData;
	private Button mNextButton;
	private Button mPrevButton;
	private TextView mPageTextVew;
	private static final String CURRENT_PAGE = "index";
	private static final String TOTAL_PAGES = "pages";
	
	public List<Integer> mMovieIds;
	
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        
        //	Make the SearchPageData and default it to start on page 1
        mSearchPageData = new SearchPageData();
        mSearchPageData.SetCurrentPage(1);
        
        //get the Buttons and Text View
        mPrevButton = (Button) findViewById(R.id.prev_button);
        mPageTextVew = (TextView) findViewById(R.id.textView1);
        mNextButton = (Button) findViewById(R.id.next_button);
        
        //	id list is nullified, will be set when we connect to api
        mMovieIds = new ArrayList<Integer>();
        mMovieIds.clear();
        
    
        //	Check to see if the screen was rotated
        if( savedInstanceState != null) {
        	mSearchPageData.SetCurrentPage(savedInstanceState.getInt(CURRENT_PAGE, 1));
        	mSearchPageData.SetTotalPages(savedInstanceState.getInt(TOTAL_PAGES, 1));
        }
        
        //	This is for the aciton bar drop down nav
     
        // Adapter
        SpinnerAdapter adapter =
                ArrayAdapter.createFromResource(getActionBar().getThemedContext(), R.array.actions,
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
            	} else if(position == 0) {
            		//	This is to go to MainListActivity
            		//Toast.makeText(getBaseContext(), "Already on that page", Toast.LENGTH_SHORT).show();
            	}
            	
                return true;

            }

        };

        // Action Bar
        ActionBar actions = getActionBar();
        actions.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actions.setDisplayShowTitleEnabled(false);
        actions.setListNavigationCallbacks(adapter, callback);
        
        //getActionBar().setTitle(Html.fromHtml("<font color='red'>" + getString(R.string.app_name) + "</font>"));
        
        if(isNetworkAvailable()) {
        	//	If the network is available, call a thread to get the json data
        	mProgressBar.setVisibility(View.VISIBLE);
        	
        	//APICall();
        	//UpdateDisplay();
        	
        	GetAPITask getAPITask = new GetAPITask();
        	getAPITask.execute();
        }
        else {
			Toast.makeText(this, "Cannot connect!", Toast.LENGTH_LONG).show();
		}
        
        
        
        //Set up the listeners and put the page number
        mPrevButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int current_page = mSearchPageData.GetCurrentPage();
				if( current_page > 1) {
					mSearchPageData.SetCurrentPage(current_page -= 1);
					// Set the TextView
					//UpdateDisplay();
					//APICall();
					
					//mProgressBar.setVisibility(View.VISIBLE);
					GetAPITask getAPITask = new GetAPITask();
		        	getAPITask.execute();
				}
			}
		});
        mNextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int current_page = mSearchPageData.GetCurrentPage();
				int total_pages = mSearchPageData.GetTotalPages();
				//Log.v(TAG,"Current page is " + current_page);
				if( current_page < total_pages) {
						mSearchPageData.SetCurrentPage(current_page += 1);
						// Set the TextView
				        //UpdateDisplay();
						//APICall();
				        //mProgressBar.setVisibility(View.VISIBLE);
				        GetAPITask getAPITask = new GetAPITask();
			        	getAPITask.execute();
				}
				
			}
		});
     
        
        //UpdateDisplay();
        
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState);
    	savedInstanceState.putInt(TOTAL_PAGES, mSearchPageData.GetTotalPages());
    	savedInstanceState.putInt(CURRENT_PAGE, mSearchPageData.GetCurrentPage());
    }
    
    private void UpdateDisplay() {
    	String page = mSearchPageData.GetCurrentPage() + " / " + mSearchPageData.GetTotalPages();
        mPageTextVew.setText(page);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void APICall() {
    	RestAdapter restAdapter = new RestAdapter.Builder()
        .setServer("http://api.themoviedb.org") // The base API
        .build();
        
        myAPI upcomingMoviesAPI = restAdapter.create(myAPI.class);
        
        final SearchPageData spd = new SearchPageData();
        int current_page = mSearchPageData.GetCurrentPage();
        String api_key = "af5072ae51691cc5dd87d905a9f32680";
        upcomingMoviesAPI.getMovies(current_page, api_key, new Callback() {
        	
			@Override
			public void failure(RetrofitError arg0) {
				
				//	If it failed to get API info
				
				Log.e(TAG,"Error getting API info" + arg0);
			}

			@Override
			public void success(Object object, Response response) {
				
				// If it was successful in getting the API info
				
				UpcomingMoviesResponse response1 =  (UpcomingMoviesResponse) object;
				List<Movie> movies = response1.getMovies();
				mSearchPageData.SetTotalPages(response1.getTotalPages());
				UpdateDisplay();
				//Log.v(TAG, "1 Total pages " + response1.getTotalPages());
				spd.SetTotalPages(response1.getTotalPages());
				
				List<Map<String,String>> movieTitlesAndDates = new ArrayList<Map<String, String>>();
				//List<String> titles = new ArrayList<String>();
				
				mMovieIds.clear();
				for( Movie movie : movies) {
					//Log.v(TAG, "Got " + movie.GetTitle());
					
					//titles.add(movie.GetTitle());
					Map<String,String> movieTitleAndDate = new HashMap<String,String>(2);
					movieTitleAndDate.put("title",movie.GetTitle());
					movieTitleAndDate.put("release_date", movie.GetReleaseDate());
					movieTitlesAndDates.add(movieTitleAndDate);
					
					//	Add the id number to mMovieIds
					mMovieIds.add(movie.GetId());
				}
				
				//	Add the titles and the release_date to the ListView
				
				SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), movieTitlesAndDates,
						android.R.layout.simple_list_item_2,
						new String[] {"title", "release_date"},
						new int[] {android.R.id.text1, android.R.id.text2});
				setListAdapter(adapter);
				
			}
			
        });
        mProgressBar.setVisibility(View.INVISIBLE);
        //Log.v(TAG, "2 This is in api call for total pages: " + spd.GetTotalPages());
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
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	
    	//	Get the id of the movie that we want to look at
    	int id_num = (Integer) mMovieIds.toArray()[position];
    	//Log.v(TAG, "A list item was clicked, info:" + " " + position + " " + id_num);
    	
    	Intent intent = new Intent(this, MovieInformationDisplayActivity.class);
    	intent.putExtra("id_num", id_num);
    	intent.putExtra("mark_mode", true);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
    }

    
    private class GetAPITask extends AsyncTask<Void,Void,Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				APICall();
				
			} 
			catch (Exception e) {
	        	Log.d(TAG,"Exception caught in GetAPITask " + e);
	        }
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			
			//Log.v(TAG,"3 asyn total pages " + mSearchPageData.GetTotalPages());
		}
	}
	
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//int itemId = item.getItemId();
		
		return super.onOptionsItemSelected(item);
	}

	private void navMyMovies() {
		Intent intent = new Intent(this, WatchListActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
	}
}
