

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Add_question
 */
@WebServlet("/Add_question")
public class Add_question extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Add_question() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		if(session!=null&&session.getAttribute("aname")!=null){
			String testid = request.getParameter("date");
			String ques = request.getParameter("ques");
			String opt1 = request.getParameter("opt1");
			String opt2 = request.getParameter("opt2");
			String ans = request.getParameter("ans");

			if (testid == null || ques == null || opt1 == null || opt2 == null || ans == null ||
				testid.trim().isEmpty() || ques.trim().isEmpty() || opt1.trim().isEmpty() || opt2.trim().isEmpty() || ans.trim().isEmpty()) {
				response.sendRedirect("admin-dashboard.jsp");
				return;
			}

			try {
				int date = Integer.parseInt(testid.trim());
				int testId = V2Dao.ensureTestForDate(date, "Test " + date, 30 * 60);
				V2Dao.addQuestion(testId, ques.trim(), opt1.trim(), opt2.trim(), ans.trim());
				response.sendRedirect("AdminDashboard");
			} catch (NumberFormatException e) {
				response.sendRedirect("AdminDashboard");
			} catch (Exception e) {
				e.printStackTrace();
				response.sendRedirect("AdminDashboard");
			}
		}else {
			 response.sendRedirect("admin.jsp");
		}
		
	}

}
