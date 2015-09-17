package br.com.oromar.dev.android.choosemovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    public static final String MAX_RATE = "/10";
    public static final String SELECTED_MOVIE = "SelectedMovie";
    public static final String YYYY = "yyyy";
    private TextView titleTextView;
    private TextView overviewTextView;
    private TextView releaseDateTextView;
    private TextView rateTextView;
    private ImageView imageView;
    private Movie selectedMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        init();
    }

    private void init() {
        DateFormat df = new SimpleDateFormat(YYYY);
        selectedMovie = (Movie) getIntent().getExtras().get(SELECTED_MOVIE);
        titleTextView = (TextView) findViewById(R.id.movie_text);
        titleTextView.setText(selectedMovie.getTitle());
        overviewTextView = (TextView) findViewById(R.id.overview_text);
        overviewTextView.setText(selectedMovie.getOverview());
        imageView = (ImageView) findViewById(R.id.detail_image_poster);
        releaseDateTextView = (TextView)findViewById(R.id.relase_date_text);
        Date releaseDate = selectedMovie.getReleaseDate();
        if (releaseDate != null) {
            releaseDateTextView.setText(df.format(releaseDate));
        }
        rateTextView = (TextView)findViewById(R.id.rate_text);
        rateTextView.setText(selectedMovie.getRate() + MAX_RATE);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int imageWidth = width / 2;
        Picasso.with(this).load(selectedMovie.getCompletePosterPath())
                .resize(imageWidth, (int) Math.round(imageWidth * 1.5))
                .into(imageView);
        ImageView backdrop = (ImageView) findViewById(R.id.backdrop_image);
        Picasso.with(this).load(selectedMovie.getCompleteBackdropPath())
                .resize(width, (int) Math.round(imageWidth * 1.2))
                .into(backdrop);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
}
