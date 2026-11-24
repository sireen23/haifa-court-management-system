package control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Case;
import model.CriminalCase;
import model.FamilyCase;
import model.FinancialCase;
import model.Judge;
import model.Verdict;

public class VerdictPublicController implements Initializable {

	@FXML
	private TableView<Verdict> verdictsTable;
	@FXML
	private TableColumn<Verdict, String> verdictIDColumn;
	@FXML
	private TableColumn<Verdict, String> summaryColumn;
	@FXML
	private TableColumn<Verdict, String> dateColumn;
	@FXML
	private TableColumn<Verdict, String> judgeIDColumn;
	@FXML
	private TableColumn<Verdict, String> judgeNameColumn;
	@FXML
	private TableColumn<Verdict, String> caseIDColumn;

	@FXML
	private CheckBox allVerdicts, criminal, family, financial;
	@FXML
	private DatePicker datePicker;
	@FXML
	private TextField keywordSummar, judgeNameFilter;
	@FXML
	private ComboBox<String> casesIDFilter;
	@FXML
	private Button filterButton, exportButton, detailsButton;
	@FXML
	private TextArea detailsTextArea;

	private Court court;
	private ObservableList<Verdict> verdictList;

	@FXML
	private ImageView backImage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		court = Court.getInstance();
		setupTable();
		loadVerdicts();
		loadCaseIDs();

		// ✅ Set event listeners for buttons
		filterButton.setOnAction(event -> applyFilters());
		detailsButton.setOnAction(event -> showVerdictDetails());
		exportButton.setOnAction(event -> exportSelectedVerdictToPDF());

		// ✅ Ensure "All Verdicts" selection clears other filters
		allVerdicts.setOnAction(event -> {
			if (allVerdicts.isSelected()) {
				criminal.setSelected(false);
				family.setSelected(false);
				financial.setSelected(false);
				datePicker.setValue(null);
				keywordSummar.clear();
				judgeNameFilter.clear();
				casesIDFilter.setValue(null);
				loadVerdicts(); // Reset table to display all verdicts
			}
		});
		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/main.fxml", "Main Page");
		});
	}

	private void setupTable() {
		verdictIDColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getVerdictID())));

		summaryColumn
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVerdictSummary()));

		// ✅ Format Date as "dd/MM/yyyy"
		dateColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(formatDate(cellData.getValue().getIssusedDate())));

		judgeIDColumn.setCellValueFactory(cellData -> {
			Judge judge = cellData.getValue().getJudge();
			return new SimpleStringProperty(judge != null ? String.valueOf(judge.getId()) : "N/A");
		});

		judgeNameColumn.setCellValueFactory(cellData -> {
			Judge judge = cellData.getValue().getJudge();
			return new SimpleStringProperty(judge != null ? judge.getFullName() : "Unknown Judge");
		});

		caseIDColumn.setCellValueFactory(cellData -> {
			Case casee = cellData.getValue().getCasee();
			return new SimpleStringProperty(casee != null ? casee.getCode() : "N/A");
		});
	}

	private void loadVerdicts() {
		verdictList = FXCollections.observableArrayList(court.getAllVerdicts().values());
		verdictsTable.setItems(verdictList);
	}

	private void loadCaseIDs() {
		List<String> caseIDs = court.getAllCases().values().stream().map(Case::getCode).collect(Collectors.toList());

		caseIDs.add(0, ""); // ✅ Add empty field at the top of the list
		casesIDFilter.setItems(FXCollections.observableArrayList(caseIDs));
		casesIDFilter.setValue(""); // ✅ Default to empty
	}

	// ✅ Improved Filter Functionality
	@FXML
	private void applyFilters() {
		ObservableList<Verdict> filteredList = FXCollections.observableArrayList(court.getAllVerdicts().values());

		// ✅ If "All Verdicts" is selected, display all verdicts & ignore other filters
		if (allVerdicts.isSelected()) {
			verdictsTable.setItems(filteredList);
			return;
		}

		Predicate<Verdict> predicate = v -> true;

		if (criminal.isSelected()) {
			predicate = predicate.and(v -> v.getCasee() instanceof CriminalCase);
		}
		if (family.isSelected()) {
			predicate = predicate.and(v -> v.getCasee() instanceof FamilyCase);
		}
		if (financial.isSelected()) {
			predicate = predicate.and(v -> v.getCasee() instanceof FinancialCase);
		}

		if (datePicker.getValue() != null) {
			predicate = predicate.and(v -> {
				LocalDate verdictDate = v.getIssusedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				return verdictDate.equals(datePicker.getValue());
			});
		}

		if (!keywordSummar.getText().isEmpty()) {
			predicate = predicate
					.and(v -> v.getVerdictSummary().toLowerCase().contains(keywordSummar.getText().toLowerCase()));
		}

		if (!judgeNameFilter.getText().isEmpty()) {
			predicate = predicate.and(
					v -> v.getJudge().getFullName().toLowerCase().contains(judgeNameFilter.getText().toLowerCase()));
		}

		// ✅ Check if Case ID filter is applied (ignore if empty)
		if (casesIDFilter.getValue() != null && !casesIDFilter.getValue().isEmpty()) {
			predicate = predicate.and(v -> v.getCasee().getCode().equals(casesIDFilter.getValue()));
		}

		// ✅ Apply final filtered list
		verdictsTable.setItems(filteredList.filtered(predicate));
	}

	@FXML
	private void showVerdictDetails() {
		// ✅ Ensure a verdict is selected
		Verdict selectedVerdict = verdictsTable.getSelectionModel().getSelectedItem();
		if (selectedVerdict == null) {
			showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a verdict to view details.");
			return;
		}

		// ✅ Build detailed verdict text
		StringBuilder details = new StringBuilder();
		details.append("Verdict ID: ").append(selectedVerdict.getVerdictID()).append("\n");
		details.append("Summary: ").append(selectedVerdict.getVerdictSummary()).append("\n");
		details.append("Issued Date: ").append(formatDate(selectedVerdict.getIssusedDate())).append("\n");
		details.append("Judge: ").append(selectedVerdict.getJudge().getFullName()).append("\n");
		details.append("Case ID: ").append(selectedVerdict.getCasee().getCode()).append("\n");

		if (selectedVerdict.getAppeal() != null) {
			details.append("Appeal ID: ").append(selectedVerdict.getAppeal().getAppealID()).append("\n");
		}

		// ✅ Set text area
		detailsTextArea.setText(details.toString());
	}

	// ✅ Utility: Format Date as dd/MM/yyyy
	private String formatDate(Date date) {
		if (date == null)
			return "N/A";
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(date);
	}

	public void exportSelectedVerdictToPDF() {
		// ✅ Check if a row is selected
		Verdict selectedVerdict = verdictsTable.getSelectionModel().getSelectedItem();
		if (selectedVerdict == null) {
			showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a verdict to export.");
			return;
		}

		// ✅ Ask user where to save the PDF
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
		fileChooser.setTitle("Save Verdict as PDF");
		fileChooser.setInitialFileName("Verdict_" + selectedVerdict.getVerdictID() + ".pdf");
		File file = fileChooser.showSaveDialog(new Stage());

		if (file == null)
			return; // User canceled

		try {
			// ✅ Create PDF Document
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(file));
			document.open();

			// ✅ Add Title
			document.add(new Paragraph("Verdict Details\n\n"));

			// ✅ Add Basic Verdict Info
			document.add(new Paragraph("Verdict ID: " + selectedVerdict.getVerdictID()));
			document.add(new Paragraph("Summary: " + selectedVerdict.getVerdictSummary()));

			// ✅ Format date properly (DD/MM/YYYY)
			LocalDate issuedDate = selectedVerdict.getIssusedDate().toInstant().atZone(ZoneId.systemDefault())
					.toLocalDate();
			document.add(new Paragraph("Issued Date: " + issuedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

			// ✅ Add Judge Details
			document.add(new Paragraph("\nJudge Details:"));
			document.add(new Paragraph("Judge Name: " + selectedVerdict.getJudge().getFullName()));
			document.add(new Paragraph("Judge ID: " + selectedVerdict.getJudge().getId()));

			// ✅ Add Case Details
			document.add(new Paragraph("\nCase Details:"));
			document.add(new Paragraph("Case ID: " + selectedVerdict.getCasee().getCode()));
			document.add(new Paragraph("Case Status: " + selectedVerdict.getCasee().getCaseStatus()));
			document.add(new Paragraph("Case Type: " + selectedVerdict.getCasee().getCaseType()));
			document.add(new Paragraph("Opened Date: " + selectedVerdict.getCasee().getOpenedDate()));

			// ✅ Check if Lawyer Exists
			if (selectedVerdict.getCasee().getLawyer() != null) {
				document.add(new Paragraph("Lawyer: " + selectedVerdict.getCasee().getLawyer().getFullName()));
			} else {
				document.add(new Paragraph("Lawyer: No lawyer assigned"));
			}

			// ✅ Include Appeal Information (if exists)
			if (selectedVerdict.getAppeal() != null) {
				document.add(new Paragraph("\nAppeal Details:"));
				document.add(new Paragraph("Appeal ID: " + selectedVerdict.getAppeal().getAppealID()));
				document.add(new Paragraph("Appeal Summary: " + selectedVerdict.getAppeal().getAppealSummary()));
			}

			// ✅ Close document
			document.close();

			showAlert(Alert.AlertType.INFORMATION, "Success", "Verdict exported successfully!");

		} catch (IOException | DocumentException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Could not save the file.");
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
