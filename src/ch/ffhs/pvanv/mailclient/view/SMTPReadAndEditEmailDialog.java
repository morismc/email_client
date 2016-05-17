package ch.ffhs.pvanv.mailclient.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ch.ffhs.pvanv.mailclient.model.ConfigUtility;
import ch.ffhs.pvanv.mailclient.model.MultiPartMail;
 
/**
 * Oberflaeche mit Angaben zum Versenden von E-Mails
 * Diese Oberflaeche wurde auf Basis des Beispiels
 * von http://www.codejava.net/ erstellt und an die
 * eigenen Ansprueche angepasst
 */
public class SMTPReadAndEditEmailDialog extends JFrame {
    
    private enum SendTypes{
        SEND_TYPE_FORWARD,
        SEND_TYPE_REPLY
    }
    
    /* Speicher für die Konfiguration des POP3 und SMTP */
    private ConfigUtility configUtil = new ConfigUtility();
     
    /* Textnachticht aus dem geöffneten Dialog*/
    private JTextArea textAreaMessage = new JTextArea(10, 30);
     
    /**
     * Konstruktor zum Laden der Oberflaeche
     */
    public SMTPReadAndEditEmailDialog(Message mailMessage) {
        super("Swing E-mail Sender Program");
        
        // set up layout
        setLayout(new BorderLayout());
     
        try {
            setupForm(mailMessage);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Ein Fehler trat beim Editieren der Mail auf. " + e,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
         
        setPreferredSize(new Dimension(800, 600));
        pack();
        setLocationRelativeTo(null); // center on screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);    
    }

    /**
     * Erstellt das Formular zum Senden einer E-Mail
     * @throws IOException 
     * @throws MessagingException 
     */
    private void setupForm(final Message mailMessage) throws Exception {
        
        // Sub-Panel für die beiden Knöpfe Reply und Forward
        final MultiPartMail mpm = new MultiPartMail(mailMessage);
        JPanel subPanel = new JPanel();
        
        addReplyButton(mailMessage, mpm, subPanel);
        addForwardButton(mailMessage, mpm, subPanel);
        
        add(subPanel, BorderLayout.PAGE_START);
        
        // E-Mailbody
        textAreaMessage.setPreferredSize(new Dimension(600, 400));
        textAreaMessage.setEditable(false);
        add(new JScrollPane(textAreaMessage), BorderLayout.CENTER); 
        
        if(mpm!=null)
          textAreaMessage.setText(mpm.getMessageContent());        
    }
   
    /**
     * Fügt dem Panel einen Reply-Button hinzu. Wird dieser angeklickt, so wird die aktuelle 
     * Nachricht als neuer E-Mail Body verwendet und der Absender als Adressat eingesetzt. 
     * 
     * @param mailMessage ist die eigentliche Nachricht vom Sender, auf die geantwortet wird
     * @param mpm beschreibt den E-Mail Content
     * @param subPanel ist das Panel, an welches der Button angeheftet werden soll 
     */
    private void addReplyButton(final Message mailMessage, final MultiPartMail mpm, 
            final JPanel subPanel){
        JButton replyButton = new JButton("Reply");
        replyButton.setPreferredSize(new Dimension(100, 50));
        
        /*Der Listener fuer den Reply-Knopf*/
        replyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                replyEMail(mailMessage, mpm);
            }
        });
        
        subPanel.add(replyButton);
    }
    
    /**
     * Der Forward button dient dazu den Dialog zur Weiterleitung der E-Mail
     * aufzurufen.
     * 
     * @param subPanel ist das Panel, an welches der button angeheftet werden soll
     */
    private void addForwardButton(final Message mailMessage, final MultiPartMail mpm, final JPanel subPanel){
        JButton forwardButton = new JButton("Forward");
        forwardButton.setPreferredSize(new Dimension(100, 50));
        
        /*Der Listener fuer den Reply-Knopf*/
        forwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                //buttonSendActionPerformed(event);
                // TODO: Forward-Dialog mit E-Mailtext des Absenders öffnen
                forwardEmail(mailMessage, mpm);
            }
        });             

        subPanel.add(forwardButton);
    }
    
    private void replyEMail(final Message mailMessage, final MultiPartMail mpm){
        callSendMailDialog(mailMessage, mpm, SendTypes.SEND_TYPE_REPLY);
    }
    
    private void forwardEmail(final Message mailMessage, final MultiPartMail mpm){
        callSendMailDialog(mailMessage, mpm, SendTypes.SEND_TYPE_FORWARD);
    }
    
    private void callSendMailDialog(final Message mailMessage, final MultiPartMail mpm, 
            SendTypes sendType){
        StringBuffer strbuf = new StringBuffer();
        strbuf.append("------------------------------\t\n\t\n");
        
        SMTPSendEmailDialog dialog = new SMTPSendEmailDialog();
        try {           
            String subjectPrefix = "";
            switch(sendType){
            case SEND_TYPE_FORWARD:
                subjectPrefix = "FW: ";
                break;
            case SEND_TYPE_REPLY:
                subjectPrefix = "RE: ";
                dialog.setFieldToValue(mailMessage.getFrom()[0].toString());
                break;
            }            
            /* betreff setzen */
            dialog.setFieldSubjectValue(subjectPrefix + mailMessage.getSubject());
            /* nachricht setzen */
            strbuf.append(mpm.getMessageContent());
            dialog.setTextAreaMessage(strbuf.toString());
            dialog.setVisible(true);
            dialog.setAlwaysOnTop(true);
        } catch (MessagingException e) {
            System.out.println("Reply Dialog Error.");
        }
    }
    
}
