package io.mosip.authentication.common.service.websub;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;

public class BaseIDAWebSubInitializerTest {

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {

	}

	private BaseIDAWebSubInitializer createTestSubject() {
		BaseIDAWebSubInitializer baseIDAWebSubInitializer = new BaseIDAWebSubInitializer() {

			@Override
			protected int doInitSubscriptions() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			protected int doRegisterTopics() {
				// TODO Auto-generated method stub
				return 0;
			}};
			
			ReflectionTestUtils.setField(baseIDAWebSubInitializer, "taskScheduler", Mockito.mock(ThreadPoolTaskScheduler.class));
		return baseIDAWebSubInitializer;
	}

	@Test
	public void testOnApplicationEvent() throws Exception {
		BaseIDAWebSubInitializer testSubject;
		ApplicationReadyEvent event = null;

		// default test
		testSubject = createTestSubject();
		testSubject.onApplicationEvent(event);

	}
	
	@Test
	public void testOnApplicationEventWithDelay() throws Exception {
		BaseIDAWebSubInitializer testSubject;
		ApplicationReadyEvent event = null;

		// default test
		testSubject = createTestSubject();
		ReflectionTestUtils.setField(testSubject, "reSubscriptionDelaySecs", 10);
		testSubject.onApplicationEvent(event);

	}

	@Test
	public void testRegisterTopics() throws Exception {
		BaseIDAWebSubInitializer testSubject;
		boolean result;

		// default test
		testSubject = createTestSubject();
		result = ReflectionTestUtils.invokeMethod(testSubject, "registerTopics");
		assertTrue(result);
	}
	
	@Test
	public void testRegisterTopicsException() throws Exception {
		BaseIDAWebSubInitializer testSubject;
		boolean result;

		// default test
		testSubject = createExceptionTestSubject();
		result = ReflectionTestUtils.invokeMethod(testSubject, "registerTopics");
		assertFalse(result);
	}

	@Test
	public void testScheduleRetrySubscriptions() throws Exception {
		BaseIDAWebSubInitializer testSubject;

		// default test
		testSubject = createTestSubject();
		ReflectionTestUtils.invokeMethod(testSubject, "scheduleRetrySubscriptions");

	}

	@Test
	public void testInitSubsriptions() throws Exception {
		BaseIDAWebSubInitializer testSubject;
		boolean result;

		// default test
		testSubject = createTestSubject();
		result = ReflectionTestUtils.invokeMethod(testSubject, "initSubsriptions");
		assertTrue(result);
	}
	
	@Test
	public void testInitSubsriptionsException() throws Exception {
		BaseIDAWebSubInitializer testSubject;
		boolean result;

		// default test
		testSubject = createExceptionTestSubject();
		result = ReflectionTestUtils.invokeMethod(testSubject, "initSubsriptions");
		assertFalse(result);
	}

	private BaseIDAWebSubInitializer createExceptionTestSubject() {
		BaseIDAWebSubInitializer baseIDAWebSubInitializer = new BaseIDAWebSubInitializer() {

			@Override
			protected int doInitSubscriptions() {
				throw new RuntimeException("error");
			}

			@Override
			protected int doRegisterTopics() {
				throw new RuntimeException("error");
			}};
			
			ReflectionTestUtils.setField(baseIDAWebSubInitializer, "taskScheduler", Mockito.mock(ThreadPoolTaskScheduler.class));
		return baseIDAWebSubInitializer;
	}

	@Test
	public void testDoRegisterTopics() throws Exception {
		BaseIDAWebSubInitializer testSubject;
		int result;

		// default test
		testSubject = createTestSubject();
		result = ReflectionTestUtils.invokeMethod(testSubject, "doRegisterTopics");
		
	}

	@Test
	public void testDoInitSubscriptions() throws Exception {
		BaseIDAWebSubInitializer testSubject;
		int result;

		// default test
		testSubject = createTestSubject();
		result = ReflectionTestUtils.invokeMethod(testSubject, "doInitSubscriptions");

	}
}