package ui.controllers;

import client.RMIClient;
import database.VideoMetadata;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class VideoPlayerController {

    @FXML private MediaView mediaView;
    @FXML private Text videoTitleText;
    @FXML private Text videoFullTitle;
    @FXML private Text videoDesc;
    @FXML private Button playPauseBtn;
    @FXML private Label timeLabel;
    @FXML private Slider seekSlider;
    @FXML private Slider volumeSlider;
    @FXML private VBox loadingOverlay;
    @FXML private VBox recommendationList;

    private MediaPlayer mediaPlayer;
    private VideoMetadata currentVideo;

    public void setVideo(VideoMetadata video) {
        this.currentVideo = video;
        videoTitleText.setText(video.getTitle());
        videoFullTitle.setText(video.getTitle());
        videoDesc.setText(video.getDescription());
        
        loadVideoFile(video);
        loadRecommendations();
    }

    private void loadVideoFile(VideoMetadata video) {
        loadingOverlay.setVisible(true);
        
        new Thread(() -> {
            try {
                System.out.println("Downloading video for playback...");
                byte[] data = RMIClient.downloadVideo(video);
                
                if (data == null) {
                    System.err.println("Failed to download video data.");
                    return;
                }

                // Create a temporary file to play
                File tempFile = File.createTempFile("playing_", "_" + video.getFileName());
                tempFile.deleteOnExit();
                
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(data);
                }

                Platform.runLater(() -> {
                    Media media = new Media(tempFile.toURI().toString());
                    mediaPlayer = new MediaPlayer(media);
                    mediaView.setMediaPlayer(mediaPlayer);
                    
                    setupControls();
                    mediaPlayer.play();
                    loadingOverlay.setVisible(false);
                    playPauseBtn.setText("Pause");
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupControls() {
        // Seek slider
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!seekSlider.isValueChanging()) {
                seekSlider.setValue(newTime.toSeconds());
            }
            updateTimeLabel(newTime, mediaPlayer.getTotalDuration());
        });

        mediaPlayer.setOnReady(() -> {
            seekSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            updateTimeLabel(Duration.ZERO, mediaPlayer.getTotalDuration());
        });

        seekSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (seekSlider.isValueChanging()) {
                mediaPlayer.seek(Duration.seconds(newVal.doubleValue()));
            }
        });

        // Volume
        mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty().divide(100.0));
    }

    private void updateTimeLabel(Duration current, Duration total) {
        int curSec = (int) current.toSeconds();
        int totSec = (int) (total != null ? total.toSeconds() : 0);
        timeLabel.setText(formatTime(curSec) + " / " + formatTime(totSec));
    }

    private String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", mins, secs);
    }

    @FXML
    private void togglePlay() {
        if (mediaPlayer == null) return;
        
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            playPauseBtn.setText("Play");
        } else {
            mediaPlayer.play();
            playPauseBtn.setText("Pause");
        }
    }

    @FXML
    private void handleBack() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/main-view.fxml"));
            Stage stage = (Stage) mediaView.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void toggleFullscreen() {
        Stage stage = (Stage) mediaView.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    private void loadRecommendations() {
        recommendationList.getChildren().clear();
        List<VideoMetadata> all = RMIClient.searchVideos("");
        if (all != null) {
            for (VideoMetadata v : all) {
                if (v.getId() == currentVideo.getId()) continue;
                
                // Create horizontal item
                HBox item = new HBox(12);
                item.getStyleClass().add("sidebar-item");
                
                // Mini Thumbnail
                StackPane thumb = new StackPane();
                thumb.getStyleClass().add("sidebar-thumb");
                String[] gradients = {"#FF0000", "#1e3a8a", "#065f46", "#7c2d12", "#581c87"};
                thumb.setStyle("-fx-background-color: " + gradients[Math.abs(v.getTitle().hashCode()) % gradients.length] + "; -fx-background-radius: 8;");
                
                VBox info = new VBox(2);
                Label title = new Label(v.getTitle());
                title.getStyleClass().add("sidebar-title");
                title.setWrapText(true);
                
                Label meta = new Label(v.getNodeId() + "\n" + (10 + (int)(Math.random()*900)) + "K views");
                meta.getStyleClass().add("sidebar-meta");
                
                info.getChildren().addAll(title, meta);
                item.getChildren().addAll(thumb, info);
                
                item.setOnMouseClicked(e -> {
                    if (mediaPlayer != null) mediaPlayer.stop();
                    setVideo(v);
                });
                
                recommendationList.getChildren().add(item);
            }
        }
    }
}
