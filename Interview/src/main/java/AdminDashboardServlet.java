import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AdminDashboard")
public class AdminDashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("aname") == null) {
            response.sendRedirect("admin.jsp");
            return;
        }

        List<Map<String, Object>> tests = V2Dao.getAllTests();
        String tidText = request.getParameter("tid");
        String section = request.getParameter("section");
        if (section == null || section.trim().isEmpty()) {
            section = "tests";
        }
        int selectedTestId = -1;
        if (tidText != null) {
            try {
                selectedTestId = Integer.parseInt(tidText);
            } catch (Exception ex) {
                selectedTestId = -1;
            }
        }

        if (selectedTestId <= 0 && !tests.isEmpty()) {
            selectedTestId = ((Integer) tests.get(0).get("id")).intValue();
        }

        List<Map<String, Object>> questions = selectedTestId > 0
                ? V2Dao.getQuestionsByTest(selectedTestId)
                : Collections.<Map<String, Object>>emptyList();
        List<Map<String, Object>> candidates = selectedTestId > 0
                ? V2Dao.getCandidatesByTest(selectedTestId)
                : Collections.<Map<String, Object>>emptyList();

        request.setAttribute("tests", tests);
        request.setAttribute("selectedTestId", Integer.valueOf(selectedTestId));
        request.setAttribute("questions", questions);
        request.setAttribute("candidates", candidates);
        request.setAttribute("activeSection", section);

        RequestDispatcher rd = request.getRequestDispatcher("/admin-dashboard.jsp");
        rd.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
