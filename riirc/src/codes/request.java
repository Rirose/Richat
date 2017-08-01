package codes;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Rirose on 5/19/2017.
 * GET methods for Twitch Api
 */
public class request implements  Runnable{
    private messageParsing parser;
    private String clientID = "5vf2ed47wt3a2kovherdfphw2eimyn";
    private String roomID;
    private String[] emoteSet = new String[5];
    private int k;
    public request(messageParsing parser){
        this.parser = parser;
        k=0;
    }
    private org.json.JSONObject requestSubBadge() {
        try {
            String url = "https://badges.twitch.tv/v1/badges/channels/" + roomID + "/display";
            URL obj = new URL(url);
            HttpURLConnection emoteCon = (HttpURLConnection) obj.openConnection();
            emoteCon.setRequestMethod("GET");
            emoteCon.setRequestProperty("Client-ID", clientID);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(emoteCon.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject e = new JSONObject(response.toString().replaceAll(" ",""));
            return e;
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject requestBadge() {
        try {
            String url = "https://badges.twitch.tv/v1/badges/global/display";
            URL obj = new URL(url);
            HttpURLConnection emoteCon = (HttpURLConnection) obj.openConnection();
            emoteCon.setRequestMethod("GET");
            emoteCon.setRequestProperty("Client-ID", clientID);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(emoteCon.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject e = new JSONObject(response.toString().replaceAll(" ",""));
            return e;
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject requestEmote(int i) {
        try {
            String url = "https://api.twitch.tv/kraken/chat/emoticon_images?emotesets=" + emoteSet[i];
            URL obj = new URL(url);
            HttpURLConnection emoteCon = (HttpURLConnection) obj.openConnection();
            emoteCon.setRequestMethod("GET");
            emoteCon.setRequestProperty("Client-ID", clientID);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(emoteCon.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject e = new JSONObject(response.toString().replaceAll(" ",""));
            return e;
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void run() {
        parser.setSubBadges(requestSubBadge());
        parser.setBadges(requestBadge());
        for(int i=0;i<k;i++){
            parser.setEmotes(i,requestEmote(i));
        }
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public void setEmoteSet(String i) {
        this.emoteSet[k] = i;
        k++;
    }
}
