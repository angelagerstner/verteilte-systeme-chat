package com.info.sperber.gerstner.chat.http;

import org.restlet.Component;
import org.restlet.data.Protocol;

import com.info.sperber.gerstner.chat.util.PersistanceUtil;


public class HttpServer {

    private Component component = null;
    private ChatApplication chat = null;
    private Server server = null;
    
    public HttpServer(String host, int port) {
    	server = new Server(host, port);
    	chat = new ChatApplication(server);
    	component = new Component();
    	component.getServers().add(Protocol.HTTP, port);
    	component.getDefaultHost().attach("/rest", chat);
    }
    
    public void start() throws Exception{
    	if (null != component){
    		component.start();
    		PersistanceUtil.getInstance().getEntityManager();
    	}
    }

	public void connectToServer(Server server) {
		chat.connectToServer(server);
	}
    
}