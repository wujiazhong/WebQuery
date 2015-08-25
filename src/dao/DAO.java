package dao;

import java.sql.*;

import db_connection.DB_Connection;
import t_user.T_User;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import java.util.regex.*;
import java.util.ArrayList;

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
	    ResultSet rs_union_code = null;
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
	        	
	        	sql ="select distinct UNION_CODE from T_PREDICT where USERTEAM='"+user.getUserTeam()+"'";
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
	
	public JSONArray getDiagID(final String unioncode, final String fix_date, final String jh){
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		final String date = "date";
		final String time = "time";
		final String diag_id = "diag_id";
		
		Connection conn = DB_Connection.getConnection(this.db_driver, this.db_url, this.db_user, this.db_pwd);
		Statement stm= null;
	    ResultSet rs =null;
	    
	    try{
			String sql ="select distinct DIAGRAM_ID, P03 "
					  + "from T_DBAT2070 "
					  + "where P01 = '"+unioncode+"' and P02='"+jh+"' and P03>'"+fix_date+"' order by P03";
			stm = conn.createStatement();
	        rs = stm.executeQuery(sql);
	        
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
	    
	    try{
	    	String sql_first_base = "select INDICATOR_BASE_ID from DB2INST1.T_USERTEAM_WELL T0 "
					  			  + "where (JH='"+well_name+"' and UNION_CODE='"+unioncode+"')";
			
			String sql_init_base = "update DB2INST1.T_USERTEAM_WELL set INDICATOR_BASE_ID='"+diag_id+"' "
								 + "where (JH='"+well_name+"' and UNION_CODE='"+unioncode+"')";
			
			String sql_insert_base_diag_id = "insert DB2INST1.T_INDI_ADD_MAIN (JH,USERTEAM,UNION_CODE,INDICATOR_BASE_DT) "
					                       + "values ('"+well_name+"','"+userteam+"','"+unioncode+"', TO_DATE('"+date_time+"', 'YYYY-MM-DD HH24:MI:SS'))";
			
			String sql_update_base_diag_id = "update DB2INST1.T_INDI_ADD_MAIN set INDICATOR_BASE_DT=TO_DATE('"+date_time+"', 'YYYY-MM-DD HH24:MI:SS') "
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
			    				            + "WHERE (((T1.P01 = '"+unioncode+"') AND (T1.P02 = '"+userteam+"')) "
			    				            + "AND ((JULIAN_DAY(T1.P03) - JULIAN_DAY(TO_DATE('"+date_time+"', 'YYYY-MM-DD HH24:MI:SS'))) >= 0)) " 
			    					        + "AND (T0.P01 = T1.P01) AND (T0.P02 = T1.P02) AND (T0.P03 = T1.P03) ORDER BY 11 ASC";
	        			System.out.println("diag:"+sql_get_diag);
	        			rs_get_diag = stm.executeQuery(sql_get_diag);
	        			System.out.println("get update info whose date>basedate");
	        			//delete old records of diagram in T_INDI_ADD_DIAGRAM
	        			if(!isFirstTimeSetBaseDiag){
		        			String sql_del_old_records = "delete from DB2INST1.T_INDI_ADD_DIAGRAM "
		        					                   + "where (JH='"+well_name+"' and UNION_CODE='"+unioncode+"' and INDICATOR_OVERLAP_ID="+indi_id+")";
		        			System.out.println("del:"+sql_del_old_records);
		        			stm.executeUpdate(sql_del_old_records);
		        			System.out.println("del old records");
	        			}
	        			
	        			String sql_insert_diag = "INSERT INTO DB2INST1.'T_INDI_ADD_DIAGRAM' "
	        					               + "('INDICATOR_OVERLAP_ID',USERTEAM,'UNION_CODE',JH,'INDICATOR_ID','DIAGRAM_ID','COLLECT_DATETIME','LOAD_MAX','LOAD_MIN','ELECTRICITY_MAX','ELECTRICITY_MIN',CHONGCHENG,CHONGCI,'LOAD_MAX_ADD','LOAD_MIN_ADD',AREA,'AREA_ADD') " 
	        					               + "VALUES ";
	        			System.out.println("insert info whose date>basedate");
	        			while(rs_get_diag.next()){
	        				String value = " (";
	        				int rs_indi_overlap_id = rs_get_diag.getInt("INDICATOR_OVERLAP_ID");
	        				value += rs_indi_overlap_id + ",";
	        				String rs_userteam = rs_get_diag.getString("USERTEAM");
	        				value += rs_userteam + ",";
	        				String rs_unioncode = rs_get_diag.getString("UNION_CODE");
	        				value += rs_unioncode + ",";
	        				String rs_jh = rs_get_diag.getString("JH");
	        				value += rs_jh + ",";
	        				int rs_indi_id = rs_get_diag.getInt("INDICATOR_ID");
	        				value += rs_indi_id + ",";
	        				int rs_diag_id = rs_get_diag.getInt("DIAGRAM_ID");
	        				value += rs_diag_id + ",";
	        				String rs_collect_time = rs_get_diag.getString("COLLECT_DATETIME");
	        				value += rs_collect_time + ",";
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
				System.out.println("there is a sql error!");
				e.printStackTrace();
			}
		}
	    return flag;
	}
	
/*	public JSONArray getIndicatorID(final String userteam){
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		String well_name = "well_name";
		String base_date = "base_date";
		String fix_date = "fix_date";
		
		Connection conn = DB_Connection.getConnection(this.db_driver, this.db_url, this.db_user, this.db_pwd);
		Statement stm= null;
	    ResultSet rs =null;
		
	    try{
			String sql ="select distinct JH, CSMC, SGNR, WGRQ "
					  + "from T_DDCC03 "
					  + "where INDICATOR_OVERLAP_ID in (select INDICATOR_OVERLAP_ID from T_INDI_ADD_MAIN where USERTEAM = '"+userteam+"')";
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
	}*/
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
