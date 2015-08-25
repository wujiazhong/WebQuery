package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAO;
import net.sf.json.JSONException;

public class SetBaseDiag_do extends HttpServlet {
	private static final long serialVersionUID = 1L;  
	/**
	 * Constructor of the object.
	 */
	public SetBaseDiag_do() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8"); 
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		final String unioncode = request.getParameter("unioncode");
		final String well_name = request.getParameter("well_name");
		final String userteam = request.getParameter("userteam");
		final String diag_id = request.getParameter("diag_id");
		final String basedate = request.getParameter("basedate");
		final String basetime = request.getParameter("basetime");
		
		final String date_time = basedate+" "+basetime;

		DAO t_dao = new DAO();
		boolean isOKSetBaseDiag = t_dao.setBaseDiagID(unioncode, userteam, well_name, diag_id, date_time);
		
		try {
			PrintWriter out = response.getWriter();
			out.write(String.valueOf(isOKSetBaseDiag));
			out.flush();
			out.close();
			out = null;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}

