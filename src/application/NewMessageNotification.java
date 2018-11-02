package application;

public class NewMessageNotification {
	public  String account;
	public int num_message;
	public boolean isGroup;
	public NewMessageNotification(String account, int num_message, boolean isGroup) {
		this.account = account;
		this.num_message = num_message;
		this.isGroup = isGroup;
	}
	
	public NewMessageNotification(String account, int num_message) {
		this.account = account;
		this.num_message = num_message;
	}
	
	public String toString() {
		return this.account+": "+this.num_message;
	}
	
}


