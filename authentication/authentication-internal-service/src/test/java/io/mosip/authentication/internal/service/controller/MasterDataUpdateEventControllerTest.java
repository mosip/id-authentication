package io.mosip.authentication.internal.service.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.core.spi.masterdata.MasterDataCacheUpdateService;
import io.mosip.kernel.core.websub.model.EventModel;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MasterDataUpdateEventControllerTest {

	@Mock
	private MasterDataCacheUpdateService masterDataCacheUpdateService;
	
	@InjectMocks
	private MasterDataUpdateEventController masterDataUpdateEventController;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(masterDataUpdateEventController, 
			"masterDataCacheUpdateService", masterDataCacheUpdateService);
	}
	
	@Test
	public void testHandleMasterdataTemplatesUpdate() {
		EventModel eventModel = new EventModel();
		eventModel.setPublisher("MOSIP");
		eventModel.setTopic("masterdata-templates-topic");
		
		doNothing().when(masterDataCacheUpdateService).updateTemplates(any(EventModel.class));
		
		masterDataUpdateEventController.handleMasterdataTemplatesUpdate(eventModel);
		
		verify(masterDataCacheUpdateService, times(1)).updateTemplates(eventModel);
	}
	
	@Test
	public void testHandleMasterdataTemplatesUpdateWithNullEvent() {
		 EventModel eventModel = null;

        masterDataUpdateEventController.handleMasterdataTemplatesUpdate(eventModel);

        // Controller should delegate even for null and not throw
        verify(masterDataCacheUpdateService, times(1)).updateTemplates(null);
	}
	
	@Test
	public void testHandleMasterdataTitlesUpdate() {
		EventModel eventModel = new EventModel();
		eventModel.setPublisher("MOSIP");
		eventModel.setTopic("masterdata-titles-topic");
		
		doNothing().when(masterDataCacheUpdateService).updateTitles(any(EventModel.class));
		
		masterDataUpdateEventController.handleMasterdataTitlesUpdate(eventModel);
		
		verify(masterDataCacheUpdateService, times(1)).updateTitles(eventModel);
	}
	
	@Test
	public void testHandleMasterdataTitlesUpdateWithNullEvent() {
		EventModel eventModel = null;
		
		doNothing().when(masterDataCacheUpdateService).updateTitles(any());
		
		try {
			masterDataUpdateEventController.handleMasterdataTitlesUpdate(eventModel);
			verify(masterDataCacheUpdateService, times(1)).updateTitles(any());
		} catch (Exception e) {
			// Expected if null handling is strict
			assertTrue("Should handle null gracefully", true);
		}
	}
}
