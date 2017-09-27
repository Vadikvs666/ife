/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vadikvs.ife;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javax.mail.Message;

/**
 * FXML Controller class
 *
 * @author vadim
 */
public class MailController implements Initializable {

    private ObservableList<MessageEntity> messageData = FXCollections.observableArrayList();
    private ObservableList<String> atachData = FXCollections.observableArrayList();
    private ObservableList<AtachmentEntity> addData = FXCollections.observableArrayList();
    private Email email;
    private Stage stage;
    @FXML
    private Button exitButton;
    @FXML
    private Button sendButton;
    @FXML
    private Button addButton;
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
        idColumn.setCellValueFactory(new PropertyValueFactory<MessageEntity, Integer>("id"));
        fromColumn.setCellValueFactory(new PropertyValueFactory<MessageEntity, String>("from"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<MessageEntity, String>("subject"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<MessageEntity, Date>("date"));
        addColumn.setCellValueFactory(new PropertyValueFactory<AtachmentEntity, String>("filename"));
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
            System.out.println(e.getFilename() + " " + it.getFilename());
            System.out.println(e.getMessageId() + " " + it.getMessageId());
            if ((Objects.equals(e.getMessageId(), it.getMessageId()))) {
                if (e.getFilename().equals(it.getFilename())) {
                    add = false;
                }
            }
        }
        if (add) {
            addData.add(e);
        }
    }

    @FXML
    public void onFileSelect() {
        //
        String filename = fileListView.getSelectionModel().selectedItemProperty().getValue();
        MessageEntity entity = mailTableView.getSelectionModel().selectedItemProperty().getValue();
        addButton.setDisable(false);

    }

    @FXML
    public void onSendButton() {
        //
        String filename = fileListView.getSelectionModel().selectedItemProperty().getValue();
        MessageEntity entity = mailTableView.getSelectionModel().selectedItemProperty().getValue();
        entity.saveAtachByFilename("/tmp/ife/atach/" + entity.getFrom() + File.separatorChar + String.valueOf(entity.getId()), filename);
        sendButton.setDisable(true);
    }

}
