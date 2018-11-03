package application;
/*
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SignupController implements Initializable {
    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private AnchorPane parent;
    @FXML
    private TextField inputUsername;
    @FXML
    private TextField inputPassword;
    @FXML
    private TextField inputRePassword;
    @FXML
    private TextField inputEmail;
    @FXML
    private TextField inputFullName;
    @FXML
    private JFXButton btnSignUp, cancel;
    @FXML
    private void close(MouseEvent event) {

        System.exit(0);
    }

    public static String signup(String displayName, String password, String account, String fullName,
                                String email, String gender) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost("http://handychat.gameworld.vn:8003/api/users");

        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("display_name", displayName));
        pairs.add(new BasicNameValuePair("password", password));
        pairs.add(new BasicNameValuePair("account", account));
        pairs.add(new BasicNameValuePair("email", email));
        pairs.add(new BasicNameValuePair("gender", gender));
        pairs.add(new BasicNameValuePair("full_name", fullName));
        //pairs.add(new BasicNameValuePair("date_of_birth", dob.toString()));

        request.setEntity(new UrlEncodedFormEntity(pairs));
        HttpResponse resp = client.execute(request);

        return EntityUtils.toString(resp.getEntity());
    }

    public  void SignUp(ActionEvent event){
        String usnameInp = inputUsername.getText();
        String pwInp = inputPassword.getText();
        String repwInp = inputRePassword.getText();
        String emInp = inputEmail.getText();
        String fnInp = inputFullName.getText();
        
        System.out.println("inputUsername: "+usnameInp);
        System.out.println("inputPassword: "+pwInp);
        System.out.println("inputRePassword: "+repwInp);
        System.out.println("inputEmail: "+emInp);
        System.out.println("inputFullName: "+fnInp);
        
        

        if (!pwInp.equals(repwInp)  || usnameInp.equals("") || pwInp.equals("") || repwInp.equals("") || emInp.equals("") || fnInp.equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR password");
            alert.setHeaderText("Password mismatch");
            alert.setContentText("");
            alert.show();
        }

        try {
            signup(usnameInp, pwInp, usnameInp, fnInp, emInp,"");
        } catch (Exception e) {
            e.printStackTrace();
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
*/