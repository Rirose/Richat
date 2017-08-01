package GUI;


import codes.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Rirose on 5/14/2017.
 * Login Frame
 */
public class loginFrame extends JFrame {
    private String nickName;
    private styleSheet sheet = new styleSheet();

    public loginFrame(){
        setTitle( "RiChat | Please Login" );
        setLayout( new BorderLayout() );
        JLabel label = new JLabel();
        label.setText( "Twitch Login Name:" );
        sheet.setStyle( label );

        final JTextField nick = new JTextField();
        final JButton enter = new JButton();

        nick.setPreferredSize( new Dimension(125,20 ) );
        nick.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
               actionEnter( nick );
               nick.setEnabled( false );
               enter.setEnabled( false );
            }
        });

        enter.setPreferredSize( new Dimension(125,20 ) );
        enter.setText( "Login" );
        enter.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ev ) {
                actionEnter( nick );
                nick.setEnabled( false );
                enter.setEnabled( false );
            }
        });

        JPanel panel = new JPanel();
        sheet.setStyle( panel );
        panel.setLayout( new BorderLayout() );

        JPanel loginPanel = new JPanel();
        sheet.setStyle( loginPanel );
        loginPanel.add( label );
        loginPanel.add( nick );

        final JPanel space = new JPanel();
        sheet.setStyle( space );
        space.setPreferredSize( new Dimension(280,75 ) );

        final JPanel space2 = new JPanel();
        sheet.setStyle( space2 );
        space2.setPreferredSize( new Dimension(280,75 ) );

        JPanel buttonPanel = new JPanel();
        sheet.setStyle( buttonPanel );
        buttonPanel.add( enter );

        JPanel pan = new JPanel();
        sheet.setStyle( pan );
        pan.setPreferredSize( new Dimension(280,100 ) );
        pan.setLayout( new BorderLayout() );
        pan.add( loginPanel, BorderLayout.NORTH );
        pan.add( buttonPanel, BorderLayout.CENTER );

        panel.add( space, BorderLayout.NORTH );
        panel.add( pan, BorderLayout.CENTER );
        panel.add( space2, BorderLayout.SOUTH );

        add( panel, BorderLayout.NORTH );

        setDefaultCloseOperation( EXIT_ON_CLOSE );
        setResizable(false);
        pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        setVisible( true );
    }

    private void actionEnter(JTextField nick){
        if( nick.getText().length() != 0 ) {
            nickName = nick.getText().toLowerCase();
            final String url = "https://api.twitch.tv/kraken/oauth2/authorize?client_id=5vf2ed47wt3a2kovherdfphw2eimyn&redirect_uri=http://richat.surge.sh/richatcon.html&response_type=token&scope=chat_login&force_verify=true";
            try {
                Desktop.getDesktop().browse(new URI(url));   //for default browser
                server s = new server(this, nickName);
                Thread t1 = new Thread( s );
                t1.start();
           }
           catch (IOException ex) {
               ex.printStackTrace();
           } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
