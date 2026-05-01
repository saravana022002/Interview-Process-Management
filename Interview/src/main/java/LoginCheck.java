
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LoginCheck
 */
@WebServlet("/LoginCheck")
public class LoginCheck extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String aname = request.getParameter("name") == null ? "" : request.getParameter("name").trim();
		String apwd = request.getParameter("password") == null ? "" : request.getParameter("password").trim();

		if (aname.isEmpty() || apwd.isEmpty()) {
			response.sendRedirect("Error.jsp");
			return;
		}

		try (java.sql.Connection con = DBUtil.getConnection();
			 PreparedStatement pst = con.prepareStatement("select * from admins where aname =? and passwd =?")) {
			pst.setString(1, aname);
			pst.setString(2, apwd);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				HttpSession session=request.getSession();
				session.setAttribute("aname", aname);
				response.sendRedirect("AdminDashboard");
			} else {
				response.sendRedirect("Error.jsp");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
