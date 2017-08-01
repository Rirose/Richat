package GUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Rirose on 5/13/2017.
 * Main Frame
 */
public class mainFrame extends JFrame {
    private final String name;
    private final String pass;
    private JTabbedPane tabbedPane;
    private int tabCount=0;
    private styleSheet sheet = new styleSheet();

    public mainFrame( String name, String pass ){
        this.name = name;
        this.pass = pass;

        setTitle( "RiChat | Welcome #" + name );

        sheet.setUI();
        createMenu();
        createTabs();

        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        sheet.setStyle( this );
        pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2 );
        toFront();
        setVisible( true );
    }

    private void createMenu(){
        JMenu fileMenu = new JMenu("File");
        sheet.setStyle( fileMenu );
        JMenuItem aboutItem = new JMenuItem("About");
        sheet.setStyle( aboutItem );
        fileMenu.add( aboutItem );

        aboutItem.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        JOptionPane.showMessageDialog( mainFrame.this,
                                "RiChat","Hi!",JOptionPane.PLAIN_MESSAGE );
                    }
                }
        );

        JMenuItem disconnectItem = new JMenuItem("Disconnect Your Twitch Account" );
        sheet.setStyle( disconnectItem );
        fileMenu.add( disconnectItem );

        disconnectItem.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        loginFrame login = new loginFrame();
                        dispose();
                    }
                }
        );

        JMenuItem exitItem = new JMenuItem("Exit" );
        sheet.setStyle( exitItem );
        fileMenu.add( exitItem );

        exitItem.addActionListener(
                new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        System.exit( 0 );
                    }
                }
        );

        JMenu chatMenu = new JMenu("Chat" );
        sheet.setStyle( chatMenu );

        JMenuItem newChatItem = new JMenuItem("Add New Chat" );
        sheet.setStyle( newChatItem );
        newChatItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addTab();
            }
        });
        chatMenu.add( newChatItem );

        JMenuItem closeItem = new JMenuItem("Close Chat" );
        sheet.setStyle( closeItem );

        closeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = tabbedPane.getSelectedIndex();
                tabbedPane.removeTabAt( index );
                tabCount--;
                if( tabCount == 0 ) {
                    addTab();
                    index = 0;
                }
                if( index != 0 )
                    index--;
                tabbedPane.setSelectedIndex( index );
            }
        });

        chatMenu.add( closeItem );
        JMenuBar bar = new JMenuBar();
        setJMenuBar ( bar );
        bar.add( fileMenu );
        bar.add( chatMenu );
        sheet.setStyle( bar );
    }

    private void createTabs(){
        tabbedPane = new JTabbedPane();

        JPanel panelNewTab = new JPanel();
        sheet.setStyle( panelNewTab );

        tabbedPane.setOpaque( true );
        sheet.setStyle( tabbedPane );

        tabbedPane.addTab("+",null,panelNewTab,null );
        tabbedPane.setBackgroundAt(0, Color.black );
        tabbedPane.setForegroundAt(0, Color.white );
        addTab();
        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                JTabbedPane sourceTabbedPane = ( JTabbedPane ) changeEvent.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                if( index == tabbedPane.getTabCount() - 1 && index != 0 && tabCount == tabbedPane.getTabCount() )
                    addTab();
                else
                    tabCount = tabbedPane.getTabCount();
            }
        };
        tabbedPane.addChangeListener( changeListener );

        add(tabbedPane, BorderLayout.CENTER );
    }

    private void addTab(){
        boolean bool = false;
        int index = tabbedPane.getTabCount() - 2;
        newPanel panel = new newPanel( tabbedPane, name, pass );
        sheet.setStyle( panel );
        if ( index == -1 ) {
            index++;
            bool = true;
        }

        tabbedPane.setSelectedIndex( index );
        if( bool ) {
            tabbedPane.insertTab("New", null, panel, null, index );
            tabbedPane.setSelectedIndex( index );
        }
        else {
            index++;
            tabbedPane.insertTab("New", null, panel, null, index );
            tabbedPane.setSelectedIndex( index );
        }
        tabCount=tabbedPane.getTabCount();
    }
}
