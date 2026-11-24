package control;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import model.Department;

public class DepartmentsController {

	@FXML
	private TableView<Department> departmentsTable;

	@FXML
	private TableColumn<Department, Integer> departmentNumber;

	@FXML
	private TableColumn<Department, String> departmentName;

	@FXML
	private TableColumn<Department, String> departmentSpecialization;

	@FXML
	private TableColumn<Department, Integer> managerID;

	@FXML
	private TableColumn<Department, String> building;

	@FXML
	private ImageView backImage;

	@FXML
	public void initialize() {
		System.out.println("✅ DepartmentsController Initialized");

		// ✅ Ensure the TableView is properly set up
		setupTableColumns();

		// ✅ Load departments data into the table AFTER the UI is ready
		Platform.runLater(this::loadDepartmentsData);

		// ✅ Set back button click event
		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void setupTableColumns() {
		// ✅ Match table columns to Department class properties
		departmentNumber.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getNumber()));

		departmentName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

		// ✅ Get department specialization (with null check)
		departmentSpecialization.setCellValueFactory(cellData -> new SimpleStringProperty(
				(cellData.getValue().getSpecialization() != null) ? cellData.getValue().getSpecialization().toString()
						: "N/A"));

		// ✅ Get manager ID (with null check)
		managerID.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
				(cellData.getValue().getManager() != null) ? cellData.getValue().getManager().getId() : -1));

		building.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBuilding()));

		// ✅ Apply text wrapping to departmentName and building columns
		wrapTextColumn(departmentName, 150);
		wrapTextColumn(building, 130);
	}

	private void wrapTextColumn(TableColumn<Department, String> tableColumn, double width) {
		tableColumn.setCellFactory(column -> new TableCell<Department, String>() {
			private final Text text = new Text();

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					text.setText(item);
					text.setWrappingWidth(width); // ✅ Ensures proper text wrapping
					setGraphic(text);
				}
			}
		});
	}

	private void loadDepartmentsData() {
		Court court = Court.getInstance(); // ✅ Assuming Court is a Singleton

		// ✅ Retrieve all departments
		List<Department> departmentList = new ArrayList<>(court.getAllDepartments().values());

		// ✅ Convert List to ObservableList
		ObservableList<Department> observableDepartments = FXCollections.observableArrayList(departmentList);

		if (observableDepartments.isEmpty()) {
			System.out.println("❌ No departments found!");
		} else {
			System.out.println("✅ Loaded " + observableDepartments.size() + " departments into the table.");
		}

		// ✅ Set data and refresh table
		departmentsTable.setItems(observableDepartments);
		departmentsTable.refresh(); // 🔥 Ensure table updates
	}
}
