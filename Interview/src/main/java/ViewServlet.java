

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
@WebServlet("/ViewServlet")
public class ViewServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSession session = request.getSession(false);
		if(session!=null&&session.getAttribute("aname")!=null){
		PrintWriter out=response.getWriter();
		String date = request.getParameter("date");
		if (date == null || date.trim().isEmpty()) {
			out.println("<html><body>Invalid date</body></html>");
			out.close();
			return;
		}

		date = date.trim();
		session.setAttribute("adate", date);
		List<Emp> list=EmpDao.getAllusersbydate(date);

		out.print("<!DOCTYPE html><html><head><meta charset='ISO-8859-1'>");
		out.print("<meta name='viewport' content='width=device-width, initial-scale=1'>");
		out.print("<title>Candidate List</title>");
		out.print("<link rel='preconnect' href='https://fonts.googleapis.com'>");
		out.print("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
		out.print("<link href='https://fonts.googleapis.com/css2?family=Merriweather:wght@700&family=Space+Grotesk:wght@400;500;600&display=swap' rel='stylesheet'>");
		out.print("<link rel='stylesheet' href='assets/app.css'>");
		out.print("</head><body><main class='page'><section class='card'>");
		out.print("<div class='spread'><h2>Test Attendees</h2><a class='btn ghost' href='start.jsp'>Back To Dashboard</a></div>");
		out.print("<p class='quiet'>Test date: <strong>" + escape(date) + "</strong></p>");
		out.print("<table>");
		out.print("<tr><th>Id</th><th>Name</th><th>Date</th><th>Email</th><th>City</th><th>Status</th><th>Block</th><th>Allow</th></tr>");
		for(Emp e:list){
			String status = e.getStatus() == null ? "unknown" : e.getStatus();
			String statusClass = "allowed".equalsIgnoreCase(status) ? "allowed" : "blocked";
			out.print("<tr><td>" + e.getId() + "</td><td>" + escape(e.getName()) + "</td><td>" + escape(e.getDate()) + "</td><td>" + escape(e.getEmail()) + "</td><td>" + escape(e.getCity()) + "</td><td><span class='pill " + statusClass + "'>" + escape(status) + "</span></td><td><a class='btn danger' href='BlockServlet?id=" + e.getId() + "'>Block</a></td><td><a class='btn' href='AllowServlet?id=" + e.getId() + "'>Allow</a></td></tr>");
		}
		out.print("</table></section></main></body></html>");
		
		out.close();
	}else {
		response.sendRedirect("admin.jsp");
	}
		
	}

	private static String escape(String value) {
		if (value == null) {
			return "";
		}
		return value.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}
}
