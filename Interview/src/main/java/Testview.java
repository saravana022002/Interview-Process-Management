

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
		String name=null;
		String date=null;
		String email=null;
		String viewer=null;
		HttpSession ses = request.getSession(false);
		if(ses != null && ses.getAttribute("name")!=null){
			name =ses.getAttribute("name").toString();
			date =ses.getAttribute("date").toString();
			email =ses.getAttribute("email").toString();
		try (java.sql.Connection con = DBUtil.getConnection();
			 PreparedStatement pst = con.prepareStatement("select status_value from users where name =? and date =? and email=? order by id desc limit 1")) {
			pst.setString(1, name);
			pst.setString(2, date);
			pst.setString(3, email);
			ResultSet rs = pst.executeQuery();
			if (!rs.next()) {
				response.sendRedirect("user.html");
				out.close();
				return;
			}
			viewer=rs.getString("status_value");
			if("allowed".equalsIgnoreCase(viewer)) {
				rd = request.getRequestDispatcher("view.jsp");
				rd.forward(request, response);
			}else {
				rd = request.getRequestDispatcher("Waiting.jsp"); 
				rd.forward(request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		out.close();
		}else {
			response.sendRedirect("user.html");
		}
	}
	}

	
