package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SingleMovieActivity extends AppCompatActivity {
    TextView titleView;
    TextView yearView;
    TextView directorView;
    TextView genresView;
    TextView starsView;

    String movieId;

    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "cs122b_project4_war";
    private final String serverEndpoint = "/api/single-movie?";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain + serverEndpoint;

    public SingleMovieActivity(String movieId) {
        this.movieId = movieId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);
        titleView = findViewById(R.id.single_title);
        yearView = findViewById(R.id.single_year);
        directorView = findViewById(R.id.single_director);
        genresView = findViewById(R.id.single_genres);
        starsView = findViewById(R.id.single_stars);


        String parameters = "id=" + movieId;

        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest ajaxRequest = new StringRequest(
                Request.Method.GET,
                baseURL + parameters,
                response -> {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        titleView.setText(jsonObj.getString("movie_title"));
                        yearView.setText(jsonObj.getString("movie_year"));
                        directorView.setText(jsonObj.getString("movie_director"));
                        genresView.setText(jsonObj.getString("movie_genres"));
                        starsView.setText(jsonObj.getString("movie_stars"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                }) {
        };
        // important: queue.add is where the login request is actually sent
        queue.add(ajaxRequest);
    }
}
