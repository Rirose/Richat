package codes;

import org.json.JSONObject;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

/**
 * Created by Rirose on 5/8/2017.
 * Connecting to Twitch IRC Server
 */

public class connection implements Runnable {
    private Socket socket;
    private String nick;
    private String pass;
    private String userBadge;
    private String channel;
    private StyledDocument doc;
    private BufferedWriter writer;
    private BufferedReader reader;
    private messageParsing parser;
    private request req;


    public connection( String nick, String pass, String channel, JTextPane chat )  {
        this.nick = nick;
        this.pass = pass;
        userBadge = "";
        this.channel = channel;
        this.doc = chat.getStyledDocument();
        parser = new messageParsing( this );
        req = new request( parser );
        parser.setReq( req );
        doc.addStyle("default", null);
        doc.getStyle("default" ).addAttribute( StyleConstants.Foreground, Color.white );
        doc.getStyle("default" ).addAttribute( StyleConstants.FontSize, 14 );
        doc.addStyle("notice",null);
        doc.getStyle("notice" ).addAttribute( StyleConstants.Foreground, Color.gray );
        doc.getStyle("notice" ).addAttribute( StyleConstants.FontSize, 14 );
    }

    private void connectToServer() throws Exception {
        String server = "irc.chat.twitch.tv";
        socket = new Socket( server, 6667 );
        writer = new BufferedWriter(
                new OutputStreamWriter( socket.getOutputStream() ) );
        reader = new BufferedReader(
                new InputStreamReader( socket.getInputStream() ) );

        writer.write("CAP REQ :twitch.tv/membership\r\n" );
        writer.flush();
        writer.write("CAP REQ :twitch.tv/tags\r\n" );
        writer.flush();
        writer.write("CAP REQ :twitch.tv/commands\r\n" );
        writer.flush();
        writer.write("PASS oauth:" + pass + "\r\n" );
        writer.write("NICK " + nick + "\r\n" );
        writer.flush();

        String line;
        while ( ( line = reader.readLine() ) != null ) {
            line = parser.parseMessage( line )[1];
            if( line.equals("Connected" ) ){
                break;
            }
        }

        Thread t1 = new Thread(this );
        t1.start();
    }

    public void sendMessage( String msg ){
        try {
            writer.write("PRIVMSG " + channel + " :" + msg + "\r\n");
            writer.flush();
            String emo = parser.getEmoteText(msg);
            displayMessage( nick, msg, userBadge,emo );
        }
        catch ( IOException e){
            displayMessage("Error sending message." );
        }
    }

    private void displayMessage( final String nick, final String msg,final String badge, final String emo ) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        try {
                            String[] chat = doc.getText(0, doc.getLength()).split("\r\n");
                            if( chat.length > 250 )
                                doc.remove(0,chat[0].length()+2);
                            if(!badge.equals("")) {
                                String[] badges = badge.split(",");
                                for(int i=0; i<badges.length;i++){
                                    String url="";
                                    String[] prs = badges[i].split("/");
                                    if(badges[i].startsWith( "subscriber" ) ){
                                        JSONObject jsonSubBadges = parser.getSubBadges();
                                        if(jsonSubBadges!=null) {
                                            url = jsonSubBadges.getJSONObject("badge_sets").getJSONObject(prs[0]).getJSONObject("versions")
                                                    .getJSONObject(prs[1]).getString("image_url_1x");
                                        }
                                    }
                                    else {
                                        JSONObject jsonBadges = parser.getBadges();
                                        if(jsonBadges!=null) {
                                            url = jsonBadges.getJSONObject("badge_sets").getJSONObject(prs[0]).getJSONObject("versions")
                                                    .getJSONObject(prs[1]).getString("image_url_1x");
                                        }
                                    }
                                    if(!url.equals("")) {
                                        final SimpleAttributeSet attrs = new SimpleAttributeSet();
                                        StyleConstants.setIcon(attrs, new ImageIcon(new URL(url)));
                                        doc.insertString(doc.getLength(), " ", attrs);
                                    }
                                }
                            }

                            doc.insertString( doc.getLength(), nick, doc.getStyle( nick ) );

                            int len = doc.getLength();
                            doc.insertString( len, ": "+ msg + "\r\n", doc.getStyle("default" ) );

                            if( !emo.equals( "" ) ) {
                                String[] parse = emo.split("/" );
                                for(int i = 0; i < parse.length; i++ ) {
                                    String[] emoteId = parse[i].split(":" );
                                    final SimpleAttributeSet attrs = new SimpleAttributeSet();
                                    String url = "http://static-cdn.jtvnw.net/emoticons/v1/" + emoteId[0] + "/1.0";
                                    StyleConstants.setIcon( attrs,new ImageIcon( new URL ( url ) ) );
                                    String emoteIndex = parse[i].substring( parse[i].indexOf(":") + 1 );
                                    String[] emoteIndexs = emoteIndex.split("," );

                                    for( int j=0; j < emoteIndexs.length; j++ ) {
                                        String[] indexs = emoteIndexs[j].split("-" );
                                        int firstIndex = Integer.parseInt( indexs[0] );
                                        int lastIndex = Integer.parseInt( indexs[1] );
                                        doc.remove(len+2+firstIndex,lastIndex-firstIndex+1);
                                        String text="";
                                        for(int k = 0; k <= lastIndex - firstIndex; k++){
                                            text+=" ";
                                        }
                                        doc.insertString(len+2+firstIndex,text,attrs);
                                    }
                                }
                            }
                        }
                        catch ( BadLocationException e ) {
                            e.printStackTrace();
                        }
                        catch ( MalformedURLException e ) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    private void displayMessage( final String msg ) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        try {
                            String[] chat = doc.getText(0, doc.getLength()).split("\r\n");
                            if( chat.length > 100 )
                                doc.remove(0,chat[0].length()+2);
                            doc.insertString( doc.getLength() , msg + "\r\n", doc.getStyle("notice" ) );
                        }
                        catch ( BadLocationException e ) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    private void use ( String[] text ) {
        if ( !text[1].equals( "" ) ) {
            if( text[0].equals( "" ))
                displayMessage( text[1] );
            else {
                displayMessage( text[0], text[1], text[2], text[3] );
            }
        }
    }

    public void run() {
        try {
            String line;
            writer.write("JOIN " + channel + "\r\n" );
            writer.flush();

            while ( true ) {
                line = reader.readLine();
                if ( line.startsWith( "PING " ) ) {
                    writer.write("PONG " + line.substring(5) + "\r\n" );
                    writer.flush();
                } else {
                    String[] txt = parser.parseMessage( line );
                    use( txt );
                }
            }
        }
        catch( IOException e ){
            e.printStackTrace();
        }
        finally{
            close();
        }
    }

    public void runChat() {
        try {
            connectToServer();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            writer.close();
            reader.close();
            socket.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void setNick( String nick ) {
        this.nick = nick;
    }

    public String getNick() {
        return nick;
    }

    public void setUserBadge( String userBadge ) {
        this.userBadge = userBadge;
    }

    public StyledDocument getDoc(){
        return doc;
    }
}
