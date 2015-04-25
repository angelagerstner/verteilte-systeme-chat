package com.info.sperber.gerstner.chat;

import java.util.Date;
import java.util.UUID;

import javax.persistence.*;

@Entity
public class Message{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
    private String messageID;
    
    
    private String message;
    @ManyToOne
    @JoinColumn(name="email")
    private User sender;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date sentDate = null;
    
    
    public Message(){}

    public Message(User sender, String message, Date sentDate) {
        this.message = message;
        this.sender = sender;
        this.sentDate = sentDate;
        this.messageID = UUID.randomUUID().toString();
    }

    public String getId() {
        return messageID;
    }
    
    public void setId(Integer id){
    	this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (obj instanceof Message){
			Message toCompare = (Message) obj;
			return this.messageID.equals(toCompare.getId());
		}
		return false;
	}
}
