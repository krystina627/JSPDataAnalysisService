
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
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
	// Login.jsp
	// language test file
	// Author: Nick Radonic
	// Date: Feb. 10, 2016
%>

<%
	String userName="";
	String message="";
	boolean fromLoginHandler = false;
	String fromLogin="";
	
	// check if this is a new session or its coming from the LoginForm handler
	if (!session.isNew()) {
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
			session.setAttribute("userName", "");
		}
	}
%>

<%@ page import="java.util.*"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Welcome Please Log In</title>
<link rel="stylesheet" href="css/common.css">
</head>
<body>
	<div id=wrapper>
		<%@ include file="common/banner.jspf"%>
		<div id=login_welcome>
			<h1>Welcome Please Log In to Gain Access</h1>
			<p><%=new Date()%></p>
			<br>
			<p><%=message%></p>

		</div>
		<div id=login_form style="">
			<h2>Login: Administrator Assigned</h2>
			<br> <br>
			<form method=POST action="LoginForm">
				<p>
					Email:<INPUT TYPE=TEXT NAME=email SIZE=30 maxlength="30"
						value="<%=fromLoginHandler ? userName : ""%>" placeholder="email">
				</p>
				<br>
				<p>
					Password: <INPUT TYPE="password" NAME=password SIZE=30 AUTOCOMPLETE='OFF' maxlength="30" placeholder="password">
				</p>
				<br>
				<p>
					<input type=SUBMIT>
				</p>
			</form>
		</div>
	</div>

</body>
</html>