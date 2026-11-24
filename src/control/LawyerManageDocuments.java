package control;

import java.util.Date;
import java.util.List;
import java.util.Set;

import enums.Status;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import model.Case;
import model.Document;
import model.Lawyer;

public class LawyerManageDocuments {
	private MediaPlayer mediaPlayer;
	private Lawyer loggedInLawyer;

	@FXML
	private MediaView mediaView;
	@FXML
	private ComboBox<String> caseIDCombo, addCase;
	@FXML
	private TableView<Document> documentsTable;
	@FXML
	private TableColumn<Document, Integer> colunm1Code;
	@FXML
	private TableColumn<Document, String> column2Title, column3Content, column4Date;
	@FXML
	private Button displayDocButton, submitButton, resetButton;
	@FXML
	private TextField addTitle;
	@FXML
	private TextArea addContent;
	@FXML
	private DatePicker addDate;

	@FXML
	private ImageView backImage;

	public void initialize() {
		// Load Background Video
		String videoPath = getClass().getResource("/resources/documents.mp4").toExternalForm();
		Media media = new Media(videoPath);
		mediaPlayer = new MediaPlayer(media);
		mediaView.setMediaPlayer(mediaPlayer);
		mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		mediaPlayer.setMute(true);
		mediaPlayer.play();

		// Setup table columns
		colunm1Code
				.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCode()).asObject());
		column2Title.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
		column3Content.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContent()));
		column4Date.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getIssusedDate().toString()));

		// Wrap text in content column
		column3Content.setCellFactory(tc -> {
			TableCell<Document, String> cell = new TableCell<>() {
				private final Text text = new Text();
				{
					text.wrappingWidthProperty().bind(column3Content.widthProperty().subtract(10));
					this.setGraphic(text);
					this.setPrefHeight(Control.USE_COMPUTED_SIZE);
				}

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						text.setText("");
					} else {
						text.setText(item);
					}
				}
			};
			return cell;
		});

		// Set button actions
		displayDocButton.setOnAction(event -> loadCaseDocuments());
		submitButton.setOnAction(event -> addNewDocument());
		resetButton.setOnAction(event -> resetForm());

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Lawyer.fxml", "Lawyer Page");
		});
	}

	public void setLawyer(Lawyer lawyer) {
		this.loggedInLawyer = lawyer;
		if (lawyer == null) {
			System.err.println("⚠️ ERROR: Lawyer is NULL in LawyerManageDocuments!");
			return;
		}
		System.out.println("✅ Lawyer Set in LawyerManageDocuments: " + loggedInLawyer.getFullName());

		// Debugging lawyer cases
		if (loggedInLawyer.getCasesHandled().isEmpty()) {
			System.out.println("⚠️ WARNING: This lawyer has NO cases.");
		} else {
			System.out.println("✅ Lawyer has " + loggedInLawyer.getCasesHandled().size() + " cases.");
		}

		loadLawyerCases();
	}

	private void loadLawyerCases() {
		if (loggedInLawyer == null) {
			System.err.println("⚠️ ERROR: Lawyer is NULL in loadLawyerCases!");
			return;
		}

		List<String> lawyerCases = loggedInLawyer.getCasesHandled().stream().map(Case::getCode).toList();

		List<String> openCases = loggedInLawyer.getCasesHandled().stream().filter(c -> {
			System.out.println("🔍 Checking case: " + c.getCode() + " | Status: " + c.getCaseStatus());
			return c.getCaseStatus() == Status.inProcess; // Only allow in-process cases
		}).map(Case::getCode).toList();

		// Debugging
		System.out.println("📌 All Lawyer Cases: " + lawyerCases);
		System.out.println("📌 Open Cases for Adding Docs: " + openCases);

		caseIDCombo.getItems().setAll(lawyerCases);
		addCase.getItems().setAll(openCases); // 🛠️ This is where cases should be added

		// Ensure something is selected
		if (!caseIDCombo.getItems().isEmpty())
			caseIDCombo.getSelectionModel().select(0);
		if (!addCase.getItems().isEmpty())
			addCase.getSelectionModel().select(0);
	}

	private void loadCaseDocuments() {
		String selectedCaseCode = caseIDCombo.getValue();
		if (selectedCaseCode == null) {
			showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a case.");
			return;
		}

		Case selectedCase = loggedInLawyer.getCasesHandled().stream().filter(c -> c.getCode().equals(selectedCaseCode))
				.findFirst().orElse(null);

		if (selectedCase != null) {
			Set<Document> documents = selectedCase.getDocumentsList();
			ObservableList<Document> documentList = FXCollections.observableArrayList(documents);
			documentsTable.setItems(documentList);
		} else {
			showAlert(Alert.AlertType.ERROR, "Case Error", "No documents found for this case.");
		}
	}

	private void addNewDocument() {
		String title = addTitle.getText();
		String content = addContent.getText();
		String selectedCaseCode = addCase.getValue();
		Date issueDate = java.sql.Date.valueOf(addDate.getValue());

		if (title.isEmpty() || content.isEmpty() || selectedCaseCode == null || issueDate == null) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "All fields must be filled out.");
			return;
		}

		Case selectedCase = loggedInLawyer.getCasesHandled().stream().filter(c -> c.getCode().equals(selectedCaseCode))
				.findFirst().orElse(null);

		if (selectedCase != null && selectedCase.getCaseStatus() == Status.inProcess) {
			Document newDoc = new Document(title, content, issueDate, selectedCase);
			selectedCase.getDocumentsList().add(newDoc);
			showAlert(Alert.AlertType.INFORMATION, "Success", "Document added successfully.");
			resetForm();
		} else {
			showAlert(Alert.AlertType.ERROR, "Case Error", "You can only add documents to open cases.");
		}
	}

	private void resetForm() {
		addTitle.clear();
		addContent.clear();
		addDate.setValue(null);
		addCase.getSelectionModel().clearSelection();
	}

	private void showAlert(Alert.AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
