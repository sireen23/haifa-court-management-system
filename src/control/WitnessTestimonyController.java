package control;

import java.net.URL;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

import enums.Gender;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import model.Case;
import model.Testimony;
import model.Witness;

public class WitnessTestimonyController implements Initializable {

	@FXML
	private TextField witnessID, witnessFirstName, witnessLastName, witnessAddress, witnessPhoneNumber, witnessEmail;
	@FXML
	private DatePicker witnessBirthDate;
	@FXML
	private ComboBox<Gender> genderComboBox;
	@FXML
	private TextArea testimonyContent;
	@FXML
	private ComboBox<String> TestimonyCaseIDSCombo; // Dropdown to select the case ID
	@FXML
	private Button addButton, resetButton;

	private Court court;

	@FXML
	private ImageView backImage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		court = Court.getInstance();

		// Populate Gender Options
		genderComboBox.getItems().addAll(Gender.F, Gender.M);

		// Load Available Cases
		Map<String, Case> allCases = court.getAllCases();
		if (allCases != null) {
			TestimonyCaseIDSCombo.getItems().addAll(allCases.keySet());
		} else {
			System.out.println("❌ Court's case map is null.");
		}

		// Add button click event
		addButton.setOnAction(event -> addWitnessAndTestimony());
		resetButton.setOnAction(event -> clearFields());

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/main.fxml", "Main Page");
		});
	}

	private void addWitnessAndTestimony() {
		try {
			// ✅ Get Witness Information
			int id = Integer.parseInt(witnessID.getText());
			String firstName = witnessFirstName.getText();
			String lastName = witnessLastName.getText();
			Date birthDate = Date.from(witnessBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
			String address = witnessAddress.getText();
			String phone = witnessPhoneNumber.getText();
			String email = witnessEmail.getText();
			Gender gender = genderComboBox.getValue();

			// ✅ Validate Witness Inputs
			if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || phone.isEmpty() || email.isEmpty()
					|| gender == null || birthDate == null) {
				showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill all witness details.");
				return;
			}

			// ✅ Create and Add Witness
			Witness witness = new Witness(id, firstName, lastName, birthDate, address, phone, email, gender);
			court.addWitness(witness); // Save witness in the system
			System.out.println("✅ Witness added: " + witness.getFirstName() + " " + witness.getLastName());

			// ✅ Get Testimony Information
			String content = testimonyContent.getText();
			String selectedCaseID = TestimonyCaseIDSCombo.getValue();

			// ✅ Validate Testimony Inputs
			if (content.isEmpty() || selectedCaseID == null) {
				showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill all testimony details.");
				return;
			}

			Case selectedCase = court.getRealCase(selectedCaseID);
			if (selectedCase == null) {
				showAlert(Alert.AlertType.ERROR, "Error", "Selected case does not exist.");
				return;
			}

			// ✅ Create and Add Testimony
			Testimony testimony = new Testimony(selectedCase, content, witness);
			court.addTestimony(testimony);
			System.out.println("✅ Testimony added for witness ID: " + witness.getId());

			// ✅ Confirmation Message
			showAlert(Alert.AlertType.INFORMATION, "Success", "Testimony recorded successfully.");

			// ✅ Clear Fields After Submission
			clearFields();

		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Invalid Input", "Witness ID must be a number.");
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Unexpected Error", "Something went wrong!");
		}
	}

	// ✅ Helper Method to Show Alerts
	private void showAlert(Alert.AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	// ✅ Helper Method to Clear Input Fields After Submission
	private void clearFields() {
		witnessID.clear();
		witnessFirstName.clear();
		witnessLastName.clear();
		witnessBirthDate.setValue(null);
		witnessAddress.clear();
		witnessPhoneNumber.clear();
		witnessEmail.clear();
		genderComboBox.getSelectionModel().clearSelection();
		TestimonyCaseIDSCombo.getSelectionModel().clearSelection();
		testimonyContent.clear();
	}

}
