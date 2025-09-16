package io.mosip.authentication.otp.service.controller;
import io.mosip.authentication.core.spi.masterdata.MasterDataCacheUpdateService;
import io.mosip.kernel.core.websub.model.EventModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MasterDataUpdateEventControllerTest {

    private MasterDataUpdateEventController controller;
    private MasterDataCacheUpdateService masterDataCacheUpdateService;

    @BeforeEach
    void setUp() {
        masterDataCacheUpdateService = Mockito.mock(MasterDataCacheUpdateService.class);
        controller = new MasterDataUpdateEventController();
        try {
            var field = MasterDataUpdateEventController.class.getDeclaredField("masterDataCacheUpdateService");
            field.setAccessible(true);
            field.set(controller, masterDataCacheUpdateService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testHandleMasterdataTemplatesUpdate() {
        EventModel eventModel = new EventModel();
        controller.handleMasterdataTemplatesUpdate(eventModel);
        verify(masterDataCacheUpdateService, times(1)).updateTemplates(eventModel);
    }

    @Test
    void testHandleMasterdataTitlesUpdate() {
        EventModel eventModel = new EventModel();
        controller.handleMasterdataTitlesUpdate(eventModel);
        verify(masterDataCacheUpdateService, times(1)).updateTitles(eventModel);
    }
}
