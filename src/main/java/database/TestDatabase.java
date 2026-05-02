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
        String url = "jdbc:sqlserver://10.198.73.40\\SQLEXPRESS:1433;databaseName=YouTubeEthiopia;encrypt=false;trustServerCertificate=true;";
        String user = "root";
        String pass = "";

        try {
            // Explicitly load the SQL Server driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("✅ SUCCESS: Connected to SQL Server!");

            Statement stmt = conn.createStatement();
            // Check if the 'videos' table exists in SQL Server
            ResultSet rs = stmt.executeQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'videos'");

            if (rs.next()) {
                System.out.println("✅ SUCCESS: Table 'videos' exists!");
            } else {
                System.out.println("❌ ERROR: Table 'videos' does NOT exist. Please run the SQL script.");
            }

        } catch (Exception e) {
            System.err.println("❌ ERROR: Could not connect to SQL Server.");
            System.err.println("Message: " + e.getMessage());
            System.err.println("\nPlease make sure:");
            System.err.println("1. SQL Server SQLEXPRESS instance is running.");
            System.err.println("2. Database 'YouTubeEthiopia' is created.");
            System.err.println("3. Username 'yt_admin' and Password 'Admin123' are correct.");
            System.err.println("4. Remote connections are enabled in SQL Server Configuration Manager.");
        }
    }
}
