package control;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import enums.Specialization;
import enums.Status;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import model.Accused;
import model.Case;
import model.CriminalCase;
import model.FamilyCase;
import model.FinancialCase;
import model.Lawyer;
import model.Testimony;

public class LawyerManageCases {

	@FXML
	private TableView<Case> casesTable1;

	@FXML
	private TableColumn<Case, String> codeColumn1;

	@FXML
	private TableColumn<Case, String> typeColumn1;

	@FXML
	private TableColumn<Case, String> statusColumn1;

	@FXML
	private TableColumn<Case, String> openDateColumn1;

	@FXML
	private TableColumn<Case, String> accusedColumn1;

	@FXML
	private CheckBox allCases1, criminal1, family1, financial1;
	@FXML
	private CheckBox inProcess1, finished1, canceled1;
	@FXML
	private TextField accusedName1;
	@FXML
	private DatePicker date1;
	@FXML
	private ComboBox<Specialization> specialization1;
	@FXML
	private ImageView search;
	@FXML
	private Button filter1;

	private Lawyer loggedInLawyer;
	private HashSet<Case> allCases; // Store all cases for filtering

	// Second Tab Components
	@FXML
	private ComboBox<String> casesIds; // Displays lawyer's case IDs
	@FXML
	private Button displayAccusedButton;
	@FXML
	private TextArea accusedTextArea;

	// ✅ Third Tab Components (Testimonies)
	@FXML
	private ComboBox<String> casesOfTestimoniesTab;
	@FXML
	private Button testimoniesDisplayButton;
	@FXML
	private TableView<Testimony> testimoniesTable;
	@FXML
	private TableColumn<Testimony, Integer> testimonyID;
	@FXML
	private TableColumn<Testimony, String> content;
	@FXML
	private TableColumn<Testimony, Integer> witnessID;
	@FXML
	private TableColumn<Testimony, String> witnessFullName;

	@FXML
	private ImageView backImage;

	/** ✅ Called automatically when FXML is loaded */
	@FXML
	public void initialize() {
		System.out.println("⚡ LawyerManageCases Initialized");
		specialization1.getItems().setAll(Specialization.values());

		// Attach event to filter button
		filter1.setOnAction(event -> applyFilters());
		search.setOnMouseClicked(event -> applySearch());

		// Attach event to accused display button
		displayAccusedButton.setOnAction(event -> displayAccusedDetails());

		testimoniesDisplayButton.setOnAction(event -> displayTestimonies());

		// Initialize Testimonies Table
		initializeTestimoniesTable();

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Lawyer.fxml", "Lawyer Page");
		});

	}

	@FXML
	private void applySearch() {
		if (loggedInLawyer == null)
			return; // Ensure judge is logged in

		// ✅ Get ONLY the cases assigned to THIS judge
		List<Case> judgeCasesList = new ArrayList<>(loggedInLawyer.getCasesHandled());

		// ✅ Get search values
		String accusedFilter = accusedName1.getText().trim().toLowerCase();
		Specialization selectedSpecialization = specialization1.getValue();
		LocalDate selectedDate = date1.getValue();

		// ✅ Apply Filtering
		List<Case> filteredCases = judgeCasesList.stream().filter(c -> {
			boolean matchesAccused = accusedFilter.isEmpty()
					|| c.getAccused().getFullName().toLowerCase().contains(accusedFilter);
			boolean matchesSpecialization = (selectedSpecialization == null)
					|| c.getCaseType() == selectedSpecialization;
			boolean matchesDate = (selectedDate == null)
					|| c.getOpenedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(selectedDate);

			return matchesAccused && matchesSpecialization && matchesDate;
		}).toList();

		// ✅ Update TableView with filtered results
		casesTable1.getItems().setAll(filteredCases);
	}

	/** ✅ Set the logged-in lawyer */
	public void setLawyer(Lawyer lawyer) {
		System.out
				.println("we are in LawyerManageCases setLawyer method and the loggedInLawyer is : " + lawyer.getId());

		this.loggedInLawyer = lawyer;

		if (this.loggedInLawyer == null) {
			System.err.println("⚠️ ERROR: Lawyer is NULL in setLawyer()!");
		} else {
			System.out.println("✅ Lawyer Set: " + loggedInLawyer.getFullName());
			loadCases();
			loadCasesIntoComboBox(); // ✅ Load cases into ComboBox for accused tab
			loadCasesForTestimoniesTab(); // ✅ Load cases into Testimonies Tab ComboBox
		}
	}

	private void loadCasesIntoComboBox() {
		List<String> caseIdsList = new ArrayList<>();
		for (Case c : loggedInLawyer.getCasesHandled()) {
			caseIdsList.add(c.getCode()); // Add case ID to the list
		}

		ObservableList<String> observableCaseIds = FXCollections.observableArrayList(caseIdsList);
		casesIds.setItems(observableCaseIds);
	}

	/** ✅ Display accused details when the button is clicked */
	private void displayAccusedDetails() {
		String selectedCaseId = casesIds.getValue(); // Get selected case ID

		if (selectedCaseId == null) {
			accusedTextArea.setText("⚠️ Please select a case to view accused details.");
			return;
		}

		// Find the selected case
		Case selectedCase = loggedInLawyer.getCasesHandled().stream().filter(c -> c.getCode().equals(selectedCaseId))
				.findFirst().orElse(null);

		if (selectedCase == null || selectedCase.getAccused() == null) {
			accusedTextArea.setText("❌ No accused found for the selected case.");
			return;
		}

		// ✅ Format accused details
		Accused accused = selectedCase.getAccused();
		String accusedDetails = String.format(
				"🔹 Accused ID: %d\n" + "🔹 Name: %s %s\n" + "🔹 Birth Date: %s\n" + "🔹 Address: %s\n"
						+ "🔹 Phone Number: %s\n" + "🔹 Email: %s\n" + "🔹 Gender: %s\n" + "🔹 Job: %s\n",
				accused.getId(), accused.getFirstName(), accused.getLastName(), accused.getBirthDate(),
				accused.getAddress(), accused.getPhoneNumber(), accused.getEmail(), accused.getGender(),
				accused.getJob());

		// ✅ Display formatted details in the text area
		accusedTextArea.setText(accusedDetails);
	}

	/** ✅ Load all lawyer cases into the TableView */
	private void loadCases() {
		allCases = new HashSet<>(loggedInLawyer.getCasesHandled()); // Store all cases

		// Set up table columns
		codeColumn1.setCellValueFactory(new PropertyValueFactory<>("code"));
		typeColumn1.setCellValueFactory(new PropertyValueFactory<>("caseType"));
		statusColumn1.setCellValueFactory(new PropertyValueFactory<>("caseStatus"));
		openDateColumn1.setCellValueFactory(new PropertyValueFactory<>("openedDate"));
		accusedColumn1.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
				String.valueOf(cellData.getValue().getAccused().getFullName()) // Convert int to String
		));

		// Add all cases initially
		casesTable1.getItems().setAll(allCases);
	}

	/** ✅ Apply filtering based on checkboxes */
	@FXML
	private void applyFilters() {
		// Step 1: Get all cases assigned to the lawyer
		List<Case> allCasesList = new ArrayList<>(loggedInLawyer.getCasesHandled());

		// Step 2: Check if "Display All Cases" is selected
		if (allCases1.isSelected()) {
			casesTable1.getItems().setAll(allCasesList); // Show all cases
			return; // Skip filtering
		}

		// Step 3: Apply Filtering
		List<Case> filteredCases = allCasesList.stream().filter(c -> {
			boolean typeMatch = false;
			boolean statusMatch = false;

			// ✅ Check case type filter
			if (criminal1.isSelected() && c instanceof CriminalCase)
				typeMatch = true;
			if (family1.isSelected() && c instanceof FamilyCase)
				typeMatch = true;
			if (financial1.isSelected() && c instanceof FinancialCase)
				typeMatch = true;

			// If no case type checkbox is selected, default to true (no filtering on type)
			if (!criminal1.isSelected() && !family1.isSelected() && !financial1.isSelected()) {
				typeMatch = true;
			}

			// ✅ Check case status filter
			if (inProcess1.isSelected() && c.getCaseStatus() == Status.inProcess)
				statusMatch = true;
			if (finished1.isSelected() && c.getCaseStatus() == Status.finished)
				statusMatch = true;
			if (canceled1.isSelected() && c.getCaseStatus() == Status.canceled)
				statusMatch = true;

			// If no status checkbox is selected, default to true (no filtering on status)
			if (!inProcess1.isSelected() && !finished1.isSelected() && !canceled1.isSelected()) {
				statusMatch = true;
			}

			// Only return cases that match BOTH filters
			return typeMatch && statusMatch;
		}).toList();

		// Step 4: Update TableView
		casesTable1.getItems().setAll(filteredCases);
	}

	/** ✅ Load cases into the Testimonies Tab ComboBox */
	private void loadCasesForTestimoniesTab() {
		List<String> caseIdsList = new ArrayList<>();
		for (Case c : loggedInLawyer.getCasesHandled()) {
			caseIdsList.add(c.getCode()); // Add case ID
		}

		ObservableList<String> observableCaseIds = FXCollections.observableArrayList(caseIdsList);
		casesOfTestimoniesTab.setItems(observableCaseIds);
	}

	/** ✅ Initialize the Testimonies Table */
	private void initializeTestimoniesTable() {
		testimonyID.setCellValueFactory(new PropertyValueFactory<>("testimonyID"));
		content.setCellValueFactory(new PropertyValueFactory<>("testimonyContent"));
		witnessID.setCellValueFactory(
				cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getWitness().getId())
						.asObject());
		witnessFullName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
				cellData.getValue().getWitness().getFirstName() + " "
						+ cellData.getValue().getWitness().getLastName()));
	}

	/** ✅ Display testimonies when the button is clicked */
	private void displayTestimonies() {
		if (casesOfTestimoniesTab == null || testimoniesTable == null) {
			System.err.println("❌ ERROR: UI Components not initialized properly.");
			return;
		}

		String selectedCaseId = casesOfTestimoniesTab.getValue(); // Get selected case ID

		if (selectedCaseId == null) {
			System.err.println("⚠️ Please select a case to display testimonies.");
			return;
		}

		// Find the selected case
		Case selectedCase = loggedInLawyer.getCasesHandled().stream().filter(c -> c.getCode().equals(selectedCaseId))
				.findFirst().orElse(null);

		if (selectedCase == null) {
			System.err.println("❌ No testimonies found for the selected case.");
			testimoniesTable.getItems().clear();
			return;
		}

		// ✅ Get testimonies of the selected case
		Set<Testimony> testimonies = selectedCase.gettestimoniesList();

		if (testimonies == null || testimonies.isEmpty()) {
			System.err.println("⚠️ No testimonies available for the selected case.");
			testimoniesTable.getItems().clear();
			return;
		}

		// ✅ Display testimonies in the table
		testimoniesTable.getItems().setAll(testimonies);
	}

}
