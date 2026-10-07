/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.util.Date;
import java.io.IOException;
import javax.mail.MessagingException;
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
    
    private AttachmentReader.Result attachments;

    public MessageEntity(Message message) {


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
        this.attachments = null;
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

    public synchronized AttachmentReader.Result loadAttachments() throws IOException, MessagingException {
        if (attachments == null) {
            attachments = AttachmentReader.read(message, this);
        }
        return attachments;
    }

    private void getMessageData() {
            setId(MessageUtility.getId(message));
            setFrom(MessageUtility.getFrom(message));
            setSubject(MessageUtility.getSubject(message));
            setDate(MessageUtility.getDate(message));
            setBody(MessageUtility.getBody(message));

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    


}
