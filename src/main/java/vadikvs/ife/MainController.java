package vadikvs.ife;

import com.vadikvs.Signalslots.Signal;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController implements Initializable {

    private ObservableList<FirmEntity> firmData = FXCollections.observableArrayList();
    public Signal close = new Signal();
    public Signal firmChanged = new Signal();
    private DataAccessor DA;
    @FXML
    private Button closeButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button importButton;
    @FXML
    private Button mailButton;
    @FXML
    private Button saveParamButton;
    @FXML
    private TableView<FirmEntity> firmTableView;
    @FXML
    private TableColumn<FirmEntity, Integer> idColumn;
    @FXML
    private TableColumn<FirmEntity, String> nameColumn;
    @FXML
    private TextField artikulColTextEdit;
    @FXML
    private TextField summColTextEdit;
    @FXML
    private TextField countColTextEdit;
    @FXML
    private TextField nameColTextEdit;
    @FXML
    private TextField startRowTextEdit;
    @FXML
    private TextField numberColTextEdit;
    @FXML
    private TextField numberRowTextEdit;
    @FXML
    private TextField maxRowTextFiled;

    @FXML
    private void onCloseButton(ActionEvent event) {
        close.emit();
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onSettingsButton() {
        try {
            Settings settings = new Settings();
            SettingsFormGenerator form = new SettingsFormGenerator(settings);
            form.show();
        } catch (Exception e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);

        }
    }

    @FXML
    private void onImportButton() {
        try {
            Settings settings = new Settings();
            FileChooser chooser = new FileChooser();
            Stage stage = (Stage) closeButton.getScene().getWindow();
            File file = chooser.showOpenDialog(stage);
            List<ProductEntity> products = new ArrayList<>();
            DataExtractor DE = new DataExtractor(file, getCurrentParam());
            products.addAll(DE.getProductsFromFile(settings.getValue("tempPath")));
            RequestMaker req = new RequestMaker(products, settings.getValue("server"),
                    settings.getValue("addition"));
            BrowserLauncher bl = new BrowserLauncher();
            JsonMaker jm = new JsonMaker(products);
            String data = jm.getJson();
            Float addition = Float.parseFloat(settings.getValue("addition"));
            Ife ife = new Ife(data, getCurrentFirm().getId(), addition, "");
            DA.insertIfe(ife);
            bl.openBrowser(req.getStringWithHash(ife.getHash()),
                    settings.getValue("browser"));
        } catch (Exception e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);

        }
    }

    @FXML
    private void onMailButton() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/fxml/Mail.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialog = new Stage();
            Stage stage = (Stage) closeButton.getScene().getWindow();
            FirmEntity firm = getCurrentFirm();
            dialog.setTitle("Выбрать счета для переделки фирмы: " + firm.getName());
            dialog.initOwner(stage);
            Scene scene = new Scene(page);
            dialog.setScene(scene);
            MailController controller = loader.getController();
            controller.setStage(dialog);
            controller.setFirm(firm);
            controller.setParam(getCurrentParam());
            close.connect(controller.closeAction);
            firmChanged.connect(controller.firmChanged);
            dialog.showAndWait();
        } catch (IOException e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);

        }
    }

    @FXML
    private void onSaveParamButton() {
        Settings settings = new Settings();
        String server = settings.getValue("server");
        String user = settings.getValue("user");
        String db = settings.getValue("database");
        String password = settings.getValue("password");
        FirmEntity firm = getCurrentFirm();
        ParamsEntity entity = new ParamsEntity(startRowTextEdit.getText(), maxRowTextFiled.getText(), countColTextEdit.getText(),
                summColTextEdit.getText(), artikulColTextEdit.getText(),
                nameColTextEdit.getText(), numberColTextEdit.getText(),
                numberRowTextEdit.getText(), firm.getId());
        DA.insertParams(entity);
        saveParamButton.setText("Сохранить");
    }

    @FXML
    public void paramsChanged() {
        saveParamButton.setText("Сохранить*");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        Settings settings = new Settings();
        String server = settings.getValue("server");
        String user = settings.getValue("user");
        String db = settings.getValue("database");
        String password = settings.getValue("password");
        try {
            String conString = "jdbc:mysql://";
            conString += server;
            conString += "/";
            conString += db;
            this.DA = new DataAccessor("com.mysql.jdbc.Driver", conString, user, password);
            List<FirmEntity> list = DA.getFirmList();
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            firmData = FXCollections.observableArrayList(list);
            firmTableView.setItems(firmData);
            firmTableView.getSelectionModel().select(0);
            onFirmSelect();
        } catch (Exception ex) {
            // onSaveParamButton();
        }

    }

    public void onFirmSelect() {
        clearParamsTextFields();
        ParamsEntity params = getCurrentParam();
        startRowTextEdit.setText(params.getStart_rowString());
        numberColTextEdit.setText(params.getNumber_colString());
        numberRowTextEdit.setText(params.getNumber_rowString());
        nameColTextEdit.setText(params.getName_colString());
        countColTextEdit.setText(params.getCount_colString());
        summColTextEdit.setText(params.getSumm_colString());
        artikulColTextEdit.setText(params.getArtikul_colString());
        maxRowTextFiled.setText(params.getMax_rowString());

    }

    private void clearParamsTextFields() {
        startRowTextEdit.setText("");
        numberColTextEdit.setText("");
        numberRowTextEdit.setText("");
        nameColTextEdit.setText("");
        countColTextEdit.setText("");
        summColTextEdit.setText("");
        artikulColTextEdit.setText("");
        maxRowTextFiled.setText("");
    }

    private ParamsEntity getCurrentParam() {
        FirmEntity entity = getCurrentFirm();
        Object[] args = {entity};
        firmChanged.emit(args);
        return DA.getParamsByFirmId(entity.getId());
    }
    private FirmEntity getCurrentFirm(){
        return firmTableView.getSelectionModel().selectedItemProperty().getValue();
    }
}
