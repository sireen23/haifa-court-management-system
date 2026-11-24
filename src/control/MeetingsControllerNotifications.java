package control;

import java.io.IOException;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import model.Meeting;

public class MeetingsControllerNotifications {
	@FXML
	private TableView<Meeting> meetingsTable; // ✅ TableView for Meetings

	@FXML
	private TableColumn<Meeting, Integer> meetingIDColumn;

	@FXML
	private TableColumn<Meeting, String> meetingDateColumn;

	@FXML
	private TableColumn<Meeting, String> meetingTimeColumn;

	@FXML
	private TableColumn<Meeting, String> courtroomColumn;

	@FXML
	private TableColumn<Meeting, String> caseIDColumn;

	@FXML
	private ListView<Pane> meetingsListView; // ListView to hold meeting cards

	// Method to load meetings dynamically
	public void loadMeetings(List<Meeting> meetings) {
		meetingsListView.getItems().clear(); // Clear previous items

		for (Meeting meeting : meetings) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MeetingCard.fxml"));
				Node card = loader.load();

				// Get the controller for the MeetingCard
				MeetingCardController cardController = loader.getController();
				cardController.setMeetingDetails(meeting); // Pass the meeting data

				meetingsListView.getItems().add((Pane) card);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
