package db_conn_properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

public class DB_Conn_Properties {
	private String db_driver = "";
	private String db_url = "";
	private String db_user = "";
	private String db_pwd = "";
	
	public DB_Conn_Properties(final String db_properties_location){		
		Properties prop = new Properties();
		if(db_properties_location != null){
			try{
				File db_conn = new File(db_properties_location);
				FileInputStream in = new FileInputStream(db_conn);
				try{
					prop.load(in);
					this.setDb_driver(prop.getProperty("DBDriver"));
					this.setDb_url(prop.getProperty("DBUrl"));
					this.setDb_user(prop.getProperty("DBUsername"));
					this.setDb_pwd(prop.getProperty("DBPassword"));
					
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}
		}
	}

	public String getDb_driver() {
		return db_driver;
	}

	private void setDb_driver(String db_driver) {
		this.db_driver = db_driver;
	}

	public String getDb_url() {
		return db_url;
	}

	private void setDb_url(String db_url) {
		this.db_url = db_url;
	}

	public String getDb_user() {
		return db_user;
	}

	private void setDb_user(String db_user) {
		this.db_user = db_user;
	}

	public String getDb_pwd() {
		return db_pwd;
	}

	private void setDb_pwd(String db_pwd) {
		this.db_pwd = db_pwd;
	}
}
