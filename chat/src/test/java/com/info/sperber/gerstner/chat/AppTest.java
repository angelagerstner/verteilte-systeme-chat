package com.info.sperber.gerstner.chat;

import java.util.Date;

import com.info.sperber.gerstner.chat.clients.ChatMessagePeriod;
import com.info.sperber.gerstner.chat.util.DateUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        Date date = new Date(2013,2,12,4,4,4);
        Date allDate = DateUtil.getDateFromChatMessagePeriod(ChatMessagePeriod.ALL);
        Date weekDate = DateUtil.getDateFromChatMessagePeriod(ChatMessagePeriod.WEEK);
        Date dayDate = DateUtil.getDateFromChatMessagePeriod(ChatMessagePeriod.DAY);
        Date hourDate = DateUtil.getDateFromChatMessagePeriod(ChatMessagePeriod.HOUR);
    }
}
