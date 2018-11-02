package application;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class GroupInforController implements Initializable{
	@FXML 
	private Label lbl_GroupName;
	
	@FXML
	private Label lbl_GroupCreateDate;
	
	@FXML
	private ListView<String> list_Participants;
	
	@FXML 
	ScrollPane scrollUser;
	
	@FXML
	private VBox vboxOflistCheckbox ;
	
	List<ColoredText> list_User = new ArrayList<ColoredText>();
	List<String> list_account ;
	List<CheckBox> list_checkbox = new ArrayList<CheckBox>();
	List<AccountInformation> list_Other_User;
	List<AccountInformation> list_Other_User_Result = new ArrayList<AccountInformation>();
	String toUser;
	int num_user_to_add = 0;
	boolean isExit = false;

	
	public void ClickToAddUser(MouseEvent event) {
		ClickToAddUserHandler();
	}
	
	public void ClickToLeaveGroup(MouseEvent event) {
		ClickToLeaveGroupHander();
	}
	
	private void ClickToAddUserHandler() {
		
		if(!vboxOflistCheckbox.getChildren().isEmpty()) {
			vboxOflistCheckbox.getChildren().clear();
		}
		create_listcheckbox();
	}
	
	public void close_windows() {
		Stage stage = (Stage) lbl_GroupName.getScene().getWindow();
		stage.close();
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	public void Set_Group_Name(String name) {
		lbl_GroupName.setText(name);
	}
	
	public void Set_Group_Date(String Date) {
		lbl_GroupCreateDate.setText(Date);
	}
	
	public void Set_List_User(List<String> Users) {
		for(String term : Users) {
			list_Participants.getItems().add(term);
		}
	}
	
	public void setListOnlineOffline(List<ColoredText> list_User) {
		this.list_User = list_User;
		this.num_user_to_add = list_User.size();
	}
	
	public void setListAccount(List<String> list_account) {
		this.list_account = list_account;
	}
	
	public void SetToUser(String toUser) {
		this.toUser = toUser;
	}
	
	public boolean getisExit() {
		return isExit;
	}
	
	private List<AccountInformation> getListUser() {
		List<AccountInformation> result = new ArrayList<AccountInformation>();
		List<Integer> dumplicate_idx = new ArrayList<Integer>();
		int j = 0;
		for (ColoredText big_list : this.list_User) {
			int i = 0;
			for (String account : this.list_account) {
				if(big_list.getID().equals(account)) {
					dumplicate_idx.add(j);
					System.out.println(account);
					break;
				}
				else {
					i++;
				}
			}
			if(i == this.list_account.size()) {
				
				result.add(new AccountInformation(big_list.getID(), big_list.getText(), big_list.getText()));
			}
			j++;
		}
		
		if(dumplicate_idx.size()>0) {
			Collections.sort(dumplicate_idx);
			int t = 0;
			for(int i : dumplicate_idx) {
				System.out.println("i: "+i+", t: "+t);
				list_User.remove(i-t);
				t++;
			}
		}
		
		return result;
	}
	
	public void create_listcheckbox() {
		if(list_checkbox.size()>0) list_checkbox.clear();
		list_Other_User = getListUser();
		if(list_Other_User.size()>0) {
			for(int i = 0;i<list_Other_User.size();++i) {
				CheckBox chb = new CheckBox(list_Other_User.get(i).display_name);
				//chb.setStyle("-fx-text-fill: white;-fx-highlight-fill: white;");
				list_checkbox.add(chb);
			}
			vboxOflistCheckbox.getChildren().addAll(list_checkbox);
			vboxOflistCheckbox.setSpacing(3);
			Button btnOK = new Button();
			btnOK.setText("Add");
			btnOK.setOnMouseClicked(value-> {
				btnOKhandler();
			});
			vboxOflistCheckbox.getChildren().add(btnOK);
		}
		
	}
	
	public void btnOKhandler() {
		int i = 0;
		List<Integer> dumplicate_idx = new ArrayList<Integer>();
		for(CheckBox cb : list_checkbox) {
			if (cb.isSelected()) {
				list_Participants.getItems().add(cb.getText());
				AccountInformation acc = list_Other_User.get(i);
				list_Other_User_Result.add(new AccountInformation(acc.account_name, acc.display_name, acc.full_name));
				dumplicate_idx.add(i);
			}
			i++;
		}
		
		if(dumplicate_idx.size()>0) {
			int t = 0;
			Collections.sort(dumplicate_idx);
			for(int j : dumplicate_idx) {
				list_User.remove(j-t);
				t++;
			}
		}
		
		vboxOflistCheckbox.getChildren().clear();
		list_Other_User_Result.forEach(action -> {
			System.out.println(action.account_name+": "+action.display_name);
		});
	}
	
	public void ClickToLeaveGroupHander() {
		this.isExit = true;
		close_windows();
	}
}
