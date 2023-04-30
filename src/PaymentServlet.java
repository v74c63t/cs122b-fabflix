

import com.google.gson.JsonObject;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDate;
import java.util.Map;

@WebServlet(name = "PaymentServlet", urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {

    private static final long serialVersionUID = 11L;

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
        String ccId = request.getParameter("ccId");
        String firstName = request.getParameter("first");
        String lastName = request.getParameter("last");

        String expirationDate = request.getParameter("expirationDate");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();
        User customer = (User) request.getSession().getAttribute("user");
        int customerId = customer.getId();

        HashMap<String, HashMap<String,Double>> itemCart = (HashMap<String, HashMap<String,Double>>)request.getSession().getAttribute("itemCart");

        if(itemCart == null) {
            responseJsonObject.addProperty("status", "fail");
            // Log to localhost log
            request.getServletContext().log("Empty cart");
            responseJsonObject.addProperty("message", "Cart is empty");
            // Write JSON string to output
            response.getWriter().write(responseJsonObject.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);
        }
        else if (itemCart.isEmpty()) {
            responseJsonObject.addProperty("status", "fail");
            // Log to localhost log
            request.getServletContext().log("Empty cart");
            responseJsonObject.addProperty("message", "Cart is empty");
            // Write JSON string to output
            response.getWriter().write(responseJsonObject.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);
        }

        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */
        else {
            try (Connection conn = dataSource.getConnection()) {
                // Get a connection from dataSource

                // Construct a query with parameter represented by "?"
                String query = String.join("",
                        "SELECT *",
                        "FROM creditcards ",
                        "WHERE id=? AND firstName=? AND lastName=? AND expirationDate=? ");

                // Declare our statement
                PreparedStatement statement = conn.prepareStatement(query);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                statement.setString(1, ccId);
                statement.setString(2, firstName);
                statement.setString(3, lastName);
                statement.setString(4, expirationDate);

                // Perform the query
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                    // Get instance of current session
                    HttpSession session = request.getSession();

                    // Get or create a final sales cart
                    ArrayList<Integer> salesId = (ArrayList<Integer>) session.getAttribute("salesId");

                    // Create a sales cart and add items in if there isn't one
                    if (salesId == null) {
                        salesId = new ArrayList<Integer>();
                        session.setAttribute("salesId", salesId);
                    }

                    // Iterate through each item in itemCart
                    //      to get sale info
                    for (Map.Entry<String,HashMap<String,Double>> entry: itemCart.entrySet() ) {

                        String insertQuery = String.join("",
                                "INSERT INTO sales (customerId, movieId, saleDate, quantity, price, total) ",
                                "VALUES (?, ?, ?, ?, ?, ?);");

                        // Statement for inserting into table
                        PreparedStatement insertStatement = conn.prepareStatement(insertQuery);

                        // Setting parameters for insert query
                        insertStatement.setString(1, String.valueOf(customerId));
                        insertStatement.setString(2, entry.getKey());
                        insertStatement.setString(3, LocalDate.now().toString());
                        insertStatement.setString(4, String.valueOf(entry.getValue().get("quantity")));
                        insertStatement.setString(5, String.valueOf(entry.getValue().get("price")));
                        insertStatement.setString(6, String.valueOf( new BigDecimal((entry.getValue().get("quantity"))*(entry.getValue().get("price"))).setScale(2, RoundingMode.HALF_UP)));

                        // Executes insert statement query
                        int updateRS = insertStatement.executeUpdate();

                        // Create statement for getting last inserted id
                        Statement getIdStatement = conn.createStatement();

                        // Query statement for getting last inserted id
                        String getIdQuery = "SELECT last_insert_id()";

                        // Execute statement for getting last inserted id
                        ResultSet getIdRS = getIdStatement.executeQuery(getIdQuery);

                        // Get last inserted id to salesCart
                        if (getIdRS.next()) {
                            salesId.add(getIdRS.getInt(1));
                        }
                        // Add HashMap to an Array and update "salesCart"
                        session.setAttribute("salesId", salesId);

                        // Close all statements/executes
                        insertStatement.close();
                        getIdStatement.close();
                        getIdRS.close();
                    }

                } else {
                    responseJsonObject.addProperty("status", "fail");
                    // Log to localhost log
                    request.getServletContext().log("Verifying Failed");
                    responseJsonObject.addProperty("message", "Invalid credit card information");
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
}
