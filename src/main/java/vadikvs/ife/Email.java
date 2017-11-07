package vadikvs.ife;

import com.sun.mail.util.MailSSLSocketFactory;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

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
    private Store store;
    private Folder folderInbox;

    public Email(String protocol, String host, String port,
            String userName, String password) {
        try {
            Properties properties = getProperties(protocol, host, port);
            Session session = Session.getInstance(properties, null);
            store = session.getStore(protocol);
            store.connect(host, Integer.valueOf(port), userName, password);

        } catch (MessagingException ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private Properties getProperties(String protocol, String host,
            String port) {
        MailSSLSocketFactory socketFactory = null;
        try {
            socketFactory = new MailSSLSocketFactory();
            String[] trustedHosts = {host};
            socketFactory.setTrustedHosts(trustedHosts);
        } catch (GeneralSecurityException e) {
        }

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
     * Downloads messages from folder.
     *
     * @param folder
     * @param count
     * @return
     *
     */
    public Message[] getMessages(String folder, Integer count) {
        try {
            folderInbox = store.getFolder(folder);
            folderInbox.open(Folder.READ_ONLY);
            int end = folderInbox.getMessageCount();
            int start = end - count;
            Message[] messages = folderInbox.getMessages(start + 1, end);
            return messages;
        } catch (MessagingException ex) {
            System.out.println("Could not open messages folder");
        }
        return null;
    }
    
    public Message[] getMoreMessages(String folder, Integer start,Integer count) {
        try {
            folderInbox = store.getFolder(folder);
            folderInbox.open(Folder.READ_ONLY);
            int end = folderInbox.getMessageCount();
            Message[] messages = folderInbox.getMessages(start + 1, end);
            return messages;
        } catch (MessagingException ex) {
            System.out.println("Could not open messages folder");
        }
        return null;
    }

    public void disconnect() {
        try {
            folderInbox.close(false);
            store.close();
        } catch (MessagingException ex) {
            Logger.getLogger(Email.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Integer getCountMessagesInFolder(String folder) throws MessagingException{
        Folder fold = store.getFolder(folder);
        return fold.getMessageCount();
    }

}
