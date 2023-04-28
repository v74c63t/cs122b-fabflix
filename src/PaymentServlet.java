

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
        System.out.println(request.getParameter("expirationDate"));
        System.out.println(request.getParameter("ccId"));
        System.out.println(request.getParameter("first"));
        System.out.println(request.getParameter("last"));
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();
        User customer = (User) request.getSession().getAttribute("user");
        int customerId = customer.getId();
        HashMap<String, HashMap<String,Double>> itemCart = (HashMap<String, HashMap<String,Double>>)request.getSession().getAttribute("itemCart");
        if(customer.getFirstName().equals(firstName) || customer.getLastName().equals(lastName)){
            responseJsonObject.addProperty("status", "fail");
            // Log to localhost log
            request.getServletContext().log("Verifying Failed");
            responseJsonObject.addProperty("message", "Invalid credit card information");
            // Write JSON string to output
            response.getWriter().write(responseJsonObject.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);
        }
        else if(itemCart == null) {
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
//                    LocalDate currDate = LocalDate.now();
//                    for(String movieId: itemCart.keySet()) {
//                        LocalDate currDate = LocalDate.now();
//                        // figure out if we plan on updating the sales table to include more info
//                        // insert each item into sale table
                                // INSERT INTO Sales(customerId, movieId, saleDate, quantity, price, (maybe total but it can be calulcated anyways so it doesnt rly matter))
                                // VALUES(customerId, movieId, currDate, itemCart.get(movieId).get('quantity'), itemCart.get(movieId).get('price')
//                        // add sale id for that item into an arraylist to be set in session attribute
                                // use SELECT LAST_INSERTED_ID()
//                    }
                    // Get instance of current session
                    HttpSession session = request.getSession();
                    HashMap<String, HashMap<String,Double>> itemCart = (HashMap<String, HashMap<String,Double>>) session.getAttribute("itemCart");
                    ArrayList<HashMap<String, String>> salesCart = (ArrayList<HashMap<String, String>>) session.getAttribute("salesCart");

                    if (salesCart == null) {
                        salesCart = new ArrayList<HashMap<String, String>>();
                        for (Map.Entry<String,HashMap<String,Double>> entry: itemCart.entrySet() ) {
//                            String query2 = String.join("",
//                                    "INSERT INTO sales (customerId, movieId, saleDate, quantity, price, total) ",
//                                    "VALUES (?, ?, CURRENT_DATE(), ?, ?, ?);");
//
//                            PreparedStatement statement2 = conn.createStatement(query2);

                            HashMap<String,String> individualSale = new HashMap<String,String>();
                            individualSale.put("movieId", entry.getKey());
                            individualSale.put("quantity", String.valueOf(entry.getValue().get("quantity")));
                            individualSale.put("price", String.valueOf(entry.getValue().get("price")));
                            individualSale.put("total", String.valueOf(Math.round((entry.getValue().get("quantity"))*(entry.getValue().get("price")))*100/100);
                        }

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