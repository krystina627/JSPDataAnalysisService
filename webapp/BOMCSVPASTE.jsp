
<%
	// bominput.jsp
	// BOM input at file level paste
	// Author: Nick Radonic
	// Date: Feb. 10, 2016
%>
<%// to prevent all framing of this content
response.addHeader( "X-FRAME-OPTIONS", "DENY" );

// to allow framing of this content only by this site
response.addHeader( "X-FRAME-OPTIONS", "SAMEORIGIN" ); 

// header enables the Cross-site scripting (XSS) filter built into most recent web browsers
response.addHeader("X-XSS-Protection","1; mode=block");

// "nosniff", prevents Internet Explorer and Google Chrome from MIME-sniffing a response away from the declared content-type
response.addHeader("X-Content-Type-Options", "nosniff");

//Prevent caching of a JSP output
response.setHeader("Cache-Control","no-cache"); 
response.setHeader("Pragma","no-cache"); 
response.setDateHeader ("Expires", -1); 
%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>

<!DOCTYPE html >
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>BOM CSV File Input</title>
<link rel="stylesheet" href="css/common.css">
</head>

<%
	// check for direct entry, and redirect if new
	if (session.isNew()) {
		response.sendRedirect("login.jsp");
	}
	request.getSession(true);
	int userID = 0;
	try {
		userID = (Integer) session.getAttribute("userID");
	} catch (Exception e) {
		userID = 0;
	}

	String userName = "";
	String message = "";
	boolean fromLoginHandler = false;
	String fromLogin = "";

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
		// don't let the attribute be reused...
		session.setAttribute("message", "");
	}
%>
<body>
	<div id=wrapper>
		<header>
			<%@ include file="common/banner.jspf"%>
		</header>
		<%
			if (userName != null) {
		%>
		<H3 Style="text-align: center">
			User:
			<%=userName%></H3>
		<p>
			<a id=back_button href="BOMmain.jsp" style="float: right;">Main
				Page</a>
		</p>
		<br>
		<%
			}
		%>
		<%
			// build up a default BOM name
			String Email = (String) request.getAttribute("Email");
			String dateS = ((Email == null) ? "" : Email) + "BOM ";
			dateS = dateS + (new Date().toString());
		%>
		<br>
		<%
			// draw a box with and input for a title and another for CSV data
		%>
		<%
			// two hidden fields that actually get transmitted back
		%>

		<div id="paste_box">
			<H2 style="text-align: center">Paste your CSV BOM file here:</H2>


			<p style="text-align: center">Format: (Part #), (Qty),
				(Description), (optional fields)</p>
			<br>

			<FORM METHOD=POST ACTION="BOMPASTEINPUT" id="CSVFORM">

				<H3 style="text-align: center">
					BOM Name: <input style="" id="bomnameinput" name=BOMName maxlength="50"
						value="<%=dateS%>"></input>
				</H3>
				<p style="text-align: center">
					<TEXTAREA style="" id="bominput" TYPE=TEXT NAME=BOMData rows="40"
						cols="70" form="CSVFORM"></TEXTAREA>
				</p>

				<P style="text-align: center">
					<INPUT id=paste_submit TYPE=SUBMIT />
				</P>
			</FORM>
		</div>
	</div>
</body>
</html>


