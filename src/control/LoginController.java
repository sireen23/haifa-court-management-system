package control;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import model.Employee;
import model.Judge;
import model.Lawyer;
import model.SessionManager;
import model.User;

public class LoginController {
	@FXML
	private AnchorPane videoPane;

	@FXML
	private MediaView mediaView;

	@FXML
	private AnchorPane loginPanel;

	@FXML
	private TextField usernameField; // Username Input Field

	@FXML
	private PasswordField passwordField; // Password Input Field

	@FXML
	private Button loginButton; // Login Button
	@FXML
	private Label mainTab, verdict, testimony;

	private MediaPlayer mediaPlayer;

	private Court court;

	public void initialize() {
		// Load Court Data
		court = Court.getInstance();

		// Load video background
		String videoPath = getClass().getResource("/resources/background.mp4").toExternalForm();
		Media media = new Media(videoPath);
		mediaPlayer = new MediaPlayer(media);
		mediaView.setMediaPlayer(mediaPlayer);

		// Resize dynamically
		mediaView.fitWidthProperty().bind(videoPane.widthProperty());
		mediaView.fitHeightProperty().bind(videoPane.heightProperty());

		// Play video in loop
		mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		mediaPlayer.play();

		// Ensure no blur is applied
		loginPanel.setEffect(null); // No blur, fully transparent

		// Handle login button action
		loginButton.setOnAction(event -> handleLogin(event));

		verdict.setOnMouseClicked(this::openVerdictPage);
		testimony.setOnMouseClicked(this::openTestimonyDocumentation);
		mainTab.setOnMouseClicked(this::openMainPage);
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

	private void handleLogin(ActionEvent event) {
		String username = usernameField.getText();
		String password = passwordField.getText();

		if (username.equals("ADMIN") && password.equals("ADMIN")) {
			System.out.println("✅ Admin Login Successful! Opening Admin Panel...");
			openAdminPage(event);
			return;
		}

		// ✅ Check User Credentials in Court System
		User user = Court.getInstance().authenticateUser(username, password);
		if (user != null) {
			System.out.println("✅ Login Successful! Opening " + user.getRole() + " Panel...");
			openUserPage(event, user);
		} else {
			showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password. Please try again.");
		}
	}

	private void openUserPage(ActionEvent event, User user) {
		try {
			// Store the logged-in user in SessionManager
			SessionManager.getInstance().setLoggedInUser(user);

			FXMLLoader loader;
			String fxmlPath = null;
			AnchorPane userPage = null;

			switch (user.getRole()) {
			case "Judge":
				fxmlPath = "/view/Judge.fxml";
				loader = new FXMLLoader(getClass().getResource(fxmlPath));
				userPage = loader.load();

				JudgeController judgeController = loader.getController();
				Judge judge = (Judge) court.getAllLawyers().get(user.getPersonId()); // FIX: getAllJudges()

				if (judge == null) {
					System.err.println("❌ ERROR: Lawyer is NULL in LoginController!");
					return;
				}

				System.out.println("✅ Lawyer Loaded: " + judge.getFullName());
				judgeController.setJudge(judge);
				System.out.println("✅ setLawyer() was successfully called in LoginController.");
				break;

			case "Lawyer":
				fxmlPath = "/view/Lawyer.fxml";
				loader = new FXMLLoader(getClass().getResource(fxmlPath));
				userPage = loader.load();

				LawyerPageController lawyerController = loader.getController();
				Lawyer lawyer = (Lawyer) court.getAllLawyers().get(user.getPersonId());

				if (lawyer == null) {
					System.err.println("❌ ERROR: Lawyer is NULL in LoginController!");
					return;
				}

				System.out.println("✅ Lawyer Loaded: " + lawyer.getFullName());
				lawyerController.setLawyer(lawyer);
				System.out.println("✅ setLawyer() was successfully called in LoginController.");
				break;

			case "Employee":
				fxmlPath = "/view/Employee.fxml";
				loader = new FXMLLoader(getClass().getResource(fxmlPath));
				userPage = loader.load();

				EmployeeController employeeController = loader.getController();
				Employee employee = (Employee) court.getAllEmployees().get(user.getPersonId());

				if (employee == null) {
					System.err.println("❌ ERROR: Employee is NULL in LoginController!");
					return;
				}

				System.out.println("✅ Employee Loaded: " + employee.getFullName());
				employeeController.setEmployee(employee);
				System.out.println("✅ setEmployee() was successfully called in LoginController.");
				break;

			default:
				showAlert(Alert.AlertType.ERROR, "Access Denied", "Invalid user data.");
				return;
			}

			if (userPage != null) {
				openNewPage(event, userPage, user.getRole() + " Dashboard"); // ✅ LawyerPage opens only once
			}

		} catch (IOException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Loading Error", "Failed to load the user interface.");
		}
	}

	// ✅ Helper method to open a new page
	private void openNewPage(ActionEvent event, AnchorPane page, String title) {
		Stage newStage = new Stage();
		newStage.setTitle(title);
		newStage.setScene(new Scene(page));
		newStage.show();

		// ✅ Close current login window
		Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		currentStage.close();
	}

	private void openAdminPage(ActionEvent event) {
		try {
			// Load Admin Page
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Admin.fxml"));
			AnchorPane adminPage = loader.load();

			// Create a new Stage
			Stage adminStage = new Stage();
			adminStage.setTitle("Admin Dashboard");
			adminStage.setScene(new Scene(adminPage));
			adminStage.show();

			// Close current login window
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("❌ Error loading Admin Page.");
		}
	}

	private void showAlert(Alert.AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	@FXML
	private void handleEmployeeLogin() {
		String username = usernameField.getText();
		String password = passwordField.getText();

		User user = court.getUsers().get(username);
		if (user != null && user.getPassword().equals(password) && user.getRole().equals("Employee")) {
			Employee employee = court.getAllEmployees().get(user.getPersonId()); // Retrieve the Employee object

			// Open Employee Panel
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Employee.fxml"));
				AnchorPane page = loader.load();

				EmployeeController controller = loader.getController();
				controller.setLoggedInEmployee(employee); // Pass logged-in employee

				Stage stage = (Stage) loginButton.getScene().getWindow();
				stage.close();

				Stage newStage = new Stage();
				newStage.setScene(new Scene(page));
				newStage.setTitle("Employee Panel");
				newStage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
		}
	}

}
