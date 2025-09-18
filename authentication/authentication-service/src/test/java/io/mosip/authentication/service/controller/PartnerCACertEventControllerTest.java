package io.mosip.authentication.service.controller;

import io.mosip.authentication.common.service.spi.websub.PartnerCACertEventService;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.kernel.core.websub.model.EventModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PartnerCACertEventControllerTest {

    @Mock
    private PartnerCACertEventService partnerCACertEventService;

    @InjectMocks
    private PartnerCACertEventController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleCACertificate_CallsService() throws RestServiceException, IdAuthenticationBusinessException {
        EventModel eventModel = new EventModel();
        doNothing().when(partnerCACertEventService).evictCACertCache(eventModel);

        controller.handleCACertificate(eventModel);

        verify(partnerCACertEventService, times(1)).evictCACertCache(eventModel);
    }
}
