<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
"http://www.w3.org/TR/html4/loose.dtd" > 
      <%@ page import="java.sql.*" %> 
<%@ page import="java.io.*" %> 
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>view questions</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Merriweather:wght@700&family=Space+Grotesk:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="assets/app.css">
</head>
<body>
<main class="page">
<section class="card">
<div class="spread">
    <h2>Questions</h2>
    <a class="btn ghost" href="start.jsp">Back To Dashboard</a>
</div>
<%
session = request.getSession(false);
if(session!=null&&session.getAttribute("aname")!=null){         
String date = request.getParameter("date");
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
    try
    {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection con=(Connection)DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        PreparedStatement pst=con.prepareStatement("select * from questions where udate=?");
        pst.setString(1, date);
        ResultSet rs=pst.executeQuery();
    %><p class="quiet">Showing questions for date: <strong><%=date%></strong></p>
      <table>
      <thead>
          <tr>
             <th>Ques no</th>
             <th>date</th>
             <th>Question</th>
             <th>opt-1</th>
             <th>opt-2</th>
             <th>answer</th>
          </tr>
      </thead>
      <tbody>
        <%while(rs.next())
        {
            %>
            <tr>
                <td><%=rs.getInt("quesno") %></td>
                <td><%=rs.getInt("udate") %></td>
                <td><%=rs.getString("question") %></td>
                <td><%=rs.getString("opt1") %></td>
                <td><%=rs.getString("opt2") %></td>
                <td><%=rs.getString("answer") %></td>
            </tr>
            <%}%>
           </tbody>
        </table><br>
    <%}
    catch(Exception e){
        out.print(e.getMessage());%><br><%
    }
   }else{
        response.sendRedirect("admin.jsp");
    }
    %>
</section>
</main>
</body>
</html>

