package control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Employee;

public class RemoveEmployeeController {

	@FXML
	private TableView<Employee> employeeTable;

	@FXML
	private TableColumn<Employee, Integer> colId;

	@FXML
	private TableColumn<Employee, String> colFirstName;

	@FXML
	private TableColumn<Employee, String> colLastName;

	@FXML
	private TableColumn<Employee, String> colPosition;

	@FXML
	private TableColumn<Employee, Double> colSalary;

	@FXML
	private TableColumn<Employee, String> colPhone;

	@FXML
	private TableColumn<Employee, String> colEmail;

	@FXML
	private Button deleteButton;

	@FXML
	private Button cancelButton;

	@FXML
	private ImageView backImage;

	private ObservableList<Employee> employeesList = FXCollections.observableArrayList();

	public void initialize() {
		System.out.println("Initializing Remove Employee Page...");

		// Setup columns
		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
		colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
		colPosition.setCellValueFactory(new PropertyValueFactory<>("position"));
		colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
		colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

		// Load employees
		loadEmployees();

		// Delete button action
		deleteButton.setOnAction(event -> handleDelete());

		// Cancel button action
		cancelButton.setOnAction(event -> handleCancel());

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void loadEmployees() {
		// Get employees from the system and populate the table
		employeesList.setAll(Court.getInstance().getAllEmployees().values());
		employeeTable.setItems(employeesList);
	}

	private void handleDelete() {
		Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();

		if (selectedEmployee == null) {
			showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select an employee to delete.");
			return;
		}

		// Remove from the court system
		Court.getInstance().removeEmployee(selectedEmployee);
		Court.saveCourt();

		// Remove from UI table
		employeesList.remove(selectedEmployee);

		showAlert(Alert.AlertType.INFORMATION, "Success", "Employee deleted successfully.");
	}

	private void handleCancel() {
		Stage stage = (Stage) cancelButton.getScene().getWindow();
		stage.close();
	}

	private void showAlert(Alert.AlertType type, String title, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
