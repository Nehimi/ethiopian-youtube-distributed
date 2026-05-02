package client;

import database.VideoMetadata;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * BackendTest verifies the entire distributed system:
 * 1. Upload to Node 1 (Google video)
 * 2. Upload to Node 2 (Other video)
 * 3. Search and retrieve from database
 */
public class BackendTest {

    public static void main(String[] args) {
        System.out.println("Starting Full Backend Integration Test...\n");

        try {
            // 1. Create dummy video files for testing
            File googleFile = createDummyFile("google_test.mp4", 2); // 2MB
            File musicFile = createDummyFile("music_test.mp4", 1); // 1MB

            // 2. Test Upload to Node 1 (LoadBalancer should pick Node 1 for "Google")
            System.out.println("[STEP 1] Testing Upload to Node 1...");
            VideoMetadata meta1 = new VideoMetadata("Google Ad Video", "A video about Google", "google_test.mp4", "",
                    "", 0);
            boolean success1 = RMIClient.uploadVideo(meta1, googleFile);
            System.out.println("Node 1 Upload Result: " + (success1 ? "SUCCESS ✅" : "FAILED ❌"));

            // 3. Test Upload to Node 2 (LoadBalancer should pick Node 2 for "Music")
            System.out.println("\n[STEP 2] Testing Upload to Node 2...");
            VideoMetadata meta2 = new VideoMetadata("Amazing Music Video", "A cool music video", "music_test.mp4", "",
                    "", 0);
            boolean success2 = RMIClient.uploadVideo(meta2, musicFile);
            System.out.println("Node 2 Upload Result: " + (success2 ? "SUCCESS ✅" : "FAILED ❌"));

            // 4. Test Search
            System.out.println("\n[STEP 3] Testing Global Search...");
            List<VideoMetadata> results = RMIClient.searchVideos("video");
            System.out.println("Search Results Found: " + (results != null ? results.size() : 0));

            if (results != null) {
                for (VideoMetadata v : results) {
                    System.out.println(" - Found Video: " + v.getTitle() + " [Stored on: " + v.getNodeId() + "]");
                }
            }

            System.out.println("\nBackend Integration Test Completed!");

        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static File createDummyFile(String name, int sizeMB) throws Exception {
        File file = new File(name);
        byte[] data = new byte[1024 * 1024]; // 1MB
        try (FileOutputStream fos = new FileOutputStream(file)) {
            for (int i = 0; i < sizeMB; i++) {
                fos.write(data);
            }
        }
        return file;
    }
}
