package control;

import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Appeal;
import model.Verdict;

public class LawyerAppealController {

	@FXML
	private TextArea summary;
	@FXML
	private DatePicker date;
	@FXML
	private ComboBox<Verdict> currentVerdict, newVerdict;
	@FXML
	private Button submitButton;
	@FXML
	private ImageView backButton, resetButton;

	private Court court;

	public void initialize() {
		court = Court.getInstance();

		// ✅ Extract all Verdicts from the HashMap and merge into a single list
		List<Verdict> allVerdicts = court.getAllVerdicts().values().stream().collect(Collectors.toList());

		// ✅ Populate Verdict ComboBoxes
		currentVerdict.getItems().setAll(allVerdicts);
		newVerdict.getItems().setAll(allVerdicts);

		// ✅ Set Event Handlers
		submitButton.setOnAction(event -> submitAppeal());
		resetButton.setOnMouseClicked(event -> resetForm());
		backButton.setOnMouseClicked(event -> goBack(event));
	}

	private void submitAppeal() {
		// ✅ Validate Inputs
		if (summary.getText().isEmpty() || date.getValue() == null || currentVerdict.getValue() == null
				|| newVerdict.getValue() == null) {
			showAlert(Alert.AlertType.ERROR, "Missing Fields", "Please fill in all fields before submitting.");
			return;
		}

		if (currentVerdict.getValue().equals(newVerdict.getValue())) {
			showAlert(Alert.AlertType.WARNING, "Invalid Verdicts",
					"New verdict cannot be the same as the current verdict.");
			return;
		}

		// ✅ Create and Add Appeal
		Appeal appeal = new Appeal(summary.getText(), java.sql.Date.valueOf(date.getValue()), currentVerdict.getValue(),
				newVerdict.getValue());

		court.addAppeal(appeal);
		showAlert(Alert.AlertType.INFORMATION, "Success", "Appeal submitted successfully.");
		resetForm(); // Clear Form After Submission
	}

	private void resetForm() {
		summary.clear();
		date.setValue(null);
		currentVerdict.setValue(null);
		newVerdict.setValue(null);
	}

	private void goBack(javafx.scene.input.MouseEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Lawyer.fxml"));
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(loader.load()));
			stage.setTitle("Lawyer Dashboard");
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not go back.");
		}
	}

	private void showAlert(Alert.AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
