package server;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.io.*;
import java.util.*;

import database.VideoMetadata;

public class VideoImpl extends UnicastRemoteObject implements VideoInterface {

    private static final long serialVersionUID = 1L;

    // Use correct key: metadata.getId()
    private Map<Integer, VideoMetadata> metadataMap = new HashMap<>();

    private String storagePath;

    public VideoImpl(String storagePath) throws RemoteException {
        this.storagePath = storagePath;

        File dir = new File(storagePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public boolean uploadVideo(VideoMetadata metadata, byte[] videoData) throws RemoteException {
        try {
            // Ensure file path is set correctly
            String filePath = storagePath + File.separator + metadata.getFileName();

            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(videoData);
            fos.close();

            // Update metadata to reflect actual storage
            metadata.setFilePath(filePath);

            // Store metadata using correct ID
            metadataMap.put(metadata.getId(), metadata);

            System.out.println("Uploaded: " + metadata.getFileName() + " to " + storagePath);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public byte[] downloadVideo(int videoId) throws RemoteException {
        try {
            VideoMetadata metadata = metadataMap.get(videoId);

            if (metadata == null) {
                System.out.println("Video not found: ID " + videoId);
                return null;
            }

            File file = new File(metadata.getFilePath());

            byte[] data = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(data);
            fis.close();

            return data;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<VideoMetadata> getAllVideos() throws RemoteException {
        return new ArrayList<>(metadataMap.values());
    }

    @Override
    public List<VideoMetadata> searchVideos(String query) throws RemoteException {
        List<VideoMetadata> result = new ArrayList<>();

        for (VideoMetadata video : metadataMap.values()) {
            if (video.getTitle() != null &&
                video.getTitle().toLowerCase().contains(query.toLowerCase())) {
                result.add(video);
            }
        }

        return result;
    }

    @Override
    public VideoMetadata getVideoDetails(int videoId) throws RemoteException {
        return metadataMap.get(videoId);
    }
}
