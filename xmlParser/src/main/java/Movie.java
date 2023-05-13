import java.util.ArrayList;
public class Movie {
    private String id;
    private String title;
    private int year;
    private String director;
    private ArrayList<String> genres;

    public Movie() {

    }

    public Movie (String id, String title, int year, String director, String genre) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.genres = new ArrayList<String>();

    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    // Setters
    public String setId( String id ) {
        return this.id = id;
    }

    public String setTitle( String title ) {
        return this.title = title;
    }

    public int setYear( int year ) {
        return this.year = year;
    }

    public String setDirector( String director ) {
        return this.director = director;
    }

    public ArrayList<String> setGenres(ArrayList<String> genres) {
        // Might need to deep copy this
        return this.genres = genres;
    }

    public String addGenre( String genre ) {
        genres.add(genre)
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movie Details - ");
        sb.append("Id:" + getId());
        sb.append(", ");
        sb.append("Title:" + getTitle());
        sb.append(", ");
        sb.append("Year:" + getYear());
        sb.append(", ");
        sb.append("Director:" + getDirector());
        sb.append(", ");
        sb.append("Genres:" + getGenres());
        sb.append(".");

        return sb.toString();
    }


}