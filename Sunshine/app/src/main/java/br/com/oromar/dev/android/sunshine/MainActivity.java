package br.com.oromar.dev.android.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private SwipeRefreshLayout mContainerSwipeRefreshLayout;
    private String mLocation;
    private static final String FORECASTFRAGMENT_TAG = "fragment_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default_value));

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();
        }
        setContentView(R.layout.activity_main);
    }


    @Override
    protected void onResume() {
        String location = Utility.getPreferredLocation(this);
        if (!location.equals(mLocation)) {
            ForecastFragment fragment = (ForecastFragment) getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            fragment.onLocationChanged();
            mLocation = location;
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_map) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String location = preferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default_value));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", location).build();
            mapIntent.setData(uri);
            if (mapIntent.resolveActivity(getPackageManager()) != null){
                startActivity(mapIntent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
