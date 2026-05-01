import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ManageQuestions")
public class ManageQuestionsServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("aname") == null) {
            response.sendRedirect("admin.jsp");
            return;
        }

		String returnTid = request.getParameter("returnTid");
		String returnSection = request.getParameter("returnSection");
		if (returnSection == null || returnSection.trim().isEmpty()) {
			returnSection = "questions";
		}
		String redirectUrl = (returnTid == null || returnTid.trim().isEmpty())
				? "AdminDashboard?section=" + returnSection
				: "AdminDashboard?tid=" + returnTid.trim() + "&section=" + returnSection;

		try {
			int testId = Integer.parseInt(request.getParameter("testId"));
            String question = request.getParameter("question") == null ? "" : request.getParameter("question").trim();
            String opt1 = request.getParameter("opt1") == null ? "" : request.getParameter("opt1").trim();
            String opt2 = request.getParameter("opt2") == null ? "" : request.getParameter("opt2").trim();
            String answer = request.getParameter("answer") == null ? "" : request.getParameter("answer").trim();

            if (!question.isEmpty() && !opt1.isEmpty() && !opt2.isEmpty() && !answer.isEmpty()) {
                V2Dao.addQuestion(testId, question, opt1, opt2, answer);
            }
        } catch (Exception e) {
        }

		response.sendRedirect(redirectUrl);
	}
}
