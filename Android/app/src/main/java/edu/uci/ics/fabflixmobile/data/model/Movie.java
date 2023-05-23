package edu.uci.ics.fabflixmobile.data.model;

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

    public Movie(String title, String id, String year, String director, String genres, String stars) {
        this.title = title;
        this.id = id;
        this.year = year;
        this.director = director;
        String[] parsed = genres.split(", ");
        this.genres = "";
        for( String p : parsed) {
            String[] parsed2 = p.split("\\|");
            this.genres += parsed2[1] + ", ";
        }
        this.genres = this.genres.substring(0, this.genres.length()-3);
        parsed = stars.split(", ");
        this.stars = "";
        for( String p : parsed) {
            String[] parsed2 = p.split("\\|");
            this.stars += parsed2[1] + ", ";
        }
        this.stars = this.stars.substring(0, this.stars.length()-3);
//        this.genres = genres;
//        this.stars = stars;
    }

    public String getTitle() {return title;}

    public String getId() {return id;}

    public String getYear() {return year;}

    public String getDirector() {return director;}

    public String getGenres() {

        return genres;
    }

    public String getStars() {
        return stars;
    }
}