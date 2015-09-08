package dao;

/*import java.io.FileInputStream;*/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;

import db_connection.DB_Connection;
import t_user.T_User;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import java.util.regex.*;
import java.util.ArrayList;
import java.util.List;
/*import java.io.FileReader;
import java.io.BufferedReader;*/
import java.util.Date;
import java.util.Properties;
import java.text.SimpleDateFormat;

import db_conn_properties.DB_Conn_Properties;
/*import java.util.Properties;
import java.io.FileInputStream;
import java.io.File;*/

public class DAO {
/*	private String db_driver = "com.mysql.jdbc.Driver";
	private String db_url = "jdbc:mysql://localhost:3306/jsp";
	private String db_user = "root";
	private String db_pwd = "123";
	private String db_table = "login_info";*/
/*	private String db_driver = "com.ibm.db2.jcc.DB2Driver";
	private String db_url = "jdbc:db2://9.110.83.168:50000/PMQNEW";
	private String db_user = "db2inst1";
	private String db_pwd = "db2inst1";*/
	
	private String db_driver = "";
	private String db_url = "";
	private String db_user = "";
	private String db_pwd = "";
	
	public DAO(){
		
	}
	
	public DAO(DB_Conn_Properties db_conn){
		db_driver = db_conn.getDb_driver();
		db_url = db_conn.getDb_url();
		db_user = db_conn.getDb_user();
		db_pwd = db_conn.getDb_pwd();
	}
	
	/*public void initDBConnPara(){
		Properties prop = new Properties();
		File file = new File("db_conn.properties");	
		String ConfFilePath = file.getAbsolutePath();		
		String ConfFilePath = "db_conn.properties";
		try{
			String path = DAO.class.getClassLoader().getResource("").toURI().getPath();
			System.out.println("path:"+path);
			File new_file = new File(path + ConfFilePath);
			FileInputStream in = new FileInputStream(new_file);
			System.out.println(new_file.getAbsolutePath());

			try{
				prop.load(in);
				this.db_driver = prop.getProperty("DBDriver");
				this.db_url = prop.getProperty("DBUrl");
				this.db_user = prop.getProperty("DBUsername");
				this.db_pwd = prop.getProperty("DBPassword");
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
	}*/
	
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
	    ResultSet rs_union_code = null;
	    T_User user = new T_User(username);
	    
	    try{
	    	String sql = "select T0.*, T1.USERTEAM, T1.USERTEAM_NAME "
	    			   + "from DB2INST1.T_USER T0, DB2INST1.T_USERTEAM T1 "
	    			   + "where T0.USERNAME='"+username+"' and T0.USERTEAM = T1.USERTEAM";
			stm = conn.createStatement();
	        rs = stm.executeQuery(sql);
	        System.out.println(sql);
	        if(rs.next())
	        {
	        	user.setUserTeam(rs.getString("USERTEAM"));
	        	user.setUserType(rs.getString("USERTYPE"));
	        	user.setName(rs.getString("NAME"));
	        	user.setGender(rs.getString("GENDER"));
	        	user.setUserteamName(rs.getString("USERTEAM_NAME"));
	        	
	        	sql ="select distinct UNION_CODE from DB2INST1.T_PREDICT where USERTEAM='"+user.getUserTeam()+"'";
	        	rs_union_code = stm.executeQuery(sql);
	        	
	        	if(rs_union_code.next()){
	        		user.setUnionCode(rs_union_code.getString("UNION_CODE"));
	        	}
	        }
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				rs.close();
				rs_union_code.close();
				stm.close();
				conn.close();
			}catch(SQLException e){
				System.out.println("there is a sql error!");
				e.printStackTrace();
			}
		}
	    return user;
	}
	
	public JSONArray getOperDateList(final String userteam){
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		final String well_name = "well_name";
		final String date = "date";
		final String oper = "oepration";
		
		Connection conn = DB_Connection.getConnection(this.db_driver, this.db_url, this.db_user, this.db_pwd);
		Statement stm= null;
	    ResultSet rs_jl =null;
	    ResultSet rs_ql =null;
	    
	    try{
	    	String sql_jl = "select JH,JLRQ from DB2INST1.T_JH_JL where JH in (select JH from DB2INST1.T_USERTEAM_WELL where USERTEAM ='"+userteam+"')";
	    	String sql_ql = "select JH,QLRQ from DB2INST1.T_JH_QL where JH in (select JH from DB2INST1.T_USERTEAM_WELL where USERTEAM ='"+userteam+"')";
			stm = conn.createStatement();
	        rs_jl = stm.executeQuery(sql_jl);
	        
	        while(rs_jl.next()){
	        	String rs_well_name = rs_jl.getString("JH");
        		String rs_jl_date = rs_jl.getString("JLRQ");
        		String rs_oper = "jl";
        		boolean isSameWell = false;
        		
        		for(int i=0;i<jsonArray.size();i++){
        			JSONObject temp_obj = (JSONObject)jsonArray.get(i);
        			if(rs_well_name.equals(temp_obj.getString(well_name))){
        				isSameWell = true;
        				
/*        				String str_date = temp_obj.getString(date);
        				str_date += ","+rs_jl_date;
        				temp_obj.put(date, str_date);*/
        				@SuppressWarnings("unchecked")
        				List <String> jl_date_list = (List<String>)temp_obj.get(date);
        				jl_date_list.add(rs_jl_date);
        				temp_obj.put(date, jl_date_list);
        			}
        		}
        		if(!isSameWell)
        		{
		        	try{
		        		List <String> jl_date_item_list = new ArrayList<String>();
		        		jl_date_item_list.add(rs_jl_date);
		        		
		        		jsonObj.clear();
		        		jsonObj.put(oper,rs_oper);
	        			jsonObj.put(well_name, rs_well_name);
	        			jsonObj.put(date, jl_date_item_list);
	        			jsonArray.add(jsonObj);
		        	}catch(JSONException e){
		        		e.printStackTrace();
		        	}
        		}
	        }
	        
	        rs_ql = stm.executeQuery(sql_ql);
	        while(rs_ql.next()){
	        	String rs_well_name = rs_ql.getString("JH");
        		String rs_xj_date = rs_ql.getString("QLRQ");
        		String rs_oper = "xj";
        		boolean isSameWell = false;
        		
        		for(int i=0;i<jsonArray.size();i++){
        			JSONObject temp_obj = (JSONObject)jsonArray.get(i);
        			if(rs_oper.equals(temp_obj.getString(oper)) && rs_well_name.equals(temp_obj.getString(well_name))){
        				isSameWell = true;
        				
/*        				String str_date = temp_obj.getString(date);
        				str_date += ","+rs_xj_date;
        				temp_obj.put(date, str_date);*/
        				@SuppressWarnings("unchecked")
        				List <String> xj_date_list = (List<String>)temp_obj.get(date);
        				xj_date_list.add(rs_xj_date);
        				temp_obj.put(date, xj_date_list);
        			}
        		}
        		if(!isSameWell)
        		{
		        	try{
		        		List <String> xj_date_item_list = new ArrayList<String>();
		        		xj_date_item_list.add(rs_xj_date);
		        		
		        		jsonObj.clear();
		        		jsonObj.put(oper,rs_oper);
	        			jsonObj.put(well_name, rs_well_name);
	        			jsonObj.put(date, xj_date_item_list);
	        			jsonArray.add(jsonObj);
		        	}catch(JSONException e){
		        		e.printStackTrace();
		        	}
        		}
	        }
/*	        
	        for(int i=0;i<jsonArray.size();i++){
	        	try{
		        	JSONObject json_item = jsonArray.getJSONObject(i);
		        	String str_check_date = json_item.getString(date);
		        	json_item.put(date, str_check_date.split(","));
	        	}catch(JSONException e){
	        		e.printStackTrace();
	        	}
	        }*/
	    }catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				rs_jl.close();
				rs_ql.close();
				stm.close();
				conn.close();
			}catch(SQLException e){
				System.out.println("there is a sql error!");
				e.printStackTrace();
			}
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
			String sql ="select distinct JH, CSMC, SGNR, WGRQ "
					  + "from DB2INST1.T_DDCC03 "
					  + "where JH in (select JH from T_USERTEAM_WELL where USERTEAM = '"+userteam+"') order by 1";
			System.out.println(sql);
			stm = conn.createStatement();
			System.out.println("DB2INST1.T_DDCC03: "+sql);
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
        				
/*        				String str_date = temp_obj.getString(check_date);
        				str_date += ","+rs_check_date;
        				temp_obj.put(check_date, str_date);*/
        				
        				@SuppressWarnings("unchecked")
        				List <String> date_list = (List<String>)temp_obj.get(check_date);
        				date_list.add(rs_check_date);
        				temp_obj.put(check_date, date_list);
/*        				for(int j=0;j<date_list.size();j++){
        					System.out.println(date_list.get(j));
        				}*/
        				
/*        				String str_csmc = temp_obj.getString(csmc);
        				str_csmc += ","+rs_csmc;
        				temp_obj.put(csmc, str_csmc);*/
        				
        				@SuppressWarnings("unchecked")
        				List <String> csmc_list = (List<String>)temp_obj.get(csmc);
        				csmc_list.add(rs_csmc);
        				temp_obj.put(csmc, csmc_list);
/*        				
        				String str_sgnr = temp_obj.getString(sgnr);
        				str_sgnr += ","+rs_sgnr;
        				temp_obj.put(sgnr, str_sgnr);*/
        				
        				@SuppressWarnings("unchecked")
        				List <String> sgnr_list = (List<String>)temp_obj.get(sgnr);
        				sgnr_list.add(rs_sgnr);
        				temp_obj.put(sgnr, sgnr_list);
        				
        			}
        		}
        		if(!isSameWell)
        		{
		        	try{      		
		        		List <String> date_item_list = new ArrayList<String>();
		        		date_item_list.add(rs_check_date);
		        		
		        		List <String> csmc_item_list = new ArrayList<String>();
		        		csmc_item_list.add(rs_csmc);
		        		
		        		List <String> sgnr_item_list = new ArrayList<String>();
		        		sgnr_item_list.add(rs_sgnr);
		        		
		        		jsonObj.clear();
	        			jsonObj.put(well_name, rs_well_name);
	        			jsonObj.put(check_date, date_item_list);
	        			jsonObj.put(csmc, csmc_item_list);
	        			jsonObj.put(sgnr, sgnr_item_list);
/*	        			jsonObj.put(check_date, rs_check_date);
	        			jsonObj.put(csmc, rs_csmc);
	        			jsonObj.put(sgnr, rs_sgnr);*/
	        			jsonArray.add(jsonObj);
		        	}catch(JSONException e){
		        		e.printStackTrace();
		        	}
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
	
	public JSONArray getDiagID(final String unioncode, final String fix_date, final String jh){
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		final String date = "date";
		final String time = "time";
		final String diag_id = "diag_id";
		System.out.println("jh="+jh);
		System.out.println("date="+fix_date);
		Connection conn = DB_Connection.getConnection(this.db_driver, this.db_url, this.db_user, this.db_pwd);
		Statement stm= null;
	    ResultSet rs =null;
	    
	    try{
			String sql ="select distinct DIAGRAM_ID, P03 "
					  + "from DB2INST1.T_DBAT2070 "
					  + "where P01 = '"+unioncode+"' and P02='"+jh+"' and P03>'"+fix_date+"' order by P03";
			stm = conn.createStatement();
	        rs = stm.executeQuery(sql);
	        System.out.println(sql);
	        final int MAX_DIAG_FOR_ONE_DAY = 4; //每天最多显示4幅图
	        final int MAX_DATE = 5; //最多显示5天
	        ArrayList<String> max_diag_list = new ArrayList<String>();
	        while(rs.next()){
	        	String rs_fix_date = rs.getString("P03");
	        	String rs_diag_id = rs.getString("DIAGRAM_ID");
	        	String pattern = "(\\d{4}-\\d{2}-\\d{2})\\s(\\d{2}:\\d{2}:\\d{2})";
	        	Pattern reg = Pattern.compile(pattern);
	        	Matcher m = reg.matcher(rs_fix_date);
	        	if(m.groupCount() != 2){
	        		System.out.println("Wrong format of COLLECT_DATETIME in database");
	        		continue;
	        	}
	        	
	        	boolean isContinue = (m.find() && m.group(1).equals(fix_date)) || (max_diag_list.contains(m.group(1)));
	        	if(isContinue){
	        		continue;
	        	}
	        	
	        	boolean isSameDay = false;
        		for(int i=0;i<jsonArray.size();i++){
        			JSONObject temp_obj = (JSONObject)jsonArray.get(i);
        			if(m.group(1).equals(temp_obj.getString(date))){
        				isSameDay = true;
        				String str_time = temp_obj.getString(time);
        				str_time += ","+m.group(2);
        				temp_obj.put(time, str_time);
        				
        				String str_diag_id = temp_obj.getString(diag_id);
        				str_diag_id += ","+rs_diag_id;
        				temp_obj.put(diag_id, str_diag_id);
        				
        				if(str_time.split(",").length == MAX_DIAG_FOR_ONE_DAY){
        					max_diag_list.add(m.group(1));
        				}
        			}
        		}
        		if(!isSameDay)
        		{
		        	try{
		        		jsonObj.clear();
	        			jsonObj.put(date, m.group(1));
	        			jsonObj.put(time, m.group(2));
	        			jsonObj.put(diag_id, rs_diag_id);
	        			jsonArray.add(jsonObj);
		        	}catch(JSONException e){
		        		e.printStackTrace();
		        	}
        		}
        		
        		if(jsonArray.size() == MAX_DATE)
        			break;
	        }
	        for(int i=0;i<jsonArray.size();i++){
	        	try{
		        	JSONObject json_item = jsonArray.getJSONObject(i);
		        	String str_time = json_item.getString(time);
		        	json_item.put(time, str_time.split(","));
		        	
		        	String str_diag_id = json_item.getString(diag_id);
		        	json_item.put(diag_id, str_diag_id.split(","));
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
	
	public boolean setBaseDiagID(final String unioncode, final String userteam, final String well_name, final String diag_id,String date_time){
		Connection conn = DB_Connection.getConnection(this.db_driver, this.db_url, this.db_user, this.db_pwd);
		Statement stm= null;
	    ResultSet rs =null;
	    ResultSet rs_query =null;
	    ResultSet rs_sel_base = null;
	    ResultSet rs_get_diag = null;
	    boolean flag = true;
	    
	    Date sys_date = new Date(); 
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String sys_cur_date = dateFormat.format(sys_date);
        String sys_cur_time = timeFormat.format(sys_date);
        String sys_curDateTime = datetimeFormat.format(sys_date);
        
	    
	    try{
	    	String sql_first_base = "select INDICATOR_BASE_ID from DB2INST1.T_USERTEAM_WELL T0 "
					  			  + "where (JH='"+well_name+"' and UNION_CODE='"+unioncode+"')";
			
			String sql_init_base = "update DB2INST1.T_USERTEAM_WELL set INDICATOR_BASE_ID='"+diag_id+"' "
								 + "where (JH='"+well_name+"' and UNION_CODE='"+unioncode+"')";
			
			String sql_insert_base_diag_id = "insert into DB2INST1.T_INDI_ADD_MAIN (JH,USERTEAM,UNION_CODE,INDICATOR_BASE_DT,UPDATE_DATETIME,UPDATE_DATE,UPDATE_TIME) "
					                       + "values ('"+well_name+"','"+userteam+"','"+unioncode+"', TO_DATE('"+date_time+"', 'YYYY-MM-DD HH24:MI:SS'), "
					                       + "TO_DATE('"+sys_curDateTime+"', 'YYYY-MM-DD HH24:MI:SS'), "
					                       + "'"+sys_cur_date+"', "
					                       + "'"+sys_cur_time+"')";
			
			String sql_update_base_diag_id = "update DB2INST1.T_INDI_ADD_MAIN "
					                       + "set INDICATOR_BASE_DT=TO_DATE('"+date_time+"', 'YYYY-MM-DD HH24:MI:SS'),"
					                       + "UPDATE_DATETIME = TO_DATE('"+sys_curDateTime+"', 'YYYY-MM-DD HH24:MI:SS'),"
					                       + "UPDATE_DATE = '"+sys_cur_date+"',"
					                       + "UPDATE_TIME=  '"+sys_cur_time+"' "
					                       + "where JH='"+well_name+"'";
			
			String sql_query_indi_id = "select INDICATOR_OVERLAP_ID from DB2INST1.T_INDI_ADD_MAIN "
					                 + "where (JH='"+well_name+"' and UNION_CODE='"+unioncode+"')";
			
			stm = conn.createStatement();
	        rs = stm.executeQuery(sql_first_base);
	        
	        boolean isFirstTimeSetBaseDiag = false;
	        if(rs.next()){
	        	int indi_base_id = rs.getInt("INDICATOR_BASE_ID");
	        	stm.executeUpdate(sql_init_base);
	        	if(indi_base_id == -1){
	        		System.out.println(sql_insert_base_diag_id);
	        		stm.executeUpdate(sql_insert_base_diag_id);
	        		isFirstTimeSetBaseDiag = true;
	        	}else{
	        		stm.executeUpdate(sql_update_base_diag_id);
	        	}
	        	System.out.println("after update base_id in T_INDI_ADD_MAIN");
	        	System.out.println(sql_query_indi_id);
	        	rs_query = stm.executeQuery(sql_query_indi_id);
	        	if(rs_query.next()){
	        		int indi_id = rs_query.getInt("INDICATOR_OVERLAP_ID");
	        		System.out.println("overlap-id: "+indi_id);
	        		String sql_sel_base = "SELECT T0.P07 AS CHONGCHENG,"
							            + "T0.P08 AS CHONGCI,"
							            + "T1.DIAGRAM_ID AS DIAGRAM_ID,"
							            + "T1.LOAD_MAX AS LOAD_MAX,"
							            + "T1.LOAD_MIN AS LOAD_MIN,"
							            + "T1.ELECTRICITY_MAX AS ELECTRICITY_MAX,"
							            + "T1.ELECTRICITY_MIN AS ELECTRICITY_MIN,"
							            + "T1.AREA AS AREA,"
							            + "T0.P01 AS P01,"
							            + "T0.P02 AS P02,"
							            + "T0.P03 AS P03,"
							            + indi_id + " AS INDICATOR_OVERLAP_ID,"
							            + "'"+userteam+"'" + " AS USERTEAM,"
							            + "0 AS LOAD_MIN_ADD,"
							            + "0 AS LOAD_MAX_ADD,"
							            + "0 AS INDICATOR_ID,"
							            + "0 AS AREA_ADD "
	        		                    + "FROM  DB2INST1.T_DBAT2071 T0, DB2INST1.T_DBAT2070 T1 "
	        	                        + "WHERE (((T1.P03 = TO_DATE('"+date_time+"', 'YYYY-MM-DD HH24:MI:SS')) " 
	        	                        + "AND (T1.P01 = '"+unioncode+"')) " 
	        				            + "AND (T1.P02 = '"+well_name+"')) " 
	        			                + "AND (T0.P01 = T1.P01) " 
	        			                + "AND (T0.P02 = T1.P02) " 
	        			                + "AND (T0.P03 = T1.P03)";
	        		System.out.println(sql_sel_base);
	        		rs_sel_base = stm.executeQuery(sql_sel_base);
	        		System.out.println("obtain base info in 2070 and 2071");
	        		if(rs_sel_base.next()){
	        			double load_max = rs_sel_base.getDouble("LOAD_MAX");
	        			double load_min = rs_sel_base.getDouble("LOAD_MIN");
	        			//delete old records of diagram in T_INDI_ADD_DIAGRAM
	        			System.out.println(isFirstTimeSetBaseDiag);
	        			if(!isFirstTimeSetBaseDiag){
		        			String sql_del_old_records = "delete from DB2INST1.T_INDI_ADD_DIAGRAM "
		        					                   + "where (JH='"+well_name+"' and UNION_CODE='"+unioncode+"' and INDICATOR_OVERLAP_ID="+indi_id+")";
		        			System.out.println("del:"+sql_del_old_records);
		        			int test = stm.executeUpdate(sql_del_old_records);
		        			System.out.println("del entries: "+test);
		        			System.out.println("del old records");
	        			}
	        			
	        			
	        			
	        			String sql_get_diag = "SELECT T0.P07 AS CHONGCHENG, "
			                                + "T0.P08 AS CHONGCI, "
			                                + "T1.DIAGRAM_ID AS DIAGRAM_ID, "
			                                + "T1.LOAD_MAX AS LOAD_MAX, "
			                                + "T1.LOAD_MIN AS LOAD_MIN, "
			                                + "T1.ELECTRICITY_MAX AS ELECTRICITY_MAX, "
			                                + "T1.ELECTRICITY_MIN AS ELECTRICITY_MIN, "
			                                + "T1.AREA AS AREA,T0.P01 AS P01,T0.P02 AS P02,T0.P03 AS P03, "
			                                + indi_id + " AS INDICATOR_OVERLAP_ID, "
			                                + "'"+userteam+"'" + " AS USERTEAM, "
			                                + "(T1.LOAD_MIN - "+load_max+") AS LOAD_MIN_ADD, "
			                                + "(T1.LOAD_MAX - "+load_min+") AS LOAD_MAX_ADD, "
			                                + "(T1.AREA - 0.00000000000000000000e+00) AS AREA_ADD "
			    				            + "FROM DB2INST1.T_DBAT2071 T0,DB2INST1.T_DBAT2070 T1 "
			    				            + "WHERE (((T1.P01 = '"+unioncode+"') AND (T1.P02 = '"+well_name+"')) "
			    				            + "AND (TIMESTAMPDIFF(2,CHAR(TIMESTAMP(T0.P03)-TIMESTAMP('"+date_time+"')))>=0)) " 
			    					        + "AND (T0.P01 = T1.P01) AND (T0.P02 = T1.P02) AND (T0.P03 = T1.P03) ORDER BY 11 ASC";
	        			System.out.println("diag:"+sql_get_diag);
	        			rs_get_diag = stm.executeQuery(sql_get_diag);
	        			System.out.println("get update info whose date>basedate");
	        			
	        			String sql_insert_diag = "INSERT INTO DB2INST1.T_INDI_ADD_DIAGRAM "
	        					               + "(INDICATOR_OVERLAP_ID,USERTEAM,UNION_CODE,JH,INDICATOR_ID,DIAGRAM_ID,COLLECT_DATETIME,LOAD_MAX,LOAD_MIN,ELECTRICITY_MAX,ELECTRICITY_MIN,CHONGCHENG,CHONGCI,LOAD_MAX_ADD,LOAD_MIN_ADD,AREA,AREA_ADD) " 
	        					               + "VALUES ";
	        			System.out.println("insert info whose date>basedate");
	        			int indi_id_index = 0;
	        			while(rs_get_diag.next()){
	        				System.out.println("it is "+indi_id_index+"insert data");
	        				String value = " (";
	        				int rs_indi_overlap_id = rs_get_diag.getInt("INDICATOR_OVERLAP_ID");
	        				value += rs_indi_overlap_id + ",";
	        				String rs_userteam = userteam;
	        				value += "'"+rs_userteam + "',";
	        				String rs_unioncode = unioncode;
	        				value += "'"+rs_unioncode + "',";
	        				String rs_jh = well_name;
	        				value += "'"+rs_jh + "',";
	        				int rs_indi_id = indi_id_index++;
	        				value += rs_indi_id + ",";
	        				int rs_diag_id = rs_get_diag.getInt("DIAGRAM_ID");
	        				value += rs_diag_id + ",";
	        				String rs_collect_time = rs_get_diag.getString("P03");
	        				value += "'"+rs_collect_time + "',";
	        				double rs_load_max = rs_get_diag.getDouble("LOAD_MAX");
	        				value += rs_load_max + ",";
	        				double rs_load_min = rs_get_diag.getDouble("LOAD_MIN");
	        				value += rs_load_min + ",";
	        				double rs_elec_max = rs_get_diag.getDouble("ELECTRICITY_MAX");
	        				value += rs_elec_max + ",";
	        				double rs_elec_min = rs_get_diag.getDouble("ELECTRICITY_MIN");
	        				value += rs_elec_min + ",";
	        				double rs_chongcheng = rs_get_diag.getDouble("CHONGCHENG");
	        				value += rs_chongcheng + ",";
	        				double rs_chongci = rs_get_diag.getDouble("CHONGCI");
	        				value += rs_chongci + ",";
	        				double rs_load_max_add = rs_get_diag.getDouble("LOAD_MAX_ADD");
	        				value += rs_load_max_add + ",";
	        				double rs_load_min_add = rs_get_diag.getDouble("LOAD_MIN_ADD");
	        				value += rs_load_min_add + ",";
	        				double rs_area = rs_get_diag.getDouble("AREA");
	        				value += rs_area + ",";
	        				double rs_area_add = rs_get_diag.getDouble("AREA_ADD");
	        				value += rs_area_add + ")";
	        				/*System.out.println(value);*/
	        				stm.addBatch(sql_insert_diag+value);
	        			}
	        			stm.executeBatch();
	        			System.out.println("all finish");
	        		}
	        		
	        		
	        	}
	        	
	        }
	        
	    }catch(SQLException e){
			e.printStackTrace();
			flag = false;
		}finally{
			try{
				rs.close();
				rs_query.close();
				rs_sel_base.close();
				rs_get_diag.close();
				stm.close();
				conn.close();
			}catch(SQLException e){
				flag = false;
				System.out.println("there is a sql error!");
				e.printStackTrace();
			}
		}
	    /*System.out.println(flag);*/
	    System.out.println(flag);
	    return flag;
	}
	
	public boolean UpdateOperationDate(final String oper, final String json_str, final String userteam, final String unioncode){
		JSONArray jsonArray = JSONArray.fromObject(json_str);
		boolean isSuccess = false;
		String db_table_name = "";
		String db_action = "";
		String db_date = "";
		String db_action_name = "";
		if(oper.equals("xj")){
			db_table_name = "T_JH_QL";
			db_action_name = "QLMS";
			db_action = "进行油井结腊清洗";
			db_date = "QLRQ";
		}else{
			db_table_name = "T_JH_JL";
			db_action_name = "JLMS";
			db_date = "JLRQ";
			db_action = "结蜡事件";
		}
		
		Connection conn = DB_Connection.getConnection(this.db_driver, this.db_url, this.db_user, this.db_pwd);
		Statement stm= null;
	    try{
	    	stm = conn.createStatement();
			try{
				for(int index=0;index<jsonArray.size();index++){
					JSONObject jsonObj = jsonArray.getJSONObject(index);
					if(jsonObj.get("type").equals("new")){
						System.out.println("in new sql");
						String sql_insert_date = "INSERT INTO DB2INST1."+db_table_name+" "
			        			   + "(JH,USERTEAM,UNION_CODE,"+db_date+","+db_action_name+") " 
			                       + "VALUES ";
						try{
							JSONArray contentArray = (jsonObj.getJSONArray("content"));
														
							for(int i=0;i<contentArray.size();i++){
								stm.clearBatch();
								JSONObject contentObj = contentArray.getJSONObject(i);
								final String well_name = contentObj.getString("well_name");
								String base_value = " ('"+well_name+"','"+userteam+"','"+unioncode+"',";
								@SuppressWarnings("unchecked")
								List<String> date_list = (List<String>)contentObj.get("date");
								for(int j=0;j<date_list.size();j++){
									String value = base_value+"'"+date_list.get(j)+"','"+db_action+"')";
									System.out.println(sql_insert_date+value);
									stm.addBatch(sql_insert_date+value);
								}
								stm.executeBatch();
			        			System.out.println("insert finish");
			        			isSuccess = true;
							}
							
							
						}catch(JSONException e){
							isSuccess = false;
							e.printStackTrace();
						}
					}else if(jsonObj.get("type").equals("edit")){
						String sql_insert_date = "UPDATE DB2INST1."+db_table_name+" "
			        			   + "SET "+db_date+"= 'new_date' " 
			                       + "WHERE JH='well_name' and "+db_date+"='origin_date' and USERTEAM='"+userteam+"' and UNION_CODE='"+unioncode+"'";
					
						try{
							JSONArray contentArray = (jsonObj.getJSONArray("content"));
														
							for(int i=0;i<contentArray.size();i++){
								stm.clearBatch();
								JSONObject contentObj = contentArray.getJSONObject(i);
								final String well_name = contentObj.getString("well_name");
								@SuppressWarnings("unchecked")
								List<String> origin_date_list = (List<String>)contentObj.get("origin_date");
								
								String sql_insert_date_item = sql_insert_date.replace("well_name", well_name);
								
								@SuppressWarnings("unchecked")
								List<String> date_list = (List<String>)contentObj.get("date");
								for(int j=0;j<date_list.size();j++){
									String temp = sql_insert_date_item.replace("new_date", date_list.get(j));
									temp = temp.replace("origin_date", origin_date_list.get(j));
									System.out.println(temp);
									stm.addBatch(temp);
								}
								stm.executeBatch();
			        			System.out.println("update finish");
			        			isSuccess = true;
							}
						}catch(JSONException e){
							isSuccess = false;
							e.printStackTrace();
						}
					}else if(jsonObj.get("type").equals("del")){
						String sql_del_date = "DELETE FROM DB2INST1."+db_table_name+" "
			        			            + "WHERE JH='well_name' and "+db_date+"='origin_date' and USERTEAM='"+userteam+"' and UNION_CODE='"+unioncode+"'";
						try{
							JSONArray contentArray = (jsonObj.getJSONArray("content"));
														
							for(int i=0;i<contentArray.size();i++){
								stm.clearBatch();
								JSONObject contentObj = contentArray.getJSONObject(i);
								final String well_name = contentObj.getString("well_name");
									
								String sql_del_date_item = sql_del_date.replace("well_name", well_name);
								
								@SuppressWarnings("unchecked")
								List<String> origin_date_list = (List<String>)contentObj.get("origin_date");
								for(int j=0;j<origin_date_list.size();j++){
									System.out.println("origin_date:"+origin_date_list.get(j));
									String temp = sql_del_date_item.replace("origin_date", origin_date_list.get(j));
									System.out.println(temp);
									stm.addBatch(temp);
								}
								stm.executeBatch();
			        			System.out.println("del finish");
			        			isSuccess = true;
							}
						}catch(JSONException e){
							isSuccess = false;
							e.printStackTrace();
						}
					}
				}
			}catch(JSONException e){
				isSuccess = false;
				e.printStackTrace();
			}
	    }catch(SQLException e){
	    	isSuccess = false;
	    	e.printStackTrace();
	    }finally{
	    	try{
				stm.close();
				conn.close();
			}catch(SQLException e){
				isSuccess = false;
				System.out.println("there is a sql error!");
				e.printStackTrace();
			}
	    }	
		return isSuccess;
	}
	
	public String getCognosReportID(final String userteam, final String report_id_uri){
		String report_id = "";		
		Properties prop = new Properties();
		if(report_id_uri != null){
			try{
				File db_conn = new File(report_id_uri);
				System.out.println(report_id_uri);
				FileInputStream in = new FileInputStream(db_conn);
				try{
					prop.load(in);
					report_id = prop.getProperty(userteam);
					System.out.println(">>>>id"+report_id);
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}
		}
		
		return report_id;
	}
	
	public JSONObject GetWellName(final String userteam){
		JSONObject jsonObj = new JSONObject();
		
		Connection conn = DB_Connection.getConnection(this.db_driver, this.db_url, this.db_user, this.db_pwd);
		Statement stm= null;
	    ResultSet rs =null;
	    String key = "first_well_name";
		String first_well_name = "";
	    
	    try{
	    	String sql = "select distinct JH from DB2INST1.T_INDI_ADD_MAIN where USERTEAM='"+userteam+"' order by 1";    	
	    	stm = conn.createStatement();
	        rs = stm.executeQuery(sql);
	        
	        if(rs.next()){
	        	try{
	        		first_well_name = rs.getString("JH");
	        		jsonObj.put(key, first_well_name);
	        	}catch(JSONException e){
	        		e.printStackTrace();
	        	}
	        }
	    }catch(SQLException e){
	    	e.printStackTrace();
	    }finally{
	    	try{
				stm.close();
				conn.close();
			}catch(SQLException e){
				System.out.println("there is a sql error!");
				e.printStackTrace();
			}
	    }	
		return jsonObj;
	}
}
