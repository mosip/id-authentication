package io.mosip.authentication.otp.service.controller;
import io.mosip.authentication.common.service.spi.websub.PartnerCACertEventService;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.kernel.core.websub.model.EventModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PartnerCACertEventControllerTest {

    private PartnerCACertEventController controller;
    private PartnerCACertEventService partnerCACertEventService;

    @BeforeEach
    void setUp() {
        partnerCACertEventService = Mockito.mock(PartnerCACertEventService.class);
        controller = new PartnerCACertEventController();

        try {
            var field = PartnerCACertEventController.class.getDeclaredField("partnerCACertEventService");
            field.setAccessible(true);
            field.set(controller, partnerCACertEventService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testHandleCACertificateSuccess() throws RestServiceException, IdAuthenticationBusinessException {
        EventModel eventModel = new EventModel();
        controller.handleCACertificate(eventModel);
        verify(partnerCACertEventService, times(1)).evictCACertCache(eventModel);
    }


    @Test
    void testHandleCACertificateIdAuthBusinessException() throws RestServiceException, IdAuthenticationBusinessException {
        EventModel eventModel = new EventModel();
        doThrow(new IdAuthenticationBusinessException("ERR", "Business error")).when(partnerCACertEventService).evictCACertCache(eventModel);

        assertThrows(IdAuthenticationBusinessException.class, () -> controller.handleCACertificate(eventModel));
        verify(partnerCACertEventService, times(1)).evictCACertCache(eventModel);
    }
}
