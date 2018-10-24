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
    private void ClickToChooseFile(MouseEvent e) {
    	Choose_File();
    }

    @FXML
    private void minimize(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }
 
    
    
    
    // SET UP CONNECTION
    String Username="a";
    String toUser = "";
    String serverHost = "35.240.252.106";
    int[] port = {8000,8001,8002,8003};
    List<String> list_User = null;
    int num_port  = 4;
    List<Socket> socketOfClient = new ArrayList<Socket>();
    List<BufferedReader> is =  new ArrayList<BufferedReader>();
    List<BufferedWriter> os =  new ArrayList<BufferedWriter>();

    
    
    // Desktop (File)
    private Desktop desktop = Desktop.getDesktop();
    private FileChooser fileChooser = new FileChooser();
    
    
    // Control
    public void setDisableOfCreateGroup(Boolean val) {
    	btnCreateGroup.setDisable(val);
    }

    
    
    
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
    
    public void create_group_from_gui(List<String> list_account, String namegroup) {
    	
    	UUID uid = UUID.randomUUID();
    	JSONObject jo =  reqhan.Req_create_group_Chat(list_account,uid.toString());
    	Create_Group(namegroup, uid.toString());
    	System.out.println(jo.toJSONString());
//    	try {
//			os.get(0).write(jo.toString());
//			os.get(0).newLine();
//		    os.get(0).flush();
//		    	
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
       
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
    		if(!groupcont.get_name_group().equals("")) create_group_from_gui(groupcont.getlist_user_after_check(),groupcont.get_name_group());
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
    	listOnlineUser.getItems().add(new ColoredText(namegroup,Color.BLUE, true, uuid));
    	listNumNewMessage.getItems().add(new ColoredText(String.valueOf(0),Color.BLUE, true, uuid));
    	
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
    
    
    
    // File control
    private void Choose_File() {
    	File file  = fileChooser.showOpenDialog(btn1.getScene().getWindow());
    	if(file !=null) {
    		this.openFile(file);
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
    

    
    // port 8000
    public void EnterToSendMessage(KeyEvent event) {
        if(event.getCode()==KeyCode.ENTER) {
        	if(txtContMess.getText().length()!=0) {
        		scrollChat.vvalueProperty().bind(vboxChat.heightProperty());
        		SendMessage(toUser,txtContMess.getText());
        		txtContMess.setText("");
        		
        	}
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
    
    public void GetMessagesCount() {
    	try {
        	JSONObject jo = reqhan.Req_Get_Messages_Count(toUser);
        	os.get(0).write(jo.toString());
            os.get(0).newLine();
            os.get(0).flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void Get_Some_Message(int lastmessage, int num_now_message) {
    	try {
    		JSONObject jo =  reqhan.Req_Get_Messages(toUser, lastmessage,num_now_message );
    		System.out.println(jo.toJSONString());
        	os.get(0).write(jo.toString());
            os.get(0).newLine();
            os.get(0).flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
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
    
    // port 8001
    
    public void ReceiceListOnlineUser( List<AccountInformation> list_online,  List<AccountInformation> list_offline) {
    	this.list_online = list_online;
    	this.list_offline = list_offline;
    	listOnlineUser.getItems().clear();
    	for(int i = 0;i<list_online.size();++i) {
    		listOnlineUser.getItems().add(new ColoredText(list_online.get(i).display_name,Color.GREEN,false,list_online.get(i).account_name));
    	}
    	for(int i = 0;i<list_offline.size();++i) {
    		listOnlineUser.getItems().add(new ColoredText(list_offline.get(i).display_name,Color.BLACK,false,list_offline.get(i).account_name));
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
    
    private void SetNewMessageNotification() {
    	if(new_message_num != null) {
    		listNumNewMessage.getItems().clear();
    		List<ColoredText> list = listOnlineUser.getItems();
    		list.forEach(action -> {
    			int term = 0;
    			for(int i = 0;i<new_message_num.size();++i) {
    				if(action.getID().equals(new_message_num.get(i).account)) {
    					listNumNewMessage.getItems().add(new ColoredText(String.valueOf(new_message_num.get(i).num_message),Color.RED));
    					term = 1;
    					break;
    				}
    			}
    			if(term == 0) listNumNewMessage.getItems().add(new ColoredText(String.valueOf(0),Color.RED));
    		});
    		
    	}
    }
    
    
    // all port
    
    public void SetUser() {
        this.Username = txtUser.getText();
        check_account(Username);
        btnSetUser.setDisable(true);
        ResponseHandler.NowUser = this.Username;
        RequestHandler.NowUser = this.Username;
        reqhan.create_db_for_new_User();
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void OnMessage(UiController cont, String line) {
            javafx.application.Platform.runLater(new Runnable() {
                @Override
                public void run() {
                	if (res_hand.Res_count_of_conversation(line,toUser)!=-1) {
                		ResponseHandler.Num_Message_Now_Conversation = res_hand.Res_count_of_conversation(line,toUser);
                		Get_Some_Message(ResponseHandler.Num_Message_Now_Conversation,responsehandler.Count_Message_Now_In_Conversation(toUser));
                	}
                	else if(res_hand.Res_get_messages(line, toUser)!=-1) {
                		System.out.println("Get Messages Successfully For "+Username+" From "+toUser);
                		SetListChat(responsehandler.Load_Message_New(Username, toUser));
                	}
                	else if(res_hand.Res_Send_Message(line).equals(Constant.res_success)) {

                	}
                }
            });
        }

    }
    
   
    class SocketNotificationThread extends Thread {
    	UiController controller;
        ResponseHandler res_hand = new ResponseHandler();
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
             	} catch (IOException e) {
                e.printStackTrace();
            }
           
        }
    	public void OnNotification(UiController cont, String line) {
             javafx.application.Platform.runLater(new Runnable() {
                 @Override
                 public void run() {
                    if(!res_hand.Res_New_Message(line).data.isEmpty()) {
                       Message mess = res_hand.Res_New_Message(line);
                 	   if(!mess.from.equals(toUser)) create_notification(Constant.create_title_new_message(mess.from),mess.data);
      
                 	   else  cont.RenderMessage(mess.ToFrom,mess.timestamp,mess.data);
                 	    
                 	   reqhan.create_new_conversation(false, get_list_User(mess.from));
                 	   res_hand.Save_Message_New(Username,mess);
                 	}
                    else if(!res_hand.Res_New_Message_Notification(line).get(0).account.equals("")) {
                    	new_message_num = res_hand.result_new_nofication;
                    	new_message_num.remove(0);
                    	SetNewMessageNotification();

                    	
                    }
                    else if(!res_hand.Res_Error(line).content.isEmpty()) {
                    	ErrorRespond  err = res_hand.Res_Error(line);
                    	create_notification(Constant.create_title_error(err.error_code), err.content);
                    	
                    }
                    else {
                    	//System.out.println("Test 4");
                    }
                 }
             });
         }
    	
    }
    
    class SocketUserPresence extends Thread {
    	UiController controller ;
        ResponseHandler res_hand = new ResponseHandler();
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
				} catch (IOException e) {
					e.printStackTrace();
			}
        }
    	
    	public void OnUserPresence(UiController cont, String line) {
    		javafx.application.Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					if(!res_hand.Res_Presence(line).type.equals("")) {
						ResponsePresence responsePresence = res_hand.Res_Presence(line);
						ReceiceListOnlineUser(responsePresence.list_online, responsePresence.list_offline);
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
    
    public void Get_Messsage_Res() {
    	GetMessagesCount();
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
        
        listOnlineUser.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ColoredText>() {

            @Override
            public void changed(ObservableValue<? extends ColoredText> observable, ColoredText oldValue, ColoredText newValue) {
            	
            	int index = listOnlineUser.getSelectionModel().getSelectedIndex();
            	if(index!=-1) {
            		if(!vboxChat.getChildren().isEmpty()) vboxChat.getChildren().clear();
            		
            		toUser = newValue.getID();
            		lblToUser.setText(newValue.getText());
            		if(newValue.getColor() == Color.GREEN) lblToUserStatus.setText(Constant.create_status(""));
                	else lblToUserStatus.setText(Constant.create_status("12h"));
            		reqhan.create_new_conversation(false, get_list_User(toUser));
            		responsehandler.Num_Got_Message = 0;
            		if(responsehandler.Count_Message_Now_In_Conversation(toUser) == 0) {
                    	Get_Messsage_Res();
                	}
                	else {
                		scrollChat.vvalueProperty().bind(vboxChat.heightProperty());
                		SetListChat(responsehandler.Load_Message_New(Username, toUser));
                	}
            	}
            	
            }
        });
        
        scrollChat.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override public void handle(ScrollEvent event) {
                if(scrollChat.vvalueProperty().isBound()) {
                	scrollChat.vvalueProperty().unbind();
                }
                event.consume();
                if(scrollChat.getVvalue() == 0){
    	            
    	            int term = responsehandler.Count_Message_Now_In_Conversation(toUser);
    	            if(vboxChat.getChildren().size()<term) {
    	            	SetListChat(responsehandler.Load_Message_New(Username, toUser));
    	            	System.out.println("now get the chat in DB");
    	            }
    	            else {
    	            	Get_Messsage_Res();
    	            	System.out.println("now get the chat from Server");
    	            }
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
        
        

        
        

    	   
        
//        listOnlineUser.getItems().add(new ColoredText("huytc", Color.RED));
//        listOnlineUser.getItems().add(new ColoredText("anhnq", Color.BLUE));
//        listOnlineUser.getItems().add(new ColoredText("user3", Color.YELLOW));
        makeStageDrageable();
        

    }
}
