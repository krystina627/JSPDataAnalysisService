package login;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LoginForm
 */
@WebServlet(description = "Handles login processing from entry page", urlPatterns = { "/LoginForm" })
public class LoginForm extends HttpServlet {
	private static final long serialVersionUID = 1L;

	String email;
	String password;
	boolean authenticated = false;
	Authentication auth = null;
	int userID = 0;
	String userName;
	
	// path strings
	final String moveOnToBOMEntry = "BOMmain.jsp";
	final String retryLogin = "login.jsp";
	final String adminLink = "CreateUser.jsp";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginForm() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Create a session object if it is already not created.
		HttpSession session = request.getSession(true);
		// TODO Auto-generated method stub
		this.email = request.getParameter("email");
		this.password = request.getParameter("password");

		// check for string match
		authenticate();

		// Set response content type
		response.setContentType("text/html");
		// return userID as found
		session.setAttribute("userID", userID);
		session.setAttribute("userName", email);
		
		// default failure message and return to login
		String destination = retryLogin;
		String message = "Credential Failure: " + email;;
		if (authenticated) {
			// Admin goes only to Create User screen. User goes to BOM editing screen
			if(auth.getIsAdmin()){
				// successful administrator login
				destination = adminLink;
				message = "Administrator: " + email;
			} else {
				// successful user login
				destination = moveOnToBOMEntry;
				message = "Welcome: " + userName;
			}
		} 
		// return a message to the login screen
		session.setAttribute("message", message);
		// a flag so that screens know where it comes from
		session.setAttribute("fromLogin", "fromLogin");
		
		// Set response content type
		response.setContentType("text/html");

		response.setStatus(response.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", destination);
	}

	// creates the user and checks if they are valid
	private boolean authenticate() {
		// create an Authentication instance for the email password combination
		auth = new Authentication(email, password);

		// is the user authenticated?
		authenticated = auth.getIsValid();

		if (authenticated) {
			// if authenticated update the userID number
			userID = auth.getUserID();
			userName = email;
		}

		return authenticated;
	}
}
