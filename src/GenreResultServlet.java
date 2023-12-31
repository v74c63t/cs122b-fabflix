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
import java.util.Random;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "GenreResultServlet", urlPatterns = "/api/by-genre")
public class GenreResultServlet extends HttpServlet {
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
        String genreId = request.getParameter("genreId");

        //figure out how to add this info into url
        // firstRecord used for offset
        String firstRecord = request.getParameter("firstRecord");
        // numRecords decide how many to display on each page
        // used for limit
        String numRecords = request.getParameter("numRecords");
        String sortBy = request.getParameter("sortBy");
        String[] sort = sortBy.split(" ");

//        System.out.println(request.getParameter("sortBy"));
        // The log message can be found in localhost log
        request.getServletContext().log("getting genreId: " + genreId);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            if(sort[0].equals("title")) {
                if(!sort[2].equals("rating")) {
                    throw new Exception("Invalid sorting criteria");
                }
            }
            else if(sort[0].equals("rating")) {
                if(!sort[2].equals("title")) {
                    throw new Exception("Invalid sorting criteria");
                }
            }
            else {
                throw new Exception("Invalid sorting criteria");
            }
            if(!(sort[1].equals("ASC") || sort[1].equals("DESC")) && !(sort[3].equals("ASC") || sort[3].equals("DESC"))){
                throw new Exception("Invalid sorting criteria");
            }

//          Construct a query with parameter represented by "?"
            String query = String.join("",
                    "SELECT COUNT(*) over() AS maxRecords, gim.movieId, title, year, director, rating ",
                    "FROM genres_in_movies AS gim ",
                    "JOIN movies AS m ",
                    "ON m.id = gim.movieId ",
                    "LEFT JOIN ratings AS r ",
                    "ON r.movieId = m.id ",
                    "WHERE genreId= ? ",
                    "ORDER BY ", sort[0], " ", sort[1], ",", sort[2], " ", sort[3], " ",
                    "LIMIT ? ",
                    "OFFSET ? ");

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, genreId);
            statement.setInt(2, Integer.parseInt(numRecords));
            statement.setInt(3, Integer.parseInt(firstRecord));

            ResultSet rs = statement.executeQuery();

            // New Query for getting stars
            query = String.join("",
                    "SELECT s.id, s.name ",
                    "FROM stars AS s, stars_in_movies AS sim ",
                    "WHERE s.id IN (SELECT s.id ",
                    "FROM stars AS s, stars_in_movies AS sim ",
                    "WHERE sim.movieId=? ",
                    "AND s.id=sim.starId) ",
                    "AND s.id=sim.starId ",
                    "GROUP BY s.id ",
                    "ORDER BY COUNT(*) DESC, s.name ASC ",
                    "LIMIT 3;");
            PreparedStatement statement2 = conn.prepareStatement(query);

            // New Query for getting genres
            query = String.join("",
                    "select genreId, name ",
                    "from genres AS g ",
                    "join genres_in_movies AS gim ",
                    "on  g.id = gim.genreId ",
                    "WHERE gim.movieId=? ",
                    "ORDER BY name ",
                    "LIMIT 3;");

            PreparedStatement statement3 = conn.prepareStatement(query);

//            System.out.println(query);

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String movie_rating = rs.getString("rating");
                String movie_id = rs.getString("movieId");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String max_records = rs.getString("maxRecords");

                statement2.setString(1,movie_id);
                ResultSet newRS = statement2.executeQuery();

                ArrayList<String> starsArray = new ArrayList<>();

                while (newRS.next()) {
                    starsArray.add(newRS.getString("id") + "|" + newRS.getString("name"));
                }
                newRS.close();
                String stars = String.join(", ", starsArray);

                statement3.setString(1,movie_id);

                newRS = statement3.executeQuery();

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
            statement2.close();
            statement3.close();

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

