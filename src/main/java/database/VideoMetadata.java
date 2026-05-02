package database;

import java.io.Serializable;

public class VideoMetadata implements Serializable {
    private static final long serialVersionUID = 1L;
    // ይህ የመረጃው መለያ (Version) ነው።

    private int id;
    private String title;
    private String description;
    private String fileName;
    private String filePath;
    private String nodeId;
    private int nodePort;

    public VideoMetadata() {
    }

    public VideoMetadata(String title, String description, String fileName, String filePath, String nodeId,
            int nodePort) {
        this.title = title;
        this.description = description;
        this.fileName = fileName;
        this.filePath = filePath;
        this.nodeId = nodeId;
        this.nodePort = nodePort;
    }

    // Getters እና Setters ማለት በጃቫ ውስጥ የክላስን መረጃዎች
    // (Fields) በስርዓት ለማንበብ እና ለመቀየር የምንጠቀምባቸው መንገዶች ናቸው
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public int getNodePort() {
        return nodePort;
    }

    public void setNodePort(int nodePort) {
        this.nodePort = nodePort;
    }
}
