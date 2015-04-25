package com.info.sperber.gerstner.chat.http;

import java.io.IOException;
import java.util.LinkedList;

import org.restlet.ext.gson.GsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.info.sperber.gerstner.chat.Message;
import com.info.sperber.gerstner.chat.util.DateUtil;
import com.info.sperber.gerstner.chat.util.GsonUtil;
import com.info.sperber.gerstner.chat.util.PersistanceUtil;

public class ServerMessageResource extends ServerResource{
	
	public ServerMessageResource() {
		
	}
	
	@Get
	public GsonRepresentation<LinkedList<Message>> getAllMessages(){
		LinkedList<Message> messages = PersistanceUtil.getInstance().getMessagesFromDatabaseSince(DateUtil.MINIMUN_DATE);
		GsonRepresentation<LinkedList<Message>> rep = GsonUtil.getRepresentationFromMessageList(messages);
        return rep;
	}
	
	@Post
    public synchronized void synchronizeMessages(GsonRepresentation<LinkedList<Message>> entity) throws IOException{
        String msgList = entity.getText();
        LinkedList<Message> messages = GsonUtil.getMessageListFromJson(msgList);
        
        saveMessages(messages);
	}

	private void saveMessages(LinkedList<Message> messages) {
		LinkedList<Message> messagesInDatabase = PersistanceUtil.getInstance().getMessagesFromDatabaseSince(DateUtil.MINIMUN_DATE);
		
		for (Message m : messages){
			if (!messagesInDatabase.contains(m))
				PersistanceUtil.getInstance().persist(m);
        }
	}
}
