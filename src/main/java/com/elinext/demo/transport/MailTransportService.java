package com.elinext.demo.transport;

import com.elinext.demo.UserModel;

public interface MailTransportService {
	void notifyUser(UserModel user);
}