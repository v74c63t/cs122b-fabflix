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

            // Declare our statement
            Statement statement = conn.createStatement();

            // Query to get a list of tables in the database
            String query = "SHOW tables;";

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String table = rs.getString(1);

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("table_name", table);

                Statement statement2 = conn.createStatement();
                String query2 = "select * from " + table;
                ResultSet rs2 = statement2.executeQuery(query2);
                ResultSetMetaData resultSetMetaData = rs2.getMetaData();
                // use getTableName, getColumnName, getColumnType to get all the info

//                NEED TO IMPLEMENT THIS
//                      GETTING ERRORS DOING
//                          String field = rs.getString(1);
//                          String field = rs.getString("Field");


//                // Query to get metadata of each table
//                query = "DESCRIBE " + table;
//
//                ResultSet newRS = statement2.executeQuery(query);
//
//                JsonArray fieldsArray = new JsonArray();
//                JsonArray typesArray = new JsonArray();
//
//                while (newRS.next()) {
//
//                    String field = rs.getString(1);
//                    System.out.println(field);
//                    String type = rs.getString(2);
//                    System.out.println(type);
//
//                    fieldsArray.add(field);
//                    typesArray.add(type);
//                }
//
//                jsonObject.add("fields", fieldsArray);
//                jsonObject.add("types", fieldsArray);

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