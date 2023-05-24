package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import org.json.JSONArray;

import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);
        // TODO: this should be retrieved from the backend server
//        final ArrayList<Movie> movies = new ArrayList<>();
//        movies.add(new Movie("The Terminal", "2004"));
//        movies.add(new Movie("The Final Season", "2007"));
        Bundle extras = getIntent().getExtras();
        String jsonStr = extras.getString("movies");
        Gson gson = new Gson();
        final ArrayList<Movie> movies = gson.fromJson(jsonStr, new TypeToken<ArrayList<Movie>>(){}.getType());
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
}