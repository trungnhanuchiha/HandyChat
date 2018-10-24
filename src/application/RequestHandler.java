package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RequestHandler {

	String UTC_Format = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'";
	String DataPassTest = "./DataPastTest/";
	public static String NowUser;
	String Conversationfile = NowUser+"_dual.json";
	String GroupChatfile = NowUser+"_group.json";
	
	public int amount_messages = 15;
	public int amount_messages_torender = 5;
	
	private String get_Conversationfile() {
		Conversationfile = NowUser+"_dual.json";
		return Conversationfile;
	}
	
	
	// port 8000
	public JSONObject Req_Send_Message(String to, String content) {
		JSONObject jo = new JSONObject();
			jo.put("type", Constant.req_send_message_type);
			jo.put("to", to);
			TimeZone tz = TimeZone.getTimeZone("UTC");
			DateFormat df = new SimpleDateFormat(UTC_Format);
			df.setTimeZone(tz);
			String nowAsISO = df.format(new Date());
			jo.put("timestamp",nowAsISO);
			jo.put("content", content);

		return jo;
	}
	public JSONObject Req_Get_Messages_Count(String toUser) {
		JSONObject jo = new JSONObject();
			jo.put("type",Constant.req_get_messages_count);
			jo.put("account", toUser);
			return  jo;
		
	}
	
	
	// port 8001
	public JSONObject Req_Inquire_Presence(String to) {
		JSONObject jo = new JSONObject();
		jo.put("type", Constant.req_inquire_presence);
		jo.put("to", to);
		return jo;
	}
	
	// port 8002
	public JSONObject Req_Notification_Online(String account) {
		JSONObject jo  = new JSONObject();
			jo.put("type", Constant.req_online_notification_type);
			jo.put("account", account);
		return jo;
	}
	
	// login
	public void create_db_for_new_User() {
		File Conversation = new File(DataPassTest+get_Conversationfile());
		if(!Conversation.exists()) {
			JSONObject jo = new JSONObject();
			jo.put("conversation", new JSONArray());
			PrintWriter pw;
			try {
				pw = new PrintWriter(DataPassTest+Conversationfile);
		        pw.write(jo.toJSONString()); 
		        pw.flush(); 
		        pw.close(); 
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} 
		}
	}
	
	public void create_new_conversation(boolean isGroup, List<String> Users) {
		JSONObject jo;
		JSONArray arr_user = new JSONArray();
		arr_user.add(NowUser);
		try {
			jo = (JSONObject) new JSONParser().parse(new FileReader(DataPassTest+get_Conversationfile()));
			JSONArray jo_arr = (JSONArray) jo.get("conversation");
			if(!isGroup) {
				arr_user.add(Users.get(0));
				int i;
				for(i = 0;i<jo_arr.size();++i) {
					JSONObject temp = (JSONObject) jo_arr.get(i);
					if(ResponseHandler.check_JSON_Array_Equal((JSONArray) temp.get("participants"),arr_user)){
						return ;
					}
				}
			}
			JSONObject jo_new =  new JSONObject();
			jo_new.put("isGroup", String.valueOf(isGroup));
			JSONArray arr_paticipants = new JSONArray();
			arr_paticipants.add(NowUser);
			Users.forEach(action -> {
				arr_paticipants.add(action);
			});
			jo_new.put("participants", arr_paticipants);
			jo_new.put("num_message", 0);
			jo_new.put("messages", new JSONArray());
			jo_arr.add(jo_new);
			PrintWriter pw = new PrintWriter(DataPassTest+Conversationfile); 
	        pw.write(jo.toJSONString()); 
	        pw.flush(); 
	        pw.close();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	public JSONObject Req_Get_Messages(String account, int num_messages, int num_now_message) {
		JSONObject jo = new JSONObject();
		int message_index_to_get = num_messages -  num_now_message;
		jo.put("type",Constant.req_get_messages);
		jo.put("account", account);
		jo.put("content", String.valueOf(message_index_to_get-amount_messages)+":"+ String.valueOf(message_index_to_get-1));
		return jo;
	}
	public JSONObject Req_create_group_Chat(List<String> list_account, String uuid) {
		JSONObject jo = new JSONObject();
		String term = list_account.get(0);
		for(int i  = 1;i<list_account.size();++i) term += "|" + list_account.get(i);
		jo.put("type",Constant.req_create_group);
		jo.put("content",uuid+";"+term);
		return jo;
		
	}
	
	
}
