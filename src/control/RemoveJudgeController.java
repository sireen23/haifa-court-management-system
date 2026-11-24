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
import model.Judge;

public class RemoveJudgeController {

	@FXML
	private TableView<Judge> judgeTable;

	@FXML
	private TableColumn<Judge, Integer> colId;

	@FXML
	private TableColumn<Judge, String> colFirstName;

	@FXML
	private TableColumn<Judge, String> colLastName;

	@FXML
	private TableColumn<Judge, String> colSpecialization;

	@FXML
	private TableColumn<Judge, Double> colSalary;

	@FXML
	private TableColumn<Judge, String> colPhone;

	@FXML
	private TableColumn<Judge, String> colEmail;

	@FXML
	private Button deleteButton;

	@FXML
	private Button cancelButton;

	@FXML
	private ImageView backImage;

	private ObservableList<Judge> judgesList = FXCollections.observableArrayList();

	public void initialize() {
		System.out.println("Initializing Remove Judge Page...");

		// Setup table columns
		colId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
		colLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
		colSpecialization.setCellValueFactory(new PropertyValueFactory<>("specialization"));
		colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
		colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

		// Load judges
		loadJudges();

		// Delete button action
		deleteButton.setOnAction(event -> handleDelete());

		// Cancel button action
		cancelButton.setOnAction(event -> handleCancel());

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void loadJudges() {
		// Filter only judges from the allLawyers map
		judgesList
				.setAll(Court.getInstance().getAllLawyers().values().stream().filter(lawyer -> lawyer instanceof Judge) // ✅
																														// Keep
																														// only
																														// Judges
						.map(lawyer -> (Judge) lawyer) // ✅ Cast to Judge
						.toList() // ✅ Convert to list
				);

		judgeTable.setItems(judgesList);
	}

	private void handleDelete() {
		Judge selectedJudge = judgeTable.getSelectionModel().getSelectedItem();

		if (selectedJudge == null) {
			showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a judge to delete.");
			return;
		}

		// Remove from the court system
		Court.getInstance().removeJudge(selectedJudge);
		Court.saveCourt();

		// Remove from UI table
		judgesList.remove(selectedJudge);

		showAlert(Alert.AlertType.INFORMATION, "Success", "Judge deleted successfully.");
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
