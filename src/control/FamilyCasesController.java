package control;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import model.Case;
import model.FamilyCase;

public class FamilyCasesController {

	@FXML
	private TableView<FamilyCase> familyCasesTable;

	@FXML
	private TableColumn<FamilyCase, String> code;

	@FXML
	private TableColumn<FamilyCase, Integer> accusedID;

	@FXML
	private TableColumn<FamilyCase, String> openDate; // ✅ String column for formatted date

	@FXML
	private TableColumn<FamilyCase, String> status;

	@FXML
	private TableColumn<FamilyCase, String> specialization;

	@FXML
	private TableColumn<FamilyCase, Integer> lawyerID;

	@FXML
	private TableColumn<FamilyCase, Integer> verdictID;

	@FXML
	private TableColumn<FamilyCase, Integer> victimID;

	@FXML
	private TableColumn<FamilyCase, String> relationType;

	@FXML
	private ImageView backImage;

	@FXML
	public void initialize() {
		System.out.println("✅ FamilyCasesController Initialized");

		// Ensure the TableView is properly set up
		setupTableColumns();

		// ✅ Load family cases data into the table AFTER the UI is ready
		Platform.runLater(this::loadFamilyCasesData);

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void setupTableColumns() {
		// ✅ Match table columns to FamilyCase class properties
		code.setCellValueFactory(cellData -> new SimpleObjectProperty<String>(cellData.getValue().getCode())); // ✅
																												// FIXED!
																												// Fixed!

		// ✅ Get accused ID from Accused object
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

		// ✅ Get verdict ID
		verdictID.setCellValueFactory(
				cellData -> new SimpleObjectProperty<>(cellData.getValue().getVerdict().getVerdictID()));

		// ✅ Get victim ID
		victimID.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getVictim().getId()));

		// ✅ Get relation type as a string
		relationType.setCellValueFactory(new PropertyValueFactory<>("relationType"));

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

	private void loadFamilyCasesData() {
		Court court = Court.getInstance(); // Assuming Court is a Singleton

		// ✅ Fix: Ensure List is correctly parameterized with <FamilyCase>
		ArrayList<FamilyCase> familyCasesList = new ArrayList<>();

		// ✅ Filter FamilyCase instances from allCases
		for (Case caseItem : court.getAllCases().values()) {
			if (caseItem instanceof FamilyCase) {
				familyCasesList.add((FamilyCase) caseItem);
			}
		}

		// ✅ Convert List to ObservableList
		ObservableList<FamilyCase> observableFamilyCases = FXCollections.observableArrayList(familyCasesList);

		if (observableFamilyCases.isEmpty()) {
			System.out.println("❌ No family cases found!");
		} else {
			System.out.println("✅ Loaded " + observableFamilyCases.size() + " family cases into the table.");
		}

		// ✅ Set data and refresh table
		familyCasesTable.setItems(observableFamilyCases);
		familyCasesTable.refresh(); // 🔥 Ensure table updates
	}

	private void goBack() {
		System.out.println("🔙 Going back to the previous page...");
		// Implement scene switching logic here
	}
}
