package vadikvs.ife;

import com.sun.mail.util.MailSSLSocketFactory;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.StoreEvent;
import javax.mail.event.StoreListener;
import javax.mail.internet.MimeBodyPart;
import vadikvs.ife.MessageEntity;

/**
 *
 * @author vadim
 */
public class Email {

    /**
     * Returns a Properties object which is configured for a POP3/IMAP server
     *
     * @param protocol either "imap" or "pop3"
     * @param host
     * @param port
     * @return a Properties object
     */
    private Properties getProperties(String protocol, String host,
            String port) {
        MailSSLSocketFactory socketFactory = null;
        try {
            socketFactory = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        String[] trustedHosts = {host};
        socketFactory.setTrustedHosts(trustedHosts);
        Properties props = new Properties();
        props.put(String.format("mail.%s.host", protocol), host);
        props.put(String.format("mail.%s.port", protocol), port);
        props.setProperty("mail.mime.charset", "UTF-8");
        //props.put(String.format("mail.%s.ssl.trust", protocol), host);
        //props.setProperty("mail.imap.ssl.enable", "true");
        //props.put(String.format("mail.%s.ssl.checkserveridentity", protocol), "false");
        props.put(
                String.format("mail.%s.socketFactory", protocol),
                socketFactory);
        return props;
    }

    /**
     * Downloads new messages and fetches details for each message.
     *
     * @param protocol
     * @param host
     * @param port
     * @param userName
     * @param password
     */
    public ObservableList<MessageEntity> getMessages(String protocol, String host, String port,
            String userName, String password, Integer count) {

        ObservableList<MessageEntity> data = FXCollections.observableArrayList();

        Properties properties = getProperties(protocol, host, port);
        Session session = Session.getInstance(properties, null);

        try {
            // connects to the message store
            Store store = session.getStore(protocol);
            store.connect(host, Integer.valueOf(port), userName, password);
            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_ONLY);

            // fetches new messages from server
            int end = folderInbox.getMessageCount();
            int start = end - count;
            Message[] messages = folderInbox.getMessages(start + 1, end);

            for (int i = 0; i < messages.length; i++) {
                MessageEntity entity = new MessageEntity(messages[i]);
                data.add(entity);
            }

            // disconnect
            folderInbox.close(false);
            store.close();

        } catch (NoSuchProviderException ex) {
            System.out.println("No provider for protocol: " + protocol);
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        }
        return data;
    }

    

}
