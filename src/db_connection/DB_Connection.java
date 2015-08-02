package db_connection;

import java.sql.*;

public class DB_Connection {
	public static Connection getConnection(final String db_driver, final String db_url,final String db_user, final String db_pwd) {
		// TODO Auto-generated constructor stub
		Connection conn = null;
		try{  
			Class.forName(db_driver); 
			try{  
	            conn = DriverManager.getConnection(db_url,db_user,db_pwd);  
			} catch (SQLException e){
				System.out.println("Fail to connect to the databse!"); 
				e.printStackTrace();
			}    
		} catch (ClassNotFoundException e){  
            e.printStackTrace();
		}  
		return conn;
	}
	public void closeConnection(Connection conn){
		try{
			if( conn != null){
				conn.close();
				conn=null;
			}
		} catch (SQLException e){  
            e.printStackTrace();
            System.out.println("Error in close database："+e.getMessage());
		}  
	}
}
