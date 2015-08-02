package t_user;

public class T_User {
	private String username;
	private String password;
	private String userteam;
	private String usertype;
	private String name;
	private String gender;
	
	public T_User(final String user){		
		setUserName(username);
	}
	
	public String getUserName(){
		return this.username;
	}
	public void setUserName(final String username){
		this.username = username;
	}
	
	public String getPassword(){
		return this.password;
	}
	public void setPassword(final String password){
		this.password = password;
	}
		
	public String getUserTeam(){
		return this.userteam;
	}
	public void setUserTeam(final String userteam){
		this.userteam = userteam;
	}
	
	public String getUserType(){
		return this.usertype;
	}
	public void setUserType(final String usertype){
		this.usertype = usertype;
	}
	
	public String getName(){
		return this.name;
	}
	public void setName(final String name){
		this.name = name;
	}
	
	public String getGender(){
		return this.gender;
	}
	public void setGender(final String gender){
		this.gender = gender;
	}
}
