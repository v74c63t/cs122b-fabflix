package edu.uci.ics.fabflixmobile.data.model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    String title;
    String id;
    String year;
    String director;
    String genres;
    String stars;
    String rating;

    public Movie(String title, String id, String year, String director, String genres, String stars, String rating) {
        this.title = title;
        this.id = id;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
        this.rating = rating;
    }

    public String getTitle() {return title;}

    public String getId() {return id;}

    public String getYear() {return year;}

    public String getDirector() {return director;}

    public String getGenres() {return genres;}

    public String getStars() {return stars;}

    public String getRating() {return rating;}
}