package model;

import java.io.Serializable;

public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private String role; // "Judge", "Lawyer", "Employee"
	private int personId; // ID of the person (Judge, Lawyer, Employee)

	public User(String username, String password, String role, int personId) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.personId = personId;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getRole() {
		return role;
	}

	public int getPersonId() {
		return personId;
	}
}
