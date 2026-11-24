package control;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import enums.Position;
import enums.Specialization;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Department;
import model.Employee;
import model.Judge;
import model.Lawyer;
import model.Person;

public class DepartmentManagementController {

	@FXML
	private TableView<Department> departmentsTable;

	@FXML
	private TableColumn<Department, Integer> number;

	@FXML
	private TableColumn<Department, String> name;

	@FXML
	private TableColumn<Department, String> manager;

	@FXML
	private TableColumn<Department, String> building;

	@FXML
	private TableColumn<Department, Specialization> specialization;

	@FXML
	private Button removeDepartmentButton;

	@FXML
	private ImageView backButton;

	@FXML
	private TextField numberField, nameField, buildingField;

	@FXML
	private ComboBox<Integer> managerComboBox;

	@FXML
	private ComboBox<Specialization> specializationCombo;

	@FXML
	private Button specializataddDepartmentButtonionCombo;
	@FXML
	private ComboBox<Integer> allDepartmentsCombo;

	@FXML
	private ComboBox<Integer> allLawyersCombo;

	@FXML
	private ComboBox<Integer> allJudgesCombo;

	@FXML
	private Button addLawyerJudge;

	@FXML
	private Button resetLawyerJudge;

	private Court court;

	@FXML
	private ImageView backImage;

	@FXML
	public void initialize() {
		System.out.println("🚀 Initializing DepartmentManagementController...");

		court = Court.getInstance();
		if (court == null) {
			System.out.println("❌ Court instance is NULL!");
			return;
		}

		System.out.println("✅ Court instance loaded with " + court.getAllDepartments().size() + " departments.");

		// Set table column bindings for department overview
		number.setCellValueFactory(new PropertyValueFactory<>("number"));
		name.setCellValueFactory(new PropertyValueFactory<>("name"));
		manager.setCellValueFactory(new PropertyValueFactory<>("manager"));
		building.setCellValueFactory(new PropertyValueFactory<>("building"));
		specialization.setCellValueFactory(new PropertyValueFactory<>("specialization"));

		// Load data for Departments Overview Tab
		loadDepartments(); // For departmentsTable

		// Load data for Adding Departments
		loadManagers();
		loadSpecializations();

		// Load data for Lawyer-Judge assignment tab
		loadDepartmentsForCombo(); // Ensure the departments are also loaded into the ComboBox
		loadLawyers();
		loadJudges();

		// Set event handlers
		removeDepartmentButton.setOnAction(this::handleRemoveDepartment);
		specializataddDepartmentButtonionCombo.setOnAction(this::handleAddDepartment);
		backButton.setOnMouseClicked(event -> navigateBack());

		addLawyerJudge.setOnAction(this::handleAddLawyerJudge);
		resetLawyerJudge.setOnAction(this::handleReset);

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Employee.fxml", "Employee Page");
		});
	}

	private void loadDepartmentsForCombo() {
		allDepartmentsCombo.getItems().clear();

		List<Integer> departmentIds = court.getAllDepartments().values().stream().map(Department::getNumber)
				.collect(Collectors.toList());

		allDepartmentsCombo.setItems(FXCollections.observableArrayList(departmentIds));
	}

	private void loadDepartments() {
		departmentsTable.getItems().clear();

		// Debugging: Check if Court instance is loaded
		if (court == null) {
			System.out.println("❌ Court instance is NULL!");
			return;
		}

		// Debugging: Print the number of departments stored
		System.out.println("📌 Departments found in Court: " + court.getAllDepartments().size());

		// Fetch departments
		ObservableList<Department> departmentList = FXCollections
				.observableArrayList(court.getAllDepartments().values());

		// Debugging: Print each department
		for (Department d : departmentList) {
			System.out.println("✅ Department #" + d.getNumber() + " - Name: " + d.getName());
		}

		departmentsTable.setItems(departmentList);
	}

	private void loadLawyers() {
		allLawyersCombo.getItems().clear();
		List<Integer> lawyerIds = court.getAllLawyers().values().stream().filter(lawyer -> !(lawyer instanceof Judge)) // Exclude
																														// Judges
				.map(Lawyer::getId).collect(Collectors.toList());

		allLawyersCombo.setItems(FXCollections.observableArrayList(lawyerIds));
	}

	private void loadJudges() {
		allJudgesCombo.getItems().clear();
		List<Integer> judgeIds = court.getAllLawyers().values().stream().filter(lawyer -> lawyer instanceof Judge) // Only
																													// Judges
				.map(Lawyer::getId).collect(Collectors.toList());

		allJudgesCombo.setItems(FXCollections.observableArrayList(judgeIds));
	}

	@FXML
	private void handleAddLawyerJudge(ActionEvent event) {
		Integer departmentId = allDepartmentsCombo.getValue();
		Integer lawyerId = allLawyersCombo.getValue();
		Integer judgeId = allJudgesCombo.getValue();

		if (departmentId == null) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "Please select a department.");
			return;
		}

		if (lawyerId == null && judgeId == null) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "Please select at least one: Lawyer or Judge.");
			return;
		}

		Department department = court.getAllDepartments().get(departmentId);
		boolean added = false;

		if (lawyerId != null) {
			Lawyer lawyer = court.getAllLawyers().get(lawyerId);
			if (lawyer != null) {
				if (court.addLawyerToDepartment(department, lawyer)) {
					added = true;
					System.out.println("✅ Lawyer ID " + lawyerId + " added to Department #" + departmentId);
				} else {
					showAlert(Alert.AlertType.ERROR, "Error", "Failed to add lawyer.");
				}
			}
		}

		if (judgeId != null) {
			Judge judge = (Judge) court.getAllLawyers().get(judgeId);
			if (judge != null) {
				if (court.addJudgeToDepartment(department, judge)) {
					added = true;
					System.out.println("✅ Judge ID " + judgeId + " added to Department #" + departmentId);
				} else {
					showAlert(Alert.AlertType.ERROR, "Error", "Failed to add judge.");
				}
			}
		}

		// **✅ Save Court Data after assignment**
		if (added) {
			court.saveCourt(); // 🔹 Ensures data persists in `court.ser`
			printDepartmentsWithLawyers();
			showAlert(Alert.AlertType.INFORMATION, "Success", "Lawyer/Judge added successfully.");
			handleReset(null);
		}
	}

	@FXML
	private void handleReset(ActionEvent event) {
		allDepartmentsCombo.getSelectionModel().clearSelection();
		allLawyersCombo.getSelectionModel().clearSelection();
		allJudgesCombo.getSelectionModel().clearSelection();
	}

	private void loadManagers() {
		// Retrieve only employees with position "Manager"
		List<Integer> managerIds = court.getAllEmployees().values().stream()
				.filter(emp -> emp.getPosition() == Position.Manager).map(Person::getId).collect(Collectors.toList());

		managerComboBox.setItems(FXCollections.observableArrayList(managerIds));
	}

	private void loadSpecializations() {
		specializationCombo.setItems(FXCollections.observableArrayList(Specialization.values()));
	}

	@FXML
	private void handleRemoveDepartment(ActionEvent event) {
		Department selectedDepartment = departmentsTable.getSelectionModel().getSelectedItem();
		if (selectedDepartment == null) {
			showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a department to delete.");
			return;
		}

		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Delete Confirmation");
		confirmation
				.setHeaderText("Are you sure you want to delete Department #" + selectedDepartment.getNumber() + "?");
		confirmation.setContentText("This action cannot be undone.");
		Optional<ButtonType> result = confirmation.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			court.removeDepartment(selectedDepartment); // Ensure this calls saveCourt() internally
			loadDepartments();
			showAlert(Alert.AlertType.INFORMATION, "Success", "Department deleted successfully.");
		}
	}

	@FXML
	private void handleAddDepartment(ActionEvent event) {
		try {
			int departmentNumber = Integer.parseInt(numberField.getText().trim());
			String departmentName = nameField.getText().trim();
			String building = buildingField.getText().trim();
			Integer managerId = managerComboBox.getValue();
			Specialization specialization = specializationCombo.getValue();

			if (departmentName.isEmpty() || building.isEmpty() || managerId == null || specialization == null) {
				showAlert(Alert.AlertType.ERROR, "Input Error", "All fields are required.");
				return;
			}

			if (court.getAllDepartments().containsKey(departmentNumber)) {
				showAlert(Alert.AlertType.ERROR, "Input Error", "Department number already exists.");
				return;
			}

			Employee manager = court.getAllEmployees().get(managerId);
			if (manager == null) {
				showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid manager selection.");
				return;
			}

			// Create and add new department
			Department newDepartment = new Department(departmentNumber, departmentName, manager, building,
					specialization);
			court.addDepartment(newDepartment);

			loadDepartments();
			resetForm();

			showAlert(Alert.AlertType.INFORMATION, "Success", "Department added successfully.");
		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid department number.");
		}
	}

	@FXML
	private void resetForm() {
		numberField.clear();
		nameField.clear();
		buildingField.clear();
		managerComboBox.getSelectionModel().clearSelection();
		specializationCombo.getSelectionModel().clearSelection();
	}

	private void navigateBack() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Employee.fxml"));
			Parent root = loader.load();
			Stage stage = (Stage) backButton.getScene().getWindow();
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showAlert(Alert.AlertType alertType, String title, String content) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void printDepartmentsWithLawyers() {
		System.out.println("\n📌 Printing Departments and Assigned Lawyers...");
		if (court == null) {
			System.out.println("❌ Court instance is NULL!");
			return;
		}

		for (Department department : court.getAllDepartments().values()) {
			System.out.println("🔹 Department #" + department.getNumber() + " - " + department.getName());

			if (department.getLawyers().isEmpty()) {
				System.out.println("   ❌ No lawyers assigned.");
			} else {
				for (Lawyer lawyer : department.getLawyers()) {
					System.out.println("   ✅ Lawyer ID: " + lawyer.getId() + " - " + lawyer.getFullName());
				}
			}
		}
		System.out.println("✅ End of Department List.\n");
	}

}
