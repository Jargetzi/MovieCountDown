package com.example.moviecountdown;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class WatchListActivity extends ListActivity {
	public static final String TAG = WatchListActivity.class.getSimpleName();
	//private List<Map<String,String>> mMovieList;
	private List<Map<String,String>> mMovieIds;
	//private ListView mListView;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch_list);
		
//		This is for the aciton bar drop down nav
	     
        // Adapter
        SpinnerAdapter action_adapter =
                ArrayAdapter.createFromResource(getActionBar().getThemedContext(), R.array.watch_actions,
                android.R.layout.simple_spinner_dropdown_item);
        
        // Callback
        OnNavigationListener callback = new OnNavigationListener() {

            //String[] items = getResources().getStringArray(R.array.watch_actions); // List items from res

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
        actions.setListNavigationCallbacks(action_adapter, callback);
		
		String file = readFromFile();
		if( file == "") {
			displayNoWatched();
		}
		//	make hash map to put into an adapter
		List<Map<String,String>> movieTitlesAndDates = new ArrayList<Map<String, String>>();
		mMovieIds = new ArrayList<Map<String, String>>();
		
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(file);
			@SuppressWarnings("unchecked")
			Iterator<String> iter = jsonObject.keys();
			while( iter.hasNext()) {
				String title = iter.next();
				
				try {
					JSONObject movieInfo = (JSONObject) jsonObject.get(title);
					Map<String,String> movieTitleAndDate = new HashMap<String,String>(2);
					movieTitleAndDate.put("title", title);
					movieTitleAndDate.put("release_date", movieInfo.getString("release_date"));
					movieTitlesAndDates.add(movieTitleAndDate);
					
					Map<String,String> releaseDateAndId = new HashMap<String,String>(2);
					releaseDateAndId.put("release_date", movieInfo.getString("release_date"));
					releaseDateAndId.put("id", movieInfo.getString("id"));
					
					mMovieIds.add(releaseDateAndId);
				} catch (Exception e) {
					Log.e(TAG,"Error getting json object: " + e.toString());
				}
			}
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		
		//	Sort movieTitlesAndDates to have the movie coming out soonest on top
		Comparator<Map<String, String>> mapComparator = new Comparator<Map<String, String>>() {
		    public int compare(Map<String, String> m1, Map<String, String> m2) {
		        return m1.get("release_date").compareTo(m2.get("release_date"));
		    }
		};
		Log.v(TAG,"This is the movieids:" + mMovieIds.toString());
		Collections.sort(movieTitlesAndDates, mapComparator);
		Collections.sort(mMovieIds,mapComparator);
		//mMovieList = movieTitlesAndDates;
		// Add list to adapter
		SimpleAdapter adapter = new SimpleAdapter(this, movieTitlesAndDates,
				android.R.layout.simple_list_item_2,
				new String[] {"title", "release_date"},
				new int[] {android.R.id.text1, android.R.id.text2});
		
		setListAdapter(adapter);
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
			Log.e(TAG,"File not found" + e.toString());
		} catch(Exception e) {
			Log.e(TAG, "Cannot read file: " + e.toString());
		}
		return ret;
	}
	
	//	If there is no saved data, then show a dialog informing user
	public void displayNoWatched() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.watch_no_movies_title));
		builder.setMessage(getString(R.string.watch_no_movies_text));
		builder.setPositiveButton(android.R.string.ok, null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
    	
    	//	Get the id of the movie that we want to look at
    	String string_id_num = mMovieIds.get(position).get("id");
    	int id_num;
    	try {
    	    id_num = Integer.parseInt(string_id_num);
    	} catch(NumberFormatException nfe) {
    		id_num = -1;
    		Log.e(TAG,"problem string to int " + nfe.toString());
    	}
    	if( id_num != -1) {
	    	Intent intent = new Intent(this, MovieInformationDisplayActivity.class);
	    	intent.putExtra("id_num", id_num);
	    	intent.putExtra("mark_mode", false);
	    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	startActivity(intent);
    	}
    }
	
	//	Menu options and handles
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.watch_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		
		if (itemId == R.id.action_delete_all ){
			//deleteFile();
			confirmDelete();
		} 
		
		return super.onOptionsItemSelected(item);
	}

	private void deleteFile() {
		String filename = "mymovies";
		File dir = getFilesDir();
		File file = new File(dir,filename);
		file.delete();
		Toast.makeText(getBaseContext(), "file is deleted", Toast.LENGTH_SHORT).show();
		finish();
		startActivity(getIntent());
	}
	
	//	Display a pop up confirming the delete file function
	public void confirmDelete() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setMessage("Do you want to delete all marked movies?")
			.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// confirm delete
					deleteFile();
				}
			})
			.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// cancel the dialog
					
				}
			});
		
		AlertDialog dialog = builder.create();
		dialog.show();
		
	}
	
	private void navMyMovies() {
		Intent intent = new Intent(this, MainListActivity.class);    	
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
	}

}
