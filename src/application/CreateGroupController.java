package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


import com.jfoenix.controls.JFXButton;

import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class CreateGroupController implements Initializable {

	@FXML
	private JFXButton btnOK;
	
	@FXML
	private VBox vboxOflistCheckbox ;
	
	@FXML
	private TextField txtGroupName;
	

	private List<CheckBox> list_checkbox = new ArrayList<CheckBox>();
	private List<String> list_account_after_check = new ArrayList<String>();
	private List<String> list_name_after_check = new ArrayList<String>();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	List<ColoredText> list_User = new ArrayList<ColoredText>();
	String temp;
	
	public void setUiControler(JFXButton ui_cont) {
		
	}
	
	public void setListOnlineOffline(List<ColoredText> list_User) {
		this.list_User = list_User;
	}
	
	public void create_list_user() {
		list_User.add(new ColoredText("ty",Color.GREEN));
		list_User.add(new ColoredText("teo",Color.GREEN));
		list_User.add(new ColoredText("tun",Color.GREEN));

	}
	
	public void create_listcheckbox() {
		for(int i = 0;i<this.list_User.size();++i) {
			CheckBox chb = new CheckBox(this.list_User.get(i).getText());
			chb.setStyle("-fx-text-fill: white;-fx-highlight-fill: white;");
			list_checkbox.add(chb);
		}
		vboxOflistCheckbox.getChildren().addAll(list_checkbox);
		vboxOflistCheckbox.setSpacing(3);
	}
	
	public void btnOKhandler(MouseEvent e) {
		if(txtGroupName.getText().equals("")) {
			throw_alert_error("Please type the name of group !");
		}
		else {
			get_List_CheckBox();
			
		}
	}
	
	private void get_List_CheckBox() {
		for(int i = 0 ;i<list_checkbox.size();++i) {
			if(list_checkbox.get(i).isSelected()) {
				list_account_after_check.add(this.list_User.get(i).getID());
				list_name_after_check.add(this.list_User.get(i).getText());
			}
		}
		
		list_account_after_check.add(ResponseHandler.NowUser);
		list_name_after_check.add(ResponseHandler.NowUserName);

		if(list_account_after_check.size()>1) {
			close_windows();
		}
		else {
			throw_alert_error("Please choose member of group !");
		}
	}
	
	public String get_name_group() {
		return txtGroupName.getText();
	}
	
	public void close_windows() {
		Stage stage = (Stage) btnOK.getScene().getWindow();
		stage.close();
	}
	
	public void throw_alert_error(String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error Dialog");
		alert.setHeaderText("Look, an Error Dialog");
		alert.setContentText(content);
		alert.showAndWait();
	}
	
	// getter
	public List<String> getlist_user_after_check() {
		return this.list_account_after_check;
	}
	public List<String> getlist_name_after_check(){
		return this.list_name_after_check;
	}

}
