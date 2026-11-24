package model;

public class SessionManager {
	private static SessionManager instance;
	private User loggedInUser; // Stores the currently logged-in user

	private SessionManager() {
	} // Private constructor to prevent direct instantiation

	public static SessionManager getInstance() {
		if (instance == null) {
			instance = new SessionManager();
		}
		return instance;
	}

	// Store the logged-in user
	public void setLoggedInUser(User user) {
		this.loggedInUser = user;
	}

	// Retrieve the logged-in user
	public User getLoggedInUser() {
		return loggedInUser;
	}

	// Check if a user is logged in
	public boolean isUserLoggedIn() {
		return loggedInUser != null;
	}

	// Clear session on logout
	public void logout() {
		loggedInUser = null;
	}
}
