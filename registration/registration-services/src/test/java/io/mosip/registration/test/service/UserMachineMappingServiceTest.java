package io.mosip.registration.test.service;

import static org.junit.Assert.assertNotNull;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.MachineMappingDAO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.SuccessResponseDTO;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.repositories.UserMachineMappingRepository;
import io.mosip.registration.service.BaseService;
import io.mosip.registration.service.operator.impl.UserMachineMappingServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RegistrationAppHealthCheckUtil.class })
public class UserMachineMappingServiceTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private UserMachineMappingServiceImpl userMachineMappingServiceImpl;
	@Mock
	private MachineMappingDAO machineMappingDAO;
	@Mock
	private UserMachineMappingRepository userMachineMappingRepository;
	@Mock
	private BaseService baseService;
	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;

	@Test
	public void syncUserDetailsTest() throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException, RegBaseCheckedException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		List<UserMachineMapping> list = new ArrayList<>();
		UserMachineMapping userMachineMapping = new UserMachineMapping();
		UserDetail userDetail = new UserDetail();
		userDetail.setId("id");
		userDetail.setIsActive(true);
		userMachineMapping.setUserDetail(userDetail);
		list.add(userMachineMapping);
		String macAdress = "macAddress";
		String machineId = "machineId";
		String centerId = "centerId";
		Mockito.when(baseService.getMacAddress()).thenReturn(macAdress);
		Mockito.when(baseService.getStationId(Mockito.anyString())).thenReturn(machineId);
		Mockito.when(baseService.getCenterId(Mockito.anyString())).thenReturn(centerId);
		Mockito.when(machineMappingDAO.getUserMappingDetails(Mockito.anyString())).thenReturn(list);
		//new code
		Map<String,Object> myMap=new HashMap<>();
		myMap.put(RegistrationConstants.INITIAL_SETUP, RegistrationConstants.ENABLE);
		Map<String, String> map = new HashMap<>();
		LinkedHashMap<String, Object> responseMap=new LinkedHashMap<>();
		Map<String, String> masterSyncMap = new LinkedHashMap<>();
		masterSyncMap.put("lastSyncTime", "2019-03-27T11:07:34.408Z");
		responseMap.put("response", masterSyncMap);
		map.put(RegistrationConstants.USER_CENTER_ID, "10011");
		
		
		
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(), Mockito.anyString())).thenReturn(responseMap);
		Map<String, Object> map1 = new HashMap<>();

		map1.put("cntrId", centerId);
		map1.put("isActive", userMachineMapping.getUserDetail().getIsActive());
		map1.put("machineId", machineId);
		map1.put("userId", userMachineMapping.getUserDetail().getId());
		
        ResponseDTO responseDTO=new ResponseDTO();
        SuccessResponseDTO sucessResponse=new SuccessResponseDTO();
        sucessResponse.setCode(RegistrationConstants.MASTER_SYNC_SUCESS_MSG_CODE);
		sucessResponse.setInfoType(RegistrationConstants.ALERT_INFORMATION);
		sucessResponse.setMessage(RegistrationConstants.MASTER_SYNC_SUCCESS);

		responseDTO.setSuccessResponseDTO(sucessResponse);


		assertNotNull(userMachineMappingServiceImpl.syncUserDetails());
	}

	@Test
	public void syncUserDetailsOffLineTest() {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(false);
		assertNotNull(userMachineMappingServiceImpl.syncUserDetails());

	}
	
	@Test
	public void isUserNewToMachineSuccessTest() {
		
		Mockito.when(machineMappingDAO.isExists(RegistrationConstants.JOB_TRIGGER_POINT_USER)).thenReturn(true);
		
		assertNotNull(userMachineMappingServiceImpl.isUserNewToMachine(RegistrationConstants.JOB_TRIGGER_POINT_USER).getSuccessResponseDTO());
	}
	
	@Test
	public void isUserNewToMachineFailureTest() {
		
		Mockito.when(machineMappingDAO.isExists(RegistrationConstants.JOB_TRIGGER_POINT_USER)).thenReturn(false);
		
		assertNotNull(userMachineMappingServiceImpl.isUserNewToMachine(RegistrationConstants.JOB_TRIGGER_POINT_USER).getErrorResponseDTOs());
	}
	@Test
	public void syncfailureTest() throws HttpClientErrorException, ResourceAccessException, SocketTimeoutException, RegBaseCheckedException
	
	{
		LinkedHashMap<String, Object> responseMap=new LinkedHashMap<>();
		Map<String, Object> userDetailsMap = new LinkedHashMap<>();
		userDetailsMap.put("errorCode", "KER-SNC-303");
		userDetailsMap.put("message", "Registration center user not found ");
		List<Map<String, Object>> userFailureList=new ArrayList<>();
		userFailureList.add(userDetailsMap);
		responseMap.put("errors", userFailureList);
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		List<UserMachineMapping> list = new ArrayList<>();
		UserMachineMapping userMachineMapping = new UserMachineMapping();
		UserDetail userDetail = new UserDetail();
		userDetail.setId("id");
		userDetail.setIsActive(true);
		userMachineMapping.setUserDetail(userDetail);
		list.add(userMachineMapping);
		String macAdress = "macAddress";
		String machineId = "machineId";
		String centerId = "centerId";
		Mockito.when(baseService.getMacAddress()).thenReturn(macAdress);
		Mockito.when(baseService.getStationId(Mockito.anyString())).thenReturn(machineId);
		Mockito.when(baseService.getCenterId(Mockito.anyString())).thenReturn(centerId);
		Mockito.when(machineMappingDAO.getUserMappingDetails(Mockito.anyString())).thenReturn(list);
		Mockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(), Mockito.anyString())).thenReturn(responseMap);
		Map<String, Object> map1 = new HashMap<>();

		map1.put("cntrId", centerId);
		map1.put("isActive", userMachineMapping.getUserDetail().getIsActive());
		map1.put("machineId", machineId);
		map1.put("userId", userMachineMapping.getUserDetail().getId());
		assertNotNull(userMachineMappingServiceImpl.syncUserDetails());
	}

}
