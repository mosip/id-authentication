package io.mosip.registration.test.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;

import java.net.SocketTimeoutException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.registration.audit.AuditManagerSerivceImpl;
import io.mosip.registration.constants.AuditEvent;
import io.mosip.registration.constants.Components;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.UserOnboardDAO;
import io.mosip.registration.dao.impl.GlobalParamDAOImpl;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.entity.GlobalParam;
import io.mosip.registration.entity.id.GlobalParamId;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.config.impl.GlobalParamServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.healthcheck.RegistrationSystemPropertiesChecker;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RegistrationAppHealthCheckUtil.class })
public class GlobalParamServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private AuditManagerSerivceImpl auditFactory;

	@InjectMocks
	private GlobalParamServiceImpl gloablContextParamServiceImpl;

	@Mock
	private GlobalParamDAOImpl globalParamDAOImpl;

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
				Mockito.anyString(), Mockito.anyString());

		Map<String, Object> globalParamMap = new LinkedHashMap<>();
		Mockito.when(globalParamDAOImpl.getGlobalParams()).thenReturn(globalParamMap);
		assertEquals(globalParamMap, gloablContextParamServiceImpl.getGlobalParams());
	}

	@Test
	public void syncConfigDataTest() throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException {

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		HashMap<String, Object> globalParamJsonMap = new HashMap<>();
		globalParamJsonMap.put("retryAttempts", "3");
		globalParamJsonMap.put("kernel", "5");
		HashMap<String, Object> globalParamJsonMap2 = new HashMap<>();
		globalParamJsonMap2.put("loginSequence1", "OTP");
		globalParamJsonMap.put("response", globalParamJsonMap2);

		globalParamJsonMap.put("map", globalParamJsonMap2);

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenReturn(globalParamJsonMap);
		Mockito.doNothing().when(globalParamDAOImpl).saveAll(Mockito.anyList());

		Map<String, Object> globalParamMap = new LinkedHashMap<>();
		globalParamMap.put("ANY", "ANY");
		Mockito.when(globalParamDAOImpl.getGlobalParams()).thenReturn(globalParamMap);
		java.util.List<GlobalParam> globalParamList = new ArrayList<>();
		GlobalParam globalParam = new GlobalParam();
		globalParam.setVal("2");
		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode("retryAttempts");
		globalParam.setGlobalParamId(globalParamId);
		globalParamList.add(globalParam);
		Mockito.when(globalParamDAOImpl.getAllEntries()).thenReturn(globalParamList);
		gloablContextParamServiceImpl.synchConfigData(false);
	}

	@Test
	public void syncConfigData() throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException {

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		HashMap<String, Object> globalParamJsonMap = new LinkedHashMap<>();

		globalParamJsonMap.put("kernel", "5");
		HashMap<String, Object> globalParamJsonMap2 = new LinkedHashMap<>();
		globalParamJsonMap2.put("loginSequence1", "OTP");
		globalParamJsonMap.put("response", globalParamJsonMap2);

		globalParamJsonMap.put("map", globalParamJsonMap2);

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenReturn(globalParamJsonMap);
		Mockito.doNothing().when(globalParamDAOImpl).saveAll(Mockito.anyList());

		Map<String, Object> globalParamMap = new LinkedHashMap<>();
		globalParamMap.put("retryAttempts", "2");
		Mockito.when(globalParamDAOImpl.getGlobalParams()).thenReturn(globalParamMap);
		java.util.List<GlobalParam> globalParamList = new ArrayList<>();
		GlobalParam globalParam = new GlobalParam();
		globalParam.setVal("2");
		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode("kernel");
		globalParam.setGlobalParamId(globalParamId);
		globalParamList.add(globalParam);
		Mockito.when(globalParamDAOImpl.getAllEntries()).thenReturn(globalParamList);

		gloablContextParamServiceImpl.synchConfigData(false);
	}

	@Test
	public void syncConfigDataExceptionTest()
			throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException {

		Map<String, Object> globalParamMap = new LinkedHashMap<>();
		globalParamMap.put("ANY", "ANY");
		Mockito.when(globalParamDAOImpl.getGlobalParams()).thenReturn(globalParamMap);

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenThrow(HttpClientErrorException.class);

		gloablContextParamServiceImpl.synchConfigData(false);
	}

	@Test
	public void syncConfigTest() throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException {

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		HashMap<String, Object> globalParamJsonMap = new HashMap<>();
		globalParamJsonMap.put("retryAttempts", "3");
		globalParamJsonMap.put("kernel", "5");
		HashMap<String, Object> globalParamJsonMap2 = new HashMap<>();
		globalParamJsonMap2.put("loginSequence1", "OTP");

		globalParamJsonMap.put("map", globalParamJsonMap2);

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenReturn(globalParamJsonMap);
		Mockito.doNothing().when(globalParamDAOImpl).saveAll(Mockito.anyList());

		Map<String, Object> globalParamMap = new LinkedHashMap<>();
		globalParamMap.put("ANY", "ANY");
		Mockito.when(globalParamDAOImpl.getGlobalParams()).thenReturn(globalParamMap);
		java.util.List<GlobalParam> globalParamList = new ArrayList<>();
		GlobalParam globalParam = new GlobalParam();
		globalParam.setVal("ANY");
		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode("ANY");
		globalParam.setGlobalParamId(globalParamId);
		globalParamList.add(globalParam);
		Mockito.when(globalParamDAOImpl.getAllEntries()).thenReturn(globalParamList);

		gloablContextParamServiceImpl.synchConfigData(false);
	}

	@Test
	public void updateSoftwareUpdateStatusSuccessCaseTest() {

		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParamId.setLangCode("eng");

		GlobalParam globalParam = new GlobalParam();
		globalParam.setName(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParam.setGlobalParamId(globalParamId);
		globalParam.setVal("Y");

		Mockito.when(globalParamDAOImpl.updateSoftwareUpdateStatus(Mockito.anyBoolean(), Mockito.any(Timestamp.class)))
				.thenReturn(globalParam);
		ResponseDTO responseDTO = gloablContextParamServiceImpl.updateSoftwareUpdateStatus(true,
				Timestamp.from(Instant.now()));
		assertEquals(responseDTO.getSuccessResponseDTO().getMessage(),
				RegistrationConstants.SOFTWARE_UPDATE_SUCCESS_MSG);
	}

	@Test
	public void updateSoftwareUpdateStatusFailureCaseTest() {

		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParamId.setLangCode("eng");

		GlobalParam globalParam = new GlobalParam();
		globalParam.setName(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParam.setGlobalParamId(globalParamId);
		globalParam.setVal("N");

		Mockito.when(globalParamDAOImpl.updateSoftwareUpdateStatus(Mockito.anyBoolean(), Mockito.any(Timestamp.class)))
				.thenReturn(globalParam);
		ResponseDTO responseDTO = gloablContextParamServiceImpl.updateSoftwareUpdateStatus(false,
				Timestamp.from(Instant.now()));
		assertEquals(responseDTO.getSuccessResponseDTO().getMessage(),
				RegistrationConstants.SOFTWARE_UPDATE_FAILURE_MSG);
	}

	@Test
	public void updatetest() {
		GlobalParam globalParam = new GlobalParam();
		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.INITIAL_SETUP);
		globalParamId.setLangCode("en");
		globalParam.setGlobalParamId(globalParamId);
		Mockito.when(globalParamDAOImpl.update(globalParam)).thenReturn(globalParam);

		gloablContextParamServiceImpl.update(RegistrationConstants.INITIAL_SETUP, RegistrationConstants.DISABLE);
	}

	@Test
	public void syncConfigDataUpdate()
			throws RegBaseCheckedException, HttpClientErrorException, SocketTimeoutException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		HashMap<String, Object> globalParamJsonMap = new LinkedHashMap<>();

		globalParamJsonMap.put("kernel", "5");
		HashMap<String, Object> globalParamJsonMap2 = new LinkedHashMap<>();
		globalParamJsonMap2.put("loginSequence1", "OTP");
		globalParamJsonMap.put("response", globalParamJsonMap2);

		globalParamJsonMap.put("map", globalParamJsonMap2);

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenReturn(globalParamJsonMap);
		Mockito.doNothing().when(globalParamDAOImpl).saveAll(Mockito.anyList());

		Map<String, Object> globalParamMap = new LinkedHashMap<>();
		globalParamMap.put("retryAttempts", "2");
		Mockito.when(globalParamDAOImpl.getGlobalParams()).thenReturn(globalParamMap);
		java.util.List<GlobalParam> globalParamList = new ArrayList<>();
		GlobalParam globalParam = new GlobalParam();
		globalParam.setVal("2");
		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode("retryAttempts");
		globalParam.setGlobalParamId(globalParamId);
		globalParamList.add(globalParam);
		Mockito.when(globalParamDAOImpl.getAllEntries()).thenReturn(globalParamList);

		gloablContextParamServiceImpl.synchConfigData(false);
	}

}
