package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DAO;
import net.sf.json.JSONException;

public class UpdateDatabase_do extends HttpServlet {
	private static final long serialVersionUID = 1L;  
	private String mb_input="";
	private String mb_output="";
	private String mb_trigger_file = "";
	private String mb_complete_flag_file = "";
	private String mb_stream_file = "";
	/**
	 * Constructor of the object.
	 */
	public UpdateDatabase_do() {
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

		DAO t_dao = new DAO();
		String stream = t_dao.updateDatabase(this.getMb_input(),this.getMb_output(),this.getMb_trigger_file(),this.getMb_complete_flag_file(),this.getMb_stream_file());
		
		try {
			PrintWriter out = response.getWriter();
			out.write(stream);
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
	public void init(ServletConfig config) throws ServletException {
		// Put your code here
		String mb_config_path = config.getInitParameter("mb_config_path"); 
		mb_config_path = UpdateDatabase_do.class.getResource("/"+mb_config_path).toString().substring(5); 
		
		Properties prop = new Properties();
		if(mb_config_path != null){
			try{
				File db_conn = new File(mb_config_path);
				FileInputStream in = new FileInputStream(db_conn);
				try{
					prop.load(in);
					this.setMb_input(prop.getProperty("mb_input"));
					this.setMb_output(prop.getProperty("mb_output"));
					this.setMb_trigger_file(prop.getProperty("mb_trigger_file"));
					this.setMb_complete_flag_file(prop.getProperty("mb_complete_flag_file"));
					this.setMb_stream_file(prop.getProperty("mb_stream_file"));
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}
		}
		
	}
	
	private String getMb_input(){
		return this.mb_input;
	}
	
	private void setMb_input(String mb_input) {
		this.mb_input = mb_input;
	}

	private String getMb_output(){
		return this.mb_output;
	}
	
	private void setMb_output(String mb_output) {
		this.mb_output = mb_output;
	}

	public String getMb_trigger_file() {
		return mb_trigger_file;
	}

	public void setMb_trigger_file(String mb_trigger_file) {
		this.mb_trigger_file = mb_trigger_file;
	}

	public String getMb_complete_flag_file() {
		return mb_complete_flag_file;
	}

	public void setMb_complete_flag_file(String mb_complete_flag_file) {
		this.mb_complete_flag_file = mb_complete_flag_file;
	}

	public String getMb_stream_file() {
		return mb_stream_file;
	}

	public void setMb_stream_file(String mb_stream_file) {
		this.mb_stream_file = mb_stream_file;
	}

}


