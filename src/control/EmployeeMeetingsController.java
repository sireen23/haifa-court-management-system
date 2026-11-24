package control;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import model.Meeting;

public class EmployeeMeetingsController implements Initializable {

	private ZonedDateTime dateFocus;
	private ZonedDateTime today;
	private Court court;

	@FXML
	private Text year;

	@FXML
	private Text month;

	@FXML
	private FlowPane calendar;

	@FXML
	private ComboBox<String> monthCombo;

	@FXML
	private TextField yearInput;

	@FXML
	private Button goButton;

	@FXML
	private TextArea meetingDetails;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		court = Court.getInstance();
		System.out.println("🔥 EmployeeMeetingsController Initialized");

		dateFocus = ZonedDateTime.now();
		today = ZonedDateTime.now();
		drawCalendar(); // ✅ Draw the calendar initially

		// Populate months in the ComboBox
		monthCombo.setItems(FXCollections.observableArrayList("January", "February", "March", "April", "May", "June",
				"July", "August", "September", "October", "November", "December"));

		// Set action for GO button
		goButton.setOnAction(event -> applyDateFilter());
	}

	private void applyDateFilter() {
		String selectedMonth = monthCombo.getValue();
		String selectedYear = yearInput.getText();

		if (selectedMonth == null || selectedYear.isEmpty()) {
			showAlert(Alert.AlertType.ERROR, "Error", "Please select both month and year!");
			return;
		}

		try {
			int year = Integer.parseInt(selectedYear);
			int monthIndex = monthCombo.getSelectionModel().getSelectedIndex() + 1;

			dateFocus = ZonedDateTime.of(year, monthIndex, 1, 0, 0, 0, 0, dateFocus.getZone());
			refreshCalendar();

		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Invalid Year", "Please enter a valid year!");
		}
	}

	@FXML
	void backOneMonth() {
		dateFocus = dateFocus.minusMonths(1);
		refreshCalendar();
	}

	@FXML
	void forwardOneMonth(ActionEvent event) {
		dateFocus = dateFocus.plusMonths(1);
		refreshCalendar();
	}

	private void refreshCalendar() {
		System.out.println("🔄 Refreshing calendar...");
		calendar.getChildren().clear();
		drawCalendar();
	}

	private void drawCalendar() {
		System.out.println("🔄 Drawing calendar for: " + dateFocus.getMonth() + " " + dateFocus.getYear());

		// Clear calendar before redrawing
		calendar.getChildren().clear();

		year.setText(String.valueOf(dateFocus.getYear()));
		month.setText(String.valueOf(dateFocus.getMonth()));

		double calendarWidth = calendar.getPrefWidth();
		double calendarHeight = calendar.getPrefHeight();
		double strokeWidth = 1;
		double spacingH = calendar.getHgap();
		double spacingV = calendar.getVgap();

		// ✅ Fetch ALL meetings from Court
		Map<Integer, List<Meeting>> meetingMap = getMeetingsForMonth(dateFocus);

		int monthMaxDate = dateFocus.getMonth().maxLength();
		if (dateFocus.getYear() % 4 != 0 && monthMaxDate == 29) {
			monthMaxDate = 28;
		}
		int dateOffset = ZonedDateTime
				.of(dateFocus.getYear(), dateFocus.getMonthValue(), 1, 0, 0, 0, 0, dateFocus.getZone()).getDayOfWeek()
				.getValue();

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				StackPane stackPane = new StackPane();

				Rectangle rectangle = new Rectangle();
				rectangle.setFill(Color.WHITE);
				rectangle.setStrokeWidth(1);
				rectangle.setArcWidth(10);
				rectangle.setArcHeight(10);

				double rectangleWidth = (calendarWidth / 7) - strokeWidth - spacingH;
				rectangle.setWidth(rectangleWidth);
				double rectangleHeight = (calendarHeight / 6) - strokeWidth - spacingV;
				rectangle.setHeight(rectangleHeight);
				stackPane.getChildren().add(rectangle);
				rectangle.setStroke(Color.BLACK);

				int calculatedDate = (j + 1) + (7 * i);
				if (calculatedDate > dateOffset) {
					int currentDate = calculatedDate - dateOffset;
					if (currentDate <= monthMaxDate) {
						// ✅ Display Date
						Text dateText = new Text(String.valueOf(currentDate));
						dateText.setFill(Color.web("#A6A6A6"));
						dateText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
						stackPane.getChildren().add(dateText);

						List<Meeting> meetings = meetingMap.get(currentDate);
						if (meetings != null) {
							highlightMeetingDate(meetings, rectangle, stackPane);
						}
					}
					if (today.getYear() == dateFocus.getYear() && today.getMonth() == dateFocus.getMonth()
							&& today.getDayOfMonth() == currentDate) {
						rectangle.setStroke(Color.BLUE);
					}
				}

				calendar.getChildren().add(stackPane);
			}
		}
	}

	private void highlightMeetingDate(List<Meeting> meetings, Rectangle rectangle, StackPane stackPane) {
		rectangle.setFill(Color.web("#75B484"));

		for (Meeting meeting : meetings) {
			Text meetingHour = new Text(meeting.getHour().toString());
			meetingHour.setFill(Color.WHITE);
			stackPane.getChildren().add(meetingHour);
		}

		stackPane.setOnMouseClicked(event -> {
			StringBuilder details = new StringBuilder();
			details.append("📅 Meetings on " + dateFocus.getMonth() + " "
					+ ((Text) stackPane.getChildren().get(1)).getText() + ":\n\n");

			for (Meeting meeting : meetings) {
				details.append("🕒 Time: ").append(meeting.getHour()).append("\n");
				details.append("📜 Case: ").append(meeting.getCasee().getCode()).append("\n");
				details.append(
						(meeting.getCourtroom() != null) ? "⚖ Courtroom: " + meeting.getCourtroom().getCourtroomNumber()
								: "⚠ No Courtroom Assigned")
						.append("\n\n");
			}

			meetingDetails.setText(details.toString());
		});
	}

	private Map<Integer, List<Meeting>> getMeetingsForMonth(ZonedDateTime dateFocus) {
		List<Meeting> allMeetings = new ArrayList<>(court.getAllMeetings().values());

		Map<Integer, List<Meeting>> meetingMap = new HashMap<>();
		for (Meeting meeting : allMeetings) {
			// Convert java.sql.Date -> java.util.Date
			java.util.Date utilDate = new java.util.Date(meeting.getMeetingDate().getTime());

			// Convert to LocalDate
			LocalDate localDate = utilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

			if (localDate.getYear() == dateFocus.getYear() && localDate.getMonth() == dateFocus.getMonth()) {
				int day = localDate.getDayOfMonth();
				meetingMap.computeIfAbsent(day, k -> new ArrayList<>()).add(meeting);
			}
		}
		return meetingMap;
	}

	private void showAlert(Alert.AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
