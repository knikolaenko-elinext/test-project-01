package com.elinext.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.elinext.demo.transport.MailTransportService;
import com.elinext.demo.transport.MailTransportServiceImpl;

public class Main {
	private static final int FILE_READER_BUFFER_SIZE = 8124;
	private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

	// Tasks Execution infrastructure

	private final BlockingQueue<String> tasksTransferQueue = new LinkedBlockingQueue<>(2 * THREAD_POOL_SIZE);
	private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

	private final AtomicLong executedTasksCounter = new AtomicLong();

	// External dependencies
	private final File usersStorageFile;
	private final MailTransportService mailTransportService;

	Main(String usersStorageFilePath, MailTransportService mailTransportService) {
		this.usersStorageFile = Paths.get(usersStorageFilePath).toFile();
		this.mailTransportService = mailTransportService;
	}

	long notifyActivatedUsers() throws IOException {
		readUsersStorageAndProcess(usersStorageFile);
		waitUntilAllUsersProcessed();
		return executedTasksCounter.get();
	}

	void readUsersStorageAndProcess(File file) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(file), FILE_READER_BUFFER_SIZE)) {
			String userLine = null;
			while ((userLine = br.readLine()) != null) {
				System.out.println(String.format(">> Read line %s", userLine));
				submitUserLineForProcessing(userLine);
			}
		}
	}

	void submitUserLineForProcessing(String userLine) {
		SendEmailTasksConsumer task = new SendEmailTasksConsumer(tasksTransferQueue, mailTransportService, executedTasksCounter);
		executorService.submit(task);
		try {
			tasksTransferQueue.put(userLine);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	void waitUntilAllUsersProcessed() {
		executorService.shutdown();
		try {
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			throw new InvalidParameterException("A single argument representing users storage file path should be passed.");
		}

		String usersStorageFilePath = args[0];
		MailTransportService mailTransportService = new MailTransportServiceImpl();

		Main main = new Main(usersStorageFilePath, mailTransportService);
		main.notifyActivatedUsers();
	}
}
