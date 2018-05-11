<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="billofmaterialssearch.DBConnection" %>
<%@ page import="billofmaterialssearch.BOM" %>
<%@ page import="java.sql.*" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Test JSP</title>
</head>

<body>
<% int k = 0; int bomID = 0; String s = "none";%>
<% Connection conn = DBConnection.getConnection();
	if(conn != null)
	k= 10;
	
	PreparedStatement SQLstatement = conn.prepareStatement("select BOM_ID,PartNumber from BOM;");
	ResultSet rs = SQLstatement.executeQuery();
	while(rs.next()){
    	bomID = rs.getInt("BOM_ID");
    	s = rs.getString("PartNumber");
    }
%>
	Connection Result = <%=k %> <br>
	Query Result = <%=bomID %>,<%=s %>
	
	
</body>
</html>