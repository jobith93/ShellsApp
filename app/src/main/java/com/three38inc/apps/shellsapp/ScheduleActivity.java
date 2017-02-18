package com.three38inc.apps.shellsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mancj.slideup.SlideUp;
import com.three38inc.apps.shellsscan.UnityPlayerActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ScheduleActivity extends AppCompatActivity {

    private SlideUp slideUp;
    private View dim;
    private View slideView;
    private FloatingActionButton fab;
    private String eventList;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

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
        new ScheduleActivity.DownloadTask().execute(url);

    }

    @Override
    public void onBackPressed() {

        if(slideUp.isVisible())
            slideUp.animateOut();
        else
            super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);


            ImageView img = (ImageView) rootView.findViewById(R.id.imgSchedule);

            Glide.with(this)
                    .load("http://shells.kristujayanti.edu.in/assets/img/schedule-"+getArguments().getInt(ARG_SECTION_NUMBER)+".jpg")
                    .dontAnimate()
                    .into(img);


            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Day 1";
                case 1:
                    return "Day 2";
            }
            return null;
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
