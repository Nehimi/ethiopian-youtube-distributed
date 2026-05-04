package client;

import database.VideoMetadata;
import server.VideoInterface;
import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * RMIClient coordinates communication with multiple server nodes.
 * It uses the LoadBalancer to decide where to upload and download videos.
 */
public class RMIClient {
    static {
        // Set this to YOUR computer's IP so the server can talk back to you
        System.setProperty("java.rmi.server.hostname", "10.198.70.78");
    }

    /**
     * Connects to a specific node and retrieves the VideoService.
     */
    public static VideoInterface getService(String host, int port) {
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            return (VideoInterface) registry.lookup("VideoService");
        } catch (Exception e) {
            System.err.println("Could not connect to VideoService at " + host + ":" + port);
            return null;
        }
    }

    /**
     * Uploads a video from a file path using chunking.
     * This is the "Real YouTube" way of handling large files.
     */
    public static boolean uploadVideo(VideoMetadata metadata, File videoFile) {
        // 1. Decide which node should store this video
        String targetNodeId = LoadBalancer.decideNode(metadata);
        int port = LoadBalancer.getPort(targetNodeId);

        // 2. Connect to the node
        VideoInterface service = getService(LoadBalancer.getHost(), port);
        if (service == null)
            return false;

        try {
            FileInputStream fis = new FileInputStream(videoFile);
            byte[] buffer = new byte[64 * 1024]; // 64KB chunks (more reliable for RMI)
            int bytesRead;
            boolean isFirstChunk = true;

            System.out.println("Starting chunked upload to " + targetNodeId + "...");

            while ((bytesRead = fis.read(buffer)) != -1) {
                // If the buffer is not full (last chunk), resize it
                byte[] actualChunk = bytesRead == buffer.length ? buffer : java.util.Arrays.copyOf(buffer, bytesRead);

                boolean success = service.uploadChunk(metadata.getFileName(), actualChunk, isFirstChunk);
                if (!success) {
                    System.err.println("❌ Failed to upload chunk to node " + targetNodeId);
                    fis.close();
                    return false;
                }
                isFirstChunk = false;
            }
            fis.close();

            // 3. Finalize by saving metadata
            metadata.setNodeId(targetNodeId);
            metadata.setNodePort(port);
            return service.saveMetadata(metadata);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Downloads a video from the specific node where it is stored.
     */
    public static byte[] downloadVideo(VideoMetadata metadata) {
        if (metadata == null)
            return null;

        // Connect to the specific node where this video is saved
        VideoInterface service = getService(LoadBalancer.getHost(), metadata.getNodePort());
        if (service != null) {
            try {
                return service.downloadVideo(metadata.getId());
            } catch (Exception e) {
                System.err.println("Download failed: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Searches for videos across the entire system.
     * Since all nodes share the same database, we can connect to any active node.
     */
    public static List<VideoMetadata> searchVideos(String query) {
        // Default to Node 1 for general queries, or Node 2 if Node 1 is down
        VideoInterface service = getService(LoadBalancer.getHost(), 1099);
        if (service == null)
            service = getService(LoadBalancer.getHost(), 1100);

        if (service != null) {
            try {
                return service.searchVideos(query);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
