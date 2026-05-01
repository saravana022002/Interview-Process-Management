import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/CandidateTest")
public class CandidateTestServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession ses = request.getSession(false);
        if (ses == null || ses.getAttribute("userId") == null || ses.getAttribute("name") == null) {
            response.sendRedirect("user.html");
            return;
        }

        int userId = Integer.parseInt(String.valueOf(ses.getAttribute("userId")));
        String attemptIdText = request.getParameter("attemptId");
        if (attemptIdText == null || attemptIdText.trim().isEmpty()) {
            Object attemptIdObj = ses.getAttribute("attemptId");
            attemptIdText = attemptIdObj == null ? "" : String.valueOf(attemptIdObj);
        }

        if (attemptIdText.trim().isEmpty()) {
            response.sendRedirect("startbtn.jsp");
            return;
        }

        int attemptId;
        try {
            attemptId = Integer.parseInt(attemptIdText.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect("startbtn.jsp");
            return;
        }

        Map<String, Object> attempt = V2Dao.getAttemptForUser(attemptId, userId);
        if (attempt == null) {
            response.sendRedirect("user.html");
            return;
        }

        attempt = V2Dao.startAttemptIfAllowed(attemptId);
        String status = String.valueOf(attempt.get("status"));
        if ("blocked".equalsIgnoreCase(status)) {
            response.sendRedirect("Waiting.jsp");
            return;
        }
        if (!("allowed".equalsIgnoreCase(status) || "in_progress".equalsIgnoreCase(status))) {
            response.sendRedirect("Waiting.jsp");
            return;
        }

        int testId = ((Integer) attempt.get("test_id")).intValue();
        List<Map<String, Object>> questions = V2Dao.getQuestionsForTest(testId);
        Timestamp endsAt = (Timestamp) attempt.get("ends_at");
        long endMillis = endsAt == null ? 0L : endsAt.getTime();

        ses.setAttribute("attemptId", Integer.valueOf(attemptId));

        request.setAttribute("attemptId", Integer.valueOf(attemptId));
        request.setAttribute("candidateName", String.valueOf(ses.getAttribute("name")));
        request.setAttribute("testName", String.valueOf(attempt.get("test_name")));
        request.setAttribute("questions", questions);
        request.setAttribute("endMillis", Long.valueOf(endMillis));

        RequestDispatcher rd = request.getRequestDispatcher("/candidate-test.jsp");
        rd.forward(request, response);
    }
}
