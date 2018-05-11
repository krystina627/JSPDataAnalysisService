<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
 <% int bomidrand = (int) (Math.random() * 1000000)+1000000; %>
 <% String bomName = request.getParameter("BOMNAMEINPUT"); %>
 <% String bomCSV = request.getParameter("BOMINPUT");
 	String[] bomCSVData = bomCSV.split("\n");
 	
 %>
<% // set up a BOM instance %>
<jsp:useBean id="bomInstance" class="billofmaterialssearch.BOM" scope="session" >
   <jsp:setProperty name="bomInstance" property="uid" value="3" />
   <jsp:setProperty name="bomInstance" property="n" value="house" />
</jsp:useBean>


<% // create components for BOM
	for ( int line = 0; line< bomCSVData.length; line++){
		
		// break CSV lines into fields
		String[] compData = bomCSVData[line].split(",",3);
		
		// check for fields...
		String partID = compData[0];
		String description = compData.length==3 ? compData[2] : "No Description";
		int lenQ = compData.length;
		int qty = 0;
		if (lenQ>1) {
			try {qty = Integer.valueOf(compData[1]);} catch (NumberFormatException e) {qty = 0;} finally{}
		}

		// create component entry for BOM
		bomInstance.addComponent(partID, description, qty);
	}
	
	String bomString = bomInstance.toString();
	request.setAttribute("BOMName", bomName);
	request.setAttribute("BOMDescription", bomString);
%>

<jsp:forward page="bomdisplay.jsp" /> 
