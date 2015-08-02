package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

public class GetWellOperation_do extends HttpServlet {
	private static final long serialVersionUID = 1L;  
	/**
	 * Constructor of the object.
	 */
	public GetWellOperation_do() {
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
		final String well_name = "well_name";
		final String check_date = "check_date";
		request.setCharacterEncoding("utf-8"); 
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html; charset=utf-8");
		
		HttpSession hs = request.getSession(true);  
		JSONArray jsonArray = (JSONArray)hs.getAttribute("well_operation");
		System.out.println("operation array size: "+jsonArray.size());
		
		JSONObject jsonOperObj = new JSONObject();
		JSONArray jsonOperArray = new JSONArray();
		try {
			try{
				for(int i=0;i<jsonArray.size();i++){
					JSONObject json_item = (JSONObject)jsonArray.get(i);
					if(well_name.equals(json_item.getString("well_name")) && check_date.equals(json_item.getString("check_date"))){
						jsonOperObj.clear();
						jsonOperObj.put("well_operation", json_item.getString("well_operation"));
						jsonOperObj.put("operation_content", json_item.getString("operation_content"));
						jsonOperArray.add(jsonOperObj);
						break;
					}
				}
			}catch(JSONException e){
				e.printStackTrace();
			}
			PrintWriter out = response.getWriter();
			out.write(jsonOperArray.toString());
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
