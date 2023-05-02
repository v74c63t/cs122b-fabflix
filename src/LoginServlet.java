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
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            out.println("<html>");
            out.println("<head><title>Error</title></head>");
            out.println("<body>");
            out.println("<p>recaptcha verification error</p>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</body>");
            out.println("</html>");

            out.close();
            return;
        }

        String email= request.getParameter("email");
        String password = request.getParameter("password");
        response.setContentType("application/json"); // Response mime type
        // The log message can be found in localhost log
        request.getServletContext().log("getting email: " + email);
        request.getServletContext().log("getting password: " + password);

        JsonObject responseJsonObject = new JsonObject();
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query = String.join("",
                    "SELECT *",
                    "FROM customers ",
                    "WHERE email=? ");

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, email);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            if(rs.next()) {
                if (rs.getString("password").equals(password)) {
                    int customerId = rs.getInt("id");
                    String customerFirstName = rs.getString("firstName");
                    String customerLastName = rs.getString("lastName");
                    // additional information is stored so it can be used for payment confirmation later
                    request.getSession().setAttribute("user", new User(email, customerFirstName, customerLastName, customerId));
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                }
                else {
                    // Login fail
                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Login failed");

                    responseJsonObject.addProperty("message", "Incorrect password");
                }

            }
            else {
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");

                responseJsonObject.addProperty("message", "User with that email doesn't exist");

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
