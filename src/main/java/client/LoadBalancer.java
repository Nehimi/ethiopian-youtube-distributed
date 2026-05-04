package client;

import database.VideoMetadata;

/**
 * LoadBalancer decides which server node should handle the video based on its
 * content.
 * 
 * Rules:
 * - Videos with "google" in the title go to Node 1 (Port 1099).
 * - All other videos go to Node 2 (Port 1100).
 */
public class LoadBalancer {

    private static final String HOST = "10.198.73.40";
    private static final int NODE1_PORT = 1099;
    private static final int NODE2_PORT = 1100;

    /**
     * Determines which node is responsible for the given video metadata.
     * 
     * @param metadata The video metadata containing the title.
     * @return The node ID ("Node1" or "Node2").
     */
    public static String decideNode(VideoMetadata metadata) {
        String title = metadata.getTitle().toLowerCase();

        // Load balancing rule: Google videos to Node1, others to Node2
        if (title.contains("google")) {
            return "Node1";
        } else {
            return "Node2";
        }
    }

    /**
     * Returns the port number for a given node ID.
     */
    public static int getPort(String nodeId) {
        return nodeId.equals("Node1") ? NODE1_PORT : NODE2_PORT;
    }

    /**
     * Returns the hostname (currently always localhost).
     */
    public static String getHost() {
        return HOST;
    }
}
