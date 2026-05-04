package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A simple utility to test if the MySQL database is correctly set up.
 */
public class TestDatabase {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/ethiopian_youtube?useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root";
        String pass = "";

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("✅ SUCCESS: Connected to MySQL (WAMP)!");

            Statement stmt = conn.createStatement();
            // Check if the 'videos' table exists in MySQL
            ResultSet rs = stmt.executeQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'ethiopian_youtube' AND TABLE_NAME = 'videos'");

            if (rs.next()) {
                System.out.println("✅ SUCCESS: Table 'videos' exists!");
            } else {
                System.out.println("❌ ERROR: Table 'videos' does NOT exist. Please run the SQL script.");
            }

        } catch (Exception e) {
            System.err.println("❌ ERROR: Could not connect to MySQL.");
            System.err.println("Message: " + e.getMessage());
            System.err.println("\nPlease make sure:");
            System.err.println("1. WampServer is running (Icon should be green).");
            System.err.println("2. Database 'ethiopian_youtube' has been created in phpMyAdmin.");
            System.err.println("3. Username 'root' and empty password are correct.");
            System.err.println("4. MySQL is listening on port 3306.");
        }
    }
}
