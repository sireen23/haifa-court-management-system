package control;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
import model.Judge;

public class JudgesController {

	@FXML
	private TableView<Judge> judgesTable;

	@FXML
	private TableColumn<Judge, Integer> id;

	@FXML
	private TableColumn<Judge, String> firstName;

	@FXML
	private TableColumn<Judge, String> lastName;

	@FXML
	private TableColumn<Judge, String> birthDate;

	@FXML
	private TableColumn<Judge, String> address;

	@FXML
	private TableColumn<Judge, String> phoneNumber;

	@FXML
	private TableColumn<Judge, String> email;

	@FXML
	private TableColumn<Judge, String> gender;

	@FXML
	private TableColumn<Judge, String> specialization; // ✅ This must match FXML exactly!

	@FXML
	private TableColumn<Judge, Integer> licenseNumber;

	@FXML
	private TableColumn<Judge, String> salary;

	@FXML
	private TableColumn<Judge, Integer> experienceYears;

	@FXML
	private ImageView backImage;

	@FXML
	private TableColumn<Judge, String> imageColumn;

	@FXML
	public void initialize() {
		System.out.println("✅ JudgesController Initialized");

		// ✅ Ensure the TableView is properly set up
		setupTableColumns();

		// ✅ Load judges data into the table AFTER the UI is ready
		Platform.runLater(this::loadJudgesData);

		// ✅ Set back button click event
		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void setupTableColumns() {
		// ✅ Match table columns to Judge class properties
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

		// ✅ Experience Years
		experienceYears
				.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getExperienceYear()));

		imageColumn.setCellValueFactory(new PropertyValueFactory<>("profileImagePath"));

		imageColumn.setCellFactory(column -> new TableCell<Judge, String>() { // Replace Judge with Employee/Lawyer
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

	private void loadJudgesData() {
		Court court = Court.getInstance(); // Assuming Court is a Singleton

		// ✅ Retrieve only JUDGES from allLawyers
		List<Judge> judgeList = court.getAllLawyers().values().stream().filter(lawyer -> lawyer instanceof Judge) // 🔥
																													// Ensure
																													// only
																													// Judges
																													// are
																													// included
				.map(judge -> (Judge) judge) // 🔄 Cast to Judge
				.collect(Collectors.toList());

		// ✅ Convert List to ObservableList
		ObservableList<Judge> observableJudges = FXCollections.observableArrayList(judgeList);

		if (observableJudges.isEmpty()) {
			System.out.println("❌ No judges found!");
		} else {
			System.out.println("✅ Loaded " + observableJudges.size() + " judges into the table.");
		}

		// ✅ Set data and refresh table
		judgesTable.setItems(observableJudges);
		judgesTable.refresh(); // 🔥 Ensure table updates
	}
}
