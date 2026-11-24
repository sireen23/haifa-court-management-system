package control;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Meeting;

public class MeetingCardController {

	@FXML
	private ImageView meetingIcon;

	@FXML
	private Label meetingTitle, meetingDetails;

	// ✅ Set meeting details dynamically
	public void setMeetingDetails(Meeting meeting) {
		meetingTitle.setText("Meeting on " + meeting.getMeetingDate() + " at " + meeting.getHour());
		meetingDetails.setText("Courtroom: " + meeting.getCourtroom().getCourtroomNumber() + " | Case: "
				+ meeting.getCasee().getCode());

		// ✅ Set a meeting icon (optional)
		File iconFile = new File("src/assets/meeting_icon.png");
		if (iconFile.exists()) {
			meetingIcon.setImage(new Image(iconFile.toURI().toString()));
		}
	}
}
