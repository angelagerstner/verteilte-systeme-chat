package com.info.sperber.gerstner.chat.clients;

public enum ChatMessagePeriod {
	ALL("Alle"),
	WEEK("Letzte Woche"),
	DAY("Letzter Tag"),
	HOUR("Letzte Stunde");
	
	private final String text; 
	
	private ChatMessagePeriod(String text){
		this.text = text;
	}
	
	@Override
	public String toString(){
		return text;
	}
}
