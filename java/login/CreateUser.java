package login;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import billofmaterialssearch.DBConnection;

/**
 * Servlet implementation class CreateUser
 */
@WebServlet(description = "Allows creation of users, storing email address and encrypted password into Users table", urlPatterns = {
		"/CreateUser" })
public class CreateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// Administrator data
	String AdministratorID;
	String AdministratorPassword;

	// User data
	String userEmail; // typically email address
	String userPassword1;
	String userPassword2;
	String encryptedUserPassword;

	UserStatus AdminStatus;
	UserStatus UserStatus;
	String message;

	boolean wroteSuccessfully = false;

	int userID;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateUser() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		message = "";
		// Create a session object if it is already not created.
		HttpSession session = request.getSession(true);
		// TODO Auto-generated method stub
		this.AdministratorID = request.getParameter("ADMINISTRATOR");
		this.AdministratorPassword = request.getParameter("PASSWORDA");
		this.userEmail = request.getParameter("EMAIL");
		this.userPassword1 = request.getParameter("PASSWORD1");
		this.userPassword2 = request.getParameter("PASSWORD2");
		// create an authentication instance
		Authentication admin = new Authentication(AdministratorID, AdministratorPassword);
		// check if the administrator exists
		if (admin.getIsValid()) {
			serviceUser();
		} else if (admin.doAdminAccountsExist()) {
			message = "Bad administrator account/password";
		} else {
			message += " First administrator account being created " + AdministratorID;
			createAdministrator();
		}

		// response.getWriter().append("<p>message</p>" + "<br>Served at:
		// ").append(request.getContextPath());
		response.setContentType("text/html");
		session.setAttribute("CreateUserMessage", message);
		String destination = "CreateUserResponse.jsp";
		response.setStatus(response.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", destination);
	}

	// Check if the administrator account exists...if not create one
	private boolean validateOrCreateAdmin() {
		boolean result = false;
		return result;
	}

	private void serviceUser() {
		if (userPassword1.contentEquals(userPassword2)) {
			Authentication user = new Authentication(userEmail, userPassword1);
			if (user.doesUserExist()) {
				message += "Overwriting existing user account: " + userEmail + " ";
				writeToDB(userEmail, userPassword1, false);
				if (wroteSuccessfully) {
					message += "User account overwritten: " + userEmail + " ";
				} else {
					message += "User account write failed: " + userEmail + " ";
				}
			} else {
				writeToDB(userEmail, userPassword1, false);
				if (wroteSuccessfully) {
					message += "New user account created: " + userEmail + " ";
				} else {
					message += "New user account write failed: " + userEmail + " ";
				}
			}
		} else {
			message += "user passwords don't match";
		}
	}

	private void createAdministrator() {
		writeToDB(AdministratorID, AdministratorPassword, true);
	}

	// Writes to database with User parameters:
	private void writeToDB(String email, String password, boolean adminflag) {

		// translate boolean into integer for sql query
		int isAdmin = adminflag ? 1 : 0;
		// account is not locked
		int isLocked = 0;
		// account is approved by admin
		int isApproved = 1;

		// Encrypt the user-entered password, so it can be compared to the
		// already-encrypted password from the database

		String encryptpassword = null;
		try {
			encryptpassword = Encryption.encrypt(password);
		} catch (Exception e) {
			System.out.println("An error has occurred in password encryption.");
		}

		// Will eventually just reference the DBConnection.java class
		Connection conn = null;
		Statement stmt = null;
		String query = "";
		ResultSet rs = null;

		try {

			conn = DBConnection.getConnection();

			// See if the user already exists. If so, add a delete timestamp and
			// insert another user
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			query = "select userID, password, isAdmin, isLocked, isApproved from Users where Email = '" + email
					+ "' AND DeleteDate is null;";
//			stmt.executeQuery(query);
			rs = stmt.executeQuery(query);

			// select and update the first returned row, if any
			if (rs.next()) {
				query = "update  Users set DeleteDate=NOW() WHERE Email= '" + email + "' ; ";
				stmt.executeUpdate(query);

				query = "update  Users set Password='"+encryptpassword+"', DeleteDate=NULL WHERE Email= '" + email + "' LIMIT 1 ; ";
				stmt.executeUpdate(query);
			} else {

				// write a new User into the table
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				query = "insert into Users (email, password, isadmin, islocked, isapproved) values ('" + email + "', '"
						+ encryptpassword + "', " + isAdmin + ", " + isLocked + ", " + isApproved + ");";
				stmt.executeUpdate(query);

				// read back to confirm
				query = "select email, password, isadmin, islocked, isapproved from Users where email='" + email
						+ "' AND DeleteDate is null;";
				rs = stmt.executeQuery(query);

				// select and test the first returned row
				if (rs.next()) {
					if (email.contentEquals(rs.getString(1)) && encryptpassword.contentEquals(rs.getString(2))
							&& isAdmin == rs.getInt(3) && isLocked == rs.getInt(4) && isApproved == rs.getInt(5)) {
						wroteSuccessfully = true;
					}
				}
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
}
