/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.mail.Message;


/**
 *
 * @author vadim
 */
public class MessageEntity {

    private int id;
    private String from;
    private String to;
    private String subject;
    private String body;
    private Date date;
    private Message message;
    
    private ObservableList<String> atach;

    public MessageEntity(Message message) {

        atach = FXCollections.observableArrayList();
        setMessage(message);

    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setMessage(Message message) {
        this.message = message;
        getMessageData();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public ObservableList<String> getAtach() {
        return atach;
    }

    public void setAtach(ObservableList<String> atach) {
        this.atach = atach;
    }

    private void getMessageData() {
            setId(MessageUtility.getId(message));
            setFrom(MessageUtility.getFrom(message));
            setDate(MessageUtility.getDate(message));
            setBody(MessageUtility.getBody(message));
            setAtach(FXCollections.
                    observableArrayList(MessageUtility.getAtaches(message)));       
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    
    public void saveAtachByFilename(String path,String filename){
        MessageUtility.saveMessageAtachByFilename(path, filename, message);
    }

}
