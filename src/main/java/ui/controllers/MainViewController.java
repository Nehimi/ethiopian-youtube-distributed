package ui.controllers;

import client.RMIClient;
import database.VideoMetadata;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;

import java.util.List;

public class MainViewController {

    @FXML
    private FlowPane videoGrid;
    @FXML
    private VBox emptyState;
    @FXML
    private TextField searchField;
    @FXML
    private Label statusLabel;
    @FXML
    private Label videoCountLabel;
    @FXML
    private Label node1StatusDot;
    @FXML
    private Label node2StatusDot;
    @FXML
    private Label alertLabel;
    @FXML
    private HBox alertBox;
    @FXML
    private Label pageTitleLabel;
    @FXML
    private ScrollPane scrollPane;

    @FXML
    public void initialize() {
        // Async load so the UI opens instantly
        statusLabel.setText("Connecting to nodes...");
        checkNodeStatus();
        loadVideos("");
    }

    /** Check if Node1 and Node2 are reachable */
    private void checkNodeStatus() {
        new Thread(() -> {
            boolean n1 = RMIClient.getService("10.198.73.40", 8081) != null;
            boolean n2 = RMIClient.getService("10.198.73.40", 8080) != null;
            Platform.runLater(() -> {
                if (n1) {
                    node1StatusDot.setStyle("-fx-text-fill: #2ba640;");
                } else {
                    node1StatusDot.setStyle("-fx-text-fill: #ff4e45;");
                }
                if (n2) {
                    node2StatusDot.setStyle("-fx-text-fill: #2ba640;");
                } else {
                    node2StatusDot.setStyle("-fx-text-fill: #ff4e45;");
                }
            });
        }).start();
    }

    /** Load (or search) videos from the backend */
    private void loadVideos(String query) {
        statusLabel.setText("Loading videos...");
        new Thread(() -> {
            List<VideoMetadata> videos = RMIClient.searchVideos(query);
            Platform.runLater(() -> {
                videoGrid.getChildren().clear();
                if (videos == null || videos.isEmpty()) {
                    emptyState.setVisible(true);
                    emptyState.setManaged(true);
                    videoCountLabel.setText("0 videos");
                    statusLabel.setText("No videos found. Start the RMI servers first.");
                } else {
                    emptyState.setVisible(false);
                    emptyState.setManaged(false);
                    for (VideoMetadata video : videos) {
                        videoGrid.getChildren().add(VideoCardController.createCard(video));
                    }
                    videoCountLabel.setText(videos.size() + " video" + (videos.size() == 1 ? "" : "s"));
                    statusLabel.setText("Loaded " + videos.size() + " video(s) successfully.");
                }
            });
        }).start();
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        pageTitleLabel.setText(query.isEmpty() ? "Home" : "Results: \"" + query + "\"");
        loadVideos(query);
    }

    @FXML
    private void handleHome() {
        searchField.clear();
        pageTitleLabel.setText("Home");
        loadVideos("");
    }

    @FXML
    private void handleUpload() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/upload-modal.fxml"));
            Parent modalRoot = loader.load();
            UploadModalController controller = loader.getController();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.initOwner(videoGrid.getScene().getWindow());
            dialog.setTitle("Upload Video");

            Scene scene = new Scene(modalRoot, 560, 620);
            scene.getStylesheets().add(getClass().getResource("/styles/glassmorphism.css").toExternalForm());
            dialog.setScene(scene);

            controller.setOnUploadSuccess(() -> {
                dialog.close();
                showAlert("✅  Video uploaded successfully!", true);
                loadVideos(""); // Refresh
            });
            controller.setOnCancel(dialog::close);

            dialog.showAndWait();
        } catch (Exception e) {
            showAlert("❌  Could not open upload window: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    private void showAlert(String message, boolean success) {
        alertLabel.setText(message);
        alertLabel.getStyleClass().setAll(success ? "alert-success" : "alert-error");
        alertBox.setVisible(true);
        alertBox.setManaged(true);
        // Auto-hide after 4 seconds
        new Thread(() -> {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ignored) {
            }
            Platform.runLater(() -> {
                alertBox.setVisible(false);
                alertBox.setManaged(false);
            });
        }).start();
    }
}
