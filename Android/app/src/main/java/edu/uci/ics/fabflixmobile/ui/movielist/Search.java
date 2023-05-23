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
    TextView textView;
    Button searchButton;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "cs122b_project4_war";
    private final String serverEndpoint = "/api/fulltext?";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain + serverEndpoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        textView = findViewById(R.id.tempTextView);
        query_input = findViewById(R.id.query_input);
        searchButton = findViewById(R.id.searchBtn);

        searchButton.setOnClickListener(view -> search(query_input, textView));
    }

    @SuppressLint("SetTextI18n")
    public void search(EditText query, TextView tv) {
//        tv.setText("TEST");
        // need to change firstRecord
        String parameters = "query=" + query.getText() + "&sortBy=title+ASC+rating+ASC&numRecords=20&firstRecord=0";
//        tv.setText(query.getText());
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.GET,
                baseURL + parameters,
                response -> {
                    try {
                        JSONArray jsonArr = new JSONArray(response);
                        final ArrayList<Movie> movies = new ArrayList<>();
                        // Testing if title of movie is displayed on TextView
//                        tv.setText(jsonArr.getJSONObject(0).getString("movie_title"));
                        for ( int i = 0; i < jsonArr.length(); ++i ) {
                            JSONObject jsonObj = jsonArr.getJSONObject(i);
                            movies.add(new Movie(jsonObj.getString("movie_title"), jsonObj.getString("movie_id"), jsonObj.getString("movie_year"),
                                    jsonObj.getString("movie_director"), jsonObj.getString("movie_genres"), jsonObj.getString("movie_stars"), jsonObj.getString("movie_rating")));
                        }
                        Gson gson = new Gson();
                        String moviesJsonStr = gson.toJson(movies);
                        Intent MovieListPage = new Intent(Search.this, MovieListActivity.class);
                        MovieListPage.putExtra("movies", moviesJsonStr);
                        startActivity(MovieListPage);
//                        setContentView(R.layout.activity_movielist);
//                        // Need testing/adjustments below
//                        MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
//                        ListView listView = findViewById(R.id.list);
//                        listView.setAdapter(adapter);
//                        listView.setOnItemClickListener((parent, view, position, id) -> {
//                            Movie movie = movies.get(position);
//                            @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %s", position, movie.getTitle(), movie.getYear());
//                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//                        });

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // POST request form data
//                final Map<String, String> params = new HashMap<>();
//                params.put("email", email.getText().toString());
//                params.put("password", password.getText().toString());
//                return params;
//            }
        };
        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}
