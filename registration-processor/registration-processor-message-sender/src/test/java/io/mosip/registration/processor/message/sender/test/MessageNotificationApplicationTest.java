package io.mosip.registration.processor.message.sender.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MessageNotificationApplicationTest extends TestCase {

	public MessageNotificationApplicationTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(MessageNotificationApplicationTest.class);
	}

	public void testApp() {
		assertTrue(true);
	}
}
