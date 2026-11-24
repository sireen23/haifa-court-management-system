package model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;

import control.Court;
import enums.Gender;
import enums.Position;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Employee extends Person implements Serializable {
	private Date workStartDate;
	private HashSet<Department> departments;
	private double salary;
	private static final long serialVersionUID = 1L;
	private String profileImagePath;
	private transient BooleanProperty selected = new SimpleBooleanProperty(false);

	private Position position;

	public Employee(int id, String firstName, String lastName, java.util.Date birthDate, String address,
			String phoneNumber, String email, Gender gender, Date workStartDate, double salary, Position position) {
		super(id, firstName, lastName, birthDate, address, phoneNumber, email, gender);
		setWorkStartDate(workStartDate);
		this.departments = new HashSet<Department>();
		this.salary = salary;
		this.position = position;
		this.profileImagePath = "src/uploaded_images/default_image.png"; // Default image
		this.selected = new SimpleBooleanProperty(false);

	}

	public BooleanProperty selectedProperty() {
		if (selected == null) {
			selected = new SimpleBooleanProperty(false); // ✅ Always ensure it's initialized
		}
		return selected;
	}

	public boolean isSelected() {
		return selected.get();
	}

	public void setSelected(boolean value) {
		selected.set(value);
	}

	// Setter for Image Path
	public void setProfileImagePath(String imagePath) {
		this.profileImagePath = imagePath;
		System.out.println("new image path is : " + profileImagePath);
	}

	// Getter for Image Path
	public String getProfileImagePath() {
		return profileImagePath;
	}

	public boolean addDepartment(Department department) {
		if (department == null)
			return false;
		if (departments.contains(department))
			return false;
		departments.add(department);
		return true;
	}

	public boolean removeDepartment(Department department) {
		if (department == null)
			return false;
		if (!departments.contains(department))
			return false;
		departments.remove(department);
		return true;
	}

	public void setWorkStartDate(Date workStartDate) {
		// if the birthday is after "today", it set to "today"
		if (workStartDate.after(Court.TODAY)) {
			workStartDate = Court.TODAY;
		}
		this.workStartDate = workStartDate;
	}

	public HashSet<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(HashSet<Department> departments) {
		this.departments = departments;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Date getWorkStartDate() {
		return workStartDate;
	}

	@Override
	public String toString() {
		return "Employee [id =" + getId() + ", FirstName=" + getFirstName() + ", LastName=" + getLastName()
				+ ", BirthDate=" + getBirthDate().toString() + ", Address=" + getAddress() + ", PhoneNumber="
				+ getPhoneNumber() + ", Email=" + getEmail() + ", Gender=" + getGender() + "workStartDate= "
				+ workStartDate.toString() + ", salary=" + salary + ", position=" + position.toString() + "]";

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(position, salary, workStartDate);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		return Objects.equals(departments, other.departments) && position == other.position
				&& Double.doubleToLongBits(salary) == Double.doubleToLongBits(other.salary)
				&& Objects.equals(workStartDate, other.workStartDate);
	}

}
