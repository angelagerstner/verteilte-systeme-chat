package com.info.sperber.gerstner.chat.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.info.sperber.gerstner.chat.ChatApp;
import com.info.sperber.gerstner.chat.clients.ChatMessagePeriod;

public class DateUtil {
	
	public static String CHAT_DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	public static Date MINIMUN_DATE = new Date(0);
	
	public static String getDateForGetParameter(Date date){
		DateFormat formatter = new SimpleDateFormat(CHAT_DATEFORMAT);
		String dateString = formatter.format(date);
		
		return dateString;
	}
	
	public static Date getDateFromString(String dateString){
		DateFormat formatter = new SimpleDateFormat(CHAT_DATEFORMAT);
		Date date = null;
		try {
			date = formatter.parse(dateString);
		} catch (ParseException ex) {
			Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, "Datum konnte nicht geparst werden: " + dateString, ex);
		}
		return date;
	}
	
    public static Date getDateFromChatMessagePeriod(ChatMessagePeriod chatMessagePeriod) {
		Date date = new Date();
		switch (chatMessagePeriod){
		case ALL:
			date = new Date(0);
			break;
		case DAY:
			date.setTime(date.getTime() - 24*3600*1000);
			break;
		case HOUR:
			date.setTime(date.getTime() - 3600*1000);
			break;
		case WEEK:
			date.setTime(date.getTime() - 7*24*3600*1000);
			break;
		default:
			Logger.getLogger(ChatApp.class.getName()).log(Level.SEVERE, null, "error");
			break;
		}
		return date;
	}
}
