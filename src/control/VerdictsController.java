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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import model.Verdict;

public class VerdictsController {

	@FXML
	private TableView<Verdict> verdictsTable;

	@FXML
	private TableColumn<Verdict, Integer> verdictID;

	@FXML
	private TableColumn<Verdict, String> summary;

	@FXML
	private TableColumn<Verdict, String> issuedDate;

	@FXML
	private TableColumn<Verdict, Integer> judgeID;

	@FXML
	private TableColumn<Verdict, String> caseID; // ✅ Fixed Type (String)

	@FXML
	private TableColumn<Verdict, Integer> appealID;

	@FXML
	private ImageView backImage;

	@FXML
	public void initialize() {
		System.out.println("✅ VerdictsController Initialized");

		// ✅ Ensure TableView is properly set up
		setupTableColumns();

		// ✅ Load verdict data into the table AFTER UI is ready
		Platform.runLater(this::loadVerdictsData);

		// ✅ Handle back button click
		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void setupTableColumns() {
		// ✅ Match table columns to Verdict class properties
		verdictID.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getVerdictID()));

		// ✅ Fix: Proper text wrapping for Summary
		summary.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVerdictSummary()));
		summary.setCellFactory(column -> new TableCell<Verdict, String>() {
			private final Text text = new Text();

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					text.setText(item);
					text.setWrappingWidth(230); // ✅ Ensures text wraps properly
					text.setStyle("-fx-font-size: 13px;");
					setGraphic(text);
				}
			}
		});

		// ✅ Format Issued Date as "DD/MM/YYYY"
		issuedDate.setCellValueFactory(cellData -> {
			return new SimpleStringProperty(formatDate(cellData.getValue().getIssusedDate()));
		});

		// ✅ Get Judge ID (Handle null)
		judgeID.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
				(cellData.getValue().getJudge() != null) ? cellData.getValue().getJudge().getId() : -1));

		// ✅ Get Case ID (Handle null) (🔧 FIXED!)
		caseID.setCellValueFactory(cellData -> new SimpleStringProperty(
				(cellData.getValue().getCasee() != null) ? cellData.getValue().getCasee().getCode() : "N/A"));

		// ✅ Get Appeal ID (Handle null)
		appealID.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
				(cellData.getValue().getAppeal() != null) ? cellData.getValue().getAppeal().getAppealID() : -1));
	}

	private String formatDate(java.util.Date date) {
		if (date == null)
			return "N/A";
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(date);
	}

	private void loadVerdictsData() {
		Court court = Court.getInstance(); // Assuming Court is a Singleton

		// ✅ Retrieve all verdicts
		List<Verdict> verdictList = new ArrayList<>(court.getAllVerdicts().values());

		// ✅ Convert List to ObservableList
		ObservableList<Verdict> observableVerdicts = FXCollections.observableArrayList(verdictList);

		if (observableVerdicts.isEmpty()) {
			System.out.println("❌ No verdicts found!");
		} else {
			System.out.println("✅ Loaded " + observableVerdicts.size() + " verdicts into the table.");
		}

		// ✅ Set data and refresh table
		verdictsTable.setItems(observableVerdicts);
		verdictsTable.refresh(); // 🔥 Ensure table updates
	}
}
