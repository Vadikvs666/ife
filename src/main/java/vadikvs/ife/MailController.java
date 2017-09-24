/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vadikvs.ife;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import vadikvs.ife.Email;

/**
 * FXML Controller class
 *
 * @author vadim
 */
public class MailController implements Initializable {
    private Stage stage;
    @FXML
    private Button exitButton;
    @FXML
    private Button sendButton;
    @FXML
    private TableView mailTableView;
    @FXML
    private ListView fileListView;
    
    private Settings settings;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        settings=new Settings();
        TableColumn senderNameCol = new TableColumn("Отправитель");
        TableColumn temeNameCol = new TableColumn("Тема");
        TableColumn dateNameCol = new TableColumn("Дата");   
        mailTableView.getColumns().addAll(senderNameCol, temeNameCol, dateNameCol);
        
        String protocol = settings.getProtocolMail();
        String host = settings.getServerMail();
        String port = settings.getPortMail();
 
 
        String userName = settings.getUserMail();
        String password = settings.getUserPassword();
        String count = settings.getCountMail();
        Email receiver = new Email();
        receiver.getMessages(protocol, host, port, userName, password,Integer.valueOf(count));
    }    
    
    public void setStage(Stage stage){
        this.stage=stage;
    }
    
    @FXML
    public void onExitButton(){
        stage.close();
    }
    @FXML
    public void onMailSelect(){
        //
    }
    @FXML
    public void onFileSelect(){
       //
    }
    @FXML
    public void onSendButton(){
       //
    }
}
