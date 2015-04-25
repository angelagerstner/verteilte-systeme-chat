package com.info.sperber.gerstner.chat.util;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedList;

import org.restlet.ext.gson.GsonRepresentation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.info.sperber.gerstner.chat.Message;
import com.info.sperber.gerstner.chat.User;
import com.info.sperber.gerstner.chat.http.Server;

public class GsonUtil {

	public static GsonBuilder builder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).setDateFormat(DateUtil.CHAT_DATEFORMAT);
	
	public static Gson gson = builder.create();
	
	public static GsonRepresentation<LinkedList<Message>> getRepresentationFromMessageList(LinkedList<Message> messages) {
		GsonRepresentation<LinkedList<Message>> rep = new GsonRepresentation<LinkedList<Message>>(messages);
        rep.setBuilder(builder);
        
        return rep;
	}
	
	public static GsonRepresentation<HashSet<Server>> getRepresentationFromServerSet(HashSet<Server> servers){
		GsonRepresentation<HashSet<Server>> rep = new GsonRepresentation<HashSet<Server>>(servers);
		rep.setBuilder(builder);
		
		return rep;
	}
	
	public static GsonRepresentation<User> getRepresentationFromUser(User user) {
		GsonRepresentation<User> rep = new GsonRepresentation<User>(user);
		return rep;
	}
	
	public static GsonRepresentation<Server> getRepresentationFromServer(Server server){
		GsonRepresentation<Server> rep = new GsonRepresentation<Server>(server);
		return rep;
	}
	
	public static LinkedList<Message> getMessageListFromJson(String json){
		Type listType = new TypeToken<LinkedList<Message>>() {}.getType();
        LinkedList<Message> messages = gson.fromJson(json, listType);
        return messages;
	}
	
	public static HashSet<Server> getServerSetFromJson(String json){
		Type listType = new TypeToken<HashSet<Server>>() {}.getType();
		HashSet<Server> servers = gson.fromJson(json, listType);
		return servers;
	}
	
	public static Message getMessageFromJson(String json){
		Message message = gson.fromJson(json, Message.class);
		return message;
	}
	
	public static Server getServerFromJson(String json){
		Server server = gson.fromJson(json, Server.class);
		return server;
	}
}
