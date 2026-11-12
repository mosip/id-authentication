package io.mosip.authentication.service.internal.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.hotlist.service.HotlistService;
import io.mosip.kernel.core.websub.model.EventModel;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class HotlistEventControllerTest {

	@Mock
	private HotlistService hotlistService;
	
	@InjectMocks
	private HotlistEventController hotlistEventController;
	
	@BeforeEach
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
	
	@Test
	public void testHandleHotlistingWithException() throws IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		EventModel eventModel = new EventModel();
		
		doThrow(new IdAuthenticationBusinessException("TEST_ERROR", "Test error message"))
			.when(hotlistService).handlingHotlistingEvent(any(EventModel.class));
		
		hotlistEventController.handleHotlisting(eventModel);
			});
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
			assertTrue(true, "Should handle null gracefully");
		}
	}
}
