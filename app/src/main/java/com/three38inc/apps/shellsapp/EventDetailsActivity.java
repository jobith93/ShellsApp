package com.three38inc.apps.shellsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mancj.slideup.SlideUp;
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

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;

public class EventDetailsActivity extends AppCompatActivity implements MaterialTabListener {

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

    private SlideUp slideUp;
    private View dim;
    private View slideView;
    private FloatingActionButton fab;
    private String newString;
    private int pos;

    private String eventList;

    private static JSONArray array;

    MaterialTabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newString= null;
                pos = 0;
            } else {
                newString= extras.getString("EVENTS");
                pos = extras.getInt("pos");
            }
        } else {
            newString= (String) savedInstanceState.getSerializable("EVENTS");
            pos= (int) savedInstanceState.getSerializable("pos");

        }

        try {
            JSONObject  jsonRootObject = new JSONObject(newString);

            array = jsonRootObject.optJSONArray("events");

        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//        Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
//        Log.d("XYZ","Array: "+array.toString());

        tabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);

        // init view pager
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                Log.d("XYZ","Selected: "+position);
                tabHost.setSelectedNavigationItem(position);
            }
        });

        // insert all tabs from pagerAdapter data
        Log.d("XYZ","Count: "+mSectionsPagerAdapter.getCount());


        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            try {
                tabHost.addTab(
                        tabHost.newTab()
                                .setText(array.getJSONObject(i).getString("name"))
                                .setTabListener(this)
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(pos);

        String url = "http://shells.kristujayanti.edu.in/assets/data/events-app.json";
        new EventDetailsActivity.DownloadTask().execute(url);

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
        //getMenuInflater().inflate(R.menu.menu_event_details, menu);
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

    @Override
    public void onTabSelected(MaterialTab tab) {
        Log.d("XYZ","Selected Tab: "+tab.getPosition());
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class EventFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public EventFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static EventFragment newInstance(int pos) {
            EventFragment fragment = new EventFragment();
            Bundle args = new Bundle();
            try {
                args.putString("name",array.getJSONObject(pos).getString("name"));
                args.putString("nickName",array.getJSONObject(pos).getString("nickName"));
                args.putString("tagLine",array.getJSONObject(pos).getString("tagLine"));
                args.putString("description",array.getJSONObject(pos).getString("description"));
                args.putString("icon",array.getJSONObject(pos).getString("icon"));
                args.putString("members",array.getJSONObject(pos).getString("members"));
                args.putString("teams",array.getJSONObject(pos).getString("teams"));

                JSONArray arrayCoordinators = array.getJSONObject(pos).getJSONArray("coordinators");
                args.putInt("coordinators",arrayCoordinators.length());
                for(int i=0; i<arrayCoordinators.length(); i++){
                    args.putString("designation"+(i+1),arrayCoordinators.getJSONObject(i).getString("designation"));
                    args.putString("name"+(i+1),arrayCoordinators.getJSONObject(i).getString("name"));
                    args.putString("contact"+(i+1),arrayCoordinators.getJSONObject(i).getString("contact"));
                    args.putString("email"+(i+1),arrayCoordinators.getJSONObject(i).getString("email"));
                }

                JSONArray arrayRules = array.getJSONObject(pos).getJSONArray("rules");
                args.putInt("rules",arrayRules.length());
                for(int i=0; i<arrayRules.length(); i++){
                    args.putString("line"+(i+1),arrayRules.getJSONObject(i).getString("line"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_event_details, container, false);


            TextView name = (TextView) rootView.findViewById(R.id.event_name);
            ImageView imgView = (ImageView) rootView.findViewById(R.id.thumbnail);
            TextView nickName = (TextView) rootView.findViewById(R.id.event_nickname);
            TextView tagline = (TextView) rootView.findViewById(R.id.event_caption);
            TextView members = (TextView) rootView.findViewById(R.id.event_members);
            TextView teams = (TextView) rootView.findViewById(R.id.event_teams);
            TextView description = (TextView) rootView.findViewById(R.id.event_description);

            LinearLayout lin = (LinearLayout) rootView.findViewById(R.id.events_rules);

            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            int rulesNo = getArguments().getInt("rules");

            for(int i=0; i< rulesNo; i++){
                View v = vi.inflate(R.layout.line_layout, null);

                TextView line = (TextView) v.findViewById(R.id.line);
                line.setText("• "+getArguments().getString("line"+(i+1)));
                Log.d("XYZ","Line "+i+" : "+getArguments().getString("line"+(i+1)));

                if(line.getParent()!=null)
                    ((ViewGroup)line.getParent()).removeView(line); // <- fix
                lin.addView(line);
            }

            lin = (LinearLayout) rootView.findViewById(R.id.events_coordinators);

            vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rulesNo = getArguments().getInt("coordinators");

            for(int i=0; i< rulesNo; i++){
                View v = vi.inflate(R.layout.coordinator_layout, null);

                TextView cname = (TextView) v.findViewById(R.id.c_name);
                cname.setText(getArguments().getString("name"+(i+1)));

                TextView cdes = (TextView) v.findViewById(R.id.c_designation);
                cdes.setText(getArguments().getString("designation"+(i+1))+" Coordinator");

                TextView cemail = (TextView) v.findViewById(R.id.c_email);
                cemail.setText(getArguments().getString("email"+(i+1)));

                final TextView cmob = (TextView) v.findViewById(R.id.c_contact);
                cmob.setText(getArguments().getString("contact"+(i+1)));
                cmob.setTextColor(Color.BLUE);
                cmob.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        Log.d("XYZ","Call: "+cmob.getText().toString());
                        String p = "tel:" + cmob.getText().toString();
                        callIntent.setData(Uri.parse(p));
                        startActivity(callIntent);
                    }
                });

                if(v.getParent()!=null)
                    ((ViewGroup)v.getParent()).removeView(v); // <- fix
                lin.addView(v);
            }



            name.setText(getArguments().getString("name"));

            Glide.with(this)
                    .load("http://shells.kristujayanti.edu.in/assets/img/events-sq/"+getArguments().getString("icon"))
                    .asBitmap()
                    .dontAnimate()
                    .into(imgView);

            nickName.setText(getArguments().getString("nickName"));
            tagline.setText(getArguments().getString("tagLine"));
            members.setText(getArguments().getString("members"));
            teams.setText(getArguments().getString("teams"));
            description.setText(getArguments().getString("description"));

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
            return EventFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return array.length();
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "SECTION 1";
//                case 1:
//                    return "SECTION 2";
//                case 2:
//                    return "SECTION 3";
//            }
            try {
                return array.getJSONObject(position).getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
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
