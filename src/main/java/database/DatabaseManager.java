package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager handles all MySQL database operations for video metadata.
 * It follows the DAO (Data Access Object) pattern.
 */
public class DatabaseManager {
    // Database credentials for MS SQL Server (Using user-provided connection
    // string)
    private static final String URL = "jdbc:sqlserver://10.198.73.40\\SQLEXPRESS;databaseName=YouTubeEthiopia;encrypt=false;trustServerCertificate=true;";
    private static final String USER = "yt_admin";
    private static final String PASSWORD = "Admin123";

    static {
        try {
            // Load the MS SQL Server JDBC driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("MS SQL Server JDBC Driver not found in classpath.");
            e.printStackTrace();
        }
    }

    /**
     * Establishes a connection to the MS SQL Server database.
     */
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Saves new video metadata to the database and sets the generated ID.
     */
    public static boolean saveVideoMetadata(VideoMetadata metadata) {
        String query = "INSERT INTO videos (title, description, file_name, file_path, node_id, node_port) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, metadata.getTitle());
            pstmt.setString(2, metadata.getDescription());
            pstmt.setString(3, metadata.getFileName());
            pstmt.setString(4, metadata.getFilePath());
            pstmt.setString(5, metadata.getNodeId());
            pstmt.setInt(6, metadata.getNodePort());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        metadata.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Database: Metadata saved for " + metadata.getTitle());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Database Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Retrieves all video metadata from the database.
     */
    public static List<VideoMetadata> getAllVideos() {
        List<VideoMetadata> videos = new ArrayList<>();
        String query = "SELECT id, title, description, file_name, file_path, node_id, node_port FROM videos";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                videos.add(mapResultSetToMetadata(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all videos: " + e.getMessage());
            e.printStackTrace();
        }
        return videos;
    }

    /**
     * Searches for videos by title or description.
     */
    public static List<VideoMetadata> searchVideos(String queryText) {
        List<VideoMetadata> videos = new ArrayList<>();
        String query = "SELECT id, title, description, file_name, file_path, node_id, node_port FROM videos WHERE title LIKE ? OR description LIKE ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + queryText + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    videos.add(mapResultSetToMetadata(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching videos: " + e.getMessage());
            e.printStackTrace();
        }
        return videos;
    }

    /**
     * Retrieves a single video's metadata by its ID.
     */
    public static VideoMetadata getVideoById(int id) {
        String query = "SELECT id, title, description, file_name, file_path, node_id, node_port FROM videos WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMetadata(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving video by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Helper method to map a Database ResultSet row to a VideoMetadata object.
     */
    private static VideoMetadata mapResultSetToMetadata(ResultSet rs) throws SQLException {
        VideoMetadata metadata = new VideoMetadata();
        metadata.setId(rs.getInt("id"));
        metadata.setTitle(rs.getString("title"));
        metadata.setDescription(rs.getString("description"));
        metadata.setFileName(rs.getString("file_name"));
        metadata.setFilePath(rs.getString("file_path"));
        metadata.setNodeId(rs.getString("node_id"));
        metadata.setNodePort(rs.getInt("node_port"));
        return metadata;
    }
}
