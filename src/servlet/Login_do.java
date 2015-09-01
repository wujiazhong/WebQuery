package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAO;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import t_user.T_User;
import org.apache.log4j.Logger;

public class Login_do extends HttpServlet {
	private static final long serialVersionUID = 1L;  
	private static Logger logger = Logger.getLogger(Login_do.class);
	/**
	 * Constructor of the object.
	 */
	public Login_do() {
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
		String name=request.getParameter("username");
		String pwd=request.getParameter("password");
		request.setCharacterEncoding("utf-8"); 
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		
		logger.debug("enter login_do action...");
		DAO t_user_query = new DAO();
		try{
			logger.debug("before verifyUser...");
			boolean check_status = t_user_query.verifyUser(name, pwd) ? true : false;
			JSONObject jsonObj = new JSONObject();
			if(check_status){
				try{
					logger.debug("before queryUserInfo...");
					T_User user = t_user_query.queryUserInfo(name);
					logger.debug("after queryUserInfo");
					
					try {
						jsonObj.put("msg", String.valueOf(check_status)); 
						jsonObj.put("userteam", user.getUserTeam());
						jsonObj.put("usertype", user.getUserType());
						jsonObj.put("unioncode", user.getUnionCode());
						
						PrintWriter out = response.getWriter();
						out.write(jsonObj.toString());
						out.flush();
						out.close();
						out = null;
					} catch (JSONException e) {
						logger.error("fail to get information from database!");
						e.printStackTrace();
					}
				}catch(Exception e){
					logger.error("fail to queryUserInfo from database!");
					e.printStackTrace();
				}
			}else{
				logger.debug("no such user...");
				jsonObj.put("msg", String.valueOf(check_status)); 
				
				PrintWriter out = response.getWriter();
				out.write(jsonObj.toString());
				out.flush();
				out.close();
				out = null;
			} 
		}catch (JSONException e) {
			logger.error("fail to get information from database!");
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
