/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vadikvs.ife;

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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javax.mail.Message;

/**
 * FXML Controller class
 *
 * @author vadim
 */
public class MailController implements Initializable {

    public Slot closeAction = new Slot(this, "onExitButton");
    public Slot firmChanged = new Slot(this, "onFirmChanged");
    private final ObservableList<MessageEntity> messageData = FXCollections.observableArrayList();
    private ObservableList<String> atachData = FXCollections.observableArrayList();
    private final ObservableList<AtachmentEntity> addData = FXCollections.observableArrayList();
    private Email email;
    private Stage stage;
    private FirmEntity firm;
    private ParamsEntity param;

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
    private Settings settings;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        settings = new Settings();
        String protocol = settings.getProtocolMail();
        String host = settings.getServerMail();
        String port = settings.getPortMail();
        String userName = settings.getUserMail();
        String password = settings.getUserPassword();
        String count = settings.getCountMail();
        email = new Email(protocol, host, port, userName, password);
        Message[] messages = email.getMessages("INBOX", Integer.valueOf(count));
        for (int i = Integer.valueOf(count) - 1; i >= 0; i--) {
            MessageEntity entity = new MessageEntity(messages[i]);
            messageData.add(entity);
        }
        sendButton.setDisable(true);
        addButton.setDisable(true);
        deleteButton.setDisable(true);
        saveButton.setDisable(true);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        addColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
        mailTableView.setItems(messageData);
        mailTableView.setEditable(true);
        addedfileListView.setItems(addData);

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void onExitButton() {
        email.disconnect();
        stage.close();
    }

    @FXML
    public void onMailSelect() {
        //

        MessageEntity entity = mailTableView.getSelectionModel().selectedItemProperty().getValue();
        atachData = entity.getAtach();
        fileListView.setItems(atachData);
    }

    @FXML
    public void onAddButton() {
        String filename = fileListView.getSelectionModel().selectedItemProperty().getValue();
        MessageEntity entity = mailTableView.getSelectionModel().selectedItemProperty().getValue();
        AtachmentEntity e = new AtachmentEntity(filename, entity);
        boolean add = true;
        for (int i = 0; i < addData.size(); i++) {
            AtachmentEntity it = addData.get(i);
            if ((Objects.equals(e.getMessageId(), it.getMessageId()))
                    && e.getFilename().equals(it.getFilename())) {
                add = false;
            }
        }
        if (add) {
            addData.add(e);
        }
        addButton.setDisable(true);
        sendButton.setDisable(false);
    }

    @FXML
    public void onFileSelect() {
        //
        String filename = fileListView.getSelectionModel().selectedItemProperty().getValue();
        MessageEntity entity = mailTableView.getSelectionModel().selectedItemProperty().getValue();
        if (atachData.size() > 0) {
            if (filename != null) {
                addButton.setDisable(false);
            }
        }

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
        List<ProductEntity> products = new ArrayList<>();
        for (int i = 0; i < addData.size(); i++) {
            AtachmentEntity entity = addData.get(i);
            DataExtractor DE = new DataExtractor(entity, param);
            products.addAll(DE.getProductsFromFile(settings.getTempPath()));
        }
        RequestMaker req = new RequestMaker(products, settings.getServer(), "30");
        BrowserLauncher bl= new BrowserLauncher();
        bl.openBrowser(req.getStringRequest());
        sendButton.setDisable(true);
    }

    public void setFirm(FirmEntity firm) {
        this.firm = firm;
    }

    public void onFirmChanged(FirmEntity firm) {
        this.firm = firm;
        stage.setTitle(firm.getName());
    }

    private void openBrowser(String url) {
        try {
            URI u = new URI(url);
            System.out.println(u.toString());
            java.awt.Desktop.getDesktop().browse(u);
        } catch (MalformedURLException ex) {
            Logger.getLogger(MailController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(MailController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
