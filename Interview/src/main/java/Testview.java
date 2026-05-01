

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Servlet implementation class Testview
 */
@WebServlet("/Testview")
public class Testview extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Testview() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out=response.getWriter();
		RequestDispatcher rd;
		HttpSession ses = request.getSession(false);
		if(ses != null && ses.getAttribute("name")!=null && ses.getAttribute("userId") != null){
			int userId = Integer.parseInt(String.valueOf(ses.getAttribute("userId")));
			int attemptId = ses.getAttribute("attemptId") == null ? -1 : Integer.parseInt(String.valueOf(ses.getAttribute("attemptId")));
			if (attemptId <= 0) {
				attemptId = V2Dao.getLatestAttemptForUser(userId);
				ses.setAttribute("attemptId", Integer.valueOf(attemptId));
			}

			Map<String, Object> attempt = V2Dao.getAttemptForUser(attemptId, userId);
			if (attempt == null) {
				response.sendRedirect("user.html");
				out.close();
				return;
			}

			String status = String.valueOf(attempt.get("status"));
			if("allowed".equalsIgnoreCase(status) || "in_progress".equalsIgnoreCase(status)) {
				V2Dao.startAttemptIfAllowed(attemptId);
				response.sendRedirect("CandidateTest?attemptId=" + attemptId);
			}else {
				rd = request.getRequestDispatcher("Waiting.jsp");
				rd.forward(request, response);
			}

		out.close();
		}else {
			response.sendRedirect("user.html");
		}
	}
	}

	
