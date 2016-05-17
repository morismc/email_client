package ch.ffhs.pvanv.mailclient.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Properties;
 
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import ch.ffhs.pvanv.mailclient.model.ConfigUtility;
 
/**
 * Displays a setting form that allows configuring SMTP settings.
 * @author www.codejava.net
 *
 */
public class SettingsDialog extends JDialog {
 
    private ConfigUtility configUtil;
     
    private JLabel labelSMTPHost = new JLabel("SMTP Host name: ");
    private JLabel labelSMTPPort = new JLabel("SMTP Port number: ");
    private JLabel labelUser = new JLabel("Username: ");
    private JLabel labelPass = new JLabel("Password: ");
    private JLabel labelPOP3Host = new JLabel("POP3 Host name: ");
    private JLabel labelPOP3Port = new JLabel("POP3 Port number: ");
    
   
    private JTextField textSMTPHost = new JTextField(20);
    private JTextField textSMTPPort = new JTextField(20);
    private JTextField textUser = new JTextField(20);
    private JTextField textPass = new JTextField(20);
    private JTextField textPOP3Host = new JTextField(20);
    private JTextField textPOP3Port = new JTextField(20);
    
     
    private JButton buttonSave = new JButton("Save");
     
    public SettingsDialog(JFrame parent, ConfigUtility configUtil) {
        super(parent, "SMTP and POP3 Settings", true);
        this.configUtil = configUtil;
         
        setupForm();
         
        loadSettings();
         
        pack();
        setLocationRelativeTo(null);
    }
     
    private void setupForm() {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 5, 10);
        constraints.anchor = GridBagConstraints.WEST;
         
        // Host Label                  - Position 0 0
        add(labelSMTPHost, constraints);
         
        constraints.gridx = 1;
        
        // Host Textbox - Position 1 0
        add(textSMTPHost, constraints);
        
        constraints.gridy = 1;
        constraints.gridx = 0;
        
        // Port Label                  - Position 0 1
        add(labelSMTPPort, constraints);
        
        constraints.gridx = 1;
        // Port Textbox - Position 1 1
        add(textSMTPPort, constraints);
 
        constraints.gridy = 2;
        constraints.gridx = 0;
        // User Label                  - Position 0 2
        add(labelPOP3Host, constraints);
         
        constraints.gridx = 1;
        // User Textbox - Position 1 2
        add(textPOP3Host, constraints);
 
        constraints.gridy = 3;
        constraints.gridx = 0;
        // Pass Label                  - Position 0 3
        add(labelPOP3Port, constraints);
         
        constraints.gridx = 1;
        // Pass Textbox - Position 1 3
        add(textPOP3Port, constraints);
         
        constraints.gridy = 4;
        constraints.gridx = 0;
        
        // POP3 Host Label             - Position 0 4
        add(labelUser, constraints);
         
        constraints.gridx = 1;
        
        // POP3 Host Textbox - Position 1 4
        add(textUser, constraints);
        
        constraints.gridy = 5;
        constraints.gridx = 0;
        
        // POP3 Port Label             - Position 0 5
        add(labelPass, constraints);
         
        constraints.gridx = 1;
        
        // POP3 Port Textbox - Position 1 5
        add(textPass, constraints);
        
        constraints.gridy = 1;
        constraints.gridx = 0;
        
        constraints.gridy = 6;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        add(buttonSave, constraints);
         
        buttonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                buttonSaveActionPerformed(event);
            }
        });    
    }
     
    private void loadSettings() {
        Properties configProps = null;
        try {
            configProps = configUtil.loadProperties();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error reading settings: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
         
        textSMTPHost.setText(configProps.getProperty("mail.smtp.host"));
        textSMTPPort.setText(configProps.getProperty("mail.smtp.port"));
        textPOP3Host.setText(configProps.getProperty("mail.pop3.host"));
        textPOP3Port.setText(configProps.getProperty("mail.pop3.port"));
        textUser.setText(configProps.getProperty("mail.user"));
        textPass.setText(configProps.getProperty("mail.password")); 
    }
     
    private void buttonSaveActionPerformed(ActionEvent event) {
        try {
            configUtil.saveProperties(textSMTPHost.getText(),
                    textSMTPPort.getText(),
                    textPOP3Host.getText(),
                    textPOP3Port.getText(),
                    textUser.getText(),
                    textPass.getText());
            JOptionPane.showMessageDialog(SettingsDialog.this,
                    "Properties were saved successfully!");    
            dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving properties file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }      
    }
}