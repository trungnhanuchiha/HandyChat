package application;

import java.awt.JobAttributes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;




public class ResponseHandler {
	
	String DataPassPath = "./DataPast/";
	String DataPassTest = "./DataPastTest/";
	String Conversationfile = NowUser+"_dual.json";
	String GroupChatfile = NowUser+"_group.json";
	
	String NameFileDataPath = "MessagePassMain.json";
	String UTC_Format = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'";
	public static String NowUser;
	public static int Num_Message_Now_Conversation;
	public static int Num_Got_Message = 0;
	public List<NewMessageNotification> result_new_nofication = new ArrayList<NewMessageNotification>();
	RequestHandler reqhan = new RequestHandler();
	
	public ResponseHandler() {
		
	}
	
	private String get_Conversationfile() {
		Conversationfile = NowUser+"_dual.json";
		return Conversationfile;
	}
	
	// port 8000
	public String Res_Send_Message(String res) {
		String res_string = "";
		JSONObject jo = null ;
		try {
			jo = (JSONObject) new JSONParser().parse(res);
			String result = (String) jo.get("type");
			if(result.equals(Constant.res_success)) {
				res_string = Constant.res_success;
			}
		} catch (ParseException  e) {
			e.printStackTrace();
		}
		return res_string;
	}
	public Message Res_New_Message(String res) {
		//System.out.println("Test 1");
		String res_from = "";
		String res_content = "";
		String res_timestamp = "";
		JSONObject jo = null ;
		try {
			jo = (JSONObject) new JSONParser().parse(res);

			String result = (String) jo.get("type");
			if(result.equals(Constant.res_message_notification)) {
				res_from = (String) jo.get("from") ; 
				res_content = ((String) jo.get("content")).split(":")[1] ;
				res_timestamp = (String) jo.get("timestamp");
			}
		} catch (ParseException  e) {
			e.printStackTrace();
		}
		return new Message(res_from,"",res_timestamp,res_content,1);
	}
	public ErrorRespond Res_Error(String res) {
		//System.out.println("Test 2");
		String res_type = "";
		String res_error_code = "";
		String res_content = "";
		JSONObject jo = null ;
		try {
			jo = (JSONObject) new JSONParser().parse(res);
			String result = (String) jo.get("type");
			if(result.equals(Constant.res_error)) {
				res_type = (String) jo.get("type") ; 
				res_error_code = (String) jo.get("errorcode") ;
				res_content = (String) jo.get("content");
			}
		} catch (ParseException  e) {
			e.printStackTrace();
		}
		return new ErrorRespond(res_type,res_error_code,res_content);
	}
	public ResponsePresence Res_Presence(String res) {
		String res_type = "";
		List<AccountInformation> res_list_online = null;
		List<AccountInformation> res_list_offline = null;
		JSONObject jo = null ;
		try {
			jo = (JSONObject) new JSONParser().parse(res);
			String result = (String) jo.get("type");
			if(result.equals(Constant.res_presence)) {
				res_type = (String) jo.get("type") ; 
				jo = (JSONObject) jo.get("content");
				res_list_online = create_list_account_from_array(((String) jo.get("online")).split(";")) ;
				res_list_offline = create_list_account_from_array(((String) jo.get("offline")).split(";"));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(jo != null) {
			
			
		}
		return new ResponsePresence(res_type, res_list_online, res_list_offline);
	}
	
	public String[] Extract_display_name(List<AccountInformation> list) {
		String[] res = new String[list.size()];
		for(int i = 0;i<list.size();++i) {
			res[i] = list.get(i).display_name;
		}
		return res;
	}
	
	private List<AccountInformation> create_list_account_from_array(String[] arr) {
		List<AccountInformation> list = new  ArrayList<AccountInformation>();
		for(int i = 0;i<arr.length;++i) {
			list.add(new AccountInformation(arr[i],arr[i]+" dips",arr[i]));
		}
		return list;
	}
	
	public List<NewMessageNotification> Res_New_Message_Notification(String res){
		//System.out.println("Test 3");
		if(!result_new_nofication.isEmpty()) result_new_nofication.clear();
		List<String> temp = new ArrayList<String>();
		JSONObject jo = null ;
		try {
			jo = (JSONObject) new JSONParser().parse(res);
			result_new_nofication.add(new NewMessageNotification("",0));
			String type = (String) jo.get("type");
			if(type.equals(Constant.res_new_message_notification)) {
				result_new_nofication.set(0, new NewMessageNotification(" ",0));
				JSONArray jo1 = (JSONArray) jo.get("content");
				for(int i = 0 ;i<jo1.size();++i) {
					JSONObject jo2 = (JSONObject) jo1.get(i);
					JSONObject jo3 = (JSONObject) jo2.get("sender");
					String from = (String) jo3.get("account");
					reqhan.create_new_conversation(false,UiController.get_list_User(from));
					temp.add(from);
					Message message = new Message(from,"",(String) jo2.get("timestamp"),(String) jo2.get("content"),1);
					Save_Message_New(NowUser, message);
				} 
				String[] arr_string = new String[temp.size()];
				temp.toArray(arr_string);
				Set<String> set_temp = new TreeSet<String>(temp);
				
				set_temp.forEach(action -> {
					NewMessageNotification new_message = new NewMessageNotification(action,count(arr_string,action));
					result_new_nofication.add(new_message);
				});
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result_new_nofication;
	}
	private int count(String[] list, String k) {
		int result  = 0;
		for(int i = 0;i<list.length;++i) {
			if(list[i].equals(k))
			result++;
		}
		return result;
	}
	public String convert_UTC_to_Local(String utctime) {
		DateFormat df1 = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
		DateFormat df2 = new SimpleDateFormat(UTC_Format);
		TimeZone now = Calendar.getInstance().getTimeZone();
		df1.setTimeZone(TimeZone.getTimeZone(now.getID()));
		df2.setTimeZone(TimeZone.getTimeZone("UTC"));
		String result = "";
		try {
			result = df1.format(df2.parse(utctime));
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
	public String convert_Local_to_UTC(String localtime) {
		DateFormat df1 = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
		DateFormat df2 = new SimpleDateFormat(UTC_Format);
		TimeZone now = Calendar.getInstance().getTimeZone();
		df1.setTimeZone(TimeZone.getTimeZone(now.getID()));
		df2.setTimeZone(TimeZone.getTimeZone("UTC"));
		String result = "";
		try {
			result = df2.format(df1.parse(localtime));
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean check_UTC_Format_Time(String time) {
		DateFormat df2 = new SimpleDateFormat(UTC_Format);
		df2.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date result;
		try {
			result = df2.parse(time);
			return true;
		} catch (java.text.ParseException e) {
			return false;
		}
	}
	
	public static boolean check_JSON_Array_Equal(JSONArray arr1, JSONArray arr2) {
		for(int i = 0;i<arr1.size();++i) {
			int j = 0 ;
			while(j<arr2.size()) {
				if(arr1.get(i).equals(arr2.get(j))) {
						break;
				}
				j++;

			}
			if(j==arr2.size()) return false;
		}
		return true;
	}
	public int ChecktoFrom(String NowUser, String toUser) {
		if(NowUser.equals(toUser)) return 0;
		return 1;
	}
	
	
	public List<Message> Load_Message_New(String NowUser,String toUser) {
		List<Message> result = new ArrayList<Message>();
		JSONArray arr_user = new JSONArray();
		arr_user.add(NowUser);
		arr_user.add(toUser);
		try {
			JSONObject jo = (JSONObject) new JSONParser().parse(new FileReader(DataPassTest+get_Conversationfile()));
			JSONArray jo_arr = (JSONArray) jo.get("conversation");
			
			for(int i = 0;i<jo_arr.size();++i) {
				JSONObject temp = (JSONObject) jo_arr.get(i);
				if(check_JSON_Array_Equal((JSONArray) temp.get("participants"),arr_user)){
					JSONArray temp1 = (JSONArray) temp.get("messages");
					if(temp1.size()>=reqhan.amount_messages_torender + Num_Got_Message) {
						for(int j = temp1.size() - reqhan.amount_messages_torender - Num_Got_Message;j<temp1.size()-Num_Got_Message;++j) {
							JSONObject temp2 = (JSONObject) temp1.get(j);
							Message message = new Message((String)temp2.get("from"),"",convert_UTC_to_Local((String) temp2.get("timestamp")),(String) temp2.get("content"),ChecktoFrom(NowUser,(String)temp2.get("from")));
							result.add(message);
							System.out.println(j);
						}
						Num_Got_Message += reqhan.amount_messages_torender;
					}
					else {
						for(int j = 0;j<temp1.size();++j) {
							JSONObject temp2 = (JSONObject) temp1.get(j);
							Message message = new Message((String)temp2.get("from"),"",convert_UTC_to_Local((String) temp2.get("timestamp")),(String) temp2.get("content"),ChecktoFrom(NowUser,(String)temp2.get("from")));
							result.add(message);
						}
						Num_Got_Message += temp1.size();
					}
					
					break;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		Collections.reverse(result);
		return result;	
	}
	public void Save_Message_New(String nowUSer, Message message) {
		JSONObject jo;
		JSONArray arr_user = new JSONArray();
		arr_user.add(nowUSer);
		if(message.to.equals("")) arr_user.add(message.from);
		else arr_user.add(message.to);
		
		System.out.println(arr_user.toString());
		try {
			jo = (JSONObject) new JSONParser().parse(new FileReader(DataPassTest+get_Conversationfile()));
			System.out.println("ahihi");
			JSONArray jo_arr = (JSONArray) jo.get("conversation");
			for(int i = 0;i<jo_arr.size();++i) {
				JSONObject temp = (JSONObject) jo_arr.get(i);
				int stt = Math.toIntExact((Long) temp.get("num_message"));
				if(check_JSON_Array_Equal((JSONArray) temp.get("participants"),arr_user)){
					System.out.println("ahihi1");
					JSONArray temp1 = (JSONArray) temp.get("messages");
					JSONObject jo_new_mess = new JSONObject();
					jo_new_mess.put("order",stt);
					jo_new_mess.put("from",message.from);
					jo_new_mess.put("content",message.data);
					jo_new_mess.put("timestamp",message.timestamp);
					temp1.add(jo_new_mess);
					temp.put("num_message", stt+1);
					break;
				}
			}
			PrintWriter pw = new PrintWriter(DataPassTest+get_Conversationfile()); 
	        pw.write(jo.toJSONString()); 
	        pw.flush(); 
	        pw.close(); 			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	public int Res_count_of_conversation(String res, String toUser) {
		int result = -1;
		JSONObject jo;
		try {
			jo = (JSONObject) new JSONParser().parse(res);
			String type = (String) jo.get("type");
			if(type.equals(Constant.res_get_message_count)) {
				result = Math.toIntExact((long) jo.get("content"));
				
				
				JSONArray arr_user = new JSONArray();
				arr_user.add(NowUser);
				arr_user.add(toUser);
				
				jo = (JSONObject) new JSONParser().parse(new FileReader(DataPassTest+get_Conversationfile()));
				JSONArray jo_arr = (JSONArray) jo.get("conversation");
				for(int i = 0;i<jo_arr.size();++i) {
					JSONObject temp = (JSONObject) jo_arr.get(i);
					if(check_JSON_Array_Equal((JSONArray) temp.get("participants"),arr_user)){
						temp.put("num_message", result);
						break;
					}
				}
				
				
				PrintWriter pw = new PrintWriter(DataPassTest+get_Conversationfile()); 
		        pw.write(jo.toJSONString()); 
		        pw.flush(); 
		        pw.close(); 			
			}
			
			
			
			
		} catch (ParseException e) {
			System.out.println("Parser JSON exception !");
		} catch (Exception e1) {
			System.out.println("Not Response Count Messages !");
		}
		System.out.println("Count: "+result);
		return result;
	}
	public int Res_get_messages(String res, String toUser) {
		int result = -1;
		JSONObject jo ;
		JSONObject jo_old ;
		JSONArray paticipants_array =  new JSONArray();
		paticipants_array.add(NowUser);
		paticipants_array.add(toUser);
		
		try {
			jo = (JSONObject) new JSONParser().parse(res);
			String type = (String) jo.get("type");
			if(type.equals(Constant.res_get_messages)) {
				int num_now_message = Count_Message_Now_In_Conversation(toUser);
				jo_old = (JSONObject) new JSONParser().parse(new FileReader(DataPassTest+get_Conversationfile()));
				JSONArray jo_arr_old = (JSONArray) jo_old.get("conversation");
				for(int i = 0;i<jo_arr_old.size();++i) {
					JSONObject temp = (JSONObject) jo_arr_old.get(i);

					
					if(check_JSON_Array_Equal((JSONArray) temp.get("participants"),paticipants_array)){
						JSONArray message_array = (JSONArray) temp.get("messages");
						JSONArray message_get = new JSONArray();

						JSONArray jo_arr = (JSONArray) jo.get("content");
						for(int j = 0;j<jo_arr.size();++j) {
							JSONObject jo_new =  new JSONObject();
							JSONObject jo_message_get = (JSONObject) jo_arr.get(j);
							JSONObject senderObj = (JSONObject) jo_message_get.get("sender");
							
							
							jo_new.put("from", (String) senderObj.get("account"));
							jo_new.put("content", (String) jo_message_get.get("content"));
							jo_new.put("timestamp", (String) jo_message_get.get("timestamp"));
							jo_new.put("order",Num_Message_Now_Conversation-reqhan.amount_messages-num_now_message+j);
							message_get.add(jo_new);
						}
						message_get.addAll(message_array);
						temp.put("messages", message_get);
						break;
					}
				}
				PrintWriter pw = new PrintWriter(DataPassTest+get_Conversationfile()); 
		        pw.write(jo_old.toJSONString()); 
		        pw.flush(); 
		        pw.close(); 	
		        result = 1;
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return result;
		
	}
	public int Count_Message_Now_In_Conversation(String toUSer) {
		int result = 0;
		JSONObject jo;
		JSONArray arr_user = new JSONArray();
		arr_user.add(NowUser);
		arr_user.add(toUSer);
		
		System.out.println(arr_user.toString());
		try {
			jo = (JSONObject) new JSONParser().parse(new FileReader(DataPassTest+get_Conversationfile()));
			JSONArray jo_arr = (JSONArray) jo.get("conversation");
			for(int i = 0;i<jo_arr.size();++i) {
				JSONObject temp = (JSONObject) jo_arr.get(i);
				int stt = Math.toIntExact((Long) temp.get("num_message"));
				if(check_JSON_Array_Equal((JSONArray) temp.get("participants"),arr_user)){
					JSONArray messages_arr = (JSONArray) temp.get("messages");
					result = messages_arr.size();
					break;
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println("Now Count: "+result);
		return result;
	}
}
