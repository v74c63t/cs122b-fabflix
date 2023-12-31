import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.Gson;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.util.Random;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {

    private static final long serialVersionUID = 13L;
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Get instance of current session
        HttpSession session = request.getSession();

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get the most recent result page url
        String resultUrl = (String) session.getAttribute("resultUrl");
        String sessionId = session.getId();

        long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        // set a session attribute of the sale ids in payment
        ArrayList<Integer> saleIds = (ArrayList<Integer>) session.getAttribute("salesId");


        try (Connection conn = dataSource.getConnection()) {

            String query = String.join("",
                    "SELECT s.id, s.movieId, m.title, s.quantity, s.price, s.total ",
                    "FROM sales as s, movies as m ",
                    "WHERE s.id = ? AND s.movieId = m.id;");

            PreparedStatement statement = conn.prepareStatement(query);

            // Log to localhost log
            for (int saleId : saleIds) {

                // Declare our statement
                statement.setInt(1, saleId);

                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    int sale_id = rs.getInt("id");
                    String movie_id = rs.getString("movieId");
                    String movie_title = rs.getString("title");
                    int quantity = rs.getInt("quantity");
                    float price = rs.getFloat("price");
                    float movie_total = rs.getFloat("total");

                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("sale_id", String.valueOf(sale_id));
                    jsonObject.addProperty("movie_id", movie_id);
                    jsonObject.addProperty("movie_title", movie_title);
                    jsonObject.addProperty("movie_quantity", quantity);
                    jsonObject.addProperty("movie_price", price);
                    jsonObject.addProperty("movie_total", movie_total);
                    jsonObject.addProperty("resultUrl", resultUrl);
                    jsonArray.add(jsonObject);
                }
                rs.close();
            }
            session.removeAttribute("itemCart");
            session.removeAttribute("salesId");
            response.getWriter().write(jsonArray.toString());
            statement.close();

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
    }
}