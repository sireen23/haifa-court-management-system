package control;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class MainController {
	@FXML
	private AnchorPane videoPane;

	@FXML
	private MediaView mediaView;

	@FXML
	private ImageView loginIcon, publicVerdictImage, testimonyDocumantationIcon;

	@FXML
	private Label loginLabel, publicVerdictLabel, testimonyLabel, mainTab, loginTab, verdictTab, testimonyTab;

	private MediaPlayer mediaPlayer;

	public void initialize() {
		System.out.println("Initializing MainController...");
		// #e9b57b

		try {
			// ✅ Check if the media file exists
			URL mediaURL = getClass().getResource("/resources/background.mp4");

			if (mediaURL == null) {
				throw new IOException("❌ Background video file not found! Skipping video.");
			}

			String videoPath = mediaURL.toExternalForm();
			Media media = new Media(videoPath);
			mediaPlayer = new MediaPlayer(media);
			mediaView.setMediaPlayer(mediaPlayer);

			// Resize video dynamically
			mediaView.fitWidthProperty().bind(videoPane.widthProperty());
			mediaView.fitHeightProperty().bind(videoPane.heightProperty());

			// Play video in loop
			mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
			mediaPlayer.play();

			// Stop video on app close
			mediaPlayer.setOnStopped(() -> System.out.println("✅ Media Player Stopped."));

		} catch (IOException e) {
			System.out.println("❌ Media file error: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("❌ Error initializing MediaPlayer: " + e.getMessage());
			e.printStackTrace();
		}

		// ✅ Add event listeners to open login page
		if (loginIcon != null) {
			loginIcon.setOnMouseClicked(this::openLoginPage);
			System.out.println("✅ loginIcon Click Listener Added!");
		} else {
			System.out.println("❌ ERROR: loginIcon is NULL! Check FXML.");
		}

		if (loginLabel != null) {
			loginLabel.setOnMouseClicked(this::openLoginPage);
			System.out.println("✅ loginLabel Click Listener Added!");
		} else {
			System.out.println("❌ ERROR: loginLabel is NULL! Check FXML.");
		}

		publicVerdictImage.setOnMouseClicked(this::openVerdictPage);
		publicVerdictLabel.setOnMouseClicked(this::openVerdictPage);
		testimonyLabel.setOnMouseClicked(this::openTestimonyDocumentation);
		testimonyDocumantationIcon.setOnMouseClicked(this::openTestimonyDocumentation);
		loginTab.setOnMouseClicked(this::openLoginPage);
		mainTab.setOnMouseClicked(this::openMainPage);
		verdictTab.setOnMouseClicked(this::openVerdictPage);
		testimonyTab.setOnMouseClicked(this::openTestimonyDocumentation);
	}

	private void openMainPage(MouseEvent event) {
		try {
			// Create FXMLLoader instance
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));

			// Load the FXML without setting the controller explicitly
			Parent testimonyPage = loader.load();

			// Close current stage safely
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();
			mainTab.setStyle("-fx-text-fill: #e9b57b;");

			// Open the testimony page in a new stage
			Stage newStage = new Stage();
			newStage.setScene(new Scene(testimonyPage));
			newStage.setTitle("📜 Adding Testimony - Non Registered Users"); // ✅ Fixed Title
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openTestimonyDocumentation(MouseEvent event) {
		try {
			// Create FXMLLoader instance
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TestimonyDocumentation.fxml"));

			// Load the FXML without setting the controller explicitly
			Parent testimonyPage = loader.load();

			// Close current stage safely
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();

			// Open the testimony page in a new stage
			Stage newStage = new Stage();
			newStage.setScene(new Scene(testimonyPage));
			newStage.setTitle("📜 Adding Testimony - Non Registered Users"); // ✅ Fixed Title
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openVerdictPage(MouseEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PublicVerdicts.fxml"));
			Parent verdictPage = loader.load();

			// Close current stage safely
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();

			// Open the Verdicts page in a new stage
			Stage newStage = new Stage();
			newStage.setScene(new Scene(verdictPage));
			newStage.setTitle("📜 Public Verdicts - HRS Court System"); // ✅ Fixed Title
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openLoginPage(MouseEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
			AnchorPane loginPage = loader.load();

			// Close current stage
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();

			loginTab.setStyle("-fx-text-fill: #e9b57b;");
			// Open new Login Stage
			Stage newStage = new Stage();
			newStage.setScene(new Scene(loginPage));
			newStage.setTitle("Login - HRS Court Management");
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Stop media when closing the app
	public void stopMedia() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.dispose(); // ✅ Release resources
			System.out.println("✅ Media Player Released.");
		}
	}

}
