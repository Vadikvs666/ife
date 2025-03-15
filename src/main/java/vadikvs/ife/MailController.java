/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vadikvs.ife;

import com.vadikvs.Signalslots.Signal;
import com.vadikvs.Signalslots.Slot;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * FXML Controller class
 *
 * @author vadim
 */
public class MailController implements Initializable {

    public Signal close = new Signal();
    public Slot closeAction = new Slot(this, "onExitButton");
    public Slot firmChanged = new Slot(this, "onFirmChanged");
    private final ObservableList<MessageEntity> messageData = FXCollections.observableArrayList();
    private ObservableList<String> atachData = FXCollections.observableArrayList();
    private final ObservableList<AtachmentEntity> addData = FXCollections.observableArrayList();
    private Email email;
    private Stage stage;
    private FirmEntity firm;
    private ParamsEntity param;
    private DataAccessor DA;
    Integer count_messages;
    MainController firmController;

    public void setParam(ParamsEntity param) {
        this.param = param;
    }

    @FXML
    private Button exitButton;
    @FXML
    private Button sendButton;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button moreMessageButton;
    @FXML
    private Button settingsButton;
    @FXML
    private TableView<MessageEntity> mailTableView;
    @FXML
    private ListView<String> fileListView;
    @FXML
    private TableView<AtachmentEntity> addedfileListView;
    @FXML
    private TableColumn<MessageEntity, String> fromColumn;
    @FXML
    private TableColumn<MessageEntity, String> subjectColumn;
    @FXML
    private TableColumn<MessageEntity, Date> dateColumn;
    @FXML
    private TableColumn<MessageEntity, Integer> idColumn;
    @FXML
    private TableColumn<AtachmentEntity, String> addColumn;
    @FXML
    private WebView emailBody;
    @FXML
    private TextField textAddition;
    private Settings settings;
    
    public Slot settingsChanged = new Slot(this, "settingChangeds");

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        settings = new Settings();
        
        String protocol = settings.getValue("protocol");
        String host = settings.getValue("serverMail");
        String port = settings.getValue("portMail");
        String userName = settings.getValue("userMail");
        String password = settings.getValue("passMail");
        String count = settings.getValue("countMail");
        String server = settings.getValue("server");
        String user = settings.getValue("user");
        String db = settings.getValue("database");
        String password_db = settings.getValue("password");
        textAddition.setText( settings.getValue("addition"));
        String conString = "jdbc:mysql://";
        conString += server;
        conString += "/";
        conString += db;
        conString += "?useSSL=false";
        count_messages = Integer.valueOf(count);
        DA = new DataAccessor("com.mysql.jdbc.Driver", conString, user, password_db);
        email = new Email(protocol, host, port, userName, password);
        new Thread(() -> {
            try {
                Message[] messages = email.getMessages("INBOX", Integer.valueOf(count));
                for (int i = Integer.valueOf(count) - 1; i >= 0; i--) {
                    final int counter = i;
                    messageData.add(new MessageEntity(messages[counter]));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        /* new Thread(
                new Runnable() {
            @Override
            public void run() {
                Message[] messages = email.getMessages("INBOX", Integer.valueOf(count));
                for (int i = Integer.valueOf(count) - 1; i >= 0; i--) {
                    final int counter = i;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            messageData.add(new MessageEntity(messages[counter]));
                        }
                    });
                }
            }
        }
        ).start();*/
        sendButton.setDisable(
                true);
        addButton.setDisable(
                false);
        deleteButton.setDisable(
                true);
        saveButton.setDisable(
                true);
        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        fromColumn.setCellValueFactory(
                new PropertyValueFactory<>("from"));
        subjectColumn.setCellValueFactory(
                new PropertyValueFactory<>("subject"));
        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("date"));
        addColumn.setCellValueFactory(
                new PropertyValueFactory<>("filename"));
        mailTableView.setItems(messageData);

        mailTableView.setEditable(
                true);
        addedfileListView.setItems(addData);

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
     public void settingChangeds(){
         System.out.println("vadikvs.ife.MailController.settingChangeds()");
        textAddition.setText( settings.getValue("addition"));
     }
    @FXML
    private void onSettingsButton() {
        try {
            Settings settings = new Settings();
            settingsChanged.connect(settings.changed);
            SettingsFormGenerator form = new SettingsFormGenerator(settings);
            form.show();

        } catch (Exception e) {
            Logger.getLogger(MainController.class
                    .getName()).log(Level.SEVERE, null, e);

        }
    }

    @FXML
    private void onFirmSelectButton() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader
                    .setLocation(MainApp.class
                            .getResource("/fxml/Main.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Выбрать  фирму");
            Scene scene = new Scene(page);
            dialog.setScene(scene);
            firmController = loader.getController();
            firmController.setStage(dialog);
            firmChanged.connect(firmController.firmChanged);
            close.connect(firmController.closeAction);
            dialog.showAndWait();

        } catch (IOException e) {
            Logger.getLogger(MainController.class
                    .getName()).log(Level.SEVERE, null, e);

        }
    }

    @FXML
    public void onExitButton() {
        email.disconnect();
        close.emit();
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onMailSelect() {
        //

        MessageEntity entity = mailTableView.getSelectionModel().selectedItemProperty().getValue();
        atachData = entity.getAtach();
        fileListView.setItems(atachData);
        emailBody.getEngine().loadContent(entity.getBody());
    }

    @FXML
    public void onAddButton() {
        String filename = fileListView.getSelectionModel().selectedItemProperty().getValue();
        MessageEntity entity = mailTableView.getSelectionModel().selectedItemProperty().getValue();
        AtachmentEntity e = new AtachmentEntity(filename, entity);
        boolean add = true;
        /* for (int i = 0; i < addData.size(); i++) {
            AtachmentEntity it = addData.get(i);
            if ((Objects.equals(e.getMessageId(), it.getMessageId()))
                    && e.getFilename().equals(it.getFilename())) {
                add = false;
            }
        }*/
        if (add) {
            if (filename != "") {
                if (filename != null) {
                    addData.add(e);
                    sendButton.setDisable(false);
                }
            }
        }
        //addButton.setDisable(true);

    }

    @FXML
    public void onFileSelect() {
        //
        /*String filename = fileListView.getSelectionModel().selectedItemProperty().getValue();
        MessageEntity entity = mailTableView.getSelectionModel().selectedItemProperty().getValue();
        if (atachData.size() > 0) {
            if (filename != null) {
                addButton.setDisable(false);
            }
        }*/

    }

    @FXML
    public void onMoreMessage() {
        String count = settings.getValue("countMail");
        new Thread(() -> {

            try {
                Integer start = email.getCountMessagesInFolder("INBOX") - count_messages;
                Message[] messages = email.getMoreMessages("INBOX", start, Integer.valueOf(count));
                for (int i = Integer.valueOf(count) - 1; i >= 0; i--) {
                    final int counter = i;

                    messageData.add(new MessageEntity(messages[counter]));
                }

            } catch (MessagingException ex) {
                Logger.getLogger(MailController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }).start();
        count_messages += Integer.valueOf(count);
    }

    @FXML
    public void onAddFileSelect() {
        AtachmentEntity entity = addedfileListView.getSelectionModel().getSelectedItem();
        if (entity != null) {
            deleteButton.setDisable(false);
            saveButton.setDisable(false);
            sendButton.setDisable(false);
        }
    }

    @FXML
    public void onDeleteButton() {
        deleteButton.setDisable(true);
        saveButton.setDisable(true);
        AtachmentEntity entity = addedfileListView.getSelectionModel().getSelectedItem();
        addData.remove(entity);
        if (addData.size() <= 0) {
            sendButton.setDisable(true);
        }
    }

    @FXML
    public void onSaveButton() {
        saveButton.setDisable(true);
        DirectoryChooser fileChooser = new DirectoryChooser();
        AtachmentEntity entity = addedfileListView.getSelectionModel().getSelectedItem();
        fileChooser.setTitle("Cохранить вложение");
        File file = fileChooser.showDialog(stage);
        if (file != null) {
            String path = file.getPath();
            entity.saveAtach(path);
        }

    }

    @FXML
    public void onSendButton() {
        if (sendButton.getText().contains("файлы")) {
            onFirmSelectButton();
        } else {
            ProductsList products = new ProductsList();
            String tempPath = settings.getValue("tempPath");
            String converterServer = settings.getValue("converterServer");
            for (int i = 0; i < addData.size(); i++) {
                AtachmentEntity entity = addData.get(i);
                DataExtractor DE = new DataExtractor(entity, param);
                products.addAll(DE.getProductsFromFile(tempPath, converterServer));
            }
            RequestMaker req = new RequestMaker(products.get(), settings.getValue("server"),
                    textAddition.getText());
            BrowserLauncher bl = new BrowserLauncher();
            JsonMaker jm = new JsonMaker(products.get());
            String data = jm.getJson();
            Float addition = Float.parseFloat(textAddition.getText());
            Ife ife = new Ife(data, firm.getId(), addition, "");
            DA.insertIfe(ife);
            bl.openBrowser(req.getStringWithHash(ife.getHash()),
                    settings.getValue("browser"));
            sendButton.setDisable(true);
            deleteButton.setDisable(true);
            saveButton.setDisable(true);
            addData.clear();
        }
    }

    public void setFirm(FirmEntity firm) {
        this.firm = firm;
    }

    public void onFirmChanged(FirmEntity firm) {
        this.firm = firm;
        setStage((Stage) exitButton.getScene().getWindow());
        stage.setTitle("Выбрать счета для переделки фирмы: " + firm.getName());
        sendButton.setText("Отправить: " + firm.getName());
        this.param = DA.getParamsByFirmId(firm.getId());
    }

    @FXML
    private void onImportButton() {
        try {
            Settings settings = new Settings();
            FileChooser chooser = new FileChooser();
            Stage stage = (Stage) exitButton.getScene().getWindow();
            File file = chooser.showOpenDialog(stage);
            List<ProductEntity> products = new ArrayList<>();
            DataExtractor DE = new DataExtractor(file, param);
            String tempPath = settings.getValue("tempPath");
            String converterServer = settings.getValue("converterServer");
            products.addAll(DE.getProductsFromFile(tempPath, converterServer));
            RequestMaker req = new RequestMaker(products, settings.getValue("server"),
                    textAddition.getText());
            BrowserLauncher bl = new BrowserLauncher();
            JsonMaker jm = new JsonMaker(products);
            String data = jm.getJson();
            Float addition = Float.parseFloat(textAddition.getText());
            Ife ife = new Ife(data, firm.getId(), addition, "");
            DA.insertIfe(ife);
            bl.openBrowser(req.getStringWithHash(ife.getHash()),
                    settings.getValue("browser"));

        } catch (Exception e) {
            Logger.getLogger(MainController.class
                    .getName()).log(Level.SEVERE, null, e);

        }
    }

}
