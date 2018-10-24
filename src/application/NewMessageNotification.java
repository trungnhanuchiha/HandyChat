package application;

public class NewMessageNotification {
	public  String account;
	public int num_message;
	public NewMessageNotification(String account, int num_message) {
		this.account = account;
		this.num_message = num_message;
	}
	public String toString() {
		return this.account+": "+this.num_message;
	}
}


