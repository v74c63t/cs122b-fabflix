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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SearchResultServlet", urlPatterns = "/api/by-search")
public class SearchResultServlet extends HttpServlet {
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
        Map<String, String[]> parameterMap = request.getParameterMap();


        // The log message can be found in localhost log
        request.getServletContext().log("getting parameters: " + parameterMap.toString());

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

//          Construct a query with parameter represented by "?"
            String query = "WITH starMovies AS " +
                    "(SELECT m.id AS movieId, title, year, director, starId, name AS star, rating " +
                    "FROM movies AS m " +
                    "JOIN stars_in_movies AS sim " +
                    "JOIN stars AS s " +
                    "JOIN ratings AS r " +
                    "ON m.id = sim.movieId " +
                    "AND sim.starId = s.id " +
                    "AND r.movieId = m.id), " +
                    "distinctResult AS (SELECT DISTINCT movieId, title, year, director, rating " +
                    "FROM starMovies AS sm ";

            ArrayList<String> queryParameters = new ArrayList<String>();
            String limit = "";
            String offset = "";
            String order = "";
            if (!parameterMap.isEmpty()) {
                query = query.concat("WHERE ");
                Iterator<Map.Entry<String, String[]>> itr = parameterMap.entrySet().iterator();

                // Get first parameter from url
                if (itr.hasNext()) {
                    Map.Entry<String, String[]> entry = itr.next();

                    if (entry.getKey().equals("title") || entry.getKey().equals("director") || entry.getKey().equals("star")) {
                        query = query.concat(entry.getKey().concat(" LIKE ? "));
                        queryParameters.add("%" + entry.getValue()[0] + "%");
                    }else if (entry.getKey().equals("year")) {
                        query = query.concat(entry.getKey().concat(" = ? "));
                        queryParameters.add(entry.getValue()[0]);
                    }
                }

                // Get parameters from url starting from 2nd parameter
                while(itr.hasNext())
                {
                    Map.Entry<String, String[]> entry = itr.next();

                    if (entry.getKey().equals("title") || entry.getKey().equals("director") || entry.getKey().equals("star")) {
                        query = query.concat(entry.getKey() + " LIKE ? ");
                        queryParameters.add("AND %" + entry.getValue()[0] + "%");
                    }else if (entry.getKey().equals("year")) {
                        query = query.concat("AND " + entry.getKey() + " = ? ");
                        queryParameters.add(entry.getValue()[0]);
                    }else if (entry.getKey().equals("sortBy")) {
                        query = query.concat(") SELECT COUNT(*) over() AS maxRecords, movieId, title, year, director, rating FROM distinctResult ");
                        String[] sort = entry.getValue()[0].split(" ");
                        order = "ORDER BY " + sort[0] + " " + sort[1] + ", " + sort[2] + " " + sort[3] + " ";
                        query = query.concat(order);

                    } else if (entry.getKey().equals("numRecords")) {
                        limit = "LIMIT " + entry.getValue()[0] + " ";
                        query = query.concat(limit);
                    } else if (entry.getKey().equals("firstRecord")) {
                        offset = "OFFSET " + entry.getValue()[0];
                        query = query.concat(offset);
                    }

                }
            }

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);
            Statement statement2 = conn.createStatement();


            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            for (int i = 0; i < queryParameters.size(); ++i) {
                statement.setString(i+1, queryParameters.get(i));
            }

            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String movie_rating = rs.getString("rating");
                String movie_id = rs.getString("movieId");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String max_records = rs.getString("maxRecords");

                // New Query for getting stars
//                query = String.join("",
//                        "select starId, name ",
//                        "from stars as s ",
//                        "join stars_in_movies as sim ",
//                        "on id = starId ",
//                        "where sim.movieId='", movie_id, "'");
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

                // New Query for getting genres
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