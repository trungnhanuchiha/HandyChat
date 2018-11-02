package application;

public class Message {
	public String from;
	public String to;
	public String timestamp;
	public String data;
	public int ToFrom;
	public boolean isSave;
	
	public Message(String from, String to, String timestamp, String data,int ToFrom){
		this.from = from;
		this.timestamp = timestamp;
		this.data = data;
		this.ToFrom = ToFrom;
		this.to = to;
	}
	
	public Message(String from, String to, String timestamp, String data,int ToFrom, boolean isSave){
		this.from = from;
		this.timestamp = timestamp;
		this.data = data;
		this.ToFrom = ToFrom;
		this.to = to;
		this.isSave = isSave;
	}
}

