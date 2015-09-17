package br.com.oromar.dev.android.choosemovies;

import android.net.Uri;

import java.io.Serializable;
import java.util.Date;

public class Movie implements Serializable {

    private static final String GET_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String W780 = "w780";


    private String title;
    private String backDropPath;
    private Date releaseDate;
    private String posterPath;
    private String overview;
    private String rate;

    public Movie(){
    }

    public Uri getCompletePosterPath(){
        return Uri.parse(GET_IMAGE_BASE_URL).buildUpon().appendEncodedPath(W780).appendEncodedPath(getPosterPath()).build();
    }

    public Uri getCompleteBackdropPath(){
        return Uri.parse(GET_IMAGE_BASE_URL).buildUpon().appendEncodedPath(W780).appendEncodedPath(getBackDropPath()).build();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackDropPath() {
        return backDropPath;
    }

    public void setBackDropPath(String backDropPath) {
        this.backDropPath = backDropPath;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}


