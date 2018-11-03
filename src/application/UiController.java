package application;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.awt.Desktop;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;

import ChatBubble.BubbleSpec;
import ChatBubble.BubbledLabel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

import org.controlsfx.control.Notifications;
import org.controlsfx.control.textfield.TextFields;


public class UiController implements Initializable {

    private double xOffset = 0;
    private double yOffset = 0;
    
    @FXML
    private JFXTextField txtContMess;
    
    @FXML
    private VBox vboxChat;
    
    @FXML
    private ScrollPane scrollChat;
    
    @FXML
    private ScrollPane scrollOnline;
    
    @FXML
    private JFXListView<ColoredText> listOnlineUser;
    
    @FXML
    private JFXListView<ColoredText> listNumNewMessage;
    
    @FXML
    private Label lblNotification;
    
    @FXML
    private Label lblToUser;
    
    @FXML 
    private Label lblToUserStatus;
    
    @FXML
    private Button btnSetUser;

    @FXML
    private TextField txtUser;
    
    @FXML
    private AnchorPane parent;
    
    @FXML
    private TextField txtSearch;

    @FXML
    private Pane chatAll, conversation1;
    @FXML
    private JFXButton btnCreateGroup, btn1, btnSendMess;
    
    

    @FXML
    private void close(MouseEvent event) {
        Terminate_connection();
    	Stage stage_chat = (Stage) txtSearch.getScene().getWindow();
    	stage_chat.close();
    }

    @FXML
    private void maximize(MouseEvent event) {
    }

    //CHANGE TAB TO ANOTHER CONVERSATION

    @FXML
    private void handlerButtonAction(ActionEvent event){
        if(event.getSource() == btn1){
            conversation1.toFront();
        }
        else if(event.getSource() == btnCreateGroup){
            chatAll.toFront();
            create_group_gui();
        }
    }
    
    @FXML
    private void handlerSearchbtn(MouseEvent e) {
    	Change_Item_listOnlineUser(txtSearch.getText());
    }
    
    @FXML
    private void ClickToChooseFile(MouseEvent e) throws IOException {
    	Choose_File();
    }

    @FXML
    private void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }
 
    
    
    
    // SET UP CONNECTION
    String Username="";
    String toUser = "";
    String serverHost = "35.240.252.106";
    int[] port = {8000,8001,8002,8003};
    List<String> list_User = null;
    int num_port  = 4;
    List<Socket> socketOfClient = new ArrayList<Socket>();
    List<BufferedReader> is =  new ArrayList<BufferedReader>();
    List<BufferedWriter> os =  new ArrayList<BufferedWriter>();
    boolean isGettingMessage = false;

    
    
    // Desktop (File)
    private Desktop desktop = Desktop.getDesktop();
    private FileChooser fileChooser = new FileChooser();
    
    
    // Control
    public void setDisableOfCreateGroup(Boolean val) {
    	btnCreateGroup.setDisable(val);
    }
    
    private boolean ReceiveDataAfterLogin = false;
    
    
    
    //Handler
    ResponseHandler responsehandler = new ResponseHandler();
    RequestHandler reqhan = new RequestHandler();
    
    private void Change_Item_listOnlineUser(String item) {
    	List<ColoredText> list_item = listOnlineUser.getItems();
    	for(int i = 0;i<list_item.size();++i) {
    		if(list_item.get(i).getText().equals(item)) {
    			scroll_and_select_item(i);
    			break;
    		}
    	}
    }
    
    public void scroll_and_select_item(int index) {
    	listOnlineUser.scrollTo(index);
    	listOnlineUser.getSelectionModel().select(index);
    }
    
    public void create_group_from_gui(List<String> list_account, String namegroup, List<String> list_name) {
    	
    	this.list_name_users_group = list_name;
    	UUID uid = UUID.randomUUID();
    	JSONObject jo =  reqhan.Req_create_group_Chat(list_account,uid.toString(),namegroup);
    	
    	ColoredText group = new ColoredText(namegroup, Color.BLUE, true, uid.toString(), list_account);
    	Create_Group(group);
    	
    	System.out.println(jo.toJSONString());
    	try {
			os.get(0).write(jo.toString());
			os.get(0).newLine();
		    os.get(0).flush();
		    	
		} catch (IOException e) {
			e.printStackTrace();
		}
       
    }
    
    
    
    private void create_group_gui() {

    	FXMLLoader loader =  new FXMLLoader();
    	loader.setLocation(getClass().getResource("Create_group_gui.fxml"));
    	
    	Parent create_group_view = null;
		try {
			create_group_view = loader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
			
		}
    	Scene scene = new Scene(create_group_view);
    	Stage create_group_state = new Stage();
    	create_group_state.setScene(scene);
    	create_group_state.setTitle("Create Group");
    	
    	CreateGroupController groupcont = loader.getController();
    	groupcont.setListOnlineOffline(getListUser());
    	//groupcont.create_list_user();
    	groupcont.create_listcheckbox();
    	
    	create_group_state.show();
    	create_group_state.setOnHidden((WindowEvent event1) ->{
    		setDisableOfCreateGroup(false);
    		if(!groupcont.get_name_group().equals("")) create_group_from_gui(groupcont.getlist_user_after_check(),groupcont.get_name_group(), groupcont.getlist_name_after_check());
    	});
    	
    	setDisableOfCreateGroup(true);
    }
    private List<ColoredText> getListUser(){
    	List<ColoredText> result = new ArrayList<ColoredText>();
    	listOnlineUser.getItems().forEach(action -> {
    		if(!action.getisGroup()) result.add(action);
    	});
    	return result;
    }
    
    private void Create_Group(String namegroup, String uuid) {
    	listOnlineUser.getItems().add(new ColoredText(namegroup,Color.BLUE, true, uuid,""));
    	listNumNewMessage.getItems().add(new ColoredText(String.valueOf(0),Color.BLUE, true, uuid,""));
    	
    }
    
    private void Create_Group(ColoredText group) {
    	listOnlineUser.getItems().add(group);
    	listNumNewMessage.getItems().add(new ColoredText(String.valueOf(0),Color.BLUE, true, group.getID(),""));
    	responsehandler.list_cache_list_Group.add(new ColoredText(group.getText(), group.getColor(), group.getisGroup(), group.getID(),group.getparticipants()));
    }
    
    private void Create_Groups(List<ColoredText> listGroup) {
    	listGroup.forEach(action -> {
    		listOnlineUser.getItems().add(action);
        	listNumNewMessage.getItems().add(new ColoredText(String.valueOf(0),Color.BLUE, true, action.getID(),""));
    		
    	});
    	
    }
    
    private void Mark_Message_Seen(String ID, boolean isGroup) {
    	JSONObject jo = reqhan.Req_mark_messages_seen(ID, isGroup);
    	try {
    		os.get(2).write(jo.toString());
			os.get(2).newLine();
		    os.get(2).flush();
    	}
    	catch(IOException e) {
    		e.printStackTrace();
    	}
    	
    }
    
    private HBox createBubble(int alignment, VBox vbox, String text,String datetime, String color, Pos pos, BubbleSpec bubbleSpec){
        HBox hBox = new HBox();
        BubbledLabel b1 = new BubbledLabel();
        BubbledLabel b2 = new BubbledLabel();
        b1.setText(text);
        b1.setFont(new Font(12));
        b1.setMaxSize(250, 500);
        b1.setCursor(Cursor.HAND);
        b1.setStyle("-fx-background-color: "+color);
        b1.setWrapText(true);
        hBox.setMaxWidth(vbox.getWidth() - 20);
        hBox.setAlignment(pos);
        b1.setBubbleSpec(bubbleSpec);
        b2.setText(datetime);
        b2.setBubbleSpec(bubbleSpec);
        b2.setStyle("-fx-background-color: black;-fx-text-fill: white");
       
        b2.setVisible(false);
        
        b1.setOnMouseEntered(value ->{
        	b2.setVisible(true);
        });
        b1.setOnMouseExited(value ->{
        	b2.setVisible(false);
        });
        
        if(alignment == 0) {
        	hBox.getChildren().add(b2);
            hBox.getChildren().add(b1);
        }
        else {
        	hBox.getChildren().add(b1);
            hBox.getChildren().add(b2);
        }
        
        
        
        JFXDepthManager.setDepth(hBox, 2);
        return  hBox;
    }
    
    private int Get_Num_Message_By_ID(String ID) {
    	int id = 0;
    	for(int i = 0;i<listNumNewMessage.getItems().size();++i) {
    		if(listNumNewMessage.getItems().get(i).getID().equals(ID)) {
    			return Integer.valueOf(listNumNewMessage.getItems().get(i).getText());
    		}
    	}
    	return id;
    }
   
    
    
    // File control
    private void Choose_File() throws IOException {
    	File fileToSend  = fileChooser.showOpenDialog(btn1.getScene().getWindow());
    	if(fileToSend !=null) {
    		Socket socket = null;
            String host = "127.0.0.1";

            socket = new Socket(host, 4444);

            //File fileToSend = new File("d:\\snakeGame.html");
            // Get the size of the file
            long length = fileToSend.length();
            
            byte[] bytes = new byte[16 * 1024];
            InputStream in = new FileInputStream(fileToSend);
            OutputStream out = socket.getOutputStream();

            //send the file name first
            DataOutputStream fnOut = new DataOutputStream(out);
            fnOut.writeUTF(fileToSend.getName());
            fnOut.writeLong(length); 
            
            		
            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }

            out.close();
            in.close();
            socket.close();
    	}
    }
    
    private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {
           System.out.println("Fail to open file !");
        }
    }
    

    
    // Data Structure
    Queue<Message> queue_message = new LinkedList();
    List<NewMessageNotification> new_message_num = null;
    List<AccountInformation> list_online = null;
    List<AccountInformation> list_offline = null;
    List<String> list_name_users_group = null;
    List<ColoredText> list_cache_list_Online = null;
    

    
    // port 8000
    public void EnterToSendMessage(KeyEvent event) {
        if(event.getCode()==KeyCode.ENTER) {
        	EnterToSendMessageHandler();
        }
    }
    
    public void ClickToSendMessage(MouseEvent event) {
    	EnterToSendMessageHandler();
    }
    
    public void ClickToViewInFor(MouseEvent event) {
    	if(listOnlineUser.getSelectionModel().getSelectedItem().getisGroup()) {
    		ClickToViewInForHandler(listOnlineUser.getSelectionModel().getSelectedItem().getID());
    	}
    }
    
    public void ClickToViewInForHandler(String ID) {
    	JSONObject jo = reqhan.Get_InFor_Group(ID);
    	
    	System.out.println("jo "+jo.toJSONString());
    	
    	FXMLLoader loader =  new FXMLLoader();
    	loader.setLocation(getClass().getResource("GroupInfor.fxml"));
    	
    	Parent group_infor_view = null;
		try {
			group_infor_view = loader.load();
		} catch (IOException e1) {
			e1.printStackTrace();
			
		}
    	Scene scene = new Scene(group_infor_view);
    	Stage group_infor_state = new Stage();
    	group_infor_state.setScene(scene);
    	group_infor_state.setTitle("Group Information");
    	
    	GroupInforController groupcont = loader.getController();
    	groupcont.Set_Group_Name((String) jo.get("name"));
    	groupcont.Set_Group_Date(responsehandler.convert_UTC_to_Local((String) jo.get("date_created")));
    	List<String> list_user = new ArrayList<String>();
    	list_user.addAll((JSONArray) jo.get("participants"));
    	
    	groupcont.Set_List_User(list_user);
    	groupcont.setListOnlineOffline(getListUser());
    	groupcont.setListAccount(listOnlineUser.getSelectionModel().getSelectedItem().getparticipants());
    	
    	group_infor_state.setOnHidden((WindowEvent event1) ->{
    		if(groupcont.list_Other_User_Result.size()>0) Add_New_User_To_Group_GUI(groupcont.list_Other_User_Result);
    		if(groupcont.getisExit()) LeaveGroup();
    	});
    	
    	
    	group_infor_state.show();
    	
    }
    
    public void Add_New_User_To_Group_GUI(List<AccountInformation> list_acc) {
    	List<String> name_users = new ArrayList<String>();
    	for(AccountInformation acc: list_acc) {
    		JSONObject jo = reqhan.Add_New_User_To_Group(toUser, acc.account_name);
    		name_users.add(acc.display_name);
    		try {
    			os.get(0).write(jo.toString());
                os.get(0).newLine();
                os.get(0).flush();
    		}
    		catch (IOException e) {
    			  e.printStackTrace();
			}
    		listOnlineUser.getSelectionModel().getSelectedItem().participants.add(acc.account_name);
    	}
    	responsehandler.Add_User_To_Group(toUser, name_users);
    }
    
    public void Add_New_User_To_Group_GUI_ID(ColoredText group) {
		listOnlineUser.getItems().forEach(action -> {
			if(action.getID().equals(group.getID())) {
				action.participants.addAll(group.getparticipants());
			}
		});
		responsehandler.list_cache_list_Group.forEach(action -> {
			if(action.getID().equals(group.getID())) {
				action.participants.addAll(group.getparticipants());
			}
		});
		
	}
    
    public void LeaveGroup() {
    	JSONObject jObject = reqhan.Leave_Group(toUser);
    	try {
			os.get(0).write(jObject.toString());
            os.get(0).newLine();
            os.get(0).flush();
            removeGroup(toUser,true,"");
		}
		catch (IOException e) {
			  e.printStackTrace();
		}
    }
    public void removeGroup(String ID, boolean active, String account) {
    	if(active) {
        	int i = 0;
        	for(ColoredText group: listOnlineUser.getItems()) {
        		if (group.getID().equals(ID)){
        			break;
        		}
        		i++;
        	}
        	listOnlineUser.getItems().remove(i);
        	listNumNewMessage.getItems().remove(i);
        	i = 0;
        	for(ColoredText group: responsehandler.list_cache_list_Group) {
        		if (group.getID().equals(ID)){
        			break;
        		}
        		i++;
        	}
        	responsehandler.list_cache_list_Group.remove(i);
        	listOnlineUser.getSelectionModel().select(0);
    	}
    	else {
        	for(ColoredText group: listOnlineUser.getItems()) {
        		if (group.getID().equals(ID)){
        			RemoveFromParticipant(group.participants, account);
        			break;
        		}
        	}
//        	for(ColoredText group: responsehandler.list_cache_list_Group) {
//        		if (group.getID().equals(ID)){
//        			RemoveFromParticipant(group.participants, account);
//        			break;
//        		}
//        	}
    	}
    }
    
    public void RemoveFromParticipant(List<String> pars, String account) {
    	int i = 0;
    	for(String acc: pars) {
    		if (acc.equals(account)){
    			break;
    		}
    		i++;
    	}
    	pars.remove(i);
    }
    
    public void EnterToSendMessageHandler() {
    	if(txtContMess.getText().length()!=0 && !toUser.equals(Username)) {
    		scrollChat.vvalueProperty().bind(vboxChat.heightProperty());
        	if(listOnlineUser.getSelectionModel().getSelectedItem().getisGroup()) {
    			SendMessageGroup(listOnlineUser.getSelectionModel().getSelectedItem().getID(),txtContMess.getText());
    		}
    		else {
    			SendMessage(toUser,txtContMess.getText());
    		}
        	txtContMess.setText("");
    		
    	}
    }
    
    
    public void SendMessage(String to, String mess) {
 
    	try {
        	JSONObject jo = reqhan.Req_Send_Message(to,mess);
        	String datetime = responsehandler.convert_UTC_to_Local((String) jo.get("timestamp"));
        	Message message =  new Message(Username,to,(String) jo.get("timestamp"),mess,0);
        	responsehandler.Save_Message_New(Username,message);
        	RenderMessage(0,datetime,mess);
        	os.get(0).write(jo.toString());
            os.get(0).newLine();
            os.get(0).flush();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public void SendMessageGroup(String ID, String Content) {
    	try {
        	JSONObject jo = reqhan.Req_Send_Message_Group(ID, Content);
        	String datetime = responsehandler.convert_UTC_to_Local((String) jo.get("timestamp"));
        	RenderMessage(0,datetime,Content);
        	responsehandler.Save_Message_Group(ID, new Message(Username,ID,(String) jo.get("timestamp"), Content,0));
        	
        	os.get(0).write(jo.toString());
            os.get(0).newLine();
            os.get(0).flush();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    	
    }
    
    
    public void GetMessagesCount(boolean isGroup) {
    	try {
    		JSONObject jo = reqhan.Req_Get_Messages_Count(toUser,isGroup); 
        	os.get(0).write(jo.toString());
            os.get(0).newLine();
            os.get(0).flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    
    public void Get_Some_Message(int lastmessage, int num_now_message, boolean isGroup) {
    	try {
    		JSONObject jo =  reqhan.Req_Get_Messages(toUser, lastmessage,num_now_message, isGroup);
    		System.out.println(jo.toJSONString());
        	os.get(0).write(jo.toString());
            os.get(0).newLine();
            os.get(0).flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    }
    
    public List<Message> get_List_Message_From_ID(String ID, boolean isGroup){
    	List<Message> list = new ArrayList<Message>();
    	if(isGroup) {
    		for(int i = 0;i<ResponseHandler.list_group.size();++i) {
    			if(ResponseHandler.list_group.get(i).to.equals(ID) && !ResponseHandler.list_group.get(i).isSave){
    				list.add(ResponseHandler.list_group.get(i));
    				ResponseHandler.list_group.get(i).isSave = true;
    			}
    		}
    	}
    	else {
    		for(int i = 0;i<ResponseHandler.list_dual.size();++i) {
    			if(ResponseHandler.list_dual.get(i).from.equals(ID) && !ResponseHandler.list_dual.get(i).isSave) {
    				list.add(ResponseHandler.list_dual.get(i));
    				ResponseHandler.list_dual.get(i).isSave = true;
    			}
    		}
    	}
    	return list;
    }
    
    public void RecieveMessage(int alignment, String datetime,String mess) {
    	HBox hbox = createBubbledMesage(mess, datetime,alignment);
    	vboxChat.getChildren().add(0,hbox);
    }
    
    public void RenderMessage(int alignment,String datetime,String mess) {
    	HBox hbox = createBubbledMesage(mess, datetime,alignment);
    	vboxChat.getChildren().add(hbox);
    	txtContMess.setText(null);
    }
    public HBox createBubbledMesage(String data,String datetime, int alignment) {
    	HBox hbox = null;
    	if(alignment == 1) {
    		hbox = createBubble(alignment,vboxChat, " "+ data , datetime, "lightgreen", Pos.TOP_LEFT, BubbleSpec.FACE_LEFT_CENTER);
    	}
    	else {
    		hbox = createBubble(alignment,vboxChat, " "+ data , datetime,"white", Pos.TOP_RIGHT, BubbleSpec.FACE_RIGHT_CENTER);
    	}
    	return hbox;
    }
    
    public void create_Notification_for_new_Group() {
    	if(ResponseHandler.GroupNameNew.size()>0) {
    		String content = ResponseHandler.GroupNameNew.get(0);
    		for(int i = 1;i<ResponseHandler.GroupNameNew.size();++i) {
    			content+= ", "+ResponseHandler.GroupNameNew.get(i);
    		}
    		create_notification("Group Chat !", "You have just been invited to groups: "+content);
    	}
    	
    }
    
    public void Set_ID_New_Group(String[] arr) {
    	listOnlineUser.getItems().forEach(action -> {
    		if(action.getID().equals(arr[0])) {
    			action.setID(arr[1]);
    			responsehandler.createGroupInDB(arr[1],this.list_name_users_group, action.getText());
    		}
    	});
    	responsehandler.list_cache_list_Group.forEach(action -> {
    		if(action.getID().equals(arr[0])) {
    			action.setID(arr[1]);
    		}
    	});
    }
    
    
    // port 8001
    
    public void ReceiveListOnlineUser( List<AccountInformation> list_online,  List<AccountInformation> list_offline) {
    	this.list_online = list_online;
    	this.list_offline = list_offline;
    
    	listOnlineUser.getItems().clear();
    	for(int i = 0;i<list_online.size();++i) {
    		listOnlineUser.getItems().add(new ColoredText(list_online.get(i).display_name,Color.GREEN,false,list_online.get(i).account_name,list_online.get(i).online_status));
    		
    	}
    	for(int i = 0;i<list_offline.size();++i) {
    		listOnlineUser.getItems().add(new ColoredText(list_offline.get(i).display_name,Color.BLACK,false,list_offline.get(i).account_name,list_offline.get(i).online_status));
    		
    	}
    	setBindingSearch();
    	SetNewMessageNotification();
    	
    }

    
    
    public void setBindingSearch() {
    	String[] arr_concat = new String[list_online.size() + list_offline.size()];
    	String[] arr_online = responsehandler.Extract_display_name(list_online);
    	String[] arr_offline = responsehandler.Extract_display_name(list_offline);
    	System.arraycopy(arr_online, 0, arr_concat, 0, arr_online.length);
    	System.arraycopy(arr_offline, 0, arr_concat, arr_online.length, arr_offline.length);
    	TextFields.bindAutoCompletion(txtSearch, arr_concat);
    }
    
    
    // port 8002 
   
    public void ReceiveNotification(String note) {
        lblNotification.setText("Notification: "+note);
    }
    
    private void SetListChat(List<Message> list_old_message) {
    	if(vboxChat.getChildren()!=null) {
    		list_old_message.forEach(action -> {
    			RecieveMessage(action.ToFrom, action.timestamp, action.data);
    		});
    	}
    }
    
    public  void Call_btn_click(MouseEvent e) {
    	if(!toUser.equals("")) SendCall(toUser);
    }
    
    public void SendCall(String to) {
    	try {
            JSONObject jo = reqhan.Req_Send_Call(to);
            String datetime = responsehandler.convert_UTC_to_Local((String) jo.get("timestamp"));
            Call call =  new Call(Username,to,(String) jo.get("timestamp"),0);
            os.get(0).write(jo.toString());
            os.get(0).newLine();
            os.get(0).flush();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    private void SetNewMessageNotification() {
    	if(new_message_num != null) {
    		listNumNewMessage.getItems().clear();
    		List<ColoredText> list = listOnlineUser.getItems();
    		list.forEach(action -> {
    			int term = 0;
    			for(int i = 0;i<new_message_num.size();++i) {
    				if(action.getID().equals(new_message_num.get(i).account)) {
    					if(!new_message_num.get(i).isGroup) {
    						listNumNewMessage.getItems().add(new ColoredText(String.valueOf(new_message_num.get(i).num_message),Color.RED,false,new_message_num.get(i).account,""));
    					}
    					else {
    						listNumNewMessage.getItems().add(new ColoredText(String.valueOf(new_message_num.get(i).num_message),Color.BLUE,true,new_message_num.get(i).account,""));
    					}
    					term = 1;
    					break;
    				}
    			}
    			if(term == 0) {
    				if(!action.getisGroup()) {
						listNumNewMessage.getItems().add(new ColoredText(String.valueOf(0),Color.RED,false,action.getID(),""));
					}
					else {
						listNumNewMessage.getItems().add(new ColoredText(String.valueOf(0),Color.BLUE,true,action.getID(),""));
					}
    			}
    		});
    		
    	}
    }
    
    public void Set_Seen_Conversation(String ID) {
    	int i;
    	List<ColoredText> num_new_message_list = listNumNewMessage.getItems();
    	for(i = 0;i<num_new_message_list.size();++i) {
    		if (num_new_message_list.get(i).getID().equals(ID)) {
    			if(!num_new_message_list.get(i).getisGroup()) {
    				listNumNewMessage.getItems().set(i, new ColoredText(String.valueOf(0),Color.RED,false,num_new_message_list.get(i).getID(),""));
    			}
    			else {
    				listNumNewMessage.getItems().set(i, new ColoredText(String.valueOf(0),Color.BLUE,true,num_new_message_list.get(i).getID(),""));
    			}
    			break;
    		}
    	}
    	
    }
    
    
    // all port
    
    public void SetUser() {
        //this.Username = txtUser.getText();
    	txtUser.setDisable(true);
    	btnSetUser.setDisable(true);
        check_account(Username);
        ResponseHandler.NowUser = this.Username;
        RequestHandler.NowUser = this.Username;
        reqhan.create_db_for_new_User();
    }
    
    public void Set_Account(String account) {
    	this.Username = account;
    	txtUser.setText(Username);
    }
    
    public void check_account(String account) {
		JSONObject jo = reqhan.Req_Notification_Online(account);
		for(int i = 0 ;i<num_port;++i) {
			try {
				os.get(i).write(jo.toString());
				os.get(i).newLine();
				os.get(i).flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
    
    public void Get_Connection(){
        for (int i = 0 ;i<num_port;++i) {
        	try{
                socketOfClient.add(new Socket(serverHost, port[i]));
                os.add(new BufferedWriter(new OutputStreamWriter(socketOfClient.get(i).getOutputStream())));
                is.add(new BufferedReader(new InputStreamReader(socketOfClient.get(i).getInputStream())));
            }catch (UnknownHostException e){
                System.err.println("Don't know about host " + serverHost);
                return;
            }catch (IOException e){
                System.err.println("Could't get IO for the connection to "+ serverHost);
                return;
            }
        }
    }
    
    public void Terminate_connection() {
        for(int i = 0;i<num_port;++i) {
        	try {
                os.get(i).close();
                is.get(i).close();
                socketOfClient.get(i).close();
            } catch (UnknownHostException e) {
                System.err.println("Trying to connect to unknown host: " + e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Test terminate !");
    }
    
   
    
    

    private void makeStageDrageable() {
        parent.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Main.stage.setX(event.getScreenX() - xOffset);
                Main.stage.setY(event.getScreenY() - yOffset);
                Main.stage.setOpacity(0.7f);
            }
        });
        parent.setOnDragDone((e) -> {
            Main.stage.setOpacity(1.0f);
        });
        parent.setOnMouseReleased((e) -> {
            Main.stage.setOpacity(1.0f);
        });

    }
    
    public static void create_notification(String title, String content) {
    	Notifications notification = Notifications.create()
                .title(title)
                .text(content)
                .hideAfter(Duration.millis(3 * 1000))
                .position(Pos.TOP_RIGHT)
                .onAction((ect) -> {
                    System.out.println("You clicked me!");
                });
        notification.darkStyle();
        notification.show();
    }
    
    private int  Get_index_user_by_ID(String ID) {
    	List<ColoredText> list = listOnlineUser.getItems();
    	int i = -1;
    	for( i  = 0;i<list.size();++i) {
    		if(list.get(i).getID().equals(ID)) return i;
    	}
    	return i;
    }
    
    class SocketMessageThread extends Thread {
        UiController cont;
        ResponseHandler res_hand = new ResponseHandler();
        BufferedReader isthread = null;
        public SocketMessageThread(UiController cont){
            this.cont = cont;
            this.isthread = is.get(0); 
        }
        @Override
        public void run() {
            
            
            try {
            		String responseLine;
            		System.out.println("I am wating ... (8000) ");
            		while ((responseLine = this.isthread.readLine()) != null) {
            			System.out.println("8000:"+responseLine);
            			OnMessage(this.cont, responseLine);
            		}
            		System.out.println("terminate !");
            		create_notification("Attention", "Terminate the server connection !");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void OnMessage(UiController cont, String line) {
            javafx.application.Platform.runLater(new Runnable() {
                @Override
                public void run() {

                	JSONObject jo;
					try {
						jo = (JSONObject) new JSONParser().parse(line);
						String type = (String) jo.get("type");
						if(type.equals(Constant.res_get_message_count)) {
							ResponseHandler.Num_Message_Now_Conversation = res_hand.Res_count_of_conversation(toUser,jo);
							int now_num_message = responsehandler.Count_Message_Now_In_Conversation(toUser);
							if(ResponseHandler.Num_Message_Now_Conversation>now_num_message) {
								Get_Some_Message(ResponseHandler.Num_Message_Now_Conversation,now_num_message,false);
							}
							else {
								create_notification("Full Message !", "You have already loaded enough messages for this chat !");
							}
							
						}
						else if(type.equals(Constant.res_get_messages)) {
							int result = res_hand.Res_get_messages(toUser,jo);
							if(result!=-1) {
								System.out.println("Get Messages Successfully For "+Username+" From "+toUser);
								
							}
							SetListChat(responsehandler.Load_Message_New(Username, toUser));
							isGettingMessage = false;
						}
						else if(type.equals(Constant.res_create_group)) {
							String[] result = res_hand.Res_Create_Group_Success(jo);
							Set_ID_New_Group(result);
						}
						else if(type.equals(Constant.res_get_message_group_count)) {
							int num_message_in_group = res_hand.Res_Message_Count_In_Group(jo);
							int num_message_in_DB = res_hand.Get_Num_Message_In_Group(toUser);
							if(num_message_in_DB<num_message_in_group) {
								Get_Some_Message(num_message_in_group, num_message_in_DB, true);
							}
							else {
								create_notification("Full Message !", "You have already loaded enough messages for this group !");
							}
						}
						else if(type.equals(Constant.res_get_messages_group)) {
							int result = res_hand.Res_Get_Message_Group(jo);
							if(result!=-1) {
								System.out.println("Get Messages Successfully For "+Username+" From "+toUser);
							}
							SetListChat(responsehandler.Load_Message_Group(toUser));
						}

						else if(type.equals(Constant.res_success)) {
							
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
        			
                	
                }
            });
        }

    }
    
   
    class SocketNotificationThread extends Thread {
    	UiController controller;
        //ResponseHandler res_hand = new ResponseHandler();
        BufferedReader isthread = is.get(2);
    	public SocketNotificationThread(UiController controller) {
			this.controller = controller;
		}
    	
    	@Override
    	public void run() {
            
            
            try {
            	String responseLine;
            	System.out.println("I am wating ... (8002)");
            	while ((responseLine = this.isthread.readLine()) != null) {
            		System.out.println("8002:"+responseLine);
            		OnNotification(this.controller, responseLine);
            	}
            	System.out.println("terminate !");
            	create_notification("Attention", "Terminate the server connection !");
             	} catch (IOException e) {
                e.printStackTrace();
            }
           
        }
    	public void OnNotification(UiController cont, String line) {
             javafx.application.Platform.runLater(new Runnable() {
                 @Override
                 public void run() {

                	JSONObject jo;
					try {
						jo = (JSONObject) new JSONParser().parse(line);
						String type = (String) jo.get("type");
						if(type.equals(Constant.res_message_notification)) {
							Message mess = responsehandler.Res_New_Message(jo);
							if(!mess.from.equals(toUser)) create_notification(Constant.create_title_new_message(mess.from),mess.data);
							else cont.RenderMessage(mess.ToFrom,responsehandler.convert_UTC_to_Local(mess.timestamp),mess.data);
		                 	reqhan.create_new_conversation(false, get_list_User(mess.from));
		                 	responsehandler.Save_Message_New(Username,mess);
		                 	Mark_Message_Seen(mess.from, false);
						}
						else if(type.equals(Constant.res_msg_account_data)) {
							List<ColoredText> listGroup = responsehandler.Res_New_Message_Notification(jo);
							Create_Groups(listGroup);
							ReceiveDataAfterLogin = true;
							new_message_num = responsehandler.result_new_nofication;
							SetNewMessageNotification();
							create_Notification_for_new_Group();
							BindingTwoScrollBar();
						}
						else if(type.equals(Constant.res_notification_new_group)) {
							ColoredText colortxt = responsehandler.Res_Notification_New_Group(jo);
							Create_Group(colortxt);
							create_notification("Group Chat !", "You have just been invited to the group: "+colortxt.getText());
						}
						else if(type.equals(Constant.res_group_notification)) {
							Message mess = responsehandler.Res_Group_Message_Notification(jo);
							if(mess.to.split(":")[0].equals(toUser)) {
								RenderMessage(1, responsehandler.convert_UTC_to_Local(mess.timestamp), mess.data);
							}
							else {
								create_notification("Group Chat !", "You Just Got A New Message In The Group: "+mess.to.split(":")[1]);
							}
							Mark_Message_Seen(mess.to.split(":")[0], true);
						}
						else if(type.equals(Constant.res_mark_messages_seen)) {
							String ID = responsehandler.Res_Mark_Seen_Message(jo,false);
							Set_Seen_Conversation(ID);
						}
						else if(type.equals(Constant.res_mark_group_messages_seen)) {
							String ID = responsehandler.Res_Mark_Seen_Message(jo,true);
							Set_Seen_Conversation(ID);
						}
						else if(type.equals(Constant.res_notification_added_to_group)) {
							ColoredText group = responsehandler.Res_Notification_Added_To_Group(jo);
							Create_Group(group);
						}
						else if(type.equals(Constant.res_notification_new_paticipant)) {
							ColoredText group = responsehandler.Res_Notification_New_Participant(jo);
							Add_New_User_To_Group_GUI_ID(group);
						}
						else if(type.equals(Constant.res_notification_leave_group)) {
							AccountInformation accountInformation = responsehandler.Res_Leave_Group(jo);
							removeGroup(accountInformation.full_name, false, accountInformation.account_name);
						}
						else if(type.equals(Constant.res_call_notification)) {
							Call call = responsehandler.Res_New_Call(jo);
							create_notification(Constant.create_title_new_call("New Call"),"");
						}
					
						
					
					} catch (ParseException e) {
						e.printStackTrace();
					}
                   
                 }
             });
         }
    	
    }
    
    class SocketUserPresence extends Thread {
    	UiController controller ;
        BufferedReader isthread = is.get(1);
    	public SocketUserPresence(UiController controller) {
			this.controller = controller;
		}
    	@Override
    	public void run() {

	    	try {
	        	String responseLine ;
	        	System.out.println("I am wating ... (8001)");
	    		while ((responseLine = this.isthread.readLine()) != null) {
	    			System.out.println("8001:"+responseLine);
	    			OnUserPresence(this.controller, responseLine);
	    		}
				System.out.println("terminate !");
				create_notification("Attention", "Terminate the server connection !");
				} catch (IOException e) {
					e.printStackTrace();
			}
        }
    	
    	public void OnUserPresence(UiController cont, String line) {
    		javafx.application.Platform.runLater(new Runnable() {
				
				@Override
				public void run() {

					JSONObject jo;
					try {
						jo = (JSONObject) new JSONParser().parse(line);
						String type = (String) jo.get("type");
						if (type.equals(Constant.res_presence)) {
							ResponsePresence responsePresence = responsehandler.Res_Presence(jo);
							ReceiveListOnlineUser(responsePresence.list_online, responsePresence.list_offline);
							if(ReceiveDataAfterLogin) {
								Create_Groups(responsehandler.Load_Group_Infor_From_DB());
							}
							if(Get_index_user_by_ID(toUser)!=-1) {
								listOnlineUser.getSelectionModel().select(Get_index_user_by_ID(toUser));
							}
							 
						}
					}
					catch(ParseException e) {
						e.printStackTrace();
					}
					
					
				}
			});
    		
    	}
    	
    }
    
    public static List<String> get_list_User(String User) {
    	List<String> list = new ArrayList<String>();
    	list.add(User);
    	return list;
    }
    
    public List<String> get_list_User_from_notification(List<NewMessageNotification> list_new) {
    	List<String> list = new ArrayList<String>();
    	list_new.forEach(action -> {
    		list.add(action.account);
    	});
    	return list;
    }
    
    public void BindingTwoScrollBar() {
    	 ScrollBar scrollBarListOnline = (ScrollBar) listOnlineUser.lookup(".scroll-bar:vertical");
         ScrollBar scrollBarListNumNewMessage = (ScrollBar) listNumNewMessage.lookup(".scroll-bar:vertical");
         scrollBarListOnline.valueProperty().bindBidirectional(scrollBarListNumNewMessage.valueProperty());
    }
    
    public void clickToChoice(MouseEvent event) {
    	listOnlineUser.getSelectionModel().select(0);
    }
    
    public void customizeListOnlineUser() {
        listOnlineUser.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ColoredText>() {

            @Override
            public void changed(ObservableValue<? extends ColoredText> observable, ColoredText oldValue, ColoredText newValue) {
            	
            	try {
                	System.out.println("New value: "+newValue.toString());
                	if(!newValue.getID().equals(toUser)) {
                		if(!vboxChat.getChildren().isEmpty()) vboxChat.getChildren().clear();
                		toUser = newValue.getID();
                		lblToUser.setText(newValue.getText());
                		if(!newValue.getisGroup()) {
                			lblToUserStatus.setText(Constant.create_status(newValue.getOnlineStatus()));
                			reqhan.create_new_conversation(false, get_list_User(toUser));
                    		ResponseHandler.Num_Got_Message = 0;
                    		List<Message> list_messages = get_List_Message_From_ID(toUser,false);
                    		if(list_messages.size()>0) {
                    			list_messages.forEach(action -> {
                        			responsehandler.Save_Message_New(Username, action);
                        			System.out.println("list_dual");
                        		});
                    			scrollChat.vvalueProperty().bind(vboxChat.heightProperty());
                        		SetListChat(responsehandler.Load_Message_New(Username, toUser));
                    		}
                    		else {
                        		if(responsehandler.Count_Message_Now_In_Conversation(toUser) == 0 && !isGettingMessage) {
                        			GetMessagesCount(false);
                        			isGettingMessage = true;
                        		}
                        		else {
                        			scrollChat.vvalueProperty().bind(vboxChat.heightProperty());
                            		SetListChat(responsehandler.Load_Message_New(Username, toUser));
                        		}
                    		}
                    		
                		}
                		else {
                			ResponseHandler.Num_Got_Message_Group = 0;
                			lblToUserStatus.setText("Group Chat");
                			
                			List<Message> list_messages = get_List_Message_From_ID(toUser,true);
                			
                			System.out.println("ID: "+newValue.getparticipants().toString());
                			
                			
                			if(list_messages.size()>0) {
                				list_messages.forEach(action ->{
                    				responsehandler.Save_Message_Group(toUser, action);
                    				System.out.println("list_group");
                    			});
                        		scrollChat.vvalueProperty().bind(vboxChat.heightProperty());
                        		SetListChat(responsehandler.Load_Message_Group(toUser));
                			}
                			else {
                    			if(responsehandler.Get_Num_Message_In_Group(toUser) == 0 && !isGettingMessage) {
                    				GetMessagesCount(true);
                    			}
                    			else {
                            		scrollChat.vvalueProperty().bind(vboxChat.heightProperty());
                            		SetListChat(responsehandler.Load_Message_Group(toUser));
                    			}
                			}
                		
                		}
                		System.out.println("ID: "+newValue.getID());
                		if(Get_Num_Message_By_ID(toUser)>0) {
                			Mark_Message_Seen(toUser,newValue.getisGroup());
                		}
                	}
            	}
            	catch (NullPointerException e) {
            		System.out.println("Null New User");
            	}
            	
            }
        });
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Get_Connection();
        SocketMessageThread socketMessageThread = new SocketMessageThread(this);
        SocketNotificationThread socketNotificationThread = new SocketNotificationThread(this);
        SocketUserPresence socketUserPresence = new SocketUserPresence(this);
        socketMessageThread.start();
        socketNotificationThread.start();
        socketUserPresence.start();
        scrollChat.setVmax(2);
        
        listOnlineUser.setCellFactory(lv -> new ListCell<ColoredText>() {
        	@Override
        	protected void updateItem(ColoredText item, boolean empty) {
        		super.updateItem(item, empty);
                if (item == null) {
                    setText(null);
                    setTextFill(null);
                } else {
                    setText(item.getText());
                    setTextFill(item.getColor());
                }
        	}
        	
        });
        
        listNumNewMessage.setCellFactory(lv -> new ListCell<ColoredText>() {
        	@Override
        	protected void updateItem(ColoredText item, boolean empty) {
        		super.updateItem(item, empty);
                if (item == null) {
                    setText(null);
                    setTextFill(null);
                } else {
                    setText(item.getText());
                    setTextFill(item.getColor());
                }
        	}
        });
       
        customizeListOnlineUser();

        
        scrollChat.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override public void handle(ScrollEvent event) {
                if(scrollChat.vvalueProperty().isBound()) {
                	scrollChat.vvalueProperty().unbind();
                }
                event.consume();
                if(scrollChat.getVvalue() == 0){
                	 if(!listOnlineUser.getSelectionModel().getSelectedItem().getisGroup()) {
                     	int term = responsehandler.Count_Message_Now_In_Conversation(toUser);
             	        if(vboxChat.getChildren().size()<term) {
	             	        SetListChat(responsehandler.Load_Message_New(Username, toUser));
	             	        System.out.println("now get the dual chat in DB");
             	        }
             	        else {
	             	        GetMessagesCount(false);
	             	        System.out.println("now get the dual chat from Server");
             	        }
             	         
                     }
                     else {
                     	
                     	int term = responsehandler.Get_Num_Message_In_Group(toUser);
                     	  if(vboxChat.getChildren().size()<term) {
           	            	SetListChat(responsehandler.Load_Message_Group(toUser));
           	            	System.out.println("now get the group chat in DB");
           	             }
           	             else {
           	            	GetMessagesCount(true);
           	            	System.out.println("now get the group chat from Server");
           	             }
                     }
                	 scrollChat.setVvalue(1.5);
                }
              }
        });
        
        
        
//        scrollChat.vvalueProperty().addListener(new ChangeListener<Number>() 
//        {
//          @Override
//          public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
//        	  System.out.println("now get the chat history1");
//        	  if(newValue.doubleValue() == 0){
//	            System.out.println("now get the chat history");
//	            int term = responsehandler.Count_Message_Now_In_Conversation(toUser);
//	            if(vboxChat.getChildren().size()<term) {
//	            	SetListChat(responsehandler.Load_Message_New(Username, toUser));
//	            }
//	            else {
//	            	Get_Messsage_Res();
//	            }
//	          }
//          }
//        });

        makeStageDrageable();
        

    }
}
