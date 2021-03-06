package com.example.qing.personalizedsunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.example.qing.personalizedsunshine.data.WeatherContract;
import com.example.qing.personalizedsunshine.sync.SunshineSyncAdapter;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback{

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    final String LOG_TAG = MainActivity.class.getSimpleName();
    String mLocation;
    boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        Log.i(LOG_TAG, "onCreate is called");
        setContentView(R.layout.activity_main);

        mLocation = "";//Utility.getPreferredLocation(this);
        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0);
        }
        ForecastFragment forecastFragment = (ForecastFragment)(getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast));
        forecastFragment.setUseTodayLayout(!mTwoPane);

        SunshineSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    protected void onStart() {
        Log.v(LOG_TAG, "onStart is called");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.i(LOG_TAG, "onStop is called");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.i(LOG_TAG, "onPause is called");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i(LOG_TAG, "onResume is called");
        super.onResume();
        String locationInPref = Utility.getPreferredLocation(this);
        // update the location in our second pane using the fragment manager
        if (locationInPref != null && !locationInPref.equals(mLocation)) {
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if (null != ff) { // one / two pane
                ff.onLocationChanged();
            }
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != df) { // two pane
                df.onLocationChanged(locationInPref);
            }

            mLocation = locationInPref;
        }
    }
    @Override
    protected void onDestroy() {
        Log.i(LOG_TAG, "onDestroy is called");
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }





    @Override
    public void onItemSelected(Uri contentUri) {
        if(mTwoPane){
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a fragment transaction
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI,contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();

        }else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}
