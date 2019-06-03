package io.mosip.registration.test.util.restclient;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Assert;
import org.junit.Before;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import io.mosip.registration.constants.LoginMode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.util.advice.RestClientAuthAdvice;
import io.mosip.registration.util.restclient.RequestHTTPDTO;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ApplicationContext.class, SessionContext.class })
public class RestClientAuthAdviceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;
	@Mock
	private ProceedingJoinPoint proceedingJoinPoint;
	@InjectMocks
	private RestClientAuthAdvice restClientAuthAdvice;

	@Before
	public void addAuthZTokens() throws Exception {
		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.mockStatic(SessionContext.class);
		AuthTokenDTO authTokenDTO = new AuthTokenDTO();
		authTokenDTO.setCookie("cookie");
		PowerMockito.doReturn(authTokenDTO).when(ApplicationContext.class, "authTokenDTO");
		PowerMockito.doReturn(authTokenDTO).when(SessionContext.class, "authTokenDTO");
		PowerMockito.when(SessionContext.isSessionContextAvailable()).thenReturn(true);
		Map<String, Object> value = new HashMap<>();
		LoginUserDTO loginUserDTO = new LoginUserDTO();
		loginUserDTO.setUserId("user");
		value.put(RegistrationConstants.USER_DTO, loginUserDTO);
		PowerMockito.when(ApplicationContext.map()).thenReturn(value);
	}

	@Test
	public void addAuthZTokenWithoutAuth() throws Throwable {
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		Object[] args = new Object[1];
		args[0] = requestHTTPDTO;
		Mockito.when(proceedingJoinPoint.getArgs()).thenReturn(args);
		Mockito.when(proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs())).thenReturn(new Object());
		
		Assert.assertNotNull(restClientAuthAdvice.addAuthZToken(proceedingJoinPoint));
	}

	@Test(expected=Throwable.class)
	public void addAuthZTokenException() throws Throwable {
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		Object[] args = new Object[1];
		args[0] = requestHTTPDTO;
		Mockito.when(proceedingJoinPoint.getArgs()).thenReturn(args);
		Mockito.when(proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs())).thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));
		
		restClientAuthAdvice.addAuthZToken(proceedingJoinPoint);
	}

	@Test
	public void addAuthZTokenUsingClientId() throws Throwable {
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		requestHTTPDTO.setAuthRequired(true);
		requestHTTPDTO.setAuthZHeader("Authorization:OAUTH");
		requestHTTPDTO.setClazz(Object.class);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		requestHTTPDTO.setHttpHeaders(httpHeaders);
		requestHTTPDTO.setHttpMethod(HttpMethod.POST);
		requestHTTPDTO.setTriggerPoint(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
		Object[] args = new Object[1];
		args[0] = requestHTTPDTO;
		Mockito.when(proceedingJoinPoint.getArgs()).thenReturn(args);
		Mockito.when(proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs())).thenReturn(new Object());
		Mockito.when(serviceDelegateUtil.isAuthTokenValid(Mockito.anyString())).thenReturn(true);
		
		Assert.assertNotNull(restClientAuthAdvice.addAuthZToken(proceedingJoinPoint));
	}

	@Test
	public void invalidAuthZTokenUsingClientId() throws Throwable {
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		requestHTTPDTO.setAuthRequired(true);
		requestHTTPDTO.setAuthZHeader("Authorization:OAUTH");
		requestHTTPDTO.setClazz(Object.class);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		requestHTTPDTO.setHttpHeaders(httpHeaders);
		requestHTTPDTO.setHttpMethod(HttpMethod.POST);
		requestHTTPDTO.setTriggerPoint(RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
		Object[] args = new Object[1];
		args[0] = requestHTTPDTO;
		Mockito.when(proceedingJoinPoint.getArgs()).thenReturn(args);
		Mockito.when(proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs())).thenReturn(new Object());
		Mockito.when(serviceDelegateUtil.isAuthTokenValid(Mockito.anyString())).thenReturn(false);
		Mockito.doNothing().when(serviceDelegateUtil).getAuthToken(Mockito.any(LoginMode.class));
		
		Assert.assertNotNull(restClientAuthAdvice.addAuthZToken(proceedingJoinPoint));
	}

	@Test
	public void addAuthZTokenUsingUserId() throws Throwable {
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		requestHTTPDTO.setAuthRequired(true);
		requestHTTPDTO.setAuthZHeader("Authorization:AUTH");
		requestHTTPDTO.setClazz(Object.class);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		requestHTTPDTO.setHttpHeaders(httpHeaders);
		requestHTTPDTO.setHttpMethod(HttpMethod.POST);
		requestHTTPDTO.setTriggerPoint(RegistrationConstants.JOB_TRIGGER_POINT_USER);
		Object[] args = new Object[1];
		args[0] = requestHTTPDTO;
		Mockito.when(proceedingJoinPoint.getArgs()).thenReturn(args);
		Mockito.when(proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs())).thenReturn(new Object());
		Mockito.when(serviceDelegateUtil.isAuthTokenValid(Mockito.anyString())).thenReturn(true);
		
		Assert.assertNotNull(restClientAuthAdvice.addAuthZToken(proceedingJoinPoint));
	}

	@Test
	public void invalidAuthZTokenUsingUserId() throws Throwable {
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		requestHTTPDTO.setAuthRequired(true);
		requestHTTPDTO.setClazz(Object.class);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		requestHTTPDTO.setHttpHeaders(httpHeaders);
		requestHTTPDTO.setHttpMethod(HttpMethod.POST);
		requestHTTPDTO.setTriggerPoint(RegistrationConstants.JOB_TRIGGER_POINT_USER);
		Object[] args = new Object[1];
		args[0] = requestHTTPDTO;
		Mockito.when(proceedingJoinPoint.getArgs()).thenReturn(args);
		Mockito.when(proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs())).thenReturn(new Object());
		Mockito.when(serviceDelegateUtil.isAuthTokenValid(Mockito.anyString())).thenReturn(false);
		Mockito.doNothing().when(serviceDelegateUtil).getAuthToken(Mockito.any(LoginMode.class));
		
		Assert.assertNotNull(restClientAuthAdvice.addAuthZToken(proceedingJoinPoint));
	}

	@Test
	public void invalidAuthZTokenUsingUserIdPwd() throws Throwable {
		RequestHTTPDTO requestHTTPDTO = new RequestHTTPDTO();
		requestHTTPDTO.setAuthRequired(true);
		requestHTTPDTO.setAuthZHeader("Authorization:BASIC");
		requestHTTPDTO.setClazz(Object.class);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		requestHTTPDTO.setHttpHeaders(httpHeaders);
		requestHTTPDTO.setHttpMethod(HttpMethod.POST);
		requestHTTPDTO.setTriggerPoint(RegistrationConstants.JOB_TRIGGER_POINT_USER);
		Object[] args = new Object[1];
		args[0] = requestHTTPDTO;
		Mockito.when(proceedingJoinPoint.getArgs()).thenReturn(args);
		Mockito.when(proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs())).thenReturn(new Object());
		Mockito.when(serviceDelegateUtil.isAuthTokenValid(Mockito.anyString())).thenReturn(false);
		Mockito.doNothing().when(serviceDelegateUtil).getAuthToken(Mockito.any(LoginMode.class));
		PowerMockito.when(SessionContext.isSessionContextAvailable()).thenReturn(false);
		Map<String, Object> value = new HashMap<>();
		LoginUserDTO loginUserDTO = new LoginUserDTO();
		loginUserDTO.setUserId("user");
		loginUserDTO.setPassword("password");
		value.put(RegistrationConstants.USER_DTO, loginUserDTO);
		PowerMockito.when(ApplicationContext.map()).thenReturn(value);
		
		Assert.assertNotNull(restClientAuthAdvice.addAuthZToken(proceedingJoinPoint));
	}

}
