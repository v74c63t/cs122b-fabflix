
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
import java.util.Random;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
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

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    // note: have to modify and adjust to use code
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Get instance of current session
        HttpSession session = request.getSession();

        // Get the most recent result page url
        String resultUrl = (String) session.getAttribute("resultUrl");

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
            String query = "SELECT * from stars s WHERE s.id = ?;";
            PreparedStatement basicInfo = conn.prepareStatement(query);
            basicInfo.setString(1, id);
            ResultSet rs = basicInfo.executeQuery();
            String starName = "";
            String starDob = "";
            if(rs.next()) {
                starName = rs.getString("name");
                starDob = rs.getString("birthYear");
            }
            basicInfo.close();


            query = "SELECT * from stars as s, stars_in_movies as sim, movies as m " +
                    "where m.id = sim.movieId and sim.starId = s.id and s.id = ? order by m.year desc, m.title asc;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();


            // Iterate through each row of rs
            while (rs.next()) {

//                String starId = rs.getString("starId");
//                String starName = rs.getString("name");
//                String starDob = rs.getString("birthYear");

                String movieId = rs.getString("movieId");
                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");

                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("star_id", id);
                jsonObject.addProperty("star_name", starName);
                jsonObject.addProperty("star_dob", starDob);
                jsonObject.addProperty("movie_id", movieId);
                jsonObject.addProperty("movie_title", movieTitle);
                jsonObject.addProperty("movie_year", movieYear);
                jsonObject.addProperty("movie_director", movieDirector);
                jsonObject.addProperty("resultUrl", resultUrl);

                jsonArray.add(jsonObject);
            }
            if(jsonArray.isEmpty()) {
                if(starName != "") {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("star_id", id);
                    jsonObject.addProperty("star_name", starName);
                    jsonObject.addProperty("star_dob", starDob);
                    jsonObject.addProperty("resultUrl", resultUrl);
                    jsonArray.add(jsonObject);
                }
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


