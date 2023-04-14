import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 4L;

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
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email= request.getParameter("email");
        String password = request.getParameter("password");
        response.setContentType("application/json"); // Response mime type
        // The log message can be found in localhost log
        request.getServletContext().log("getting email: " + email);
        request.getServletContext().log("getting password: " + password);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
        JsonObject responseJsonObject = new JsonObject();
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = String.join("",
                    "SELECT *",
                    "FROM customers ",
                    "WHERE email=? AND password=?");

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, email);
            statement.setString(2, password);

            // Perform the query
            ResultSet rs = statement.executeQuery();

//            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            if(rs.next()) {
                // not sure whether to keep this information/add to user obj or not
//                String customerId = rs.getString("id");
//                String customerFirstName = rs.getString("firstName");
//                String customerLastName = rs.getString("lastName");
//                String customerCcId = rs.getString("ccId");
//                String customerAddress = rs.getString("address");
//
//                // Create a JsonObject based on the data we retrieve from rs
//                responseJsonObject.addProperty("customer_id", customerId);
//                responseJsonObject.addProperty("customer_first_name", customerFirstName);
//                responseJsonObject.addProperty("customer_last_name", customerLastName);
//                responseJsonObject.addProperty("customer_cc_id", customerCcId);
//                responseJsonObject.addProperty("customer_address", customerAddress);
                request.getSession().setAttribute("user", new User(email));

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
            }
            else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
//                if (!username.equals("a@email.com")) {
//                    responseJsonObject.addProperty("message", "User (" + username + ") doesn't exist");
//                } else {
//                    responseJsonObject.addProperty("message", "Incorrect password");
//                }
                responseJsonObject.addProperty("message", "User doesn't exist or incorrect password");
            }
        rs.close();
        statement.close();

        // Write JSON string to output
        response.getWriter().write(responseJsonObject.toString());
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
    }
}
