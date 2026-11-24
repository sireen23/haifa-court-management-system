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
import model.Testimony;

public class TestimoniesController {

	@FXML
	private TableView<Testimony> testimoniesTable;

	@FXML
	private TableColumn<Testimony, Integer> testimonyID;

	@FXML
	private TableColumn<Testimony, String> caseID;

	@FXML
	private TableColumn<Testimony, String> content;

	@FXML
	private TableColumn<Testimony, Integer> witnessID;

	@FXML
	private ImageView backImage;

	@FXML
	public void initialize() {
		System.out.println("✅ TestimoniesController Initialized");

		// ✅ Ensure TableView is properly set up
		setupTableColumns();

		// ✅ Load testimonies data AFTER UI is ready
		Platform.runLater(this::loadTestimoniesData);

		// ✅ Set back button click event
		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void setupTableColumns() {
		testimonyID.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTestimonyID()));

		// ✅ Display Case ID (with null check)
		caseID.setCellValueFactory(cellData -> new SimpleStringProperty(
				(cellData.getValue().getCasee() != null) ? cellData.getValue().getCasee().getCode() : "N/A"));

		// ✅ Fix: Proper text wrapping for testimony content
		content.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTestimonyContent()));
		content.setCellFactory(column -> new TableCell<Testimony, String>() {
			private final Text text = new Text();

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					text.setText(item);
					text.setWrappingWidth(250); // ✅ Ensures proper text wrapping
					text.setStyle("-fx-font-size: 13px;"); // ✅ Adjust font size if needed
					setGraphic(text);
				}
			}
		});

		// ✅ Display Witness ID (with null check)
		witnessID.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
				(cellData.getValue().getWitness() != null) ? cellData.getValue().getWitness().getId() : -1));
	}

	private void loadTestimoniesData() {
		Court court = Court.getInstance(); // Assuming Court is a Singleton

		List<Testimony> testimonyList = new ArrayList<>(court.getAllTestimonies().values());

		System.out.println("✅ Found " + testimonyList.size() + " testimonies.");

		for (Testimony t : testimonyList) {
			System.out.println("📝 Testimony ID: " + t.getTestimonyID() + " | Content: " + t.getTestimonyContent());
		}

		// ✅ Convert List to ObservableList
		ObservableList<Testimony> observableTestimonies = FXCollections.observableArrayList(testimonyList);

		// ✅ Check if it's empty
		if (observableTestimonies.isEmpty()) {
			System.out.println("❌ No testimonies found!");
		} else {
			System.out.println("✅ Loaded " + observableTestimonies.size() + " testimonies into the table.");
		}

		// ✅ Set data and refresh table
		testimoniesTable.setItems(observableTestimonies);
		testimoniesTable.refresh(); // 🔥 Ensure table updates
	}

}
