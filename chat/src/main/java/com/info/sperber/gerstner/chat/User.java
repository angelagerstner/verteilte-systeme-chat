package com.info.sperber.gerstner.chat;

import java.util.Set;

import javax.persistence.*;

@Entity
public class User{
	@Id
    private String email;
    private String userName;
    private String password;
    
    @OneToMany(mappedBy="sender")
    private transient Set<Message> messages;

    public User() {}
    
	public User(String email, String userName) {
		super();
		this.email = email;
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}
    
}
