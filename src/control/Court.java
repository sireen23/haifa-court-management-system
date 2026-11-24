package control;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import enums.Gender;
import enums.Position;
import enums.Status;
import model.Accused;
import model.Appeal;
import model.Case;
import model.Courtroom;
import model.CriminalCase;
import model.Department;
import model.Document;
import model.Employee;
import model.FamilyCase;
import model.FinancialCase;
import model.Judge;
import model.Lawyer;
import model.Meeting;
import model.Person;
import model.Testimony;
import model.User;
import model.Verdict;
import model.Witness;
import utils.UtilsMethods;

public class Court implements Serializable {

	private static Court instance;
	private HashMap<Integer, Lawyer> allLawyers;
	private HashMap<Integer, Accused> allAccuseds;
	private HashMap<Integer, Employee> allEmployees;
	private HashMap<Integer, Witness> allWitnesses;
	private HashMap<Integer, Testimony> allTestimonies;
	private HashMap<Integer, Department> alldepartments;
	private HashMap<Integer, Courtroom> allCourtrooms;
	private HashMap<String, Case> allCases;
	private HashMap<Integer, Meeting> allMeetings;
	private HashMap<Integer, Verdict> allVerdicts;
	private HashMap<Integer, Appeal> allAppeals;
	private HashMap<Integer, Document> allDocuments;

	private HashMap<String, User> users;
	private HashMap<Integer, Person> allPeople;

	public final static Date TODAY = UtilsMethods.parseDate("26/11/2024");
	private static final long serialVersionUID = 1L;

	// private constructor
	private Court() {
		super();
		this.allLawyers = new HashMap<>();
		this.allAccuseds = new HashMap<>();
		this.allEmployees = new HashMap<>();
		this.allWitnesses = new HashMap<>();
		this.allTestimonies = new HashMap<>();
		this.alldepartments = new HashMap<>();
		this.allCourtrooms = new HashMap<>();
		this.allCases = new HashMap<>();
		this.allMeetings = new HashMap<>();
		this.allVerdicts = new HashMap<>();
		this.allAppeals = new HashMap<>();
		this.allDocuments = new HashMap<>();
		this.users = new HashMap<>();
		allPeople = new HashMap<>();

		// Ensure admin account exists
		users.put("ADMIN", new User("ADMIN", "ADMIN", "Admin", -1));

	}

	// getInstance
	public static Court getInstance() {
		if (instance == null) {
			instance = loadCourt();
		}
		if (instance.users == null) { // Ensure users HashMap is initialized
			instance.users = new HashMap<>();
			instance.users.put("ADMIN", new User("ADMIN", "ADMIN", "Admin", -1));
		}

		if (instance.allPeople == null) { // Ensure users HashMap is initialized
			instance.allPeople = new HashMap<>();
		}
		return instance;
	}

	public HashMap<Integer, Person> getAllPeople() {
		if (allPeople == null) {
			System.out.println("⚠️ allPeople is NULL in Court!");
			return new HashMap<>(); // Prevent crashes
		}
		return allPeople;
	}

	public void setAllPeople(HashMap<Integer, Person> allPeople) {
		this.allPeople = allPeople;
	}

	private static Court loadCourt() {
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("Court.ser"))) {
			return (Court) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("No existing court data found. Creating a new instance.");
			return new Court();
		}
	}

	// Assign a Lawyer to a Case
	public boolean assignLawyerToCase(String caseId, int lawyerId) {
		Case selectedCase = allCases.get(caseId);
		Lawyer selectedLawyer = allLawyers.get(lawyerId);

		if (selectedCase == null || selectedLawyer == null) {
			return false; // Case or Lawyer not found
		}

		selectedCase.setLawyer(selectedLawyer);
		saveCourt(); // Automatically save after update
		return true;
	}

	// Assign a Judge to a Case Verdict
	public boolean assignJudgeToVerdict(String caseId, int judgeId) {
		Case selectedCase = allCases.get(caseId);
		Judge potentialJudge = (Judge) allLawyers.get(judgeId);

		if (selectedCase == null || potentialJudge == null || !(potentialJudge instanceof Judge)) {
			return false; // Case or Judge not found or not a real judge
		}

		Verdict verdict = selectedCase.getVerdict();
		if (verdict == null) {
			verdict = new Verdict(selectedCase); // ✅ Link case immediately
			selectedCase.setVerdict(verdict);
		} else {
			verdict.setCasee(selectedCase); // ✅ Ensure case is set even for existing verdict
		}

		potentialJudge.addCase(selectedCase);
		potentialJudge.addCase(selectedCase);

		verdict.setJudge(potentialJudge);

		saveCourt(); // Automatically save after update
		return true;
	}

	public void registerMissingUsers() {
		for (Lawyer judge : allLawyers.values()) { // ✅ Judges extend Lawyer
			if (judge instanceof Judge) {
				String username = String.valueOf(judge.getId()); // Username = ID
				if (!users.containsKey(username)) {
					users.put(username, new User(username, username, "Judge", judge.getId()));
					System.out.println("✅ Registered Judge: " + username);
				}
			}
		}

		for (Lawyer lawyer : allLawyers.values()) {
			String username = String.valueOf(lawyer.getId()); // Username = ID
			if (!users.containsKey(username)) {
				users.put(username, new User(username, username, "Lawyer", lawyer.getId()));
				System.out.println("✅ Registered Lawyer: " + username);
			}
		}

		for (Employee employee : allEmployees.values()) {
			String username = String.valueOf(employee.getId()); // Username = ID
			if (!users.containsKey(username)) {
				users.put(username, new User(username, username, "Employee", employee.getId()));
				System.out.println("✅ Registered Employee: " + username);
			}
		}
	}

	public void printCases() {
		System.out.println("printing all cases");
		for (Case casee : allCases.values()) {
			System.out.println("case : " + casee.getCode());

		}

	}

	public static void save() {
		saveCourt();
	}

	public static void saveCourt() {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Court.ser"))) {
			out.writeObject(instance);
			System.out.println("Court data saved successfully.");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to save court data.");
		}
	}

	public void printAllCases() {
		if (allCases == null || allCases.isEmpty()) {
			System.out.println("⚠️ No cases found in the system.");
			return;
		}

		System.out.println("📜 ✅ Listing All Cases in the System:");
		for (Case c : allCases.values()) {
			System.out.println("─────────────────────────────────────────");
			System.out.println("🔹 Case Code: " + c.getCode());
			System.out.println("🔹 Type: " + c.getClass().getSimpleName()); // CriminalCase, FamilyCase, FinancialCase
			System.out.println("🔹 Accused: "
					+ (c.getAccused() != null ? c.getAccused().getFirstName() + " " + c.getAccused().getLastName()
							: "N/A"));
			System.out.println("🔹 Open Date: " + c.getOpenedDate());
			System.out.println("🔹 Status: " + c.getCaseStatus());
			System.out.println("🔹 Case Type: " + c.getCaseType());
			System.out.println("🔹 Lawyer: "
					+ (c.getLawyer() != null ? c.getLawyer().getFirstName() + " " + c.getLawyer().getLastName()
							: "N/A"));
			System.out.println("🔹 Lawyer: " + (c.getLawyer() != null ? c.getLawyer().getId() : "N/A"));
			// ✅ Print Case-Specific Fields
			if (c instanceof CriminalCase) {
				CriminalCase cc = (CriminalCase) c;
				System.out.println("🔹 Crime Scene: " + cc.getCrimeScene());
				System.out.println("🔹 Crime Tool: " + cc.getCrimeTool());
				System.out.println("🔹 Victim: "
						+ (cc.getVictim() != null ? cc.getVictim().getFirstName() + " " + cc.getVictim().getLastName()
								: "N/A"));
			} else if (c instanceof FamilyCase) {
				FamilyCase fc = (FamilyCase) c;
				System.out.println("🔹 Relation Type: " + fc.getRelationType());
				System.out.println("🔹 Victim: "
						+ (fc.getVictim() != null ? fc.getVictim().getFirstName() + " " + fc.getVictim().getLastName()
								: "N/A"));
			} else if (c instanceof FinancialCase) {
				FinancialCase fc = (FinancialCase) c;
				System.out.println("🔹 Loss Amount: " + fc.getLossesAmount());
				System.out.println("🔹 Damaged Item: " + fc.getDamagedItem());
			}

			System.out.println("─────────────────────────────────────────\n");
		}
	}

	// New: Add a user (Only called by ADMIN)
	public boolean addUser(String username, String password, String role, int personId) {
		if (users.containsKey(username)) {
			System.out.println("User already exists.");
			return false;
		}
		users.put(username, new User(username, password, role, personId));
		saveCourt();
		return true;
	}

	// New: Authenticate user
	public User authenticateUser(String username, String password) {
		User user = users.get(username);
		if (user != null && user.getPassword().equals(password)) {
			return user;
		}
		return null;
	}

	public void printJudgeCases() {
		System.out.println("\n===== Judges and Their Assigned Cases =====");

		HashMap<Integer, List<Case>> judgeCases = new HashMap<>();

		// ✅ Loop through all cases and check assigned lawyers
		for (Case c : allCases.values()) {
			Lawyer lawyer = c.getLawyer();
			if (lawyer instanceof Judge) { // ✅ Check if the lawyer is actually a judge
				Judge judge = (Judge) lawyer;
				judgeCases.putIfAbsent(judge.getId(), new ArrayList<>());
				judgeCases.get(judge.getId()).add(c);
			}
		}

		// ✅ Print cases for each judge
		for (Lawyer lawyer : allLawyers.values()) {
			if (lawyer instanceof Judge) {
				Judge judge = (Judge) lawyer;
				System.out.println("\n👨‍⚖️ Judge ID: " + judge.getId() + " | Name: " + judge.getFirstName() + " "
						+ judge.getLastName());

				List<Case> cases = judgeCases.get(judge.getId());
				if (cases == null || cases.isEmpty()) {
					System.out.println("❌ No cases assigned.");
				} else {
					System.out.println("📌 Assigned Cases:");
					for (Case c : cases) {
						System.out.println("   ➤ Case Code: " + c.getCode() + " | Type: " + c.getClass().getSimpleName()
								+ " | Status: " + c.getCaseStatus() + " | Opened: " + c.getOpenedDate());
					}
				}
			}
		}
	}

	public void printLawyerCases() {
		System.out.println("\n===== Lawyers and Their Assigned Cases =====");

		HashMap<Integer, List<Case>> lawyerCases = new HashMap<>();

		// ✅ Loop through all cases and assign them to their respective lawyers
		for (Case c : allCases.values()) {
			Lawyer lawyer = c.getLawyer();
			if (lawyer != null) { // ✅ Ensure the case has a lawyer assigned
				lawyerCases.putIfAbsent(lawyer.getId(), new ArrayList<>());
				lawyerCases.get(lawyer.getId()).add(c);
			}
		}

		// ✅ Print cases for each lawyer
		for (Lawyer lawyer : allLawyers.values()) {
			System.out.println("\n⚖️ Lawyer ID: " + lawyer.getId() + " | Name: " + lawyer.getFirstName() + " "
					+ lawyer.getLastName());

			List<Case> cases = lawyerCases.get(lawyer.getId());
			if (cases == null || cases.isEmpty()) {
				System.out.println("❌ No cases assigned.");
			} else {
				System.out.println("📌 Assigned Cases:");
				for (Case c : cases) {
					System.out.println("   ➤ Case Code: " + c.getCode() + " | Type: " + c.getClass().getSimpleName()
							+ " | Status: " + c.getCaseStatus() + " | Opened: " + c.getOpenedDate());
				}
			}
		}
	}

	public Set<Case> getCasesForEmployee(Employee employee) {
		Set<Case> relevantCases = new HashSet<>();

		System.out
				.println("\n🔍 Checking Cases for Employee: " + employee.getFirstName() + " " + employee.getLastName());

		if (employee.getDepartments() == null || employee.getDepartments().isEmpty()) {
			System.out.println("❌ Employee is not assigned to any department!");
			return relevantCases;
		}

		for (Department department : employee.getDepartments()) {
			System.out.println("📂 Checking Department: " + department.getName());

			for (Lawyer lawyer : allLawyers.values()) {
				if (lawyer.getDepartment() != null && lawyer.getDepartment().equals(department)) {
					System.out.println("⚖️ Found Lawyer: " + lawyer.getFirstName() + " " + lawyer.getLastName()
							+ " handling cases for department: " + department.getName());

					if (lawyer.getCasesHandled() == null || lawyer.getCasesHandled().isEmpty()) {
						System.out.println("   ❌ Lawyer has no cases!");
					} else {
						System.out.println("   ✅ Cases Found: ");
						for (Case c : lawyer.getCasesHandled()) {
							System.out.println("      - " + c.getCode() + " (" + c.getCaseType() + ")");
							relevantCases.add(c);
						}
					}
				}
			}
		}

		if (relevantCases.isEmpty()) {
			System.out.println("❌ No cases assigned to this employee!");
		} else {
			System.out.println("✅ Total Cases Assigned: " + relevantCases.size());
		}

		return relevantCases;
	}

	public void printEmployeesWithCases() {
		for (Employee employee : allEmployees.values()) {
			System.out.println("is the employee" + employee.getId() + "  manager ? " + employee.getPosition());
			System.out.println("Employee: " + employee.getFullName());
			Set<Case> cases = getCasesForEmployee(employee);
			if (cases.isEmpty()) {
				System.out.println("  No cases assigned.");
			} else {
				System.out.println("  Related Cases:");
				for (Case c : cases) {
					System.out.println("    - " + c.getCode()); // Assuming Case has getCaseName()
				}
			}
		}
	}

	public HashMap<String, User> getUsers() {
		return users;
	}

	public void printEmployeeDepartments() {
		System.out.println("\n🔍 Checking Employees and Their Departments...");
		for (Employee emp : allEmployees.values()) {
			System.out.println("👤 Employee: " + emp.getFirstName() + " " + emp.getLastName());

			if (emp.getDepartments() == null || emp.getDepartments().isEmpty()) {
				System.out.println("❌ No department assigned!");
			} else {
				System.out.println("✅ Departments: ");
				for (Department dep : emp.getDepartments()) {
					System.out.println("   - " + dep.getName());
				}
			}
		}
	}

	public void printLawyerDepartments() {
		System.out.println("\n🔍 Checking Lawyers and Their Departments...");
		for (Lawyer lawyer : allLawyers.values()) {
			System.out.println("⚖️ Lawyer: " + lawyer.getFirstName() + " " + lawyer.getLastName());

			if (lawyer.getDepartment() == null) {
				System.out.println("❌ No department assigned!");
			} else {
				System.out.println("✅ Department: " + lawyer.getDepartment().getName());
			}
		}
	}

	// add methods

	public boolean addEmployeeToDepartment(Department department, Employee employee) {
		if (department == null || employee == null)
			return false;
		if (alldepartments.containsKey(department.getNumber()) && allEmployees.containsKey(employee.getId())) {
			save();

			return department.addEmployee(employee);
		}
		return false;
	}

	public boolean addLawyerToDepartment(Department department, Lawyer lawyer) {
		if (department == null || lawyer == null)
			return false;
		if (alldepartments.containsKey(department.getNumber()) && allLawyers.containsKey(lawyer.getId())) {
			save();

			return department.addLawyer(lawyer);
		}
		return false;
	}

	public boolean addJudgeToDepartment(Department department, Judge judge) {
		if (department == null) {
			System.out.println("❌ NULL Department or Judge");
			return false;
		}
		if (judge == null) {
			System.out.println("❌ NULL Judge");
			return false;
		}

		if (!alldepartments.containsKey(department.getNumber())) {
			System.out.println("❌ Department does not exist: " + department.getNumber());
			return false;
		}

		if (!allLawyers.containsKey(judge.getId())) { // Use allJudges instead of allLawyers
			System.out.println("❌ Judge does not exist: " + judge.getId());
			return false;
		}

		save();
		return department.addJudge(judge);
	}

	public boolean addDepartment(Department department) {
		if (department == null)
			return false;
		if (alldepartments.containsKey(department.getNumber()))
			return false;
		alldepartments.put(department.getNumber(), department);
		System.out.println("department added successfully : " + department);
		saveCourt();
		return true;
	}

	public boolean addCourtroom(Courtroom courtroom) {
		if (courtroom == null)
			return false;
		if (allCourtrooms.containsKey(courtroom.getCourtroomNumber()))
			return false;
		if (alldepartments.containsKey(courtroom.getDepartment().getNumber()))
			alldepartments.get(courtroom.getDepartment().getNumber()).addCourtroom(courtroom);
		allCourtrooms.put(courtroom.getCourtroomNumber(), courtroom);
		save();
		return true;
	}

	public boolean addLawyer(Lawyer lawyer) {
		if (lawyer == null)
			return false;
		if (allLawyers.containsKey(lawyer.getId()))
			return false;
		allLawyers.put(lawyer.getId(), lawyer);
		save();

		return true;
	}

	public boolean addJudge(Judge judge) {
		if (judge == null)
			return false;

		if (allLawyers.containsKey(judge.getId()))
			return false;

		allLawyers.put(judge.getId(), judge);
		save();

		System.out.println("🟢 Judge added to allPeople: " + judge.getFirstName() + " " + judge.getLastName());

		return true;
	}

	public boolean addEmployee(Employee employee) {
		if (employee == null)
			return false;
		if (allEmployees.containsKey(employee.getId()))
			return false;
		for (Department dep : alldepartments.values())
			if (employee.getDepartments().contains(dep)) {
				dep.addEmployee(employee);
				break;
			}

		allEmployees.put(employee.getId(), employee);
		System.out.println("we add employee in court");
		save();

		return true;
	}

	public boolean addDocument(Document doc) {
		if (doc == null)
			return false;
		if (allDocuments.containsKey(doc.getCode()))
			return false;
		for (Case curCase : allCases.values()) {
			if (doc.getCasee().equals(curCase)) {
				curCase.addDocument(doc);
				break;
			}
		}
		allDocuments.put(doc.getCode(), doc);
		save();
		return true;
	}

	public boolean addVerdict(Verdict verdict) {
		if (verdict == null)
			return false;

		// Prevent adding duplicate verdicts
		if (allVerdicts.containsKey(verdict.getVerdictID()))
			return false;

		// Assign verdict to the case
		for (Case curCase : allCases.values()) {
			if (verdict.getCasee().equals(curCase)) {
				if (curCase.getVerdict() != null) {
					// Case already has a verdict, ensure we handle updates properly
					System.out.println("⚠️ Case already has a verdict! Updating existing verdict.");
				}
				curCase.setVerdict(verdict);
				curCase.setCaseStatus(Status.finished);
				break;
			}
		}

		// Handle appeals if applicable
		if (verdict.getAppeal() != null) {
			// Set new verdict for the appeal
			verdict.getAppeal().setNewVerdict(verdict);
			allAppeals.put(verdict.getAppeal().getAppealID(), verdict.getAppeal());
		}

		// Save the verdict in court records
		allVerdicts.put(verdict.getVerdictID(), verdict);

		// Save the court data to ensure persistence
		save();

		return true;
	}

	public boolean addAccused(Accused accused) {
		if (accused == null)
			return false;
		if (allAccuseds.containsKey(accused.getId()))
			return false;
		allAccuseds.put(accused.getId(), accused);
		save();

		return true;
	}

	public boolean addAppeal(Appeal appeal) {
		if (appeal == null || appeal.getCurrentVerdict() == null || appeal.getNewVerdict() == null) {
			return false; // Prevents NullPointerException if any necessary object is missing
		}

		if (allAppeals.containsKey(appeal.getAppealID()))
			return false;

		// Ensure current verdict exists in the system before adding
		if (!allVerdicts.containsKey(appeal.getCurrentVerdict().getVerdictID()))
			allVerdicts.put(appeal.getCurrentVerdict().getVerdictID(), appeal.getCurrentVerdict());

		// Ensure new verdict exists in the system before adding
		if (!allVerdicts.containsKey(appeal.getNewVerdict().getVerdictID()))
			allVerdicts.put(appeal.getNewVerdict().getVerdictID(), appeal.getNewVerdict());

		// Ensure current verdict is valid before setting appeal
		if (allVerdicts.containsKey(appeal.getCurrentVerdict().getVerdictID())) {
			allVerdicts.get(appeal.getCurrentVerdict().getVerdictID()).setAppeal(appeal);
		}

		// Ensure the case exists before setting a new verdict
		if (appeal.getCurrentVerdict().getCasee() != null) {
			if (allCases.containsKey(appeal.getCurrentVerdict().getCasee().getCode())) {
				allCases.get(appeal.getCurrentVerdict().getCasee().getCode()).setVerdict(appeal.getNewVerdict());
			}
		} else {
			System.out.println(
					"❌ Error: Case associated with verdict " + appeal.getCurrentVerdict().getVerdictID() + " is null.");
		}

		allAppeals.put(appeal.getAppealID(), appeal);
		save();

		return true;
	}

	public boolean addTestimony(Testimony testimony) {
		if (testimony == null)
			return false;
		if (allTestimonies.containsKey(testimony.getTestimonyID()))
			return false;
		if (!allWitnesses.containsKey(testimony.getWitness().getId()))
			allWitnesses.put(testimony.getWitness().getId(), testimony.getWitness());
		allWitnesses.get(testimony.getWitness().getId()).addTestimony(testimony);
		allCases.get(testimony.getCasee().getCode()).addTestimony(testimony);
		allTestimonies.put(testimony.getTestimonyID(), testimony);
		return true;
	}

	public boolean addWitness(Witness witness) {
		if (witness == null)
			return false;
		if (allWitnesses.containsKey(witness.getId()))
			return false;
		for (Testimony tes : witness.getTestimoniesList()) {
			if (!allTestimonies.containsKey(tes.getTestimonyID()))
				allTestimonies.put(tes.getTestimonyID(), tes);
		}
		for (Case c : allCases.values()) {
			for (Testimony tes : witness.getTestimoniesList()) {
				if (tes.getCasee().equals(c))
					c.addTestimony(tes);
			}
		}
		allWitnesses.put(witness.getId(), witness);
		save();
		return true;
	}

	public boolean addMeeting(Meeting meet) {
		if (meet == null || meet.getCourtroom() == null || meet.getCasee() == null) {
			System.out.println("❌ Meeting is NULL or missing a courtroom/case.");
			return false;
		}

		if (allMeetings.containsKey(meet.getMeetingID())) {
			System.out.println("❌ Meeting already exists with ID: " + meet.getMeetingID());
			return false;
		}

		if (!allCases.containsKey(meet.getCasee().getCode())) {
			System.out.println("⚠️ Case not found in system. Adding now...");
			allCases.put(meet.getCasee().getCode(), meet.getCasee());
		}

		if (!allCourtrooms.containsKey(meet.getCourtroom().getCourtroomNumber())) {
			System.out.println("⚠️ Courtroom not found in system. Adding now...");
			allCourtrooms.put(meet.getCourtroom().getCourtroomNumber(), meet.getCourtroom());
		}

		// ❌ Prevent scheduling conflicts
		for (Meeting m : allMeetings.values()) {
			if (m.getCourtroom() != null && m.getCourtroom().equals(meet.getCourtroom())
					&& m.getHour().equals(meet.getHour()) && m.getMeetingDate().equals(meet.getMeetingDate())) {
				System.out.println(
						"❌ Meeting conflict detected in courtroom " + meet.getCourtroom().getCourtroomNumber());
				return false;
			}
		}

		// ❌ Prevent adding meeting before case opening date
		if (meet.getMeetingDate().before(meet.getCasee().getOpenedDate())) {
			System.out.println("❌ Meeting date is before the case opening date!");
			return false;
		}

		// ✅ Add meeting to the case
		boolean caseUpdated = false;
		for (Case c : allCases.values()) {
			if (c.equals(meet.getCasee())) {
				System.out.println("🔄 Adding meeting to case: " + c.getCode());
				c.addMeeting(meet);
				caseUpdated = true;
				break;
			}
		}

		if (!caseUpdated) {
			System.out.println("❌ ERROR: Meeting not linked to any case!");
			return false;
		}

		// ✅ Store meeting in system
		allMeetings.put(meet.getMeetingID(), meet);
		System.out.println("✅ Meeting stored successfully! ID: " + meet.getMeetingID());

		// ✅ SAVE to file/database
		save();

		return true;
	}

	public boolean addCase(Case c) {
		if (c == null)
			return false;
		if (allCases.containsKey(c.getCode())) {
			System.out.println("the case exist in the system");
			return false;
		}
		if (c.getLawyer() != null) {
			if (!allLawyers.containsKey(c.getLawyer().getId()))
				allLawyers.put(c.getLawyer().getId(), c.getLawyer());

			allLawyers.get(c.getLawyer().getId()).addCase(c);
		}
		if (!allAccuseds.containsKey(c.getAccused().getId()))
			allAccuseds.put(c.getAccused().getId(), c.getAccused());
		allAccuseds.get(c.getAccused().getId()).addCase(c);
		if (c.getVerdict() != null) {
			if (!allVerdicts.containsKey(c.getVerdict().getVerdictID()))
				allVerdicts.put(c.getVerdict().getVerdictID(), c.getVerdict());
			allVerdicts.get(c.getVerdict().getVerdictID()).setCasee(c);
		}

		for (Meeting meet : c.getMeetingsList()) {
			System.out.println("we get here ?");
			if (!allMeetings.containsKey(meet.getMeetingID()))
				allMeetings.put(meet.getMeetingID(), meet);
		}
		for (Document doc : c.getDocumentsList()) {
			if (!allDocuments.containsKey(doc.getCode()))
				allDocuments.put(doc.getCode(), doc);
		}
		for (Testimony tes : c.gettestimoniesList()) {
			allWitnesses.put(tes.getWitness().getId(), tes.getWitness());
			allTestimonies.put(tes.getTestimonyID(), tes);
		}
		allCases.put(c.getCode(), c);
		save();
		return true;
	}

	public boolean addFinancialCase(FinancialCase c) {
		return addCase(c);
	}

	public boolean addCriminalCase(CriminalCase c) {
		return addCase(c);
	}

	public boolean addFamilyCase(FamilyCase c) {
		return addCase(c);
	}

	// getReal methods
	public Department getRealDepartment(int number) {
		return alldepartments.get(number);
	}

	public Courtroom getRealCourtroom(int code) {
		return allCourtrooms.get(code);
	}

	public Lawyer getRealLawyer(int ID) {
		return allLawyers.get(ID);
	}

	public Person getRealPerson(int ID) {
		return allPeople.get(ID);
	}

	public Judge getRealJudge(int ID) {
		return (Judge) allLawyers.get(ID);

	}

	public Accused getRealAccused(int ID) {
		return allAccuseds.get(ID);
	}

	public Employee getRealEmployee(int ID) {
		return allEmployees.get(ID);
	}

	public Document getRealDocument(int code) {
		return allDocuments.get(code);
	}

	public Verdict getRealVerdict(int number) {
		return allVerdicts.get(number);
	}

	public Testimony getRealTestimony(int code) {
		return allTestimonies.get(code);
	}

	public Witness getRealWitness(int ID) {
		return allWitnesses.get(ID);
	}

	public Appeal getRealAppeal(int number) {
		return allAppeals.get(number);
	}

	public Meeting getRealMeeting(int number) {
		return allMeetings.get(number);
	}

	public Case getRealCase(String code) {
		return allCases.get(code);
	}

	public FinancialCase getRealFinancialCase(String code) {
		return (FinancialCase) allCases.get(code);
	}

	public CriminalCase getRealCriminalCase(String code) {
		return (CriminalCase) allCases.get(code);
	}

	public FamilyCase getRealFamilyCase(String code) {
		return (FamilyCase) allCases.get(code);
	}

	// remove methods
	public boolean removeDepartment(Department department) {
		if (department == null)
			return false;
		if (!alldepartments.containsKey(department.getNumber()))
			return false;
		for (Employee e : department.getEmployees())
			e.removeDepartment(department);
		for (Lawyer l : department.getLawyers())
			l.setDepartment(null);
		for (Courtroom c : department.getCourtrooms())
			c.setDepartment(null);

		alldepartments.remove(department.getNumber());
		saveCourt();
		return true;
	}

	public boolean removeCourtroom(Courtroom courtroom) {
		if (courtroom == null)
			return false;
		if (!allCourtrooms.containsKey(courtroom.getCourtroomNumber()))
			return false;
		for (Department dep : alldepartments.values())
			if (dep.equals(courtroom.getDepartment())) {
				dep.removeCourtroom(courtroom);
				break;
			}
		allCourtrooms.remove(courtroom.getCourtroomNumber());
		save();
		return true;
	}

	public boolean removeLawyer(Lawyer lawyer) {
		if (lawyer == null)
			return false;
		if (!allLawyers.containsKey(lawyer.getId()))
			return false;
		for (Department dep : alldepartments.values())
			if (dep.equals(lawyer.getDepartment())) {
				dep.removeLawyer(lawyer);
				break;
			}
		allLawyers.remove(lawyer.getId());
		save();
		return true;
	}

	public boolean removeJudge(Judge judge) {
		if (judge == null)
			return false;
		if (!allLawyers.containsKey(judge.getId()))
			return false;
		for (Department dep : alldepartments.values())
			if (dep.equals(judge.getDepartment())) {
				dep.removeJudge(judge);
				break;
			}
		allLawyers.remove(judge.getId());
		save();
		return true;
	}

	public boolean removeEmployee(Employee employee) {
		if (employee == null)
			return false;
		if (!allEmployees.containsKey(employee.getId()))
			return false;
		for (Department dep : employee.getDepartments()) {
			dep.removeEmployee(employee);
			break;
		}
		allEmployees.remove(employee.getId());
		save();
		return true;
	}

	public boolean removeDocument(Document doc) {
		if (doc == null)
			return false;
		if (!allDocuments.containsKey(doc.getCode()))
			return false;
		for (Case curCase : allCases.values()) {
			if (doc.getCasee().equals(curCase)) {
				curCase.removeDocument(doc);
				break;
			}
		}
		allDocuments.remove(doc.getCode());
		save();
		return true;
	}

	public boolean removeVerdict(Verdict verdict) {
		if (verdict == null)
			return false;
		if (!allVerdicts.containsKey(verdict.getVerdictID()))
			return false;
		for (Case curCase : allCases.values()) {
			if (verdict.getCasee() != null && verdict.getCasee().equals(curCase)) {
				curCase.setVerdict(null);
				break;
			}
		}
		List<Integer> keysToRemove = new ArrayList<>();
		for (Map.Entry<Integer, Appeal> entry : allAppeals.entrySet()) {
			Appeal a = entry.getValue();

			if ((a != null && a.getCurrentVerdict() != null && verdict.equals(a.getCurrentVerdict()))
					|| (a != null && a.getNewVerdict() != null && verdict.equals(a.getNewVerdict()))) {
				keysToRemove.add(entry.getKey());
			}
		}
		for (Integer key : keysToRemove) {
			allAppeals.remove(key);
		}
		allVerdicts.remove(verdict.getVerdictID());
		save();
		return true;
	}

	public boolean removeAppeal(Appeal appeal) {
		if (appeal == null)
			return false;
		if (!allAppeals.containsKey(appeal.getAppealID()))
			return false;
		for (Verdict a : allVerdicts.values()) {
			if (a.getAppeal().equals(appeal))
				a.setAppeal(null);
		}
		allAppeals.remove(appeal.getAppealID());
		save();
		return true;
	}

	public boolean removeTestimony(Testimony testimony) {
		if (testimony == null)
			return false;
		if (!allTestimonies.containsKey(testimony.getTestimonyID()))
			return false;
		for (Witness wit : allWitnesses.values()) {
			if (wit.equals(testimony.getWitness())) {
				wit.removeTestimony(testimony);
				break;
			}
		}
		allTestimonies.remove(testimony.getTestimonyID());
		save();
		return true;
	}

	public boolean removeWitness(Witness witness) {
		if (witness == null)
			return false;
		if (!allWitnesses.containsKey(witness.getId()))
			return false;
		for (Case c : allCases.values()) {
			for (Testimony tes : witness.getTestimoniesList()) {
				c.removeTestimony(tes);
				allTestimonies.remove(tes.getTestimonyID());
			}
		}
		for (Integer i : allTestimonies.keySet()) {
			Testimony t = allTestimonies.get(i);
			if (t.getWitness().equals(witness)) {
				allTestimonies.remove(i);
			}
		}
		allWitnesses.remove(witness.getId());
		save();
		return true;
	}

	public boolean removeMeeting(Meeting meet) {
		if (meet == null)
			return false;
		if (!allMeetings.containsKey(meet.getMeetingID()))
			return false;
		for (Case c : allCases.values()) {
			if (c.equals(meet.getCasee()))
				c.removeMeeting(meet);
		}
		allMeetings.remove(meet.getMeetingID());
		save();
		return true;
	}

	public boolean removeAccused(Accused accused) {
		if (accused == null)
			return false;
		if (!allAccuseds.containsKey(accused.getId()))
			return false;
		ArrayList<Case> casesToRemove = new ArrayList<>();
		for (Case c : allCases.values()) {
			if (accused.getCases().contains(c)) {
				casesToRemove.add(c);
			}
		}
		for (Case cc : casesToRemove)
			allCases.remove(cc.getCode());
		allAccuseds.remove(accused.getId());
		save();
		return true;
	}

	public boolean removeCase(Case c) {
		if (c == null)
			return false;
		if (!allCases.containsKey(c.getCode()))
			return false;

		for (Lawyer l : allLawyers.values()) {
			if (l.getCasesHandled().contains(c)) {
				l.removeCase(c);
			}
		}

		for (Accused a : allAccuseds.values()) {
			if (a.getCases().contains(c)) {
				a.removeCase(c);
			}
		}

		for (int i = allVerdicts.size() - 1; i >= 0; i--) {
			Verdict v = allVerdicts.get(i);
			if (v != null && v.getCasee().equals(c)) {
				for (int j = allAppeals.size() - 1; j >= 0; j--) {
					Appeal a = allAppeals.get(j);
					if (v.getAppeal() != null && v.getAppeal().equals(a)) {
						allAppeals.remove(j);
					}
				}
				allVerdicts.remove(i);
			}
		}

		for (Meeting meet : c.getMeetingsList()) {
			allMeetings.remove(meet.getMeetingID());
			c.getMeetingsList().remove(meet);
		}

		for (Document doc : c.getDocumentsList()) {
			allDocuments.remove(doc.getCode());
			c.getDocumentsList().remove(doc);
		}

		for (Testimony tes : c.gettestimoniesList()) {
			allTestimonies.remove(tes.getTestimonyID());
			allWitnesses.get(tes.getWitness().getId()).removeTestimony(tes);
		}
		allCases.remove(c.getCode());
		save();
		return true;
	}

	public boolean removeFamilyCase(FamilyCase c) {
		return removeCase(c);
	}

	public boolean removeFinancialCase(FinancialCase c) {
		return removeCase(c);
	}

	public boolean removeCriminalCase(CriminalCase c) {
		return removeCase(c);
	}

	// Queries
	public int howManyCasesBefore(Date date) {
		int count = 0;
		for (Case casee : allCases.values()) {
			if (casee.getVerdict() != null && casee.getVerdict().getIssusedDate() != null) {
				if (casee.getVerdict().getIssusedDate().before(date)) {
					count++;
				}
			}
		}
		return count;
	}

	public ArrayList<Case> findCasesWithMoreThanThreeTestimoniesFromSameGender(Gender gender) {
		ArrayList<Case> result = new ArrayList<>();
		ArrayList<Case> checkedCases = new ArrayList<>();

		for (Case courtCase : allCases.values()) {
			if (checkedCases.contains(courtCase))
				continue;

			int count = 0;
			for (Witness witness : allWitnesses.values()) {
				if (witness.getGender() == gender) {
					for (Testimony testimony : witness.getTestimoniesList()) {
						if (testimony.getCasee() != null && testimony.getCasee().equals(courtCase)) {
							count++;
						}
					}
				}
			}

			if (count > 3) {
				result.add(courtCase);
			}
			checkedCases.add(courtCase);
		}
		System.out.println(result);

		return result;
	}

	// Getters
	public Map<Integer, Lawyer> getAllLawyers() {
		return Collections.unmodifiableMap(allLawyers);
	}

	public Map<Integer, Accused> getAllAccuseds() {
		return Collections.unmodifiableMap(allAccuseds);

	}

	public Map<Integer, Employee> getAllEmployees() {
		return allEmployees;

	}

	public Map<Integer, Witness> getAllWitnesses() {
		return Collections.unmodifiableMap(allWitnesses);

	}

	public Map<Integer, Testimony> getAllTestimonies() {
		return Collections.unmodifiableMap(allTestimonies);

	}

	public Map<Integer, Department> getAllDepartments() {
		return Collections.unmodifiableMap(alldepartments);

	}

	public Map<Integer, Courtroom> getAllCourtrooms() {
		return Collections.unmodifiableMap(allCourtrooms);

	}

	public Map<String, Case> getAllCases() {
		return allCases;

	}

	public Map<Integer, Meeting> getAllMeetings() {
		return Collections.unmodifiableMap(allMeetings);

	}

	public Map<Integer, Verdict> getAllVerdicts() {
		return Collections.unmodifiableMap(allVerdicts);

	}

	public Map<Integer, Appeal> getAllAppeals() {
		return Collections.unmodifiableMap(allAppeals);

	}

	public Map<Integer, Document> getAllDocuments() {
		return Collections.unmodifiableMap(allDocuments);

	}

	// hw2 queries

	public HashMap<Department, Integer> findInActiveCasesCountByDepartment() {
		HashMap<Department, Integer> toRet = new HashMap<Department, Integer>();
		HashSet<Case> helper;
		for (Department d : alldepartments.values()) {
			helper = new HashSet<Case>();
			for (Lawyer l : d.getLawyers()) {
				if (!(l instanceof Judge) && l.getDepartment() != null) {

					for (Case c : l.getCasesHandled()) {
						if (!helper.contains(c)
								&& (c.getCaseStatus() == Status.finished || c.getCaseStatus() == Status.canceled)) {

							helper.add(c);
						}
					}
				}
			}
			toRet.put(d, helper.size());
		}
		return toRet;
	}

	public Lawyer findTheSuitableLawyer(Case casee) {
		if (casee == null)
			return null;

		Lawyer suitableLawyer = null;
		for (Lawyer law : allLawyers.values()) {
			if (law.getSpecialization().equals(casee.getCaseType()) && law.getCasesHandled().size() < 5) {
				suitableLawyer = law;
				break;
			}
		}

		if (suitableLawyer == null) {
			for (Lawyer law : allLawyers.values()) {
				if (suitableLawyer == null || law.getCasesHandled().size() < suitableLawyer.getCasesHandled().size()) {
					suitableLawyer = law;
				}
			}
		}
		Date latestDate = null;
		for (Case c : suitableLawyer.getCasesHandled()) {
			if (latestDate == null || (c.getOpenedDate() != null && c.getOpenedDate().after(latestDate))) {
				latestDate = c.getOpenedDate();
			}
			for (Meeting m : c.getMeetingsList()) {
				if (m.getMeetingDate() != null && m.getMeetingDate().after(latestDate)) {
					latestDate = m.getMeetingDate();
				}
			}
		}

		if (latestDate == null) {
			latestDate = new Date();
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(latestDate);
		calendar.add(Calendar.DAY_OF_MONTH, 2);
		Date newMeetingDate = calendar.getTime();
		Courtroom availableCourtroom = null;
		if (suitableLawyer.getDepartment() != null) {
			for (Courtroom cr : suitableLawyer.getDepartment().getCourtrooms()) {
				boolean isAvailable = true;
				for (Meeting m : allMeetings.values()) {
					if (m.getCourtroom().equals(cr) && m.getMeetingDate().equals(newMeetingDate)) {
						isAvailable = false;
						break;
					}
				}
				if (isAvailable) {
					availableCourtroom = cr;
					break;
				}
			}
		}
		if (availableCourtroom == null) {
			for (Courtroom cr : allCourtrooms.values()) {
				boolean isAvailable = true;
				for (Meeting m : allMeetings.values()) {
					if (m.getCourtroom().equals(cr) && m.getMeetingDate().equals(newMeetingDate)) {
						isAvailable = false;
						break;
					}
				}
				if (isAvailable) {
					availableCourtroom = cr;
					break;
				}
			}
		}
		Time meetingTime = Time.valueOf("09:00:00");
		Meeting newMeeting = new Meeting(newMeetingDate, meetingTime, availableCourtroom, casee);
		casee.addMeeting(newMeeting);
		casee.setLawyer(suitableLawyer);
		allCases.put(casee.getCode(), casee);
		suitableLawyer.addCase(casee);
		return suitableLawyer;
	}

	public Employee AppointANewManager(Department department) {
		if (department == null) {
			System.out.println("❌ Department is NULL!");
			return null;
		}

		if (department.getEmployees().isEmpty()) {
			System.out.println("⚠️ No employees in department #" + department.getNumber() + ". No manager to appoint.");
			return null; // No employees to appoint
		}

		Employee newManager = null;

		// Find the longest-serving employee
		for (Employee e : department.getEmployees()) {
			if (e.getWorkStartDate() == null)
				continue; // Skip employees with no start date
			if (newManager == null || e.getWorkStartDate().before(newManager.getWorkStartDate())) {
				newManager = e;
			}
		}

		if (newManager == null) {
			System.out.println("⚠️ No eligible employee found for promotion in department #" + department.getNumber());
			return null;
		}

		// Promote the new manager
		newManager.setPosition(Position.Manager);
		newManager.setSalary(newManager.getSalary() + 5000);

		// Remove the old manager safely
		Employee oldManager = department.getManager();
		if (oldManager != null) {
			allEmployees.remove(oldManager.getId());
		}

		// Set the new manager
		department.setManager(newManager);
		System.out.println(
				"✅ Appointed new manager: " + newManager.getId() + " for Department #" + department.getNumber());

		return newManager;
	}

	public Set<String> findUniqueCrimeToolsByCrimeScene(String crimeScene) {
		TreeSet<String> uniqueCrimeTools = new TreeSet<>();
		for (Case c : allCases.values()) {
			if (c instanceof CriminalCase criminalCase) {
				if (criminalCase.getCrimeScene().equals(crimeScene)) {
					uniqueCrimeTools.add(criminalCase.getCrimeTool());
				}
			}
		}
		return uniqueCrimeTools;
	}

	public List<FamilyCase> findFamilyCasesWithWitnessesFromBothSides() {
		ArrayList<FamilyCase> toRet = new ArrayList<FamilyCase>();
		for (Case c : allCases.values()) {
			if (c instanceof FamilyCase) {
				for (Testimony tes : c.gettestimoniesList()) {
					Witness wit = tes.getWitness();
					if (wit.getId() == ((FamilyCase) c).getVictim().getId() && !toRet.contains(c))
						if (!toRet.contains((FamilyCase) c))
							toRet.add((FamilyCase) c);
				}

			}

		}
		return toRet;
	}

	public int differenceBetweenTheLongestAndShortestCase(Lawyer lawyer) {
		Integer longestCase = Integer.MIN_VALUE;
		Integer shortestCase = Integer.MAX_VALUE;

		if (lawyer == null || lawyer.getCasesHandled() == null || lawyer.getCasesHandled().isEmpty()) {
			return 0;
		}

		for (Case c : lawyer.getCasesHandled()) {
			if (c.getMeetingsList() != null && !c.getMeetingsList().isEmpty()) {
				Date firstMeet = c.getMeetingsList().iterator().next().getMeetingDate();
				Date lastMeet = c.getMeetingsList().iterator().next().getMeetingDate();
				// Iterate through the meetings to find the earliest and latest dates
				for (Meeting m : c.getMeetingsList()) {
					if (m.getMeetingDate().before(firstMeet)) {
						firstMeet = m.getMeetingDate();
					}
					if (m.getMeetingDate().after(lastMeet)) {
						lastMeet = m.getMeetingDate();
					}
				}

				// Calculate the duration in days
				int duration = (int) ((lastMeet.getTime() - firstMeet.getTime()) / (1000 * 60 * 60 * 24));

				if (duration > longestCase)
					longestCase = duration;
				if (duration < shortestCase)
					shortestCase = duration;
			}
		}

		if (longestCase == Integer.MIN_VALUE || shortestCase == Integer.MAX_VALUE) {
			return 0;
		}

		return longestCase - shortestCase;
	}

}
