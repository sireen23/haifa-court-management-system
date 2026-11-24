package control;

import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import enums.Status;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Appeal;
import model.Case;
import model.Judge;
import model.Verdict;

public class AddVerdictController implements Initializable {

	@FXML
	private DatePicker issuedDate;
	@FXML
	private TextField judge;
	@FXML
	private ComboBox<String> casee;
	@FXML
	private ComboBox<String> appeal;
	@FXML
	private TextArea verdictSummary;
	@FXML
	private Button addVerdict, back, resetFields;

	private Court court;
	private Judge loggedInJudge;

	@FXML
	private ImageView backImage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		court = Court.getInstance();

		// Set button actions
		addVerdict.setOnAction(event -> handleAddVerdict());
		back.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Judge.fxml", "Judge Page");
		});
		resetFields.setOnAction(event -> resetFields());

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Judge.fxml", "Judge Page");
		});
	}

	public void setJudge(Judge judge1) {
		this.loggedInJudge = judge1;
		if (loggedInJudge == null) {
			showAlert(Alert.AlertType.ERROR, "Error", "Judge information is missing.");
			return;
		}

		// ✅ Ensure judge has cases
		Set<Case> judgeCases = loggedInJudge.getCasesHandled();
		if (judgeCases == null || judgeCases.isEmpty()) {
			System.err.println("⚠️ No cases found for Judge: " + loggedInJudge.getId());
			return;
		}

		// ✅ Set judge ID (uneditable)
		judge.setText(String.valueOf(loggedInJudge.getId()));
		judge.setEditable(false);

		// ✅ Load judge's cases into the case ComboBox
		List<String> caseIds = judgeCases.stream().map(Case::getCode).collect(Collectors.toList());

		if (casee != null) {
			casee.getItems().clear();
			casee.getItems().setAll(caseIds);
			System.out.println("✅ Cases loaded into ComboBox: " + caseIds);
		} else {
			System.err.println("❌ ERROR: casee ComboBox is NULL.");
		}

		// ✅ Load appeals where the current verdict was issued by this judge
		List<String> appealIds = court.getAllAppeals().values().stream()
				.filter(a -> a.getCurrentVerdict() != null && a.getCurrentVerdict().getJudge().equals(loggedInJudge))
				.map(a -> String.valueOf(a.getAppealID())).collect(Collectors.toList());

		if (appeal != null) {
			appeal.getItems().clear();
			appeal.getItems().setAll(appealIds);
		} else {
			System.err.println("❌ ERROR: appeal ComboBox is NULL.");
		}
	}

	private void handleAddVerdict() {
		if (issuedDate.getValue() == null || verdictSummary.getText().isEmpty() || casee.getValue() == null) {
			showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill in all mandatory fields.");
			return;
		}

		// Convert inputs
		Date selectedDate = Date.valueOf(issuedDate.getValue());
		String verdictText = verdictSummary.getText();
		Case selectedCase = court.getAllCases().get(casee.getValue());
		Appeal selectedAppeal = appeal.getValue() != null
				? court.getAllAppeals().get(Integer.parseInt(appeal.getValue()))
				: null;

		// Ensure selected case exists
		if (selectedCase == null) {
			showAlert(Alert.AlertType.ERROR, "Invalid Case", "The selected case does not exist.");
			return;
		}

		// Create a new verdict
		Verdict newVerdict = new Verdict(verdictText, selectedDate, loggedInJudge, selectedCase, selectedAppeal);

		// Add the verdict to the case and update its status
		selectedCase.setVerdict(newVerdict);
		selectedCase.setCaseStatus(Status.finished);

		// If the verdict is linked to an appeal, update it
		if (selectedAppeal != null) {
			selectedAppeal.setNewVerdict(newVerdict);
		}

		// Save verdict to court
		boolean success = court.addVerdict(newVerdict);
		if (success) {
			showAlert(Alert.AlertType.INFORMATION, "Success", "Verdict added successfully.");
			resetFields();
		} else {
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to add the verdict.");
		}
	}

	private void resetFields() {
		issuedDate.setValue(null);
		verdictSummary.clear();
		casee.getSelectionModel().clearSelection();
		appeal.getSelectionModel().clearSelection();
	}

	private void goBackToJudgePanel() {
		Stage stage = (Stage) back.getScene().getWindow();
		stage.close();
	}

	private void showAlert(Alert.AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
