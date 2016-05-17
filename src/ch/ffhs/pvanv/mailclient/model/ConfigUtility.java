package ch.ffhs.pvanv.mailclient.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
 
/**
 * A utility class that reads/saves SMTP settings from/to a properties file.
 * @author www.codejava.net
 *
 */
public class ConfigUtility {
    private File configFile = new File("smtp.properties");
    private Properties configProps;
     
    public Properties loadProperties() throws IOException {
        Properties defaultProps = new Properties();
        // sets default properties
        defaultProps.setProperty("mail.smtp.host", "smtp-mail.outlook.com");
        defaultProps.setProperty("mail.smtp.port", "587");
        defaultProps.setProperty("mail.pop3.host", "pop-mail.outlook.com");
        defaultProps.setProperty("mail.pop3.port", "995");
        defaultProps.setProperty("mail.user", "maurice.test@outlook.de");
        defaultProps.setProperty("mail.password", "MauriceTest4711");
        defaultProps.setProperty("mail.smtp.starttls.enable", "true");
        defaultProps.setProperty("mail.pop3.starttls.enable", "true");
        defaultProps.setProperty("mail.smtp.auth", "true");
         
        configProps = new Properties(defaultProps);
         
        // loads properties from file
        if (configFile.exists()) {
            InputStream inputStream = new FileInputStream(configFile);
            configProps.load(inputStream);
            inputStream.close();
        }
         
        return configProps;
    }
    
    /**
     * 
     * @param host
     * @param port
     * @param user
     * @param pass
     * @throws IOException
     */
    public void saveProperties(String SMTPHost, String SMTPPort, String POP3Host, 
            String POP3Port, String user, String pass) throws IOException {
        
        // SMTP config
        configProps.setProperty("mail.smtp.host", SMTPHost);
        configProps.setProperty("mail.smtp.port", SMTPPort);
        configProps.setProperty("mail.user", user);
        configProps.setProperty("mail.password", pass);
        configProps.setProperty("mail.smtp.starttls.enable", "true");
        configProps.setProperty("mail.smtp.auth", "true");
        
        // POP3 config
        configProps.setProperty("mail.pop3.host", POP3Host);
        configProps.setProperty("mail.pop3.port", POP3Port);
        configProps.setProperty("mail.pop3.starttls.enable", "true");
         
        OutputStream outputStream = new FileOutputStream(configFile);
        configProps.store(outputStream, "host setttings");
        outputStream.close();
    }  
}