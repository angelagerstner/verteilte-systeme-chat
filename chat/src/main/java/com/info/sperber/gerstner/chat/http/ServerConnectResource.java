package com.info.sperber.gerstner.chat.http;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.ext.gson.GsonRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.info.sperber.gerstner.chat.ChatApp;
import com.info.sperber.gerstner.chat.util.GsonUtil;

public class ServerConnectResource extends ServerResource{
	
	public ServerConnectResource() {
		
	}
	
	@Post
	public GsonRepresentation<HashSet<Server>> connect(GsonRepresentation<Server> serverRep) throws IOException{
		GsonRepresentation<HashSet<Server>> rep = null;
		try {
			ChatApplication chat = (ChatApplication) getApplication();
			Gson gson = new GsonBuilder().create();
	        String serverString = serverRep.getText();
	        Server server = GsonUtil.getServerFromJson(serverString);
	        if (chat.addConnectedServer(server)){
	        	chat.sendConnectionToConnectedServers(server);
	        	chat.synchonizeWith(server);
	        }
	        HashSet<Server> connectedServers = chat.getConnectedServers();
	    	rep = GsonUtil.getRepresentationFromServerSet(connectedServers);
		} catch (Exception ex){
			Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, "Konnte nicht mit Server verbinden", ex);
		}
        
        return rep;
	}

}
