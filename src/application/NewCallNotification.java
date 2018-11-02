package application;

public class NewCallNotification {
	public  String account;
	public NewCallNotification(String account) {
		this.account = account;
	}
	public String toString() {
		return this.account+" is calling";
	}
}
