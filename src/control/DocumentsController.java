package control;

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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import model.Document;

public class DocumentsController {

	@FXML
	private TableView<Document> documentsTable;

	@FXML
	private TableColumn<Document, Integer> code;

	@FXML
	private TableColumn<Document, String> title;

	@FXML
	private TableColumn<Document, String> content;

	@FXML
	private TableColumn<Document, String> issuedDate;

	@FXML
	private TableColumn<Document, String> caseID;

	@FXML
	private ImageView backImage;

	@FXML
	public void initialize() {
		System.out.println("✅ DocumentsController Initialized");

		// ✅ Ensure the TableView is properly set up
		setupTableColumns();

		// ✅ Load documents data into the table AFTER the UI is ready
		Platform.runLater(this::loadDocumentsData);

		// ✅ Set back button click event
		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Admin.fxml", "Admin Page");
		});
	}

	private void setupTableColumns() {
		// ✅ Match table columns to Document class properties
		code.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCode()));

		title.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));

		// ✅ Proper Text Wrapping for Content Column
		content.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContent()));

		content.setCellFactory(column -> new TableCell<Document, String>() {
			private final Text text = new Text();

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null || item.trim().isEmpty()) {
					setGraphic(null);
				} else {
					text.setText(item);
					text.setWrappingWidth(160); // ✅ Ensures text wraps properly
					text.setStyle("-fx-font-size: 14px;"); // ✅ Adjust text size if needed
					setGraphic(text);
				}
			}
		});

		// ✅ Format Issued Date as "DD/MM/YYYY"
		issuedDate.setCellValueFactory(cellData -> {
			Date date = cellData.getValue().getIssusedDate();
			return new SimpleStringProperty((date != null) ? formatDate(date) : "N/A");
		});

		// ✅ Display Case ID
		caseID.setCellValueFactory(cellData -> new SimpleStringProperty(
				(cellData.getValue().getCasee() != null) ? cellData.getValue().getCasee().getCode() : "N/A"));
	}

	private String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		return formatter.format(date);
	}

	private void loadDocumentsData() {
		Court court = Court.getInstance(); // Assuming Court is a Singleton

		// ✅ Retrieve all documents
		List<Document> documentList = new ArrayList<>(court.getAllDocuments().values());

		// ✅ Convert List to ObservableList
		ObservableList<Document> observableDocuments = FXCollections.observableArrayList(documentList);

		if (observableDocuments.isEmpty()) {
			System.out.println("❌ No documents found!");
		} else {
			System.out.println("✅ Loaded " + observableDocuments.size() + " documents into the table.");
		}

		// ✅ Set data and refresh table
		documentsTable.setItems(observableDocuments);
		documentsTable.refresh(); // 🔥 Ensure table updates
	}
}
