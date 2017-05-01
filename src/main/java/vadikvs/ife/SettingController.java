/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vadikvs.ife;

import com.vadikvs.Signalslots.Slot;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author vadim
 */
public class SettingController implements Initializable {
    public Slot close=new Slot(this,"onCloseButton");
    @FXML 
    private Button saveButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button closeButton;
    @FXML
    private TextField fileNameText;
    @FXML
    private TextField userText;
    @FXML
    private TextField passwordText;
    private Stage stage;
    private Settings settings;
    private Boolean changed=false;
    
    @FXML
    private void onSaveButton(){
        saveButton.setText("Сохранить");
        changed = false;
        settings.setFilePath(fileNameText.getText());
        settings.setUser(userText.getText());
        settings.setPassword(passwordText.getText());
    }
    @FXML
    protected void onResetButton(){
        settings.reset();
        setFiledsFromSettings();
    }
    @FXML
    public void onCloseButton(){
        stage.close();
    }
    
    public void setStage(Stage stage){
        this.stage=stage;
    }
    @FXML
    private void setChanged(){
        saveButton.setText("Сохранить*");
        changed=true;
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        settings=new Settings();
        setFiledsFromSettings();
   }  
   
    private void setFiledsFromSettings(){
        fileNameText.setText(settings.getFilePath());
        userText.setText(settings.getUser());
        passwordText.setText(settings.getPassword());
        
    }
    
  
    
}
