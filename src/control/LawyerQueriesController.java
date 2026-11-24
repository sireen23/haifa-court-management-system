package control;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import model.FamilyCase;
import model.Lawyer;

public class LawyerQueriesController {

	@FXML
	private ComboBox<Integer> lawyersCombo; // Lawyer IDs
	@FXML
	private Button activateQuery1;
	@FXML
	private Label query1Result;
	@FXML
	private TableView<FamilyCase> casesOfBothSidesWitnesses;
	@FXML
	private TableColumn<FamilyCase, String> code1;
	@FXML
	private TableColumn<FamilyCase, String> openDate1;
	@FXML
	private TableColumn<FamilyCase, String> status1;
	@FXML
	private TableColumn<FamilyCase, String> specialization1;
	@FXML
	private TableColumn<FamilyCase, String> lawyer1;
	@FXML
	private TableColumn<FamilyCase, String> verdict1;
	@FXML
	private TableColumn<FamilyCase, String> accused1;

	private Court court;

	@FXML
	private ImageView backImage;

	@FXML
	public void initialize() {
		court = Court.getInstance();

		// ✅ Populate ComboBox with Lawyer IDs
		lawyersCombo.setItems(FXCollections.observableArrayList(court.getAllLawyers().keySet()));

		// ✅ Attach event listeners to activate queries
		activateQuery1.setOnAction(event -> activateQuery1());
		loadFamilyCasesWithBothSideWitnesses(); // Automatically loads query 2 results

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Lawyer.fxml", "Lawyer Page");
		});
	}

	private void activateQuery1() {
		Integer selectedLawyerID = lawyersCombo.getValue();
		if (selectedLawyerID == null) {
			query1Result.setText("⚠️ Please select a lawyer first.");
			return;
		}

		Lawyer lawyer = court.getAllLawyers().get(selectedLawyerID);
		if (lawyer == null) {
			query1Result.setText("❌ Lawyer not found.");
			return;
		}

		// ✅ Call the method from Court
		int difference = court.differenceBetweenTheLongestAndShortestCase(lawyer);

		// ✅ Display a meaningful result
		if (difference == 0) {
			query1Result.setText("No significant time difference between the longest and shortest cases.");
		} else {
			query1Result
					.setText("The time difference between the longest and shortest case is: " + difference + " days.");
		}
	}

	private void loadFamilyCasesWithBothSideWitnesses() {
		// ✅ Retrieve cases from Court method
		List<FamilyCase> cases = court.findFamilyCasesWithWitnessesFromBothSides();
		ObservableList<FamilyCase> observableCases = FXCollections.observableArrayList(cases);

		// ✅ Configure TableView Columns
		code1.setCellValueFactory(new PropertyValueFactory<>("code"));
		openDate1.setCellValueFactory(new PropertyValueFactory<>("openedDate"));
		status1.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getCaseStatus().toString()));
		specialization1.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getCaseType().toString()));
		lawyer1.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getLawyer().getFullName()));
		verdict1.setCellValueFactory(new PropertyValueFactory<>("verdict"));
		accused1.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getAccused().getFullName()));

		// ✅ Populate the TableView
		casesOfBothSidesWitnesses.setItems(observableCases);
	}
}
