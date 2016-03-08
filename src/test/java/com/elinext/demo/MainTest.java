package com.elinext.demo;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.elinext.demo.transport.MailTransportServiceMock;

public class MainTest {
	private Main testable = new Main("data/MOCK_DATA.csv", new MailTransportServiceMock());
	
	@Test
	public void testThatNumberOfProcessedUserMatchesNumberOfLinesInFile() throws IOException{
		Assert.assertEquals(1000, testable.notifyActivatedUsers());
	}
}
