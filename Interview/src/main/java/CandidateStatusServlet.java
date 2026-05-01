import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/CandidateStatus")
public class CandidateStatusServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            int userId = Integer.parseInt(String.valueOf(session.getAttribute("userId")));
            int attemptId = Integer.parseInt(request.getParameter("attemptId"));
            Map<String, Object> attempt = V2Dao.getAttemptForUser(attemptId, userId);
            if (attempt == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String status = String.valueOf(attempt.get("status"));
            Timestamp endsAt = (Timestamp) attempt.get("ends_at");
            long endMillis = endsAt == null ? 0L : endsAt.getTime();

            response.setContentType("text/plain");
            response.getWriter().write(status + "|" + endMillis);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
