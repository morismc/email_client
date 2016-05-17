package ch.ffhs.pvanv.mailclient.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.xml.internal.ws.util.StringUtils;

/**
 * A utility class that sends an e-mail message with attachments.
 * 
 * @author www.codejava.net
 *
 */
public class EmailUtility {
    
    private static Folder emailFolder;
    private static Store store;
    
    
    public static void sendEmail(Properties smtpProperties, String toAddress,
            String subject, String message, File[] attachFiles)
                    throws AddressException, MessagingException, IOException {

        final String userName = smtpProperties.getProperty("mail.user");
        final String password = smtpProperties.getProperty("mail.password");

        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getInstance(smtpProperties, auth);

        // creates a new e-mail message
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(userName));
        InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());

        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(message, "text/plain");

        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // adds attachments
        if (attachFiles != null && attachFiles.length > 0) {
            for (File aFile : attachFiles) {
                MimeBodyPart attachPart = new MimeBodyPart();

                try {
                    attachPart.attachFile(aFile);
                } catch (IOException ex) {
                    throw ex;
                }

                multipart.addBodyPart(attachPart);
            }
        }

        // sets the multi-part as e-mail's content
        msg.setContent(multipart);

        // sends the e-mail
        Transport.send(msg);

    }

    public static String getEMailFolders(Properties properties)
    {
        Folder f;
        try {
            properties.put("mail.mime.address.strict", "false");
            Session emailSession = Session.getDefaultInstance(properties);
            
            store = emailSession.getStore("pop3s");

            store.connect(properties.getProperty("mail.pop3.host"), 
                    properties.getProperty("mail.user"), 
                    properties.getProperty("mail.password"));
            
            f = store.getFolder("INBOX");
            f.open(Folder.READ_ONLY);

            
            if ((f.getType() & Folder.HOLDS_MESSAGES) != 0)
            {
              return ""+f.getName()+" ("+f.getMessageCount()+")";
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public static Message[] getEMails(Properties properties, String folderName) {
        
        try {

            Session emailSession = Session.getDefaultInstance(properties);

            // create the POP3 store object and connect with the pop
            // server
            store = emailSession.getStore("pop3s");

            store.connect(properties.getProperty("mail.pop3.host"), 
                    properties.getProperty("mail.user"), 
                    properties.getProperty("mail.password"));

            // create the folder object and open it
            emailFolder = store.getFolder(folderName);
            emailFolder.open(Folder.READ_ONLY);

            // retrieve the messages from the folder in an array and
            // print it
            return emailFolder.getMessages();

        } catch (NoSuchProviderException e1) {
            e1.printStackTrace();
        } catch (MessagingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }
    
    /**
     * Ermittle den Inhalt einer Multipart-E-Mail
     * @throws MessagingException 
     * @throws IOException 
     */
    public static String getMultipartText(Message mailMessage) throws IOException, MessagingException{
        Object msgContent = mailMessage.getContent();

        String content = "";             

         /* Check if content is pure text/html or in parts */                     
         if (msgContent instanceof Multipart) {

             Multipart multipart = (Multipart) msgContent;

             System.out.println("MultiPartCount: "+multipart.getCount());

             for (int j = 0; j < multipart.getCount(); j++) {

              BodyPart bodyPart = multipart.getBodyPart(j);

              if (bodyPart.isMimeType("text/plain")) {
                  return bodyPart.getContent().toString();
              }
            }
         }
         else{
             return mailMessage.getContent().toString();
         }
             
        return content;
    }
    
    /**
     * Schliesse store and folder Objekte
     */
    public void close(){
        
        if(emailFolder!=null){
            try {
                emailFolder.close(false);
            } catch (MessagingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if(store!=null){
            try {
                store.close();
            } catch (MessagingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }          
    }
}
