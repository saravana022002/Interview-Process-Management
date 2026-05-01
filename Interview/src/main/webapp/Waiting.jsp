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
if(ses!=null&&ses.getAttribute("name")!=null&&ses.getAttribute("attemptId")!=null){
	String name=ses.getAttribute("name").toString();
	int attemptId=Integer.parseInt(String.valueOf(ses.getAttribute("attemptId")));
%>
<main class="page">
    <section class="card" style="max-width:640px;">
        <span class="kicker">Status</span>
        <h2>Hi <%=name%></h2>
        <p id="statusText">Your request is saved. Please wait for admin approval before starting the test.</p>
        <div class="actions">
            <a class="btn" href="startbtn.jsp">Check Again</a>
            <a class="btn ghost" href="index.html">Home</a>
        </div>
    </section>
</main>
<script>
(function () {
    var attemptId = <%=attemptId%>;
    var statusText = document.getElementById("statusText");

    function onAllowed() {
        statusText.textContent = "You are allowed now. Redirecting to test window...";
        setTimeout(function () {
            location.href = "CandidateTest?attemptId=" + attemptId;
        }, 900);
    }

    function process(text) {
        if (!text) {
            return;
        }
        var parts = text.split("|");
        var status = (parts[0] || "").toLowerCase();
        if (status === "allowed" || status === "in_progress") {
            onAllowed();
        }
    }

    var socketUrl = (location.protocol === "https:" ? "wss://" : "ws://") + location.host + "<%=request.getContextPath()%>/ws/candidate/" + attemptId;
    try {
        var ws = new WebSocket(socketUrl);
        ws.onmessage = function (event) {
            var data = event.data || "";
            if (data === "UNBLOCKED") {
                onAllowed();
            }
        };
    } catch (e) {
    }

    setInterval(function () {
        fetch("CandidateStatus?attemptId=" + attemptId, { cache: "no-store" })
            .then(function (resp) {
                if (!resp.ok) {
                    return null;
                }
                return resp.text();
            })
            .then(process)
            .catch(function () {});
    }, 5000);
})();
</script>
<%}else{
	response.sendRedirect("user.html");
}%>
</body>
</html>
