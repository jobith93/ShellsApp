package com.three38inc.apps.shellsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.mancj.slideup.SlideUp;
import com.three38inc.apps.shellsscan.UnityPlayerActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatActivity extends AppCompatActivity {

    private SlideUp slideUp;
    private View dim;
    private View slideView;
    private FloatingActionButton fab;
    private String eventList;
    private ImageView loader;
    private WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        slideView = findViewById(R.id.slideView);
        dim = findViewById(R.id.dim);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        slideUp = new SlideUp(slideView);
        slideUp.hideImmediately();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideUp.animateIn();
                fab.hide();
            }
        });

        slideUp.setSlideListener(new SlideUp.SlideListener() {

            @Override
            public void onSlideDown(float v) {
                dim.setAlpha(1 - (v / 100));
            }

            @Override
            public void onVisibilityChanged(int visibility) {
                if (visibility == View.GONE) {
                    fab.show();
                }
            }
        });

        String url = "http://shells.kristujayanti.edu.in/assets/data/events-app.json";
        new ChatActivity.DownloadTask().execute(url);

        loader = (ImageView) findViewById(R.id.loader);
        RotateAnimation rotateAnimation = new RotateAnimation(30, 90,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        loader.setAnimation(rotateAnimation);

        browser = (WebView) findViewById(R.id.chatView);
        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        browser.setWebViewClient(new MyBrowser());
        browser.setWebChromeClient(new MyChromeBrowser());

        if(Utils.isNetworkAvailable(this)) {
            browser.loadUrl("http://shells.kristujayanti.edu.in/chat/index.html");
            loader.setVisibility(View.GONE);
            browser.setVisibility(View.VISIBLE);
        }
        else{
            loader.setVisibility(View.VISIBLE);
            browser.setVisibility(View.GONE);
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if(Utils.isNetworkAvailable(getApplicationContext())) {
                view.loadUrl(url);
                loader.setVisibility(View.GONE);
                browser.setVisibility(View.VISIBLE);
            }
            else{
                loader.setVisibility(View.VISIBLE);
                browser.setVisibility(View.GONE);
            }


            return true;
        }
    }

    private class MyChromeBrowser extends WebChromeClient {
        public MyChromeBrowser() {
            super();
        }

    }

    public class DownloadTask extends AsyncTask<String, Void, Integer> {


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    parseResult(response.toString());
                    result = 1; // Successful
                    Log.d("XYZ","result=1");

                } else {
                    result = 0; //"Failed to fetch data!";
                }
            } catch (Exception e) {
                Log.d("XYZ", e.getLocalizedMessage());
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 1) {

            } else {
                Log.d("XYZ","Failed to fetch data!");
            }
        }
    }

    private void parseResult(String result) {

        Log.d("XYZ",result);
        eventList = result;

    }

    public void handleButton(View v){
        Intent i;
        i = new Intent(this, HomeActivity.class);

        slideUp.animateOut();

        switch(v.getId()){
            case R.id.homeBtn:
                i = new Intent(this, HomeActivity.class);
                break;
            case R.id.eventsBtn:
                i = new Intent(this, EventDetailsActivity.class);
                i.putExtra("EVENTS", eventList);
                break;
            case R.id.scheduleBtn:
                i =  new Intent(this, ScheduleActivity.class);
                break;
            case R.id.locationBtn:
                double latitude = 13.058149;
                double longitude = 77.642600;
                String label = "Kristu Jayanti College Autonomous, Bangalore";
                String uriBegin = "geo:" + latitude + "," + longitude;
                String query = latitude + "," + longitude + "(" + label + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                i = new Intent(android.content.Intent.ACTION_VIEW, uri);
                break;
            case R.id.registerBtn:
                i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://shells.kristujayanti.edu.in/register"));
                break;
            case R.id.resultsBtn:
                i = new Intent(this, ResultsActivity.class);
                break;
            case  R.id.chatBtn:
                i = new Intent(this, ChatActivity.class);
                break;
            case  R.id.promoBtn:
                Log.d("XYZ","Going to Unity");
                i = new Intent(this, UnityPlayerActivity.class);
                Toast.makeText(this,"Scan the Shells 2K17 Logo to see the magic.", Toast.LENGTH_LONG).show();
                break;
            default:
                i = new Intent(this, HomeActivity.class);
        }

        startActivity(i);
    }

}
