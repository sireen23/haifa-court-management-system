package control;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import model.Case;
import model.Judge;
import model.SessionManager;
import model.User;

public class JudgeController {
	@FXML
	private MediaView mediaView;
	@FXML
	private TableView<Case> casesTable;
	@FXML
	private TableColumn<Case, String> colCode;
	@FXML
	private TableColumn<Case, String> colType;
	@FXML
	private TableColumn<Case, String> colStatus;
	@FXML
	private TableColumn<Case, String> colOpenDate;
	@FXML
	private TableColumn<Case, String> colAccused;
	@FXML
	private TableColumn<Case, String> colLawyer;
	@FXML
	private ImageView calendar, logout, profileImage;

	@FXML
	private Label manageCases, scheduleMeeting, verdictsAndAppeals, judgeQueries, judgeNameLabel;

	private MediaPlayer mediaPlayer;
	private Judge loggedInJudge;
	private boolean isInitialized = false; // ✅ Prevent premature access to FXML components

	public void setJudge(Judge judge) {
		this.loggedInJudge = judge;

		if (loggedInJudge == null) {
			System.err.println("❌ ERROR: Null Judge in setJudge()");
			return;
		}

		System.out.println("✅ Judge Set: " + loggedInJudge.getFullName());

		// ✅ Set the Judge's Name in the Label
		judgeNameLabel.setText(loggedInJudge.getFullName());

		// 🔥 Load and Set Profile Image
		if (loggedInJudge.getProfileImagePath() != null) {
			System.out.println("📸 Judge Image Path: " + loggedInJudge.getProfileImagePath());

			File imageFile = new File(loggedInJudge.getProfileImagePath());
			if (imageFile.exists()) {
				profileImage.setImage(new Image(imageFile.toURI().toString())); // ✅ Load Image
				System.out.println("✅ Profile Image Loaded!");
			} else {
				System.err
						.println("❌ ERROR: Profile Image File NOT Found! Expected at: " + imageFile.getAbsolutePath());
				profileImage.setImage(new Image(getClass().getResourceAsStream("/uploaded_images/default_image.png"))); // ✅
																														// Set
																														// Default
																														// Image
			}
		} else {
			System.err.println("❌ ERROR: Judge has NO Profile Image Path!");
			profileImage.setImage(new Image(getClass().getResourceAsStream("/uploaded_images/default_image.png"))); // ✅
																													// Set
																													// Default
																													// Image
		}

		// ✅ If FXML is already initialized, load cases immediately
		if (isInitialized) {
			loadJudgeCases();
		}
	}

	@FXML
	public void initialize() {
		try {
			// ✅ Load Background Video First
			String videoPath = getClass().getResource("/resources/judgeBackground.mp4").toExternalForm();
			Media media = new Media(videoPath);
			mediaPlayer = new MediaPlayer(media);
			mediaView.setMediaPlayer(mediaPlayer);
			mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
			mediaPlayer.setMute(true);
			mediaPlayer.play();

			// ✅ Set Button Click Listeners
			manageCases.setOnMouseClicked(this::openJudgeCases);
			scheduleMeeting.setOnMouseClicked(this::openAddMeeting);
			verdictsAndAppeals.setOnMouseClicked(this::openAddVerdict);
			judgeQueries.setOnMouseClicked(this::openJudgeQueries);
			calendar.setOnMouseClicked(this::openCalendarPage);
			logout.setOnMouseClicked(this::handleLogout);

			// ✅ Run SessionManager Code AFTER JavaFX Fully Loads UI
			Platform.runLater(() -> {
				if (judgeNameLabel == null) {
					System.err.println("❌ ERROR: judgeNameLabel is still NULL! UI components not loaded yet.");
					return;
				}

				User loggedInUser = SessionManager.getInstance().getLoggedInUser();

				if (loggedInUser != null && loggedInUser.getRole().equals("Judge")) {
					loggedInJudge = (Judge) Court.getInstance().getAllLawyers().get(loggedInUser.getPersonId());

					if (loggedInJudge != null) {
						System.out.println("✅ Judge Retrieved: " + loggedInJudge.getFullName());
						judgeNameLabel.setText(loggedInJudge.getFullName()); // 🔥 FIXED: Runs only when
																				// UI is fully loaded
					} else {
						System.err.println("❌ ERROR: Judge not found in Court HashMap.");
					}
				} else {
					System.err.println("❌ ERROR: No logged-in judge found in SessionManager.");
				}

				// ✅ If judge data exists, load cases now
				if (loggedInJudge != null) {
					loadJudgeCases();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		isInitialized = true; // ✅ Ensures FXML Components Are Fully Loaded
	}

	@FXML
	public void handleLogout(MouseEvent event) {
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
			// Load FXML and manually set the JudgeMeetingsController
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CalendarPublicPage.fxml"));
			JudgeMeetingsController controller = new JudgeMeetingsController();
			loader.setController(controller);

			// Load the FXML page
			AnchorPane page = loader.load();

			// Pass the logged-in judge to the controller
			controller.setJudge(loggedInJudge); // ✅ Ensure judge info is passed

			// Open the calendar in a new stage
			Stage newStage = new Stage();
			newStage.setScene(new Scene(page));
			newStage.setTitle("Judge Meetings Calendar");
			newStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openJudgeQueries(MouseEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/JudgeQueries.fxml"));
			AnchorPane page = loader.load();

			// ✅ Get Controller and Set Logged-In Judge
			JudgeQueriesController controller = loader.getController();
			controller.setJudge(loggedInJudge); // Ensure JudgeQueriesController has a setJudge() method

			// ✅ Close current Judge.fxml window
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();

			// ✅ Open new form
			Stage newStage = new Stage();
			newStage.setScene(new Scene(page));
			newStage.setTitle("Judge Queries");
			newStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openAddVerdict(MouseEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddVerdict.fxml"));
			AnchorPane page = loader.load();

			// ✅ Get Controller and Set Logged-In Judge
			AddVerdictController controller = loader.getController();
			controller.setJudge(loggedInJudge); // Ensure this method exists in AddMeetingController

			// ✅ Close current Judge.fxml window
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();

			// ✅ Open new form
			Stage newStage = new Stage();
			newStage.setScene(new Scene(page));
			newStage.setTitle("Schedule a New Meeting");
			newStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openAddMeeting(MouseEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/JudgeManageMeetings.fxml"));
			AnchorPane page = loader.load();

			// ✅ Get Controller and Set Logged-In Judge
			AddMeetingController controller = loader.getController();
			controller.setJudge(loggedInJudge); // Ensure this method exists in AddMeetingController

			// ✅ Close current Judge.fxml window
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();

			// ✅ Open new form
			Stage newStage = new Stage();
			newStage.setScene(new Scene(page));
			newStage.setTitle("Schedule a New Meeting");
			newStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openJudgeCases(MouseEvent event) {
		openForm(event, "/view/JudgeManageCases.fxml", "Judge Manage Cases");
	}

	private void openForm(MouseEvent event, String fxmlPath, String title) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
			AnchorPane page = loader.load();

			JudgeCasesController controller = loader.getController();
			controller.setJudge(loggedInJudge);

			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();

			Stage newStage = new Stage();
			newStage.setScene(new Scene(page));
			newStage.setTitle(title);
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadJudgeCases() {
		if (loggedInJudge == null || casesTable == null) {
			System.out.println("⚠️ Judge is not logged in or TableView is not initialized.");
			return;
		}

		List<Case> judgeCases = loggedInJudge.getCasesPresided().stream().toList();
		casesTable.getItems().setAll(judgeCases);
		System.out.println("✅ Loaded " + judgeCases.size() + " cases for Judge ID: " + loggedInJudge.getId());
	}
}
