package com.elinext.demo;

import java.security.InvalidParameterException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Assert;
import org.junit.Test;

import com.elinext.demo.transport.MailTransportServiceMock;

public class SendEmailTasksConsumerTest {
	private BlockingQueue<String> tasksTransferQueue = new LinkedBlockingQueue<>();
	private AtomicLong executedTasksCounter = new AtomicLong();
	private SendEmailTasksConsumer testable = new SendEmailTasksConsumer(tasksTransferQueue, new MailTransportServiceMock(), executedTasksCounter);
	
	@Test
	public void parseUserTest() {
		UserModel user = testable.parseUser("9,Earl Fuller,efuller8@nature.com,true");
		
		Assert.assertEquals(9, user.id);
		Assert.assertEquals("Earl Fuller", user.name);
		Assert.assertEquals("efuller8@nature.com", user.email);
		Assert.assertEquals(true, user.isActivated);
	}
	
	@Test(expected=InvalidParameterException.class)
	public void parseInvalidUserTest() {
		testable.parseUser("abc,Earl Fuller,efuller8@nature.com,Y");
	}
	
	@Test
	public void needToSendEmailTest(){
		Assert.assertEquals(true, testable.needToSendEmail(testable.parseUser("9,Earl Fuller,efuller8@nature.com,true")));
		Assert.assertEquals(false, testable.needToSendEmail(testable.parseUser("9,Earl Fuller,efuller8@nature.com,false")));
	}
}
