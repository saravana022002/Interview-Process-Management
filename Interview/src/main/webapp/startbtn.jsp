<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Start Test</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Merriweather:wght@700&family=Space+Grotesk:wght@400;500;600&display=swap" rel="stylesheet">
<link rel="stylesheet" href="assets/app.css">
</head>
<body>
<%
String name=null;
String date=null;
String email=null;
HttpSession ses = request.getSession(false);
if(ses != null && ses.getAttribute("name")!=null){
	name =ses.getAttribute("name").toString();
	date =ses.getAttribute("date").toString();
	email =ses.getAttribute("email").toString();
	
%>
<main class="page">
    <section class="card" style="max-width:660px;">
        <span class="kicker">Candidate Area</span>
        <h2>Hello <%=name%></h2>
        <p>You are registered for test date <strong><%=date%></strong>. Click below to check your approval and start the test.</p>

		<form action="Testview" method="get">
			<input type="hidden" name="name" value="<%=name%>">
			<input type="hidden" name="date" value="<%=date%>">
			<input type="hidden" name="email" value="<%=email%>">
			<div class="actions">
				<input type="submit" value="Start Test">
				<a class="btn ghost" href="index.html">Home</a>
			</div>
		</form>
    </section>
</main>
<%
}else{
response.sendRedirect("user.html");
}
%>

</body>
</html>
