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
import model.FinancialCase;

public class FinancialCasesController {

	@FXML
	private TableView<FinancialCase> FinancialCasesTable;

	@FXML
	private TableColumn<FinancialCase, String> code;

	@FXML
	private TableColumn<FinancialCase, Integer> accusedID;

	@FXML
	private TableColumn<FinancialCase, String> openDate; // ✅ Formatted date

	@FXML
	private TableColumn<FinancialCase, String> status;

	@FXML
	private TableColumn<FinancialCase, String> specialization;

	@FXML
	private TableColumn<FinancialCase, Integer> lawyerID;

	@FXML
	private TableColumn<FinancialCase, Integer> verdictID;

	@FXML
	private TableColumn<FinancialCase, Double> lossAmount; // ✅ New Field

	@FXML
	private TableColumn<FinancialCase, String> damagedItem; // ✅ New Field

	@FXML
	private ImageView backImage;

	@FXML
	public void initialize() {
		System.out.println("✅ FinancialCasesController Initialized");

		// Ensure the TableView is properly set up
		setupTableColumns();

		// ✅ Load financial cases data into the table AFTER the UI is ready
		Platform.runLater(this::loadFinancialCasesData);

		// ✅ Set back button click event
		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void setupTableColumns() {
		// ✅ Match table columns to FinancialCase class properties
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

		// ✅ Get verdict ID
		verdictID.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
				(cellData.getValue().getVerdict() != null) ? cellData.getValue().getVerdict().getVerdictID() : -1));

		// ✅ New: Loss Amount (Double)
		lossAmount.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getLossesAmount()));

		// ✅ New: Damaged Item (String)
		damagedItem.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDamagedItem()));

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

	private void loadFinancialCasesData() {
		Court court = Court.getInstance(); // Assuming Court is a Singleton

		// ✅ Filter only FinancialCase instances from allCases
		List<FinancialCase> financialCasesList = new ArrayList<>();

		for (model.Case caseItem : court.getAllCases().values()) {
			if (caseItem instanceof FinancialCase) {
				financialCasesList.add((FinancialCase) caseItem);
			}
		}

		// ✅ Convert List to ObservableList
		ObservableList<FinancialCase> observableFinancialCases = FXCollections.observableArrayList(financialCasesList);

		if (observableFinancialCases.isEmpty()) {
			System.out.println("❌ No financial cases found!");
		} else {
			System.out.println("✅ Loaded " + observableFinancialCases.size() + " financial cases into the table.");
		}

		// ✅ Set data and refresh table
		FinancialCasesTable.setItems(observableFinancialCases);
		FinancialCasesTable.refresh(); // 🔥 Ensure table updates
	}

	private void goBack() {
		System.out.println("🔙 Going back to the previous page...");
		// Implement scene switching logic here
	}
}
