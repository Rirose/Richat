package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Rirose on 5/13/2017.
 * New Panel for getting Channel Name
 */
public class newPanel extends JPanel {
    public newPanel( final JTabbedPane p, final String name, final String pass ){
        JPanel channelPanel = new JPanel();
        channelPanel.setLayout( new FlowLayout() );
        styleSheet sheet = new styleSheet();
        sheet.setStyle( channelPanel );

        setLayout( new BorderLayout() );
        setPreferredSize( new Dimension(1000,800) );

        JLabel label = new JLabel();
        label.setText( "Channel Name: " );
        JTextField field = new JTextField();
        field.setEditable( true );
        field.addActionListener(
                new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        chatPanel panel = new chatPanel( e.getActionCommand().toLowerCase(), name, pass );
                        int index = p.getSelectedIndex();
                        p.removeTabAt( index );
                        p.insertTab( e.getActionCommand(),null,panel,null, index );
                        p.setSelectedIndex( index );
                    }
                }
        );
        sheet.setStyle( label );
        field.setPreferredSize( new Dimension(100,20 ) );

        JLabel msg = new JLabel("Welcome to RiChat. " );
        JLabel msg2 = new JLabel("Please Enter Channel Name!" );

        sheet.setStyle( msg );
        sheet.setStyle( msg2 );

        channelPanel.add( label );
        channelPanel.add( field );
        channelPanel.setPreferredSize( new Dimension( getWidth(),450 ) );

        JPanel msgP = new JPanel();
        sheet.setStyle( msgP );

        JPanel space = new JPanel();
        sheet.setStyle( space );
        space.setPreferredSize( new Dimension( getWidth(),250 ) );

        msgP.setLayout( new FlowLayout() );
        msgP.setPreferredSize( new Dimension( getWidth(),100 ) );

        msgP.add( msg );
        msgP.add( msg2 );

        add( space, BorderLayout.NORTH );
        add( msgP, BorderLayout.CENTER );
        add( channelPanel, BorderLayout.SOUTH );
    }
}
