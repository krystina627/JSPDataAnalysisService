
<%
	// ManualBOM_Input.jsp
	// Purpose: Form to manually input user's BOM data
	// Author: Krystina Poling
	// Date: Feb. 17, 2016
	// Modified for Servlet connection
	// Date: Feb: 25
	// Author: Nick Radonic
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

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="css/common.css">
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
<%
	// build up a default BOM name
	String Email = (String) request.getAttribute("Email");
	String dateS = ((Email == null) ? "" : Email) + "BOM ";
	dateS = dateS + (new Date().toString());
%>
<title>Manual BOM Form</title>
<script src="./javascript_jquery/jquery.js"></script>
<script>
	var k;

	$(document).ready(function() {
		k = 1;

		$('#manual_table tr:last').after(showData());

		$(".add").live('click', function() {

			k++;
			$('#manual_table tr:last').after(showData());
			$("#counter").val(k);

		});

		// strip non digits from quantity field
		$(".digit").keyup(function(e){this.value = this.value.replace(/\D/g,'');});
		$("input").keyup(function(e){this.value = this.value.replace(/,/g,' ');});
	});
	// summarize all row data and put in return variable
	function extractData() {
		var lineData = "";
		var totalD = [];
		//find lines, process into a string array
		$.each($("#manual_table tr.data"), function(i, v) {
			var t0 = $(v).find(".columnA").text();
			t0 = t0.match(/\d/g).join("");
			var t1 = $(v).find(".columnB").first("input").find("input").val();
			var t2 = $(v).find(".columnC").first("input").find("input").val();
			var t3 = $(v).find(".columnD").first("input").find("input").val();

			var lineD = [ t0, t1, t2, t3 ];
			totalD.push(JSON.stringify(lineD));
		});
		$("#total_data").val(totalD);
	}

	function lremove(l) {
		var lid = l.id;

		var rowno = lid.match(/\d+$/);
		if (rowno > 1) {
			$("#line" + rowno).remove();
		}
	}
	function showData() {

		var str = "<tr id='line"+k+ "' class='data'>"
				+ "<td class='columnA'>"
				+ "<font face='calibri' size='2' color='#'><b>"
				+ k
				+ ")</b></font>"
				+ "</td>"
				+ "<td class='columnB'>"
				+ "<input type='text' name='part_number"+k+"' id='part_number"+k+"' maxlength='20' size='20'>"
				+ "</td>"
				+ "<td class='columnC'>"
				+ "<input class='digit' type='text' name='qty"
				+ k
				+ "' id='qty"
				+ k
				+ "' value='' size='10' maxlength='20'>"
				+ "</td>"
				+ "<td class='columnD'>"
				+ "<input type='text' name='description"+k+"' id='description"+k+"' value='' maxlength='100' size='40'>"
				+ "</td>"
				+ "<td class='columnE'>"
				+ "<input type='button' class='add' value='+' height='15' width='15'/></td>"
				+ "<td class='columnF'";
		if (k === 1) {
			str += " style='display:none' ";
		}
		str += "><input type='button' value='x' id='remove"
				+ k
				+ "' onclick='lremove(this)' src='Delete.png' height='30' width='30'"
				+ "/></td></tr>";

		return str;
	}
	
</script>
</head>
<body>
	<div id=wrapper>
		<header> <%@ include file="common/banner.jspf"%>
		</header>
		<h3>
			User:
			<%=userName%></h3>
		<a id=back_button href="BOMmain.jsp">Main Page</a>
		<div id=manual_form>
			<h2>Manual BOM Input Form</h2>
			<form Method=POST name="frminfom" action="SaveManualForm"
				id="frminfom">
				<p>
					Enter New BOM Title:<input type="text" name="bom_title"
						id="bomnameinput" value='<%=dateS%>' maxlength="50" size="30"
						form="frminfom">
				</p>
				<table id=manual_table name="BOMTable">
					<tbody>
						<tr>
							<td class="columnA"></td>
							<td class="columnB">Part #</td>
							<td class="columnC">Qty</td>
							<td class="columnD">Description</td>
							<td class="columnE"></td>
							<td class="columnF"></td>
						</tr>
					</tbody>
				</table>
				<input type="hidden" value="1" id="counter"> <input
					type=submit value="submit" onclick="extractData()"> <input
					type="reset" value="clear">
			</form>
		</div>
	</div>
	<input id="total_data" style="visibility: hidden" name="total_data"
		form="frminfom" />
</body>
</html>

