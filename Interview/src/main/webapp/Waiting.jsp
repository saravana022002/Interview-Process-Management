<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Waiting for Approval</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Merriweather:wght@700&family=Space+Grotesk:wght@400;500;600&display=swap" rel="stylesheet">
<link rel="stylesheet" href="assets/app.css">
</head>
<body>
<%
HttpSession ses = request.getSession(false);
if(ses!=null&&ses.getAttribute("name")!=null){  
	String name=ses.getAttribute("name").toString();
%>
<main class="page">
    <section class="card" style="max-width:640px;">
        <span class="kicker">Status</span>
        <h2>Hi <%=name%></h2>
        <p>Your request is saved. Please wait for admin approval before starting the test.</p>
        <div class="actions">
            <a class="btn" href="startbtn.jsp">Check Again</a>
            <a class="btn ghost" href="index.html">Home</a>
        </div>
    </section>
</main>
<%}else{
	response.sendRedirect("user.html");
}%>
</body>
</html>
