/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

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
        atach.add("Вложения:");
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

    private void getMessageData() {
        try {
            setId(message.getMessageNumber());
            Address[] fromAddress = message.getFrom();
            String from = fromAddress[0].toString();

            try {
                setFrom(MimeUtility.decodeText(from));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(MessageEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
            setSubject(message.getSubject());
            String toList = parseAddresses(message
                    .getRecipients(Message.RecipientType.TO));
            String ccList = parseAddresses(message
                    .getRecipients(Message.RecipientType.CC));
            setDate(message.getSentDate());
            try {
                String contentType = message.getContentType();
                if (contentType.contains("text/plain")
                        || contentType.contains("text/html")) {

                    Object content = message.getContent();
                    if (content != null) {
                        setBody(content.toString());
                    }

                }
                if (contentType.contains("multipart")) {
                    Multipart multiPart = (Multipart) message.getContent();
                    for (int j = 0; j < multiPart.getCount(); j++) {
                        MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(j);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                            AtachmentEntity at = new AtachmentEntity(part.getFileName());
                            String filename = part.getFileName();
                            atach.add(MimeUtility.decodeText(filename));

                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (MessagingException ex) {
            Logger.getLogger(MessageEntity.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Returns a list of addresses in String format separated by comma
     *
     * @param address an array of Address objects
     * @return a string represents a list of addresses
     */
    private String parseAddresses(Address[] address) {
        String listAddress = "";

        if (address != null) {
            for (int i = 0; i < address.length; i++) {
                listAddress += address[i].toString() + ", ";
            }
        }
        if (listAddress.length() > 1) {
            listAddress = listAddress.substring(0, listAddress.length() - 2);
        }

        return listAddress;
    }

    public void saveFileByFilename(String filename) {
        try {
            Multipart multiPart = (Multipart) message.getContent();
            for (int j = 0; j < multiPart.getCount(); j++) {
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(j);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    String fileName = part.getFileName();
                     System.out.println("savefile decoded "+ fileName);
                    if (MimeUtility.decodeText(fileName).equals(filename)) {
                        saveFile(part.getInputStream(), filename, "files");
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MessageEntity.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (MessagingException ex) {
            Logger.getLogger(MessageEntity.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    private void saveFile(InputStream stream, String filename, String path) {
        try {
            BufferedInputStream in = new BufferedInputStream(stream);
            BufferedOutputStream out = null;
            byte[] buf = new byte[65535]; // буфер ввода/вывода
            while (in.read(buf) != -1) {
                try {
                    out = new BufferedOutputStream(new FileOutputStream(filename));
                    System.out.println("savefile"+path + filename);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MessageEntity.class.getName()).log(Level.SEVERE, null, ex);
                    ex.printStackTrace();
                }
            }
            out.write(buf); // Запись

            //закрытие потоков
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MessageEntity.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

    }

}
