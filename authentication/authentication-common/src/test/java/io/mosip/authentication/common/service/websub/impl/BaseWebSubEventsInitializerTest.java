package io.mosip.authentication.common.service.websub.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.function.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.common.service.helper.WebSubHelper;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.exception.IdAuthRetryException;
import io.mosip.kernel.websub.api.exception.WebSubClientException;

@RunWith(MockitoJUnitRunner.class)
public class BaseWebSubEventsInitializerTest {
	
	private class TestBaseWebSubEventsInitializer extends BaseWebSubEventsInitializer {
		private boolean subscribed;
		private boolean registered;
		@Override
		protected void doSubscribe() {
			subscribed = true;			
		}

		@Override
		protected void doRegister() {
			registered = true;			
		}
	}
	
	/** The env. */
	@Mock
	protected EnvUtil env;
	
	@Mock
	protected WebSubHelper webSubHelper;
	
	private TestBaseWebSubEventsInitializer createTestInstance() {
		return createTestInstance(false);
	}

	protected TestBaseWebSubEventsInitializer createTestInstance(boolean isException) {
		TestBaseWebSubEventsInitializer baseWebSubEventsInitializer;
		if (isException) {
			baseWebSubEventsInitializer = new TestBaseWebSubEventsInitializer() {
				protected void doSubscribe() {
					throw new WebSubClientException(null, null);
				}
				
				@Override
				public void register(Supplier<Boolean> enableTester) {
					throw new WebSubClientException(null, null);
				}
			};
		} else {
			baseWebSubEventsInitializer = new TestBaseWebSubEventsInitializer();
		}
		ReflectionTestUtils.setField(baseWebSubEventsInitializer, "env" ,env);
        ReflectionTestUtils.setField(baseWebSubEventsInitializer, "webSubHelper" ,webSubHelper);
		return baseWebSubEventsInitializer;
	}
	
	@Test
	public void testSubscribeNullEnableTester() {
		TestBaseWebSubEventsInitializer baseWebSubEventsInitializer = createTestInstance();
        baseWebSubEventsInitializer.subscribe(null);
		assertTrue(baseWebSubEventsInitializer.subscribed);
	}
	
	@Test
	public void testSubscribeWithEnableTester() {
		TestBaseWebSubEventsInitializer baseWebSubEventsInitializer = createTestInstance();
		baseWebSubEventsInitializer.subscribe(() -> true);
		assertTrue(baseWebSubEventsInitializer.subscribed);
	}
	
	@Test
	public void testSubscribeWithEnableTesterFalse() {
		TestBaseWebSubEventsInitializer baseWebSubEventsInitializer = createTestInstance();
		baseWebSubEventsInitializer.subscribe(() -> false);
		assertFalse(baseWebSubEventsInitializer.subscribed);
	}
	
	@Test(expected = IdAuthRetryException.class)
	public void testSubscribeWithEnableTesterThrowsException() {
		TestBaseWebSubEventsInitializer baseWebSubEventsInitializer = createTestInstance(true);
		baseWebSubEventsInitializer.subscribe(() -> true);
	}
	
	@Test
	public void testRegisterNullEnableTester() {
		TestBaseWebSubEventsInitializer baseWebSubEventsInitializer = createTestInstance();
		baseWebSubEventsInitializer.register(null);
		assertTrue(baseWebSubEventsInitializer.registered);
	}
	
	@Test
	public void testRegisterWithEnableTester() {
		TestBaseWebSubEventsInitializer baseWebSubEventsInitializer = createTestInstance();
		baseWebSubEventsInitializer.register(() -> true);
		assertTrue(baseWebSubEventsInitializer.registered);
	}
	
	@Test
	public void testRegisterWithEnableTesterFalse() {
		TestBaseWebSubEventsInitializer baseWebSubEventsInitializer = createTestInstance();
		baseWebSubEventsInitializer.register(() -> false);
		assertFalse(baseWebSubEventsInitializer.registered);
	}
	
	@Test
	public void testTryRegisterTopic() {
		TestBaseWebSubEventsInitializer baseWebSubEventsInitializer = createTestInstance();
		baseWebSubEventsInitializer.tryRegisterTopicEvent("topic");
	}
	
	@Test
	public void testTryRegisterTopicWithException() {
		TestBaseWebSubEventsInitializer baseWebSubEventsInitializer = createTestInstance();
		Mockito.doThrow(new RuntimeException()).when(webSubHelper).registerTopic(Mockito.anyString());
		baseWebSubEventsInitializer.tryRegisterTopicEvent("topic");
	}
	
	@Test
	public void testSubscribeForEvent() {
		TestBaseWebSubEventsInitializer baseWebSubEventsInitializer = createTestInstance();
		baseWebSubEventsInitializer.subscribeForEvent("topic", "url", "secret");
	}
	
	@Test(expected = RuntimeException.class)
	public void testSubscribeForEventWithException() {
		TestBaseWebSubEventsInitializer baseWebSubEventsInitializer = createTestInstance();
		Mockito.doThrow(new RuntimeException()).when(webSubHelper).subscribe(Mockito.any());
		baseWebSubEventsInitializer.subscribeForEvent("topic", "url", "secret");
	}

}
