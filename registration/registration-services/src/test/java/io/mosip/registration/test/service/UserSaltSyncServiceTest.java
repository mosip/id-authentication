package io.mosip.registration.test.service;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
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

import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.repositories.UserDetailRepository;
import io.mosip.registration.service.operator.impl.UserSaltDetailsServiceImpl;
import io.mosip.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ RegistrationAppHealthCheckUtil.class })
public class UserSaltSyncServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;

	@Mock
	private UserDetailRepository userDetailRepository;
	@InjectMocks
	private UserSaltDetailsServiceImpl userSaltDetailsServiceImpl;

	@SuppressWarnings("unchecked")
	@Test
	public void getSaltDetails() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		LinkedHashMap<String, Object> arrayMap = new LinkedHashMap<>();
		LinkedHashMap<String, Object> mainMap = new LinkedHashMap<>();
		List<LinkedHashMap<String, Object>> respList = new ArrayList<>();
		LinkedHashMap<String, Object> response = new LinkedHashMap<>();

		response.put("userId", "110024");
		response.put("salt", "F025586A2143B63D");
		respList.add(response);
		arrayMap.put("mosipUserSaltList", respList);

		mainMap.put("response", arrayMap);
		mainMap.put("errors", null);
		mainMap.put("responsetime", "2019-04-30T10:51:03.385Z");

		List<UserDetail> userList = new ArrayList<>();
		UserDetail users = new UserDetail();
		users.setId("110024");
		users.setIsActive(true);
		userList.add(users);

		Mockito.when(
				serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenReturn(mainMap);

		Mockito.when(userDetailRepository.findByIsActiveTrue()).thenReturn(userList);

		Mockito.when(userDetailRepository.saveAll(Mockito.anyCollection())).thenReturn(userList);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		userSaltDetailsServiceImpl.getUserSaltDetails("System");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getSaltDetailsError() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		LinkedHashMap<String, Object> mainMap = new LinkedHashMap<>();
		Map<String, Object> errorMap = new LinkedHashMap<>();
		errorMap.put("errorCode", "KER-ATH-008");
		errorMap.put("message", "Invalid datasource and Please check the application id");
		List<Map<String, Object>> userSaltFailureList = new ArrayList<>();
		userSaltFailureList.add(errorMap);
		mainMap.put("response", null);
		mainMap.put("errors", userSaltFailureList);
		mainMap.put("responsetime", "2019-04-30T10:51:03.385Z");

		List<UserDetail> userList = new ArrayList<>();
		UserDetail users = new UserDetail();
		users.setId("110024");
		users.setIsActive(true);
		userList.add(users);

		Mockito.when(
				serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenReturn(mainMap);

		Mockito.when(userDetailRepository.findByIsActiveTrue()).thenReturn(userList);

		Mockito.when(userDetailRepository.saveAll(Mockito.anyCollection())).thenReturn(userList);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		userSaltDetailsServiceImpl.getUserSaltDetails("System");
	}

	@Test
	public void getSaltDetailsErrorList()
			throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);

		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(false);

		userSaltDetailsServiceImpl.getUserSaltDetails("System");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getSaltDetailsEmpty() throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		LinkedHashMap<String, Object> arrayMap = new LinkedHashMap<>();
		LinkedHashMap<String, Object> mainMap = new LinkedHashMap<>();
		List<LinkedHashMap<String, Object>> respList = new ArrayList<>();
		LinkedHashMap<String, Object> response = new LinkedHashMap<>();

		response.put("userId", "110024");
		response.put("salt", "F025586A2143B63D");
		respList.add(response);
		arrayMap.put("mosipUserSaltList", respList);

		mainMap.put("response", arrayMap);
		mainMap.put("errors", null);
		mainMap.put("responsetime", "2019-04-30T10:51:03.385Z");

		List<UserDetail> userList = new ArrayList<>();

		Mockito.when(
				serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenReturn(mainMap);

		Mockito.when(userDetailRepository.findByIsActiveTrue()).thenReturn(userList);

		Mockito.when(userDetailRepository.saveAll(Mockito.anyCollection())).thenReturn(userList);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		userSaltDetailsServiceImpl.getUserSaltDetails("System");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getSaltDetailsException()
			throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		LinkedHashMap<String, Object> arrayMap = new LinkedHashMap<>();
		LinkedHashMap<String, Object> mainMap = new LinkedHashMap<>();
		List<LinkedHashMap<String, Object>> respList = new ArrayList<>();
		LinkedHashMap<String, Object> response = new LinkedHashMap<>();

		response.put("userId", "110024");
		response.put("salt", "F025586A2143B63D");
		respList.add(response);
		arrayMap.put("mosipUserSaltList", respList);

		mainMap.put("response", arrayMap);
		mainMap.put("errors", null);
		mainMap.put("responsetime", "2019-04-30T10:51:03.385Z");

		List<UserDetail> userList = new ArrayList<>();
		UserDetail users = new UserDetail();
		users.setId("110024");
		users.setIsActive(true);
		userList.add(users);

		Mockito.when(
				serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenReturn(mainMap);

		Mockito.when(userDetailRepository.findByIsActiveTrue()).thenThrow(RegBaseCheckedException.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		userSaltDetailsServiceImpl.getUserSaltDetails("System");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getSaltDetailsNetworkException()
			throws HttpClientErrorException, SocketTimeoutException, RegBaseCheckedException {

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		LinkedHashMap<String, Object> arrayMap = new LinkedHashMap<>();
		LinkedHashMap<String, Object> mainMap = new LinkedHashMap<>();
		List<LinkedHashMap<String, Object>> respList = new ArrayList<>();
		LinkedHashMap<String, Object> response = new LinkedHashMap<>();

		response.put("userId", "110024");
		response.put("salt", "F025586A2143B63D");
		respList.add(response);
		arrayMap.put("mosipUserSaltList", respList);

		mainMap.put("response", arrayMap);
		mainMap.put("errors", null);
		mainMap.put("responsetime", "2019-04-30T10:51:03.385Z");

		List<UserDetail> userList = new ArrayList<>();
		UserDetail users = new UserDetail();
		users.setId("110024");
		users.setIsActive(true);
		userList.add(users);

		Mockito.when(
				serviceDelegateUtil.get(Mockito.anyString(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyString()))
				.thenThrow(HttpClientErrorException.class);
		Mockito.when(RegistrationAppHealthCheckUtil.isNetworkAvailable()).thenReturn(true);

		userSaltDetailsServiceImpl.getUserSaltDetails("System");
	}

}
