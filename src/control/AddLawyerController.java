package control;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
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
import model.Lawyer;

public class AddLawyerController {

	@FXML
	private TextField id, fname, lname, address, phone, email, salary, licenceNumber;

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
	private ImageView profileImageView; // Lawyer Profile Image

	private File selectedImageFile; // Stores the uploaded image file

	private Court court;
	private String imagePath = "src/uploaded_images/default_image.png"; // Default image path

	public void initialize() {
		System.out.println("Initializing Add Lawyer Form...");

		// Load Gender Enum into ComboBox
		gender.getItems().setAll(Gender.values());

		// Load Specialization Enum into ComboBox
		specialization.getItems().setAll(Specialization.values());

		// ✅ Load Court Data from File
		court = Court.getInstance();

		// Set button actions
		add.setOnAction(event -> handleAddLawyer());
		reset.setOnAction(event -> handleResetForm());
		back.setOnAction(event -> handleBackToMenu());

		// ✅ Set image selection on click
		profileImageView.setOnMouseClicked(this::handleImageUpload);
	}

	// ✅ Handle Image Upload (Fix `selectedImageFile` assignment)
	private void handleImageUpload(MouseEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Lawyer Profile Picture");
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
				String newFileName = "lawyer_" + id.getText() + ".png";
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

	// Handle Add Lawyer
	private void handleAddLawyer() {
		try {
			// ✅ Check if any field is empty
			if (id.getText().isEmpty() || fname.getText().isEmpty() || lname.getText().isEmpty()
					|| bdate.getValue() == null || address.getText().isEmpty() || phone.getText().isEmpty()
					|| email.getText().isEmpty() || salary.getText().isEmpty() || licenceNumber.getText().isEmpty()
					|| gender.getValue() == null || specialization.getValue() == null || userID.getText().isEmpty()
					|| password.getText().isEmpty()) {
				showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required!");
				return;
			}

			// ✅ Parse Inputs
			int lawyerID = Integer.parseInt(id.getText());
			String firstName = fname.getText();
			String lastName = lname.getText();
			Date birthDate = Date.valueOf(bdate.getValue());
			String lawyerAddress = address.getText();
			String phoneNumber = phone.getText();
			String lawyerEmail = email.getText();
			Gender lawyerGender = gender.getValue();
			Specialization lawyerSpecialization = specialization.getValue();
			int licenseNum = Integer.parseInt(licenceNumber.getText());
			double lawyerSalary = Double.parseDouble(salary.getText());
			String username = userID.getText();
			String userPassword = password.getText();

			// ✅ Check if Lawyer Exists
			if (court.getAllLawyers().containsKey(lawyerID)) {
				throw new ObjectAlreadyExistsException("Lawyer with ID " + lawyerID + " already exists.");
			}
			if (court.getUsers().containsKey(username)) {
				throw new ObjectAlreadyExistsException("Username is already taken!");
			}
			if (birthDate.after(Court.TODAY)) {
				throw new FutureDateException("Birthdate cannot be in the future!");
			}
			if (lawyerSalary < 0) {
				throw new NegativeSalaryException("Salary cannot be negative!");
			}

			// ✅ Debugging: Check if `selectedImageFile` is properly assigned
			System.out.println("🔍 Checking selectedImageFile in handleAddLawyer: "
					+ (selectedImageFile != null ? selectedImageFile.getAbsolutePath() : "null"));

			// ✅ Ensure Image File is Selected and Saved
			String finalImagePath;
			if (selectedImageFile != null) {
				File destinationFile = new File("src/uploaded_images/lawyer_" + lawyerID + ".png");

				// ✅ Ensure directory exists
				File imageDir = destinationFile.getParentFile();
				if (!imageDir.exists()) {
					imageDir.mkdirs();
				}

				// ✅ Copy the file correctly
				Files.copy(selectedImageFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				finalImagePath = destinationFile.getAbsolutePath();
				System.out.println("✅ Image path set to: " + finalImagePath);
			} else {
				finalImagePath = "src/uploaded_images/default_image.png";
				System.err.println("❌ No image file selected. Assigning default image.");
			}

			// ✅ Create Lawyer Object
			Lawyer newLawyer = new Lawyer(lawyerID, firstName, lastName, birthDate, lawyerAddress, phoneNumber,
					lawyerEmail, lawyerGender, lawyerSpecialization, licenseNum, lawyerSalary);

			// ✅ Set Lawyer Profile Image Path
			newLawyer.setProfileImagePath(finalImagePath);
			System.out.println("✅ Lawyer Profile Image Path Set: " + newLawyer.getProfileImagePath());

			// ✅ Add to Court System
			court.addLawyer(newLawyer);

			// ✅ Add User Credentials
			court.addUser(username, userPassword, "Lawyer", lawyerID);

			// ✅ Save Data Persistently
			Court.saveCourt();

			// ✅ Show Success Message
			showAlert(Alert.AlertType.INFORMATION, "Success", "Lawyer added successfully!");

			// ✅ Reset Form
			handleResetForm();

		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "ID, Salary, and License Number must be numbers.");
		} catch (FutureDateException e) {
			showAlert(Alert.AlertType.ERROR, "Date Error", e.getMessage());
		} catch (NegativeSalaryException e) {
			showAlert(Alert.AlertType.ERROR, "Salary Error", e.getMessage());
		} catch (ObjectAlreadyExistsException e) {
			showAlert(Alert.AlertType.ERROR, "Duplicate Entry", e.getMessage());
		} catch (IllegalArgumentException e) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", e.getMessage());
		} catch (IOException e) {
			showAlert(Alert.AlertType.ERROR, "File Error", "Failed to save lawyer's profile image.");
			e.printStackTrace();
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
		address.clear();
		phone.clear();
		email.clear();
		salary.clear();
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
