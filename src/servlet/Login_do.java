package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import db_conn_properties.DB_Conn_Properties;

import javax.servlet.ServletConfig;
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
	private DB_Conn_Properties db_conn = null;
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

		DAO t_user_query = new DAO(db_conn);
		
		
		try{
			boolean check_status = t_user_query.verifyUser(name, pwd) ? true : false;
			JSONObject jsonObj = new JSONObject();
			if(check_status){
				try{
					T_User user = t_user_query.queryUserInfo(name);
					
					try {
						jsonObj.put("msg", String.valueOf(check_status)); 
						jsonObj.put("userteam", user.getUserTeam());
						jsonObj.put("usertype", user.getUserType());
						jsonObj.put("unioncode", user.getUnionCode());
						jsonObj.put("userteam_name", user.getUserteamName());
						
						PrintWriter out = response.getWriter();
						out.write(jsonObj.toString());
						out.flush();
						out.close();
						out = null;
					} catch (JSONException e) {
						logger.error("Fail to get information from database!");
						e.printStackTrace();
					}
				}catch(Exception e){
					logger.error("Fail to queryUserInfo from database!");
					e.printStackTrace();
				}
			}else{
				logger.debug("No such user...");
				jsonObj.put("msg", String.valueOf(check_status)); 
				
				PrintWriter out = response.getWriter();
				out.write(jsonObj.toString());
				out.flush();
				out.close();
				out = null;
			} 
		}catch (JSONException e) {
			logger.error("Fail to get information from database!");
			e.printStackTrace();
		}		
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init(ServletConfig config) throws ServletException {
		// Put your code here
		String db_properties_location = config.getInitParameter("db_conn_properties_location"); 
		db_properties_location = Login_do.class.getResource("/"+db_properties_location).toString(); 
		
		//leave out "file:" in location string
		db_properties_location = db_properties_location.substring(5);

		System.out.println(db_properties_location);
		db_conn = new DB_Conn_Properties(db_properties_location);

	}

}
