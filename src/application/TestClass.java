package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.UUID;


import org.json.simple.*;
import org.json.simple.parser.JSONParser;
 
  

public class TestClass {
	
	public static void main(String[] args) throws FileNotFoundException {
		JSONArray arr = new JSONArray();
		List<String> term = new ArrayList<String>();
		String[] term2 = {"123","456","789"};
		term.add("1");
		term.add("2");
		term.add("3");
		arr.addAll(Arrays.asList(term2));
		//System.out.println(arr);
		String term1 = "nhanvt|huytc|test1";
		
		System.out.println(term1.split("\\|")[2]);
		
		
//		UUID uid = UUID.randomUUID();
//		System.out.println(uid.toString());
//		uid = UUID.randomUUID();
//		System.out.println(uid.toString());
//		uid = UUID.randomUUID();
//		System.out.println(uid.toString());
//		uid = UUID.randomUUID();
//		System.out.println(uid.toString());
//		uid = UUID.randomUUID();
//		System.out.println(uid.toString());
//		uid = UUID.randomUUID();
//		System.out.println(uid.toString());
//		uid = UUID.randomUUID();
//		System.out.println(uid.toString());
//		TimeZone tz = TimeZone.getTimeZone("UTC");
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
//		df.setTimeZone(tz);
//		String nowAsISO = df.format(new Date());
//		System.out.println(nowAsISO);
//		
//		
//	
//		DateFormat df1 = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
//		TimeZone now = Calendar.getInstance().getTimeZone();
//		System.out.println(now.getID());
//		df1.setTimeZone(TimeZone.getTimeZone(now.getID()));
//		String nowAsISO1;
//		try {
//			nowAsISO1 = df1.format(df.parse(nowAsISO));
//			System.out.println(nowAsISO1);
//		} catch (java.text.ParseException e) {
//			e.printStackTrace();
//		}
//		JSONArray arr_user = new JSONArray();
//		arr_user.put("nhanvt");
//		arr_user.put("huytc");
//		JSONArray arr_user1 = new JSONArray();
//		arr_user1.put("nhanvt");
//		arr_user1.put("huytc");
//		System.out.println(ResponseHandler.check_JSON_Array_Equal(arr_user, arr_user1));
//		
//		//String jsondata = "{\"now\":{\"0\":\"nhanvt\",\"1\"\"huytc\"}}";
//		String jsondata ="{\"now\":[\"ahihi\",\"ahuhu\"]}";
//		try {
//			JSONObject jo = new JSONObject(jsondata);
//			JSONArray jo_arr = jo.getJSONArray("now");
//			System.out.println(jo_arr.get(1));
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		//JsonCreate();
		
//		List<Message> list = new ArrayList<Message>();
//		list.add(new Message("1", "aa", "aaa"));
//		list.add(new Message("2", "bb", "bbb"));
//		list.add(new Message("3", "cc", "ccc"));
//
//		Message temp = list.get(0);
//		System.out.println(temp.from);
//		temp.from = "10";
//		Message temp1 = list.get(0);
//		System.out.println(temp1.from);
		//String a = "con mèo con chó";
		//System.out.println(a);
//		TimeZone tz = TimeZone.getTimeZone("UTC");
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
//		df.setTimeZone(tz);
//		String nowAsISO = df.format(new Date());
//		System.out.println(nowAsISO);
//		String json = "{\"content\":{\"0\":{\"ahihi\":\"ahihi\"},\"1\":{\"ahuhu\":\"ahuhu\"},\"2\":{\"ahehe\":\"ahehe\"}}}";
//		List<String> temp = new ArrayList<String>();
//		temp.add("ab");
//		temp.add("b");
//		temp.add("ab");
//		temp.add("b");
//		temp.add("ab");
//		temp.add("c");
//		String[] arr_string = new String[temp.size()];
//		temp.toArray(arr_string);
//		Set<String> set_temp = new TreeSet<String>(temp);
//		set_temp.forEach(action -> {
//			System.out.println(count(arr_string,action));
//		});
//		System.out.println(set_temp.toString());
		//System.out.println(check_UTC_Time("2018-10-19T07:03:59.000Z"));
		
		
	}
	private static boolean check_UTC_Time(String time) {
		DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
		df2.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date result;
		try {
			result = df2.parse(time);
			return true;
		} catch (java.text.ParseException e) {
			return false;
			
		}
	}
	private static int count(String[] list, String k) {
		int result  = 0;
		for(int i = 0;i<list.length;++i) {
			if(list[i].equals(k))
			result++;
		}
		return result;
	}
	 public void testreadfile1() {
	    	
	    	FileReader fr;
			try {
				File f = new File("MessagePass1.txt");
				fr = new FileReader(f);
				BufferedReader br =  new BufferedReader( new InputStreamReader(new FileInputStream(f), "UTF8"));
				String line;
				while((line = br.readLine()) !=null) {
					System.out.println(line);
					//RenderMessage(0,line);
				}
				fr.close();
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    public void testreadfile2() {
	    	FileReader fr;
			try {
				File f = new File("MessagePass2.txt");
				fr = new FileReader(f);
				BufferedReader br =  new BufferedReader( new InputStreamReader(new FileInputStream(f), "UTF8"));
				String line;
				while((line = br.readLine()) !=null) {
					System.out.println(line);
					//RecieveMessage(0,line);
				}
				fr.close();
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
}