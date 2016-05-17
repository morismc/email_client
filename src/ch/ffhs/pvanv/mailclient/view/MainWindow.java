package ch.ffhs.pvanv.mailclient.view;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import ch.ffhs.pvanv.mailclient.model.ConfigUtility;
import ch.ffhs.pvanv.mailclient.model.EmailUtility;

public class MainWindow extends JFrame{

    private JFrame frame;
    private final Action action = new SwingAction();
    private JTable emailsTable;
    private Message[] messages;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainWindow window = new MainWindow();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MainWindow() {
        initialize();
    }

    /**
     * Ermittelt E-Mails vom POP3-Server und schiebt sie in die JTable
     */
    private void mailsToTable(){
        ConfigUtility configUtil = new ConfigUtility();
        try {
            Properties mailProperties = configUtil.loadProperties();
            this.messages = EmailUtility.getEMails(mailProperties, "INBOX");
            
            DefaultTableModel model = (DefaultTableModel) emailsTable.getModel();

            /* Alte Rows der Tabelle vor dem Neuzeichnen löschen. */
            if (model.getRowCount() > 0) {
                for (int i = model.getRowCount() - 1; i > -1; i--) {
                    model.removeRow(i);
                }
            }
            
            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
                model.addRow(new Object[]{message.getSubject(),  message.getFrom()[0]});              
            }
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,"Error while fetching e-mails: " + ex.getMessage());
        } catch (MessagingException e1) {
            JOptionPane.showMessageDialog(null,"Error while fetching e-mails: " + e1.getMessage());
        } 
    }
    
    /**
     * Initialisierung der Frame-inhalte der Hauptseite
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 894, 557);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        loadMenu(frame);
        
        frame.getContentPane().setLayout(null);
        
        JTree postBoxTree = loadLeftPostBoxTree();
        frame.getContentPane().add(postBoxTree);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(216, 0, 652, 495);
        frame.getContentPane().add(scrollPane);
        
        getPostBoxEntries(scrollPane);
    }
    
    /**
     * Laden des Menüs in das angegebene Frame
     * 
     * @param frame Das Fenster, in dem die Menüs, Datei, Einstellungen und 
     * E-Mail schreiben dargestellt werden sollen
     */
    private void loadMenu(final JFrame frame){
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        JMenu mnDatei = new JMenu("Datei");
        menuBar.add(mnDatei);
        
        JMenuItem mntmLadeEmails = new JMenuItem("Lade E-Mails");
        mntmLadeEmails.addActionListener(new ActionListener(){
            
            public void actionPerformed(ActionEvent e) {
                mailsToTable();
            }
        });
        mnDatei.add(mntmLadeEmails);
        
        JMenuItem mntmSettings = new JMenuItem("Einstellungen");
        mntmSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SettingsDialog dialog = new SettingsDialog(frame, new ConfigUtility());
                dialog.setVisible(true);
            }
        });
        menuBar.add(mntmSettings);
        
        JMenuItem mntmEmailSchreiben = new JMenuItem("E-Mail schreiben");
        mntmEmailSchreiben.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //NewMailJFrame newMailFrame = new NewMailJFrame();
                // set look and feel to system dependent
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                 
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new SMTPSendEmailDialog().setVisible(true);
                    }
                });
                //newMailFrame.setVisible(true);
            }
        });
        menuBar.add(mntmEmailSchreiben);
    }
    
    /**
     * Lade den Baum mit E-Mail Ordnern im linken Fensterbereich
     * in den JTree
     *  
     * @return JTree mit Postbox-Einträgen aus dem POP3-Ordner 
     */
    private JTree loadLeftPostBoxTree(){
        JTree tree = new JTree();
        tree.addTreeSelectionListener(new TreeSelectionListener(){
            
            public void valueChanged(TreeSelectionEvent se) {
                JTree tree = (JTree) se.getSource();
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree
                    .getLastSelectedPathComponent();
                
                // String selectedNodeName = selectedNode.toString(); -- Node Name
                if (selectedNode.isLeaf()) {
                    mailsToTable();
                }
              }
            
        });
        
        tree.setModel(new DefaultTreeModel(
            new DefaultMutableTreeNode("E-Mail Ordner") {
                {
                }
            }
        ));
        
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        ConfigUtility configUtil = new ConfigUtility();
        Properties mailProperties;
        
        try {
            mailProperties = configUtil.loadProperties();
            String folder = EmailUtility.getEMailFolders(mailProperties);
            root.add(new DefaultMutableTreeNode(folder));
            treeModel.reload();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        tree.setBounds(7, 0, 199, 495);
        return tree;
    }
    
    /**
     * Ermittelt alle Postbox Einträge und legt sie in einer JTable mit
     * Betreff und Absender (Von) ab. Zudem wird ein Listener definiert,
     * der bei Mausklick einer E-Mail ein Anzeigefenster öffnet, in dem
     * dann diese E-Mail angezeigt wird.
     * 
     * @param scrollPane
     */
    private void getPostBoxEntries(JScrollPane scrollPane){        
        DefaultTableModel dtm = new DefaultTableModel(new Object[]{"Betreff", "Von"},0);
        
        emailsTable = new JTable(dtm);
        emailsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = emailsTable.rowAtPoint(evt.getPoint());
                int column = emailsTable.columnAtPoint(evt.getPoint());
                if (row >= 0 && column >= 0) {
                    System.out.println(emailsTable.getValueAt(row, column));
                    
                    if(messages!=null && messages.length>row)
                    {
                        Message message = messages[row];
                        // s. http://stackoverflow.com/questions/13474705/reading-body-part-of-a-mime-multipart
                        SMTPReadAndEditEmailDialog dialog = new SMTPReadAndEditEmailDialog(message);
                        dialog.setVisible(true);
                        dialog.setAlwaysOnTop(true);
                    }
                }
            }
        });
        scrollPane.setViewportView(emailsTable);
    }
    
    private class SwingAction extends AbstractAction {
        public SwingAction() {
            putValue(NAME, "SwingAction");
            putValue(SHORT_DESCRIPTION, "Some short description");
        }
        public void actionPerformed(ActionEvent e) {
        }
    }
    
    private static void addPopup(Component component, final JPopupMenu popup) {
        component.addMouseListener(new MouseAdapter() {
        	public void mousePressed(MouseEvent e) {
        		if (e.isPopupTrigger()) {
        			showMenu(e);
        		}
        	}
        	public void mouseReleased(MouseEvent e) {
        		if (e.isPopupTrigger()) {
        			showMenu(e);
        		}
        	}
        	private void showMenu(MouseEvent e) {
        		popup.show(e.getComponent(), e.getX(), e.getY());
        	}
        });
    }
}
