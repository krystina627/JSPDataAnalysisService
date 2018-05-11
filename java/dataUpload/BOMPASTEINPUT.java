package dataUpload;

// BOMPASTEINPUT.java
// BOM input at file level paste
// Author: Nick Radonic
// Date: Feb. 10, 2016

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import billofmaterialssearch.BOM;

/**
 * Servlet implementation class BOMPASTEINPUT
 */
@WebServlet("/BOMPASTEINPUT")
public class BOMPASTEINPUT extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String BOMName;
	private String BOMData;
	private int userID = 0;
	private String userName;
	// path strings
	final String moveOnToBOMEntry = "BOMmain.jsp";
	final String retryLogin = "login.jsp";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BOMPASTEINPUT() {
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
		HttpSession session = request.getSession(true);
		// TODO Auto-generated method stub
		this.BOMName = request.getParameter("BOMName");
		this.BOMData = request.getParameter("BOMData");
		this.userName = (String) session.getAttribute("userName");
		try {
			this.userID = (Integer) session.getAttribute("userID");
		} catch (Exception e) {
			userID = 0;
		}
		// Set<String> parameterNames = request.getParameterMap().keySet();
		// Enumeration<String> attributeNames = session.getAttributeNames();
		// System.out.println("BOM PASTE INPUT: userName + userID: "+userName +
		// " " +userID);

		// Set response content type
		response.setContentType("text/html");

		String destination = moveOnToBOMEntry;

		String message = processBOM(userName, userID, BOMName, BOMData);

		// Set response content type
		response.setContentType("text/html");

		response.setStatus(response.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", destination);
	}

	private String processBOM(String uName, int uID, String BName, String BData) {
		// Prototype JSON data: ["1","","",""],["2","","",""],["3","","",""] =
		// [row, ID, qty, description]
		String message = "";
		// number of splits in input string
		final int PIECES_MIN = 3;
		// split at line breaks ],[
		String text = "\\n";
		Pattern pattern = Pattern.compile(text);
		String[] bomData0 = pattern.split(BData);
		// break lines into pieces
		ArrayList<String[]> brokenOutData = new ArrayList<String[]>();
		// parse lines
		for (String s : bomData0) {
			// break each line into 3 pieces
			String stemp = s.replace("\r","");
			// line number, part number, qty, description - ignore line number
			// for now
			ArrayList<String> lineS = new ArrayList<String>();
			// add dummy line number
			lineS.add("0");
			String[] columns = stemp.split(",", 3);
			// test for line size
			if (columns.length < PIECES_MIN) {
				message += " , " + s;
				continue;
			}
			// concatenate columns into an arrayList
			for (String ss : columns) {
				lineS.add(ss);
			}

			// strip out quotes ----- strip quotes out...
			for (int i = 0; i < lineS.size(); i++) {
				lineS.set(i, lineS.get(i).replaceAll("\"", ""));
			}
			// check for numerical quantity
			try {
				int temp = Integer.parseInt(lineS.get(2).trim());
				brokenOutData.add(lineS.toArray(new String[4]));
			} catch (Exception e) {
				message = "BOM creation error: " + e.toString();
			}
		}
		try {
			BOM bom = new BOM(uID, BName);
			for (String[] sa : brokenOutData) {
				String partNumber = sa[1];
				int quantity = Integer.parseInt(sa[2].trim());
				String userDescription = sa[3];
				bom.addComponent(partNumber, userDescription, quantity);
			}
		} catch (Exception e) {
			message = "BOM creation error: " + e.toString();
		}

		System.out.println("Broken out data: " + brokenOutData.toString());
		return message;
	}

}
