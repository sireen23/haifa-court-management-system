package control;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import model.Accused;

public class AccusedsController {

	@FXML
	private TableView<Accused> tableAcuusedes;

	@FXML
	private TableColumn<Accused, Integer> id;

	@FXML
	private TableColumn<Accused, String> firstName;

	@FXML
	private TableColumn<Accused, String> lastName;

	@FXML
	private TableColumn<Accused, String> birthDate; // ✅ CORRECT TYPE

	@FXML
	private TableColumn<Accused, String> address;

	@FXML
	private TableColumn<Accused, String> phoneNumber;

	@FXML
	private TableColumn<Accused, String> email;

	@FXML
	private TableColumn<Accused, String> gender;

	@FXML
	private TableColumn<Accused, String> job;

	@FXML
	private ImageView backImage;

	@FXML
	public void initialize() {
		System.out.println("✅ AccusedsController Initialized");

		// Ensure the TableView is properly set up
		setupTableColumns();

		// ✅ Load accused data into the table AFTER the UI is ready
		Platform.runLater(this::loadAccusedData);

		// ✅ Set back button click event
		if (backImage != null) {
			backImage.setOnMouseClicked(event -> goBack());
		} else {
			System.err.println("❌ ERROR: backImage is NULL! Check FXML.");
		}

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void setupTableColumns() {
		// ✅ Match table columns to Accused class properties
		id.setCellValueFactory(new PropertyValueFactory<>("id"));
		firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
		lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
		birthDate.setCellValueFactory(cellData -> {
			Date date = cellData.getValue().getBirthDate();
			return new SimpleStringProperty((date != null) ? formatDate(date) : "N/A");
		});
		address.setCellValueFactory(new PropertyValueFactory<>("address"));
		phoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		email.setCellValueFactory(new PropertyValueFactory<>("email"));
		gender.setCellValueFactory(new PropertyValueFactory<>("gender"));
		job.setCellValueFactory(new PropertyValueFactory<>("job"));
	}

	private String formatDate(Date date) {
		if (date == null)
			return "N/A";
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(date);
	}

	private void loadAccusedData() {
		Court court = Court.getInstance(); // Assuming Court is a Singleton
		ObservableList<Accused> accusedList = FXCollections.observableArrayList(court.getAllAccuseds().values());

		if (accusedList.isEmpty()) {
			System.out.println("❌ No accused persons found!");
		} else {
			System.out.println("✅ Loaded " + accusedList.size() + " accused persons into the table.");
		}

		tableAcuusedes.setItems(accusedList);
	}

	private void goBack() {
		System.out.println("🔙 Going back to the previous page...");
		// Implement scene switching logic here
	}
}
