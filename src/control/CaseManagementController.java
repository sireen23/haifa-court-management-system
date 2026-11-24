package control;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import enums.Gender;
import enums.Specialization;
import enums.Status;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import model.Accused;
import model.Case;
import model.CriminalCase;
import model.Document;
import model.FamilyCase;
import model.FinancialCase;
import model.Judge;
import model.Lawyer;
import model.Meeting;
import model.Person;
import model.Testimony;
import model.Verdict;
import model.Witness;

public class CaseManagementController {

	@FXML
	private ComboBox<Integer> accused1, accused2, accused3;
	@FXML
	private ComboBox<Integer> lawyer1, lawyer2, lawyer3;
	@FXML
	private ComboBox<Specialization> type1, type2, type3;
	@FXML
	private ComboBox<Status> status1, status2, status3;
	@FXML
	private ComboBox<Integer> verdict1, verdict2, verdict3;
	@FXML
	private ComboBox<Integer> victim1, victim2;
	@FXML
	private TextField scene, lossAmount, damagedItem, relationType;
	@FXML
	private TextArea crimeTool;
	@FXML
	private TextField accusedName;
	@FXML
	private DatePicker openDate1, openDate2, openDate3;
	@FXML
	private Button addCriminalCase, addFinancialCase, addFamilyCase;
	@FXML
	private Button reset1, reset2, reset3;

	@FXML
	private TableView<Case> casesTable;
	@FXML
	private TableColumn<Case, String> codeColumn;
	@FXML
	private TableColumn<Case, String> typeColumn;
	@FXML
	private TableColumn<Case, String> statusColumn;
	@FXML
	private TableColumn<Case, String> openDateColumn;
	@FXML
	private TableColumn<Case, String> accusedColumn;
	@FXML
	private TableColumn<Case, String> lawyerColumn;

	private Court court;
	@FXML
	private DatePicker date; // Case open date picker

	@FXML
	private CheckBox allCases, criminal, family, financial, inProcess, finished, canceled;
	@FXML
	private Button filter;

	@FXML
	private ImageView search;

	@FXML
	private ComboBox<Specialization> specialization; // Specialization filter
	@FXML
	private Button deleteCase;

	@FXML
	private TextField id, firstName, lastName, address, phoneNumber, email, job;

	@FXML
	private DatePicker birthDate;

	@FXML
	private ComboBox<Gender> gender;

	@FXML
	private Button resetAccused, addAccused;

	@FXML
	private ComboBox<Case> caseCombobox, caseCombobox2;

	@FXML
	private ComboBox<Lawyer> lawyercombobox;

	@FXML
	private ComboBox<Judge> judgeCombobox;

	@FXML
	private Button assignlawyerButton, assignjUDGEButton;

	@FXML
	private ComboBox<Case> caseIDcombobox; // Displays case IDs only

	@FXML
	private TextArea textDetailsArea; // Displays full case details

	@FXML
	private Button displayDetails;

	@FXML
	private ComboBox<String> tab6casescombobox;
	@FXML
	private Button tab6displayButton, tab6Update, tab6Reset;
	@FXML
	private ComboBox<Accused> tab6Accused;
	@FXML
	private ComboBox<Specialization> tab6specialization;
	@FXML
	private ComboBox<Lawyer> tab6lawyer;
	@FXML
	private ComboBox<Verdict> tab6verdict;
	@FXML
	private ComboBox<Status> tab6Status;
	@FXML
	private DatePicker tab6Date;

	// Financial Case Fields
	@FXML
	private AnchorPane financialAnchorpane;
	@FXML
	private TextField tab6financialLossAmount, tab6FinancialDamagedItem;

	// Family Case Fields
	@FXML
	private AnchorPane familyAnchorpane;
	@FXML
	private ComboBox<Person> tab6FamilyVictim;
	@FXML
	private TextField tab6familyRelationType;

	// Criminal Case Fields
	@FXML
	private AnchorPane criminalAnchorpane;
	@FXML
	private ComboBox<Person> tab6CriminalVictim;
	@FXML
	private TextField tab6CriminalCrimeScene;
	@FXML
	private TextArea tab6CriminalCrimeTool;

	private Case selectedCase;

	@FXML
	private ImageView backImage;

	@FXML
	private AnchorPane dynamicFieldsAnchorpane;

	public void initialize() {
		System.out.println("✅ Initializing Case Management Controller...");
		court = Court.getInstance(); // Get the Court instance

		// Step 1: Ensure all people are added to the system
		forceAddAllPeople();

		// Step 2: Load ComboBoxes with necessary data
		loadComboBoxData();

		// Step 3: Configure table columns to display case details properly
		setupTableColumns();

		// Step 4: Load all cases into the table when the page opens
		loadCases();
		loadCases2();
		loadCases3();

		gender.getItems().setAll(Gender.values());

		specialization.getItems().addAll(Specialization.values()); // ✅ Correct
		search.setOnMouseClicked(event -> handleSearchCases());

		// Step 5: Set event listeners for adding cases
		addCriminalCase.setOnAction(event -> handleAddCriminalCase());
		addFinancialCase.setOnAction(event -> handleAddFinancialCase());
		addFamilyCase.setOnAction(event -> handleAddFamilyCase());

		// Step 6: Attach reset actions to buttons
		reset1.setOnAction(event -> resetCriminalCaseForm());
		reset2.setOnAction(event -> resetFinancialCaseForm());
		reset3.setOnAction(event -> resetFamilyCaseForm());

		// Step 7: Attach filtering functionality to filter button
		filter.setOnAction(event -> applyFilters());

		// Set event listeners
		deleteCase.setOnAction(event -> handleDeleteCase());

		addAccused.setOnAction(this::handleAddAccused);
		resetAccused.setOnAction(this::handleResetAccused);

		System.out.println("✅ Case Management Initialized Successfully!");

		loadLawyers();
		loadJudges();

		// Assign button actions
		assignlawyerButton.setOnAction(this::handleAssignLawyer);
		assignjUDGEButton.setOnAction(this::handleAssignJudge);
		displayDetails.setOnAction(this::handleDisplayCaseDetails);

		loadCases4();
		loadDropDowns();

		// Hide all case-specific fields initially
		financialAnchorpane.setVisible(false);
		familyAnchorpane.setVisible(false);
		criminalAnchorpane.setVisible(false);

		// Display button logic
		tab6displayButton.setOnAction(event -> displayCaseDetails());

		// Update button logic
		tab6Update.setOnAction(event -> updateCase());

		// Reset button logic
		tab6Reset.setOnAction(event -> resetForm1());

		backImage.setOnMouseClicked(event -> {
			Utils.switchScene(event, "/view/Employee.fxml", "Employee Page");
		});

	}

	private void loadDropDowns() {
		tab6specialization.setItems(FXCollections.observableArrayList(Specialization.values()));
		tab6Status.setItems(FXCollections.observableArrayList(Status.values()));
		tab6lawyer.setItems(FXCollections.observableArrayList(court.getAllLawyers().values()));
		tab6Accused.setItems(FXCollections.observableArrayList(court.getAllAccuseds().values()));
		tab6verdict.setItems(FXCollections.observableArrayList(court.getAllVerdicts().values()));
	}

	private void loadCases4() {
		List<String> caseCodes = court.getAllCases().values().stream().map(Case::getCode).collect(Collectors.toList());

		tab6casescombobox.setItems(FXCollections.observableArrayList(caseCodes));
	}

	// Display case details based on selected case type
	private void displayCaseDetails() {
		String caseCode = tab6casescombobox.getValue();
		if (caseCode == null) {
			showAlert(Alert.AlertType.ERROR, "No Case Selected", "Please select a case.");
			return;
		}

		selectedCase = court.getAllCases().get(caseCode);
		if (selectedCase == null) {
			showAlert(Alert.AlertType.ERROR, "Invalid Case", "Case not found.");
			return;
		}

		// Set common fields
		tab6Accused.setValue(selectedCase.getAccused());
		tab6Date.setValue(
				selectedCase.getOpenedDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
		tab6Status.setValue(selectedCase.getCaseStatus());
		tab6specialization.setValue(selectedCase.getCaseType());
		tab6lawyer.setValue(selectedCase.getLawyer());
		tab6verdict.setValue(selectedCase.getVerdict());

		// Show the correct form based on case type
		financialAnchorpane.setVisible(selectedCase instanceof FinancialCase);
		familyAnchorpane.setVisible(selectedCase instanceof FamilyCase);
		criminalAnchorpane.setVisible(selectedCase instanceof CriminalCase);

		if (selectedCase instanceof FinancialCase) {
			FinancialCase fc = (FinancialCase) selectedCase;
			tab6financialLossAmount.setText(String.valueOf(fc.getLossesAmount()));
			tab6FinancialDamagedItem.setText(fc.getDamagedItem());
		} else if (selectedCase instanceof FamilyCase) {
			FamilyCase fc = (FamilyCase) selectedCase;
			tab6FamilyVictim.setValue(fc.getVictim());
			tab6familyRelationType.setText(fc.getRelationType());
		} else if (selectedCase instanceof CriminalCase) {
			CriminalCase cc = (CriminalCase) selectedCase;
			tab6CriminalVictim.setValue(cc.getVictim());
			tab6CriminalCrimeScene.setText(cc.getCrimeScene());
			tab6CriminalCrimeTool.setText(cc.getCrimeTool());
		}
	}

	// Update case details
	private void updateCase() {
		if (selectedCase == null) {
			showAlert(Alert.AlertType.ERROR, "No Case Selected", "Please select a case first.");
			return;
		}

		selectedCase.setAccused(tab6Accused.getValue());
		selectedCase.setOpenedDate(
				java.util.Date.from(tab6Date.getValue().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
		selectedCase.setCaseStatus(tab6Status.getValue());
		selectedCase.setCaseType(tab6specialization.getValue());
		selectedCase.setLawyer(tab6lawyer.getValue());
		selectedCase.setVerdict(tab6verdict.getValue());

		if (selectedCase instanceof FinancialCase) {
			FinancialCase fc = (FinancialCase) selectedCase;
			fc.setLossesAmount(Double.parseDouble(tab6financialLossAmount.getText()));
			fc.setDamagedItem(tab6FinancialDamagedItem.getText());
		} else if (selectedCase instanceof FamilyCase) {
			FamilyCase fc = (FamilyCase) selectedCase;
			fc.setVictim(tab6FamilyVictim.getValue());
			fc.setRelationType(tab6familyRelationType.getText());
		} else if (selectedCase instanceof CriminalCase) {
			CriminalCase cc = (CriminalCase) selectedCase;
			cc.setVictim(tab6CriminalVictim.getValue());
			cc.setCrimeScene(tab6CriminalCrimeScene.getText());
			cc.setCrimeTool(tab6CriminalCrimeTool.getText());
		}

		court.save();
		refreshCaseList();
		showAlert(Alert.AlertType.INFORMATION, "Success", "Case updated successfully.");
	}

	// Reset form fields
	private void resetForm1() {
		tab6Accused.setValue(null);
		tab6Date.setValue(null);
		tab6Status.setValue(null);
		tab6specialization.setValue(null);
		tab6lawyer.setValue(null);
		tab6verdict.setValue(null);

		tab6financialLossAmount.clear();
		tab6FinancialDamagedItem.clear();
		tab6FamilyVictim.setValue(null);
		tab6familyRelationType.clear();
		tab6CriminalVictim.setValue(null);
		tab6CriminalCrimeScene.clear();
		tab6CriminalCrimeTool.clear();

		financialAnchorpane.setVisible(false);
		familyAnchorpane.setVisible(false);
		criminalAnchorpane.setVisible(false);
	}

	private void loadCases2() {
		HashSet<Case> cases = new HashSet<>();

		// Retrieve all cases from court and add them to the HashSet
		cases.addAll(court.getAllCases().values());

		// Convert to ObservableList for the ComboBoxes
		ObservableList<Case> caseList = FXCollections.observableArrayList(cases);
		caseCombobox.setItems(caseList); // For lawyer assignment
		caseCombobox2.setItems(caseList); // For judge assignment

		// Set custom StringConverter to display only Case ID in the ComboBox
		caseCombobox.setConverter(new StringConverter<>() {
			@Override
			public String toString(Case caseObj) {
				return (caseObj != null) ? caseObj.getCode() : "";
			}

			@Override
			public Case fromString(String string) {
				return null; // Not needed
			}
		});

		caseCombobox2.setConverter(new StringConverter<>() {
			@Override
			public String toString(Case caseObj) {
				return (caseObj != null) ? caseObj.getCode() : "";
			}

			@Override
			public Case fromString(String string) {
				return null; // Not needed
			}
		});
	}

	private void loadLawyers() {
		List<Lawyer> lawyers = court.getAllLawyers().values().stream().filter(lawyer -> !(lawyer instanceof Judge))
				.toList();

		lawyercombobox.setItems(FXCollections.observableArrayList(lawyers));

		// Display only the lawyer ID in the ComboBox
		lawyercombobox.setConverter(new StringConverter<>() {
			@Override
			public String toString(Lawyer lawyer) {
				return (lawyer != null) ? String.valueOf(lawyer.getId()) : "";
			}

			@Override
			public Lawyer fromString(String string) {
				return null; // Not needed
			}
		});
	}

	private void loadJudges() {
		List<Judge> judges = court.getAllLawyers().values().stream().filter(lawyer -> lawyer instanceof Judge)
				.map(lawyer -> (Judge) lawyer).toList();

		judgeCombobox.setItems(FXCollections.observableArrayList(judges));

		// Display only the judge ID in the ComboBox
		judgeCombobox.setConverter(new StringConverter<>() {
			@Override
			public String toString(Judge judge) {
				return (judge != null) ? String.valueOf(judge.getId()) : "";
			}

			@Override
			public Judge fromString(String string) {
				return null; // Not needed
			}
		});
	}

	// Assign a lawyer to the selected case
	// Assign a lawyer to a case
	@FXML
	private void handleAssignLawyer(ActionEvent event) {
		Case selectedCase = caseCombobox.getValue();
		Lawyer selectedLawyer = lawyercombobox.getValue();

		if (selectedCase == null || selectedLawyer == null) {
			showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a case and a lawyer.");
			return;
		}

		boolean success = court.assignLawyerToCase(selectedCase.getCode(), selectedLawyer.getId());

		if (success) {
			showAlert(Alert.AlertType.INFORMATION, "Success", "Lawyer assigned successfully.");
		} else {
			showAlert(Alert.AlertType.ERROR, "Assignment Failed", "Failed to assign lawyer.");
		}
		Court.save();

	}

	// Assign a judge to a case's verdict
	@FXML
	private void handleAssignJudge(ActionEvent event) {
		Case selectedCase = caseCombobox2.getValue();
		Judge selectedJudge = judgeCombobox.getValue();

		if (selectedCase == null || selectedJudge == null) {
			showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a case and a judge.");
			return;
		}

		boolean success = court.assignJudgeToVerdict(selectedCase.getCode(), selectedJudge.getId());

		if (success) {
			showAlert(Alert.AlertType.INFORMATION, "Success", "Judge assigned successfully.");
		} else {
			showAlert(Alert.AlertType.ERROR, "Assignment Failed", "Failed to assign judge.");
		}
		Court.save();

	}

	// ✅ Load system data into ComboBoxes
	private void loadComboBoxData() {
		System.out.println("🔄 Loading ComboBox Data...");

		// ✅ Load Accused persons (Only ID)
		if (court.getAllAccuseds() != null) {
			List<Integer> accusedList = court.getAllAccuseds().values().stream().map(Accused::getId).toList();
			accused1.getItems().addAll(accusedList);
			accused2.getItems().addAll(accusedList);
			accused3.getItems().addAll(accusedList);
		}

		// ✅ Load Lawyers (Only ID)
		if (court.getAllLawyers() != null) {
			List<Integer> lawyerList = court.getAllLawyers().values().stream().map(Lawyer::getId).toList();
			lawyer1.getItems().addAll(lawyerList);
			lawyer2.getItems().addAll(lawyerList);
			lawyer3.getItems().addAll(lawyerList);
		}

		// ✅ Load Specialization & Status (Enums)
		type1.getItems().addAll(Specialization.values());
		type2.getItems().addAll(Specialization.values());
		type3.getItems().addAll(Specialization.values());

		status1.getItems().addAll(Status.values());
		status2.getItems().addAll(Status.values());
		status3.getItems().addAll(Status.values());

		// ✅ Load Verdicts (Optional)
		if (court.getAllVerdicts() != null) {
			List<Integer> verdictList = court.getAllVerdicts().values().stream().map(Verdict::getVerdictID).toList();
			verdict1.getItems().addAll(verdictList);
			verdict2.getItems().addAll(verdictList);
			verdict3.getItems().addAll(verdictList);
		}

		// ✅ Load Victims (Witnesses & Accused Only)
		if (court.getAllPeople() != null) {
			List<Integer> victimsList = court.getAllPeople().values().stream()
					.filter(p -> p instanceof Witness || p instanceof Accused).map(Person::getId).toList();

			victim1.getItems().addAll(victimsList);
			victim2.getItems().addAll(victimsList);
		}
		System.out.println("✅ ComboBox Data Loaded Successfully!");
	}

	private void handleAddCriminalCase() {
		try {
			// Get user input
			Accused accused = court.getAllAccuseds().get(accused1.getValue());
			LocalDate localDate = openDate1.getValue(); // Get value from DatePicker
			Status caseStatus = status1.getValue();
			Specialization caseType = type1.getValue();
			Lawyer lawyer = court.getAllLawyers().get(lawyer1.getValue());
			Integer victimId = victim1.getValue();
			String crimeSceneValue = scene.getText();
			String crimeToolValue = crimeTool.getText();

			// Convert LocalDate to java.util.Date
			if (localDate == null) {
				showAlert(Alert.AlertType.ERROR, "Validation Error", "Open Date must be selected.");
				return;
			}
			Date openDate = java.util.Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

			// Allow missing verdict
			Verdict verdict = (verdict1.getValue() != null) ? court.getAllVerdicts().get(verdict1.getValue()) : null;

			// Validate fields (EXCLUDING VERDICT)
			if (accused == null || caseStatus == null || caseType == null || lawyer == null || victimId == null
					|| crimeSceneValue.isEmpty() || crimeToolValue.isEmpty()) {
				showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled, except Verdict.");
				return;
			}

			// Get real victim object
			Person victim = court.getRealPerson(victimId);
			if (victim == null) {
				showAlert(Alert.AlertType.ERROR, "Validation Error", "Selected victim does not exist.");
				return;
			}

			// Add case to court
			boolean check = court.addCriminalCase(new CriminalCase(accused, openDate, caseStatus, caseType, lawyer,
					verdict, victim, crimeSceneValue, crimeToolValue));
			if (lawyer.getId() == 78) {
				System.out.println("does the case added to the lawyer ? " + check);
			}

			Court.save();
			showAlert(Alert.AlertType.INFORMATION, "Success", "Criminal Case added successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while adding the case.");
		}
	}

	private void handleAddFinancialCase() {
		try {
			// Get user input
			Accused accused = court.getAllAccuseds().get(accused2.getValue());
			LocalDate localDate = openDate2.getValue(); // Get value from DatePicker
			Status caseStatus = status2.getValue();
			Specialization caseType = type2.getValue();
			Lawyer lawyer = court.getAllLawyers().get(lawyer2.getValue());
			String damagedItemValue = damagedItem.getText();

			// Convert LocalDate to java.util.Date
			if (localDate == null) {
				showAlert(Alert.AlertType.ERROR, "Validation Error", "Open Date must be selected.");
				return;
			}
			Date openDate = java.util.Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

			// Parse Loss Amount
			double lossesAmount;
			try {
				lossesAmount = Double.parseDouble(lossAmount.getText());
			} catch (NumberFormatException e) {
				showAlert(Alert.AlertType.ERROR, "Input Error", "Loss Amount must be a valid number.");
				return;
			}

			// Allow missing verdict
			Verdict verdict = (verdict2.getValue() != null) ? court.getAllVerdicts().get(verdict2.getValue()) : null;

			// Validate fields (EXCLUDING VERDICT)
			if (accused == null || caseStatus == null || caseType == null || lawyer == null || lossesAmount < 0
					|| damagedItemValue.isEmpty()) {
				showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled, except Verdict.");
				return;
			}

			// Add case to court
			court.addFinancialCase(new FinancialCase(accused, openDate, caseStatus, caseType, lawyer, verdict,
					lossesAmount, damagedItemValue));

			showAlert(Alert.AlertType.INFORMATION, "Success", "Financial Case added successfully!");
			Court.save();

		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while adding the case.");
		}
	}

	private void handleAddFamilyCase() {
		try {
			// Get user input
			Accused accused = court.getAllAccuseds().get(accused3.getValue());
			LocalDate localDate = openDate3.getValue(); // Get value from DatePicker
			Status caseStatus = status3.getValue();
			Specialization caseType = type3.getValue();
			Lawyer lawyer = court.getAllLawyers().get(lawyer3.getValue());
			Integer victim = victim2.getValue();
			String relationTypeValue = relationType.getText();

			// Convert LocalDate to java.util.Date
			if (localDate == null) {
				showAlert(Alert.AlertType.ERROR, "Validation Error", "Open Date must be selected.");
				return;
			}
			Date openDate = java.util.Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

			// Allow missing verdict
			Verdict verdict = (verdict3.getValue() != null) ? court.getAllVerdicts().get(verdict3.getValue()) : null;

			// Validate fields (EXCLUDING VERDICT)
			if (accused == null || caseStatus == null || caseType == null || lawyer == null || victim == null
					|| relationTypeValue.isEmpty()) {
				showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled, except Verdict.");
				return;
			}

			// Add case to court
			court.addFamilyCase(new FamilyCase(accused, openDate, caseStatus, caseType, lawyer, verdict,
					court.getRealPerson(victim), relationTypeValue));

			showAlert(Alert.AlertType.INFORMATION, "Success", "Family Case added successfully!");
			Court.save();

		} catch (Exception e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while adding the case.");
		}
	}

	private void showAlert(AlertType type, String title, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void forceAddAllPeople() {
		System.out.println("🔄 Force-Adding All People to allPeople HashMap...");

		if (court.getAllPeople() == null) {
			court.setAllPeople(new HashMap<>());
		}

		// ✅ Add all Lawyers
		court.getAllLawyers().values().forEach(lawyer -> {
			if (!court.getAllPeople().containsKey(lawyer.getId())) {
				court.getAllPeople().put(lawyer.getId(), lawyer);
				System.out.println("🟢 Added Lawyer: " + lawyer.getFirstName() + " " + lawyer.getLastName());
			}
		});

		// ✅ Add all Employees
		court.getAllEmployees().values().forEach(employee -> {
			if (!court.getAllPeople().containsKey(employee.getId())) {
				court.getAllPeople().put(employee.getId(), employee);
				System.out.println("🟢 Added Employee: " + employee.getFirstName() + " " + employee.getLastName());
			}
		});

		// ✅ Add all Witnesses
		court.getAllWitnesses().values().forEach(witness -> {
			if (!court.getAllPeople().containsKey(witness.getId())) {
				court.getAllPeople().put(witness.getId(), witness);
				System.out.println("🟢 Added Witness: " + witness.getFirstName() + " " + witness.getLastName());
			}
		});

		// ✅ Add all Accused
		court.getAllAccuseds().values().forEach(accused -> {
			if (!court.getAllPeople().containsKey(accused.getId())) {
				court.getAllPeople().put(accused.getId(), accused);
				System.out.println("🟢 Added Accused: " + accused.getFirstName() + " " + accused.getLastName());
			}
		});

		System.out.println("✅ Force-Added All People. Total Count: " + court.getAllPeople().size());
	}

	// ✅ Reset Criminal Case Form
	@FXML
	private void resetCriminalCaseForm() {
		accused1.setValue(null);
		openDate1.setValue(null);
		status1.setValue(null);
		type1.setValue(null);
		lawyer1.setValue(null);
		verdict1.setValue(null);
		victim1.setValue(null);
		scene.clear();
		crimeTool.clear();
		System.out.println("🔄 Criminal Case Form Reset!");
	}

	// ✅ Reset Financial Case Form
	@FXML
	private void resetFinancialCaseForm() {
		accused2.setValue(null);
		openDate2.setValue(null);
		status2.setValue(null);
		type2.setValue(null);
		lawyer2.setValue(null);
		verdict2.setValue(null);
		lossAmount.clear();
		damagedItem.clear();
		System.out.println("🔄 Financial Case Form Reset!");
	}

	// ✅ Reset Family Case Form
	@FXML
	private void resetFamilyCaseForm() {
		accused3.setValue(null);
		openDate3.setValue(null);
		status3.setValue(null);
		type3.setValue(null);
		lawyer3.setValue(null);
		verdict3.setValue(null);
		victim2.setValue(null);
		relationType.clear();
		System.out.println("🔄 Family Case Form Reset!");
	}

	// ✅ Configure Table Columns to Extract Data
	private void setupTableColumns() {
		refreshCaseList();
		codeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCode()));
		typeColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getCaseType().toString()));
		statusColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getCaseStatus().toString()));
		openDateColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(cellData.getValue().getOpenedDate().toString()));

		accusedColumn.setCellValueFactory(cellData -> {
			Accused accused = cellData.getValue().getAccused();
			return new SimpleStringProperty(
					accused != null ? accused.getFirstName() + " " + accused.getLastName() : "N/A");
		});

		lawyerColumn.setCellValueFactory(cellData -> {
			Lawyer lawyer = cellData.getValue().getLawyer();
			return new SimpleStringProperty(
					lawyer != null ? lawyer.getFirstName() + " " + lawyer.getLastName() : "N/A");
		});
	}

	// ✅ Load All Cases into TableView
	private void loadCases() {
		casesTable.getItems().clear();
		casesTable.getItems().addAll(court.getAllCases().values());
		System.out.println("✅ Loaded " + casesTable.getItems().size() + " cases.");
	}

	@FXML
	private void applyFilters() {
		// Step 1: Get all cases
		List<Case> allCasesList = court.getAllCases().values().stream().toList();

		// Step 2: Check if "Display All Cases" is selected
		if (allCases.isSelected()) {
			casesTable.getItems().setAll(allCasesList); // Show all cases
			return; // Skip filtering
		}

		// Step 3: Apply Filtering
		List<Case> filteredCases = allCasesList.stream().filter(c -> {
			boolean typeMatch = false;
			boolean statusMatch = false;

			// ✅ Check case type filter
			if (criminal.isSelected() && c instanceof CriminalCase)
				typeMatch = true;
			if (family.isSelected() && c instanceof FamilyCase)
				typeMatch = true;
			if (financial.isSelected() && c instanceof FinancialCase)
				typeMatch = true;

			// If no case type checkbox is selected, default to true (no filtering on type)
			if (!criminal.isSelected() && !family.isSelected() && !financial.isSelected()) {
				typeMatch = true;
			}

			// Check case status filter
			if (inProcess.isSelected() && c.getCaseStatus() == Status.inProcess)
				statusMatch = true;
			if (finished.isSelected() && c.getCaseStatus() == Status.finished)
				statusMatch = true;
			if (canceled.isSelected() && c.getCaseStatus() == Status.canceled)
				statusMatch = true;

			// If no status checkbox is selected, default to true (no filtering on status)
			if (!inProcess.isSelected() && !finished.isSelected() && !canceled.isSelected()) {
				statusMatch = true;
			}

			// Only return cases that match BOTH filters
			return typeMatch && statusMatch;
		}).toList();

		// Step 4: Update TableView
		casesTable.getItems().setAll(filteredCases);
	}

	private void handleSearchCases() {
		// Get search inputs
		String accusedSearch = (accusedName.getText() != null) ? accusedName.getText().trim().toLowerCase() : "";
		LocalDate searchLocalDate = date.getValue(); // Get value from DatePicker
		Specialization selectedSpecialization = specialization.getValue();

		// Convert LocalDate to java.util.Date
		Date searchDate = (searchLocalDate != null)
				? java.util.Date.from(searchLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
				: null;

		// Get all cases from the system
		List<Case> allCases = new ArrayList<>(court.getAllCases().values());

		// Apply filters
		List<Case> filteredCases = allCases.stream()
				.filter(c -> accusedSearch.isEmpty() || (c.getAccused().getFirstName().toLowerCase() + " "
						+ c.getAccused().getLastName().toLowerCase()).contains(accusedSearch))
				.filter(c -> searchDate == null || c.getOpenedDate().compareTo(searchDate) == 0)
				.filter(c -> selectedSpecialization == null || c.getCaseType().equals(selectedSpecialization)).toList();

		// Update the table with filtered results
		casesTable.getItems().setAll(filteredCases);

		System.out.println("🔍 Search completed. Found " + filteredCases.size() + " cases.");
	}

	// ✅ DELETE CASE FUNCTION
	private void handleDeleteCase() {
		Case selectedCase = casesTable.getSelectionModel().getSelectedItem();
		if (selectedCase == null) {
			showAlert(Alert.AlertType.WARNING, "No Case Selected", "Please select a case to delete.");
			return;
		}

		// Confirmation popup
		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Confirm Deletion");
		confirmation.setHeaderText("Are you sure you want to delete this case?");
		confirmation.setContentText("This action cannot be undone.");
		Optional<ButtonType> result = confirmation.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			court.getAllCases().remove(selectedCase.getCode());
			loadCases(); // Refresh table
			showAlert(Alert.AlertType.INFORMATION, "Case Deleted", "The case has been successfully deleted.");
		}
		Court.save();

	}

	// Handle Add Accused Button
	@FXML
	private void handleAddAccused(ActionEvent event) {
		if (validateInput()) {
			String accusedID = id.getText().trim();
			String fName = firstName.getText().trim();
			String lName = lastName.getText().trim();
			LocalDate dob = birthDate.getValue();
			String accusedAddress = address.getText().trim();
			String phone = phoneNumber.getText().trim();
			String emailInput = email.getText().trim();
			String accusedJob = job.getText().trim();
			Gender selectedGender = gender.getValue();

			// Simulating adding to a database or list
			System.out.println("New Accused Added:");
			System.out.println("ID: " + accusedID + ", Name: " + fName + " " + lName + ", DOB: " + dob);
			System.out.println("Address: " + accusedAddress + ", Phone: " + phone + ", Email: " + emailInput);
			System.out.println("Job: " + accusedJob + ", Gender: " + selectedGender);

			// Show success alert
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Success");
			alert.setHeaderText(null);
			alert.setContentText("Accused successfully added!");
			alert.showAndWait();

			Court.save();

			// Reset form after adding
			resetForm();
		}
	}

	// Handle Reset Button
	@FXML
	private void handleResetAccused(ActionEvent event) {
		resetForm();
	}

	// Validate Input Fields
	private boolean validateInput() {
		String errorMessage = "";

		if (id.getText().trim().isEmpty())
			errorMessage += "ID is required.\n";
		if (firstName.getText().trim().isEmpty())
			errorMessage += "First name is required.\n";
		if (lastName.getText().trim().isEmpty())
			errorMessage += "Last name is required.\n";
		if (birthDate.getValue() == null)
			errorMessage += "Birth date is required.\n";
		if (address.getText().trim().isEmpty())
			errorMessage += "Address is required.\n";
		if (phoneNumber.getText().trim().isEmpty())
			errorMessage += "Phone number is required.\n";
		if (email.getText().trim().isEmpty() || !email.getText().contains("@"))
			errorMessage += "Valid email is required.\n";
		if (gender.getValue() == null)
			errorMessage += "Gender selection is required.\n";

		if (!errorMessage.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Invalid Input");
			alert.setHeaderText("Please correct the following errors:");
			alert.setContentText(errorMessage);
			alert.showAndWait();
			return false;
		}
		return true;
	}

	// Handle button click to display case details
	@FXML
	private void handleDisplayCaseDetails(ActionEvent event) {
		Case selectedCase = caseIDcombobox.getValue();
		if (selectedCase == null) {
			showAlert(Alert.AlertType.ERROR, "Selection Error", "Please select a case.");
			return;
		}

		// Generate case details string
		String caseDetails = generateCaseDetails(selectedCase);
		textDetailsArea.setText(caseDetails);
	}

	private void loadCases3() {
		HashSet<Case> cases = new HashSet<>(court.getAllCases().values()); // Flatten cases from HashMap

		ObservableList<Case> caseList = FXCollections.observableArrayList(cases);
		caseIDcombobox.setItems(caseList);

		// Ensure ComboBox displays only case ID
		caseIDcombobox.setConverter(new StringConverter<>() {
			@Override
			public String toString(Case caseObj) {
				return (caseObj != null) ? caseObj.getCode() : ""; // Display case ID only
			}

			@Override
			public Case fromString(String string) {
				return null; // Not needed
			}
		});
	}

	// Generate detailed information about the selected case
	private String generateCaseDetails(Case selectedCase) {
		StringBuilder details = new StringBuilder();

		details.append("📌 **Case ID:** ").append(selectedCase.getCode()).append("\n").append("📅 **Opened Date:** ")
				.append(selectedCase.getOpenedDate()).append("\n").append("⚖️ **Status:** ")
				.append(selectedCase.getCaseStatus()).append("\n").append("📂 **Type:** ")
				.append(selectedCase.getCaseType()).append("\n").append("👨‍⚖️ **Lawyer Assigned:** ")
				.append(selectedCase.getLawyer() != null ? selectedCase.getLawyer().getId() : "None").append("\n");

		// Display meetings
		details.append("\n📅 **Meetings:**\n");
		if (!selectedCase.getMeetingsList().isEmpty()) {
			for (Meeting meeting : selectedCase.getMeetingsList()) {
				details.append("- ").append(meeting).append("\n");
			}
		} else {
			details.append("None\n");
		}

		// Display documents
		details.append("\n📄 **Documents:**\n");
		if (!selectedCase.getDocumentsList().isEmpty()) {
			for (Document doc : selectedCase.getDocumentsList()) {
				details.append("- ").append(doc).append("\n");
			}
		} else {
			details.append("None\n");
		}

		// Display testimonies
		details.append("\n🗣️ **Testimonies:**\n");
		if (!selectedCase.gettestimoniesList().isEmpty()) {
			for (Testimony testimony : selectedCase.gettestimoniesList()) {
				details.append("- ").append(testimony).append("\n");
			}
		} else {
			details.append("None\n");
		}

		// Display verdict (if available)
		if (selectedCase.getVerdict() != null) {
			details.append("\n⚖️ **Verdict:** ").append(selectedCase.getVerdict()).append("\n");
		}

		// Check if the case is a Criminal, Family, or Financial case
		if (selectedCase instanceof CriminalCase) {
			CriminalCase criminalCase = (CriminalCase) selectedCase;
			details.append("\n🚔 **Criminal Case Details:**\n").append("👤 **Victim:** ")
					.append(criminalCase.getVictim()).append("\n").append("📍 **Crime Scene:** ")
					.append(criminalCase.getCrimeScene()).append("\n").append("🔧 **Crime Tool:** ")
					.append(criminalCase.getCrimeTool()).append("\n");
		}

		if (selectedCase instanceof FamilyCase) {
			FamilyCase familyCase = (FamilyCase) selectedCase;
			details.append("\n👨‍👩‍👧 **Family Case Details:**\n").append("👤 **Victim:** ")
					.append(familyCase.getVictim()).append("\n").append("🔗 **Relation Type:** ")
					.append(familyCase.getRelationType()).append("\n");
		}

		if (selectedCase instanceof FinancialCase) {
			FinancialCase financialCase = (FinancialCase) selectedCase;
			details.append("\n💰 **Financial Case Details:**\n").append("💸 **Losses Amount:** ")
					.append(financialCase.getLossesAmount()).append("\n").append("🛠️ **Damaged Item:** ")
					.append(financialCase.getDamagedItem()).append("\n");
		}

		return details.toString();
	}

	// Reset Form Fields
	private void resetForm() {
		id.clear();
		firstName.clear();
		lastName.clear();
		birthDate.setValue(null);
		address.clear();
		phoneNumber.clear();
		email.clear();
		job.clear();
		gender.getSelectionModel().clearSelection();
	}

	private void refreshCaseList() {
		List<String> caseCodes = court.getAllCases().values().stream().map(Case::getCode).collect(Collectors.toList());

		tab6casescombobox.setItems(FXCollections.observableArrayList(caseCodes));
	}

}
