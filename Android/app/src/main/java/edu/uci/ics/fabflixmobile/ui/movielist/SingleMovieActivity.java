package edu.uci.ics.fabflixmobile.ui.movielist;

import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SingleMovieActivity extends AppCompatActivity {
    TextView titleView;
    TextView ratingView;
    TextView directorView;
    TextView genresView;
    TextView starsView;

    String movieId;

    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "cs122b_project4_war";
    private final String serverEndpoint = "/api/single-movie?";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain + serverEndpoint;

//    public SingleMovieActivity(String movieId) {
//        this.movieId = movieId;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);
        titleView = findViewById(R.id.single_title);
        ratingView = findViewById(R.id.single_rating);
        directorView = findViewById(R.id.single_director);
        genresView = findViewById(R.id.single_genres);
        starsView = findViewById(R.id.single_stars);

        Bundle extras = getIntent().getExtras();
        movieId = extras.getString("movieId");

        String parameters = "id=" + movieId;

        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest ajaxRequest = new StringRequest(
                Request.Method.GET,
                baseURL + parameters,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObj = jsonArray.getJSONObject(0);
                        titleView.setText(jsonObj.getString("movie_title") + " (" + jsonObj.getString("movie_year") + ")");
                        if(jsonObj.getString("movie_rating").equals("null")) {
                            ratingView.setText("Rating: 0.0 \u2605");
                        }
                        else if(jsonObj.getString("movie_rating") == null) {
                            ratingView.setText("Rating: 0.0 \u2605");
                        }
                        else {
                            ratingView.setText("Rating: " + jsonObj.getString("movie_rating") + " \u2605");
                        }
                        directorView.setText("Director: " + jsonObj.getString("movie_director"));
                        String[] parsed = jsonObj.getString("movie_genres").split(", ");
                        String genres = "";
                        for( String p : parsed) {
                            String[] parsed2 = p.split("\\|");
                            genres += parsed2[1] + ", ";
                        }
                        genres = genres.substring(0, genres.length()-2);
                        parsed = jsonObj.getString("movie_stars").split(", ");
                        String stars = "";
                        for( String p : parsed) {
                            String[] parsed2 = p.split("\\|");
                            stars += parsed2[1] + ", ";
                        }
                        stars = stars.substring(0, stars.length()-2);
                        genresView.setText("Genres: " + genres);
                        starsView.setText("Stars: " + stars);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    // error
                    Log.d("singlemovie.error", error.toString());
                }) {
        };
        // important: queue.add is where the login request is actually sent
        queue.add(ajaxRequest);
    }
}
