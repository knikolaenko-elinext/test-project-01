package com.elinext.demo;

import java.security.InvalidParameterException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.elinext.demo.transport.MailTransportService;

class SendEmailTasksConsumer implements Callable<Boolean> {
	private static final Pattern USER_LINE_PATTERN = Pattern.compile("(?<id>\\d+),(?<name>[\\w\\s]+),(?<email>[^,]+),(?<isActivated>\\w+)");

	private final BlockingQueue<String> tasksTransferQueue;
	private final MailTransportService mailTransportService;
	private final AtomicLong executedTasksCounter;

	public SendEmailTasksConsumer(BlockingQueue<String> tasksTransferQueue, MailTransportService mailTransportService, AtomicLong executedTasksCounter) {
		this.tasksTransferQueue = tasksTransferQueue;
		this.mailTransportService = mailTransportService;
		this.executedTasksCounter = executedTasksCounter;
	}

	@Override
	public Boolean call() throws Exception {
		try {
			String userLine = tasksTransferQueue.take();
			return processUserLine(userLine);
		} finally {
			executedTasksCounter.incrementAndGet();
		}
	}

	Boolean processUserLine(String userLine) {
		UserModel user = parseUser(userLine);
		if (needToSendEmail(user)) {
			sendEmail(user);
			return true;
		}
		return false;
	}

	UserModel parseUser(String userLine) {
		Matcher userLineMather = USER_LINE_PATTERN.matcher(userLine);
		if (!userLineMather.matches()) {
			throw new InvalidParameterException(String.format("Userline '%s' doesn't match contract pattern", userLine));
		}
		UserModel user = new UserModel(Integer.valueOf(userLineMather.group("id")), userLineMather.group("name"), userLineMather.group("email"),
				Boolean.valueOf(userLineMather.group("isActivated")));
		return user;
	}

	boolean needToSendEmail(UserModel user) {
		return user.isActivated;
	}

	void sendEmail(UserModel user) {
		mailTransportService.notifyUser(user);
	}
}