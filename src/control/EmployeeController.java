package control;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import enums.Position;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import model.Employee;
import model.SessionManager;
import model.User;

public class EmployeeController implements Initializable {

	@FXML
	private MediaView mediaView; // ✅ Ensure this matches the FXML ID

	@FXML
	private AnchorPane videoPane; // ✅ Optional: Used for resizing

	@FXML
	private ImageView logout2, image, calendar;

	@FXML
	private Button casesManagement, lawyersJudges, schedule, queries, manageCourtRooms, manageDepartments;

	@FXML
	private Label employeeNameLabel;

	private MediaPlayer mediaPlayer;

	private Employee loggedInEmployee; // Store the logged-in employee

	public void setLoggedInEmployee(Employee employee) {
		this.loggedInEmployee = employee;
		if (loggedInEmployee.getPosition() == Position.Manager) {
			calendar.setVisible(true); // Show meetings button
		} else {
			calendar.setVisible(false); // Hide it for non-managers
		}
	}

	public void setEmployee(Employee employee) {
		this.loggedInEmployee = employee;

		if (loggedInEmployee == null) {
			System.err.println("❌ ERROR: Null Employee in setEmployee()");
			return;
		}

		System.out.println("✅ Employee Set: " + loggedInEmployee.getFullName());

		// 🔥 Ensure Correct Image Path
		String imagePath = loggedInEmployee.getProfileImagePath();
		System.out.println("📸 Employee Image Path: " + imagePath);

		if (imagePath == null || imagePath.isEmpty()) {
			System.err.println("❌ ERROR: Employee has NO Profile Image Path!");
			return;
		}

		File imageFile = new File(imagePath);
		if (imageFile.exists()) {
			image.setImage(new Image(imageFile.toURI().toString())); // ✅ Load Image
			System.out.println("✅ Profile Image Loaded!");
		} else {
			System.err.println("❌ ERROR: Profile Image File NOT Found! Expected at: " + imageFile.getAbsolutePath());
		}
	}

	// ✅ New method to set the employee's profile image
	private void loadProfileImage() {
		if (loggedInEmployee != null) {
			String imagePath = loggedInEmployee.getProfileImagePath();

			// 🔥 FIX: Ensure path format is correct
			if (imagePath != null && !imagePath.trim().isEmpty()) {
				try {
					Image profileImage = new Image("file:" + imagePath, true);
					image.setImage(profileImage);
					System.out.println("✅ Profile Image Loaded: " + imagePath);
				} catch (Exception e) {
					System.err.println("❌ ERROR: Unable to load profile image: " + e.getMessage());
				}
			} else {
				System.err.println("⚠️ WARNING: No profile image path found.");
			}
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("✅ Initializing EmployeeController...");

		// 🛑 Check if elements exist
		if (mediaView == null) {
			System.out.println("❌ ERROR: mediaView is NULL! Check FXML.");
			return;
		}

		// ✅ Load the video
		try {
			URL mediaURL = getClass().getResource("/resources/emp.mp4");

			if (mediaURL == null) {
				throw new Exception("❌ Video file emp.mp4 not found!");
			}

			Media media = new Media(mediaURL.toExternalForm());
			mediaPlayer = new MediaPlayer(media);
			mediaView.setMediaPlayer(mediaPlayer);

			// ✅ Play video in loop
			mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
			mediaPlayer.play();

			System.out.println("🎬 Video background loaded successfully!");

		} catch (Exception e) {
			System.out.println("❌ Error loading video: " + e.getMessage());
			e.printStackTrace();
		}

		// ✅ Set up button actions
		casesManagement.setOnMouseClicked(this::openCasesManagement);
		logout2.setOnMouseClicked(this::handleLogout);
		manageCourtRooms.setOnMouseClicked(this::openCourtRooms);
		manageDepartments.setOnMouseClicked(this::openDepartments);
		queries.setOnMouseClicked(this::openQueries);

		if (calendar != null) { // Ensure it's not null before using it
			calendar.setOnMouseClicked(event -> openCalendarPage());
		} else {
			System.err.println("❌ ERROR: Calendar button is NULL in EmployeeController!");
		}

		// ✅ Run SessionManager Code AFTER JavaFX Fully Loads UI
		Platform.runLater(() -> {
			if (employeeNameLabel == null) {
				System.err.println("❌ ERROR: employeeNameLabel is still NULL! UI components not loaded yet.");
				return;
			}

			User loggedInUser = SessionManager.getInstance().getLoggedInUser();

			if (loggedInUser != null && loggedInUser.getRole().equals("Employee")) {
				loggedInEmployee = (Employee) Court.getInstance().getAllEmployees().get(loggedInUser.getPersonId());

				if (loggedInEmployee != null) {
					System.out.println("✅ Employee Retrieved: " + loggedInEmployee.getFullName());
					employeeNameLabel.setText(loggedInEmployee.getFirstName() + "\n" + loggedInEmployee.getLastName());
					// UI is fully loaded
				} else {
					System.err.println("❌ ERROR: Employee not found in Court HashMap.");
				}
			} else {
				System.err.println("❌ ERROR: No logged-in employee found in SessionManager.");
			}
		});
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

	@FXML
	private void openCalendarPage() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CalendarPublicPage.fxml"));

			// Set EmployeeMeetingsController dynamically for employees
			loader.setController(new EmployeeMeetingsController());

			AnchorPane page = loader.load();

			Stage stage = new Stage();
			stage.setScene(new Scene(page));
			stage.setTitle("All Court Meetings");
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ✅ Open Queries & Reports
	private void openQueries(MouseEvent event) {
		openForm(event, "/view/EmployeeQueries.fxml", "Employee Activate Queries");
	}

	private void openCourtRooms(MouseEvent event) {
		openForm(event, "/view/ManageCourtRooms.fxml", "Employee Manage Court Rooms");
	}

	private void openDepartments(MouseEvent event) {
		openForm(event, "/view/ManageDepartments.fxml", "Employee Manage Court Rooms");
	}

	// ✅ Open Cases Management Page
	private void openCasesManagement(MouseEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CaseManagement.fxml"));
			AnchorPane page = loader.load();

			// Pass the logged-in employee to CaseManagementController
			CaseManagementController caseController = loader.getController();
//			caseController.setLoggedInEmployee(loggedInEmployee); // ✅ Pass employee here

			// Close current window and open new
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();

			Stage newStage = new Stage();
			newStage.setScene(new Scene(page));
			newStage.setTitle("Case Management");
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openLawyersJudges() {
		System.out.println("⚖️ Opening Lawyers & Judges Management...");
	}

	// ✅ Open Schedule and Meetings
	private void openSchedule() {
		System.out.println("📅 Opening Advanced Scheduling...");
	}

	// ✅ Handle Logout
//	private void handleLogout() {
//		System.out.println("🚪 Logging out...");
//		try {
//			Stage stage = (Stage) logout1.getScene().getWindow();
//			stage.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("❌ Error closing Employee Page.");
//		}
//	}

	// ✅ Stop media when closing the app
	public void stopMedia() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.dispose(); // ✅ Release resources
			System.out.println("✅ Media Player Released.");
		}
	}

	private void openForm(MouseEvent event, String fxmlPath, String title) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
			AnchorPane page = loader.load();

			// Close current admin window
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();

			// Open new form
			Stage newStage = new Stage();
			newStage.setScene(new Scene(page));
			newStage.setTitle(title);
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
