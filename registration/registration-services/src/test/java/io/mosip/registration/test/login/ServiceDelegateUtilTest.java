package io.mosip.registration.test.login;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.constants.LoginMode;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.dto.OtpGeneratorRequestDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.util.restclient.RequestHTTPDTO;
import io.mosip.registration.util.restclient.RestClientUtil;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ApplicationContext.class, SessionContext.class })
public class ServiceDelegateUtilTest {
	@Mock
	private RestClientUtil restClientUtil;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private ServiceDelegateUtil delegateUtil;

	@Mock
	private Environment environment;
	
	@Mock
	private RequestHTTPDTO requestHTTPDTO;
	
	@Before
	public void initialize() throws IOException, URISyntaxException {

		ReflectionTestUtils.setField(delegateUtil, "urlPath", "https://integ.mosip.io/authmanager/v1.0/authorize/validateToken");
		ReflectionTestUtils.setField(delegateUtil, "invalidateUrlPath", "https://integ.mosip.io/authmanager/v1.0/authorize/invalidateToken");
		ReflectionTestUtils.setField(delegateUtil, "clientId", "clientId");
		ReflectionTestUtils.setField(delegateUtil, "secretKey", "secretKey");

		LoginUserDTO loginDto = new LoginUserDTO();
		loginDto.setUserId("super_admin");
		loginDto.setPassword("super_admin");
		loginDto.setOtp("123456");

		PowerMockito.mockStatic(ApplicationContext.class);
		Map<String, Object> globalParams = new HashMap<>();
		globalParams.put(RegistrationConstants.USER_DTO, loginDto);
		globalParams.put(RegistrationConstants.REGISTRATION_CLIENT, "registrationclient");
		PowerMockito.when(ApplicationContext.map()).thenReturn(globalParams);
	}

	/*
	 * @Test public void getURITest() {
	 * 
	 * Map<String, String> requestParamMap = new HashMap<String, String>();
	 * requestParamMap.put(RegistrationConstants.USERNAME_KEY, "yashReddy");
	 * requestParamMap.put(RegistrationConstants.OTP_GENERATED, "099887");
	 * Assert.assertEquals(delegateUtil.getUri(requestParamMap,
	 * "http://localhost:8080/otpmanager/otps").toString(),
	 * "http://localhost:8080/otpmanager/otps?otp=099887&key=yashReddy"); }
	 */

	@Test
	public void getRequestTest() throws Exception {

		ResponseDTO response = new ResponseDTO();
		when(environment.getProperty("otp_validator.service.httpmethod")).thenReturn("GET");
		when(environment.getProperty("otp_validator.service.url")).thenReturn("http://localhost:8080/otpmanager/otps");
		when(environment.getProperty("otp_validator.service.responseType"))
				.thenReturn("io.mosip.registration.dto.OtpValidatorResponseDTO");
		when(environment.getProperty("otp_validator.service.headers")).thenReturn("Content-Type:APPLICATION/JSON");
		when(environment.getProperty("otp_validator.service.authrequired")).thenReturn("false");
		when(environment.getProperty("otp_validator.service.authheader")).thenReturn("Authorization:BASIC");
        Map<String,Object> responseMap=new HashMap<>();
		when(restClientUtil.invoke(Mockito.any())).thenReturn(responseMap);
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.USERNAME_KEY, "yashReddy");
		requestParamMap.put(RegistrationConstants.OTP, "099886");
		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		responseMap.put("responseHeader", header);
		responseMap.put("responseBody", response);
		Mockito.when(restClientUtil.invoke((Mockito.anyObject()))).thenReturn(responseMap);
		assertNotNull(delegateUtil.get("otp_validator", requestParamMap, false,"System"));
	}

	@Test
	public void postRequestTest() throws URISyntaxException, HttpClientErrorException, RegBaseCheckedException,
			HttpServerErrorException, ResourceAccessException, SocketTimeoutException {

		ResponseDTO response = new ResponseDTO();
		when(environment.getProperty("otp_generator.service.httpmethod")).thenReturn("POST");
		when(environment.getProperty("otp_generator.service.url")).thenReturn("http://localhost:8080/otpmanager/otps");
		when(environment.getProperty("otp_generator.service.requestType"))
				.thenReturn("io.mosip.registration.dto.OtpGeneratorResponseDTO");
		when(environment.getProperty("otp_generator.service.headers")).thenReturn("Content-Type:APPLICATION/JSON");
		when(environment.getProperty("otp_generator.service.authrequired")).thenReturn("false");
		when(environment.getProperty("otp_generator.service.authheader")).thenReturn("Authorization:BASIC");
		Map<String,Object> responseMap=new HashMap<>();
		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		responseMap.put("responseHeader", header);
		responseMap.put("responseBody", response);		
		when(restClientUtil.invoke(Mockito.any())).thenReturn(responseMap);
		OtpGeneratorRequestDTO generatorRequestDto = new OtpGeneratorRequestDTO();
		generatorRequestDto.setKey("yashReddy");
		assertNotNull(delegateUtil.post("otp_generator", generatorRequestDto,"System"));
	}
	
	@Test
	public void getRequestTestTrue() throws Exception {

		ResponseDTO response = new ResponseDTO();
		when(environment.getProperty("otp_validator.service.httpmethod")).thenReturn("GET");
		when(environment.getProperty("otp_validator.service.url")).thenReturn("http://localhost:8080/otpmanager/otps");
		when(environment.getProperty("otp_validator.service.responseType"))
				.thenReturn("io.mosip.registration.dto.OtpValidatorResponseDTO");
		when(environment.getProperty("otp_validator.service.headers")).thenReturn("Content-Type:APPLICATION/JSON");
		when(environment.getProperty("otp_validator.service.authrequired")).thenReturn("true");
		when(environment.getProperty("otp_validator.service.authheader")).thenReturn("Authorization:BASIC");
		Map<String,Object> responseMap=new HashMap<>();
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.USERNAME_KEY, "yashReddy");
		requestParamMap.put(RegistrationConstants.OTP, "099886");
		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		responseMap.put("responseHeader", header);
		responseMap.put("responseBody", response);
		Mockito.when(restClientUtil.invoke((Mockito.anyObject()))).thenReturn(responseMap);
		assertNotNull(delegateUtil.get("otp_validator", requestParamMap, false,"System"));
	}
	
	@Test
	public void postRequest() throws URISyntaxException, HttpClientErrorException, RegBaseCheckedException,
			HttpServerErrorException, ResourceAccessException, SocketTimeoutException {

		ResponseDTO response = new ResponseDTO();
		when(environment.getProperty("otp_generator.service.httpmethod")).thenReturn("POST");
		when(environment.getProperty("otp_generator.service.url")).thenReturn("http://localhost:8080/otpmanager/otps");
		when(environment.getProperty("otp_generator.service.requestType"))
				.thenReturn("io.mosip.registration.dto.OtpGeneratorResponseDTO");
		when(environment.getProperty("otp_generator.service.headers")).thenReturn("Content-Type:APPLICATION/JSON");
		when(environment.getProperty("otp_generator.service.authrequired")).thenReturn("true");
		when(environment.getProperty("otp_generator.service.authheader")).thenReturn("Authorization:oauth");
		Map<String,Object> responseMap=new HashMap<>();
		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		responseMap.put("responseHeader", header);
		responseMap.put("responseBody", response);		
		when(restClientUtil.invoke(Mockito.any())).thenReturn(responseMap);
		OtpGeneratorRequestDTO generatorRequestDto = new OtpGeneratorRequestDTO();
		generatorRequestDto.setKey("yashReddy");
		assertNotNull(delegateUtil.post("otp_generator", generatorRequestDto,"System"));
	}

	@Test
	public void getAuthTokenByPassword() throws Exception {
		// Return Object
		Map<String, Object> responseMap = new HashMap<>();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(RegistrationConstants.AUTH_SET_COOKIE, "Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMTAwMTEiLCJtb2JpbGUiOiI5NzkxOTQxODE1IiwibWFpbCI6ImJhbGFqaS5zcmlkaGFyYW5AbWluZHRyZWUuY29tIiwicm9sZSI6IlNVUEVSQURNSU4iLCJuYW1lIjoiMTEwMDExIiwiaWF0IjoxNTUyOTc2NDM0LCJleHAiOjE1NTI5NzgyMzR9.csY86SauoeLayfdKO2hALz9nvTipM2Rx9Ri4KkZKTAK5CDegx_AgkaGXPgKDSxtIbFNQtZHRDjVDuaRfd5_z8A; Max-Age=1800000; Expires=Tue, 09-Apr-2019 02:20:34 GMT; Path=/; Secure; HttpOnly");
		responseMap.put(RegistrationConstants.REST_RESPONSE_HEADERS, httpHeaders);

		// Mocking Method Calls
		when(environment.getProperty("auth_by_password.service.url")).thenReturn("https://integ.mosip.io/authmanager/v1.0/authenticate/useridPwd");
		when(restClientUtil.invoke(Mockito.any(RequestHTTPDTO.class))).thenReturn(responseMap);
		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.doNothing().when(SessionContext.class, "setAuthTokenDTO", Mockito.any());

		delegateUtil.getAuthToken(LoginMode.PASSWORD);
	}
	
	@Test(expected=RegBaseCheckedException.class)
	public void getAuthTokenByOTP() throws Exception {
		// Return Object
		Map<String, Object> responseMap = new HashMap<>();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(RegistrationConstants.AUTH_SET_COOKIE, "Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMTAwMTEiLCJtb2JpbGUiOiI5NzkxOTQxODE1IiwibWFpbCI6ImJhbGFqaS5zcmlkaGFyYW5AbWluZHRyZWUuY29tIiwicm9sZSI6IlNVUEVSQURNSU4iLCJuYW1lIjoiMTEwMDExIiwiaWF0IjoxNTUyOTc2NDM0LCJleHAiOjE1NTI5NzgyMzR9.csY86SauoeLayfdKO2hALz9nvTipM2Rx9Ri4KkZKTAK5CDegx_AgkaGXPgKDSxtIbFNQtZHRDjVDuaRfd5_z8A; Max-Age=1800000; Expires=Tue, 09-Apr-2019 02:20:34 GMT; Path=/; Secure; HttpOnly");
		responseMap.put(RegistrationConstants.REST_RESPONSE_HEADERS, httpHeaders);
		Map<String, Object> responseBody = new LinkedHashMap<>();
		Map<String, String> response = new LinkedHashMap<>();
		response.put("message", "OTP expired");
		responseBody.put("response", response);
		responseMap.put(RegistrationConstants.REST_RESPONSE_BODY, responseBody);

		// Mocking Method Calls
		when(environment.getProperty("auth_by_otp.service.url")).thenReturn("https://integ.mosip.io/authmanager/v1.0/authenticate/useridOTP");
		when(restClientUtil.invoke(Mockito.any(RequestHTTPDTO.class))).thenReturn(responseMap);

		delegateUtil.getAuthToken(LoginMode.OTP);
	}
	
	@Test
	public void getAuthTokenByClientId() throws Exception {
		// Return Object
		Map<String, Object> responseMap = new HashMap<>();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(RegistrationConstants.AUTH_SET_COOKIE, "Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMTAwMTEiLCJtb2JpbGUiOiI5NzkxOTQxODE1IiwibWFpbCI6ImJhbGFqaS5zcmlkaGFyYW5AbWluZHRyZWUuY29tIiwicm9sZSI6IlNVUEVSQURNSU4iLCJuYW1lIjoiMTEwMDExIiwiaWF0IjoxNTUyOTc2NDM0LCJleHAiOjE1NTI5NzgyMzR9.csY86SauoeLayfdKO2hALz9nvTipM2Rx9Ri4KkZKTAK5CDegx_AgkaGXPgKDSxtIbFNQtZHRDjVDuaRfd5_z8A; Max-Age=1800000; Expires=Tue, 09-Apr-2019 02:20:34 GMT; Path=/; Secure; HttpOnly");
		responseMap.put(RegistrationConstants.REST_RESPONSE_HEADERS, httpHeaders);

		// Mocking Method Calls
		when(environment.getProperty("auth_by_clientid_secretkey.service.url")).thenReturn("https://integ.mosip.io/authmanager/v1.0/authenticate/clientidsecretkey");
		when(restClientUtil.invoke(Mockito.any(RequestHTTPDTO.class))).thenReturn(responseMap);
		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doNothing().when(ApplicationContext.class, "setAuthTokenDTO", Mockito.any(AuthTokenDTO.class));

		delegateUtil.getAuthToken(LoginMode.CLIENTID);
	}
	
	@Test(expected=RegBaseCheckedException.class)
	public void getAuthTokenInvalidResponse() throws Exception {
		// Return Object
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(RegistrationConstants.REST_RESPONSE_HEADERS, new HttpHeaders());

		// Mocking Method Calls
		when(environment.getProperty("auth_by_clientid_secretkey.service.url")).thenReturn("https://integ.mosip.io/authmanager/v1.0/authenticate/clientidsecretkey");
		when(restClientUtil.invoke(Mockito.any(RequestHTTPDTO.class))).thenReturn(responseMap);
		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doNothing().when(ApplicationContext.class, "setAuthTokenDTO", Mockito.any(AuthTokenDTO.class));

		delegateUtil.getAuthToken(LoginMode.CLIENTID);
	}
	
	@Test(expected=RegBaseUncheckedException.class)
	public void getAuthTokenRuntimeException() throws Exception {
		// Return Object
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(RegistrationConstants.REST_RESPONSE_HEADERS, null);

		// Mocking Method Calls
		when(environment.getProperty("auth_by_clientid_secretkey.service.url")).thenReturn("https://integ.mosip.io/authmanager/v1.0/authenticate/clientidsecretkey");
		when(restClientUtil.invoke(Mockito.any(RequestHTTPDTO.class))).thenReturn(responseMap);
		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doNothing().when(ApplicationContext.class, "setAuthTokenDTO", Mockito.any(AuthTokenDTO.class));

		delegateUtil.getAuthToken(LoginMode.CLIENTID);
	}
	
	@Test(expected=RegBaseCheckedException.class)
	public void getAuthTokenCheckedException() throws Exception {

		// Mocking Method Calls
		when(environment.getProperty("auth_by_clientid_secretkey.service.url")).thenReturn("https://integ.mosip.io/authmanager/v1.0/authenticate/clientidsecretkey");
		when(restClientUtil.invoke(Mockito.any(RequestHTTPDTO.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));
		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.doNothing().when(ApplicationContext.class, "setAuthTokenDTO", Mockito.any(AuthTokenDTO.class));

		delegateUtil.getAuthToken(LoginMode.CLIENTID);
	}

	@Test
	public void isAuthTokenValidNullCookie() {
		Assert.assertFalse(delegateUtil.isAuthTokenValid(null));
	}

	@Test
	public void isAuthTokenValidTest() throws Exception {
		// Return Object
		Map<String, Object> responseMap = new HashMap<>();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(RegistrationConstants.AUTH_SET_COOKIE, "Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMTAwMTEiLCJtb2JpbGUiOiI5NzkxOTQxODE1IiwibWFpbCI6ImJhbGFqaS5zcmlkaGFyYW5AbWluZHRyZWUuY29tIiwicm9sZSI6IlNVUEVSQURNSU4iLCJuYW1lIjoiMTEwMDExIiwiaWF0IjoxNTUyOTc2NDM0LCJleHAiOjE1NTI5NzgyMzR9.csY86SauoeLayfdKO2hALz9nvTipM2Rx9Ri4KkZKTAK5CDegx_AgkaGXPgKDSxtIbFNQtZHRDjVDuaRfd5_z8A; Max-Age=1800000; Expires=Tue, 09-Apr-2019 02:20:34 GMT; Path=/; Secure; HttpOnly");
		responseMap.put(RegistrationConstants.REST_RESPONSE_HEADERS, httpHeaders);
		Map<String, String> responseBody = new LinkedHashMap<>();
		responseBody.put("message", "Validated Successfully");
		responseMap.put(RegistrationConstants.REST_RESPONSE_BODY, responseBody);

		// Mocking Method Calls
		when(restClientUtil.invoke(Mockito.any(RequestHTTPDTO.class))).thenReturn(responseMap);
		
		Assert.assertTrue(delegateUtil.isAuthTokenValid("authZToken"));
	}

	@Test
	public void isAuthTokenValidCheckedExpection() throws Exception {

		// Mocking Method Calls
		when(restClientUtil.invoke(Mockito.any(RequestHTTPDTO.class))).thenThrow(new NullPointerException());
		
		Assert.assertFalse(delegateUtil.isAuthTokenValid("authZToken"));
	}

	@Test
	public void isAuthTokenValidUncheckedExpection() throws Exception {

		// Mocking Method Calls
		when(restClientUtil.invoke(Mockito.any(RequestHTTPDTO.class))).thenThrow(new SocketTimeoutException());
		
		Assert.assertFalse(delegateUtil.isAuthTokenValid("authZToken"));
	}

	@Test
	public void invalidateTokenNullCookie() {
		delegateUtil.invalidateToken(null);
	}

	@Test
	public void invalidateTokenTest() throws Exception {
		// Return Object
		Map<String, Object> responseMap = new HashMap<>();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(RegistrationConstants.AUTH_SET_COOKIE, "Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMTAwMTEiLCJtb2JpbGUiOiI5NzkxOTQxODE1IiwibWFpbCI6ImJhbGFqaS5zcmlkaGFyYW5AbWluZHRyZWUuY29tIiwicm9sZSI6IlNVUEVSQURNSU4iLCJuYW1lIjoiMTEwMDExIiwiaWF0IjoxNTUyOTc2NDM0LCJleHAiOjE1NTI5NzgyMzR9.csY86SauoeLayfdKO2hALz9nvTipM2Rx9Ri4KkZKTAK5CDegx_AgkaGXPgKDSxtIbFNQtZHRDjVDuaRfd5_z8A; Max-Age=1800000; Expires=Tue, 09-Apr-2019 02:20:34 GMT; Path=/; Secure; HttpOnly");
		responseMap.put(RegistrationConstants.REST_RESPONSE_HEADERS, httpHeaders);
		Map<String, String> responseBody = new LinkedHashMap<>();
		responseBody.put("message", "Validated Successfully");
		responseMap.put(RegistrationConstants.REST_RESPONSE_BODY, responseBody);

		// Mocking Method Calls
		when(restClientUtil.invoke(Mockito.any(RequestHTTPDTO.class))).thenReturn(responseMap);
		
		delegateUtil.invalidateToken("authZToken");
	}

	@Test
	public void invalidateTokenCheckedExpection() throws Exception {

		// Mocking Method Calls
		when(restClientUtil.invoke(Mockito.any(RequestHTTPDTO.class))).thenThrow(new NullPointerException());
		
		delegateUtil.invalidateToken("authZToken");
	}

	@Test
	public void invalidateTokenUncheckedExpection() throws Exception {

		// Mocking Method Calls
		when(restClientUtil.invoke(Mockito.any(RequestHTTPDTO.class))).thenThrow(new SocketTimeoutException());
		
		delegateUtil.invalidateToken("authZToken");
	}

}
