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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {

    private static final long serialVersionUID = 10L;
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
        ArrayList<Integer> saleIds = (ArrayList<Integer>) session.getAttribute("saleIds");
        HashMap<String, HashMap<String,Double>> itemCart = (HashMap<String, HashMap<String,Double>>) session.getAttribute("itemCart");


        try (Connection conn = dataSource.getConnection()) {

            // Log to localhost log
//            request.getServletContext().log("getting " + itemCart.size() + " items");
            JsonArray previousItemsJsonArray = new JsonArray();
//            previousItems.forEach(previousItemsJsonArray::add);
//            for ( int i = 0; i < previousItems.size(); i++) {
            for (int saleId : saleIds) {
                System.out.println(saleId);
                Statement statement = conn.createStatement();
                String query = String.join("",
                        "SELECT s.id, s.movieId, m.title",
                        "FROM sales as s, movies as m",
                        "WHERE s.id = ", String.valueOf(saleId), " AND s.movieId = m.id;");
                System.out.println(query);
                ResultSet rs = statement.executeQuery(query);
                if (rs.next()) {
                    String sale_id = rs.getString("id");
                    String movie_id = rs.getString("movieId");
                    String movie_title = rs.getString("title");
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("sale_id", sale_id);
                    jsonObject.addProperty("movie_id", movie_id);
                    jsonObject.addProperty("movie_title", movie_title);
                    jsonObject.addProperty("movie_quantity", itemCart.get(movie_id).get("quantity"));
                    jsonObject.addProperty("movie_price", itemCart.get(movie_id).get("price"));
                    jsonObject.addProperty("resultUrl", resultUrl);
                    jsonArray.add(jsonObject);
                }
                rs.close();
                statement.close();
            }
            session.removeAttribute("itemCart");
            session.removeAttribute("saleIds");
//            responseJsonObject.add("previousItems", jsonArray);

            // write all the data into the jsonObject
//            response.getWriter().write(responseJsonObject.toString());
            response.getWriter().write(jsonArray.toString());

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