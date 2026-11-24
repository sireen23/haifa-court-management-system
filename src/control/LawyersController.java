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
import model.Lawyer;

public class LawyersController {

	@FXML
	private TableView<Lawyer> lawyersTable;

	@FXML
	private TableColumn<Lawyer, Integer> id;

	@FXML
	private TableColumn<Lawyer, String> firstName;

	@FXML
	private TableColumn<Lawyer, String> lastName;

	@FXML
	private TableColumn<Lawyer, String> birthDate;

	@FXML
	private TableColumn<Lawyer, String> address;

	@FXML
	private TableColumn<Lawyer, String> phoneNumber;

	@FXML
	private TableColumn<Lawyer, String> email;

	@FXML
	private TableColumn<Lawyer, String> gender;

	@FXML
	private TableColumn<Lawyer, String> specialization;

	@FXML
	private TableColumn<Lawyer, Integer> licenseNumber;

	@FXML
	private TableColumn<Lawyer, String> salary;

	@FXML
	private ImageView backImage;
	@FXML
	private TableColumn<Lawyer, String> imageColumn; // Replace `Judge` with the correct class (Employee, Lawyer, etc.)

	@FXML
	public void initialize() {
		System.out.println("✅ LawyersController Initialized");

		// ✅ Ensure the TableView is properly set up
		setupTableColumns();

		// ✅ Load lawyers data into the table AFTER the UI is ready
		Platform.runLater(this::loadLawyersData);

		// ✅ Set back button click event
		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void setupTableColumns() {
		// ✅ Match table columns to Lawyer class properties
		id.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
		firstName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
		lastName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
		address.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
		phoneNumber.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhoneNumber()));
		email.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
		gender.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender().toString()));
		specialization.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getSpecialization().toString()));

		// ✅ Format Birth Date as "DD/MM/YYYY"
		birthDate.setCellValueFactory(cellData -> {
			Date date = cellData.getValue().getBirthDate();
			return new SimpleStringProperty((date != null) ? formatDate(date) : "N/A");
		});

		// ✅ Format Salary
		salary.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.format("%.2f $", cellData.getValue().getSalary())));

		// ✅ License Number
		licenseNumber
				.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getLicenseNumber()));

		imageColumn.setCellValueFactory(new PropertyValueFactory<>("profileImagePath"));

		imageColumn.setCellFactory(column -> new TableCell<Lawyer, String>() { // Replace Judge with Employee/Lawyer
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

	private void loadLawyersData() {
		Court court = Court.getInstance(); // Assuming Court is a Singleton

		// ✅ Retrieve all lawyers
		List<Lawyer> lawyerList = new ArrayList<>(court.getAllLawyers().values());

		// ✅ Convert List to ObservableList
		ObservableList<Lawyer> observableLawyers = FXCollections.observableArrayList(lawyerList);

		if (observableLawyers.isEmpty()) {
			System.out.println("❌ No lawyers found!");
		} else {
			System.out.println("✅ Loaded " + observableLawyers.size() + " lawyers into the table.");
		}

		// ✅ Set data and refresh table
		lawyersTable.setItems(observableLawyers);
		lawyersTable.refresh(); // 🔥 Ensure table updates
	}
}
