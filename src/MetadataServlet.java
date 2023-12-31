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
import java.sql.*;
import java.util.Random;


// Declaring a WebServlet called MainInitServlet, which maps to url "/api/metadata"
// This is used to get all metadata of our database
@WebServlet(name = "MetadataServlet", urlPatterns = "/api/metadata")
public class MetadataServlet extends HttpServlet {
    private static final long serialVersionUID = 5L;

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

        // Get instance of current session
        HttpSession session = request.getSession();

        // Get the most recent result page url
        String resultUrl = (String) session.getAttribute("resultUrl");

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Query to get a list of tables in the database
            String query = "SHOW tables;";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String table = rs.getString(1);

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();

                // Get Query Result for Metadata for each table
                Statement statement2 = conn.createStatement();
                String query2 = "DESCRIBE " + table;
                ResultSet rs2 = statement2.executeQuery(query2);
                ResultSetMetaData resultSetMetaData = rs2.getMetaData();

                // JsonArray for each field/type
                JsonArray fieldsArray = new JsonArray();
                JsonArray typesArray = new JsonArray();

                // Iterate through each table metadata and add to JsonArray
                while ( rs2.next() ) {
                    fieldsArray.add(rs2.getString(1));
                    typesArray.add(rs2.getString(2));
                }

                // Creates that holds table_name, array of fields/types
                jsonObject.addProperty("table_name", table);
                jsonObject.add("fields", fieldsArray);
                jsonObject.add("types", typesArray);

                jsonArray.add(jsonObject);
                rs2.close();
                statement2.close();
            }
            rs.close();
            statement.close();


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