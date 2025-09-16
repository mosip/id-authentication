package io.mosip.authentication.service.controller;

import io.mosip.authentication.core.spi.masterdata.MasterDataCacheUpdateService;
import io.mosip.kernel.core.websub.model.EventModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class MasterDataUpdateEventControllerTest {

    @Mock
    private MasterDataCacheUpdateService masterDataCacheUpdateService;

    @InjectMocks
    private MasterDataUpdateEventController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleMasterdataTemplatesUpdate() {
        EventModel eventModel = new EventModel();
        doNothing().when(masterDataCacheUpdateService).updateTemplates(eventModel);

        controller.handleMasterdataTemplatesUpdate(eventModel);

        verify(masterDataCacheUpdateService, times(1)).updateTemplates(eventModel);
    }

    @Test
    void testHandleMasterdataTitlesUpdate() {
        EventModel eventModel = new EventModel();
        doNothing().when(masterDataCacheUpdateService).updateTitles(eventModel);

        controller.handleMasterdataTitlesUpdate(eventModel);

        verify(masterDataCacheUpdateService, times(1)).updateTitles(eventModel);
    }
}
