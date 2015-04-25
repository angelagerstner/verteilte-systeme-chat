package com.info.sperber.gerstner.chat.util;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import com.info.sperber.gerstner.chat.ChatApp;
import com.info.sperber.gerstner.chat.Message;
import com.info.sperber.gerstner.chat.User;

public class PersistanceUtil {
	
	EntityManagerFactory emf;

	private PersistanceUtil(){
		emf = Persistence.createEntityManagerFactory("emf");
	}
	
	private static final class InstanceHolder {
		static final PersistanceUtil INSTANCE = new PersistanceUtil();
	}
	
	public static PersistanceUtil getInstance(){
		return InstanceHolder.INSTANCE;
	}
	
	public boolean persist(Message m){
		try {
			EntityManager em = getEntityManager();
	    	em.getTransaction().begin();
	    	m.setId(null);
	    	em.persist(m);
	    	em.getTransaction().commit();
	    	em.close();
		} catch (Exception ex){
			Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, "Nachricht konnte nicht persistiert werden.", ex);
			return false;
		}
    	
    	return true;
	}
	
	public EntityManager getEntityManager(){
		return emf.createEntityManager();
	}

	public TypedQuery<Message> createGetMessagesQuery(EntityManager em, Date date) {
    	String queryString = "from Message where sentDate > :date";
    	queryString += " order by sentDate ASC";
    	
    	TypedQuery<Message> query = em.createQuery(queryString, Message.class ).setParameter("date", date);
       	return query;
    }	
	
	public LinkedList<Message> getMessagesFromDatabaseSince(Date date) {
		LinkedList<Message> list = new LinkedList<Message>();
		try {
	    	EntityManager em = getEntityManager();
		    TypedQuery<Message> query = createGetMessagesQuery(em, date);	    
		    
		    em.getTransaction().begin();
	    	List<Message> result = query.getResultList();
	    	list = new LinkedList<Message>(result);
	    	em.getTransaction().commit();
	    	em.close();
	    } catch (Exception ex){
			Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, "Nachrichten konnten nicht geladen werden.", ex);
		}
        
    	return list;
	}
	
	public LinkedList<User> getUserWithEmail(String identifier){
		List<User> result = null;
		try {
			EntityManager em = getEntityManager();
	  		em.getTransaction().begin();
	  		result = em.createQuery( "from User where email = :email", User.class ).setParameter("email", identifier).setMaxResults(1).getResultList();
	    	em.getTransaction().commit();
	    	em.close();
		} catch (Exception ex){
			Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, "Benutzer konnten nicht geladen werden.", ex);
		}  
    	
		LinkedList<User> users = new LinkedList<User>(result);
		
    	return users;
	}
}
