package io.mosip.preregistration.login.test.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.mosip.preregistration.core.common.dto.AuthNResponse;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.login.dto.MainRequestDTO;
import io.mosip.preregistration.login.dto.MainResponseDTO;
import io.mosip.preregistration.login.dto.OtpRequestDTO;
import io.mosip.preregistration.login.dto.OtpUser;
import io.mosip.preregistration.login.dto.OtpUserDTO;
import io.mosip.preregistration.login.dto.User;
import io.mosip.preregistration.login.errorcodes.ErrorCodes;
import io.mosip.preregistration.login.errorcodes.ErrorMessages;
import io.mosip.preregistration.login.exception.ConfigFileNotFoundException;
import io.mosip.preregistration.login.exception.InvalidateTokenException;
import io.mosip.preregistration.login.exception.SendOtpFailedException;
import io.mosip.preregistration.login.exception.UserIdOtpFaliedException;
import io.mosip.preregistration.login.service.LoginService;
import io.mosip.preregistration.login.util.LoginCommonUtil;
import junit.framework.Assert;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginServiceTest {

	@Mock
	private MainResponseDTO<AuthNResponse> mainResponseDTO;
	@Mock
	private MainRequestDTO<OtpRequestDTO> otpRequest;
	@Mock
	MainRequestDTO<User> userRequest;
	@Mock
	private OtpRequestDTO otp;
	@Mock
	private User user;
	@MockBean
	private LoginCommonUtil authCommonUtil;
	@Mock
	private OtpUser otpUser;
	@Mock
	private OtpUserDTO otpUserDto;
	@Mock
	private ResponseEntity<String> responseEntity;
	@Mock
	private AuthNResponse authNResposne;
	@Autowired
	@InjectMocks
	private LoginService authService;
	private LoginService spyAuthService;
	private List<String> list;
	private Map<String,String> requestMap;
	@Before
	public void setUp() {
		 list=new ArrayList<>();
		 spyAuthService=Mockito.spy(authService);
		 //responseEntity=new ResponseEntity<AuthNResponse>(authNResposne, HttpStatus.OK);
		 requestMap=new HashMap<>();
		 requestMap.put("version", "1.0");
	}
	@Test
	public void sendOtpTest() throws Exception {
		list.add("mobile");
		requestMap.put("id","mosip.pre-registration.login.sendotp");
		Mockito.when(authCommonUtil.prepareRequestMap(otpRequest)).thenReturn(requestMap);
		Mockito.when(otpRequest.getRequest()).thenReturn(otp);
		Mockito.when(authCommonUtil.validateUserIdAndLangCode(Mockito.any(),Mockito.any())).thenReturn(list);
		Mockito.doNothing().when(otpUserDto).setRequest(otpUser);
		Mockito.doReturn(responseEntity).when(authCommonUtil).getResponseEntity(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any());
		Mockito.doReturn(mainResponseDTO).when(authCommonUtil).getMainResponseDto(Mockito.any());
		Mockito.when(responseEntity.getBody()).thenReturn("authNResposne");
		Mockito.doNothing().when(spyAuthService).setAuditValues(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		authNResposne.setMessage("success");
		Mockito.doNothing().when(mainResponseDTO).setResponse(Mockito.any());
		assertNotNull(spyAuthService.sendOTP(otpRequest));
	}
	
	@Test(expected=InvalidRequestParameterException.class)
	public void sendOtpTest_Exception() throws Exception {
		requestMap.put("id","mosip.pre-registration.login.sendotp");
		Mockito.when(authCommonUtil.prepareRequestMap(otpRequest)).thenReturn(requestMap);
		Mockito.when(otpRequest.getRequest()).thenReturn(otp);
		Mockito.when(authCommonUtil.validateUserIdAndLangCode(Mockito.any(),Mockito.any())).thenReturn(list);
		Mockito.doNothing().when(otpUserDto).setRequest(otpUser);
		Mockito.doThrow(new InvalidRequestParameterException("errorCode", "errorMessage")).when(authCommonUtil).getResponseEntity(Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.doReturn(mainResponseDTO).when(authCommonUtil).getMainResponseDto(Mockito.any());
		Mockito.when(responseEntity.getBody()).thenReturn("authNResposne");
		Mockito.doNothing().when(spyAuthService).setAuditValues(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		authNResposne.setMessage("success");
		Mockito.doNothing().when(mainResponseDTO).setResponse(Mockito.any());
		spyAuthService.sendOTP(otpRequest);
	}
	
	@Test(expected=InvalidRequestParameterException.class)
	public void sendOtpTest_AuthException() throws Exception {
		requestMap.put("id","mosip.pre-registration.login.sendotp");
		Mockito.when(authCommonUtil.prepareRequestMap(otpRequest)).thenReturn(requestMap);
		Mockito.when(otpRequest.getRequest()).thenReturn(otp);
		Mockito.when(authCommonUtil.validateUserIdAndLangCode(Mockito.any(),Mockito.any())).thenReturn(list);
		Mockito.doNothing().when(otpUserDto).setRequest(otpUser);
		Mockito.doThrow(new InvalidRequestParameterException("errorCode","errorMessage")).when(authCommonUtil).getResponseEntity(Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.doReturn(mainResponseDTO).when(authCommonUtil).getMainResponseDto(Mockito.any());
		Mockito.when(responseEntity.getBody()).thenReturn("authNResposne");
		Mockito.doNothing().when(spyAuthService).setAuditValues(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		authNResposne.setMessage("success");
		Mockito.doNothing().when(mainResponseDTO).setResponse(Mockito.any());
		spyAuthService.sendOTP(otpRequest);
	}
	@Test
	public void validateWithUserIdOtp() {
		requestMap.put("id","mosip.pre-registration.login.useridotp");
		Mockito.when(authCommonUtil.prepareRequestMap(otpRequest)).thenReturn(requestMap);
		Mockito.when(userRequest.getRequest()).thenReturn(user);
		Mockito.doReturn(mainResponseDTO).when(authCommonUtil).getMainResponseDto(userRequest);
		Mockito.doReturn(responseEntity).when(authCommonUtil).getResponseEntity(Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.when(responseEntity.getBody()).thenReturn("authNResposne");
		Mockito.doNothing().when(spyAuthService).setAuditValues(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		assertNotNull(spyAuthService.validateWithUserIdOtp(userRequest));
	}
	
	@Test(expected=InvalidRequestParameterException.class)
	public void validateWithUserIdOtp_Exception()  {
		requestMap.put("id","mosip.pre-registration.login.useridotp");
		Mockito.when(authCommonUtil.prepareRequestMap(otpRequest)).thenReturn(requestMap);
		Mockito.when(userRequest.getRequest()).thenReturn(user);
		Mockito.doReturn(mainResponseDTO).when(authCommonUtil).getMainResponseDto(userRequest);
		Mockito.doThrow(new InvalidRequestParameterException("errorCode","errorMessage")).when(authCommonUtil).getResponseEntity(Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.doNothing().when(spyAuthService).setAuditValues(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		spyAuthService.validateWithUserIdOtp(userRequest);
	}
	
	@Mock
	private ResponseWrapper responseWrapped;
	@Test
	public void invalidateToken() {
		String authHeader="Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5NzQ4MTA3Mzg2IiwibW9iaWxlIjoiOTc0ODEwNzM4NiIsIm1haWwiOiIiLCJuYW1lIjoiOTc0ODEwNzM4NiIsImlzT3RwUmVxdWlyZWQiOnRydWUsImlzT3RwVmVyaWZpZWQiOnRydWUsImlhdCI6MTU1MjM4NDk1NCwiZXhwIjoxNTUyMzkwOTU0fQ.burEVnDRF4YVyRGMdx0vYP2DkZbiCKnUdl-7YDlBgcy3u40W5iE9_P8q9kdrlt2xjk4NuXnjPkb7uaFbzYcHog; Max-Age=6000000; Expires=Mon, 20-May-2019 20:42:34 GMT; Path=/; Secure; HttpOnly";
		Mockito.doReturn(responseEntity).when(authCommonUtil).getResponseEntity(Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		authNResposne.setMessage("Success");
		Mockito.when(responseEntity.getBody()).thenReturn("authNResposne");
		Mockito.when(authCommonUtil.requestBodyExchange(Mockito.any())).thenReturn(responseWrapped);
		Mockito.when(responseWrapped.getResponse()).thenReturn(authNResposne);
		//Mockito.when(authCommonUtil.responseToString(Mockito.any())).thenReturn(authNResposne.toString());
		Mockito.when(authCommonUtil.requestBodyExchangeObject(Mockito.any(), Mockito.any())).thenReturn(authNResposne);
		Mockito.doNothing().when(spyAuthService).setAuditValues(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		assertNotNull(spyAuthService.invalidateToken(authHeader));
	}
	
	@Test(expected=InvalidateTokenException.class)
	public void invalidateToken_Exception() {
		String authHeader="Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5NzQ4MTA3Mzg2IiwibW9iaWxlIjoiOTc0ODEwNzM4NiIsIm1haWwiOiIiLCJuYW1lIjoiOTc0ODEwNzM4NiIsImlzT3RwUmVxdWlyZWQiOnRydWUsImlzT3RwVmVyaWZpZWQiOnRydWUsImlhdCI6MTU1MjM4NDk1NCwiZXhwIjoxNTUyMzkwOTU0fQ.burEVnDRF4YVyRGMdx0vYP2DkZbiCKnUdl-7YDlBgcy3u40W5iE9_P8q9kdrlt2xjk4NuXnjPkb7uaFbzYcHog; Max-Age=6000000; Expires=Mon, 20-May-2019 20:42:34 GMT; Path=/; Secure; HttpOnly";
		Mockito.doThrow(new RestClientException("Rest Client exception Occurred")).when(authCommonUtil).getResponseEntity(Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		authNResposne.setMessage("Success");
		Mockito.when(responseEntity.getBody()).thenReturn("authNResposne");
		Mockito.doNothing().when(spyAuthService).setAuditValues(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
		assertNotNull(spyAuthService.invalidateToken(authHeader));
	}
	
	/**
	 * This test method is for succes case of getConfig
	 * 
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws java.io.IOException
	 */
	
	@MockBean
	private RestTemplate restTemplate;
	
	  @Test public void getConfigSuccessTest() throws Exception {
	  Properties prop=new Properties();
	  ResponseEntity<String> res = new
	  ResponseEntity<String>("mosip.secondary-language=fra", HttpStatus.OK);
	  Map<String, String> configParams = new HashMap<>();
	  configParams.put("mosip.secondary-language","fra");
	  MainResponseDTO<Map<String, String>> response = new MainResponseDTO<>();
	  Mockito.when(restTemplate.getForEntity(Mockito.anyString(),
	  Mockito.eq(String.class))).thenReturn(res); 
	  Mockito.when(authCommonUtil.configRestCall(Mockito.any())).thenReturn("fileReturn");
	  Mockito.doNothing().when(authCommonUtil).getConfigParams(Mockito.any(), Mockito.any(), Mockito.any());
	  Mockito.when(authCommonUtil.parsePropertiesString(Mockito.any())).thenReturn(prop);
	  response = authService.getConfig();

	  assertNotNull(response.getResponse());
	  //assertEquals(response.getResponse().get("mosip.secondary-language"), "fra");
	  }
	  
	  
	  @Test(expected=ConfigFileNotFoundException.class)
	  public void getConfigExceptionTest() throws Exception {
		  Properties prop=new Properties();
		  ResponseEntity<String> res = new
		  ResponseEntity<String>("mosip.secondary-language=fra", HttpStatus.OK);
		  Map<String, String> configParams = new HashMap<>();
		  configParams.put("mosip.secondary-language","fra");
		  MainResponseDTO<Map<String, String>> response = new MainResponseDTO<>();
		  Mockito.when(restTemplate.getForEntity(Mockito.anyString(),
		  Mockito.eq(String.class))).thenReturn(res); 
		  Mockito.doThrow(new ConfigFileNotFoundException(ErrorCodes.PRG_AUTH_012.name(),
					ErrorMessages.CONFIG_FILE_NOT_FOUND_EXCEPTION.name())).when(authCommonUtil).configRestCall(Mockito.any());
		  Mockito.doNothing().when(authCommonUtil).getConfigParams(Mockito.any(), Mockito.any(), Mockito.any());
		  Mockito.when(authCommonUtil.parsePropertiesString(Mockito.any())).thenReturn(prop);
		  response = authService.getConfig();
		  }
	 
	  @MockBean
	  private  AuditLogUtil auditLogUtil;
	  @Test
	  public void setAuditValuesTest() {
		  Mockito.doNothing().when(auditLogUtil).saveAuditDetails(Mockito.any());
		  spyAuthService.setAuditValues("eventId", "eventName", "eventType", "description", "idType", "userId", "userName");
		  
	  }


}
