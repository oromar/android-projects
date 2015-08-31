package br.com.oromar.dev.android.choosemovies;

import android.net.Uri;

import java.io.Serializable;
import java.util.Date;

public class Movie implements Serializable {

    private static final String GET_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String W342 = "w342";
    private static final String W500 = "w500";
    private static final String W185 = "w185";


    private String title;
    private String backDropPath;
    private boolean adult;
    private String originalLanguage;
    private Date releaseDate;
    private String posterPath;
    private String overview;
    private String rate;

    public Movie(){
    }

    public String getCompletePosterPath(){
        return Uri.parse(GET_IMAGE_BASE_URL).buildUpon().appendEncodedPath(W342).appendEncodedPath(getPosterPath()).build().toString();
    }

    public String getCompleteBackdropPath(){
        return Uri.parse(GET_IMAGE_BASE_URL).buildUpon().appendEncodedPath(W500).appendEncodedPath(getBackDropPath()).build().toString();
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

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
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


