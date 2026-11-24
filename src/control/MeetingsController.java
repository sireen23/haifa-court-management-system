package control;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import model.Meeting;

public class MeetingsController {

	@FXML
	private TableView<Meeting> meetingsTable;

	@FXML
	private TableColumn<Meeting, Integer> meetingID;

	@FXML
	private TableColumn<Meeting, String> date;

	@FXML
	private TableColumn<Meeting, String> hour;

	@FXML
	private TableColumn<Meeting, Integer> courtroom;

	@FXML
	private TableColumn<Meeting, String> caseID;

	@FXML
	private ImageView backImage;

	@FXML
	public void initialize() {
		System.out.println("✅ MeetingsController Initialized");

		// ✅ Ensure TableView is properly set up
		setupTableColumns();

		// ✅ Load meetings data AFTER UI is ready
		Platform.runLater(this::loadMeetingsData);

		// ✅ Set back button click event
		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void setupTableColumns() {
		// ✅ Match table columns to Meeting class properties
		meetingID.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getMeetingID()));

		// ✅ Format Meeting Date as "DD/MM/YYYY"
		date.setCellValueFactory(cellData -> {
			Date meetingDate = cellData.getValue().getMeetingDate();
			return new SimpleStringProperty((meetingDate != null) ? formatDate(meetingDate) : "N/A");
		});

		// ✅ Format Meeting Hour
		hour.setCellValueFactory(cellData -> {
			Time meetingHour = cellData.getValue().getHour();
			return new SimpleStringProperty((meetingHour != null) ? meetingHour.toString() : "N/A");
		});

		// ✅ Display Courtroom Number
		courtroom.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
				(cellData.getValue().getCourtroom() != null) ? cellData.getValue().getCourtroom().getCourtroomNumber()
						: -1));

		// ✅ Display Case ID (with null check)
		caseID.setCellValueFactory(cellData -> new SimpleStringProperty(
				(cellData.getValue().getCasee() != null) ? cellData.getValue().getCasee().getCode() : "N/A"));
	}

	private String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(date);
	}

	private void loadMeetingsData() {
		Court court = Court.getInstance(); // Assuming Court is a Singleton

		// ✅ Retrieve all meetings from Court
		List<Meeting> meetingList = court.getAllMeetings().values().stream().collect(Collectors.toList());

		// ✅ Convert List to ObservableList
		ObservableList<Meeting> observableMeetings = FXCollections.observableArrayList(meetingList);

		if (observableMeetings.isEmpty()) {
			System.out.println("❌ No meetings found!");
		} else {
			System.out.println("✅ Loaded " + observableMeetings.size() + " meetings into the table.");
		}

		// ✅ Set data and refresh table
		meetingsTable.setItems(observableMeetings);
		meetingsTable.refresh(); // 🔥 Ensure table updates
	}
}
