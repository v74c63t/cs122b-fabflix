import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import java.util.Random;

@WebServlet("/autocomplete")
public class Autocomplete extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

//    String[] datasources = {"java:comp/env/jdbc/moviedb_master", "java:comp/env/jdbc/moviedb_slave"};
//
//    Random random = new Random();

    public void init(ServletConfig config) {
        try {
//            int rIndex = random.nextInt(datasources.length);
//            String ds = datasources[rIndex];

            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /*
     *
     * Match the query against movie titles and return a JSON response.
     *
     * The JSON response look like this:
     * [
     * 	{ "value": title(year), "data": { "movieId": movieId } },
     * ]
     *
     * The format is like this because it can be directly used by the
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     *
     *
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = dataSource.getConnection()) {
            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars

            // Get a list of tokens from input query
            String[] queries = query.split("\\s+");

            // Construct a string for each token parameter
            StringBuffer numQueries = new StringBuffer();
            for (int i = 0; i < queries.length; ++i) {
                numQueries.append("? ");
            }

            // Query for full-text search
            String sqlQuery = "SELECT id, title, year FROM movies WHERE MATCH(title) AGAINST(" + numQueries + "IN BOOLEAN MODE) ORDER BY title LIMIT 10;";

            // Create a statement
            PreparedStatement statement = conn.prepareStatement(sqlQuery);

            // Set all parameters denoted "?" with associated token
            for (int i = 0; i < queries.length; ++i) {
                statement.setString(i+1,"+" + queries[i] + "*");
            }

            // Execute query
            ResultSet rs = statement.executeQuery();


            while ( rs.next() ) {
                String movieId = rs.getString("id");
                String title = rs.getString("title");
                String year = rs.getString("year");

                JsonObject jsonObject = new JsonObject();
                JsonObject dataJsonObject = new JsonObject();

                dataJsonObject.addProperty("movieId", movieId);
                jsonObject.addProperty("value", title + " (" + year + ")" );
                jsonObject.add("data", dataJsonObject);
                jsonArray.add(jsonObject);
            }

            response.getWriter().write(jsonArray.toString());
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }



}
