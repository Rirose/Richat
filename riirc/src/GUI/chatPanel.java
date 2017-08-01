package GUI;

import codes.connection;

import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Rirose on 5/13/2017.
 * Chat Panel
 */
class chatPanel extends JPanel {
    private connection con;

    public chatPanel( String channelName, String nick, String pass ){
        setPreferredSize( new Dimension(1000,800) );
        setLayout( new BorderLayout() );

        StyleContext style = new StyleContext();
        final DefaultStyledDocument doc = new DefaultStyledDocument( style );
        JTextPane chatArea = new JTextPane( doc );

        chatArea.setEditable( false );
        styleSheet sheet = new styleSheet();
        sheet.setStyle( chatArea );
        chatArea.setSelectedTextColor( Color.gray );

        con = new connection(nick, pass, "#"+channelName, chatArea );

        final JTextField messageField = new JTextField();
        messageField.setEditable( true );
        sheet.setStyle( messageField );
        messageField.setSelectedTextColor( Color.gray );
        messageField.setPreferredSize( new Dimension( getWidth(),50) );
        messageField.addActionListener (
                new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                        con.sendMessage( e.getActionCommand() );
                        messageField.setText( "" );
                    }
        });
        add( new JScrollPane( chatArea ), BorderLayout.CENTER );
        add( messageField, BorderLayout.SOUTH );
        setVisible( true );

        JTextArea viewerList = new JTextArea();
        viewerList.setEditable( false );
        sheet.setStyle( viewerList );
        viewerList.setSelectedTextColor( Color.gray );
        viewerList.setPreferredSize( new Dimension(150,750 ) );
        viewerList.append( " Viewers:\n" );
        //add( new JScrollPane( viewerList ), BorderLayout.EAST );
        con.runChat();
    }
}
