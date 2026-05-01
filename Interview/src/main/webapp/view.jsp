<!DOCTYPE html>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<html>
<head>
    <meta charset="ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Test</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Merriweather:wght@700&family=Space+Grotesk:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="assets/app.css">
</head>
<body>
<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setHeader("Expires", "0");

HttpSession ses = request.getSession(false);
if(ses == null || ses.getAttribute("name") == null || ses.getAttribute("date") == null){
    response.sendRedirect("user.html");
    return;
}

String date = ses.getAttribute("date").toString();
String dbUrl = System.getenv("DB_URL");
if (dbUrl == null || dbUrl.trim().isEmpty()) {
    dbUrl = "jdbc:mysql://localhost:3306/employee?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
}
String dbUser = System.getenv("DB_USER");
if (dbUser == null || dbUser.trim().isEmpty()) {
    dbUser = "root";
}
String dbPassword = System.getenv("DB_PASSWORD");
if (dbPassword == null) {
    dbPassword = "";
}
%>
<main class="page">
<section class="card" style="max-width:860px;">
<div class="spread">
    <h2>Test Questions</h2>
    <span class="quiet">Test date: <strong><%=date%></strong></span>
</div>
<form action="Storeresult" method="post">
<%
try {
    Class.forName("com.mysql.cj.jdbc.Driver");
    try (Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
         PreparedStatement pst = con.prepareStatement("select * from questions where udate=? order by rand()")) {
        pst.setString(1, date);
        ResultSet rs = pst.executeQuery();
        int i = 1;
        boolean hasQuestions = false;

        while (rs.next()) {
            hasQuestions = true;
            int current_no = rs.getInt("quesno");
            String ques = rs.getString("question");
            String opt1 = rs.getString("opt1");
            String opt2 = rs.getString("opt2");
%>
<p><strong><%=i%>.</strong> <%=ques%></p>
<label class="radio-line"><input type="radio" name="ans<%=current_no%>" value="<%=opt1%>"> A. <%=opt1%></label>
<label class="radio-line"><input type="radio" name="ans<%=current_no%>" value="<%=opt2%>"> B. <%=opt2%></label>
<br><br>
<%
            i++;
        }

        if (!hasQuestions) {
%>
<p>No questions are available for this date yet. Please wait for admin.</p>
<%
        }
    }
} catch(Exception ex){
    out.print("Unable to load questions: " + ex.getMessage());
}
%>
<input type="hidden" name="date" value="<%=date%>">
<div class="actions">
    <input type="submit" name="submit" value="Finish Test">
</div>
</form>
</section>
</main>
</body>
</html>

