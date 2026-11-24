package control;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Objects;

import enums.Gender;
import enums.Specialization;
import exceptions.FutureDateException;
import exceptions.NegativeSalaryException;
import exceptions.ObjectAlreadyExistsException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Judge;

public class AddJudgeController {

	@FXML
	private TextField id, fname, lname, address, phone, email, salary, experienceYear, licenceNumber;

	@FXML
	private TextField userID, password; // New fields for login

	@FXML
	private DatePicker bdate;

	@FXML
	private ComboBox<Gender> gender;

	@FXML
	private ComboBox<Specialization> specialization;

	@FXML
	private Button add, reset, back;

	@FXML
	private ImageView profileImageView, backButton; // ImageView for profile picture

	private File selectedImageFile; // Stores the uploaded image file

	private Court court;
	private String imagePath = "src/uploaded_images/default_image.png"; // Default avatar path

	public void initialize() {
		System.out.println("Initializing Add Judge Form...");

		// Load Gender Enum into ComboBox
		gender.getItems().setAll(Gender.values());

		// Load Specialization Enum into ComboBox
		specialization.getItems().setAll(Specialization.values());

		// Load Court Data from File
		court = Court.getInstance();

		// Set button actions
		add.setOnAction(event -> handleAddJudge());
		reset.setOnAction(event -> handleResetForm());
		back.setOnAction(event -> handleBackToMenu());

		// Set image selection on click
		profileImageView.setOnMouseClicked(this::handleImageUpload);

		backButton.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	// ✅ Handle Image Upload (Fix `selectedImageFile` assignment)
	private void handleImageUpload(MouseEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Judge Profile Picture");
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

		File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

		if (selectedFile != null) {
			try {
				File destFolder = new File("src/uploaded_images/");
				if (!destFolder.exists()) {
					destFolder.mkdirs();
				}

				// Save Unique Image Name
				String newFileName = "judge_" + id.getText() + ".png";
				File destFile = new File(destFolder, newFileName);

				// ✅ Copy the selected image
				Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				// ✅ Assign the copied file to `selectedImageFile`
				selectedImageFile = destFile;

				// ✅ Update ImageView
				profileImageView.setImage(new Image(destFile.toURI().toString()));

				System.out.println("✅ Image uploaded successfully: " + selectedImageFile.getAbsolutePath());

			} catch (IOException e) {
				showAlert(Alert.AlertType.ERROR, "Image Upload Error", "Failed to upload image.");
				e.printStackTrace();
			}
		} else {
			System.err.println("❌ No image file selected.");
		}
	}

	// ✅ Handle Add Judge
	private void handleAddJudge() {
		try {
			if (id.getText().isEmpty() || fname.getText().isEmpty() || lname.getText().isEmpty()
					|| bdate.getValue() == null || address.getText().isEmpty() || phone.getText().isEmpty()
					|| email.getText().isEmpty() || salary.getText().isEmpty() || experienceYear.getText().isEmpty()
					|| licenceNumber.getText().isEmpty() || gender.getValue() == null
					|| specialization.getValue() == null || userID.getText().isEmpty()
					|| password.getText().isEmpty()) {
				showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required!");
				return;
			}

			// ✅ Parse Inputs
			int judgeID = Integer.parseInt(id.getText());
			String firstName = fname.getText();
			String lastName = lname.getText();
			java.sql.Date birthDate = java.sql.Date.valueOf(bdate.getValue());
			String judgeAddress = address.getText();
			String phoneNumber = phone.getText();
			String judgeEmail = email.getText();
			Gender judgeGender = gender.getValue();
			Specialization judgeSpecialization = specialization.getValue();
			int licenseNum = Integer.parseInt(licenceNumber.getText());
			double judgeSalary = Double.parseDouble(salary.getText());
			int experience = Integer.parseInt(experienceYear.getText());
			String username = userID.getText();
			String userPassword = password.getText();

			if (court.getUsers() == null) {
				System.out.println("❌ ERROR: Users HashMap is NULL!");
			} else {
				System.out.println("✅ Users HashMap Loaded Successfully.");
			}

			// ✅ Check for errors
			if (court.getAllLawyers().containsKey(judgeID)) {
				throw new ObjectAlreadyExistsException("Judge with ID " + judgeID + " already exists.");
			}
			if (birthDate.after(new Date()))
				throw new FutureDateException("Birth date cannot be in the future.");
			if (judgeSalary < 0)
				throw new NegativeSalaryException("Salary cannot be negative.");
			if (experience < 0)
				throw new IllegalArgumentException("Experience years cannot be negative.");
			if (court.getUsers().containsKey(username)) {
				throw new ObjectAlreadyExistsException("Username is already taken!");
			}

			// ✅ Set Image Path
			String imagePath;
			if (selectedImageFile != null) {
				File destinationFile = new File("src/uploaded_images/judge_" + judgeID + ".png");

				// ✅ Ensure directory exists
				File imageDir = destinationFile.getParentFile();
				if (!imageDir.exists()) {
					imageDir.mkdirs();
				}

				// ✅ Copy the file correctly
				Files.copy(selectedImageFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				imagePath = destinationFile.getAbsolutePath();
			} else {
				imagePath = "src/uploaded_images/default_image.png";
			}

			// ✅ Create Judge Object
			Judge newJudge = new Judge(judgeID, firstName, lastName, birthDate, judgeAddress, phoneNumber, judgeEmail,
					judgeGender, judgeSpecialization, licenseNum, judgeSalary, experience);

			newJudge.setProfileImagePath(imagePath); // Store image path

			// ✅ Add Judge to Court
			court.addJudge(newJudge);

			// ✅ Add user credentials
			court.addUser(username, userPassword, "Judge", judgeID);

			// ✅ Save data
			Court.saveCourt();

			// ✅ Show Success Message
			showAlert(Alert.AlertType.INFORMATION, "Success", "Judge added successfully!");

			// ✅ Reset Form
			handleResetForm();

		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Input Error",
					"ID, Salary, Experience, and License Number must be numbers.");
		} catch (FutureDateException | NegativeSalaryException | ObjectAlreadyExistsException e) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
		} catch (IllegalArgumentException e) {
			showAlert(Alert.AlertType.ERROR, "Invalid Input", e.getMessage());
		} catch (Exception e) {
			showAlert(Alert.AlertType.ERROR, "System Error", "Unexpected error: " + e.getMessage());
		}
	}

	private void handleResetForm() {
		id.clear();
		fname.clear();
		lname.clear();
		userID.clear();
		password.clear();
		bdate.setValue(null);
		address.clear();
		phone.clear();
		email.clear();
		salary.clear();
		experienceYear.clear();
		licenceNumber.clear();
		gender.setValue(null);
		specialization.setValue(null);
		profileImageView.setImage(new Image(
				Objects.requireNonNull(getClass().getResourceAsStream("/uploaded_images/default_image.png"))));
		imagePath = "src/uploaded_images/default_image.png";
	}

	// ✅ Handle Back to Menu
	private void handleBackToMenu() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Admin.fxml"));
			AnchorPane adminPage = loader.load();

			// Close current window
			Stage currentStage = (Stage) back.getScene().getWindow();
			currentStage.close();

			// Open admin page
			Stage newStage = new Stage();
			newStage.setScene(new Scene(adminPage));
			newStage.setTitle("Admin Panel");
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
