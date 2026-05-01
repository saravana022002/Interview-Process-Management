

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Storeresult
 */
@WebServlet("/Storeresult")
public class Storeresult extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Storeresult() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		HttpSession ses = request.getSession(false);
		if(ses!=null&& ses.getAttribute("name")!=null && ses.getAttribute("userId") != null) {
		String name = ses.getAttribute("name").toString();
		int userId = Integer.parseInt(String.valueOf(ses.getAttribute("userId")));
		String attemptIdText = request.getParameter("attemptId") == null ? "" : request.getParameter("attemptId").trim();
		if (attemptIdText.isEmpty()) {
			response.sendRedirect("user.html");
			out.close();
			return;
		}
		try {
		  int attemptId = Integer.parseInt(attemptIdText);
		  Map<String, Object> attempt = V2Dao.getAttemptForUser(attemptId, userId);
		  if (attempt == null) {
			  response.sendRedirect("user.html");
			  return;
		  }

		  Map<Integer, String> answers = new HashMap<Integer, String>();
		  Enumeration<String> parameterNames = request.getParameterNames();
		  while (parameterNames.hasMoreElements()) {
			  String key = parameterNames.nextElement();
			  if (key != null && key.startsWith("q_")) {
				  try {
					  int questionId = Integer.parseInt(key.substring(2));
					  answers.put(Integer.valueOf(questionId), request.getParameter(key));
				  } catch (NumberFormatException e) {
				  }
			  }
		  }

		  Map<String, Integer> scored = V2Dao.submitAttempt(attemptId, answers);
		  CandidateWebSocket.broadcastStatus(attemptId, "SUBMITTED");

		  int correct = scored.get("correct").intValue();
		  int wrong = scored.get("wrong").intValue();
		  int score = scored.get("score").intValue();
		  String date = String.valueOf(attempt.get("scheduled_date"));

		  out.println("<!DOCTYPE html><html><head><meta charset='ISO-8859-1'>");
		  out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
		  out.println("<title>Test Result</title>");
		  out.println("<link rel='preconnect' href='https://fonts.googleapis.com'>");
		  out.println("<link rel='preconnect' href='https://fonts.gstatic.com' crossorigin>");
		  out.println("<link href='https://fonts.googleapis.com/css2?family=Merriweather:wght@700&family=Space+Grotesk:wght@400;500;600&display=swap' rel='stylesheet'>");
		  out.println("<link rel='stylesheet' href='assets/app.css'>");
		  out.println("</head><body><main class='page'><section class='card' style='max-width:720px;'>");
		  out.println("<span class='kicker'>Result</span>");
		  out.println("<h2>Thanks, " + escape(name) + "</h2>");
		  out.println("<p class='quiet'>Test date: <strong>" + escape(date) + "</strong></p>");
		  out.println("<table><tr><th>Metric</th><th>Value</th></tr>");
		  out.println("<tr><td>Correct Answers</td><td>" + correct + "</td></tr>");
		  out.println("<tr><td>Wrong Answers</td><td>" + wrong + "</td></tr>");
		  out.println("<tr><td>Score</td><td>" + score + "%</td></tr></table>");
		  out.println("<div class='actions'><a class='btn' href='index.html'>Go To Home</a></div>");
		  out.println("</section></main></body></html>");
		  ses.invalidate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		}else {
			response.sendRedirect("user.html");
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
