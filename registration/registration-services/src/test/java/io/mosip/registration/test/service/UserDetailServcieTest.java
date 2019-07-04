package io.mosip.registration.test.service;

import static org.mockito.Mockito.doNothing;

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

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dao.UserDetailDAO;
import io.mosip.registration.dto.UserDetailDto;
import io.mosip.registration.dto.UserDetailResponseDto;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.operator.impl.UserDetailServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RegistrationAppHealthCheckUtil.class,UserDetailDAO.class })
public class UserDetailServcieTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private UserOnboardService userOnboardService;

	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;

	@InjectMocks
	private UserDetailServiceImpl userDetailServiceImpl;

	@Mock
	private UserDetailDAO userDetailDAO;

	@Test
	public void userDtls() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		UserDetailResponseDto userDetail = new UserDetailResponseDto();
		List<UserDetailDto> list = new ArrayList<>();
		UserDetailDto userDetails = new UserDetailDto();
		userDetails.setUserName("110011");
		userDetails.setName("SUPERADMIN");
		list.add(userDetails);
		userDetail.setUserDetails(list);
		Map<String, String> map = new HashMap<>();
		map.put(RegistrationConstants.USER_CENTER_ID, "10011");
		LinkedHashMap<String, Object> responseMap=new LinkedHashMap<>();
		Map<String, Object> userDetailsMap = new HashMap<>();
		List<String> rolesList = new ArrayList<>();
		List<Object> userDetailsList = new ArrayList<>();
		rolesList.add("SUPERADMIN");
		userDetailsMap.put("userName", "mosip");
		userDetailsMap.put("mail", "superadmin@mosip.io");
		userDetailsMap.put("mobile", "999999999");
		userDetailsMap.put("userPassword",
				"e1NTSEE1MTJ9MERSeklnR2szMHpTNXJ2aVh6emRrZGdGaU9DWWZjbkVUVW5kNjQ3cXBXK0t1aExoTTNMR0t2LzZ3NUQranNjWmFoS1JGcklhdUJRZGZFRVZkcG82R2gzYVFqNXRUbWVQ");
		userDetailsMap.put("name", "superadmin");
		userDetailsMap.put("roles", rolesList);
		userDetailsList.add(userDetailsMap);
		Map<String, Object> usrDetailMap = new HashMap<>();
		usrDetailMap.put("userDetails", userDetailsList);
		responseMap.put("response", usrDetailMap);
		doNothing().when(userDetailDAO).save(Mockito.any(UserDetailResponseDto.class));
		Mockito.when(userOnboardService.getMachineCenterId()).thenReturn(map);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(),Mockito.anyString()))
				.thenReturn(responseMap);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		userDetailServiceImpl.save("System");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void userDtlsException() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		UserDetailResponseDto userDetail = new UserDetailResponseDto();
		List<UserDetailDto> list = new ArrayList<>();
		UserDetailDto userDetails = new UserDetailDto();
		userDetails.setUserName("110011");
		userDetails.setName("SUPERADMIN");
		list.add(userDetails);
		userDetail.setUserDetails(list);
		Map<String, String> map = new HashMap<>();
		map.put(RegistrationConstants.USER_CENTER_ID, "10011");
		doNothing().when(userDetailDAO).save(Mockito.any(UserDetailResponseDto.class));
		Mockito.when(userOnboardService.getMachineCenterId()).thenReturn(map);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(),Mockito.anyString()))
				.thenThrow(HttpClientErrorException.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		userDetailServiceImpl.save("System");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void userDtlsException1() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		UserDetailResponseDto userDetail = new UserDetailResponseDto();
		List<UserDetailDto> list = new ArrayList<>();
		UserDetailDto userDetails = new UserDetailDto();
		userDetails.setUserName("110011");
		userDetails.setName("SUPERADMIN");
		list.add(userDetails);
		userDetail.setUserDetails(list);
		Map<String, String> map = new HashMap<>();
		map.put(RegistrationConstants.USER_CENTER_ID, "10011");
		doNothing().when(userDetailDAO).save(Mockito.any(UserDetailResponseDto.class));
		Mockito.when(userOnboardService.getMachineCenterId()).thenReturn(map);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(),Mockito.anyString()))
				.thenThrow(SocketTimeoutException.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		userDetailServiceImpl.save("System");
	}
	
	@Test
	public void userDtlsFail() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		UserDetailResponseDto userDetail = new UserDetailResponseDto();
		List<UserDetailDto> list = new ArrayList<>();
		UserDetailDto userDetails = new UserDetailDto();
		userDetails.setUserName("110011");
		userDetails.setName("SUPERADMIN");
		list.add(userDetails);
		userDetail.setUserDetails(list);
		Map<String, String> map = new HashMap<>();
		map.put(RegistrationConstants.USER_CENTER_ID, "10011");
		Map<String, String> userDetailErrorMap = new LinkedHashMap<>();
		LinkedHashMap<String, Object> responseMap=new LinkedHashMap<>();
		Map<String, Object> userDetailsMap = new HashMap<>();
		List<String> rolesList = new ArrayList<>();
		List<Object> userDetailsList = new ArrayList<>();
		rolesList.add("SUPERADMIN");
		userDetailsMap.put("userName", "mosip");
		userDetailsMap.put("mail", "superadmin@mosip.io");
		userDetailsMap.put("mobile", "999999999");
		userDetailsMap.put("userPassword",
				"e1NTSEE1MTJ9MERSeklnR2szMHpTNXJ2aVh6emRrZGdGaU9DWWZjbkVUVW5kNjQ3cXBXK0t1aExoTTNMR0t2LzZ3NUQranNjWmFoS1JGcklhdUJRZGZFRVZkcG82R2gzYVFqNXRUbWVQ");
		userDetailsMap.put("name", "superadmin");
		userDetailsMap.put("roles", rolesList);
		Map<String, Object> usrDetailMap = new HashMap<>();
		usrDetailMap.put("userDetails", userDetailsList);
		responseMap.put("response", usrDetailMap);
		doNothing().when(userDetailDAO).save(Mockito.any(UserDetailResponseDto.class));
		Mockito.when(userOnboardService.getMachineCenterId()).thenReturn(userDetailErrorMap);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(),Mockito.anyString()))
				.thenReturn(responseMap);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		userDetailServiceImpl.save("System");
	}
	
	@Test
	public void userDtlsTestFail() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		UserDetailResponseDto userDetail = new UserDetailResponseDto();
		List<UserDetailDto> list = new ArrayList<>();
		UserDetailDto userDetails = new UserDetailDto();
		userDetails.setUserName("110011");
		userDetails.setName("SUPERADMIN");
		list.add(userDetails);
		userDetail.setUserDetails(list);
		Map<String, String> map = new HashMap<>();
		map.put(RegistrationConstants.USER_CENTER_ID, "10011");
		LinkedHashMap<String, Object> responseMap=new LinkedHashMap<>();
		Map<String, Object> userDetailsMap = new LinkedHashMap<>();
		userDetailsMap.put("errorCode", "KER-SNC-303");
		userDetailsMap.put("message", "Registration center user not found ");
		List<Map<String, Object>> userFailureList=new ArrayList<>();
		userFailureList.add(userDetailsMap);
		responseMap.put("errors", userFailureList);
		doNothing().when(userDetailDAO).save(Mockito.any(UserDetailResponseDto.class));
		Mockito.when(userOnboardService.getMachineCenterId()).thenReturn(map);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(),Mockito.anyString()))
				.thenReturn(responseMap);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);
		userDetailServiceImpl.save("System");
	}
	
	@Test
	public void userDtlsFailNetwork() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		UserDetailResponseDto userDetail = new UserDetailResponseDto();
		List<UserDetailDto> list = new ArrayList<>();
		UserDetailDto userDetails = new UserDetailDto();
		userDetails.setUserName("110011");
		userDetails.setName("SUPERADMIN");
		list.add(userDetails);
		userDetail.setUserDetails(list);
		Map<String, String> map = new HashMap<>();
		map.put(RegistrationConstants.USER_CENTER_ID, "10011");
		Map<String, String> userDetailErrorMap = new LinkedHashMap<>();
		LinkedHashMap<String, Object> responseMap=new LinkedHashMap<>();
		Map<String, Object> userDetailsMap = new HashMap<>();
		List<String> rolesList = new ArrayList<>();
		List<Object> userDetailsList = new ArrayList<>();
		rolesList.add("SUPERADMIN");
		userDetailsMap.put("userName", "mosip");
		userDetailsMap.put("mail", "superadmin@mosip.io");
		userDetailsMap.put("mobile", "999999999");
		userDetailsMap.put("userPassword",
				"e1NTSEE1MTJ9MERSeklnR2szMHpTNXJ2aVh6emRrZGdGaU9DWWZjbkVUVW5kNjQ3cXBXK0t1aExoTTNMR0t2LzZ3NUQranNjWmFoS1JGcklhdUJRZGZFRVZkcG82R2gzYVFqNXRUbWVQ");
		userDetailsMap.put("name", "superadmin");
		userDetailsMap.put("roles", rolesList);
		Map<String, Object> usrDetailMap = new HashMap<>();
		usrDetailMap.put("userDetails", userDetailsList);
		responseMap.put("response", usrDetailMap);
		doNothing().when(userDetailDAO).save(Mockito.any(UserDetailResponseDto.class));
		Mockito.when(userOnboardService.getMachineCenterId()).thenReturn(userDetailErrorMap);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(),Mockito.anyString()))
				.thenReturn(responseMap);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(false);
		userDetailServiceImpl.save("System");
	}


}
