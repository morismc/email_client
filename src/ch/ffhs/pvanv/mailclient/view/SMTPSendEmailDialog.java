package ch.ffhs.pvanv.mailclient.view;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ch.ffhs.pvanv.mailclient.model.ConfigUtility;
import ch.ffhs.pvanv.mailclient.model.EmailUtility;
 
/**
 * Oberflaeche mit Angaben zum Versenden von E-Mails
 * Diese Oberflaeche wurde auf Basis des Beispiels
 * von http://www.codejava.net/ erstellt und an die
 * eigenen Ansprueche angepasst
 */
public class SMTPSendEmailDialog extends JFrame {
    private ConfigUtility configUtil = new ConfigUtility();
     
    private JLabel labelTo = new JLabel("An: ");
    private JLabel labelSubject = new JLabel("Betreff: ");
     
    private JTextField fieldTo = new JTextField(30);
    
    private JTextField fieldSubject = new JTextField(30);
     
    private JButton buttonSend = new JButton("Senden");
     
    private JFileChooser fileChooser = new JFileChooser();
     
    private JTextArea textAreaMessage = new JTextArea(10, 30);
     
    private GridBagConstraints constraints = new GridBagConstraints();
    
    /*Getter und Setter*/
    public JTextField getFieldTo() {
        return fieldTo;
    }

    public void setFieldToValue(String fieldToValue) {
        this.fieldTo.setText(fieldToValue);
    }

    public JTextField getFieldSubject() {
        return fieldSubject;
    }

    public void setFieldSubjectValue(String fieldSubjectValue) {
        this.fieldSubject.setText(fieldSubjectValue);
    }

    public JTextArea getTextAreaMessage() {
        return textAreaMessage;
    }

    public void setTextAreaMessage(String textAreaMessageValue) {
        this.textAreaMessage.setText(textAreaMessageValue);
    }
    
    /**
     * Konstruktor zum Laden der Oberflaeche
     */
    public SMTPSendEmailDialog() {
        super("Swing E-mail Sender Program");
         
        // set up layout
        setLayout(new GridBagLayout());
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);
     
        setupForm();
         
        pack();
        setLocationRelativeTo(null); // center on screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);    
    }

    /**
     * Erstellt das Formular zum Senden einer E-Mail
     */
    private void setupForm() {
        
        constraints.gridx = 0;
        constraints.gridy = 0;
        // Empfaenger-Label - Position 0 0
        add(labelTo, constraints);
         
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        // Empfaenger-Text - Position 1 0
        add(fieldTo, constraints);
         
        constraints.gridx = 0;
        constraints.gridy = 1;
        // Betreff-Label - Position 0 1
        add(labelSubject, constraints);
         
        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        // Betreff-Text - Position 1 1
        add(fieldSubject, constraints);
         
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridheight = 2;
        constraints.fill = GridBagConstraints.BOTH;
        buttonSend.setFont(new Font("Arial", Font.BOLD, 14));
        // Senden-Knopf - Position 2 0 (rechts)
        add(buttonSend, constraints);
         
        /*Der Listener fuer den Senden-Knopf*/
        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                buttonSendActionPerformed(event);
            }
        });
         
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridheight = 1;
        constraints.gridwidth = 3;
        // FileChooser - Position 0 2
        add(fileChooser, constraints);
         
        constraints.gridy = 3;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        // E-Mailbody
        add(new JScrollPane(textAreaMessage), constraints);    
    }
     
    /**
     * Wenn der Senden-Knopf gedrueckt wurde, werden die folgenden
     * Aktionen ausgefuehrt
     * @param event
     */
    private void buttonSendActionPerformed(ActionEvent event) {
        if (!validateFields ()) {
            return;
        }
         
        String toAddress = fieldTo.getText();
        String subject = fieldSubject.getText();
        String message = textAreaMessage.getText();
         
        File[] attachFiles = null;
         
        /*Ermittlung des Pfades der selektierten Datei*/
        if (fileChooser.getSelectedFile() != null 
                && !fileChooser.getSelectedFile().getPath().equals("")) {
            File selectedFile = new File(fileChooser.getSelectedFile().getPath());
            attachFiles = new File[] {selectedFile};
        }
         
        /*Senden der E-Mail mit den SMTP-Properties*/
        try {
            Properties smtpProperties = configUtil.loadProperties();
            EmailUtility.sendEmail(smtpProperties, toAddress, subject, message, attachFiles);
             
            JOptionPane.showMessageDialog(this,
                    "Die E-Mail wurde erfolgreich versendet!");
             
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error while sending the e-mail: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
     
    /**
     * Pruefung, ob alle Felder (An, Betreff und Nachricht)
     * einen inhalt haben. Der Anhang ist optional.
     * @return
     */
    private boolean validateFields() {
        if (fieldTo.getText().equals("")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter To address!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            fieldTo.requestFocus();
            return false;
        }
         
        if (fieldSubject.getText().equals("")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter subject!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            fieldSubject.requestFocus();
            return false;
        }
         
        if (textAreaMessage.getText().equals("")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter message!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            textAreaMessage.requestFocus();
            return false;
        }
         
        return true;
    }
}
