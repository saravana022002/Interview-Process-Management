

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
@WebServlet("/AllowServlet")
public class AllowServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("aname") == null) {
			response.sendRedirect("admin.jsp");
			return;
		}

		String sid=request.getParameter("id");
		try {
			int id=Integer.parseInt(sid);
			EmpDao.updateallow(id);
		} catch (NumberFormatException e) {
		}
		response.sendRedirect("ViewdateServlet");
	}
}
