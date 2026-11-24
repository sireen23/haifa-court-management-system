package control;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import model.Case;
import model.Department;
import model.Employee;
import model.Lawyer;

public class DepartmentQueriesController {

	@FXML
	private DatePicker date;

	@FXML
	private Label NumberOfCasesLabel;

	@FXML
	private Button activateQuery1;

	@FXML
	private TableView<Department> departmentsTable;

	@FXML
	private TableColumn<Department, Integer> depNumber;

	@FXML
	private TableColumn<Department, Integer> depCounter;

	@FXML
	private Button findQuery2;

	@FXML
	private ComboBox<String> casesComboBox;

	@FXML
	private Button findQuery3;

	@FXML
	private TextArea displayLawyerTextArea;

	@FXML
	private ComboBox<Integer> departmentsCombo;

	@FXML
	private Button findQuery4;

	@FXML
	private TextArea employeeTextArea;

	private Court court;

	@FXML
	private ImageView backImage;

	@FXML
	public void initialize() {
		System.out.println("🚀 Initializing DepartmentQueriesController...");

		court = Court.getInstance();
		if (court == null) {
			System.out.println("❌ Court instance is NULL!");
			return;
		}

		System.out.println("✅ Court instance loaded with " + court.getAllCases().size() + " cases.");

		depNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
		depCounter.setCellValueFactory(new PropertyValueFactory<>("inactiveCasesCount")); // Fix the property name!

		loadInactiveCasesByDepartment();

		loadDepartments();
		loadCases();

		activateQuery1.setOnAction(this::handleQuery1);
		findQuery2.setOnAction(this::handleQuery2);
		findQuery3.setOnAction(this::handleQuery3);
		findQuery4.setOnAction(this::handleQuery4);

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Employee.fxml", "Employee Page");
		});
	}

	/** Query 1: Count cases before a given date */
	@FXML
	private void handleQuery1(ActionEvent event) {
		LocalDate selectedDate = date.getValue();
		if (selectedDate == null) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "Please select a date.");
			return;
		}

		// Convert LocalDate to Date
		Date selectedDateConverted = java.sql.Date.valueOf(selectedDate);

		// Get number of cases before the date
		int caseCount = court.howManyCasesBefore(selectedDateConverted);

		// Update label text
		if (caseCount > 0) {
			NumberOfCasesLabel.setText("Number of cases before " + selectedDate + " is: " + caseCount);
		} else {
			NumberOfCasesLabel.setText("No cases were finalized before " + selectedDate + ".");
		}

		System.out.println("✅ Query 1 Result: " + caseCount + " cases before " + selectedDate);
	}

	/** Query 2: Find inactive cases by department */
	@FXML
	private void handleQuery2(ActionEvent event) {
		departmentsTable.getItems().clear();
		HashMap<Department, Integer> departmentCounts = court.findInActiveCasesCountByDepartment();

		ObservableList<Department> departmentList = FXCollections.observableArrayList(departmentCounts.keySet());
		departmentsTable.setItems(departmentList);

		System.out.println("✅ Loaded " + departmentList.size() + " departments with inactive case counts.");
	}

	/** Query 3: Find the most suitable lawyer for a case */
	@FXML
	private void handleQuery3(ActionEvent event) {
		String caseId = casesComboBox.getValue();
		if (caseId == null) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "Please select a case.");
			return;
		}

		Case selectedCase = court.getAllCases().get(caseId);
		Lawyer assignedLawyer = court.findTheSuitableLawyer(selectedCase);

		if (assignedLawyer != null) {
			displayLawyerTextArea.setText("Assigned Lawyer:\n" + assignedLawyer.toString());
			System.out.println("✅ Assigned Lawyer: " + assignedLawyer.getId() + " to Case: " + caseId);

			// Save the changes (assign lawyer, schedule meeting)
			court.saveCourt();
		} else {
			displayLawyerTextArea.setText("No suitable lawyer found.");
			System.out.println("⚠️ No available lawyer for Case: " + caseId);
		}
	}

	/** Query 4: Appoint a new manager for a department */
	@FXML
	private void handleQuery4(ActionEvent event) {
		Integer departmentId = departmentsCombo.getValue();
		if (departmentId == null) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "Please select a department.");
			return;
		}

		Department selectedDepartment = court.getAllDepartments().get(departmentId);
		Employee newManager = court.AppointANewManager(selectedDepartment);

		if (newManager != null) {
			employeeTextArea.setText("New Manager Appointed:\n" + newManager.toString());
			System.out.println("✅ New manager assigned: " + newManager.getId());

			// Save changes to the system
			court.saveCourt();
		} else {
			employeeTextArea.setText("No suitable manager found.");
			System.out.println("⚠️ No employee eligible for promotion.");
		}
	}

	/** Load all departments for ComboBox */
	private void loadDepartments() {
		ObservableList<Integer> departmentIds = FXCollections.observableArrayList(
				court.getAllDepartments().values().stream().map(Department::getNumber).collect(Collectors.toList()));

		departmentsCombo.setItems(departmentIds);
	}

	/** Load all cases for ComboBox */
	private void loadCases() {
		ObservableList<String> caseIds = FXCollections.observableArrayList(
				court.getAllCases().values().stream().map(Case::getCode).collect(Collectors.toList()));

		casesComboBox.setItems(caseIds);
	}

	private void loadInactiveCasesByDepartment() {
		if (court == null) {
			System.out.println("❌ Court instance is NULL!");
			return;
		}

		ObservableList<Department> departmentList = FXCollections
				.observableArrayList(court.getAllDepartments().values());

		departmentsTable.setItems(departmentList);

		System.out.println("✅ Loaded " + departmentList.size() + " departments with inactive case counts.");
	}

	/** Show alerts for errors and messages */
	private void showAlert(Alert.AlertType alertType, String title, String content) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
