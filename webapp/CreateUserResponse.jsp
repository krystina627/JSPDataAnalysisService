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
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Create Users</title>
<link rel="stylesheet" href="css/common.css">
</head>

<%@ page import="java.util.*"%>

<body>

	<div id=wrapper>
		<%@ include file="common/banner.jspf"%>
		<div >
			<h1>Create User Response</h1>
			<%=new Date()%><br><br>
<P><%= (String)session.getAttribute("CreateUserMessage") %></P>
		</div>
</body>
</html>
