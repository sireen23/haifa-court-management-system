package control;

import java.text.SimpleDateFormat;
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
import model.Witness;

public class WitnessesController {

	@FXML
	private TableView<Witness> witnessesTable;

	@FXML
	private TableColumn<Witness, Integer> id;

	@FXML
	private TableColumn<Witness, String> firstName;

	@FXML
	private TableColumn<Witness, String> lastName;

	@FXML
	private TableColumn<Witness, String> birthDate;

	@FXML
	private TableColumn<Witness, String> address;

	@FXML
	private TableColumn<Witness, String> phoneNumber;

	@FXML
	private TableColumn<Witness, String> email;

	@FXML
	private TableColumn<Witness, String> gender;

	@FXML
	private ImageView backImage;

	@FXML
	public void initialize() {
		System.out.println("✅ WitnessesController Initialized");

		// ✅ Ensure TableView is properly set up
		setupTableColumns();

		// ✅ Load witness data into the table AFTER UI is ready
		Platform.runLater(this::loadWitnessesData);

		// ✅ Handle back button click
		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void setupTableColumns() {
		// ✅ Match table columns to Witness class properties
		id.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));

		firstName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
		lastName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));

		// ✅ Format Birth Date as "DD/MM/YYYY"
		birthDate.setCellValueFactory(cellData -> {
			return new SimpleStringProperty(formatDate(cellData.getValue().getBirthDate()));
		});

		address.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
		phoneNumber.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhoneNumber()));
		email.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
		gender.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender().toString()));
	}

	private String formatDate(java.util.Date date) {
		if (date == null)
			return "N/A";
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(date);
	}

	private void loadWitnessesData() {
		Court court = Court.getInstance(); // Assuming Court is a Singleton

		// ✅ Retrieve all witnesses
		List<Witness> witnessList = new ArrayList<>(court.getAllWitnesses().values());

		// ✅ Convert List to ObservableList
		ObservableList<Witness> observableWitnesses = FXCollections.observableArrayList(witnessList);

		if (observableWitnesses.isEmpty()) {
			System.out.println("❌ No witnesses found!");
		} else {
			System.out.println("✅ Loaded " + observableWitnesses.size() + " witnesses into the table.");
		}

		// ✅ Set data and refresh table
		witnessesTable.setItems(observableWitnesses);
		witnessesTable.refresh(); // 🔥 Ensure table updates
	}
}
