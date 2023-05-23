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

    public String getGenres() {
        ArrayList<String> splitGenres = new ArrayList<String>(Arrays.asList(genres.split(",")));
        StringBuilder genresString = new StringBuilder();
        for (int i=0; i < splitGenres.size(); ++i) {
            if ( i != 0) {
                genresString.append(", ");
            }
            genresString.append(splitGenres.get(i).split("\\|")[1]);
        }
        return genresString.toString();
    }

    public String getStars() {
        ArrayList<String> splitStars = new ArrayList<String>(Arrays.asList(stars.split(",")));
        StringBuilder starsString = new StringBuilder();
        for (int i=0; i < splitStars.size(); ++i) {
            if ( i != 0) {
                starsString.append(", ");
            }
            starsString.append(splitStars.get(i).split("\\|")[1]);
        }
        return starsString.toString();
    }

    public String getRating() {return rating;}
}