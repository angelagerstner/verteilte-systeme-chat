package com.info.sperber.gerstner.chat.http;

import com.info.sperber.gerstner.chat.Message;
import com.info.sperber.gerstner.chat.util.AuthenticationHelper;
import com.info.sperber.gerstner.chat.util.DateUtil;
import com.info.sperber.gerstner.chat.util.GsonUtil;
import com.info.sperber.gerstner.chat.util.PersistanceUtil;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.data.ChallengeResponse;
import org.restlet.ext.gson.GsonRepresentation;

/**
 *
 * @author angela
 */
public class ChatServerResource extends ServerResource{

    public ChatServerResource() {
    }
    
   @Get
    public GsonRepresentation getChatMessages() throws IOException{
	    String fromDateString = (String) this.getRequestAttributes().get("fromDate");
	    String decodedDateString = URLDecoder.decode(fromDateString, "UTF-8");
	    Date date = DateUtil.getDateFromString(decodedDateString);
	    
	    LinkedList<Message> messages = PersistanceUtil.getInstance().getMessagesFromDatabaseSince(date);
        
    	GsonRepresentation<LinkedList<Message>> rep = GsonUtil.getRepresentationFromMessageList(messages);
        
        return rep;
    } 
    

	@Post
    public synchronized void sendMessage(GsonRepresentation<Message> entity) throws IOException{
        String msg = entity.getText();
        Message message = GsonUtil.getMessageFromJson(msg);
        if (PersistanceUtil.getInstance().persist(message))
        	sendMessageToConnectedServers(message, getChallengeResponse());
    }

	private void sendMessageToConnectedServers(final Message message, final ChallengeResponse challengeResponse) {
		Thread t = new Thread(){
            @Override
            public void run(){
            	ChatApplication chat = (ChatApplication) getApplication();
            	HashSet<Server> connectedServers = chat.getConnectedServers();
            	LinkedList<Message> msgList = new LinkedList<Message>();
            	msgList.add(message);
            	for (Server server : connectedServers){
            		String url = server.getAddress() + "/rest/synchronize";
            		ClientResource client = AuthenticationHelper.createResource(url);
            		GsonRepresentation<LinkedList<Message>> rep = new GsonRepresentation<LinkedList<Message>>(msgList);
            		client.post(rep);
            	}
            }
        };
        t.start();
	}
    
}