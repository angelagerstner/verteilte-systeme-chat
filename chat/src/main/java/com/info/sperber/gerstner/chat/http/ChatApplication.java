package com.info.sperber.gerstner.chat.http;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.ext.crypto.DigestAuthenticator;
import org.restlet.ext.gson.GsonRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

import com.info.sperber.gerstner.chat.ChatApp;
import com.info.sperber.gerstner.chat.Message;
import com.info.sperber.gerstner.chat.util.AuthenticationHelper;
import com.info.sperber.gerstner.chat.util.DateUtil;
import com.info.sperber.gerstner.chat.util.GsonUtil;
import com.info.sperber.gerstner.chat.util.PersistanceUtil;

public class ChatApplication extends Application {
	private DigestAuthenticator authenticatior;
	private HashSet<Server> connectedServers = null;
	private Server server = null;
	
	public ChatApplication(Server server){
		this.server = server;
		this.connectedServers = new HashSet<Server>();
	}
	
	private DigestAuthenticator createAuthenticator() {
		DigestAuthenticator guard = new DigestAuthenticator(null, "ChatRealm", "chatServerKey");
		UserVerifier verifier = new UserVerifier();
		guard.setWrappedVerifier(verifier);
		return guard;
	}
	
	@Override
    public Restlet createInboundRoot() {
        this.authenticatior = createAuthenticator();
        
        // these resources are only accessible with authentification
        Router privateResourceRouter = new Router();
        privateResourceRouter.attach("/messages/{fromDate}", ChatServerResource.class);
        privateResourceRouter.attach("/login", UserResource.class);
        authenticatior.setNext(privateResourceRouter);
        
        Router router = new Router();
        router.setDefaultMatchingMode(Template.MODE_STARTS_WITH);
        router.attach("/chat", authenticatior);
        // no need to authenticate here
        router.attach("/serverconnect", ServerConnectResource.class);
        router.attach("/synchronize", ServerMessageResource.class);
        
        return router;
    }

    public boolean authenticate(Request request, Response response) {
        if (!request.getClientInfo().isAuthenticated()) {
            authenticatior.challenge(response, false);
            return false;
        }
        return true;
    }

	public void connectToServer(Server server) {
		String url = server.getAddress() + "/rest/serverconnect";
		ClientResource resource = AuthenticationHelper.createResource(url);
		 
		GsonRepresentation<Server> rep = GsonUtil.getRepresentationFromServer(this.server);
		HashSet<Server> servers = null;
		boolean connectionSuccessful = false;
		try {
			GsonRepresentation<HashSet<Server>> responseRep = resource.post(rep, GsonRepresentation.class);
	        String serverList = responseRep.getText();
	        
	        servers = GsonUtil.getServerSetFromJson(serverList);
			connectionSuccessful = true;
		} catch (Exception ex){
			Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, "Fehler beim Verbindungsaufbau zu Server " + server.getAddress(), ex);
		}
		if (connectionSuccessful){
			connectedServers.add(server);
	        for (Server s : servers){
	        	if (!s.equals(this.server))
	        		connectedServers.add(s);
	        }
		}
	}
	
	public boolean addConnectedServer(Server server){
		if (connectedServers.contains(server))
			return false;
		connectedServers.add(server);
		return true;
	}

	public HashSet<Server> getConnectedServers(){
		return connectedServers;
	}
	
	public Server getServer(){
		return server;
	}

	public void sendConnectionToConnectedServers(Server server) {
		for (Server s : connectedServers){
			if (server != s){
				String url = server.getAddress() + "/rest/serverconnect";
				ClientResource client = AuthenticationHelper.createResource(url);
				GsonRepresentation<Server> rep = GsonUtil.getRepresentationFromServer(server);
				client.post(rep);
			}
		}
	}

	public void synchonizeWith(final Server server) throws IOException {
		Thread t = new Thread(){
            @Override
            public void run(){
            	String url = server.getAddress() + "/rest/synchronize";
            	ClientResource client = new ClientResource(url);
        		
        		// get Messages from other server and persist
        		try {
	            	GsonRepresentation<LinkedList<Message>> messagesRep = null;
	        		messagesRep = client.get(GsonRepresentation.class);
	        		String msgList;
					msgList = messagesRep.getText();
				
	                LinkedList<Message> messages = GsonUtil.getMessageListFromJson(msgList);
	                
	                LinkedList<Message> messagesInDatabase = PersistanceUtil.getInstance().getMessagesFromDatabaseSince(DateUtil.MINIMUN_DATE);
	                for (Message m : messages){
	                	if (!messagesInDatabase.contains(m))
	                		PersistanceUtil.getInstance().persist(m);
	                }
        		
                
	                // send own messages to other server
	                GsonRepresentation<LinkedList<Message>> msgInDBRep = GsonUtil.getRepresentationFromMessageList(messagesInDatabase);
	                client.post(msgInDBRep);
        		} catch (Exception ex){
        			Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, "Fehler beim Synchronsieren mit Server " + server.getAddress(), ex);
        		}
				
            }
        };
        t.start();
	}
}
