package io.mosip.registration.test.login;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.dto.OtpGeneratorRequestDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.util.restclient.RequestHTTPDTO;
import io.mosip.registration.util.restclient.RestClientUtil;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ApplicationContext.class })
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

		ReflectionTestUtils.setField(delegateUtil, "urlPath",
				"https://integ.mosip.io/authmanager/authenticate/unpwd");

		LoginUserDTO loginDto = new LoginUserDTO();
		loginDto.setUserId("super_admin");
		loginDto.setPassword("super_admin");
		ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
		PowerMockito.mockStatic(ApplicationContext.class);
		PowerMockito.when(ApplicationContext.getInstance()).thenReturn(applicationContext);
		Map<String, Object> globalParams = new HashMap<>();
		globalParams.put("userDTO", loginDto);
		PowerMockito.when(ApplicationContext.getInstance().getApplicationMap().get("userDTO")).thenReturn(globalParams);
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

		when(restClientUtil.invoke(Mockito.any())).thenReturn(response);
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.USERNAME_KEY, "yashReddy");
		requestParamMap.put(RegistrationConstants.OTP, "099886");
		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		Mockito.when(restClientUtil.invokeHeaders((Mockito.anyObject()))).thenReturn(header);
		assertNotNull(delegateUtil.get("otp_validator", requestParamMap, false));
		assertNotNull(delegateUtil.get("otp_validator", requestParamMap, true));
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

		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		Mockito.when(restClientUtil.invokeHeaders((Mockito.anyObject()))).thenReturn(header);
		
		when(restClientUtil.invoke(Mockito.any())).thenReturn(response);
		OtpGeneratorRequestDTO generatorRequestDto = new OtpGeneratorRequestDTO();
		generatorRequestDto.setKey("yashReddy");
		assertNotNull(delegateUtil.post("otp_generator", generatorRequestDto));
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

		when(restClientUtil.invoke(Mockito.any())).thenReturn(response);
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.USERNAME_KEY, "yashReddy");
		requestParamMap.put(RegistrationConstants.OTP, "099886");
		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		Mockito.when(restClientUtil.invokeHeaders((Mockito.anyObject()))).thenReturn(header);
		assertNotNull(delegateUtil.get("otp_validator", requestParamMap, false));
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

		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		Mockito.when(restClientUtil.invokeHeaders((Mockito.anyObject()))).thenReturn(header);
		
		when(restClientUtil.invoke(Mockito.any())).thenReturn(response);
		OtpGeneratorRequestDTO generatorRequestDto = new OtpGeneratorRequestDTO();
		generatorRequestDto.setKey("yashReddy");
		assertNotNull(delegateUtil.post("otp_generator", generatorRequestDto));
	}
	
	

}
