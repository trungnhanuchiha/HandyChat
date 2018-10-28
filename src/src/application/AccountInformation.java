package application;

public class AccountInformation {
	public String account_name;
	public String display_name;
	public String full_name;
	public String online_status;
	AccountInformation(String account_name,String display_name,String full_name){
		this.account_name = account_name;
		this.display_name = display_name;
		this.full_name = full_name;
	}
	AccountInformation(String account_name,String display_name,String full_name, String online_status){
		this.account_name = account_name;
		this.display_name = display_name;
		this.full_name = full_name;
		this.online_status = online_status;
	}
}
