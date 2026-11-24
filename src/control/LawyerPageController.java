package control;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import model.Lawyer;
import model.SessionManager;
import model.User;

public class LawyerPageController {
	private Lawyer loggedInLawyer;

	@FXML
	private ImageView casesButton, appealButton, meetingsCalendar, activateQueries, documentsButton, logout,
			profileImageView;

	@FXML
	private Label casesLabel, appealLabel, meetingsLabel, queriesLabel, manaDocumentsLabel;

	private MediaPlayer mediaPlayer;
	@FXML
	private MediaView mediaView;
	private boolean isInitialized = false; // ✅ Prevent premature access to FXML components

	@FXML
	private Label lawyerNameLabel; // Label to display lawyer's name

	public void setLawyer(Lawyer lawyer) {
		if (lawyer == null) {
			System.err.println("❌ ERROR: Null Lawyer in setLawyer()");
			return;
		}

		this.loggedInLawyer = lawyer;
		System.out.println("✅ Lawyer Set: " + loggedInLawyer.getFullName());

		// ✅ Set the Lawyer's Name in the Label
		lawyerNameLabel.setText(loggedInLawyer.getFullName());

		// 🔥 Load and Set Profile Image
		if (loggedInLawyer.getProfileImagePath() != null) {
			System.out.println("📸 Lawyer Image Path: " + loggedInLawyer.getProfileImagePath());

			File imageFile = new File(loggedInLawyer.getProfileImagePath());
			if (imageFile.exists()) {
				profileImageView.setImage(new Image(imageFile.toURI().toString())); // ✅ Load Image
				System.out.println("✅ Profile Image Loaded!");
			} else {
				System.err
						.println("❌ ERROR: Profile Image File NOT Found! Expected at: " + imageFile.getAbsolutePath());
				profileImageView
						.setImage(new Image(getClass().getResourceAsStream("/uploaded_images/default_image.png")));
			}
		} else {
			System.err.println("❌ ERROR: Lawyer has NO Profile Image Path!");
			profileImageView.setImage(new Image(getClass().getResourceAsStream("/uploaded_images/default_image.png"))); //
																														// Set
																														// Default
																														// Image
		}
	}

	@FXML
	public void initialize() {
		printLawyers(); // ✅ Keeps your existing function

		try {
			// ✅ Retrieve Logged-in User from SessionManager (Keeps Previous Data)
			User loggedInUser = SessionManager.getInstance().getLoggedInUser();

			if (loggedInUser != null && loggedInUser.getRole().equals("Lawyer")) {
				loggedInLawyer = (Lawyer) Court.getInstance().getAllLawyers().get(loggedInUser.getPersonId());

				if (loggedInLawyer != null) {
					System.out.println("✅ Lawyer Retrieved: " + loggedInLawyer.getFullName());
					lawyerNameLabel.setText(loggedInLawyer.getFullName());
				} else {
					System.err.println("❌ ERROR: Lawyer not found in Court HashMap.");
				}
			} else {
				System.err.println("❌ ERROR: No logged-in lawyer found in SessionManager.");
			}

			// ✅ Keeps Your Existing Video Background
			String videoPath = getClass().getResource("/resources/lawyer.mp4").toExternalForm();
			Media media = new Media(videoPath);
			mediaPlayer = new MediaPlayer(media);
			mediaView.setMediaPlayer(mediaPlayer);
			mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
			mediaPlayer.setMute(true);
			mediaPlayer.play();

			// ✅ Keeps Your Existing Click Listeners
			casesButton.setOnMouseClicked(this::openLawyerCases);
			casesLabel.setOnMouseClicked(this::openLawyerCases);

			appealButton.setOnMouseClicked(this::openAppeal);
			appealLabel.setOnMouseClicked(this::openAppeal);

			activateQueries.setOnMouseClicked(this::openQueries);
			queriesLabel.setOnMouseClicked(this::openQueries);

			manaDocumentsLabel.setOnMouseClicked(this::openDocumentsPage);
			documentsButton.setOnMouseClicked(this::openDocumentsPage);

			meetingsCalendar.setOnMouseClicked(this::openCalendarPage);
			meetingsLabel.setOnMouseClicked(this::openCalendarPage);

			logout.setOnMouseClicked(this::logout);

		} catch (Exception e) {
			e.printStackTrace();
		}

		isInitialized = true; // ✅ Ensures FXML Components Are Fully Loaded
	}

	public void logout(MouseEvent event) {
		try {
			// Show logout success message
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Logout");
			alert.setHeaderText(null);
			alert.setContentText("Logout done successfully!");
			alert.showAndWait(); // Wait for user to acknowledge before proceeding

			// Close current window
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();

			// Load main.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
			Parent root = loader.load();

			// Set up new stage
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("Main Page");
			stage.show();

		} catch (IOException e) {
			e.printStackTrace(); // Handle error properly
		}
	}

	private void openCalendarPage(MouseEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Calendar.fxml"));
			AnchorPane page = loader.load();

			CalendarController controller = loader.getController();
			controller.setLawyer(loggedInLawyer); // ✅ Pass the lawyer

			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();

			Stage newStage = new Stage();
			newStage.setScene(new Scene(page));
			newStage.setTitle("Lawyer Calendar");
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("❌ ERROR: Failed to load Calendar.fxml");
		}
	}

	private void openDocumentsPage(MouseEvent event) {
		openForm(event, "/view/LawyerManageDocuments.fxml", "Lawyer Documents");
	}

	private void openQueries(MouseEvent event) {
		openForm(event, "/view/LawyerQueries.fxml", "Lawyer Queries");
	}

	private void openAppeal(MouseEvent event) {
		openForm(event, "/view/LawyerAppeal.fxml", "Lawyer Manage Appeal");
	}

	private void openLawyerCases(MouseEvent event) {
		openForm(event, "/view/LawyerManageCases.fxml", "Lawyer Manage Cases");
	}

	// ✅ Generic method to open lawyer-related forms
	// ✅ Generic method to open lawyer-related forms
	private void openForm(MouseEvent event, String fxmlPath, String title) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
			AnchorPane page = loader.load();

			// ✅ Get the correct controller dynamically
			Object controller = loader.getController();

			if (controller instanceof LawyerManageCases) {
				((LawyerManageCases) controller).setLawyer(loggedInLawyer);
			} else if (controller instanceof LawyerManageDocuments) { // ✅ FIXED: Pass Lawyer to LawyerManageDocuments
				((LawyerManageDocuments) controller).setLawyer(loggedInLawyer);
			} else if (controller instanceof LawyerQueriesController) {
				System.out.println("✅ Opening Lawyer Queries Page");
			} else {
				System.err.println("⚠️ WARNING: Unknown controller type for " + fxmlPath);
			}

			// ✅ Close current Lawyer.fxml window
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();

			// ✅ Open new form
			Stage newStage = new Stage();
			newStage.setScene(new Scene(page));
			newStage.setTitle(title);
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("❌ ERROR: Failed to load " + fxmlPath);
		}
	}

	public void printLawyers() {
		HashSet<Lawyer> lawyerSet = new HashSet<>();
		lawyerSet.addAll(Court.getInstance().getAllLawyers().values());

		for (Lawyer lawyer : lawyerSet) {
			System.out.println("Lawyer id : " + lawyer.getId() + "have " + lawyer.getCasesHandled().size() + " cases");
		}
	}

}
