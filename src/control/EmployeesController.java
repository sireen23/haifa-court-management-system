package control;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Employee;

public class EmployeesController {

	@FXML
	private TableView<Employee> employeesTable;

	@FXML
	private TableColumn<Employee, Integer> id;

	@FXML
	private TableColumn<Employee, String> firstName;

	@FXML
	private TableColumn<Employee, String> lastName;

	@FXML
	private TableColumn<Employee, String> birthDate;

	@FXML
	private TableColumn<Employee, String> address;

	@FXML
	private TableColumn<Employee, String> phoneNumber;

	@FXML
	private TableColumn<Employee, String> email;

	@FXML
	private TableColumn<Employee, String> gender;

	@FXML
	private TableColumn<Employee, String> workStartDate;

	@FXML
	private TableColumn<Employee, String> salary;

	@FXML
	private TableColumn<Employee, String> position;

	@FXML
	private ImageView backImage;
	@FXML
	private TableColumn<Employee, String> imageColumn;

	@FXML
	public void initialize() {
		System.out.println("✅ EmployeesController Initialized");

		// ✅ Ensure the TableView is properly set up
		setupTableColumns();

		// ✅ Load employees data into the table AFTER the UI is ready
		Platform.runLater(this::loadEmployeesData);

		// ✅ Set back button click event
		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void setupTableColumns() {
		// ✅ Match table columns to Employee class properties
		id.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
		firstName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
		lastName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
		address.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
		phoneNumber.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhoneNumber()));
		email.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
		gender.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender().toString()));
		position.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getPosition().toString()));

		// ✅ Format Birth Date as "DD/MM/YYYY"
		birthDate.setCellValueFactory(cellData -> {
			Date date = cellData.getValue().getBirthDate();
			return new SimpleStringProperty((date != null) ? formatDate(date) : "N/A");
		});

		// ✅ Format Work Start Date as "DD/MM/YYYY"
		workStartDate.setCellValueFactory(cellData -> {
			Date date = cellData.getValue().getWorkStartDate();
			return new SimpleStringProperty((date != null) ? formatDate(date) : "N/A");
		});

		// ✅ Format Salary
		salary.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.format("%.2f $", cellData.getValue().getSalary())));

		imageColumn.setCellValueFactory(new PropertyValueFactory<>("profileImagePath"));

		imageColumn.setCellFactory(column -> new TableCell<Employee, String>() { // Replace Judge with Employee/Lawyer
																					// if
																					// needed
			private final ImageView imageView = new ImageView();

			{
				// ✅ Set small size for images in the table
				imageView.setFitWidth(50);
				imageView.setFitHeight(50);
				imageView.setPreserveRatio(true);

				// ✅ Click to open full image
				imageView.setOnMouseClicked(event -> {
					String imagePath = getItem(); // ✅ Get the image path from the row
					if (imagePath != null && !imagePath.isEmpty()) {
						showFullImagePopup(imagePath); // ✅ Open Full Image Popup
					}
				});
			}

			@Override
			protected void updateItem(String imagePath, boolean empty) { // ✅ Correct Signature
				super.updateItem(imagePath, empty);

				if (empty || imagePath == null || imagePath.isEmpty()) {
					setGraphic(null); // ✅ Hide ImageView if no image
				} else {
					File file = new File(imagePath);
					if (file.exists()) {
						imageView.setImage(new Image(file.toURI().toString())); // ✅ Load Image
					} else {
						imageView.setImage(
								new Image(getClass().getResourceAsStream("/uploaded_images/default_image.png"))); // ✅
																													// Default
																													// Image
					}
					setGraphic(imageView);
				}
			}
		});

	}

	// Show Full Image Popup
	private void showFullImagePopup(String imagePath) {
		Stage popupStage = new Stage();
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.setTitle("Profile Image");

		// ✅ Create ImageView for Full Image
		ImageView fullImageView = new ImageView();
		fullImageView.setPreserveRatio(true);
		fullImageView.setFitWidth(500); // ✅ Adjust width for popup
		fullImageView.setFitHeight(500);

		File file = new File(imagePath);
		if (file.exists()) {
			fullImageView.setImage(new Image(file.toURI().toString())); // ✅ Load Full Image
		} else {
			fullImageView.setImage(new Image(getClass().getResourceAsStream("/uploaded_images/default_image.png"))); // ✅
																														// Default
																														// Image
		}

		// ✅ Add Close Button
		Button closeButton = new Button("Close");
		closeButton.setOnAction(e -> popupStage.close());

		// ✅ Layout
		VBox vbox = new VBox(fullImageView, closeButton);
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(10);
		vbox.setStyle("-fx-background-color: white; -fx-padding: 20px;");

		Scene scene = new Scene(vbox);
		popupStage.setScene(scene);
		popupStage.show();
	}

	private String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(date);
	}

	private void loadEmployeesData() {
		Court court = Court.getInstance(); // Assuming Court is a Singleton

		// ✅ Retrieve all employees
		List<Employee> employeeList = new ArrayList<>(court.getAllEmployees().values());

		// ✅ Convert List to ObservableList
		ObservableList<Employee> observableEmployees = FXCollections.observableArrayList(employeeList);

		if (observableEmployees.isEmpty()) {
			System.out.println("❌ No employees found!");
		} else {
			System.out.println("✅ Loaded " + observableEmployees.size() + " employees into the table.");
		}

		// ✅ Set data and refresh table
		employeesTable.setItems(observableEmployees);
		employeesTable.refresh(); // 🔥 Ensure table updates
	}
}
