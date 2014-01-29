package com.example.moviecountdown;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ImdbActivity extends Activity {
	private WebView mWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imdb);
		
		//	Get intent info
			Intent intent = getIntent();
	        
	        String imdb_id = intent.getStringExtra("imdb_id");
	        //Toast.makeText(getBaseContext(), "this is what I got " + imdb_id, Toast.LENGTH_LONG).show();
	        
	        mWebView = (WebView) findViewById(R.id.webView1);
	        String url = "http://www.imdb.com/title/" + imdb_id;
	        
	        mWebView.setWebViewClient(new WebViewClient() {
	            @Override
	            public boolean shouldOverrideUrlLoading(WebView view, String url) {
	                view.loadUrl(url);
	                return false;
	            }
	        });
	        
	        mWebView.loadUrl(url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.imdb, menu);
		return true;
	}

}
