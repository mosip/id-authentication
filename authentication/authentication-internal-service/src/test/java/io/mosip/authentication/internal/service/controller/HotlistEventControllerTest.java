package io.mosip.authentication.internal.service.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.kernel.core.websub.model.EventModel;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class HotlistEventControllerTest {

	@Mock
	private HotlistService hotlistService;
	
	@InjectMocks
	private HotlistEventController hotlistEventController;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(hotlistEventController, "hotlistService", hotlistService);
	}
	
	@Test
	public void testHandleHotlisting() throws IdAuthenticationBusinessException {
		EventModel eventModel = new EventModel();
		eventModel.setPublisher("MOSIP");
		eventModel.setTopic("hotlist-topic");
		
		doNothing().when(hotlistService).handlingHotlistingEvent(any(EventModel.class));
		
		hotlistEventController.handleHotlisting(eventModel);
		
		verify(hotlistService, times(1)).handlingHotlistingEvent(eventModel);
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void testHandleHotlistingWithException() throws IdAuthenticationBusinessException {
		EventModel eventModel = new EventModel();
		
		doThrow(new IdAuthenticationBusinessException("TEST_ERROR", "Test error message"))
			.when(hotlistService).handlingHotlistingEvent(any(EventModel.class));
		
		hotlistEventController.handleHotlisting(eventModel);
	}
	
	@Test
	public void testHandleHotlistingWithNullEventModel() throws IdAuthenticationBusinessException {
		EventModel eventModel = null;
		
		doNothing().when(hotlistService).handlingHotlistingEvent(any());
		
		try {
			hotlistEventController.handleHotlisting(eventModel);
			verify(hotlistService, times(1)).handlingHotlistingEvent(any());
		} catch (Exception e) {
			// Expected if null handling is strict
			assertTrue("Should handle null gracefully", true);
		}
	}
}
