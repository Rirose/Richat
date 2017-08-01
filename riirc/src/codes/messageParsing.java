package codes;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;

/**
 * Created by Rirose on 5/18/2017.
 * parser for Twitch Api
 */
public class messageParsing {
    private String[] defColors = { "#FF0000", "#0000FF", "#00FF00", "#B22222", "#FF7F50", "#9ACD32", "#FF4500", "#2E8B57",
                                "#DAA520", "#D2691E", "#5F9EA0", "#1E90FF", "#FF69B4", "#8A2BE2", "#00FF7F" };
    private connection con;
    private request req;
    private ExecutorService threadExecutor;
    private JSONObject[] emotes = new JSONObject[5];
    private String[] emoteSet = new String[5];
    private JSONObject subBadges;
    private JSONObject badges;

    public messageParsing( connection con ){
        this.con = con;
        threadExecutor = Executors.newCachedThreadPool();
    }

    public String[] parseMessage(String text) {
        //System.out.println(text+" >:"); //for debugging

        String[] message = new String[4];
        message[0] = "";
        message[1] = "";
        message[2] = "";
        message[3] = "";

        String[] parse = text.split("tmi.twitch.tv ");

        if(parse.length==1){
            message[1] = "";
        }
        else if( parse[1].startsWith( "GLOBALUSERSTATE" ) ) {
            String[] emoteParses = text.split("emote-sets=");
            String[] emoteParse = emoteParses[1].split(";");
            String[] emote = emoteParse[0].split(",");
            for(int i=0; i < emote.length; i++ ) {
                String e = emote[i];
                emoteSet[i] = e;
                req.setEmoteSet(e);
                //emotes[i] = req.requestEmote(e); //without threads
            }
            String[] nickParse = text.split("display-name=");
            String[] nick = nickParse[1].split(";");
            if(!nick[0].equals("")) {
                con.setNick(nick[0]);
            }
            else {
                nick[0] = con.getNick();
            }
            String[] colorParse = text.split("color=");
            String[] color = colorParse[1].split(";");
            if( color[0].equals( "" ) ) {
                int n = nick[0].charAt(0) + nick[0].charAt( nick[0].length() - 1 );
                color[0] = defColors[ n % defColors.length ];
            }
            con.getDoc().addStyle( nick[0], con.getDoc().getStyle("default" ) );
            con.getDoc().getStyle( nick[0] ).addAttribute( StyleConstants.Foreground, Color.decode( color[0] ) );
            con.getDoc().getStyle( nick[0] ).addAttribute( StyleConstants.FontSize, 14 );
        }
        else if( parse[1].startsWith( "USERSTATE" ) ) {
            String[] badgeParse = text.split("badges=");
            String[] badges = badgeParse[1].split(";");
            con.setUserBadge( badges[0] );
        }
        else if( parse[1].startsWith( "ROOMSTATE" ) ) {
            String[] roomParse = text.split("room-id=");
            String[] room = roomParse[1].split(";");
            req.setRoomID( room[0] );
            //subBadges = req.requestSubBadge(room[0]); //without threads
            threadExecutor.execute( req );
        }
        else if( parse[1].startsWith( "USERNOTICE" ) ) {
            String[] prs = text.split("system-msg=");
            String[] msg = prs[1].split(";");
            message[1] += msg[0].replaceAll( Matcher.quoteReplacement("\\s")," " );
        }
        else if(parse[1].startsWith( "NOTICE" ) ) {
            message[1] += getMsg( parse[1] );
        }
        else if(parse[1].startsWith( "CLEARCHAT" ) ) {
            ban(parse[1]);
        }
        else if(parse[1].startsWith("001")) {
            message[1] += "Connected";
        }
        else if( parse[1].startsWith( "PRIVMSG" ) ) {
            if( text.startsWith( ":twitchnotify" ) ) {
                message[1] = getMsg( parse[1] );
            }
            else {
                String[] nickParse = text.split("display-name=");
                String[] nick = nickParse[1].split(";");
                if (nick[0].equals("")) {
                    String[] prs = text.split("user-type= :");
                    nick = prs[1].split("!");
                }
                message[0] += nick[0];

                String[] colorParse = text.split("color=");
                String[] color = colorParse[1].split(";");
                if (color[0].equals("")) {
                    int n = nick[0].charAt(0) + nick[0].charAt(nick[0].length() - 1);
                    color[0] = defColors[n % defColors.length];
                }
                con.getDoc().addStyle(nick[0], con.getDoc().getStyle("default"));
                con.getDoc().getStyle(nick[0]).addAttribute(StyleConstants.Foreground, Color.decode(color[0]));
                con.getDoc().getStyle(nick[0]).addAttribute(StyleConstants.FontSize, 14);
                message[1] += getMsg(parse[1]);

                String[] badgeParse = text.split("badges=");
                String[] badges = badgeParse[1].split(";");
                message[2] += badges[0];

                String[] emoteParse = text.split("emotes=");
                String[] emotes = emoteParse[1].split(";");
                message[3] += emotes[0];
            }
        }
        else if( parse[1].startsWith( "JOIN" ) ) {
            if( text.startsWith( ":" + con.getNick().toLowerCase() ) )
            message[1] = "Connection successful you can start chatting.";
        }
        //System.out.println( message[0] + message[1] ); //for debugging
        //System.out.println( "" );
        return message;
    }

    private String getMsg( String text ) {
        String r = text.substring( text.indexOf(":") + 1 );
        if(r.startsWith("\u0001ACTION")){
            String[] prs = r.split("\u0001ACTION ");
            String[] prs2 = prs[1].split("\u0001");
            r = prs2[0];
        }

        return r;
    }

    private void ban( final String tex ){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    String[] ban = con.getDoc().getText(0, con.getDoc().getLength()).split("\r\n");
                    for (int i = 0; i < ban.length; i++) {
                        String ban2 = ban[i];
                        while (ban2.startsWith(" ")) {
                           ban2 = ban2.substring(1);
                        }

                        if (ban2.toLowerCase().startsWith(getMsg(tex)+": ")) {
                            int off = 0;
                            for (int j = 0; j < i; j++)
                                off += ban[j].length() + 2;
                            off += ban[i].indexOf(':') + 2;
                            int index = ban[i].indexOf(':')+2;
                            if (index >= 0) {
                                con.getDoc().remove(off , ban[i].substring(index).length()+2);
                                con.getDoc().insertString(off, "<message deleted>\r\n", con.getDoc().getStyle("default"));

                                ban = con.getDoc().getText(0, con.getDoc().getLength()).split("\r\n");
                            }
                        }
                    }
                }
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String getEmoteText(String msg){
        String emo = "";
        String[] txt = msg.split(" ");
        boolean secondEmote = true;
        boolean fixString2 = false;
        for (int i = 0; i < emotes.length; i++) {
            if (emotes[i]!=null) {
                if(!secondEmote) {
                    emo += "/";
                    fixString2=true;
                }
                boolean fixString=true;
                JSONArray emoteArray = emotes[i].getJSONObject("emoticon_sets").getJSONArray(emoteSet[i]);
                int ind;
                for (int j = 0; j < emoteArray.length(); j++) {
                    ind = emo.length();
                    emo += emoteArray.getJSONObject(j).getInt("id") + ":";
                    boolean foundEmote=true;
                    for (int k = 0; k < txt.length; k++)
                        if (txt[k].equals( emoteArray.getJSONObject(j).getString("code" ) ) ) {
                            int index=0;
                            for(int z=0;z<k;z++)
                                index+=txt[z].length()+1;
                            emo += index+"-"+(index-1+txt[k].length())+",";
                            foundEmote = false;
                        }
                    if(foundEmote) {
                        emo = emo.substring(0,ind);
                    }
                    else {
                        emo = emo.substring(0, emo.length() - 1);
                        emo += "/";
                        fixString=false;
                    }
                }
                if(!fixString) {
                    emo = emo.substring(0, emo.length() - 1);
                    secondEmote=false;
                    fixString2=false;

                }
                if(fixString2) {
                    emo = emo.substring(0, emo.length() - 1);
                }
            }
        }
        return emo;
    }

    public JSONObject getSubBadges() {
        return subBadges;
    }

    public JSONObject getBadges() {
        return badges;
    }

    public void setBadges(JSONObject badges) {
        this.badges = badges;
    }

    public void setSubBadges(JSONObject subBadges) {
        this.subBadges = subBadges;
    }

    public void setEmotes(int i,JSONObject emotes) {
        this.emotes[i] = emotes;
    }

    public void setReq(request req) {
        this.req = req;
    }
}
