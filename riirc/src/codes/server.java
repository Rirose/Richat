package codes;

import GUI.mainFrame;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Created by Rirose on 5/19/2017.
 * Getting OAuth Key From Javascript
 */
public class server implements Runnable {
    private JFrame frame;
    private String name;
    private String pass = "";
    private ServerSocket server;
    private BufferedReader in;
    private Socket socket;

    public server ( JFrame frame, String name) {
        this.frame = frame;
        this.name = name;
    }

    private void setServer() {
        try {
            server = new ServerSocket(9050,1, InetAddress.getLoopbackAddress() );
            socket = server.accept();
            in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private void getPass() {
        String line;
        try {
            while ( ( line = in.readLine() ) == null )
                ;
            String[] hash = line.split("%3D");
            String[] hash2 = hash[1].split("%26");
            pass = hash2[0];
            in.close();
            socket.close();
            server.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        setServer();
        getPass();
        if( !pass.equals( "" ) ) {
            mainFrame mframe = new mainFrame( name, pass );
        }
        frame.dispose();
    }
}
