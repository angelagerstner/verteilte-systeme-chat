package com.info.sperber.gerstner.chat.clients;

import com.info.sperber.gerstner.chat.Message;
import com.info.sperber.gerstner.chat.User;
import com.info.sperber.gerstner.chat.http.Server;
import com.info.sperber.gerstner.chat.util.AuthenticationHelper;
import com.info.sperber.gerstner.chat.util.DateUtil;
import com.info.sperber.gerstner.chat.util.GsonUtil;
import com.info.sperber.gerstner.chat.util.PasswordUtil;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.ext.gson.GsonRepresentation;
import org.restlet.resource.ClientResource;
/**
 *
 * @author angela
 */
public class HttpClient{
    ClientResource client = null;
    ClientResource sendResource = null;
    ClientResource loginResource = null;
    ChatClientFrame ccFrame = null;
    LoginFrame loginFrame = null;
    private User user = null;
    private Server server = null;
    String url = "";
    volatile Date fromDate = new Date(0);
	private ChatMessagePeriod chatMessagePeriod = ChatMessagePeriod.ALL;
 
    public static void main(String[] args) {
    	String host = "";
		Integer port = 0;
		if(args.length >= 2) {
			try {
				host = args[0];
				port = Integer.parseInt(args[1]);
				Server server = new Server(host, port);
		        HttpClient client = new HttpClient(server);
		        client.start();
			} catch (NumberFormatException ex){
			 	Logger.getLogger(HttpClient.class.getName()).log(Level.SEVERE, "Fehler beim Starten des Clients.", ex);
			}
		}
    }

    public HttpClient(Server server) {
    	this.server = server;
    	this.url = server.getAddress() + "/rest/chat";
    }
    
    public void start(){
    	showLogin();
    }
    
	public void authenticateUser(String email, char[] password) {
		boolean loginCorrect = false;
		User user = null;
		String passwordString = String.valueOf(password);
		String encodedPassword = PasswordUtil.getSHA256SecurePassword(passwordString);
		if (null != email && !"".equals(email) && null != password && 0 < password.length)
		{
			createResources(email, encodedPassword);
			user = AuthenticationHelper.getUser(loginResource, email);
			if (null != user){
				loginCorrect = true;
			}
		}
		
		if (loginCorrect){
			this.user = user;
			showChat();
		} else {
			loginFrame.showIncorrectEmailPasswordCombinationPopup();
		}
	}
	
    public boolean sendMessage(String message){
    	Message m = new Message(getUser(), message, new Date());
        GsonRepresentation<Message> rep = new GsonRepresentation<Message>(m);
        try {
          	client.post(rep);
        } catch (Exception ex){
        	Logger.getLogger(HttpClient.class.getName()).log(Level.SEVERE, "Nachricht konnte nicht gesendet werden.", ex);
        }
        getAndShowMessages();
        return true;
    }

	private void poll(int seconds){
        final long milliSeconds = seconds*1000;
        Thread t = new Thread(){
            @Override
            public void run(){
                while (true){
                    try {
                        getAndShowMessages();
                        Thread.sleep(milliSeconds);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(HttpClient.class.getName()).log(Level.SEVERE, "Fehler beim Abrufen der Nachrichten.", ex);
                    }
                }
            }
        };
        t.start();
    }
	
	private synchronized void getAndShowMessages(){
		try {
			Date newFromDate = new Date();
			
			Date date = DateUtil.getDateFromChatMessagePeriod(chatMessagePeriod);
			String dateString = DateUtil.getDateForGetParameter(date);
	    	String getUrl = url + "/messages/" + dateString;
	    	client = AuthenticationHelper.createResourceWithAuth(getUrl, user.getEmail(), user.getPassword());
	    	fromDate = newFromDate;
			GsonRepresentation<LinkedList<Message>> rep = client.get(GsonRepresentation.class);
	        String msgList = rep.getText();
	        
	        LinkedList<Message> messages = GsonUtil.getMessageListFromJson(msgList);
	        ccFrame.showMessages(messages);
		} catch (IOException ex) {
			Logger.getLogger(HttpClient.class.getName()).log(Level.SEVERE, "Fehler beim Abrufen der Nachrichten.", ex);
        }
	}

	private void showChat(){
    	loginFrame.hide();
    	ccFrame = new ChatClientFrame(this);
        poll(5);
    }

	private void createResources(String identifier, String password) {
		sendResource = AuthenticationHelper.createResourceWithAuth(url, identifier, password);
    	loginResource = AuthenticationHelper.createResourceWithAuth(url + "/login", identifier, password);
	}
    
    private void showLogin() {
    	loginFrame = new LoginFrame(this);
    	loginFrame.show();
	}
    
    public void setUser(User user){
    	this.user = user;
    }
    
    public User getUser(){
    	return this.user;
    }
    
    public Server getServer(){
    	return this.server;
    }

	public void setChatMessagePeriod(ChatMessagePeriod period) {
		this.chatMessagePeriod = period;
		getAndShowMessages();
	}
}