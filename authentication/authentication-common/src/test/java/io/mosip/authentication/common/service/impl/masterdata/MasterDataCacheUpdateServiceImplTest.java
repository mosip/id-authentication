package io.mosip.authentication.common.service.impl.masterdata;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.authentication.common.service.cache.MasterDataCache;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.kernel.core.websub.model.Event;
import io.mosip.kernel.core.websub.model.EventModel;

@RunWith(SpringRunner.class)
public class MasterDataCacheUpdateServiceImplTest {
	
	@Mock
	private MasterDataCache masterDataCache;
	
	@InjectMocks
	private MasterDataCacheUpdateServiceImpl masterDataCacheUpdateServiceImpl;
	
	@Test
	public void testUpdateTemplates_nullEventModel() {
		masterDataCacheUpdateServiceImpl.updateTemplates(null);
	}
	
	@Test
	public void testUpdateTemplates() {
		EventModel model = new EventModel();
		Event event = new Event();
		Map<String, Object> data = Map.of("templates", Map.of("templateTypeCode", "auth.sms"));
		event.setData(data);
		model.setEvent(event);
		masterDataCacheUpdateServiceImpl.updateTemplates(model);
	}
	
	@Test
	public void testUpdateTemplates_WithException() throws IdAuthenticationBusinessException {
		EventModel model = new EventModel();
		Event event = new Event();
		Map<String, Object> data = Map.of("templates", Map.of("templateTypeCode", "auth.sms"));
		event.setData(data);
		model.setEvent(event);
		Mockito.when(masterDataCache.getMasterDataTemplate(Mockito.anyString())).thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		masterDataCacheUpdateServiceImpl.updateTemplates(model);
	}
	
	@Test
	public void testUpdateTitles() {
		EventModel model = new EventModel();
		Event event = new Event();
		model.setEvent(event);
		masterDataCacheUpdateServiceImpl.updateTitles(model);
	}
	
	@Test
	public void testUpdateTitles_withException() throws IdAuthenticationBusinessException {
		EventModel model = new EventModel();
		Event event = new Event();
		model.setEvent(event);
		Mockito.when(masterDataCache.getMasterDataTitles()).thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS));
		masterDataCacheUpdateServiceImpl.updateTitles(model);
	}
	

}
