
<%
	// BOMmain.jsp
	// Purpose: Main Screen of BOM Application. Links to all functionality
	
%>
<%// to prevent all framing of this content
response.addHeader( "X-FRAME-OPTIONS", "DENY" );

// to allow framing of this content only by this site
response.addHeader( "X-FRAME-OPTIONS", "SAMEORIGIN" );

// header enables the Cross-site scripting (XSS) filter built into most recent web browsers
response.addHeader("X-XSS-Protection","1; mode=block");

// "nosniff", prevents Internet Explorer and Google Chrome from MIME-sniffing a response away from the declared content-type
response.addHeader("X-Content-Type-Options", "nosniff");

// Prevent caching of a JSP output
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", -1); 
%>



<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.io.*,java.util.*,java.sql.*"%>
<%@ page import="billofmaterialssearch.DBConnection"%>
<%@ page import="billofmaterialssearch.UserBOMList"%>

	<%request.getSession(true);
	if (session.isNew()) {
		response.sendRedirect("login.jsp");
	}
	String userName = "";
	String message = "";
	boolean fromLoginHandler = false;
	String fromLogin = "";
	int userID = 0;
	try {
		userID = (Integer) session.getAttribute("userID");
	} catch (Exception e) {
		userID = 0;
	}

	// check if this is a new session or its coming from the LoginForm handler
	fromLogin = (String) session.getAttribute("fromLogin");

	if (fromLogin == null) {
		session.setAttribute("fromLogin", "");
		fromLoginHandler = false;
	} else {
		fromLoginHandler = true;

		userName = (String) session.getAttribute("userName");
		if (userName == null) {
			userName = "";
		}

		message = (String) session.getAttribute("message");
		if (message == null) {
			message = "";
		}
		// don't let the message attribute be reused...
		session.setAttribute("message", "");
	}
%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="css/common.css">

<title>BOM Home Page</title>
<script type="text/javascript">
function ValidateForm(searchform){
if (searchform.vendor.checked == false) /* && ( form.vendor[1].checked == false ) ) */
{
alert ( "Error: Please Select a Vendor!" ); 
return false;
}
return true;
}
</script>
</head>
<body>
	<div id=main_wrapper>
		<header>
			<%@ include file="common/banner.jspf"%>
		</header>
		<div id=left_column>
			<h3>
				User:
				<%=userName%></h3>
			<br>
			<h3>Start New BOM</h3>
			<!--  <a id=automaticBOM_button href="#">Upload BOM File(.csv)</a> <br>-->
			<a id=manualform_button href="ManualBOM_Input.jsp">Manually
				Create BOM and Enter Parts</a> <a id=pastecvsbom_button
				href="BOMCSVPASTE.jsp">Paste BOM<br>CSV Text
			</a> <br>
		</div>
		<div id=right_column>

			<h2>Bill of Materials Search</h2>
			<form name="searchfom" action="ProcessVendorSearch.jsp" method="POST" onsubmit="return ValidateForm(this);">
				<p>Select BOM:</p>

				<%
					UserBOMList userBOMList = new UserBOMList();
					ResultSet rs = userBOMList.getUserBOMList(userID);
				%>

				<select name="BOMchoice">
					<%
						while (rs.next()) {
							String bomTitle = rs.getString("BOM_Title");
							int bomID = rs.getInt("BOM_ID");
					%>
					<option value="<%=bomID%>"><%=bomID + " " + bomTitle%></option>
					<%
						}
					%>
				</select> <br>
				<fieldset>
					<legend>Choose Vendor/Vendors</legend>
					<input type="checkbox" name="vendor" value="2" checked />Mouser
					Electronics<br> 
					<!--  <input type="checkbox" name="vendor"
						value="digikey" />Digi-Key Electronics<br> -->
				</fieldset>
				<br> <input type="submit" value="Start Search"/>
			</form>
			<%
				userBOMList.closeConnection();
			%>
			<br>
			<div id=view_bom>
				<hr>
				<h3>View Previously Saved BOM</h3>
				<form name="searchfom" action="ViewSavedBOM.jsp" method="POST">
					<p>Select BOM to View:</p>

					<%
						UserBOMList savedBOMList = new UserBOMList();
						ResultSet rs2 = savedBOMList.getUserBOMList(userID);
					%>

					<select name="selectBOM">
						<%
							while (rs2.next()) {
								String bomTitle = rs2.getString("BOM_Title");
								int bomID = rs2.getInt("BOM_ID");
						%>
						<option value="<%=bomID%>"><%=bomID + " " + bomTitle%></option>
						<%
							}
						%>
					</select> <br> <br> <input type="submit" value="View Previous BOM" />
				</form>
				<%
					savedBOMList.closeConnection();
				%>
			</div>
		</div>
	</div>
</body>
</html>
