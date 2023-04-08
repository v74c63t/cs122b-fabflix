
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    // note: have to modify and adjust to use code
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = String.join("",
                    "SELECT movieId, rating, title, year, director ",
                    "FROM movies AS m, ratings AS r ",
                    "WHERE m.id=? ",
                    "AND m.id=r.movieId");

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String movieId = rs.getString("movieId");
                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");

                // Construct query for getting all stars
                // with parameter represented as "?"
                query = String.join("",
                        "SELECT starId, name as stars ",
                        "FROM stars AS s, stars_in_movies AS sim ",
                        "WHERE sim.movieId=? ",
                        "AND sim.starId=s.id");

                // Declare statement for inner queries
                PreparedStatement statement2 = conn.prepareStatement(query);
                // Putting id value as parameter in query
                statement2.setString(1, id);
                // Execute inner query
                ResultSet newRS = statement2.executeQuery();

                ArrayList<String> starsArray = new ArrayList<>();

                while (newRS.next()) {
                    starsArray.add(newRS.getString("starId") + "|" + newRS.getString("stars"));
                }
                newRS.close();
                statement2.close();
                String stars = String.join(", ", starsArray);

                // Repreat query execution for genres
                query = String.join("",
                        "SELECT name as genres ",
                        "FROM genres AS g, genres_in_movies AS gim ",
                        "WHERE gim.movieId=? ",
                        "AND gim.genreId=g.id");

                statement2 = conn.prepareStatement(query);
                statement2.setString(1, id);
                newRS = statement2.executeQuery();

                ArrayList<String> genresArray = new ArrayList<>();

                while (newRS.next()) {
                    genresArray.add(newRS.getString("genres"));
                }
                newRS.close();
                statement2.close();
                String genres = String.join(", ", genresArray);

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_director", movieDirector);
                jsonObject.addProperty("movie_stars", stars);
                jsonObject.addProperty("movie_genres", genres);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}

