package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import database.VideoMetadata;

public interface VideoInterface extends Remote {

    /**
     * Uploads a video in a single call (for small files).
     */
    boolean uploadVideo(VideoMetadata metadata, byte[] videoData) throws RemoteException;

    /**
     * Uploads a part (chunk) of a video file.
     * 
     * @param fileName     The name of the file being uploaded.
     * @param chunk        The byte array of the current part.
     * @param isFirstChunk If true, a new file is created. If false, data is
     *                     appended.
     */
    boolean uploadChunk(String fileName, byte[] chunk, boolean isFirstChunk) throws RemoteException;

    /**
     * Finalizes the chunked upload by saving the metadata to the database.
     */
    boolean saveMetadata(VideoMetadata metadata) throws RemoteException;

    byte[] downloadVideo(int videoId) throws RemoteException;

    List<VideoMetadata> getAllVideos() throws RemoteException;

    List<VideoMetadata> searchVideos(String query) throws RemoteException;

    VideoMetadata getVideoDetails(int videoId) throws RemoteException;
}
