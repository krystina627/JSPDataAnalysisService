
<%
	// ViewSavedBOM.jsp
	// View Saved BOM/Previous Search Results
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
<%
	request.getSession(true);
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
		// don't let the attribute be reused...
		session.setAttribute("message", "");
	}
%>
<%@ page import="java.sql.*"%>
<%@ page import="java.text.*" %>
<%@ page import="billofmaterialssearch.BOM"%>

<%
	ResultSet rs = null;
	int bomID = Integer.parseInt(request.getParameter("selectBOM"));
%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="css/common.css">
<title>Previously BOM Saved</title>
</head>
<body>
	<div id=results_wrapper>
		<header> <%@ include file="common/banner.jspf"%>
		</header>
		<h3>
			User:
			<%=userName%></h3>
		<a id=back_button href="BOMmain.jsp">Main Page</a>
		<div id=saved_bom_results>
			<%
				BOM savedBOM = new BOM(bomID);
			String bomTitle = savedBOM.getBOMtitle();
			%>
			<h2>User: <%=userName%><br>
				BOM Saved: <%= bomTitle + "" %>
				<%=bomID%></h2>
			<table id=saved_bom_table>
				<tr>
					<th id=col1>ID</th>
					<th id=col2>Part Number</th>
					<th id=col3>Description</th>
					<th id=col4>Quantity</th>
					<th id=col5>Vendor</th>
					<th id=col6>Quantity Available</th>
					<th id=col7>Price</th>
					<th id=col8>Creation Date</th>
				</tr>
				<%
					rs = savedBOM.getComponentsTable();
					while (rs.next()) {
						int componentID = rs.getInt("ComponentID");
						int vendorID = rs.getInt("VendorID");
						String partNumber = rs.getString("PartNumber");
						String userDescription = rs.getString("Prt_Description");
						double price = rs.getDouble("PRICE");
						NumberFormat formatter = NumberFormat.getCurrencyInstance();
						String priceString = formatter.format(price);
						int qty = rs.getInt("Quantity");
						int qtyAvailable = rs.getInt("QuantityAvailable");
						Timestamp createDate = rs.getTimestamp("CreateDate");
				%>
				<tr>
					<td><%=componentID%></td>
					<td><%=partNumber%></td>
					<td><%=userDescription%></td>
					<td><%=qty%></td>
					<%if (vendorID == 2){ %>
					<td>Mouser</td> 
					<%}else{%>
					<td><%=vendorID%></td>
					<%} %>
					<td><%=qtyAvailable%></td>
					<td><%=priceString%></td>
					<td><%=createDate%></td>
				</tr>
				<%
					}
				%>
			</table>
		</div>
	</div>
</body>
</html>
