package com.info.sperber.gerstner.chat.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.data.ChallengeRequest;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.ext.gson.GsonRepresentation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.info.sperber.gerstner.chat.ChatApp;
import com.info.sperber.gerstner.chat.User;

public class AuthenticationHelper {

	public static ClientResource createResource(String url){
		ClientResource client = new ClientResource(url);
		return client;
	}
	
    public static ClientResource createResourceWithAuth(String url, String identifier, String encodedPassword){
    	ClientResource resource = new ClientResource(url);
    	resource.setChallengeResponse(ChallengeScheme.HTTP_DIGEST, identifier, encodedPassword);
    	try {
    		resource.get();
    	} catch (ResourceException re) {
    	}
    	ChallengeRequest c1 = null;
    	for (ChallengeRequest challengeRequest : resource.getChallengeRequests()){
    		if (ChallengeScheme.HTTP_DIGEST.equals(challengeRequest.getScheme())){
    			c1 = challengeRequest;
    			break;
    		}
    	}
    	
    	ChallengeResponse challengeResponse = new ChallengeResponse(c1, resource.getResponse(), identifier, encodedPassword);
    	resource.setChallengeResponse(challengeResponse);
    	return resource;
    }
    
    public static User getUser(ClientResource loginResource, String email){
    	StringRepresentation emailRep = new StringRepresentation(email);
		User user = null;
    	try {
			GsonRepresentation<User> userRep = loginResource.post(emailRep, GsonRepresentation.class);
			Gson gson=  new GsonBuilder().create();
			String userString = userRep.getText();
			user = gson.fromJson(userString, User.class);
    	} catch (Exception ex){
			Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, "Fehler beim Laden des Benutzers.", ex);
		}
    	return user;
    }
}
