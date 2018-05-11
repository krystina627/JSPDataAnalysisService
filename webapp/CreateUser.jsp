
<%
	// to prevent all framing of this content
	response.addHeader("X-FRAME-OPTIONS", "DENY");

	// to allow framing of this content only by this site
	response.addHeader("X-FRAME-OPTIONS", "SAMEORIGIN");

	// header enables the Cross-site scripting (XSS) filter built into most recent web browsers
	response.addHeader("X-XSS-Protection", "1; mode=block");

	// "nosniff", prevents Internet Explorer and Google Chrome from MIME-sniffing a response away from the declared content-type
	response.addHeader("X-Content-Type-Options", "nosniff");

	//Prevent caching of a JSP output
	response.setHeader("Cache-Control", "no-cache");
	response.setHeader("Pragma", "no-cache");
	response.setDateHeader("Expires", -1);
%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="java.io.*,java.util.*,java.sql.*"%>
<%
	// CreateUser.jsp
	// data entry screen for account creation
	// Author: Nick Radonic
	// Date: Feb. 10, 2016
%>
<%
	String userName = (String) session.getAttribute("userName");
	if (userName == null) {
		userName = "";
	}
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Create User</title>
<link rel="stylesheet" href="css/common.css">
<script type="text/javascript">
	function checkForm(createuserform) {
		if (createuserform.EMAIL.value == "") {
			alert("Error: Email cannot be blank!");
			createuserform.EMAIL.focus();
			return false;
		}
		var emailID = document.createuserform.EMAIL.value;
		atpos = emailID.indexOf("@");
		dotpos = emailID.lastIndexOf(".");

		if (atpos < 1 || (dotpos - atpos < 2)) {
			alert("Error: Incorrect Email Format! \n i.e. Someone@somewhere.com")
			document.createuserform.EMAIL.focus();
			return false;
		}

		if (createuserform.PASSWORD1.value != ""
				&& createuserform.PASSWORD1.value == createuserform.PASSWORD2.value) {
			if (createuserform.PASSWORD1.value.length < 6) {
				alert("Error: Password must contain at least six characters!");
				createuserform.PASSWORD1.focus();
				return false;
			}
			if (createuserform.PASSWORD1.value == createuserform.EMAIL.value) {
				alert("Error: Password must be different from Email Address!");
				form.pwd1.focus();
				return false;
			}
		} else {
			alert("Error: Please check that you've entered and confirmed your password!");
			createuserform.pwd1.focus();
			return false;
		}
		return true;
	}
	function stripspaces(input)
	{
	  input.value = input.value.replace(/\s/gi,"");
	  return true;
	}
</script>
</head>


<body>

	<div id=wrapper>
		<%@ include file="common/banner.jspf"%>
		<div>
			<h1>Administrator log in to create user</h1>
			<%=new java.util.Date()%><br>
			<br>

		</div>
		<div id=create_user_form>

			<form method=POST action="CreateUser" name="createuserform" onsubmit="return checkForm(this);">
				<p class="cup">
					Administrator ID: <INPUT class="cu" id="admin" TYPE=TEXT
						NAME=ADMINISTRATOR placeholder="Admin ID" value="<%=userName%>">
				</p>
				<br>
				<p class="cup">
					Admin Password: <INPUT class="cu" id="adminp" TYPE="password"
						NAME=PASSWORDA placeholder="Admin PSWD">
				</p>
				<br>
				<p class="cup" id=blankline>User Information</p>
				<br>

				<p class="cup">
					User Email:<INPUT class="cu" id="email" TYPE=TEXT NAME=EMAIL
						placeholder="user@abcd.com" maxlength="50" onkeydown="javascript:stripspaces(this)">
				</p>
				<br>
				<p class="cup">
					User Password: <INPUT class="cu" id="pswd1" TYPE="password"
						NAME=PASSWORD1 oninput="checkmatch()"
						placeholder="New User Password" maxlength="20" onkeydown="javascript:stripspaces(this)">
				</p>
				<br>
				<p class="cup">
					Duplicate Password: <INPUT class="cu" id="pswd2" TYPE="password"
						oninput="checkmatch()" NAME=PASSWORD2
						placeholder="New User Password" maxlength="20" onkeydown="javascript:stripspaces(this)">
				</p>
				<br>
				<p class="cup" id=matchcomment></p>
				<p class="cup">
					<input style="visibility: hidden" id="submitbutton" type=SUBMIT>
				</p>
			</form>
		</div>
	</div>
</body>
<script>
	function checkmatch() {
		var one = document.getElementById("pswd1").value;
		var two = document.getElementById("pswd2").value;
		var admin = document.getElementById("admin").value;
		var adminp = document.getElementById("adminp").value;

		if (one === two && one.length > 0 && admin.length > 0
				&& adminp.length > 0) {
			document.getElementById("submitbutton").style.visibility = "visible";
			document.getElementById("matchcomment").innerHTML = '';
		} else {
			document.getElementById("submitbutton").style.visibility = "hidden";
			document.getElementById("matchcomment").innerHTML = "passwords must match";
		}
	}
</script>
</html>