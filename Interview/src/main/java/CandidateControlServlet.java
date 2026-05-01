import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/CandidateControl")
public class CandidateControlServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("aname") == null) {
            response.sendRedirect("admin.jsp");
            return;
        }

		String action = request.getParameter("action") == null ? "" : request.getParameter("action").trim();
		String returnTid = request.getParameter("returnTid");
		String returnSection = request.getParameter("returnSection");
		if (returnSection == null || returnSection.trim().isEmpty()) {
			returnSection = "candidates";
		}
		String redirectUrl = (returnTid == null || returnTid.trim().isEmpty())
				? "AdminDashboard?section=" + returnSection
				: "AdminDashboard?tid=" + returnTid.trim() + "&section=" + returnSection;
		int attemptId = -1;
        try {
            attemptId = Integer.parseInt(request.getParameter("attemptId"));
            if ("allow".equalsIgnoreCase(action)) {
                V2Dao.updateCandidateStatus(attemptId, "allowed");
                CandidateWebSocket.broadcastStatus(attemptId, "UNBLOCKED");
            } else if ("block".equalsIgnoreCase(action)) {
                V2Dao.updateCandidateStatus(attemptId, "blocked");
                CandidateWebSocket.broadcastStatus(attemptId, "BLOCKED");
            } else if ("addTime".equalsIgnoreCase(action)) {
                int minutes = Integer.parseInt(request.getParameter("minutes"));
                V2Dao.addExtraTime(attemptId, Math.max(1, minutes) * 60);
                Map<String, Object> attempt = V2Dao.getAttemptById(attemptId);
                Timestamp endsAt = attempt == null ? null : (Timestamp) attempt.get("ends_at");
                if (endsAt != null) {
                    CandidateWebSocket.broadcastStatus(attemptId, "TIME_EXTENDED:" + endsAt.getTime());
                }
            }
        } catch (Exception e) {
        }

		response.sendRedirect(redirectUrl);
	}
}
