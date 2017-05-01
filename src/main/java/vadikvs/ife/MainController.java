package vadikvs.ife;


import com.vadikvs.Signalslots.Signal;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class MainController implements Initializable {
    public Signal close=new Signal();
    @FXML
    private Button closeButton;
    @FXML
    private Button settingsButton;
   
    
    @FXML
    private void onCloseButton(ActionEvent event) {
         close.emit();
         Stage stage = (Stage) closeButton.getScene().getWindow();
         stage.close();
    }
    
    @FXML
    private void onSettingsButton(){
       try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/Setting.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialog = new Stage();
            Stage stage = (Stage) closeButton.getScene().getWindow();;
            dialog.setTitle("Настройки");
            dialog.initOwner(stage);
            Scene scene = new Scene(page);
            dialog.setScene(scene);
            SettingController controller = loader.getController();
            controller.setStage(dialog);
            close.connect(controller.close);
            dialog.showAndWait();       
        } catch (IOException e) {
            e.printStackTrace();
           
        } 
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
    } 
  
}
