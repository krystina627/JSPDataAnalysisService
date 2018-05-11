
package login;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import billofmaterialssearch.DBConnection;

public class Authentication {

	/**
	 * 
	 */
	private String email;
	private String password;
	private String hashPassword;

	private boolean isValid = false;
	private UserStatus userStatus = UserStatus.DOES_NOT_EXIST;
	private int userID;
	private boolean isLocked = false;
	private boolean isAdmin = false;
	private boolean isApproved = false;
	
	// constructor with validation as part of creation
	public Authentication(String email, String password) {
		this.email = email;
		this.password = password;
		validate();
	}


	// prepare the data and validity flags
	private void validate() {
		boolean success = false;

		// Encrypt the user-entered password, so it can be compared to the
		// already-encrypted password from the database

		String encryptpassword = null;
		try {
			encryptpassword = Encryption.encrypt(password);
		} catch (Exception e) {
			System.out.println("An error has occurred in password encryption.");
		}

		// Database connection is a work in progress...
		// Will eventually just reference the DBConnection.java class
		Connection conn = null;
		Statement stmt = null;
		String query = "";
		ResultSet rs = null;

		try {

			conn = DBConnection.getConnection();

			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			query = "select userID, password, isAdmin, isLocked, isApproved, DeleteDate from Users where Email = '" + email + "' and DeleteDate is NULL ;";
			rs = stmt.executeQuery(query);

			// select and test the first returned row
			if (rs.next()) {
				userID = rs.getInt(1);
				hashPassword = rs.getString(2);
				isAdmin = rs.getBoolean(3);
				isLocked = rs.getBoolean(4);
				isApproved = rs.getBoolean(5);
				
				// set validity flag for account and password match
				isValid = encryptpassword.contentEquals(hashPassword);
				
				// does the locally encrypted password match the stored password?
				userStatus = encryptpassword.contentEquals(hashPassword) ? UserStatus.EXISTS_CORRECT_PASSWORD
						: UserStatus.EXISTS_INCORRECT_PASSWORD;
			}
		} catch (Exception e) {
			System.out.println("An error has occurred." + e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				/* ignored */}
			try {
				stmt.close();
			} catch (SQLException e) {
				/* ignored */}
			try {
				conn.close();
			} catch (SQLException e) {
				/* ignored */}
		}
	}

	// does the user account exist in the DB table?
	public boolean doesUserExist() {
		// return true if the user account is found, regardless of the password
		// match
		return (userID>0);
	}

	// return if user email and password match a stored pair
	public boolean getIsValid() {
		return isValid;
	}

	//
	public int getUserID() {
		return isValid ? userID : 0;
	}
	
	// is the account locked from use?
	public boolean getIsLocked(){
		return isValid ? isLocked : false;
	}
	
	// has the administrator approved this account?
	public boolean getIsApproved(){
		return isValid ? isApproved : false;
	}
	
	// is this and administrative account?
	public boolean getIsAdmin(){
		return isValid ? isAdmin : false;
	}
	
	// prepare the data and validity flags
	public boolean doAdminAccountsExist() {
		boolean adminExistence = false;

		// Will eventually just reference the DBConnection.java class
		Connection conn = null;
		Statement stmt = null;
		String query = "";
		ResultSet rs = null;

		try {

			conn = DBConnection.getConnection();

			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			query = "select isAdmin from Users;";
			rs = stmt.executeQuery(query);

			// select and test the first returned row
			if (rs.next()) {
				adminExistence = true;
			}
		} catch (Exception e) {
			System.out.println("An error has occurred." + e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				/* ignored */}
			try {
				stmt.close();
			} catch (SQLException e) {
				/* ignored */}
			try {
				conn.close();
			} catch (SQLException e) {
				/* ignored */}
		}
		
		return adminExistence;
	}
	
}
