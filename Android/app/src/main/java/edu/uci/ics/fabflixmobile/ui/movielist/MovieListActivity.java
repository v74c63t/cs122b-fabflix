package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {

    TextView pageView;
    TextView noResultsView;
    int maxRecords;
    int offset;
    Button nextButton;
    Button prevButton;
    String query;

    private final String host = "10.0.2.2";
    private final String port = "8080";
    private final String domain = "cs122b_project4_war";
    private final String serverEndpoint = "/api/fulltext?";
    private final String baseURL = "http://" + host + ":" + port + "/" + domain + serverEndpoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        pageView = findViewById(R.id.page);
        // TODO: this should be retrieved from the backend server
//        final ArrayList<Movie> movies = new ArrayList<>();
//        movies.add(new Movie("The Terminal", "2004"));
//        movies.add(new Movie("The Final Season", "2007"));
        Bundle extras = getIntent().getExtras();
        offset = extras.getInt("offset");
        int pageNum = offset/20 + 1;
        query = extras.getString("query");
        pageView.setText("Page " + Integer.toString(pageNum));
        String jsonStr = extras.getString("movies");
        Gson gson = new Gson();
        final ArrayList<Movie> movies = gson.fromJson(jsonStr, new TypeToken<ArrayList<Movie>>(){}.getType());
        if(movies.size() > 0) {
            maxRecords = extras.getInt("maxRecords");
            MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
            ListView listView = findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Movie movie = movies.get(position);
                //            @SuppressLint("DefaultLocale") String message = String.format("Clicked on movie id: %s", movie.getId());
                //            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                Intent SingleMoviePage = new Intent(MovieListActivity.this, SingleMovieActivity.class);
                SingleMoviePage.putExtra("movieId", movie.getId());
                startActivity(SingleMoviePage);
            });
        }
        else {
            maxRecords = 0;
            noResultsView = findViewById(R.id.noResults);
            noResultsView.setText("There are no results for the query '" + query + "'" );
        }
        nextButton = findViewById(R.id.next);
        prevButton = findViewById(R.id.prev);
        nextButton.setOnClickListener(view -> next());
        prevButton.setOnClickListener(view -> prev());
    }
    @SuppressLint("SetTextI18n")
    public void prev() {
//        tv.setText("TEST");
        // need to change firstRecord
        if(offset - 20 >= 0) {
            // use the same network queue across our application
            final RequestQueue queue = NetworkManager.sharedManager(this).queue;
            String parameters = "query=" + query + "&sortBy=title+ASC+rating+ASC&numRecords=20&firstRecord=" + Integer.toString(offset - 20);
//            @SuppressLint("DefaultLocale") String message = String.format("Param: %s", parameters);
//             Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                            int maxRecords = jsonArr.getJSONObject(0).getInt("max_records");
                            for ( int i = 0; i < jsonArr.length(); ++i ) {
                                JSONObject jsonObj = jsonArr.getJSONObject(i);
                                String rating = "";
                                if(jsonObj.getString("movie_rating").equals("null")) {
                                    rating = "0.0";
                                }
                                else if(jsonObj.getString("movie_rating") == null) {
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
                            Intent MovieListPage = new Intent(MovieListActivity.this, MovieListActivity.class);
                            MovieListPage.putExtra("movies", moviesJsonStr);
                            MovieListPage.putExtra("offset", offset-20);
                            MovieListPage.putExtra("maxRecords", maxRecords);
                            MovieListPage.putExtra("query", query);
                            startActivity(MovieListPage);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    error -> {
                        // error
                        Log.d("login.error", error.toString());
                    }) {
            };
            queue.add(loginRequest);
        }
//        tv.setText(query.getText());
        // important: queue.add is where the login request is actually sent

    }
    public void next() {
//        tv.setText("TEST");
        // need to change firstRecord
        if(offset + 20 < maxRecords) {
            // use the same network queue across our application
            final RequestQueue queue = NetworkManager.sharedManager(this).queue;
            String parameters = "query=" + query + "&sortBy=title+ASC+rating+ASC&numRecords=20&firstRecord=" + Integer.toString(offset + 20);
//            @SuppressLint("DefaultLocale") String message = String.format("Param: %s", parameters);
//             Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                            int maxRecords = jsonArr.getJSONObject(0).getInt("max_records");
                            for ( int i = 0; i < jsonArr.length(); ++i ) {
                                JSONObject jsonObj = jsonArr.getJSONObject(i);
                                String rating = "";
                                if(jsonObj.getString("movie_rating").equals("null")) {
                                    rating = "0.0";
                                }
                                else if(jsonObj.getString("movie_rating") == null) {
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
                            Intent MovieListPage = new Intent(MovieListActivity.this, MovieListActivity.class);
                            MovieListPage.putExtra("movies", moviesJsonStr);
                            MovieListPage.putExtra("offset", offset+20);
                            MovieListPage.putExtra("maxRecords", maxRecords);
                            MovieListPage.putExtra("query", query);
                            startActivity(MovieListPage);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    error -> {
                        // error
                        Log.d("login.error", error.toString());
                    }) {
            };
            queue.add(loginRequest);
        }
//        tv.setText(query.getText());
        // important: queue.add is where the login request is actually sent

    }
}
