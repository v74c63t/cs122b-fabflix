import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "StartTitleResultServlet", urlPatterns = "/api/by-start-title")
public class StartTitleResultServlet extends HttpServlet {
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

        // Get an instance of the current session
        HttpSession session = request.getSession();

        // Create an attribute "resultUrl" if it doesn't exist
        String resultUrl = (String) session.getAttribute("resultUrl");

        if (resultUrl == null) {
            resultUrl = "";
            session.setAttribute("resultUrl", resultUrl);
        }

        // Set the resultUrl to the current url
        resultUrl = request.getQueryString();
        session.setAttribute("resultUrl", resultUrl);

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String startTitle = request.getParameter("startTitle");
        // firstRecord used for offset
        String firstRecord = request.getParameter("firstRecord");
        // numRecords decide how many to display on each page
        // used for limit
        String numRecords = request.getParameter("numRecords");

        //sortBy
        String sortBy = request.getParameter("sortBy");
        String[] sort = sortBy.split(" ");

        // The log message can be found in localhost log
        request.getServletContext().log("getting startTitle: " + startTitle);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

//          Construct a query with parameter represented by "?"
            String query = "";
            if(startTitle.equals("*")) {
                query = String.join("",
                        "SELECT COUNT(*) OVER() AS maxRecords, r.movieId, title, year, director, rating ",
                        "FROM movies AS m ",
                        "LEFT join ratings AS r ",
                        "ON r.movieId = m.id ",
                        "WHERE title REGEXP '^[^A-Za-z0-9]' ",
                        "ORDER BY ", sort[0], " ", sort[1], ",", sort[2], " ", sort[3], " ",
                        "LIMIT ", numRecords, " ",
                        "OFFSET ", firstRecord, " ");

            }
            else{
                query = String.join("",
                        "SELECT COUNT(*) OVER() AS maxRecords, r.movieId, title, year, director, rating ",
                        "FROM movies AS m ",
                        "LEFT JOIN ratings AS r ",
                        "ON r.movieId = m.id ",
                        "WHERE title LIKE '", startTitle, "%' ",
                        "ORDER BY ", sort[0], " ", sort[1], ",", sort[2], " ", sort[3], " ",
                        "LIMIT ", numRecords, " ",
                        "OFFSET ", firstRecord, " ");
            }

            Statement statement = conn.createStatement();
            Statement statement2 = conn.createStatement();

            System.out.println(query);
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String movie_rating = rs.getString("rating");
                String movie_id = rs.getString("movieId");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String max_records = rs.getString("maxRecords");

                // New Query for getting stars sorted by the amount of movies they appear in
                query = String.join("",
                        "SELECT s.id, s.name ",
                        "FROM stars AS s, stars_in_movies AS sim ",
                        "WHERE s.id IN (SELECT s.id ",
                        "FROM stars AS s, stars_in_movies AS sim ",
                        "WHERE sim.movieId='", movie_id, "' ",
                        "AND s.id=sim.starId) ",
                        "AND s.id=sim.starId ",
                        "GROUP BY s.id ",
                        "ORDER BY COUNT(*) DESC, s.name ASC ",
                        "LIMIT 3; ");

                ResultSet newRS = statement2.executeQuery(query);

                ArrayList<String> starsArray = new ArrayList<>();

                while (newRS.next()) {
                    starsArray.add(newRS.getString("id") + "|" + newRS.getString("name"));
                }
                newRS.close();
                String stars = String.join(", ", starsArray);

                // New Query for getting genres sorted by name
                query = String.join("",
                        "select genreId, name ",
                        "from genres AS g ",
                        "join genres_in_movies AS gim ",
                        "on  g.id = gim.genreId ",
                        "WHERE gim.movieId='", movie_id, "'",
                        "ORDER BY name ",
                        "LIMIT 3; ");

                newRS = statement2.executeQuery(query);

                ArrayList<String> genresArray = new ArrayList<>();

                while (newRS.next()) {
                    genresArray.add(newRS.getString("genreId") + "|" + newRS.getString("name"));
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
                jsonObject.addProperty("max_records", max_records);
                jsonObject.addProperty("resultUrl", resultUrl);

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