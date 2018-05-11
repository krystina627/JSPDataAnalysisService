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


<%
	if (session.isNew()) {
		response.sendRedirect("login.jsp");
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
		session.setAttribute("userName", "");
	}
%>
<%
	// SaveManualForm.jsp
	// Calls BOM class to process and save manual input
%>
<%@ page import="java.sql.*" %>
<%@ page import="billofmaterialssearch.BOM"%>

<% int userID = (Integer)session.getAttribute("userID"); %>
	<% ResultSet rs = null;
	String bomTitle = request.getParameter("bom_title");
	BOM manualBOM = new BOM(userID, bomTitle);
	int counter = Integer.parseInt(request.getParameter("counter"));
	for (int i = 1; i <= counter; i++) {
		try {
			String partNumber = request.getParameter("part_number" + i);
			String qty = request.getParameter("qty" + i);
			int intQty = Integer.parseInt(qty);
			String description = request.getParameter("description" + i);
			manualBOM.addComponent(partNumber, description,intQty);
		} catch (Exception e) {
			continue;
		}
	} %>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="css/common.css">
<title>Manual BOM Saved</title>
</head>
<body>
	<div id=wrapper>
		<header> <%@ include file="common/banner.jspf"%>
		</header>
		<a id=back_button href="BOMmain.jsp">Main Page</a>
		<div id= saved_bom_results>
		<h2>
			BOM Saved:
			<%=bomTitle%></h2>			
		<table id= saved_bom_table>
            <tr>
                <th>ID</th>
                <th>Part Number</th>
                <th>Description</th>
                <th>Quantity</th>
                <th>Creation Date</th>
            </tr>
            <%  
            rs = manualBOM.getComponentsTable(); 
            while (rs.next()) {
            	int componentID = rs.getInt("ComponentID");
            	int vendorID = rs.getInt("VendorID");
            	String partNumber = rs.getString("PartNumber");
            	String userDescription = rs.getString("Prt_Description");  
            	double price = rs.getDouble("PRICE");
            	int qty = rs.getInt("Quantity");
            	int qtyAvailable = rs.getInt("QuantityAvailable");
            	Timestamp createDate = rs.getTimestamp("CreateDate");	
          %> 
          <tr>
          <td><%= componentID %></td>
          <td><%= partNumber %></td>
          <td><%= userDescription %></td>
          <td><%= qty %></td>
          <td><%= createDate %></td> 
          </tr>
          <%}  
          %>
		</table>
		</div>
	</div>
</body>
</html>
