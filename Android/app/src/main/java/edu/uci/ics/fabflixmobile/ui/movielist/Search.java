package edu.uci.ics.fabflixmobile.ui.movielist;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.google.gson.Gson;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.sql.DriverManager.println;

public class Search extends AppCompatActivity{
    EditText query_input;
    Button searchButton;
    String queryStr;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
//    private final String host = "54.183.170.147";
    private final String host = "10.0.2.2";
//    private final String port = "8443";

    private final String port = "8080";

    //    private final String domain = "cs122b-project4";
    private final String domain = "fabflix_war";
    private final String serverEndpoint = "/api/fulltext?";
    private final String baseURL = "https://" + host + ":" + port + "/" + domain + serverEndpoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        query_input = findViewById(R.id.query_input);
        searchButton = findViewById(R.id.searchBtn);

        searchButton.setOnClickListener(view -> search(query_input));
    }

    @SuppressLint("SetTextI18n")
    public void search(EditText query) {
        queryStr = String.valueOf(query.getText());
        String parameters = "query=" + query.getText() + "&sortBy=title+ASC+rating+ASC&numRecords=10&firstRecord=0";
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        final StringRequest searchRequest = new StringRequest(
                Request.Method.GET,
                baseURL + parameters,
                response -> {
                    try {
                        JSONArray jsonArr = new JSONArray(response);
                        final ArrayList<Movie> movies = new ArrayList<>();
                        String maxRecords = "0";
                        if(jsonArr.length() > 0) {
                           maxRecords = jsonArr.getJSONObject(0).getString("max_records");
                        }
                        for ( int i = 0; i < jsonArr.length(); ++i ) {
                            JSONObject jsonObj = jsonArr.getJSONObject(i);
                            String rating = "";
                            if(jsonObj.getString("movie_rating") == null) {
                                rating = "0.0";
                            }
                            else if(jsonObj.getString("movie_rating").equals("null")) {
                                rating = "0.0";
                            }
                            else {
                                rating = jsonObj.getString("movie_rating");
                            }
                            movies.add(new Movie(jsonObj.getString("movie_title"), jsonObj.getString("movie_id"), jsonObj.getString("movie_year"),
                                    jsonObj.getString("movie_director"), jsonObj.getString("movie_genres"), jsonObj.getString("movie_stars"), rating));
                        }
                        Gson gson = new Gson();
                        String moviesJsonStr = gson.toJson(movies);
                        Intent MovieListPage = new Intent(Search.this, MovieListActivity.class);
                        MovieListPage.putExtra("movies", moviesJsonStr);
                        MovieListPage.putExtra("offset", 0);
                        MovieListPage.putExtra("query", queryStr);
                        if(movies.size() > 0) {
                            MovieListPage.putExtra("maxRecords", Integer.parseInt(maxRecords));
                        }
                        startActivity(MovieListPage);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    // error
                    Log.d("search.error", error.toString());
                }) {
        };
        queue.add(searchRequest);
    }
}
