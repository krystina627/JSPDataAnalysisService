<%
	// Login.jsp
	// language test file
	
%>

<!DOCTYPE html >
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>BOM Diagnostic Page</title>
<link rel="stylesheet" href="css/common.css">
</head>

<%@ page import="java.util.*" %>
<body>

<%@ include file="common/banner.jspf" %>

<%
	Map<String, String> systemVariables = System.getenv();
	out.println("Number of system variables:" + systemVariables.size() + "<BR>");
	for(String x : systemVariables.keySet()){
		out.println( x + " : " + systemVariables.get(x) + "<BR>");
	}
%>

 
</body>
</html>
