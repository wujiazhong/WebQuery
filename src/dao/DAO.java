package dao;

import java.sql.*;

import db_connection.DB_Connection;
import t_user.T_User;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
/*import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;*/

public class DAO {
/*	private String db_driver = "com.mysql.jdbc.Driver";
	private String db_url = "jdbc:mysql://localhost:3306/jsp";
	private String db_user = "root";
	private String db_pwd = "123";
	private String db_table = "login_info";*/
	private final String db_driver = "com.ibm.db2.jcc.DB2Driver";
	private final String db_url = "jdbc:db2://9.110.83.168:50000/PMQNEW";
	private final String db_user = "db2inst1";
	private final String db_pwd = "db2inst1";
	
	public boolean verifyUser(final String username, final String password){
		boolean isVerified = false;
		Connection conn = DB_Connection.getConnection(this.db_driver, this.db_url, this.db_user, this.db_pwd);
		Statement stm= null;
	    ResultSet rs =null;
		
		try{
			String sql ="select * from T_USER where USERNAME='"+username+"' and PASSWORD='"+password+"'";
			stm = conn.createStatement();
	        rs = stm.executeQuery(sql);
	        if(rs.next())
	        {
	        	isVerified = true;	
	        }
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				rs.close();
				stm.close();
				conn.close();
			}catch(SQLException e){
				System.out.println("there is a sql error!");
				e.printStackTrace();
			}
		}
		return isVerified;
	}
	
	public T_User queryUserInfo(final String username){
		Connection conn = DB_Connection.getConnection(this.db_driver, this.db_url, this.db_user, this.db_pwd);
		Statement stm= null;
	    ResultSet rs =null;
	    T_User user = new T_User(username);
	    
	    try{
			String sql ="select * from T_USER where USERNAME='"+username+"'";
			stm = conn.createStatement();
	        rs = stm.executeQuery(sql);
	        if(rs.next())
	        {
	        	user.setUserTeam(rs.getString("USERTEAM"));
	        	user.setUserType(rs.getString("USERTYPE"));
	        	user.setName(rs.getString("NAME"));
	        	user.setGender(rs.getString("GENDER"));
	        }
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				rs.close();
				stm.close();
				conn.close();
			}catch(SQLException e){
				System.out.println("there is a sql error!");
				e.printStackTrace();
			}
		}
	    return user;
	}
	
	/*public Vector<String> getWellIndexList(final String userteam){
		Vector<String> well_index_list = new Vector<String>();
		Connection conn = DB_Connection.getConnection(this.db_driver, this.db_url, this.db_user, this.db_pwd);
		Statement stm= null;
	    ResultSet rs =null;
		
		try{
			String sql ="select * from T_USERTEAM_WELL where USERTEAM='"+userteam+"'";
			stm = conn.createStatement();
	        rs = stm.executeQuery(sql);
	        while(rs.next())
	        {
	        	well_index_list.add(rs.getString("JH"));	
	        }
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				rs.close();
				stm.close();
				conn.close();
			}catch(SQLException e){
				System.out.println("there is a sql error!");
				e.printStackTrace();
			}
		}
		return well_index_list;
	}*/
	
	public JSONArray getOperDateList(final String userteam){
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		final String well_name = "well_name";
		final String jl_date = "jl_date";
		final String xj_date = "xj_date";
		
		Connection conn = DB_Connection.getConnection(this.db_driver, this.db_url, this.db_user, this.db_pwd);
		Statement stm= null;
	    ResultSet rs =null;
	    
	    try{
	    	String sql ="select distinct JH, JLRQ, XJRQ "
					  + "from T_JH_T "
					  + "where JH in (select JH from T_USERTEAM_WELL where USERTEAM = '"+userteam+"')";
			stm = conn.createStatement();
	        rs = stm.executeQuery(sql);
	        
	        while(rs.next()){
	        	String rs_well_name = rs.getString("JH");
        		String rs_jl_date = rs.getString("JLRQ");
        		String rs_xj_date = rs.getString("XJRQ");
        		
        		try{
	        		jsonObj.clear();
	    			jsonObj.put(well_name, rs_well_name);
	    			jsonObj.put(jl_date, rs_jl_date);
	    			jsonObj.put(xj_date, rs_xj_date);
	    			jsonArray.add(jsonObj);
        		}catch(JSONException e){
        			e.printStackTrace();
        		}
	        }
	    }catch(SQLException e){
	    	e.printStackTrace();
	    }
	    return jsonArray;
	}
	
	public JSONArray getWellFixDateList(final String userteam){
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		String well_name = "well_name";
		String check_date = "check_date";
		String csmc = "well_oper";
		String sgnr = "well_content";
		
		Connection conn = DB_Connection.getConnection(this.db_driver, this.db_url, this.db_user, this.db_pwd);
		Statement stm= null;
	    ResultSet rs =null;

		try{
			/*Date sys_date = new Date(); 
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String sys_cur_date = dateFormat.format(sys_date);
	        
	        Calendar calendar = Calendar.getInstance();  
	        calendar.setTime(sys_date); 
	        calendar.add(Calendar.YEAR, -5);
	        sys_date = calendar.getTime();
	        String sys_last_year_date = dateFormat.format(sys_date);
			String sql ="select distinct JH, CSMC, SGNR, WGRQ "
					  + "from T_DBAT3001 "
					  + "where P02 in (select JH as P02 from T_USERTEAM_WELL where USERTEAM='"+userteam+"') "
					  + "and P30 between '"+sys_last_year_date+"' and '"+sys_cur_date+"' "
					  + "order by P02";*/
			String sql ="select distinct JH, CSMC, SGNR, WGRQ "
					  + "from T_DDCC03 "
					  + "where JH in (select JH from T_USERTEAM_WELL where USERTEAM = '"+userteam+"')";
			stm = conn.createStatement();
	        rs = stm.executeQuery(sql);
	        
	        while(rs.next())
	        {
	        	String rs_well_name = rs.getString("JH");
        		String rs_check_date = rs.getString("WGRQ");
        		String rs_csmc = rs.getString("CSMC");
        		String rs_sgnr = rs.getString("SGNR");
        		boolean isSameWell = false;
        		
        		for(int i=0;i<jsonArray.size();i++){
        			JSONObject temp_obj = (JSONObject)jsonArray.get(i);
        			if(rs_well_name.equals(temp_obj.getString(well_name))){
        				isSameWell = true;
        				
        				String str_date = temp_obj.getString(check_date);
        				str_date += ","+rs_check_date;
        				temp_obj.put(check_date, str_date);
        				
        				String str_csmc = temp_obj.getString(csmc);
        				str_csmc += ","+rs_csmc;
        				temp_obj.put(csmc, str_csmc);
        				
        				String str_sgnr = temp_obj.getString(sgnr);
        				str_sgnr += ","+rs_sgnr;
        				temp_obj.put(sgnr, str_sgnr);
        			}
        		}
        		if(!isSameWell)
        		{
		        	try{
		        		jsonObj.clear();
	        			jsonObj.put(well_name, rs_well_name);
	        			jsonObj.put(check_date, rs_check_date);
	        			jsonObj.put(csmc, rs_csmc);
	        			jsonObj.put(sgnr, rs_sgnr);
	        			jsonArray.add(jsonObj);
		        	}catch(JSONException e){
		        		e.printStackTrace();
		        	}
        		}
	        }
	        
	        for(int i=0;i<jsonArray.size();i++){
	        	try{
		        	JSONObject json_item = jsonArray.getJSONObject(i);
		        	String str_check_date = json_item.getString(check_date);
		        	json_item.put(check_date, str_check_date.split(","));
		        	String str_csmc = json_item.getString(csmc);
		        	json_item.put(csmc, str_csmc.split(","));
		        	String str_sgnr = json_item.getString(sgnr);
		        	json_item.put(sgnr, str_sgnr.split(","));
	        	}catch(JSONException e){
	        		e.printStackTrace();
	        	}
	        }

		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				rs.close();
				stm.close();
				conn.close();
			}catch(SQLException e){
				System.out.println("there is a sql error!");
				e.printStackTrace();
			}
		}

		return jsonArray;
	}
	
	/*public JSONArray getWellOperation(final JSONArray well_name_list){
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		final String wellname_key = "well_name";
		final String checkdate_key = "check_date";
		final String operation_key = "well_operation";
		final String content_key = "operation_content";
		
		String well_name_str = "";
		try{
			well_name_str = "(";
			for(int i=0;i<well_name_list.size();i++){
				JSONObject json_item = (JSONObject)well_name_list.get(i);
				well_name_str+="'"+json_item.getString(wellname_key)+"'";
				if(i+1 < well_name_list.size()){
					well_name_str+=",";
				}
			}
			well_name_str+=")";
		}catch(JSONException e){
			e.printStackTrace();
		}
		System.out.println(well_name_str);
		String regEx="^(2[0-9]{3}-[0-9]{2}-[0-9]{2})";  
        Pattern pattern = Pattern.compile(regEx);  
        Matcher matcher = pattern.matcher(operation_date); 
        String check_date = matcher.group(1);
		
		Connection conn = DB_Connection.getConnection(this.db_driver, this.db_url, this.db_user, this.db_pwd);
		Statement stm= null;
	    ResultSet rs =null;
		
		try{
		    String sql = "select JH, CSMC, SGNR, WGRQ from T_DDCC03 where JH in "+well_name_str;
		    System.out.println(sql);
	    	stm = conn.createStatement();
	        rs = stm.executeQuery(sql);
	        
	        while(rs.next()){
	        	System.out.println("in rs.next");
	        	String wellname_val = rs.getString("JH");
        		String operation_val = rs.getString("CSMC");
	        	String content_val = rs.getString("SGNR");
	        	String checkdate_val = rs.getString("WGRQ");
	        	
	        	try{
	        		jsonObj.put(wellname_key, wellname_val);
	        		jsonObj.put(checkdate_key, checkdate_val);
	        		jsonObj.put(operation_key, operation_val);
	        		jsonObj.put(content_key, content_val);
	        		jsonArray.add(jsonObj);
	        	}catch(JSONException e){
	        		e.printStackTrace();
	        	}
	        }
	    }catch(SQLException e){
	    	e.printStackTrace();
	    }finally{
	    	try{
				rs.close();
				stm.close();
				conn.close();
			}catch(SQLException e){
				System.out.println("there is a sql error!");
				e.printStackTrace();
			}
	    }
		return jsonArray;
	}*/
}
