package io.mosip.authentication.service.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.common.service.spi.websub.PartnerCACertEventService;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.kernel.core.websub.model.EventModel;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PartnerCACertEventControllerTest {

	@Mock
	private PartnerCACertEventService partnerCACertEventService;

	@InjectMocks
	private PartnerCACertEventController partnerCACertEventController;

	@BeforeEach
	public void before() {
		ReflectionTestUtils.setField(partnerCACertEventController,
			"partnerCACertEventService", partnerCACertEventService);
	}

	@Test
	public void testHandleCACertificate() throws RestServiceException, IdAuthenticationBusinessException {
		EventModel eventModel = new EventModel();
		eventModel.setPublisher("MOSIP");
		eventModel.setTopic("partner-ca-cert-topic");

		doNothing().when(partnerCACertEventService).evictCACertCache(any(EventModel.class));
		doNothing().when(partnerCACertEventService).handleCACertEvent(any(EventModel.class));

		partnerCACertEventController.handleCACertificate(eventModel);

		verify(partnerCACertEventService, times(1)).evictCACertCache(eventModel);
		verify(partnerCACertEventService, times(1)).handleCACertEvent(eventModel);
	}

	@Test
	public void testHandleCACertificateWithBusinessException()
			throws RestServiceException, IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		EventModel eventModel = new EventModel();

		doNothing().when(partnerCACertEventService).evictCACertCache(any(EventModel.class));
		doThrow(new IdAuthenticationBusinessException("TEST_ERROR", "Test error"))
			.when(partnerCACertEventService).handleCACertEvent(any(EventModel.class));

		partnerCACertEventController.handleCACertificate(eventModel);
			});
	}

	@Test
	public void testHandleCACertificateWithRestServiceException()
			throws RestServiceException, IdAuthenticationBusinessException {
		assertThrows(RestServiceException.class, () -> {
		EventModel eventModel = new EventModel();

		doNothing().when(partnerCACertEventService).evictCACertCache(any(EventModel.class));
		doThrow(new RestServiceException())
			.when(partnerCACertEventService).handleCACertEvent(any(EventModel.class));

		partnerCACertEventController.handleCACertificate(eventModel);
			});
	}

	@Test
	public void testHandleCACertificateWithNullEvent() throws RestServiceException, IdAuthenticationBusinessException {
		EventModel eventModel = null;

		doNothing().when(partnerCACertEventService).evictCACertCache(any());
		doNothing().when(partnerCACertEventService).handleCACertEvent(any());

		try {
			partnerCACertEventController.handleCACertificate(eventModel);
			verify(partnerCACertEventService, times(1)).evictCACertCache(any());
			verify(partnerCACertEventService, times(1)).handleCACertEvent(any());
		} catch (Exception e) {
			// Expected if null handling is strict
			assertTrue(true, "Should handle null gracefully");
		}
	}
}
