package com.info.sperber.gerstner.chat.http;

import java.util.HashMap;
import java.util.LinkedList;

import org.restlet.security.LocalVerifier;

import com.info.sperber.gerstner.chat.User;
import com.info.sperber.gerstner.chat.util.PersistanceUtil;

public class UserVerifier extends LocalVerifier {

	HashMap<String, char[]> authenticatedUsers = null;
	
	public UserVerifier() {
		authenticatedUsers = new HashMap<String, char[]>();
	}
	
	@Override
	public char[] getLocalSecret(String identifier) {
		if (!authenticatedUsers.containsKey(identifier)){
			LinkedList<User> users = PersistanceUtil.getInstance().getUserWithEmail(identifier);
	    	if (users.size() > 0) {
	    		User user = users.get(0);
	    		authenticatedUsers.put(identifier, user.getPassword().toCharArray());
	    	}
		}
		return authenticatedUsers.get(identifier);
	}
}
