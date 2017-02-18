package com.three38inc.apps.shellsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.mancj.slideup.SlideUp;
import com.pushbots.push.Pushbots;
import com.three38inc.apps.shellsscan.UnityPlayerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public class HomeActivity extends AppCompatActivity {

    private SlideUp slideUp;
    private View dim;
    private View slideView;
    private FloatingActionButton fab;
    private KenBurnsView kbv;

    private List<FeedItem> feedsList;
    private RecyclerView mRecyclerView;
    private EventsViewAdapter adapter;

    private CardView noNetCard;

    public static String eventList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("");

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                Log.d("XYZ", "STATE: "+state.name());
                if(state.name().equals("COLLAPSED")){
                    collapsingToolbar.setTitle(getResources().getString(R.string.app_name));
                }
                else{
                    collapsingToolbar.setTitle("");
                }
            }
        });

        collapsingToolbar.setTitle("");

        kbv = (KenBurnsView) findViewById(R.id.wave_head);
        slideView = findViewById(R.id.slideView);
        dim = findViewById(R.id.dim);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        kbv = (KenBurnsView) findViewById(R.id.wave_head);
        mRecyclerView = (RecyclerView) findViewById(R.id.events_view);
        noNetCard = (CardView) findViewById(R.id.noNetCard);

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

        Pushbots.sharedInstance().registerForRemoteNotifications();
        Pushbots.sharedInstance().setCustomHandler(customHandler.class);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        String url = "http://shells.kristujayanti.edu.in/assets/data/events-app.json";
        new HomeActivity.DownloadTask().execute(url);

    }

    @Override
    protected void onResume(){
        super.onResume();
        load();
    }

    @Override
    public void onBackPressed() {

        if(slideUp.isVisible())
            slideUp.animateOut();
        else
            super.onBackPressed();
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
                adapter = new EventsViewAdapter(HomeActivity.this, feedsList);
                AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
                alphaAdapter.setDuration(700);
                mRecyclerView.setAdapter(alphaAdapter);
                noNetCard.setVisibility(View.GONE);
                fab.show();
                mRecyclerView.setVisibility(View.VISIBLE);
                dim.setAlpha(0);

            } else {
                mRecyclerView.setVisibility(View.GONE);
                noNetCard.setVisibility(View.VISIBLE);
                fab.hide();
                dim.setAlpha(1);
                //Toast.makeText(HomeActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
                Log.d("XYZ","Failed to fetch data!");
            }
        }
    }

    private void parseResult(String result) {
        try {
            Log.d("XYZ",result);
            eventList = result;
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("events");
            feedsList = new ArrayList<>();
            Log.d("XYZ","HI");
            Log.d("XYZ","Data Get: "+result);

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                FeedItem item = new FeedItem();
                item.setName(post.optString("name"));
                item.setNickName(post.optString("nickName"));
                item.setCaption(post.optString("tagLine"));
                Log.d("XYZ",post.optString("tagLine"));
                item.setThumbnail("http://shells.kristujayanti.edu.in/assets/img/events-sq/"+post.optString("icon"));
                feedsList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void load(){

        Glide.with(this)
                .load(getString(R.string.main_header_image))
                .asBitmap()
                .dontAnimate()
                .into(new SimpleTarget<Bitmap>(SimpleTarget.SIZE_ORIGINAL, SimpleTarget.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        kbv.setImageBitmap(bitmap);
                    }
                });

        if(!Utils.isNetworkAvailable(this)){
            Snackbar snackbar = Snackbar
                    .make(mRecyclerView, "Not Connected to Internet!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("SETTINGS", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                        }
                    });
            snackbar.show();
        }

        String url = "http://shells.kristujayanti.edu.in/assets/data/events-app.json";
        new HomeActivity.DownloadTask().execute(url);

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
