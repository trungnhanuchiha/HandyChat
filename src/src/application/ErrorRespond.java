package application;

public class ErrorRespond {
	public String type;
	public String error_code;
	public String content;
	public ErrorRespond(String type, String error_code, String content) {
		this.type = type;
		this.error_code = error_code;
		this.content = content;
	}
}
