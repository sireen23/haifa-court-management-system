package control;

import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Case;
import model.Courtroom;
import model.Judge;
import model.Meeting;

public class AddMeetingController implements Initializable {

	@FXML
	private DatePicker meetingDate;
	@FXML
	private Spinner<Integer> meetingHour, minutes;
	@FXML
	private ComboBox<Integer> courtroom;
	@FXML
	private ComboBox<String> casee;
	@FXML
	private Button addMeeting, back, resetFields;

	private Court court;
	private Judge loggedInJudge;

	@FXML
	private ImageView backImage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		court = Court.getInstance();

		// Set spinner ranges (Hours: 0-23, Minutes: 0-59)
		meetingHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
		minutes.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

		// Load available courtrooms
		List<Integer> courtroomNumbers = court.getAllCourtrooms().values().stream().map(Courtroom::getCourtroomNumber)
				.collect(Collectors.toList());
		courtroom.getItems().setAll(courtroomNumbers);

		// Button actions
		addMeeting.setOnAction(event -> handleAddMeeting());
		back.setOnAction(event -> closeWindow());
		resetFields.setOnAction(event -> resetFields());

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Judge.fxml", "Judge Page");
		});
	}

	public void setJudge(Judge judge) {
		this.loggedInJudge = judge;

		if (loggedInJudge != null) {
			// Load cases assigned to the judge
			List<String> caseIds = loggedInJudge.getCasesPresided().stream().map(Case::getCode)
					.collect(Collectors.toList());
			casee.getItems().setAll(caseIds);
		}
	}

	private void handleAddMeeting() {
		// Validate inputs
		if (meetingDate.getValue() == null || courtroom.getValue() == null || casee.getValue() == null) {
			showAlert(Alert.AlertType.WARNING, "Missing Fields", "Please fill all fields before proceeding.");
			return;
		}

		// Convert values
		LocalDate localDate = meetingDate.getValue();
		Date selectedDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Time selectedTime = new Time(meetingHour.getValue(), minutes.getValue(), 0);
		Courtroom selectedCourtroom = court.getAllCourtrooms().get(courtroom.getValue());
		Case selectedCase = court.getAllCases().get(casee.getValue());

		// Validate selected case
		if (selectedCase == null) {
			showAlert(Alert.AlertType.ERROR, "Invalid Case", "Selected case does not exist.");
			return;
		}

		// Create and add meeting
		Meeting newMeeting = new Meeting(selectedDate, selectedTime, selectedCourtroom, selectedCase);
		boolean success = court.addMeeting(newMeeting);

		if (success) {
			Court.saveCourt(); // ✅ Save changes
			showAlert(Alert.AlertType.INFORMATION, "Success", "Meeting added successfully.");

			// ✅ Instead of closing, reset the fields
			resetFields();
		} else {
			showAlert(Alert.AlertType.ERROR, "Failed", "Could not schedule the meeting due to conflicts.");
		}
	}

	private void resetFields() {
		meetingDate.setValue(null);
		courtroom.getSelectionModel().clearSelection();
		casee.getSelectionModel().clearSelection();
		meetingHour.getValueFactory().setValue(12);
		minutes.getValueFactory().setValue(0);
	}

	private void closeWindow() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Judge.fxml"));
			AnchorPane judgePage = loader.load();

			// Pass the judge object back to JudgeController
			JudgeController judgeController = loader.getController();
			judgeController.setJudge(loggedInJudge);

			// Create a new scene and stage for the Judge.fxml page
			Stage newStage = new Stage();
			newStage.setScene(new Scene(judgePage));
			newStage.setTitle("Judge Panel");
			newStage.show();

			// Close the current window
			Stage currentStage = (Stage) back.getScene().getWindow();
			currentStage.close();

		} catch (IOException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to return to Judge panel.");
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
