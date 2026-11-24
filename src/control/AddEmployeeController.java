package control;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.util.Objects;

import enums.Gender;
import enums.Position;
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
import model.Employee;

public class AddEmployeeController {

	@FXML
	private TextField id, fname, lname, address, phone, email, salary;

	@FXML
	private TextField userID, password; // New fields for login

	@FXML
	private DatePicker bdate, startWork;

	@FXML
	private ComboBox<Gender> gender;

	@FXML
	private ComboBox<Position> position;

	@FXML
	private Button add, reset, back;

	@FXML
	private ImageView profileImageView, backImage; // Employee Profile Image

	private File selectedImageFile; // Stores the uploaded image file

	private Court court;
	private String imagePath = "src/uploaded_images/default_image.png"; // Default image path

	public void initialize() {
		System.out.println("Initializing Add Employee Form...");

		// Load Gender and Position Enum into ComboBox
		gender.getItems().setAll(Gender.values());
		position.getItems().setAll(Position.values());

		// ✅ Load Court Data from File
		court = Court.getInstance();

		System.out.println("✅ Initializing AddEmployeeController...");
		System.out.println("🔍 Checking FXML Bindings...");

		if (id == null) {
			System.out.println("❌ ERROR: 'id' TextField is NULL! Check FXML.");
		}
		if (fname == null) {
			System.out.println("❌ ERROR: 'fname' TextField is NULL! Check FXML.");
		}
		if (lname == null) {
			System.out.println("❌ ERROR: 'lname' TextField is NULL! Check FXML.");
		}
		if (userID == null) {
			System.out.println("❌ ERROR: 'userID' TextField is NULL! Check FXML.");
		}
		if (password == null) {
			System.out.println("❌ ERROR: 'password' TextField is NULL! Check FXML.");
		}

		// Set button actions
		add.setOnAction(event -> handleAddEmployee());
		reset.setOnAction(event -> handleResetForm());
		back.setOnAction(event -> handleBackToMenu());

		// ✅ Set image selection on click
		profileImageView.setOnMouseClicked(this::handleImageUpload);

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});

	}

	// ✅ Handle Image Upload
	private void handleImageUpload(MouseEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Employee Profile Picture");
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

		File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

		if (selectedFile != null) {
			try {
				File destFolder = new File("src/uploaded_images/");
				if (!destFolder.exists()) {
					destFolder.mkdirs();
				}

				// Generate unique image name based on Employee ID
				String newFileName = "employee_" + id.getText() + ".png";
				File destFile = new File(destFolder, newFileName);

				// ✅ Copy the selected image to the uploaded_images folder
				Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				// ✅ UPDATE `selectedImageFile` so it can be used in `handleAddEmployee`
				selectedImageFile = destFile;

				// ✅ Update `imagePath` with the correct path
				imagePath = destFile.getAbsolutePath();

				// ✅ Update UI with the selected image
				profileImageView.setImage(new Image(destFile.toURI().toString()));

				System.out.println("✅ Image uploaded successfully: " + imagePath);
				System.out.println("✅ selectedImageFile is now: " + selectedImageFile.getAbsolutePath()); // Debugging

			} catch (IOException e) {
				showAlert(Alert.AlertType.ERROR, "Image Upload Error", "Failed to upload image.");
				e.printStackTrace();
			}
		} else {
			System.err.println("❌ No image file selected.");
		}
	}

	// Handle Add Employee
	private void handleAddEmployee() {
		try {
			// ✅ Check if any field is empty
			if (id.getText().isEmpty() || fname.getText().isEmpty() || lname.getText().isEmpty()
					|| bdate.getValue() == null || startWork.getValue() == null || address.getText().isEmpty()
					|| phone.getText().isEmpty() || email.getText().isEmpty() || salary.getText().isEmpty()
					|| gender.getValue() == null || position.getValue() == null || userID.getText().isEmpty()
					|| password.getText().isEmpty()) {
				showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required!");
				return;
			}

			// ✅ Parse Inputs
			int employeeID = Integer.parseInt(id.getText());
			String firstName = fname.getText();
			String lastName = lname.getText();
			Date birthDate = Date.valueOf(bdate.getValue());
			Date startDate = Date.valueOf(startWork.getValue());
			String employeeAddress = address.getText();
			String phoneNumber = phone.getText();
			String employeeEmail = email.getText();
			Gender employeeGender = gender.getValue();
			Position employeePosition = position.getValue();
			double employeeSalary = Double.parseDouble(salary.getText());
			String username = userID.getText();
			String userPassword = password.getText();

			// ✅ Check if Employee Exists
			if (court.getAllEmployees().containsKey(employeeID)) {
				throw new ObjectAlreadyExistsException("Employee with ID " + employeeID + " already exists.");
			}
			if (court.getUsers().containsKey(username)) {
				throw new ObjectAlreadyExistsException("Username is already taken!");
			}
			if (birthDate.after(Court.TODAY) || startDate.after(Court.TODAY)) {
				throw new FutureDateException("Birthdate or Start Work Date cannot be in the future!");
			}
			if (employeeSalary < 0) {
				throw new NegativeSalaryException("Salary cannot be negative!");
			}

			// ✅ Ensure Image File is Selected
			String finalImagePath;
			if (selectedImageFile != null) {
				finalImagePath = "src/uploaded_images/employee_" + employeeID + ".png";
				saveProfileImage(selectedImageFile, employeeID); // ✅ Save Image to Directory
			} else {
				System.err.println("❌ ERROR: No image file selected. Assigning default image.");
				finalImagePath = "src/uploaded_images/default_image.png";
			}

			System.out.println("🔹 Final Image Path to be Set: " + finalImagePath); // ✅ Debugging Step

			// ✅ Create Employee Object
			Employee newEmployee = new Employee(employeeID, firstName, lastName, birthDate, employeeAddress,
					phoneNumber, employeeEmail, employeeGender, startDate, employeeSalary, employeePosition);

			// ✅ Ensure Image Path is Set AFTER Employee Creation
			newEmployee.setProfileImagePath(finalImagePath);
			System.out.println("✅ Employee Profile Image Path Set: " + newEmployee.getProfileImagePath());

			// ✅ Add to Court System
			court.addEmployee(newEmployee);

			// ✅ Add User Credentials
			court.addUser(username, userPassword, "Employee", employeeID);

			// ✅ Save Data Persistently
			Court.saveCourt();

			showAlert(Alert.AlertType.INFORMATION, "Success", "Employee added successfully!");

			// ✅ Reset Form
			handleResetForm();

		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "ID and Salary must be numbers.");
		} catch (FutureDateException e) {
			showAlert(Alert.AlertType.ERROR, "Date Error", e.getMessage());
		} catch (NegativeSalaryException e) {
			showAlert(Alert.AlertType.ERROR, "Salary Error", e.getMessage());
		} catch (ObjectAlreadyExistsException e) {
			showAlert(Alert.AlertType.ERROR, "Duplicate Entry", e.getMessage());
		} catch (IllegalArgumentException e) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
		}
	}

	// ✅ Save Uploaded Profile Image
	private void saveProfileImage(File selectedFile, int employeeID) {
		if (selectedFile != null && selectedFile.exists()) {
			File destFile = new File("src/uploaded_images/employee_" + employeeID + ".png");
			try {
				Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				System.out.println("✅ Profile image saved successfully at: " + destFile.getAbsolutePath());
			} catch (IOException e) {
				System.err.println("❌ ERROR: Failed to save image: " + e.getMessage());
			}
		} else {
			System.err.println("❌ ERROR: Selected file does not exist or is null!");
		}
	}

	// ✅ Handle Reset Form
	private void handleResetForm() {
		id.clear();
		fname.clear();
		lname.clear();
		userID.clear();
		password.clear();
		bdate.setValue(null);
		startWork.setValue(null);
		address.clear();
		phone.clear();
		email.clear();
		salary.clear();
		gender.setValue(null);
		position.setValue(null);
		profileImageView.setImage(new Image(
				Objects.requireNonNull(getClass().getResourceAsStream("/uploaded_images/default_image.png"))));
		imagePath = "src/uploaded_images/default_image.png";
	}

	// ✅ Handle Back to Menu
	private void handleBackToMenu() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Admin.fxml"));
			AnchorPane adminPage = loader.load();
			Stage currentStage = (Stage) back.getScene().getWindow();
			currentStage.close();
			Stage newStage = new Stage();
			newStage.setScene(new Scene(adminPage));
			newStage.setTitle("Admin Panel");
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ✅ Utility: Show Alert Dialog
	private void showAlert(Alert.AlertType type, String title, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
