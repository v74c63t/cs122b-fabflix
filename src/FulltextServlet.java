
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
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


// Declaring a WebServlet called StarsServlet, which maps to url "/api/movies"
@WebServlet(name = "FulltextServlet", urlPatterns = "/api/fulltext")
public class FulltextServlet extends HttpServlet {
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Get instance of current session
        HttpSession session = request.getSession();

        // Get the most recent result page url
//        String resultUrl = (String) session.getAttribute("resultUrl");

        // Create an attribute "resultUrl" if it doesn't exists
        String resultUrl = (String) session.getAttribute("resultUrl");

        if (resultUrl == null) {
            resultUrl = "";
            session.setAttribute("resultUrl", resultUrl);
        }

        // Set the resultUrl to the current url
        resultUrl = request.getQueryString();
        session.setAttribute("resultUrl", resultUrl);

        String firstRecord = request.getParameter("firstRecord");
        String numRecords = request.getParameter("numRecords");
        String sortBy = request.getParameter("sortBy");
        String[] sort = sortBy.split(" ");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            if (sort[0].equals("title")) {
                if (!sort[2].equals("rating")) {
                    throw new Exception("Invalid sorting criteria");
                }
            } else if (sort[0].equals("rating")) {
                if (!sort[2].equals("title")) {
                    throw new Exception("Invalid sorting criteria");
                }
            } else {
                throw new Exception("Invalid sorting criteria");
            }
            if (!(sort[1].equals("ASC") || sort[1].equals("DESC")) && !(sort[3].equals("ASC") || sort[3].equals("DESC"))) {
                throw new Exception("Invalid sorting criteria");
            }

            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }

            // search on superheroes and add the results to JSON Array
            // this example only does a substring match
            // TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars

            // Get a list of tokens from input query
            String[] queries = query.split("\\s+");

            // Construct a string for each token parameter
            StringBuffer numQueries = new StringBuffer();
            for (int i = 0; i < queries.length; ++i) {
                numQueries.append("? ");
            }

            // Query for full-text search
            // Not sure about  "*"
            // Maybe just title if we are only getting title for suggestions
//            String sqlQuery = "SELECT * FROM movies WHERE MATCH(title) AGAINST(" + numQueries + "IN BOOLEAN MODE) LIMIT 10;";
            String sqlQuery = String.join("",
                    "SELECT COUNT(*) over() AS maxRecords, m.id AS movieId, title, year, director, rating ",
                    "FROM movies AS m ",
                    "LEFT JOIN ratings AS r ",
                    "ON r.movieId = m.id ",
                    "WHERE MATCH(title) AGAINST(", numQueries, "IN BOOLEAN MODE) ",
                    "ORDER BY ", sort[0], " ", sort[1], ",", sort[2], " ", sort[3], " ",
                    "LIMIT ? ",
                    "OFFSET ? ");

            // Create a statement
            PreparedStatement statement = conn.prepareStatement(sqlQuery);

            sqlQuery = String.join("",
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
            PreparedStatement statement2 = conn.prepareStatement(sqlQuery);

            // New Query for getting genres
            sqlQuery = String.join("",
                    "select genreId, name ",
                    "from genres AS g ",
                    "join genres_in_movies AS gim ",
                    "on  g.id = gim.genreId ",
                    "WHERE gim.movieId=? ",
                    "ORDER BY name ",
                    "LIMIT 3;");

            PreparedStatement statement3 = conn.prepareStatement(sqlQuery);

            // Set all parameters denoted "?" with associated token
            int i;
            for (i = 0; i < queries.length; ++i) {
                statement.setString(i + 1, "+" + queries[i] + "*");
            }
            statement.setInt(i + 1, Integer.parseInt(numRecords));
            statement.setInt(i + 2, Integer.parseInt(firstRecord));

            // Execute query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray1 = new JsonArray();

            while (rs.next()) {
                String movie_rating = rs.getString("rating");
                String movie_id = rs.getString("movieId");
                String movie_title = rs.getString("title");
                String movie_year = rs.getString("year");
                String movie_director = rs.getString("director");
                String max_records = rs.getString("maxRecords");

                statement2.setString(1, movie_id);
                ResultSet newRS = statement2.executeQuery();

                ArrayList<String> starsArray = new ArrayList<>();

                while (newRS.next()) {
                    starsArray.add(newRS.getString("id") + "|" + newRS.getString("name"));
                }
                newRS.close();
                String stars = String.join(", ", starsArray);

                statement3.setString(1, movie_id);

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

//            response.getWriter().write(jsonArray.toString());

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
//
//        } catch (Exception e) {
//            System.out.println(e);
//            response.sendError(500, e.getMessage());
//        }
//    }
}