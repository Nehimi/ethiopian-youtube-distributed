package ui.controllers;

import database.VideoMetadata;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Creates a styled video card VBox for a given VideoMetadata.
 * Used programmatically inside MainViewController.
 */
public class VideoCardController {

    // Vibrant YouTube-style placeholders
    private static final String[] GRADIENTS = {
        "linear-gradient(to bottom right, #FF0000, #b91c1c)", // YouTube Red
        "linear-gradient(to bottom right, #1e3a8a, #3b82f6)", // Blue
        "linear-gradient(to bottom right, #065f46, #10b981)", // Green
        "linear-gradient(to bottom right, #7c2d12, #f97316)", // Orange
        "linear-gradient(to bottom right, #581c87, #a855f7)", // Purple
        "linear-gradient(to bottom right, #1e1b4b, #312e81)"  // Navy
    };

    private static final String[] EMOJIS = {"📺", "🎬", "📽️", "🎥", "📹", "🎞️"};

    private static int cardCounter = 0;

    public static VBox createCard(VideoMetadata video) {
        int idx = cardCounter++ % GRADIENTS.length;

        VBox card = new VBox(12);
        card.getStyleClass().add("video-card");
        card.setPrefWidth(320);

        // ── THUMBNAIL (16:9 Ratio) ──────────────────────────────────
        StackPane thumb = new StackPane();
        thumb.getStyleClass().add("thumbnail-container");
        thumb.setStyle("-fx-background-color: " + GRADIENTS[idx] + ";");
        thumb.setPrefHeight(180);
        thumb.setMinHeight(180);
        
        Text playIcon = new Text("▶");
        playIcon.setStyle("-fx-fill: white; -fx-font-size: 40; -fx-opacity: 0;");
        thumb.setOnMouseEntered(e -> playIcon.setOpacity(0.8));
        thumb.setOnMouseExited(e -> playIcon.setOpacity(0));

        // Random duration
        int mins = 3 + (int)(Math.random() * 12);
        int secs = (int)(Math.random() * 60);
        Label duration = new Label(String.format("%d:%02d", mins, secs));
        duration.getStyleClass().add("duration-label");
        duration.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-text-fill: white; -fx-padding: 2 4; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 4;");
        StackPane.setAlignment(duration, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(duration, new Insets(8));
        
        thumb.getChildren().addAll(playIcon, duration);

        // Info Section (Avatar + Text)
        HBox infoBox = new HBox(12);
        infoBox.setPadding(new Insets(12, 0, 0, 0));

        // Avatar with Initial
        StackPane avatar = new StackPane();
        Circle circle = new Circle(18);
        circle.setFill(Paint.valueOf(GRADIENTS[Math.abs(video.getNodeId().hashCode()) % GRADIENTS.length]));
        Text initial = new Text(video.getNodeId().substring(0, 1).toUpperCase());
        initial.setStyle("-fx-fill: white; -fx-font-weight: bold; -fx-font-size: 14;");
        avatar.getChildren().addAll(circle, initial);

        VBox textInfo = new VBox(4);
        Label title = new Label(video.getTitle() != null ? video.getTitle() : "Untitled Video");
        title.getStyleClass().add("video-title");
        title.setWrapText(true);
        title.setMaxHeight(45);

        // Random views and time
        int views = 10 + (int)(Math.random() * 900);
        int days = 1 + (int)(Math.random() * 30);
        Label meta = new Label(video.getNodeId() + " • " + views + "K views • " + days + " days ago");
        meta.getStyleClass().add("video-meta");

        textInfo.getChildren().addAll(title, meta);
        infoBox.getChildren().addAll(avatar, textInfo);

        card.getChildren().addAll(thumb, infoBox);

        // ── PLAY LOGIC ───────────────────────────────────────────
        card.setOnMouseClicked(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(VideoCardController.class.getResource("/fxml/video-player.fxml"));
                Parent root = loader.load();
                
                VideoPlayerController controller = loader.getController();
                controller.setVideo(video);
                
                Stage stage = (Stage) card.getScene().getWindow();
                stage.getScene().setRoot(root);
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return card;
    }
}
