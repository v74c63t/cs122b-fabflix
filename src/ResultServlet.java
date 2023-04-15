
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
import java.util.Arrays;
import java.util.Map;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "ResultServlet", urlPatterns = "/api/result")
public class ResultServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

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
        // Testing out Servlet functions
        String genreId = request.getParameter("genreId");
        Map<String, String[]> paramMap = request.getParameterMap();

        // The log message can be found in localhost log
        request.getServletContext().log("getting genreId: " + genreId);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            // REPLACE LATER || ONLY TESTING genres
            String query = "select gim.movieId, title, year, director, rating " +
                    "from genres_in_movies as gim " +
                    "join movies as m " +
                    "join ratings as r " +
                    "on r.movieId = m.id " +
                    "and m.id = gim.movieId " +
                    "where genreId = ?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, genreId);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
//            while (rs.next()) {
//
//                String starId = rs.getString("starId");
//                String starName = rs.getString("name");
//                String starDob = rs.getString("birthYear");
//
//                String movieId = rs.getString("movieId");
//                String movieTitle = rs.getString("title");
//                String movieYear = rs.getString("year");
//                String movieDirector = rs.getString("director");
//                String movieRating = rs.getString("rating");
//
                // Create a JsonObject based on the data we retrieve from rs
//
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.addProperty("star_id", starId);
//                jsonObject.addProperty("star_name", starName);
//                jsonObject.addProperty("star_dob", starDob);
//                jsonObject.addProperty("movie_id", movieId);
//                jsonObject.addProperty("movie_title", movieTitle);
//                jsonObject.addProperty("movie_year", movieYear);
//                jsonObject.addProperty("movie_director", movieDirector);
//                jsonObject.addProperty("movie_rating", movieRating);
//
//                jsonArray.add(jsonObject);
//            }

            // TESTING TO GET ALL PARAMTERS of URL
            for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(entry.getKey(), Arrays.toString(entry.getValue()));
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

