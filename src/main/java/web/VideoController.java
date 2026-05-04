package web;

import database.DatabaseManager;
import database.VideoMetadata;
import server.VideoInterface;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

@RestController
@RequestMapping("/api")
public class VideoController {

    @GetMapping("/videos")
    public List<VideoMetadata> getVideos() {
        return DatabaseManager.getAllVideos();
    }

    @GetMapping("/stream/{id}")
    public ResponseEntity<Resource> streamVideo(@PathVariable int id) {
        try {
            // 1. Get metadata to find which node has the video
            VideoMetadata metadata = DatabaseManager.getVideoById(id);
            if (metadata == null)
                return ResponseEntity.notFound().build();

            // 2. Connect to the RMI Node
            String host = "10.198.73.40"; // Your server IP
            int port = metadata.getNodePort();

            Registry registry = LocateRegistry.getRegistry(host, port);
            VideoInterface service = (VideoInterface) registry.lookup("VideoService");

            // 3. Download the video bytes via RMI
            byte[] videoData = service.downloadVideo(id);

            // 4. Return as a streamable resource
            ByteArrayResource resource = new ByteArrayResource(videoData);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + metadata.getFileName() + "\"")
                    .contentType(MediaType.parseMediaType("video/mp4")) // Assuming MP4
                    .contentLength(videoData.length)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    public List<VideoMetadata> searchVideos(@RequestParam String q) {
        return DatabaseManager.searchVideos(q);
    }
}
