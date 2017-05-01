package vadikvs.ife;


import com.vadikvs.Signalslots.Signal;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;


public class MainController implements Initializable {
    public Signal close=new Signal();
    @FXML
    private Button closeButton;
   
    
    @FXML
    private void onCloseButton(ActionEvent event) {
         close.emit();
         Stage stage = (Stage) closeButton.getScene().getWindow();
         stage.close();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
    } 
  
}
