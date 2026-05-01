<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Collections" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Admin Control Center</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Instrument+Sans:wght@400;500;600;700&family=Sora:wght@600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="assets/app.css">
</head>
<body class="admin-app-body">
<%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setHeader("Expires", "0");

session = request.getSession(false);
if(session == null || session.getAttribute("aname") == null){
    response.sendRedirect("admin.jsp");
    return;
}

List<Map<String, Object>> tests = (List<Map<String, Object>>) request.getAttribute("tests");
if (tests == null) {
    tests = Collections.<Map<String, Object>>emptyList();
}

Integer selectedTestIdObj = (Integer) request.getAttribute("selectedTestId");
int selectedTestId = selectedTestIdObj == null ? -1 : selectedTestIdObj.intValue();

List<Map<String, Object>> questions = (List<Map<String, Object>>) request.getAttribute("questions");
if (questions == null) {
    questions = Collections.<Map<String, Object>>emptyList();
}

List<Map<String, Object>> candidates = (List<Map<String, Object>>) request.getAttribute("candidates");
if (candidates == null) {
    candidates = Collections.<Map<String, Object>>emptyList();
}

String activeSection = String.valueOf(request.getAttribute("activeSection"));
if (activeSection == null || "null".equals(activeSection) || activeSection.trim().isEmpty()) {
    activeSection = "tests";
}
%>

<main class="admin-shell">
    <aside class="admin-rail">
        <div class="rail-brand">IPM</div>
        <button type="button" class="rail-btn" data-target="tests" title="Manage Tests">T</button>
        <button type="button" class="rail-btn" data-target="questions" title="Add Questions">Q</button>
        <button type="button" class="rail-btn" data-target="candidates" title="Candidates">C</button>
        <button type="button" class="rail-btn" data-target="live" title="Live Monitor">L</button>
        <div class="rail-spacer"></div>
        <form action="Logout" class="rail-logout">
            <input type="submit" class="danger" value="Logout">
        </form>
    </aside>

    <section class="admin-workspace">
        <header class="workspace-topbar">
            <div>
                <h1>Interview Control Center</h1>
                <p>Manage tests, question banks, candidates, and live runtime controls from one screen.</p>
            </div>
            <div class="admin-chip">Admin: <strong><%=session.getAttribute("aname")%></strong></div>
        </header>

        <section class="workspace-main">
            <section class="tab-panel panel-shell" data-panel="tests">
                <div class="panel-header">
                    <h3>Manage Tests</h3>
                    <span class="quiet">Default duration is 30 minutes</span>
                </div>

                <form action="ManageTests" method="post" class="panel-form">
                    <input type="hidden" name="action" value="create">
                    <input type="hidden" name="returnTid" value="<%=selectedTestId%>">
                    <input type="hidden" name="returnSection" value="tests">
                    <div class="field-grid three">
                        <label>Test Name
                            <input type="text" name="testName" placeholder="Java Screening" required>
                        </label>
                        <label>Scheduled Date
                            <input type="number" name="scheduledDate" placeholder="20260501" required>
                        </label>
                        <label>Duration (minutes)
                            <input type="number" name="durationMinutes" value="30" min="1" required>
                        </label>
                    </div>
                    <div class="actions"><input type="submit" value="Create Test"></div>
                </form>

                <table>
                    <tr><th>ID</th><th>Name</th><th>Date</th><th>Duration</th><th>Status</th><th>Select</th><th>Update</th></tr>
                    <%
                    for (Map<String, Object> test : tests) {
                        int testId = ((Integer) test.get("id")).intValue();
                        int durationMin = ((Integer) test.get("duration_seconds")).intValue() / 60;
                    %>
                    <tr>
                        <td><%=testId%></td>
                        <td><%=String.valueOf(test.get("name"))%></td>
                        <td><%=String.valueOf(test.get("scheduled_date"))%></td>
                        <td><%=durationMin%> min</td>
                        <td><span class="pill allowed"><%=String.valueOf(test.get("status"))%></span></td>
                        <td><a class="btn ghost" href="AdminDashboard?tid=<%=testId%>&section=tests">Open</a></td>
                        <td>
                            <form action="ManageTests" method="post" class="inline-form">
                                <input type="hidden" name="action" value="duration">
                                <input type="hidden" name="testId" value="<%=testId%>">
                                <input type="hidden" name="returnTid" value="<%=selectedTestId%>">
                                <input type="hidden" name="returnSection" value="tests">
                                <input type="number" name="durationMinutes" min="1" value="<%=durationMin%>" required>
                                <input type="submit" value="Save">
                            </form>
                        </td>
                    </tr>
                    <%
                    }
                    if (tests.isEmpty()) {
                    %>
                    <tr><td colspan="7">No tests created yet.</td></tr>
                    <% } %>
                </table>
            </section>

            <section class="tab-panel panel-shell" data-panel="questions">
                <div class="panel-header">
                    <h3>Add Questions</h3>
                    <span class="quiet">Selected test ID: <%=selectedTestId > 0 ? String.valueOf(selectedTestId) : "none"%></span>
                </div>

                <form action="ManageQuestions" method="post" class="panel-form">
                    <input type="hidden" name="returnTid" value="<%=selectedTestId%>">
                    <input type="hidden" name="returnSection" value="questions">
                    <div class="field-grid two">
                        <label>Test
                            <select name="testId" required>
                                <% for (Map<String, Object> test : tests) {
                                    int testId = ((Integer) test.get("id")).intValue(); %>
                                    <option value="<%=testId%>" <%=testId == selectedTestId ? "selected" : ""%>><%=test.get("name")%> - <%=test.get("scheduled_date")%></option>
                                <% } %>
                            </select>
                        </label>
                        <label>Question
                            <input type="text" name="question" required>
                        </label>
                        <label>Option 1
                            <input type="text" name="opt1" required>
                        </label>
                        <label>Option 2
                            <input type="text" name="opt2" required>
                        </label>
                        <label>Correct Answer
                            <input type="text" name="answer" placeholder="Type exactly opt1 or opt2" required>
                        </label>
                    </div>
                    <div class="actions"><input type="submit" value="Add Question"></div>
                </form>

                <table>
                    <tr><th>#</th><th>Question</th><th>Option 1</th><th>Option 2</th><th>Answer</th></tr>
                    <% int qIndex = 1;
                       for (Map<String, Object> q : questions) { %>
                        <tr>
                            <td><%=qIndex++%></td>
                            <td><%=q.get("question_text")%></td>
                            <td><%=q.get("opt1")%></td>
                            <td><%=q.get("opt2")%></td>
                            <td><%=q.get("answer")%></td>
                        </tr>
                    <% }
                       if (questions.isEmpty()) { %>
                        <tr><td colspan="5">No questions available for selected test.</td></tr>
                    <% } %>
                </table>
            </section>

            <section class="tab-panel panel-shell" data-panel="candidates">
                <div class="panel-header">
                    <h3>Candidate Access Control</h3>
                    <span class="quiet">Block/unblock instantly and add extra time for active attempts</span>
                </div>

                <table>
                    <tr><th>Attempt</th><th>Name</th><th>Email</th><th>Status</th><th>Ends At</th><th>Allow/Block</th><th>Add Time</th></tr>
                    <% for (Map<String, Object> c : candidates) {
                        int attemptId = ((Integer) c.get("attempt_id")).intValue();
                        String status = String.valueOf(c.get("status"));
                        String statusClass = "allowed".equalsIgnoreCase(status) || "in_progress".equalsIgnoreCase(status) ? "allowed" : "blocked";
                    %>
                    <tr>
                        <td><%=attemptId%></td>
                        <td><%=c.get("name")%></td>
                        <td><%=c.get("email")%></td>
                        <td><span class="pill <%=statusClass%>"><%=status%></span></td>
                        <td><%=c.get("ends_at") == null ? "-" : c.get("ends_at")%></td>
                        <td>
                            <div class="actions">
                                <form action="CandidateControl" method="post" class="inline-form">
                                    <input type="hidden" name="attemptId" value="<%=attemptId%>">
                                    <input type="hidden" name="action" value="allow">
                                    <input type="hidden" name="returnTid" value="<%=selectedTestId%>">
                                    <input type="hidden" name="returnSection" value="candidates">
                                    <input type="submit" value="Allow">
                                </form>
                                <form action="CandidateControl" method="post" class="inline-form">
                                    <input type="hidden" name="attemptId" value="<%=attemptId%>">
                                    <input type="hidden" name="action" value="block">
                                    <input type="hidden" name="returnTid" value="<%=selectedTestId%>">
                                    <input type="hidden" name="returnSection" value="candidates">
                                    <input type="submit" class="danger" value="Block">
                                </form>
                            </div>
                        </td>
                        <td>
                            <form action="CandidateControl" method="post" class="inline-form">
                                <input type="hidden" name="attemptId" value="<%=attemptId%>">
                                <input type="hidden" name="action" value="addTime">
                                <input type="hidden" name="returnTid" value="<%=selectedTestId%>">
                                <input type="hidden" name="returnSection" value="candidates">
                                <input type="number" name="minutes" min="1" value="5" required>
                                <input type="submit" class="secondary" value="Add">
                            </form>
                        </td>
                    </tr>
                    <% }
                       if (candidates.isEmpty()) { %>
                    <tr><td colspan="7">No candidate registrations yet for selected test.</td></tr>
                    <% } %>
                </table>
            </section>

            <section class="tab-panel panel-shell" data-panel="live">
                <div class="panel-header">
                    <h3>Live Monitor</h3>
                    <span class="quiet">Real-time events are sent over WebSocket</span>
                </div>
                <div class="live-card-grid">
                    <article class="mini-card">
                        <h4>Block / Unblock</h4>
                        <p>Candidates are blocked or unblocked instantly without page refresh.</p>
                    </article>
                    <article class="mini-card">
                        <h4>Time Extension</h4>
                        <p>Adding time updates the candidate timer immediately.</p>
                    </article>
                    <article class="mini-card">
                        <h4>Fallback Polling</h4>
                        <p>Every 5 seconds status is verified if websocket connection drops.</p>
                    </article>
                </div>
            </section>
        </section>
    </section>
</main>

<script>
(function () {
    var active = "<%=activeSection%>";
    var buttons = document.querySelectorAll(".rail-btn");
    var panels = document.querySelectorAll(".tab-panel");

    function show(target) {
        for (var i = 0; i < buttons.length; i++) {
            var isActiveBtn = buttons[i].getAttribute("data-target") === target;
            buttons[i].classList.toggle("active", isActiveBtn);
        }
        for (var j = 0; j < panels.length; j++) {
            var isActivePanel = panels[j].getAttribute("data-panel") === target;
            panels[j].classList.toggle("active", isActivePanel);
        }
        var url = new URL(window.location.href);
        url.searchParams.set("section", target);
        history.replaceState(null, "", url.toString());
    }

    for (var k = 0; k < buttons.length; k++) {
        buttons[k].addEventListener("click", function () {
            show(this.getAttribute("data-target"));
        });
    }

    show(active || "tests");
})();
</script>
</body>
</html>
