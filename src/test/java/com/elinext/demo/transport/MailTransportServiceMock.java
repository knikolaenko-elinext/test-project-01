package com.elinext.demo.transport;

import java.util.Random;

import com.elinext.demo.UserModel;

public class MailTransportServiceMock implements MailTransportService {	
	@Override
	public void notifyUser(UserModel user) {
		try {
			Thread.sleep(new Random().nextInt(500));
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		System.out.println(String.format("<< Message has been sent to %s", user));
	}
}
