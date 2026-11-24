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
import model.Lawyer;

public class RemoveLawyerController {

	@FXML
	private TableView<Lawyer> lawyerTable;

	@FXML
	private TableColumn<Lawyer, Integer> colId;

	@FXML
	private TableColumn<Lawyer, String> colFirstName;

	@FXML
	private TableColumn<Lawyer, String> colLastName;

	@FXML
	private TableColumn<Lawyer, String> colSpecialization;

	@FXML
	private TableColumn<Lawyer, Double> colSalary;

	@FXML
	private TableColumn<Lawyer, String> colPhone;

	@FXML
	private TableColumn<Lawyer, String> colEmail;

	@FXML
	private Button deleteButton;

	@FXML
	private Button cancelButton;

	@FXML
	private ImageView backImage;

	private ObservableList<Lawyer> lawyersList = FXCollections.observableArrayList();

	public void initialize() {
		System.out.println("Initializing Remove Lawyer Page...");

		// Setup columns
		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
		colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
		colSpecialization.setCellValueFactory(new PropertyValueFactory<>("specialization"));
		colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
		colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

		// Load lawyers
		loadLawyers();

		// Delete button action
		deleteButton.setOnAction(event -> handleDelete());

		// Cancel button action
		cancelButton.setOnAction(event -> handleCancel());

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void loadLawyers() {
		// Get lawyers from the system and populate the table
		lawyersList.setAll(Court.getInstance().getAllLawyers().values());
		lawyerTable.setItems(lawyersList);
	}

	private void handleDelete() {
		Lawyer selectedLawyer = lawyerTable.getSelectionModel().getSelectedItem();

		if (selectedLawyer == null) {
			showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a lawyer to delete.");
			return;
		}

		// Remove from the court system
		Court.getInstance().removeLawyer(selectedLawyer);
		Court.saveCourt();

		// Remove from UI table
		lawyersList.remove(selectedLawyer);

		showAlert(Alert.AlertType.INFORMATION, "Success", "Lawyer deleted successfully.");
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
