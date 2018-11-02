package application;

public class Call {
	public String from;
	public String to;
	public String timestamp;
	public int ToFrom;
	
	public Call(String from, String to, String timestamp, int ToFrom){
		this.from = from;
		this.timestamp = timestamp;
		this.ToFrom = ToFrom;
		this.to = to;
	}
}
