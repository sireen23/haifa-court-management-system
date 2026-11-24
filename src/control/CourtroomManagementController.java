package control;

import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;
import model.Courtroom;
import model.Department;

public class CourtroomManagementController {

	@FXML
	private TableView<Courtroom> courtRoomTable; // Table to display courtrooms

	@FXML
	private TableColumn<Courtroom, Integer> numberColumn; // Courtroom Number Column

	@FXML
	private TableColumn<Courtroom, Integer> departmentColumn; // Department ID Column

	@FXML
	private Button deleteCourtRoom; // Delete button

	@FXML
	private TextField number; // Courtroom Number Input Field

	@FXML
	private ComboBox<Integer> departmentCombo; // Department ComboBox

	@FXML
	private Button addButton, resetButton; // Add & Reset Buttons

	private Court court; // Instance of Court

	@FXML
	private ImageView backImage;

	@FXML
	public void initialize() {
		court = Court.getInstance(); // Load data from court.ser

		// Setup TableView columns
		numberColumn.setCellValueFactory(new PropertyValueFactory<>("courtroomNumber"));
		departmentColumn.setCellValueFactory(new PropertyValueFactory<>("departmentId"));

		// Load all courtrooms into the table
		loadCourtrooms();

		// Load department IDs into ComboBox
		loadDepartments();

		// Set actions for buttons
		deleteCourtRoom.setOnAction(this::handleDeleteCourtroom);
		addButton.setOnAction(this::handleAddCourtroom);
		resetButton.setOnAction(this::handleResetForm);

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Employee.fxml", "Employee Page");
		});
	}

	// Load all courtrooms into TableView
	private void loadCourtrooms() {
		ObservableList<Courtroom> courtroomList = FXCollections.observableArrayList(court.getAllCourtrooms().values());
		courtRoomTable.setItems(courtroomList);
	}

	// Load all department IDs into ComboBox
	private void loadDepartments() {
		List<Integer> departmentIds = court.getAllDepartments().keySet().stream().toList();
		departmentCombo.setItems(FXCollections.observableArrayList(departmentIds));

		// Display only department IDs in the ComboBox
		departmentCombo.setConverter(new StringConverter<>() {
			@Override
			public String toString(Integer departmentId) {
				return (departmentId != null) ? departmentId.toString() : "";
			}

			@Override
			public Integer fromString(String string) {
				return null; // Not needed
			}
		});
	}

	// Handle DELETE courtroom action
	@FXML
	private void handleDeleteCourtroom(ActionEvent event) {
		Courtroom selectedCourtroom = courtRoomTable.getSelectionModel().getSelectedItem();
		if (selectedCourtroom == null) {
			showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a courtroom to delete.");
			return;
		}

		// Confirmation alert before deletion
		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Delete Confirmation");
		confirmation.setHeaderText(
				"Are you sure you want to delete Courtroom #" + selectedCourtroom.getCourtroomNumber() + "?");
		confirmation.setContentText("This action cannot be undone.");
		Optional<ButtonType> result = confirmation.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			court.removeCourtroom(selectedCourtroom);
			court.saveCourt(); // Save changes
			loadCourtrooms(); // Refresh table
			showAlert(Alert.AlertType.INFORMATION, "Success", "Courtroom deleted successfully.");
		}
	}

	// Handle ADD courtroom action
	@FXML
	private void handleAddCourtroom(ActionEvent event) {
		try {
			int courtNumber = Integer.parseInt(number.getText().trim());
			Integer departmentId = departmentCombo.getValue();

			if (departmentId == null) {
				showAlert(Alert.AlertType.ERROR, "Input Error", "Please select a department.");
				return;
			}

			if (court.getAllCourtrooms().containsKey(courtNumber)) {
				showAlert(Alert.AlertType.ERROR, "Input Error", "Courtroom number already exists.");
				return;
			}

			Department department = court.getRealDepartment(departmentId);
			// Create and add new courtroom
			Courtroom newCourtroom = new Courtroom(courtNumber, department);
			court.addCourtroom(newCourtroom);
			court.saveCourt(); // Save changes

			// Refresh TableView
			loadCourtrooms();
			handleResetForm(null); // Reset form after adding

			showAlert(Alert.AlertType.INFORMATION, "Success", "Courtroom added successfully.");
		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid courtroom number.");
		}
	}

	// Handle RESET form action
	@FXML
	private void handleResetForm(ActionEvent event) {
		number.clear();
		departmentCombo.getSelectionModel().clearSelection();
	}

	// Utility method to show alerts
	private void showAlert(Alert.AlertType alertType, String title, String content) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
