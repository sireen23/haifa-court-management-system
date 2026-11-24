package control;

import java.util.ArrayList;
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
import model.Courtroom;

public class CourtroomsController {

	@FXML
	private TableView<Courtroom> courtroomsTable;

	@FXML
	private TableColumn<Courtroom, Integer> number;

	@FXML
	private TableColumn<Courtroom, Integer> departmentID;

	@FXML
	private TableColumn<Courtroom, String> departmentName;

	@FXML
	private TableColumn<Courtroom, String> departmentSpecialization;

	@FXML
	private ImageView backImage;

	@FXML
	public void initialize() {
		System.out.println("✅ CourtroomsController Initialized");

		// Ensure the TableView is properly set up
		setupTableColumns();

		// ✅ Load courtrooms data into the table AFTER the UI is ready
		Platform.runLater(this::loadCourtroomsData);

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void setupTableColumns() {
		// ✅ Match table columns to Courtroom class properties
		number.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCourtroomNumber())); // ✅
																														// Fix
																														// type
																														// mismatch

		// ✅ Get department ID with null check
		departmentID.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
				(cellData.getValue().getDepartment() != null) ? cellData.getValue().getDepartment().getNumber() : -1 // ✅
																														// Prevents
																														// NullPointerException
		));

		// ✅ Get department name with null check
		departmentName.setCellValueFactory(cellData -> new SimpleStringProperty(
				(cellData.getValue().getDepartment() != null) ? cellData.getValue().getDepartment().getName() : "N/A"));

		// ✅ Get department specialization with null check
		departmentSpecialization
				.setCellValueFactory(cellData -> new SimpleStringProperty((cellData.getValue().getDepartment() != null)
						? cellData.getValue().getDepartment().getSpecialization().toString()
						: "N/A"));

		// ✅ Handle back button click
		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void loadCourtroomsData() {
		Court court = Court.getInstance(); // Assuming Court is a Singleton

		// ✅ Retrieve all courtrooms
		List<Courtroom> courtroomList = new ArrayList<>(court.getAllCourtrooms().values());

		// ✅ Convert List to ObservableList
		ObservableList<Courtroom> observableCourtrooms = FXCollections.observableArrayList(courtroomList);

		if (observableCourtrooms.isEmpty()) {
			System.out.println("❌ No courtrooms found!");
		} else {
			System.out.println("✅ Loaded " + observableCourtrooms.size() + " courtrooms into the table.");
		}

		// ✅ Set data and refresh table
		courtroomsTable.setItems(observableCourtrooms);
		courtroomsTable.refresh(); // 🔥 Ensure table updates
	}

	private void goBack() {
		System.out.println("🔙 Going back to the previous page...");
		// Implement scene switching logic here
	}
}
