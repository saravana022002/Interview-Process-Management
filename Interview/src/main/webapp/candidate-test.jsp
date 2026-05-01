<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Collections" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Candidate Test Window</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Instrument+Sans:wght@400;500;600;700&family=Sora:wght@600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="assets/app.css">
</head>
<body class="candidate-app-body">
<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setHeader("Expires", "0");

Integer attemptIdObj = (Integer) request.getAttribute("attemptId");
if (attemptIdObj == null) {
    HttpSession ses = request.getSession(false);
    if (ses != null && ses.getAttribute("attemptId") != null) {
        response.sendRedirect("CandidateTest?attemptId=" + ses.getAttribute("attemptId"));
    } else {
        response.sendRedirect("startbtn.jsp");
    }
    return;
}

int attemptId = attemptIdObj.intValue();
String candidateName = String.valueOf(request.getAttribute("candidateName"));
String testName = String.valueOf(request.getAttribute("testName"));
List<Map<String, Object>> questions = (List<Map<String, Object>>) request.getAttribute("questions");
if (questions == null) {
    questions = Collections.<Map<String, Object>>emptyList();
}
Long endMillisObj = (Long) request.getAttribute("endMillis");
long endMillis = endMillisObj == null ? 0L : endMillisObj.longValue();
%>

<main class="candidate-shell">
    <aside class="candidate-rail">
        <div class="candidate-brand">TEST</div>
        <div class="candidate-rail-block">
            <span class="rail-label">Attempt</span>
            <strong>#<%=attemptId%></strong>
        </div>
        <div class="candidate-rail-block">
            <span class="rail-label">Total</span>
            <strong id="totalQuestions"><%=questions.size()%></strong>
        </div>
        <div class="candidate-rail-block">
            <span class="rail-label">Answered</span>
            <strong id="answeredCount">0</strong>
        </div>
        <div class="candidate-rail-actions">
            <button type="submit" form="examForm" class="btn">Submit</button>
        </div>
    </aside>

    <section class="candidate-workspace">
        <header class="workspace-topbar">
            <div>
                <h1><%=testName%></h1>
                <p>Candidate: <strong><%=candidateName%></strong></p>
            </div>
            <div class="timer-box">Time Left: <strong id="countdown">--:--</strong></div>
        </header>

        <section class="candidate-main-card">
            <div class="candidate-main-head">
                <h3>Question Window</h3>
                <span class="quiet">Select one option per question.</span>
            </div>

            <form action="Storeresult" method="post" id="examForm">
                <input type="hidden" name="attemptId" value="<%=attemptId%>">
                <div class="question-list">
                    <% int i = 1;
                       for (Map<String, Object> q : questions) {
                           int questionId = ((Integer) q.get("id")).intValue();
                    %>
                        <article class="candidate-question-card" id="q<%=questionId%>">
                            <div class="candidate-q-title"><span>Q<%=i%></span> <%=q.get("question_text")%></div>
                            <label class="radio-line"><input type="radio" name="q_<%=questionId%>" value="<%=q.get("opt1")%>"> A. <%=q.get("opt1")%></label>
                            <label class="radio-line"><input type="radio" name="q_<%=questionId%>" value="<%=q.get("opt2")%>"> B. <%=q.get("opt2")%></label>
                        </article>
                    <% i++; }
                       if (questions.isEmpty()) { %>
                        <p>No questions found for this test. Please contact admin.</p>
                    <% } %>
                </div>

                <div class="actions candidate-submit-row">
                    <input type="submit" value="Submit Test">
                </div>
            </form>
        </section>
    </section>
</main>

<div class="overlay hidden" id="blockedOverlay">
    <div class="card" style="max-width: 520px;">
        <span class="kicker">Access Update</span>
        <h3>Your test has been blocked by admin</h3>
        <p class="quiet">Your current answers are preserved, but further changes are disabled.</p>
        <div class="actions"><a class="btn ghost" href="Waiting.jsp">Go To Status Page</a></div>
    </div>
</div>

<script>
(function () {
    var attemptId = <%=attemptId%>;
    var endTs = <%=endMillis%>;
    var timerEl = document.getElementById("countdown");
    var formEl = document.getElementById("examForm");
    var blockedOverlay = document.getElementById("blockedOverlay");
    var answeredCountEl = document.getElementById("answeredCount");

    function formatLeft(ms) {
        if (ms <= 0) {
            return "00:00:00";
        }
        var total = Math.floor(ms / 1000);
        var hours = Math.floor(total / 3600);
        var mins = Math.floor((total % 3600) / 60);
        var secs = total % 60;
        return String(hours).padStart(2, "0") + ":" + String(mins).padStart(2, "0") + ":" + String(secs).padStart(2, "0");
    }

    function updateAnsweredCount() {
        var groups = {};
        var checked = formEl.querySelectorAll("input[type='radio']:checked");
        for (var i = 0; i < checked.length; i++) {
            groups[checked[i].name] = true;
        }
        answeredCountEl.textContent = Object.keys(groups).length;
    }

    function disableExam() {
        var inputs = formEl.querySelectorAll("input");
        for (var i = 0; i < inputs.length; i++) {
            if (inputs[i].type !== "hidden") {
                inputs[i].disabled = true;
            }
        }
        blockedOverlay.classList.remove("hidden");
    }

    function tick() {
        var left = endTs - Date.now();
        timerEl.textContent = formatLeft(left);
        if (left <= 0) {
            formEl.submit();
        }
    }

    formEl.addEventListener("change", function (event) {
        if (event.target && event.target.type === "radio") {
            updateAnsweredCount();
        }
    });

    updateAnsweredCount();

    if (endTs > 0) {
        tick();
        setInterval(tick, 1000);
    }

    function parseEvent(data) {
        if (data === "BLOCKED") {
            disableExam();
            return;
        }
        if (data === "UNBLOCKED") {
            location.reload();
            return;
        }
        if (data.indexOf("TIME_EXTENDED:") === 0) {
            var nextEnd = parseInt(data.split(":")[1], 10);
            if (!isNaN(nextEnd) && nextEnd > 0) {
                endTs = nextEnd;
            }
            return;
        }
        if (data === "SUBMITTED") {
            var currentPath = location.pathname;
            if (currentPath.indexOf("Storeresult") === -1) {
                location.href = "index.html";
            }
        }
    }

    var socketUrl = (location.protocol === "https:" ? "wss://" : "ws://") + location.host + "<%=request.getContextPath()%>/ws/candidate/" + attemptId;
    try {
        var ws = new WebSocket(socketUrl);
        ws.onmessage = function (event) { parseEvent(event.data || ""); };
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
            .then(function (text) {
                if (!text) {
                    return;
                }
                var parts = text.split("|");
                if (parts[0] === "blocked") {
                    disableExam();
                }
                if (parts.length > 1) {
                    var serverEnd = parseInt(parts[1], 10);
                    if (!isNaN(serverEnd) && serverEnd > 0) {
                        endTs = serverEnd;
                    }
                }
            })
            .catch(function () {});
    }, 5000);
})();
</script>
</body>
</html>
