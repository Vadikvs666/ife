/*
 *  Автор Вагин Вадим Сергеевич
 * e-mail: vadim@hoz.center
 */
package vadikvs.ife;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class MessageUtility {

    /**
     * Returns a list of addresses in String format separated by comma
     *
     * @param address an array of Address objects
     * @return a string represents a list of addresses
     */
    private static String parseAddresses(Address[] address) {
        String listAddress = "";

        if (address != null) {
            for (Address addres : address) {
                listAddress += addres.toString() + ", ";
            }
        }
        if (listAddress.length() > 1) {
            listAddress = listAddress.substring(0, listAddress.length() - 2);
        }

        return listAddress;
    }

    public static void saveMessageAtachByFilename(String path, String filename, Message message) {
        try {
            Multipart multiPart = (Multipart) message.getContent();
            for (int j = 0; j < multiPart.getCount(); j++) {
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(j);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    if (MimeUtility.decodeText(part.getFileName()).equals(filename)) {
                        Utility.saveFile(part.getInputStream(), filename, path);
                    }
                }
            }
        } catch (IOException | MessagingException ex) {
            Logger.getLogger(MessageEntity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getFrom(Message message) {
        String from = "";
        try {
            Address[] fromAddress = message.getFrom();
            from = fromAddress[0].toString();
            from = MimeUtility.decodeText(from);
        } catch (UnsupportedEncodingException | MessagingException ex) {
            Logger.getLogger(MessageUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return from;
    }

    public static Integer getId(Message message) {
        return message.getMessageNumber();
    }

    public static String getSubject(Message message) {
        String subject = "";
        try {
            subject = message.getSubject();
        } catch (MessagingException ex) {
            Logger.getLogger(MessageUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return subject;
    }

    public static String gettoList(Message message) {
        String to = "";
        try {
            to = parseAddresses(message.getRecipients(Message.RecipientType.TO));
        } catch (MessagingException ex) {
            Logger.getLogger(MessageUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return to;
    }

    public static String getccList(Message message) {
        String cc = "";
        try {
            cc = parseAddresses(message.getRecipients(Message.RecipientType.CC));
        } catch (MessagingException ex) {
            Logger.getLogger(MessageUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cc;
    }

    public static Date getDate(Message message) {
        Date date = null;
        try {
            date = message.getSentDate();
        } catch (MessagingException ex) {
            Logger.getLogger(MessageUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return date;
    }

    public static String getBody(Message message) {
        String body = "";
        try {
            String contentType = message.getContentType();
            if (contentType.contains("text/plain")
                    || contentType.contains("text/html")) {
                Object content = message.getContent();
                if (content != null) {
                    body = content.toString();
                }
            }
        } catch (IOException | MessagingException ex) {
            Logger.getLogger(MessageUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return body;
    }

    public static List<String> getAtaches(Message message) {
        List<String> atach = new ArrayList();
        try {
            String contentType = message.getContentType();
            if (contentType.contains("multipart")) {
                Multipart multiPart = (Multipart) message.getContent();
                for (int j = 0; j < multiPart.getCount(); j++) {
                    MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(j);
                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                        String filename = part.getFileName();
                        atach.add(MimeUtility.decodeText(filename));
                    }
                }
            }
        } catch (IOException | MessagingException ex) {
            Logger.getLogger(MessageUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return atach;
    }

    
}
