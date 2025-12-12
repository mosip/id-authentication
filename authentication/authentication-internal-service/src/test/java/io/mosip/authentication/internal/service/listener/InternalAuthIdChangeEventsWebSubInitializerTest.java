package io.mosip.authentication.internal.service.listener;

import io.mosip.authentication.common.service.helper.WebSubHelper;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.common.service.websub.impl.IdChangeEventsInitializer;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InternalAuthIdChangeEventsWebSubInitializerTest {

	@Mock
	private IdChangeEventsInitializer idChangeEventInitializer;
	
	@Mock
	private WebSubHelper webSubHelper;
	
	@InjectMocks
	private InternalAuthIdChangeEventsWebSubInitializer internalAuthIdChangeEventsWebSubInitializer;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(internalAuthIdChangeEventsWebSubInitializer, 
			"webSubHelper", webSubHelper);
		ReflectionTestUtils.setField(internalAuthIdChangeEventsWebSubInitializer, 
			"idChangeEventInitializer", idChangeEventInitializer);
	}
	
	@Test
	public void testDoInitSubscriptions() {
		int expectedStatus = HttpStatus.SC_OK;
		
		when(webSubHelper.initSubscriber(any(IdChangeEventsInitializer.class))).thenReturn(expectedStatus);
		
		int result = internalAuthIdChangeEventsWebSubInitializer.doInitSubscriptions();
		
		assertEquals("Should return OK status", expectedStatus, result);
		verify(webSubHelper, times(1)).initSubscriber(idChangeEventInitializer);
	}
	
	@Test
	public void testDoRegisterTopics() {
		int result = internalAuthIdChangeEventsWebSubInitializer.doRegisterTopics();
		
		assertEquals("Should return OK status", HttpStatus.SC_OK, result);
		// Should not call webSubHelper.initRegistrar (commented out in implementation)
		verify(webSubHelper, never()).initRegistrar(any());
	}
}
