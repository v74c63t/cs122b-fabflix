
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/movies"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();
            // Declare statement for inner query
            Statement statement2 = conn.createStatement();

            String query = String.join("",
                    "SELECT rating, id, title, year, director ",
                    "FROM movies AS m, ratings AS r ",
                    "WHERE m.id=r.movieId ",
                    "ORDER BY rating DESC ",
                    "LIMIT 20");

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movie_rating = rs.getString("rating");
                String movie_id = rs.getString("id");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");

                // New Query for getting top 3 stars
                query = String.join("",
                        "SELECT starId, name ",
                        "FROM stars AS s, stars_in_movies AS sim ",
                        "WHERE sim.movieId='", movie_id, "' ",
                        "AND s.id=sim.starId ",
                        "LIMIT 3");

                ResultSet newRS = statement2.executeQuery(query);

                ArrayList<String> starsArray = new ArrayList<>();

                while (newRS.next()) {
                    starsArray.add(newRS.getString("starId") + "|" + newRS.getString("name"));
                }
                newRS.close();
                String stars = String.join(", ", starsArray);

                // New Query for getting top 3 genres
                query = String.join("",
                        "SELECT id, name ",
                        "FROM genres AS g, genres_in_movies AS gim ",
                        "WHERE gim.movieId='", movie_id, "' ",
                        "AND g.id=gim.genreId ",
                        "ORDER BY name ",
                        "LIMIT 3;");

                newRS = statement2.executeQuery(query);

                ArrayList<String> genresArray = new ArrayList<>();

                while (newRS.next()) {
//                    genresArray.add(newRS.getString("name"));
                    genresArray.add(newRS.getString("id") + "|" + newRS.getString("name"));
                }
                newRS.close();
                String genres = String.join(", ", genresArray);

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_rating", movie_rating);
                jsonObject.addProperty("movie_id", movie_id);
                jsonObject.addProperty("movie_title", movie_title);
                jsonObject.addProperty("movie_year", movie_year);
                jsonObject.addProperty("movie_director", movie_director);
                jsonObject.addProperty("movie_stars", stars);
                jsonObject.addProperty("movie_genres", genres);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();
            statement2.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}