<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>admin actions</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Merriweather:wght@700&family=Space+Grotesk:wght@400;500;600&display=swap" rel="stylesheet">
<link rel="stylesheet" href="assets/app.css">
</head>
<body>
<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setHeader("Expires","0");
session = request.getSession(false);
if(session!=null && session.getAttribute("aname")!=null){
%>
<main class="page">
    <div class="topbar">
        <span class="kicker">Admin Dashboard</span>
        <form action="Logout">
            <input type="submit" class="danger" value="Logout">
        </form>
    </div>

    <section class="grid">
        <article class="card col-4">
            <h3>Candidate Controls</h3>
            <p class="quiet">Load candidates by test date and update access status.</p>
            <form action="ViewServlet" method="get">
                <div class="field-grid">
                    <label>Test Date
                        <input type="number" name="date" required>
                    </label>
                </div>
                <div class="actions">
                    <input type="submit" value="Open Candidate List">
                </div>
            </form>
        </article>

        <article class="card col-4">
            <h3>Add Question</h3>
            <p class="quiet">Create a question with two options and the correct answer.</p>
            <form action="Add_question" method="post">
                <div class="field-grid">
                    <label>Test Date
                        <input type="number" name="date" required>
                    </label>
                    <label>Question
                        <input type="text" name="ques" required>
                    </label>
                    <label>Option 1
                        <input type="text" name="opt1" required>
                    </label>
                    <label>Option 2
                        <input type="text" name="opt2" required>
                    </label>
                    <label>Answer
                        <input type="text" name="ans" required>
                    </label>
                </div>
                <div class="actions">
                    <input type="submit" value="Add Question">
                </div>
            </form>
        </article>

        <article class="card col-4">
            <h3>View Questions</h3>
            <p class="quiet">See all questions already added for a selected date.</p>
            <form action="viewques.jsp">
                <div class="field-grid">
                    <label>Test Date
                        <input type="number" name="date" id="date" required>
                    </label>
                </div>
                <div class="actions">
                    <input type="submit" value="View Questions">
                </div>
            </form>
        </article>
    </section>
</main>
<%}else{
response.sendRedirect("admin.jsp");
}%>
</body>
</html>
