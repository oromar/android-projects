package br.com.oromar.dev.android.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> adapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_main, container, false);
        final ListView listView = (ListView) result.findViewById(R.id.listview_forecast);
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_id, new ArrayList<String>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = (String) listView.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(intent);
            }
        });

        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    private void updateWeather() {
        ReadWeatherInformation reader = new ReadWeatherInformation();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        reader.execute(preferences.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default_value)));
    }

    public class ReadWeatherInformation extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStringData;

            try {

                final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
                final String QUERY_MODE_PARAM_VALUE = "json";
                final String QUERY_UNITS_PARAM_VALUE = "metric";
                final String QUERY_CNT_PARAM_VALUE = "7";

                Uri build = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("q", params[0])
                        .appendQueryParameter("mode", QUERY_MODE_PARAM_VALUE)
                        .appendQueryParameter("units", QUERY_UNITS_PARAM_VALUE)
                        .appendQueryParameter("cnt", QUERY_CNT_PARAM_VALUE).build();

                URL url = new URL(build.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                jsonStringData = buffer.toString();

            } catch (Exception e) {

                Log.d("PlaceHolderFragment", e.getMessage());

                return null;

            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.d("PlaceHolderFragment", e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            Log.v("PlaceHolderFragment", jsonStringData);
            return getDataFromJSON(jsonStringData);
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                adapter.clear();
                for (String s : strings) {
                    adapter.add(s);
                }
            }
        }

        private String[] getDataFromJSON(String jsonStringData) {

            JSONObject weather = null;
            JSONArray actualWeatherArray = null;
            JSONObject actualWeather = null;
            JSONObject actualRecord = null;
            JSONObject actualTemp = null;
            try {

                weather = new JSONObject(jsonStringData);

                JSONArray array = weather.getJSONArray("list");

                int count = weather.getInt("cnt");

                String[] records = new String[count];

                StringBuffer buffer = new StringBuffer();

                for (int i = 0; i < count; i++) {
                    actualRecord = new JSONObject(String.valueOf(array.getJSONObject(i)));
                    actualWeatherArray = actualRecord.getJSONArray("weather");
                    actualWeather = (JSONObject) actualWeatherArray.get(0);
                    actualTemp = actualRecord.getJSONObject("temp");
                    buffer.append(" ").append(getReadableDate(actualRecord.getLong("dt") * 1000L));
                    buffer.append(" - ").append(actualWeather.getString("main"));
                    formatLowHighTemperature(actualTemp, buffer);
                    records[i] = buffer.toString();
                    buffer = new StringBuffer();
                }

                return records;

            } catch (JSONException e) {
                Log.e("PlaceHolderFragment", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        private void formatLowHighTemperature(JSONObject actualTemp, StringBuffer buffer) throws JSONException {
            Double celsiusLow = actualTemp.getDouble("min");
            Double celsiusHigh = actualTemp.getDouble("max");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String units = preferences.getString(getString(R.string.pref_units_key), "");
            if (!getString(R.string.pref_units_default_value).equals(units)) {
                celsiusLow = (celsiusLow * 1.8) + 32;
                celsiusHigh = (celsiusHigh * 1.8) + 32;
                long roundedMin = Math.round(celsiusLow);
                long roundedMax = Math.round(celsiusHigh);
                buffer.append(" - ").append(roundedMin);
                buffer.append("/").append(roundedMax);
            } else {
                buffer.append(" - ").append(celsiusLow);
                buffer.append("/").append(celsiusHigh);
            }
        }

        private String getReadableDate(long date) {
            return new SimpleDateFormat("E, dd/MM").format(new Date(date));
        }
    }
}
