package server;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.io.*;
import java.util.*;

import database.DatabaseManager;
import database.VideoMetadata;

public class VideoImpl extends UnicastRemoteObject implements VideoInterface {

    private static final long serialVersionUID = 1L;
    private String storagePath;

    public VideoImpl(String storagePath, int port) throws RemoteException {
        super(port);
        this.storagePath = storagePath;

        File dir = new File(storagePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public boolean uploadVideo(VideoMetadata metadata, byte[] videoData) throws RemoteException {
        try {
            // Ensure file path is set correctly for this node
            String filePath = storagePath + File.separator + metadata.getFileName();

            // Save the physical file
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(videoData);
            fos.close();

            // Update metadata to reflect actual storage path on this node
            metadata.setFilePath(filePath);

            // Store metadata in the MySQL database
            boolean saved = DatabaseManager.saveVideoMetadata(metadata);

            if (saved) {
                System.out.println("Uploaded: " + metadata.getFileName() + " to " + storagePath + " (Saved to DB)");
            } else {
                System.err.println("File saved to disk but failed to save metadata to database.");
            }

            return saved;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean uploadChunk(String fileName, byte[] chunk, boolean isFirstChunk) throws RemoteException {
        try {
            File dir = new File(storagePath);
            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("Created storage directory: " + storagePath);
            }

            String filePath = storagePath + File.separator + fileName;
            // Open in append mode (true) unless it's the first chunk (false)
            FileOutputStream fos = new FileOutputStream(filePath, !isFirstChunk);
            fos.write(chunk);
            fos.close();
            return true;
        } catch (IOException e) {
            System.err.println("Error saving chunk for " + fileName + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean saveMetadata(VideoMetadata metadata) throws RemoteException {
        // Ensure file path is correct for this node
        String filePath = storagePath + File.separator + metadata.getFileName();
        metadata.setFilePath(filePath);
        return DatabaseManager.saveVideoMetadata(metadata);
    }

    @Override
    public byte[] downloadVideo(int videoId) throws RemoteException {
        try {
            VideoMetadata metadata = DatabaseManager.getVideoById(videoId);
            if (metadata == null)
                return null;

            File file = new File(metadata.getFilePath());
            if (!file.exists())
                return null;

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
        return DatabaseManager.getAllVideos();
    }

    @Override
    public List<VideoMetadata> searchVideos(String query) throws RemoteException {
        return DatabaseManager.searchVideos(query);
    }

    @Override
    public VideoMetadata getVideoDetails(int videoId) throws RemoteException {
        return DatabaseManager.getVideoById(videoId);
    }
}
