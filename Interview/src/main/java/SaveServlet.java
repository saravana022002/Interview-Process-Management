

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
@WebServlet("/SaveServlet")
public class SaveServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out=response.getWriter();
		String name=request.getParameter("name") == null ? "" : request.getParameter("name").trim();
		String date=request.getParameter("date") == null ? "" : request.getParameter("date").trim();
		String email=request.getParameter("email") == null ? "" : request.getParameter("email").trim();
		String city=request.getParameter("city") == null ? "" : request.getParameter("city").trim();
		String status ="blocked";

		if (name.isEmpty() || date.isEmpty() || email.isEmpty() || city.isEmpty()) {
			response.sendRedirect("user.html");
			out.close();
			return;
		}

		Emp e=new Emp();
		e.setName(name);
		e.setDate(date);
		e.setEmail(email);
		e.setCity(city);
		e.setStatus(status);

		int userId = EmpDao.saveAndReturnId(e);
		if(userId>0){
			int testDate;
			try {
				testDate = Integer.parseInt(date);
			} catch (NumberFormatException ex) {
				testDate = Integer.parseInt(date.replaceAll("[^0-9]", ""));
			}
			int testId = V2Dao.ensureTestForDate(testDate, "Test " + testDate, 30 * 60);
			int attemptId = V2Dao.createOrGetCandidateAttempt(userId, testId);

			HttpSession ses = request.getSession();
			ses.setAttribute("name", name);
			ses.setAttribute("email", email);
			ses.setAttribute("date", date);
			ses.setAttribute("userId", Integer.valueOf(userId));
			ses.setAttribute("attemptId", Integer.valueOf(attemptId));
			response.sendRedirect("startbtn.jsp");
			out.close();
			return;
		}
		response.sendRedirect("user.html");
		out.close();
	}

}
