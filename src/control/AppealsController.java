package control;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import model.Appeal;

public class AppealsController {

	@FXML
	private TableView<Appeal> tableAcuusedes;

	@FXML
	private TableColumn<Appeal, Integer> id;

	@FXML
	private TableColumn<Appeal, String> summary;

	@FXML
	private TableColumn<Appeal, String> appealDate;

	@FXML
	private TableColumn<Appeal, Integer> currentVerdictID;

	@FXML
	private TableColumn<Appeal, Integer> newVerdictID;

	@FXML
	private ImageView backImage;

	@FXML
	public void initialize() {
		System.out.println("✅ AppealsController Initialized");

		// Ensure the TableView is properly set up
		setupTableColumns();

		// ✅ Load appeals data into the table AFTER the UI is ready
		Platform.runLater(this::loadAppealsData);

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Employee Page");
		});
	}

	private void setupTableColumns() {
		// ✅ Match table columns to Appeal class properties
		id.setCellValueFactory(new PropertyValueFactory<>("appealID"));
		summary.setCellValueFactory(new PropertyValueFactory<>("appealSummary"));

		// ✅ FIX: Proper text wrapping for summary (No extra space)
		summary.setCellFactory(column -> new TableCell<Appeal, String>() {
			private final Text text = new Text();

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					text.setText(item);
					text.setWrappingWidth(200); // ✅ Adjust width for text wrapping (No extra space!)
					setGraphic(text);
				}
			}
		});

		// ✅ Format Appeal Date as "DD/MM/YYYY"
		appealDate.setCellValueFactory(cellData -> {
			Date date = cellData.getValue().getAppealDate();
			return new SimpleStringProperty((date != null) ? formatDate(date) : "N/A");
		});

		// ✅ Use `SimpleObjectProperty<Integer>` for verdict IDs (Fix type mismatch)
		currentVerdictID.setCellValueFactory(
				cellData -> new SimpleObjectProperty<>(cellData.getValue().getCurrentVerdict().getVerdictID()));

		newVerdictID.setCellValueFactory(
				cellData -> new SimpleObjectProperty<>(cellData.getValue().getNewVerdict().getVerdictID()));

		// ✅ Handle back button click
		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	// ✅ Formats Date into "DD/MM/YYYY"
	private String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(date);
	}

	private void loadAppealsData() {
		Court court = Court.getInstance(); // Assuming Court is a Singleton
		ObservableList<Appeal> appealsList = FXCollections.observableArrayList(court.getAllAppeals().values());

		if (appealsList.isEmpty()) {
			System.out.println("❌ No appeals found!");
		} else {
			System.out.println("✅ Loaded " + appealsList.size() + " appeals into the table.");
		}

		// ✅ Ensure the table refreshes properly
		tableAcuusedes.setItems(appealsList);
		tableAcuusedes.refresh(); // 🔥 Force refresh to show all rows
	}

	private void goBack() {
		System.out.println("🔙 Going back to the previous page...");
		// Implement scene switching logic here
	}
}
