package control;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import model.CriminalCase;

public class CriminalCasesController {

	@FXML
	private TableView<CriminalCase> CriminalCasesTable;

	@FXML
	private TableColumn<CriminalCase, String> code;

	@FXML
	private TableColumn<CriminalCase, Integer> accusedID;

	@FXML
	private TableColumn<CriminalCase, String> openDate; // ✅ Formatted date

	@FXML
	private TableColumn<CriminalCase, String> status;

	@FXML
	private TableColumn<CriminalCase, String> specialization;

	@FXML
	private TableColumn<CriminalCase, Integer> lawyerID;

	@FXML
	private TableColumn<CriminalCase, Integer> verdictID;

	@FXML
	private TableColumn<CriminalCase, Integer> victimID; // ✅ New Field

	@FXML
	private TableColumn<CriminalCase, String> crimeScene; // ✅ New Field

	@FXML
	private TableColumn<CriminalCase, String> crimeTool; // ✅ New Field

	@FXML
	private ImageView backImage;

	@FXML
	public void initialize() {
		System.out.println("✅ CriminalCasesController Initialized");

		// Ensure the TableView is properly set up
		setupTableColumns();

		// Load criminal cases data into the table AFTER the UI is ready
		Platform.runLater(this::loadCriminalCasesData);

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void setupTableColumns() {
		// ✅ Match table columns to CriminalCase class properties
		code.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCode())); // ✅ Fix type
																										// mismatch

		// ✅ Get accused ID
		accusedID.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAccused().getId()));

		// ✅ Format Open Date as "DD/MM/YYYY"
		openDate.setCellValueFactory(cellData -> {
			Date date = cellData.getValue().getOpenedDate();
			return new SimpleStringProperty((date != null) ? formatDate(date) : "N/A");
		});

		// ✅ Get status as a string
		status.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getCaseStatus().toString()));

		// ✅ Get specialization as a string
		specialization.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getCaseType().toString()));

		// ✅ Get lawyer ID
		lawyerID.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getLawyer().getId()));

		// ✅ Get verdict ID with null check
		verdictID.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
				(cellData.getValue().getVerdict() != null) ? cellData.getValue().getVerdict().getVerdictID() : -1 // ✅
																													// Prevents
																													// NullPointerException
		));

		// ✅ New: Victim ID with null check
		victimID.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
				(cellData.getValue().getVictim() != null) ? cellData.getValue().getVictim().getId() : -1 // ✅ Avoids
																											// null
																											// crash
		));

		// ✅ New: Crime Scene
		crimeScene.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCrimeScene()));

		// ✅ New: Crime Tool
		crimeTool.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCrimeTool()));

		// ✅ Handle back button click
		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	// ✅ Helper method: Formats Date into "DD/MM/YYYY"
	private String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(date);
	}

	private void loadCriminalCasesData() {
		Court court = Court.getInstance(); // Assuming Court is a Singleton

		// ✅ Filter only CriminalCase instances from allCases
		List<CriminalCase> criminalCasesList = new ArrayList<>();

		for (model.Case caseItem : court.getAllCases().values()) {
			if (caseItem instanceof CriminalCase) {
				criminalCasesList.add((CriminalCase) caseItem);
			}
		}

		// ✅ Convert List to ObservableList
		ObservableList<CriminalCase> observableCriminalCases = FXCollections.observableArrayList(criminalCasesList);

		if (observableCriminalCases.isEmpty()) {
			System.out.println("❌ No criminal cases found!");
		} else {
			System.out.println("✅ Loaded " + observableCriminalCases.size() + " criminal cases into the table.");
		}

		// ✅ Set data and refresh table
		CriminalCasesTable.setItems(observableCriminalCases);
		CriminalCasesTable.refresh(); // 🔥 Ensure table updates
	}

	private void goBack() {
		System.out.println("🔙 Going back to the previous page...");
		// Implement scene switching logic here
	}
}
