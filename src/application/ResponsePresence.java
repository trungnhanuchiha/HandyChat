package application;

import java.util.List;

public class ResponsePresence {
	public String type;
	public List<AccountInformation> list_online;
	public List<AccountInformation> list_offline;
	public ResponsePresence(String type, List<AccountInformation> list_online, List<AccountInformation> list_offline) {
		this.type = type;
		this.list_online = list_online;
		this.list_offline = list_offline;
	}
}
