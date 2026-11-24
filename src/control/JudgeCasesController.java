package control;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import enums.Specialization;
import enums.Status;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import model.Case;
import model.CriminalCase;
import model.FamilyCase;
import model.FinancialCase;
import model.Judge;

public class JudgeCasesController implements Initializable {

	@FXML
	private TableView<Case> casesTable;
	@FXML
	private TableColumn<Case, String> codeColumn, typeColumn, statusColumn, openDateColumn, accusedColumn, lawyerColumn;

	// Filtering Options
	@FXML
	private CheckBox allCases, criminal, family, financial, inProcess, finished, canceled;
	@FXML
	private TextField accusedName, lawyer;
	@FXML
	private DatePicker date;
	@FXML
	private Button filter, displayDetails, back, deleteCase;
	@FXML
	private ComboBox<Specialization> specialization;
	@FXML
	private TextArea caseDetailsTextArea;

	@FXML
	private ImageView search, calendar, backImage;

	private Court court;

	private Judge loggedInJudge;
	private List<Case> allCasesList = new ArrayList<>(); // Store all cases
	private ObservableList<Case> filteredCases = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		court = Court.getInstance();
		setupTableColumns();
		filter.setOnAction(e -> applyFilters()); // ✅ Filters only when button is clicked

		specialization.getItems().setAll(Specialization.values());

		// Handle Search Button (ImageView) Click
		search.setOnMouseClicked(event -> applySearch());
		deleteCase.setOnAction(event -> deleteSelectedCase());

		casesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection == null) {
				caseDetailsTextArea.clear();
			}
		});

		displayDetails.setOnAction(e -> displaySelectedCaseDetails());

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Judge.fxml", "Judge Page");
		});
		back.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Judge.fxml", "Judge Page");
		});

	}

	@FXML
	private void applySearch() {
		if (loggedInJudge == null)
			return; // Ensure judge is logged in

		// ✅ Get ONLY the cases assigned to THIS judge
		List<Case> judgeCasesList = new ArrayList<>(loggedInJudge.getCasesHandled());

		// ✅ Get search values
		String accusedFilter = accusedName.getText().trim().toLowerCase();
		Specialization selectedSpecialization = specialization.getValue();
		LocalDate selectedDate = date.getValue();

		// ✅ Apply Filtering
		List<Case> filteredCases = judgeCasesList.stream().filter(c -> {
			boolean matchesAccused = accusedFilter.isEmpty()
					|| c.getAccused().getFullName().toLowerCase().contains(accusedFilter);
			boolean matchesSpecialization = (selectedSpecialization == null)
					|| c.getCaseType() == selectedSpecialization;
			boolean matchesDate = (selectedDate == null)
					|| c.getOpenedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(selectedDate);

			return matchesAccused && matchesSpecialization && matchesDate;
		}).toList();

		// ✅ Update TableView with filtered results
		casesTable.getItems().setAll(filteredCases);
	}

	/** 🔹 Set the logged-in judge and load their cases */
	public void setJudge(Judge judge) {
		this.loggedInJudge = judge;
		loadJudgeCases();
	}

	/** 🔹 Setup TableView columns */
	private void setupTableColumns() {
		codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
		typeColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings
				.createStringBinding(() -> cellData.getValue().getCaseType().name() // Convert Specialization Enum to
																					// String
				));

		statusColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings
				.createStringBinding(() -> cellData.getValue().getCaseStatus().name()));
		openDateColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings
				.createStringBinding(() -> cellData.getValue().getOpenedDate().toString()));
		accusedColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings
				.createStringBinding(() -> cellData.getValue().getAccused().getFullName()));
		lawyerColumn.setCellValueFactory(cellData -> javafx.beans.binding.Bindings
				.createStringBinding(() -> cellData.getValue().getLawyer().getFullName()));
	}

	/** 🔹 Load all cases presided over by the judge */
	private void loadJudgeCases() {
		if (loggedInJudge == null)
			return;

		HashSet<Case> casesPresided = loggedInJudge.getCasesHandled();
		allCasesList.clear();
		allCasesList.addAll(casesPresided);

		// Initially, display all cases
		filteredCases.setAll(allCasesList);
		casesTable.setItems(filteredCases);
	}

	@FXML
	private void deleteSelectedCase() {
		// Step 1: Get the selected case
		Case selectedCase = casesTable.getSelectionModel().getSelectedItem();

		if (selectedCase == null) {
			showAlert(Alert.AlertType.WARNING, "No Case Selected", "Please select a case to delete.");
			return;
		}

		// Step 2: Confirm Deletion
		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Confirm Deletion");
		confirmation.setHeaderText("Are you sure you want to delete this case?");
		confirmation.setContentText("Case ID: " + selectedCase.getCode());

		Optional<ButtonType> result = confirmation.showAndWait();
		if (result.isEmpty() || result.get() != ButtonType.OK) {
			return;
		}

		// Debugging Output
		System.out.println("Trying to delete case: " + selectedCase.getCode());
		System.out.println("Exists in judge’s cases? " + loggedInJudge.getCasesHandled().contains(selectedCase));
		System.out.println("Exists in court cases? " + court.getAllCases().containsKey(selectedCase.getCode()));

		// Step 3: Remove case from judge's casesPresided (Using explicit loop to
		// prevent reference issues)
		boolean removedFromJudge = loggedInJudge.getCasesHandled()
				.removeIf(c -> c.getCode().equals(selectedCase.getCode()));

		// Step 4: Remove from Court’s allCases
		boolean removedFromCourt = court.getAllCases().remove(selectedCase.getCode()) != null;

		// Step 5: Also remove from Lawyer’s casesHandled if necessary
		if (selectedCase.getLawyer() != null) {
			selectedCase.getLawyer().getCasesHandled().removeIf(c -> c.getCode().equals(selectedCase.getCode()));
		}

		// Step 6: Remove case meetings
		selectedCase.getMeetingsList().clear();

		// Step 7: Refresh TableView
		casesTable.getItems().remove(selectedCase);

		// Step 8: Save Changes (Ensure it happens after all removals)
		if (removedFromJudge || removedFromCourt) {
			Court.saveCourt();
			showAlert(Alert.AlertType.INFORMATION, "Case Deleted", "The case has been successfully deleted.");
		} else {
			showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Could not delete the case.");
		}
	}

	@FXML
	private void displaySelectedCaseDetails() {
		// Step 1: Get the selected case
		Case selectedCase = casesTable.getSelectionModel().getSelectedItem();

		if (selectedCase == null) {
			showAlert(Alert.AlertType.WARNING, "No Case Selected", "Please select a case to view details.");
			caseDetailsTextArea.clear(); // Clear the text area if nothing is selected
			return;
		}

		// Step 2: Display case details in the text area
		caseDetailsTextArea.setText(selectedCase.toString());
	}

	/** 🔹 Apply filtering logic when clicking "Filter" button */
	@FXML
	private void applyFilters() {
		// Step 1: Get all cases assigned to the judge
		List<Case> allCasesList = new ArrayList<>(loggedInJudge.getCasesHandled());

		// Step 2: Check if "Display All Cases" is selected
		if (allCases.isSelected()) {
			casesTable.getItems().setAll(allCasesList); // Show all cases
			return; // Skip filtering
		}

		// Step 3: Apply Filtering
		List<Case> filteredCases = allCasesList.stream().filter(c -> {
			boolean typeMatch = false;
			boolean statusMatch = false;

			// ✅ Check case type filter
			if (criminal.isSelected() && c instanceof CriminalCase)
				typeMatch = true;
			if (family.isSelected() && c instanceof FamilyCase)
				typeMatch = true;
			if (financial.isSelected() && c instanceof FinancialCase)
				typeMatch = true;

			// If no case type checkbox is selected, default to true (no filtering on type)
			if (!criminal.isSelected() && !family.isSelected() && !financial.isSelected()) {
				typeMatch = true;
			}

			// ✅ Check case status filter
			if (inProcess.isSelected() && c.getCaseStatus() == Status.inProcess)
				statusMatch = true;
			if (finished.isSelected() && c.getCaseStatus() == Status.finished)
				statusMatch = true;
			if (canceled.isSelected() && c.getCaseStatus() == Status.canceled)
				statusMatch = true;

			// If no status checkbox is selected, default to true (no filtering on status)
			if (!inProcess.isSelected() && !finished.isSelected() && !canceled.isSelected()) {
				statusMatch = true;
			}

			// Only return cases that match BOTH filters
			return typeMatch && statusMatch;
		}).toList();

		// Step 4: Update TableView
		casesTable.getItems().setAll(filteredCases);
	}

	/** 🔹 Show details of the selected case */
	private void showCaseDetails() {
		Case selectedCase = casesTable.getSelectionModel().getSelectedItem();
		if (selectedCase == null) {
			showAlert(Alert.AlertType.WARNING, "No Case Selected", "Please select a case to view details.");
			return;
		}
		System.out.println("Opening details for case: " + selectedCase.getCode());
		// Implement case detail viewing logic here...
	}

	/** 🔹 Show alert messages */
	private void showAlert(Alert.AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
