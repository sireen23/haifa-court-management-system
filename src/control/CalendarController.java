package control;

import java.net.URL;
import java.sql.Time;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import enums.Status;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import model.Case;
import model.Courtroom;
import model.Lawyer;
import model.Meeting;

public class CalendarController implements Initializable {

	private ZonedDateTime dateFocus;
	private ZonedDateTime today;
	private Lawyer loggedInLawyer;

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
	@FXML
	private DatePicker meetingDate;
	@FXML
	private Spinner<Integer> hour, minutes;
	@FXML
	private ComboBox<Integer> courtrooms; // Courtroom IDs
	@FXML
	private ComboBox<String> cases; // Case codes
	@FXML
	private ImageView addMeetingButton, resetButton, backImage;

	private Court court;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		court = Court.getInstance();
		System.out.println("🔥 initialize() called");

		dateFocus = ZonedDateTime.now();
		today = ZonedDateTime.now();
		drawCalendar(); // ✅ Draws the initial calendar

		// Populate months in the ComboBox
		monthCombo.setItems(FXCollections.observableArrayList("January", "February", "March", "April", "May", "June",
				"July", "August", "September", "October", "November", "December"));

		setupTimeSpinners(); // ✅ Initializes the hour & minute spinners
		loadCourtrooms(); // ✅ Loads available courtrooms
		loadLawyerCases(); // ✅ Loads the lawyer's open cases into the ComboBox

		// Set action for GO button
		goButton.setOnAction(event -> applyDateFilter());
		addMeetingButton.setOnMouseClicked(event -> addMeeting());
		resetButton.setOnMouseClicked(event -> resetForm());

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Lawyer.fxml", "Admin Page");
		});
	}

	private void setupTimeSpinners() {
		SpinnerValueFactory<Integer> hourFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12);
		SpinnerValueFactory<Integer> minuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 5);
		hour.setValueFactory(hourFactory);
		minutes.setValueFactory(minuteFactory);
	}

	private void loadCourtrooms() {
		List<Integer> courtroomIDs = Court.getInstance().getAllCourtrooms().values() // ✅ Convert Map<Integer,
																						// Courtroom> to
																						// Collection<Courtroom>
				.stream().map(Courtroom::getCourtroomNumber).collect(Collectors.toList());

		courtrooms.setItems(FXCollections.observableArrayList(courtroomIDs));
	}

	private void loadLawyerCases() {
		if (loggedInLawyer == null) {
			System.err.println("❌ ERROR: loggedInLawyer is NULL in loadLawyerCases()");
			return;
		}

		System.out.println("✅ Lawyer Found: " + loggedInLawyer.getFullName());

		// Get all cases handled by the lawyer
		Set<Case> allCases = loggedInLawyer.getCasesHandled();
		if (allCases == null || allCases.isEmpty()) {
			System.err.println("⚠️ WARNING: Lawyer has NO cases!");
			return;
		}

		// Filter only OPEN cases
		List<String> openCases = allCases.stream().filter(c -> c.getCaseStatus() == Status.inProcess).map(Case::getCode)
				.collect(Collectors.toList());

		if (openCases.isEmpty()) {
			System.err.println("⚠️ WARNING: No open cases found!");
		} else {
			System.out.println("📌 Open Cases: " + openCases);
		}

		cases.setItems(FXCollections.observableArrayList(openCases));
	}

	@FXML
	private void addMeeting() {
		// ✅ Validate Inputs
		if (meetingDate.getValue() == null || courtrooms.getValue() == null || cases.getValue() == null) {
			showAlert(Alert.AlertType.ERROR, "Missing Fields", "Please fill all the fields.");
			return;
		}

		Date date = java.sql.Date.valueOf(meetingDate.getValue());
		Time time = Time.valueOf(hour.getValue() + ":" + minutes.getValue() + ":00");

		// ✅ Get Selected Case
		Case selectedCase = loggedInLawyer.getCasesHandled().stream().filter(c -> c.getCode().equals(cases.getValue()))
				.findFirst().orElse(null);

		if (selectedCase == null) {
			showAlert(Alert.AlertType.ERROR, "Case Error", "Invalid case selected.");
			return;
		}

		// ✅ Get Selected Courtroom
		Courtroom selectedCourtroom = Court.getInstance().getAllCourtrooms().get(courtrooms.getValue());

		// ✅ Create Meeting Object
		Meeting newMeeting = new Meeting(date, time, selectedCourtroom, selectedCase);

		// 🔍 DEBUGGING LOGS
		System.out.println("🔄 Trying to add meeting: " + newMeeting);

		boolean added = court.addMeeting(newMeeting);

		if (added) {
			showAlert(Alert.AlertType.INFORMATION, "Success", "Meeting added successfully.");
			System.out.println("✅ Meeting added successfully!");
		} else {
			showAlert(Alert.AlertType.ERROR, "Failed", "Could not add the meeting!");
			System.out.println("❌ Failed to add meeting!");
		}

		refreshCalendar(); // 🔄 REFRESH THE CALENDAR
		resetForm(); // ✅ Reset form fields
	}

	private void refreshCalendar() {
		System.out.println("🔄 Refreshing calendar after meeting addition...");
		calendar.getChildren().clear(); // ✅ Clear existing calendar
		drawCalendar(); // ✅ Redraw with the new meeting
	}

	@FXML
	private void resetForm() {
		meetingDate.setValue(null);
		hour.getValueFactory().setValue(12);
		minutes.getValueFactory().setValue(0);
		courtrooms.getSelectionModel().clearSelection();
		cases.getSelectionModel().clearSelection();
	}

	public void setLawyer(Lawyer lawyer) {
		if (lawyer == null) {
			System.err.println("❌ ERROR: Trying to set a NULL lawyer in setLawyer()!");
			return;
		}

		this.loggedInLawyer = lawyer;
		System.out.println("✅ Lawyer Set in Calendar: " + lawyer.getFullName());

		// ✅ Load Lawyer's Cases Immediately
		loadLawyerCases();

		// ✅ Check if Calendar Needs to be Drawn or Refreshed
		if (calendar.getChildren().isEmpty()) {
			System.out.println("🔄 First time drawing calendar...");
			drawCalendar();
		} else {
			System.out.println("🔄 Refreshing calendar with meetings...");
			calendar.getChildren().clear(); // Clear calendar before redrawing
			drawCalendar();
		}
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
			int monthIndex = monthCombo.getSelectionModel().getSelectedIndex() + 1; // Convert to 1-based index

			dateFocus = ZonedDateTime.of(year, monthIndex, 1, 0, 0, 0, 0, dateFocus.getZone());
			calendar.getChildren().clear(); // Clear previous month
			drawCalendar(); // Refresh with new date

		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Invalid Year", "Please enter a valid year!");
		}
	}

	@FXML
	void backOneMonth(ActionEvent event) {
		dateFocus = dateFocus.minusMonths(1);
		calendar.getChildren().clear();
		drawCalendar();
	}

	@FXML
	void forwardOneMonth(ActionEvent event) {
		dateFocus = dateFocus.plusMonths(1);
		calendar.getChildren().clear();
		drawCalendar();
	}

	private void drawCalendar() {
		System.out.println("🔄 Drawing calendar for: " + dateFocus.getMonth() + " " + dateFocus.getYear());

		// ✅ Clear the calendar before redrawing
		calendar.getChildren().clear();

		year.setText(String.valueOf(dateFocus.getYear()));
		month.setText(String.valueOf(dateFocus.getMonth()));

		double calendarWidth = calendar.getPrefWidth();
		double calendarHeight = calendar.getPrefHeight();
		double strokeWidth = 1;
		double spacingH = calendar.getHgap();
		double spacingV = calendar.getVgap();

		// ✅ Fetch Real Meetings Instead of Fake Data
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
				rectangle.setFill(Color.WHITE); // ✅ Default white background
				rectangle.setStrokeWidth(1); // ✅ Remove black stroke
				rectangle.setArcWidth(10); // ✅ Rounded corners (horizontal)
				rectangle.setArcHeight(10); // ✅ Rounded corners (vertical)

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
						// ✅ Date Number Styling
						Text dateText = new Text(String.valueOf(currentDate));
						dateText.setFill(Color.web("#A6A6A6")); // ✅ Grey text
						dateText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
						double textTranslationY = -(rectangleHeight / 2) * 0.75;
						dateText.setTranslateY(textTranslationY);
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

				// ✅ Improved Hover Effect (Fix disappearing color)
				stackPane.setOnMouseEntered(event -> {
					if (!rectangle.getFill().equals(Color.web("#75B484"))) { // Keep meeting days green
						rectangle.setFill(Color.web("#E3F2FD")); // Light blue hover
					}
				});
				stackPane.setOnMouseExited(event -> {
					if (!rectangle.getFill().equals(Color.web("#75B484"))) { // Keep meeting days green
						rectangle.setFill(Color.WHITE); // Reset to white
					}
				});

				calendar.getChildren().add(stackPane);
			}
		}
	}

	// ✅ Highlight Meeting Dates
	private void highlightMeetingDate(List<Meeting> meetings, Rectangle rectangle, StackPane stackPane) {
		rectangle.setFill(Color.web("#75B484")); // ✅ Highlight Meeting Dates (Green)

		for (Meeting meeting : meetings) {
			// ✅ Show only the meeting hour inside the cell
			Text meetingHour = new Text(meeting.getHour().toString());
			meetingHour.setFill(Color.WHITE); // Make text visible
			stackPane.getChildren().add(meetingHour);
		}

		// ✅ Click Event to Show Meeting Details in the TextArea
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

			meetingDetails.setText(details.toString()); // ✅ Update TextArea
		});
	}

	// ✅ Fetch Real Meeting Data
	private Map<Integer, List<Meeting>> getMeetingsForMonth(ZonedDateTime dateFocus) {
		if (loggedInLawyer == null) {
			return new HashMap<>();
		}

		List<Meeting> allMeetings = loggedInLawyer.getCasesHandled().stream().flatMap(c -> c.getMeetingsList().stream())
				.collect(Collectors.toList());

		Map<Integer, List<Meeting>> meetingMap = new HashMap<>();

		for (Meeting meeting : allMeetings) {
			java.util.Date utilDate = new java.util.Date(meeting.getMeetingDate().getTime()); // ✅ Convert SQL Date to
																								// Util Date

			LocalDate localDate = utilDate.toInstant().atZone(dateFocus.getZone()).toLocalDate();

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
