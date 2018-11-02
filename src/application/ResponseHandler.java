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
import java.util.Arrays;
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.naming.event.ObjectChangeListener;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javafx.scene.paint.Color;




public class ResponseHandler {
	
	String DataPassPath = "./DataPast/";
	String DataPassTest = "./DataPastTest/";
	String Conversationfile = NowUser+"_dual.json";
	String GroupChatfile = NowUser+"_group.json";
	
	String NameFileDataPath = "MessagePassMain.json";
	String UTC_Format = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'";
	public static String NowUser;
	public static String NowUserName;
	public static int Num_Message_Now_Conversation;
	public static int Num_Got_Message = 0;
	public static int Num_Got_Message_Group = 0;
	
	public static List<Message> list_dual = new ArrayList<Message>();
	public static List<Message> list_group = new ArrayList<Message>();
	
	public static List<String> GroupNameNew = new ArrayList<String>();
	public List<ColoredText> list_cache_list_Group = new ArrayList<ColoredText>();
	
	public List<NewMessageNotification> result_new_nofication = new ArrayList<NewMessageNotification>();
	RequestHandler reqhan = new RequestHandler();
	
	public ResponseHandler() {
		
	}
	
	private String get_Conversationfile() {
		Conversationfile = NowUser+"_dual.json";
		return Conversationfile;
	}
	
	private String get_GroupFile() {
		GroupChatfile = NowUser+"_group.json";
		return GroupChatfile;
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
	public Message Res_New_Message(JSONObject jo) {
		//System.out.println("Test 1");
		String res_from = "";
		String res_content = "";
		String res_timestamp = "";
		res_from = (String) jo.get("from") ; 
		res_content = ((String) jo.get("content")).split(":")[1] ;
		res_timestamp = (String) jo.get("timestamp");
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
	public ResponsePresence Res_Presence(JSONObject jo) {
		String res_type = "";
		List<AccountInformation> res_list_online = new ArrayList<AccountInformation>();
		List<AccountInformation> res_list_offline = new ArrayList<AccountInformation>();
		jo = (JSONObject) jo.get("content");
		
		JSONArray jo_online = (JSONArray) jo.get("online");
		JSONArray jo_offline = (JSONArray) jo.get("offline");
		for(int i = 0;i<jo_online.size();++i) {
			JSONObject term = (JSONObject) jo_online.get(i);
			if(!((String) term.get("account")).equals(NowUser)) {
				AccountInformation account = new AccountInformation((String) term.get("account"), (String) term.get("display_name"),"","Online");
				res_list_online.add(account);
			}
			else {
				ResponseHandler.NowUserName = (String) term.get("display_name");
			}
		}
			
		for(int i = 0;i<jo_offline.size();++i) {
			JSONObject term = (JSONObject) jo_offline.get(i);
			String last_online="";
			AccountInformation account;
			
			try {
				last_online = (String) term.get("last_online");
				account = new AccountInformation((String) term.get("account"), (String) term.get("display_name"),"",convert_UTC_to_Local(last_online));
			}
			catch(NullPointerException e) {
				account = new AccountInformation((String) term.get("account"), (String) term.get("display_name"),"","Not Logged In");
			}
			
			res_list_offline.add(account);
		}
		return new ResponsePresence(res_type, res_list_online, res_list_offline);
	}
	
	public List<ColoredText> Load_Group_Infor_From_DB() {
//		List<ColoredText> list_group = new ArrayList<ColoredText>();
//		try {
//			JSONObject jo_old = (JSONObject) new JSONParser().parse(new FileReader(DataPassTest+get_GroupFile()));
//			JSONArray jo_arr = (JSONArray) jo_old.get("conversation");
//			
//			
//			
//			for(int i = 0;i<jo_arr.size();++i) {
//				JSONObject group = (JSONObject) jo_arr.get(i);
//				ColoredText group_info = new ColoredText((String) group.get("name"),Color.BLUE, true, (String) group.get("ID"),"");
//				list_group.add(group_info);
//				
//			}
//			
//		} catch (IOException | ParseException e) {
//			e.printStackTrace();
//		}
		//return list_group;
		return this.list_cache_list_Group;
		
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
	
	public void Update_Member(String ID, List<String> list_name) {

		JSONArray jo_arr = new JSONArray();
		jo_arr.addAll(list_name);
		
		try {
			JSONObject jo_old = (JSONObject) new JSONParser().parse(new FileReader(DataPassTest+get_GroupFile()));
			JSONArray conversations = (JSONArray) jo_old.get("conversation");
			for(Object obj: conversations) {
				JSONObject jo = (JSONObject) obj;
				String ID_DB = (String) jo.get("ID");
				if(ID_DB.equals(ID)) {
					jo.put("participants", jo_arr);
					break;
				}
			}
			writefileDB(DataPassTest+get_GroupFile(), jo_old);
			
			
		} catch (IOException | ParseException e) {
			
			e.printStackTrace();
		}
	}
	
	public List<ColoredText> Res_New_Message_Notification(JSONObject jo){
		//System.out.println("Test 3");
		if(!result_new_nofication.isEmpty()) result_new_nofication.clear();
		List<String> temp = new ArrayList<String>();
		
		JSONArray jo2 = (JSONArray) jo.get("groups");
		List<ColoredText> listGroup = new ArrayList<ColoredText>();
		GroupNameNew.clear();
		for(int i = 0;i<jo2.size();++i) {
			JSONObject jo3 = (JSONObject) jo2.get(i);
			
			boolean new_group = (boolean) jo3.get("new");
			if(new_group) {
				GroupNameNew.add((String) jo3.get("name"));
			}
			
			List<String> list_names = new ArrayList<String>();
			List<String> list_accounts = new ArrayList<String>();
			JSONArray arr_participant = (JSONArray) jo3.get("participants");
			for(int j = 0 ; j<arr_participant.size();++j) {
				JSONObject participant = (JSONObject) arr_participant.get(j);
				list_names.add((String) participant.get("display_name"));
				list_accounts.add((String) participant.get("account"));
			}
			
			ColoredText newGroup = new ColoredText((String) jo3.get("name"),Color.BLUE, true, (String) jo3.get("_id"),list_accounts);
			listGroup.add(newGroup);
			list_cache_list_Group.add(new ColoredText((String) jo3.get("name"),Color.BLUE, true, (String) jo3.get("_id"),list_accounts));
			createGroupInDB(newGroup.getID(),list_names, (String) jo3.get("name"));
			
			if(!CheckGroupExistinDB(newGroup.getID())) createGroupInDB(newGroup.getID(),list_names, (String) jo3.get("name"));
			else {
				Update_Member(newGroup.getID(), list_names);
			}
		
		}
		if(list_dual.size()>0) list_dual.clear();
		if(list_group.size()>0) list_group.clear();
		
		JSONArray jo1 = (JSONArray) jo.get("messages");
		System.out.println("size: "+jo1.size());
		for(int i = 0 ;i<jo1.size();++i) {
			JSONObject message_new = (JSONObject) jo1.get(i);
			JSONObject jo3 = (JSONObject) message_new.get("sender");

			
			
			JSONObject conversation = (JSONObject) message_new.get("conversation");
			boolean isGroup = (boolean) conversation.get("is_group");
			String from = (String) jo3.get("account");
			
			if(isGroup) {
				String ID_Group = (String) conversation.get("_id");
				String Display_Name = (String) jo3.get("display_name");
				String Content = (String) message_new.get("content");
				Message message = new Message(from,ID_Group,(String) message_new.get("timestamp"),Display_Name+": "+Content,1, false);
				//Save_Message_Group(ID_Group, message);
				list_group.add(message);
				System.out.println("list_group1");
				temp.add(ID_Group);
			}
			else {
				reqhan.create_new_conversation(false,UiController.get_list_User(from));
				//Save_Message_New(NowUser, message);
				Message message = new Message(from,"",(String) message_new.get("timestamp"),(String) message_new.get("content"),1, false);
				list_dual.add(message);
				System.out.println("list_dual1");
				temp.add(from);
			}
		} 
		String[] arr_string = new String[temp.size()];
		temp.toArray(arr_string);
		Set<String> set_temp = new TreeSet<String>(temp);
		
		set_temp.forEach(action -> {
			NewMessageNotification new_message = new NewMessageNotification(action,count(arr_string,action));
			result_new_nofication.add(new_message);
		});
		return listGroup;
	}
	
	public List<String> list_paticipants(JSONArray jo_arr){
		List<String> result = new ArrayList<String>();
		for (Object obj : jo_arr) {
			JSONObject part = (JSONObject) obj;
			result.add((String) part.get("account"));
		}
		return result;
	}
	
	public boolean CheckGroupExistinDB(String ID) {
		boolean result = false;
		JSONObject jo_old;
		try {
			jo_old = (JSONObject) new JSONParser().parse(new FileReader(DataPassTest+get_GroupFile()));
			JSONArray jo_arr = (JSONArray) jo_old.get("conversation");
			for(int i = 0;i<jo_arr.size();++i) {
				JSONObject conversation = (JSONObject) jo_arr.get(i);
				if(((String) conversation.get("ID")).equals(ID)) return true;
			}
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String get_UTC_now() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat(UTC_Format);
		df.setTimeZone(tz);
		String nowAsISO = df.format(new Date());
		return nowAsISO;
	}
	
	public void createGroupInDB(String ID, List<String> listParticipant, String name) {
		JSONObject jo = new JSONObject();
		
		JSONArray arr_participant = new JSONArray();
		arr_participant.addAll(listParticipant);
		
		try {
			JSONObject jo_old = (JSONObject) new JSONParser().parse(new FileReader(DataPassTest+get_GroupFile()));
			JSONArray jo_arr = (JSONArray) jo_old.get("conversation");
			
			jo.put("ID", ID);
			jo.put("participants", arr_participant);
			
			jo.put("num_message", 0);
			jo.put("messages", new JSONArray());
			jo.put("name", name);
			jo.put("date_created", get_UTC_now());
			
			jo_arr.add(jo);
			
			writefileDB(DataPassTest+get_GroupFile(),jo_old);

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		
		
	}
	
	private void writefileDB(String filename, JSONObject jo) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(filename);
			pw.write(jo.toJSONString()); 
			pw.flush(); 
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
						int message_remain = temp1.size() - Num_Got_Message;
						for(int j = 0;j<message_remain;++j) {
							JSONObject temp2 = (JSONObject) temp1.get(j);
							Message message = new Message((String)temp2.get("from"),"",convert_UTC_to_Local((String) temp2.get("timestamp")),(String) temp2.get("content"),ChecktoFrom(NowUser,(String)temp2.get("from")));
							result.add(message);
						}
						Num_Got_Message += message_remain;
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
	public int Res_count_of_conversation(String toUser, JSONObject jo) {
		int result = -1;
		try {

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
			writefileDB(DataPassTest+get_Conversationfile(), jo);	
		
		} catch (ParseException e) {
			System.out.println("Parser JSON exception !");
		} catch (Exception e1) {
			System.out.println("Not Response Count Messages !");
		}
		System.out.println("Count: "+result);
		return result;
	}
	public int Res_get_messages(String toUser, JSONObject jo) {
		int result = -1;
		JSONObject jo_old ;
		JSONArray paticipants_array =  new JSONArray();
		paticipants_array.add(NowUser);
		paticipants_array.add(toUser);
		
		try {

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
								
								
							if(jo_arr.size()<reqhan.amount_messages) {
								jo_new.put("order",j);
							}
							else {
								jo_new.put("order",Num_Message_Now_Conversation-reqhan.amount_messages-num_now_message+j);
							}
							
							
							
							message_get.add(jo_new);
						}
						message_get.addAll(message_array);
						temp.put("messages", message_get);
						break;
					}
				}
				
				writefileDB(DataPassTest+get_Conversationfile(), jo_old);
				result = 1;
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
	public ColoredText Res_Notification_New_Group(JSONObject jo) {
		ColoredText result;
		String[] content = ((String) jo.get("groupInfo")).split(";");
		
		
		JSONArray participants = (JSONArray) jo.get("participants");
		List<String> list_name =  new ArrayList<String>();
		List<String> list_accounts =  new ArrayList<String>();
		
		for (Object par : participants) {
			list_name.add((String)((JSONObject) par).get("display_name"));
			list_accounts.add((String)((JSONObject) par).get("account"));
		}
		
		result = new ColoredText(content[1], Color.BLUE, true, content[0],list_accounts);
		createGroupInDB(content[0],list_name,content[1]);
		return result;
	}
	
	public void create_conversation_group(List<String> Paticipants, String ID) {
		
	}
	
	public String[] Res_Create_Group_Success(JSONObject jo) {
		return ((String) jo.get("content")).split(";");
		
	}
	
	public void Save_Message_Group(String ID, Message message) {
		JSONObject jo = new JSONObject();
		try {
			JSONObject jo_old = (JSONObject) new JSONParser().parse(new FileReader(DataPassTest+get_GroupFile()));
			JSONArray conversations = (JSONArray) jo_old.get("conversation");
			for(int i = 0;i<conversations.size();++i) {
				JSONObject conversation = (JSONObject) conversations.get(i);
				if(((String) conversation.get("ID")).equals(ID)) {
					JSONArray messages = (JSONArray) conversation.get("messages");
					int num_message = Math.toIntExact((long) conversation.get("num_message"));
					jo.put("from", message.from);
					jo.put("content", message.data);
					jo.put("timestamp", message.timestamp);
					jo.put("order", num_message);
					messages.add(jo);
					conversation.put("num_message", num_message+1);
					break;
				}
			}
			
			writefileDB(DataPassTest+get_GroupFile(), jo_old);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	
	public Message Res_Group_Message_Notification(JSONObject jo) {
		String timestamp = (String) jo.get("timestamp");
		
		
		JSONObject sender = (JSONObject) jo.get("sender");
		String display_name = (String) sender.get("display_name");
		String account = (String) sender.get("account");
		
		String content = (String) jo.get("content");
		JSONObject group_info = (JSONObject) jo.get("group");
		String ID_Group = (String) group_info.get("_id");
		String namegroup = (String) group_info.get("name");
		
		Message message = new Message(account,ID_Group+":"+namegroup, timestamp,display_name+": "+content ,1);
		
		Save_Message_Group((String) group_info.get("_id"), message);
		return message;
	}
	public List<Message> Load_Message_Group(String ID){
		List<Message> list_message = new ArrayList<Message>();
		JSONObject jo = new JSONObject();
		try {
			JSONObject jo_old = (JSONObject) new JSONParser().parse(new FileReader(DataPassTest+get_GroupFile()));
			JSONArray conversations = (JSONArray) jo_old.get("conversation");
			for(int i = 0;i<conversations.size();++i) {
				JSONObject conversation = (JSONObject) conversations.get(i);
				if(((String) conversation.get("ID")).equals(ID)) {
					JSONArray messages = (JSONArray) conversation.get("messages");
					
					if(messages.size()>=reqhan.amount_messages_torender + Num_Got_Message_Group) {
						for(int j = messages.size() - reqhan.amount_messages_torender - Num_Got_Message_Group;j<messages.size()-Num_Got_Message_Group;++j) {
							JSONObject message = (JSONObject) messages.get(j);
							Message mess = new Message((String) message.get("from"),"", convert_UTC_to_Local((String) message.get("timestamp")),(String) message.get("content"),ChecktoFrom(NowUser,(String) message.get("from")));
							list_message.add(mess);
						}
						Num_Got_Message_Group+=reqhan.amount_messages_torender;
					}
					else {
						int message_remain = messages.size() - Num_Got_Message_Group;
						for(int j = 0;j<message_remain;++j) {
							JSONObject message = (JSONObject) messages.get(j);
							Message mess = new Message((String) message.get("from"),"",convert_UTC_to_Local((String) message.get("timestamp")),(String) message.get("content"),ChecktoFrom(NowUser,(String) message.get("from")));
							list_message.add(mess);
						}
						Num_Got_Message_Group += message_remain;
						
					}
					break;
				}
			}
		}
		catch (IOException | ParseException e) {
		e.printStackTrace();
		}
		Collections.reverse(list_message);
		return list_message;
	}
	public int Get_Num_Message_In_Group(String ID) {
		JSONObject jo = new JSONObject();
		int result = 0;
		try {
			JSONObject jo_old = (JSONObject) new JSONParser().parse(new FileReader(DataPassTest+get_GroupFile()));
			JSONArray conversations = (JSONArray) jo_old.get("conversation");
			for(int i = 0;i<conversations.size();++i) {
				JSONObject conversation = (JSONObject) conversations.get(i);
				if(((String) conversation.get("ID")).equals(ID)) {
					JSONArray messages = (JSONArray) conversation.get("messages");
					result = messages.size();
					break;
				}
			}
		}
		catch (IOException | ParseException e) {
		e.printStackTrace();
		}
		return result;
	}
	
	public int Res_Message_Count_In_Group( JSONObject jo) {
		String ID  = (String) jo.get("group");
		int num_message = Math.toIntExact((long) jo.get("content"));
		try {
			JSONObject jo_old = (JSONObject) new JSONParser().parse(new FileReader(DataPassTest+get_GroupFile()));
			JSONArray conversations = (JSONArray) jo_old.get("conversation");
			for(int i = 0;i<conversations.size();++i) {
				JSONObject conversation = (JSONObject) conversations.get(i);
				if(((String) conversation.get("ID")).equals(ID)) {
					conversation.put("num_message", num_message);
					break;
				}
			}
			writefileDB(DataPassTest+get_GroupFile(), jo_old);
		}
		catch (IOException | ParseException e) {
		e.printStackTrace();
		}
		return num_message;
	}
	public int Res_Get_Message_Group(JSONObject jo) {
		int result = -1;
		String ID = (String) jo.get("group");
		try {
			JSONObject jo_old = (JSONObject) new JSONParser().parse(new FileReader(DataPassTest+get_GroupFile()));
			JSONArray conversations = (JSONArray) jo_old.get("conversation");
			for(int i = 0;i<conversations.size();++i) {
				JSONObject conversation = (JSONObject) conversations.get(i);
				if(((String) conversation.get("ID")).equals(ID)) {
					JSONArray messages = (JSONArray) conversation.get("messages");
					JSONArray messages_new = (JSONArray) jo.get("content");
					JSONArray messages_extract = new JSONArray();
					int num_now_message_in_DB = Get_Num_Message_In_Group(ID);
					int num_message_of_group = Math.toIntExact((long) conversation.get("num_message"));
					
					for(int j = 0;j<messages_new.size();++j) {
						JSONObject message_new = (JSONObject) messages_new.get(j);
						JSONObject sender = (JSONObject) message_new.get("sender");
						JSONObject message_extract =  new JSONObject();
						String account = (String) sender.get("account");
						String display_name = (String) sender.get("display_name");
						message_extract.put("from", account);
						if(!account.equals(NowUser)){
							message_extract.put("content",display_name+": "+(String) message_new.get("content"));
						}
						else {
							message_extract.put("content",(String) message_new.get("content"));
						}
						
						message_extract.put("timestamp", (String) message_new.get("timestamp"));
						message_extract.put("order", num_message_of_group-num_now_message_in_DB-messages_new.size()+j);
						
						messages_extract.add(message_extract);
					}
					messages_extract.addAll(messages);
					conversation.put("messages", messages_extract);
					result = 1;
					break;
				}
			}
			writefileDB(DataPassTest+get_GroupFile(), jo_old);
		}	
		catch (IOException | ParseException e) {
				e.printStackTrace();
		}
		return result;
		
	}
	
	public String Res_Mark_Seen_Message(JSONObject jo, boolean isGroup) {
		if (isGroup)
		return (String) jo.get("conversation");
		else return (String) jo.get("account");
	}
	
	public Call Res_New_Call(JSONObject jo) {
		//System.out.println("Test 1");
		String res_from = "";
		String res_timestamp = "";
		String result = (String) jo.get("type");
		if(result.equals(Constant.res_call_notification)) {
			res_from = (String) jo.get("from") ;
			res_timestamp = (String) jo.get("timestamp");
		}
		return new Call(res_from,"",res_timestamp,1);
	}
	public void Add_User_To_Group(String ID, List<String> list_acc) {
		
		try {
			JSONObject jo_old = (JSONObject) new JSONParser().parse(new FileReader(DataPassTest+get_GroupFile()));
			JSONArray conversations = (JSONArray) jo_old.get("conversation");
			for(Object obj: conversations) {
				JSONObject jo = (JSONObject) obj;
				String ID_DB = (String) jo.get("ID");
				if(ID_DB.equals(ID)) {
					JSONArray participants  = (JSONArray) jo.get("participants");
					participants.addAll(list_acc);
					break;
				}
			}
			writefileDB(DataPassTest+get_GroupFile(), jo_old);
			
		} catch (IOException | ParseException e) {
			
			e.printStackTrace();
		}
	}
	public ColoredText Res_Notification_Added_To_Group(JSONObject jo){
		String info = (String) jo.get("groupInfo");
		String []arr_info = info.split(";");
		JSONArray participants = (JSONArray) jo.get("participants");
		List<String> list_account = new ArrayList<String>();
		List<String> list_names = new ArrayList<String>();
		for(Object obj: participants) {
			JSONObject participant = (JSONObject) obj;
			list_account.add((String) participant.get("account"));
			list_names.add((String) participant.get("display_name"));
		}
		list_account.add(NowUser);
		list_names.add(NowUserName);
		
		createGroupInDB(arr_info[0], list_names, arr_info[1]);
		ColoredText result = new ColoredText(arr_info[1], Color.BLUE, true, arr_info[0], list_account);
		return result;
	}
	
	public ColoredText Res_Notification_New_Participant(JSONObject jo){
		String group_info = (String) jo.get("groupInfo");
		String[] arr_group_infor = group_info.split(";");
		String ID = arr_group_infor[0];
		String name = arr_group_infor[1];
		String account = (String) jo.get("account");
		String display_name = (String) jo.get("displayName");
		List<String> list_names = new ArrayList<String>();
		List<String> list_accounts = new ArrayList<String>();
		list_names.add(display_name);
		list_accounts.add(account);
		Add_User_To_Group(ID, list_names);
		return new ColoredText("", Color.BLUE, true, ID, list_accounts);
	}
	
	public JSONArray get_New_Participants(JSONArray pars, String par) {
		JSONArray participants_new = new JSONArray();
		for (Object obj: pars) {
			String old_par = (String) obj;
			if(!old_par.equals(par)) participants_new.add(old_par);
		}
		return participants_new;
	}
	
	public AccountInformation Res_Leave_Group(JSONObject jo) {
		String account = (String) jo.get("account");
		String display_name = (String) jo.get("display_name");
		String group_info = (String) jo.get("groupInfo");
		String[] arr_group_infor = group_info.split(";");
		String ID = arr_group_infor[0];
		String name_group = arr_group_infor[1];
		try {
			JSONObject jo_old = (JSONObject) new JSONParser().parse(new FileReader(DataPassTest+get_GroupFile()));
			JSONArray conversations = (JSONArray) jo_old.get("conversation");
			for(Object obj: conversations) {
				JSONObject conversation = (JSONObject) obj;
				String ID_DB = (String) conversation.get("ID");
				if(ID_DB.equals(ID)) {
					JSONArray participants = (JSONArray) conversation.get("participants");
					conversation.put("participants", get_New_Participants(participants, display_name));
					break;
				}
			}
			writefileDB(DataPassTest+get_GroupFile(), jo_old);
		}
		catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return new AccountInformation(account, display_name, ID); 
	}
	
}
