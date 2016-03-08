package com.elinext.demo.transport;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.elinext.demo.UserModel;

public class MailTransportServiceImpl implements MailTransportService {

	private Session session;

	public MailTransportServiceImpl() {
		final Properties props = new Properties();
		try {
			props.load(getClass().getResourceAsStream("/mailTransport.properties"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(props.getProperty("username"), props.getProperty("password"));
			}
		});
	}

	@Override
	public void notifyUser(UserModel user){
		try {
			Message message = buildNotoficationMessage(user);
			sendMessage(message);
			System.out.println(String.format("<< Message has been sent to %s", user));
		} catch (MessagingException e){
			throw new RuntimeException(e);
		}
	}
	
	Message buildNotoficationMessage(UserModel user) throws MessagingException, AddressException {
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(user.email));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.email));
		message.setSubject("Notification");
		message.setText(String.format("Notification for %s", user.name));
		return message;
	}
	
	void sendMessage(Message message) throws MessagingException {
		Transport.send(message);
	}
}
