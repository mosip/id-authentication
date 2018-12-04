package io.mosip.registration.test.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.AppModule;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.dao.impl.GlobalParamDAOImpl;
import io.mosip.registration.service.impl.GlobalParamServiceImpl;

public class GlobalParamServiceTest {
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Mock
	private AuditFactoryImpl auditFactory;

	@InjectMocks
	private GlobalParamServiceImpl gloablContextParamServiceImpl;
	
	@Mock
	private GlobalParamDAOImpl globalContextParamDAOImpl;

	@Test
	public void getGlobalParamsTest() { 
		
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(AppModule.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		
		Map<String,Object> globalParamMap = new LinkedHashMap<>();
		Mockito.when(globalContextParamDAOImpl.getGlobalParams()).thenReturn(globalParamMap);
		assertEquals(globalParamMap, gloablContextParamServiceImpl.getGlobalParams());
	}

}
