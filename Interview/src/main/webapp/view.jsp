<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%
HttpSession ses = request.getSession(false);
if (ses != null && ses.getAttribute("attemptId") != null) {
    response.sendRedirect("CandidateTest?attemptId=" + ses.getAttribute("attemptId"));
} else {
    response.sendRedirect("startbtn.jsp");
}
%>
