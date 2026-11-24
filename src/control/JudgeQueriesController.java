package control;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import enums.Gender;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import model.Case;
import model.FamilyCase;
import model.Judge;

public class JudgeQueriesController implements Initializable {

	@FXML
	private MediaView queryMedia;

	@FXML
	private ComboBox<Gender> genderCombobox;

	@FXML
	private Button findByGender;

	private Judge loggedInJudge; // ✅ Store the judge object

	@FXML
	private TableView<Case> casesByGender;

	@FXML
	private TableColumn<Case, String> code, openDate, status, specialization, lawyer, verdict, accused;

	@FXML
	private Label numberOfCasesByGender;

	private MediaPlayer mediaPlayer;
	private Court court;

	@FXML
	private TextArea CrimeScene; // TextArea for crime scene input

	@FXML
	private ImageView searchCrimeTools; // Search button (ImageView)

	@FXML
	private ListView<String> crimeToolsListView; // ListView to display tools
	@FXML
	private TableView<FamilyCase> casesOfBothSidesWitnesses;

	@FXML
	private TableColumn<FamilyCase, String> code1, openDate1, status1, specialization1, lawyer1, verdict1, accused1;

	@FXML
	private ImageView backImage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		court = Court.getInstance();
		loadVideo();

		genderCombobox.setItems(FXCollections.observableArrayList(Gender.values()));

		// ✅ Bind Table Columns
		code.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCode()));
		openDate.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getOpenedDate().toString()));
		status.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCaseStatus().name()));
		specialization
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCaseType().name()));

		// ✅ Display Lawyer ID instead of Name
		lawyer.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getLawyer().getId()) // Lawyer
																												// ID
				));

		// ✅ Display Accused ID instead of Name
		accused.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getAccused().getId()) // Accused
																												// ID
				));

		// ✅ Handle `null` verdict case
		verdict.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().getVerdict() != null)
				? String.valueOf(cellData.getValue().getVerdict().getVerdictID())
				: "N/A"));

		// ✅ Set Button Action
		findByGender.setOnAction(e -> findCasesByGender());
		searchCrimeTools.setOnMouseClicked(event -> searchCrimeTools());

		// ✅ Initialize Table Columns for Family Cases (Witnesses from Both Sides)
		code1.setCellValueFactory(new PropertyValueFactory<>("code"));

		openDate1.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getOpenedDate().toString()));

		status1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCaseStatus().name()));

		specialization1
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCaseType().name()));

		lawyer1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLawyer().getId() + "")); // Display
																															// ID

		verdict1.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().getVerdict() != null)
				? String.valueOf(cellData.getValue().getVerdict().getVerdictID())
				: "N/A"));

		accused1.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getAccused().getId() + "")); // Display ID

		// ✅ Load data when switching to Tab 3
		Platform.runLater(() -> loadFamilyCasesWithBothSideWitnesses());
		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Judge.fxml", "Judge Page");
		});

	}

	/** ✅ Load and Play Video */
	/** ✅ Load and Play Video in a Loop */
	private void loadVideo() {
		try {
			URL videoUrl = getClass().getResource("/resources/query.mp4");

			if (videoUrl == null) {
				System.out.println("❌ Video file not found! Ensure it's inside src/main/resources/resources/");
				return;
			}

			Media media = new Media(videoUrl.toExternalForm());
			mediaPlayer = new MediaPlayer(media);
			queryMedia.setMediaPlayer(mediaPlayer);

			// ✅ Loop the video indefinitely
			mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

			Platform.runLater(() -> {
				if (queryMedia.getScene() != null) {
					queryMedia.fitWidthProperty().bind(queryMedia.getScene().widthProperty());
					queryMedia.fitHeightProperty().bind(queryMedia.getScene().heightProperty());
					mediaPlayer.play();
					System.out.println("✅ Video is now playing in an infinite loop.");
				}
			});

		} catch (Exception e) {
			System.out.println("❌ Error loading media file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/** ✅ Search Unique Crime Tools by Crime Scene */
	private void searchCrimeTools() {
		// ✅ Get the crime scene input
		String crimeSceneInput = CrimeScene.getText().trim();

		// ✅ Validate input
		if (crimeSceneInput.isEmpty()) {
			System.out.println("⚠️ Please enter a crime scene.");
			crimeToolsListView.setItems(FXCollections.observableArrayList("⚠️ Please enter a crime scene."));
			return;
		}

		// ✅ Call the method from Court class
		Set<String> crimeTools = court.findUniqueCrimeToolsByCrimeScene(crimeSceneInput);

		// ✅ Display results in the ListView
		if (crimeTools.isEmpty()) {
			crimeToolsListView.setItems(FXCollections.observableArrayList("❌ No crime tools found."));
			System.out.println("❌ No crime tools found for: " + crimeSceneInput);
		} else {
			crimeToolsListView.setItems(FXCollections.observableArrayList(crimeTools));
			System.out.println("✅ Found Crime Tools: " + crimeTools);
		}
	}

	/**
	 * ✅ Find and Display Cases With More Than Three Testimonies From Same Gender
	 */
	private void findCasesByGender() {
		// Get selected gender
		Gender selectedGender = genderCombobox.getValue();

		if (selectedGender == null) {
			numberOfCasesByGender.setText("⚠️ Please select a gender.");
			return;
		}

		// Get filtered cases from Court class
		List<Case> resultCases = court.findCasesWithMoreThanThreeTestimoniesFromSameGender(selectedGender);

		if (resultCases.isEmpty()) {
			numberOfCasesByGender.setText("No cases found.");
			casesByGender.getItems().clear(); // Clear TableView
			return;
		}

		// ✅ Update TableView with observable list
		casesByGender.setItems(FXCollections.observableArrayList(resultCases));

		// ✅ Update Label
		numberOfCasesByGender.setText("Found " + resultCases.size() + " cases.");
	}

	/** ✅ Load Family Cases With Witnesses From Both Sides */
	private void loadFamilyCasesWithBothSideWitnesses() {
		// ✅ Retrieve cases from Court class
		List<FamilyCase> resultCases = court.findFamilyCasesWithWitnessesFromBothSides();

		// ✅ Display results in the TableView
		casesOfBothSidesWitnesses.setItems(FXCollections.observableArrayList(resultCases));

		System.out.println("✅ Found Family Cases with Witnesses from Both Sides: " + resultCases.size());
	}

	/** ✅ Set Logged-In Judge */
	public void setJudge(Judge judge) {
		this.loggedInJudge = judge;
		System.out.println("✅ Judge set: " + judge.getId()); // Debugging
	}
}
