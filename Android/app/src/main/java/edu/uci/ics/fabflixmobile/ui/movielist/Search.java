package edu.uci.ics.fabflixmobile.ui.movielist;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

}
