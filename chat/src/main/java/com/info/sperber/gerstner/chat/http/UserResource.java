package com.info.sperber.gerstner.chat.http;

import java.util.List;

import org.restlet.ext.gson.GsonRepresentation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.info.sperber.gerstner.chat.User;
import com.info.sperber.gerstner.chat.util.GsonUtil;
import com.info.sperber.gerstner.chat.util.PersistanceUtil;

public class UserResource extends ServerResource {

	// throws ResourceException if user can not be authenticated
    @Post
    public GsonRepresentation<User> doLogin(StringRepresentation identifier) {
        ChatApplication app = (ChatApplication) getApplication();
        if (!app.authenticate(getRequest(), getResponse())) {
            // Not authenticated
            return null;
        }

        // authenticated
        List<User> users = PersistanceUtil.getInstance().getUserWithEmail(identifier.getText());
    	
    	if (users.size() > 0) {
    		User user = users.get(0);
    		GsonRepresentation<User> rep = GsonUtil.getRepresentationFromUser(user);
    		return rep;
    	}
	  	
        return null;
    }     

}
