package com.info.sperber.gerstner.chat;

import com.info.sperber.gerstner.chat.http.HttpServer;
import com.info.sperber.gerstner.chat.http.Server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatApp
{   
    public static void main( String[] args ) throws IOException
    {           
    	try {
    		int serverPort = 0;
    		String serverHost = "";
    		if (args.length >= 2){
    			serverHost = args[0];
    			serverPort = Integer.parseInt(args[1]);
    		}
    		
    		Server connectedServer = null;
    		if(args.length >= 4) {
    			try {
    				String connectedServerHost = args[2];
    				int connectedServerPort = Integer.parseInt(args[3]);
    				connectedServer = new Server(connectedServerHost, connectedServerPort);
    			} catch (NumberFormatException ex){
    				Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, null, ex);
    			}
    		}
    		
            HttpServer httpServer = new HttpServer(serverHost, serverPort);
            httpServer.start();
            if (null != connectedServer){
            	httpServer.connectToServer(connectedServer);
            }
        } catch (Exception ex) {
            Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, "Fehler beim Starten des Servers.", ex);
        }
    }
}