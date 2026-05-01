import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ManageTests")
public class ManageTestsServlet extends HttpServlet {

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
			returnSection = "tests";
		}
		String redirectUrl = (returnTid == null || returnTid.trim().isEmpty())
				? "AdminDashboard?section=" + returnSection
				: "AdminDashboard?tid=" + returnTid.trim() + "&section=" + returnSection;
		try {
            if ("create".equalsIgnoreCase(action)) {
                String name = request.getParameter("testName") == null ? "" : request.getParameter("testName").trim();
                int scheduledDate = Integer.parseInt(request.getParameter("scheduledDate"));
                int duration = Integer.parseInt(request.getParameter("durationMinutes"));
                V2Dao.createTest(name, scheduledDate, Math.max(1, duration) * 60);
            } else if ("duration".equalsIgnoreCase(action)) {
                int testId = Integer.parseInt(request.getParameter("testId"));
                int duration = Integer.parseInt(request.getParameter("durationMinutes"));
                V2Dao.updateTestDuration(testId, Math.max(1, duration) * 60);
            }
        } catch (Exception e) {
        }

		response.sendRedirect(redirectUrl);
	}
}
