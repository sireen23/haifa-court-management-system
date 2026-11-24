package control;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminController {

	@FXML
	private Label addID, displayDataVBOX;

	@FXML
	private Label removeID;

	@FXML
	private VBox addVBOX, datVBOX;

	@FXML
	private VBox removeVBOX;

	@FXML
	private Label addJudgeLabel;

	@FXML
	private Label addLawyerLabel;

	@FXML
	private Label addEmployee;

	@FXML
	private Label removeEmployee;

	@FXML
	private Label removeLawyer;

	@FXML
	private Label removeJudge;

	@FXML
	private Label accused, appeals, familyCases, courtrooms, departments, documents, employees, judges, lawyers,
			meetings, testimonies, verdicts, witnesses, financialCases, criminalCases;

	@FXML
	private ImageView logout;

	public void initialize() {
		System.out.println("Admin Panel Loaded.");

		// Initially hide both vertical menus
		addVBOX.setVisible(false);
		removeVBOX.setVisible(false);
		datVBOX.setVisible(false);

		// ADD Menu Hover Logic
		addID.setOnMouseEntered(event -> addVBOX.setVisible(true)); // Show menu on hover
		addVBOX.setOnMouseEntered(event -> addVBOX.setVisible(true)); // Keep it open when inside
		addID.setOnMouseExited(event -> hideAddMenu()); // Hide when leaving label
		addVBOX.setOnMouseExited(event -> hideAddMenu()); // Hide when leaving menu

		// REMOVE Menu Hover Logic
		removeID.setOnMouseEntered(event -> removeVBOX.setVisible(true)); // Show menu on hover
		removeVBOX.setOnMouseEntered(event -> removeVBOX.setVisible(true)); // Keep it open when inside
		removeID.setOnMouseExited(event -> hideRemoveMenu()); // Hide when leaving label
		removeVBOX.setOnMouseExited(event -> hideRemoveMenu()); // Hide when leaving menu

		displayDataVBOX.setOnMouseEntered(event -> datVBOX.setVisible(true)); // Show menu on hover
		datVBOX.setOnMouseEntered(event -> datVBOX.setVisible(true)); // Keep it open when inside
		displayDataVBOX.setOnMouseExited(event -> hideDisplayMenu()); // Hide when leaving label
		datVBOX.setOnMouseExited(event -> hideDisplayMenu()); // Hide when leaving menu

		// ✅ Handle clicking "Add Judge"
		addJudgeLabel.setOnMouseClicked(this::openAddJudgeForm);

		// ✅ Handle clicking "Add Lawyer"
		addLawyerLabel.setOnMouseClicked(this::openAddLawyerForm);

		addEmployee.setOnMouseClicked(this::openAddEmployeeForm);
		removeEmployee.setOnMouseClicked(this::openRemoveEmployeeForm);
		removeLawyer.setOnMouseClicked(this::openRemoveLawyerForm);
		removeJudge.setOnMouseClicked(this::openRemoveJudgeForm);
		logout.setOnMouseClicked(this::logout);

		accused.setOnMouseClicked(event -> openPage(event, "/view/Accuseds.fxml", "Accused Information"));
		appeals.setOnMouseClicked(event -> openPage(event, "/view/Appeals.fxml", "Appeals Information"));
		familyCases.setOnMouseClicked(event -> openPage(event, "/view/FamilyCases.fxml", "Family Cases Information"));
		financialCases
				.setOnMouseClicked(event -> openPage(event, "/view/FinancialCase.fxml", "Financial Cases Information"));
		criminalCases
				.setOnMouseClicked(event -> openPage(event, "/view/CriminalCases.fxml", "Criminal Cases Information"));

		courtrooms.setOnMouseClicked(event -> openPage(event, "/view/Courtrooms.fxml", "Courtrooms Information"));
		departments.setOnMouseClicked(event -> openPage(event, "/view/Departments.fxml", "Departments Information"));
		documents.setOnMouseClicked(event -> openPage(event, "/view/Documents.fxml", "Documents Information"));
		employees.setOnMouseClicked(event -> openPage(event, "/view/Employees.fxml", "Employees Information"));
		lawyers.setOnMouseClicked(event -> openPage(event, "/view/Lawyers.fxml", "Lawyers Information"));
		judges.setOnMouseClicked(event -> openPage(event, "/view/Judges.fxml", "Judges Information"));
		meetings.setOnMouseClicked(event -> openPage(event, "/view/Meetings.fxml", "Meetings Information"));
		testimonies.setOnMouseClicked(event -> openPage(event, "/view/Testimonies.fxml", "Testimonies Information"));
		verdicts.setOnMouseClicked(event -> openPage(event, "/view/Verdicts.fxml", "Verdicts Information"));
		witnesses.setOnMouseClicked(event -> openPage(event, "/view/Witnesses.fxml", "Witnesses Information"));
	}

	private void openPage(MouseEvent event, String fxmlFile, String title) {
		Utils.switchScene(event, fxmlFile, title);
	}

	public void logout(MouseEvent event) {
		try {
			// Show logout success message
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Logout");
			alert.setHeaderText(null);
			alert.setContentText("Logout done successfully!");
			alert.showAndWait(); // Wait for user to acknowledge before proceeding

			// Close current window
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();

			// Load main.fxml
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
			Parent root = loader.load();

			// Set up new stage
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setTitle("Main Page");
			stage.show();

		} catch (IOException e) {
			e.printStackTrace(); // Handle error properly
		}
	}

	private void hideAddMenu() {
		addVBOX.setVisible(false);
	}

	private void hideRemoveMenu() {
		removeVBOX.setVisible(false);
	}

	private void hideDisplayMenu() {
		datVBOX.setVisible(false);
	}

	private void openRemoveJudgeForm(MouseEvent event) {
		openForm(event, "/view/RemoveJudge.fxml", "Remove Judge");
	}

	private void openRemoveLawyerForm(MouseEvent event) {
		openForm(event, "/view/RemoveLawyer.fxml", "Remove Lawyer");
	}

	private void openAddJudgeForm(MouseEvent event) {
		openForm(event, "/view/AddJudge.fxml", "Add Judge");
	}

	// ✅ Open Add Lawyer Form
	private void openAddLawyerForm(MouseEvent event) {
		openForm(event, "/view/AddLawyer.fxml", "Add Lawyer");
	}

	private void openAddEmployeeForm(MouseEvent event) {
		openForm(event, "/view/AddEmployee.fxml", "Add Employee");
	}

	private void openRemoveEmployeeForm(MouseEvent event) {
		openForm(event, "/view/RemoveEmployee.fxml", "Remove Employee");
	}

	// ✅ Generic Method to Open Any Form
	private void openForm(MouseEvent event, String fxmlPath, String title) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
			AnchorPane page = loader.load();

			// Close current admin window
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.close();

			// Open new form
			Stage newStage = new Stage();
			newStage.setScene(new Scene(page));
			newStage.setTitle(title);
			newStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
