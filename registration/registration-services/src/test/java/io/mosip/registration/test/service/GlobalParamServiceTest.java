package io.mosip.registration.test.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.registration.audit.AuditFactoryImpl;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.dao.UserOnboardDAO;
import io.mosip.registration.dao.impl.GlobalParamDAOImpl;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.config.impl.GlobalParamServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

public class GlobalParamServiceTest {
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@Mock
	private AuditFactoryImpl auditFactory;

	@InjectMocks
	private GlobalParamServiceImpl gloablContextParamServiceImpl;
	
	@Mock
	private GlobalParamDAOImpl globalContextParamDAOImpl;
	
	@Mock
	RegistrationAppHealthCheckUtil registrationAppHealthCheckUtil;
	
	
	@Mock
	RegistrationSystemPropertiesChecker registrationSystemPropertiesChecker;
	
	@Mock
	UserOnboardDAO onboardDAO;
	
	@Mock
	ServiceDelegateUtil serviceDelegateUtil;
	

	@Test
	public void getGlobalParamsTest() { 
		
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
		
		Map<String,Object> globalParamMap = new LinkedHashMap<>();
		Mockito.when(globalContextParamDAOImpl.getGlobalParams()).thenReturn(globalParamMap);
		assertEquals(globalParamMap, gloablContextParamServiceImpl.getGlobalParams());
	}

	@Test
	public void syncConfigDataTest() throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException { 
		
		//getGlobalParamsTest();
//		Mockito.when(registrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
//		Mockito.when(registrationSystemPropertiesChecker.getMachineId()).thenReturn("abc123");
		Mockito.when(onboardDAO.getCenterID(Mockito.anyString())).thenReturn("STN123");
		Mockito.when(onboardDAO.getCenterID(Mockito.anyString())).thenReturn("abc123");
		
		HashMap<String,Object> globalParamJsonMap = new HashMap<>();
		globalParamJsonMap.put("retryAttempts", "3");
		HashMap<String,Object> globalParamJsonMap2 = new HashMap<>();
		globalParamJsonMap2.put("loginSequence1", "OTP");
		
		globalParamJsonMap.put("map",globalParamJsonMap2);
		
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(),Mockito.anyBoolean())).thenReturn(globalParamJsonMap);
		Mockito.doNothing().when(globalContextParamDAOImpl).saveAll(Mockito.anyList());
		
		Map<String,Object> globalParamMap = new LinkedHashMap<>();
		globalParamMap.put("ANY", "ANY");
		Mockito.when(globalContextParamDAOImpl.getGlobalParams()).thenReturn(globalParamMap);
		
		gloablContextParamServiceImpl.synchConfigData();
	}
	
	@Test
	public void syncConfigDataExceptionTest() throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException { 
		
			Mockito.when(onboardDAO.getCenterID(Mockito.anyString())).thenReturn("STN123");
		Mockito.when(onboardDAO.getCenterID(Mockito.anyString())).thenReturn("abc123");
		
		Map<String,Object> globalParamMap = new LinkedHashMap<>();
		globalParamMap.put("ANY", "ANY");
		Mockito.when(globalContextParamDAOImpl.getGlobalParams()).thenReturn(globalParamMap);

		
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(),Mockito.anyBoolean())).thenThrow(HttpClientErrorException.class);
		
		gloablContextParamServiceImpl.synchConfigData();
	}
	
	//@Test
	public void syncConfigDataTest2() throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException { 
		
			Mockito.when(onboardDAO.getCenterID(Mockito.anyString())).thenReturn("STN123");
		Mockito.when(onboardDAO.getCenterID(Mockito.anyString())).thenReturn("abc123");
		
		
		
		Map<String,Object> globalParamMap = new LinkedHashMap<>();
		Mockito.when(globalContextParamDAOImpl.getGlobalParams()).thenReturn(globalParamMap);
		
		gloablContextParamServiceImpl.synchConfigData();
	}


}
