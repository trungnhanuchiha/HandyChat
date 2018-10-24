package application;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.util.Base64;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginController  implements Initializable {
    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private AnchorPane parent;
    @FXML
    private TextField inputUsername;
    @FXML
    private TextField inputPassword;
    @FXML
    private JFXButton btnLogin, btnCancel;


    @FXML
    private void close(MouseEvent event) {

        System.exit(0);
    }

    String url = "http://handychat.gameworld.vn:8003/api/login";


    private void sendGet() throws Exception {
        String usnameInp = inputUsername.getText();
        String pwInp = inputPassword.getText();
        String encoding = Base64.getEncoder().encodeToString((usnameInp+":"+pwInp).getBytes("UTF-8"));

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("Authorization", "Basic " + encoding);
        con.setRequestProperty("password", pwInp);
        con.setDoOutput(true);


        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        System.out.println("content: " + con.getHeaderFieldKey(0) );

        InputStream content = (InputStream)con.getInputStream();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(content));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }

    public  void logIn(ActionEvent event){
        try {
            sendGet();
            Parent root = FXMLLoader.load(getClass().getResource("Ui.fxml"));
            Stage stage_chat = new Stage();
            stage_chat.setScene(new Scene(root));
            stage_chat.initStyle(StageStyle.UNDECORATED);
            Stage stage_login = Main.stage;
            stage_login.hide();
            stage_chat.show();
            stage_chat.setOnHidden((WindowEvent event1) ->{
            	stage_login.show();
        	});
            
        } catch (Exception e) {
            //e.printStackTrace();
        	UiController.create_notification("Error When Login","Wrong Account Or Password !");
        	
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        makeStageDrageable();


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
}
