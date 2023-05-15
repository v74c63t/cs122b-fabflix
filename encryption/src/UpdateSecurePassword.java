import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

public class UpdateSecurePassword {

    /*
     *
     * This program updates your existing moviedb customers table to change the
     * plain text passwords to encrypted passwords.
     *
     * You should only run this program **once**, because this program uses the
     * existing passwords as real passwords, then replace them. If you run it more
     * than once, it will treat the encrypted passwords as real passwords and
     * generate wrong values.
     *
     */
    public static void main(String[] args) throws Exception {

        String loginUser = "mytestuser";
        String loginPasswd = "My6$Password";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
        Statement statement = connection.createStatement();
        Statement statement2 = connection.createStatement();

        // change the customers table password column from VARCHAR(20) to VARCHAR(128)
        String alterQuery = "ALTER TABLE customers MODIFY COLUMN password VARCHAR(128)";
        String alterQuery2 = "ALTER TABLE employees MODIFY COLUMN password VARCHAR(128)";
        int alterResult = statement.executeUpdate(alterQuery);
        int alterResult2 = statement2.executeUpdate(alterQuery2);
        System.out.println("altering customers table schema completed, " + alterResult + " rows affected");
        System.out.println("altering customers table schema completed, " + alterResult2 + " rows affected");

        // get the ID and password for each customer
        String query = "SELECT id, password from customers";
        String query2 = "SELECT email, password from employees";

        ResultSet rs = statement.executeQuery(query);
        ResultSet rs2 = statement2.executeQuery(query2);

        // we use the StrongPasswordEncryptor from jasypt library (Java Simplified Encryption)
        //  it internally use SHA-256 algorithm and 10,000 iterations to calculate the encrypted password
        PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
        PasswordEncryptor passwordEncryptor2 = new StrongPasswordEncryptor();

        ArrayList<String> updateQueryList = new ArrayList<>();
        ArrayList<String> updateQueryList2 = new ArrayList<>();

        System.out.println("encrypting password (this might take a while)");
        while (rs.next()) {
            // get the ID and plain text password from current table
            String id = rs.getString("id");
            String password = rs.getString("password");

            // encrypt the password using StrongPasswordEncryptor
            String encryptedPassword = passwordEncryptor.encryptPassword(password);

            // generate the update query
            String updateQuery = String.format("UPDATE customers SET password='%s' WHERE id=%s;", encryptedPassword,
                    id);
            updateQueryList.add(updateQuery);
        }
        rs.close();

        while (rs2.next()) {
            // get the ID and plain text password from current table
            String email = rs2.getString("email");
            String password = rs2.getString("password");

            // encrypt the password using StrongPasswordEncryptor
            String encryptedPassword = passwordEncryptor2.encryptPassword(password);

            // generate the update query
            String updateQuery = String.format("UPDATE employees SET password='%s' WHERE email=%s;", encryptedPassword,
                    id);
            updateQueryList2.add(updateQuery);
        }
        rs2.close();

        // execute the update queries to update the password
        System.out.println("updating password");
        int count = 0;
        for (String updateQuery : updateQueryList) {
            int updateResult = statement.executeUpdate(updateQuery);
            count += updateResult;
        }
        int count2 = 0;
        for (String updateQuery : updateQueryList) {
            int updateResult = statement2.executeUpdate(updateQuery2);
            count2 += updateResult;
        }
        System.out.println("updating password completed, " + count + " rows affected");
        System.out.println("updating password completed, " + count2 + " rows affected");

        statement.close();
        statement2.close();
        connection.close();

        System.out.println("finished");

    }

}