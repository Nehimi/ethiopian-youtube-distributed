package ui.controllers;

import client.RMIClient;
import database.VideoMetadata;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class UploadModalController {

    @FXML
    private TextField titleField;
    @FXML
    private TextArea descField;
    @FXML
    private Button uploadBtn;
    @FXML
    private Label alertLabel;
    @FXML
    private ProgressBar uploadProgress;
    @FXML
    private Label progressLabel;
    @FXML
    private javafx.scene.layout.VBox progressBox;
    @FXML
    private Text dropLabel;
    @FXML
    private Text dropSublabel;

    private File selectedFile;
    private Runnable onUploadSuccess;
    private Runnable onCancel;

    public void setOnUploadSuccess(Runnable callback) {
        this.onUploadSuccess = callback;
    }

    public void setOnCancel(Runnable callback) {
        this.onCancel = callback;
    }

    @FXML
    public void initialize() {
        // Enable upload button only when file and title are both present
        titleField.textProperty().addListener((obs, o, n) -> updateUploadBtnState());
    }

    @FXML
    private void handleBrowseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Video File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mkv", "*.mov", "*.wmv"));
        Stage owner = (Stage) titleField.getScene().getWindow();
        File file = fileChooser.showOpenDialog(owner);
        if (file != null) {
            selectedFile = file;
            dropLabel.setText("✅  " + file.getName());
            dropSublabel.setText(String.format("%.2f MB", file.length() / 1048576.0));
            if (titleField.getText().isBlank()) {
                // Auto-fill title from filename
                String name = file.getName();
                if (name.contains("."))
                    name = name.substring(0, name.lastIndexOf('.'));
                titleField.setText(name);
            }
            updateUploadBtnState();
        }
    }

    private void updateUploadBtnState() {
        uploadBtn.setDisable(selectedFile == null || titleField.getText().isBlank());
    }

    @FXML
    private void handleUpload() {
        if (selectedFile == null || titleField.getText().isBlank())
            return;

        String title = titleField.getText().trim();
        String desc = descField.getText().trim();

        // Show progress
        progressBox.setVisible(true);
        progressBox.setManaged(true);
        uploadBtn.setDisable(true);
        uploadProgress.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        progressLabel.setText("Uploading...");
        hideAlert();

        new Thread(() -> {
            try {
                VideoMetadata metadata = new VideoMetadata(
                        title, desc, selectedFile.getName(), "", "", 0);
                boolean success = RMIClient.uploadVideo(metadata, selectedFile);

                Platform.runLater(() -> {
                    if (success) {
                        uploadProgress.setProgress(1.0);
                        progressLabel.setText("Done!");
                        if (onUploadSuccess != null)
                            onUploadSuccess.run();
                    } else {
                        showAlert("Upload failed. Make sure the RMI servers are running.", false);
                        progressBox.setVisible(false);
                        progressBox.setManaged(false);
                        uploadBtn.setDisable(false);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showAlert("Error: " + e.getMessage(), false);
                    progressBox.setVisible(false);
                    progressBox.setManaged(false);
                    uploadBtn.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void handleCancel() {
        if (onCancel != null)
            onCancel.run();
    }

    private void showAlert(String msg, boolean success) {
        alertLabel.setText(msg);
        alertLabel.getStyleClass().setAll(success ? "alert-success" : "alert-error");
        alertLabel.setVisible(true);
        alertLabel.setManaged(true);
    }

    private void hideAlert() {
        alertLabel.setVisible(false);
        alertLabel.setManaged(false);
    }
}
