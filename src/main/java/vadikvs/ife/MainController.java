package vadikvs.ife;

import com.vadikvs.Signalslots.Signal;
import com.vadikvs.Signalslots.Slot;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javax.mail.Message;
import javax.mail.MessagingException;

public class MainController implements Initializable {

    private ObservableList<FirmEntity> firmData = FXCollections.observableArrayList();
    public Signal close = new Signal();
    public Signal firmChanged = new Signal();
    public Slot closeAction = new Slot(this, "onCloseButton");
    private DataAccessor DA;
    @FXML
    private Button closeButton;
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
    private TextField searchText;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void onCloseButton() {
        close.emit();
        FirmEntity entity = getCurrentFirm();
        Object[] args = {entity};
        firmChanged.emit(args);
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
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

        try {
            new Thread(() -> {
                getAllFrimFromDB();
                onFirmSelect();
            }).start();
        } catch (Exception ex) {
            Settings settings = new Settings();
            SettingsFormGenerator form = new SettingsFormGenerator(settings);
            form.show();
        }

    }

    private void getAllFrimFromDB() {
        Settings settings = new Settings();
        String server = settings.getValue("server");
        String user = settings.getValue("user");
        String db = settings.getValue("database");
        String port = settings.getValue("port");
        String password = settings.getValue("password");
        String conString = "jdbc:mysql://";
        conString += server+":"+port;
        conString += "/";
        conString += db;
        this.DA = new DataAccessor("com.mysql.jdbc.Driver", conString, user, password);
        List<FirmEntity> list = DA.getFirmList();
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        firmData = FXCollections.observableArrayList(list);
        firmTableView.setItems(firmData);
        firmTableView.getSelectionModel().select(0);
    }

    private void getFirmFromDBLikeName(String name) {
        Settings settings = new Settings();
        String server = settings.getValue("server");
        String user = settings.getValue("user");
        String db = settings.getValue("database");
        String port = settings.getValue("port");
        String password = settings.getValue("password");
        String conString = "jdbc:mysql://";
        conString += server+":"+port;
        conString += "/";
        conString += db;
        this.DA = new DataAccessor("com.mysql.jdbc.Driver", conString, user, password);
        List<FirmEntity> list = DA.getFirmListLikeName(name);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        firmData = FXCollections.observableArrayList(list);
        firmTableView.setItems(firmData);
        firmTableView.getSelectionModel().select(0);
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

    private FirmEntity getCurrentFirm() {
        return firmTableView.getSelectionModel().selectedItemProperty().getValue();
    }

    @FXML
    public void onSearch(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            getFirmFromDBLikeName(searchText.textProperty().get().toLowerCase());
        }
    }

    private void initFilter() {
        searchText.textProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (searchText.textProperty().get().isEmpty()) {
                    getAllFrimFromDB();
                    return;
                } else {
                    getFirmFromDBLikeName(searchText.textProperty().get().toLowerCase());
                }

            }
        });

    }
}
