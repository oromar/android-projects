package br.com.oromar.dev.android.choosemovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements OnItemClickListener , View.OnKeyListener, View.OnClickListener {

    public static final String SEARCH_BASE_URL = "http://api.themoviedb.org/3/search/movie";
    public static final String API_KEY_VALUE = "f8962980164a4f3dabaf7709de21caf8";
    public static final String SORT_BY = "sort_by";
    public static final String DISCOVER_URL = "http://api.tmdb.org/3/discover/movie";
    public static final String SELECTED_MOVIE = "SelectedMovie";
    public static final String API_KEY = "api_key";
    public static final String GET = "GET";
    public static final String RESULTS = "results";
    public static final String JSON_DATE_FORMAT = "yyyy-MM-dd";
    public static final String ADULT = "adult";
    public static final String TITLE = "title";
    public static final String BACKDROP_PATH = "backdrop_path";
    public static final String POSTER_PATH = "poster_path";
    public static final String ORIGINAL_LANGUAGE = "original_language";
    public static final String OVERVIEW = "overview";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String RELEASE_DATE = "release_date";
    public static final String LANGUAGE = "language";
    public static final String QUERY = "query";
    private ImageAdapter adapter;
    private EditText searchText;
    private Button cancelSearchButton;
    private Movie[] movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        adapter = new ImageAdapter(this, new ArrayList<String>());
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        searchText = (EditText) findViewById(R.id.search_field);
        searchText.setOnKeyListener(this);
        cancelSearchButton  = (Button) findViewById(R.id.cancel_action_btn);
        cancelSearchButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (searchText.getText() != null && !searchText.getText().toString().isEmpty())  {
            updateMovies(searchText.getText().toString());
        } else {
            updateMovies();
        }
    }

    private void updateMovies(String ...searchText) {
        DiscoveryMoviesTask task = new DiscoveryMoviesTask();
        task.execute(searchText);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Movie selectedMovie = movies[position];
        Intent i = new Intent(MainActivity.this, DetailActivity.class);
        i.putExtra(SELECTED_MOVIE, selectedMovie);
        startActivity(i);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                updateMovies(searchText.getText().toString());
            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                finish();
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(cancelSearchButton)) {
            searchText.setText("");
            updateMovies();
        }
    }

    public class DiscoveryMoviesTask extends AsyncTask<String, Void, Movie[]> {
        @Override
        protected Movie[] doInBackground(String ...v) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStringData;
            try {
                Uri build = createURL(v);
                URL url = new URL(build.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(GET);
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                jsonStringData = buffer.toString();
            } catch (Exception e) {
                return null;
            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return getDataFromJSON(jsonStringData);
        }

        private Uri createURL(String ...v) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            String sortValue = sharedPreferences.getString(getString(R.string.pref_sort_key), getString(R.string.pref_default_sort));
            String langValue = sharedPreferences.getString(getString(R.string.pref_language_key), getString(R.string.pref_default_language));
            Uri build;
            if (v != null && v.length > 0) {
                String searchValue = v[0];
                build = Uri.parse(SEARCH_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY, searchValue)
                        .appendQueryParameter(API_KEY, API_KEY_VALUE)
                        .appendQueryParameter(SORT_BY, sortValue)
                        .appendQueryParameter(LANGUAGE, langValue)
                        .build();
            } else {
                build = Uri.parse(DISCOVER_URL).buildUpon()
                        .appendQueryParameter(API_KEY, API_KEY_VALUE)
                        .appendQueryParameter(SORT_BY, sortValue)
                        .appendQueryParameter(LANGUAGE, langValue)
                        .build();
            }
            return build;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null && !Arrays.equals(movies, MainActivity.this.movies)) {
                MainActivity.this.movies = movies;
                adapter.clear();
                for (Movie m : movies) {
                    if (m != null) {
                        adapter.add(m.getCompletePosterPath());
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
        private Movie[] getDataFromJSON(String jsonStringData) {
            Movie[] records = null;
            try {
                JSONObject jsonObject = new JSONObject(jsonStringData);
                JSONArray array = jsonObject.getJSONArray(RESULTS);
                JSONObject actualRecord;
                int count = array.length();
                records = new Movie[count];
                Movie movie;
                DateFormat df = new SimpleDateFormat(JSON_DATE_FORMAT, Locale.US);
                for (int i = 0; i < count; i++) {
                    actualRecord = new JSONObject(String.valueOf(array.getJSONObject(i)));
                    movie = new Movie();
                    movie.setAdult(actualRecord.getBoolean(ADULT));
                    movie.setTitle(actualRecord.optString(TITLE));
                    movie.setBackDropPath(actualRecord.optString(BACKDROP_PATH));
                    movie.setPosterPath(actualRecord.optString(POSTER_PATH));
                    movie.setOriginalLanguage(actualRecord.optString(ORIGINAL_LANGUAGE));
                    movie.setOverview(actualRecord.optString(OVERVIEW));
                    movie.setRate(actualRecord.optString(VOTE_AVERAGE));
                    String releaseDate = actualRecord.optString(RELEASE_DATE);
                    if (releaseDate != null &&  !releaseDate.isEmpty()) {
                        movie.setReleaseDate(df.parse(releaseDate));
                    }
                    records[i] = movie;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return records;
        }
    }
}
