package io.mosip.authentication.common.service.websub.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.common.service.helper.WebSubHelper;
import io.mosip.authentication.common.service.util.EnvUtil;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractEventInitializerTest<T extends BaseWebSubEventsInitializer> {
	
	/** The env. */
	@InjectMocks
	protected EnvUtil env;
	
	@Mock
	protected Environment environment;
	
	@Mock
	protected WebSubHelper webSubHelper;
	
	
	protected T createTestInstance() {
		T baseWebSubEventsInitializer = doCreateTestInstance();
		ReflectionTestUtils.setField(baseWebSubEventsInitializer, "env" ,env);
        ReflectionTestUtils.setField(baseWebSubEventsInitializer, "webSubHelper" ,webSubHelper);
		return baseWebSubEventsInitializer;
	}
	
	
	protected abstract T doCreateTestInstance();
	
	@Test
	public void testSubscribe() {
		T baseWebSubEventsInitializer = createTestInstance();
		baseWebSubEventsInitializer.doSubscribe();
	}
	

	@Test(expected = RuntimeException.class)
	public void testSubscribeForEventWithException() {
		T baseWebSubEventsInitializer = createTestInstance();
		Mockito.doThrow(new RuntimeException()).when(webSubHelper).subscribe(Mockito.any());
		baseWebSubEventsInitializer.doSubscribe();
	}
	
	@Test
	public void testRegisterNullEnableTester() {
		T baseWebSubEventsInitializer = createTestInstance();
		baseWebSubEventsInitializer.doRegister();
	}

	
	@Test
	public void testTryRegisterTopicWithException() {
		T baseWebSubEventsInitializer = createTestInstance();
		baseWebSubEventsInitializer.doRegister();
	}
	
	
}
