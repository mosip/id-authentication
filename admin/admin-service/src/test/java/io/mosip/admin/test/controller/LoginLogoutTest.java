package io.mosip.admin.test.controller;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.admin.TestBootApplication;
import io.mosip.admin.navigation.dto.OtpUserDTO;
import io.mosip.admin.navigation.dto.UserOtpDTO;
import io.mosip.admin.navigation.dto.UserRequestDTO;
import io.mosip.admin.navigation.dto.UserResponseDTO;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;

@SpringBootTest(classes=TestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class LoginLogoutTest {

    private static final String SET_COOKIE_STRING = "Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwcmVyZWd1c2VyIiwibW9iaWxlIjoiOTY2MzE3NTkyOCIsIm1haWwiOiJ0c3BAbW9zaXAuaW8iLCJyb2xlIjoiSU5ESVZJRFVBTCIsIm5hbWUiOiJwcmVyZWciLCJpYXQiOjE1NTc0NzQ1NjIsImV4cCI6MTU1NzQ4MDU2Mn0.hhfOFk4aU86y-i8Wqj6-j05rheD0Vg2xRP6pqGZj2tl1_wWX1nHk_c43ozL1WEB4QQScNUTCE9NekgFa-d_Xqw; Max-Age=6000000; Expires=Thu, 18-Jul-2019 18:29:22 GMT; Path=/; Secure; HttpOnly";
    private static final String COOKIE_STRING = "Authorization=Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwcmVyZWd1c2VyIiwibW9iaWxlIjoiOTY2MzE3NTkyOCIsIm1haWwiOiJ0c3BAbW9zaXAuaW8iLCJyb2xlIjoiSU5ESVZJRFVBTCIsIm5hbWUiOiJwcmVyZWciLCJpYXQiOjE1NTc0Njg5NDcsImV4cCI6MTU1NzQ3NDk0N30.Ev1XNeKrAOceu4n4uIr8HAaIuNCZwmfTCnd0mVaeQdmt1f1v3LXiiUMRMLoxe4A5Fzo9IRbMcjROtVbo2WhSxQ";
    private static final String COOKIE = "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwcmVyZWd1c2VyIiwibW9iaWxlIjoiOTY2MzE3NTkyOCIsIm1haWwiOiJ0c3BAbW9zaXAuaW8iLCJyb2xlIjoiSU5ESVZJRFVBTCIsIm5hbWUiOiJwcmVyZWciLCJpYXQiOjE1NTc0Njg5NDcsImV4cCI6MTU1NzQ3NDk0N30.Ev1XNeKrAOceu4n4uIr8HAaIuNCZwmfTCnd0mVaeQdmt1f1v3LXiiUMRMLoxe4A5Fzo9IRbMcjROtVbo2WhSxQ";
    private static final String SUCCESS_MESSAGE = "success";
    private static final String LOGIN_SUCCESS_MESSAGE = "Username and password combination had been validated successfully";
    private static final String LOGOUT_SUCCESS_MESSAGE = "Token has been invalidated successfully";
    private static final String VALIDATE = "/login";
    private static final String INVALIDATE = "/logout";
    private static final String SEND_OTP = "/sendOTP";
    private static final String VALIDATE_OTP = "/validateOTP";
    private static final String APP_ID = "admin";
    private static final String USER_NAME = "central-admin";
    private static final String PASSWORD = "mosip";
    private static final String USER_ID = "test@mosip.io";
    private static final String USER_ID_TYPE = "USERID";
    private static final String CONTEXT = "auth-otp";
    private static final String OTP = "123456";
    
    private static List<String> otpChannel = new ArrayList<>();
    private static Map<String,Object> templateVariables = new HashMap<String,Object>();
    
    @Value("${mosip.admin.navigation.base-uri}")
    private String baseUri;
    @Value("${mosip.admin.navigation.authmanager-uri}")
    private String authmanagerUri;
    @Value("${mosip.admin.navigation.userIdPwd-uri}")
    private String pwdUri;
    @Value("${mosip.admin.navigation.invalidateToken-uri}")
    private String invalidateTokenUri;
    @Value("${mosip.admin.navigation.sendOTP-uri}")
    private String sendOTPUri;
    @Value("${mosip.admin.navigation.validateOTP-uri}")
    private String validateOTPUri;

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RestTemplate restTemplate;

    @SuppressWarnings("unchecked")
    ResponseEntity<String> responseEntity = Mockito.mock(ResponseEntity.class);

//    private HttpHeaders loginHeaders = new HttpHeaders();
//    private HttpHeaders logoutHeaders = new HttpHeaders();
    private RequestWrapper<UserRequestDTO> loginSuccessRequest;
    private ResponseWrapper<UserResponseDTO> loginSuccessResponse;
    private ResponseWrapper<UserResponseDTO> logoutSuccessResponse;
    private RequestWrapper<OtpUserDTO> sendOtpRequest;
    private RequestWrapper<UserOtpDTO> validateOtpRequest;

    @Before
    public void setUp() {
//	loginHeaders.add(HttpHeaders.SET_COOKIE, SET_COOKIE_STRING);
//	logoutHeaders.add(HttpHeaders.COOKIE, COOKIE_STRING);
	mapper = new ObjectMapper();
	mapper.registerModule(new JavaTimeModule());

	loginSuccessRequest = new RequestWrapper<>();
	loginSuccessResponse = new ResponseWrapper<>();
	logoutSuccessResponse = new ResponseWrapper<>();
	sendOtpRequest = new RequestWrapper<>();
	validateOtpRequest = new RequestWrapper<>();
	
	otpChannel.add("email");
	templateVariables.put("testTemplateVariable", "testValue");

	UserRequestDTO loginSuccessRequestDTO = new UserRequestDTO();
	loginSuccessRequestDTO.setAppId(APP_ID);
	loginSuccessRequestDTO.setUserName(USER_NAME);
	loginSuccessRequestDTO.setPassword(PASSWORD);
	loginSuccessRequest.setRequest(loginSuccessRequestDTO);

	UserResponseDTO loginSuccessResponseDTO = new UserResponseDTO();
	loginSuccessResponseDTO.setStatus(SUCCESS_MESSAGE);
	loginSuccessResponseDTO.setMessage(LOGIN_SUCCESS_MESSAGE);
	loginSuccessResponse.setResponse(loginSuccessResponseDTO);

	UserResponseDTO logoutSuccessResponseDTO = new UserResponseDTO();
	logoutSuccessResponseDTO.setStatus(SUCCESS_MESSAGE);
	logoutSuccessResponseDTO.setMessage(LOGOUT_SUCCESS_MESSAGE);
	logoutSuccessResponse.setResponse(logoutSuccessResponseDTO);
	
	OtpUserDTO sendOtpRequestDTO = new OtpUserDTO();
	sendOtpRequestDTO.setUserId(USER_ID);
	sendOtpRequestDTO.setOtpChannel(otpChannel);
	sendOtpRequestDTO.setAppId(APP_ID);
	sendOtpRequestDTO.setUseridtype(USER_ID_TYPE);
	sendOtpRequestDTO.setTemplateVariables(templateVariables);
	sendOtpRequestDTO.setContext(CONTEXT);
	sendOtpRequest.setRequest(sendOtpRequestDTO);
	
	UserOtpDTO validateOtpRequestDTO = new UserOtpDTO();
	validateOtpRequestDTO.setUserId(USER_ID);
	validateOtpRequestDTO.setOtp(OTP);
	validateOtpRequestDTO.setAppId(APP_ID);
	validateOtpRequest.setRequest(validateOtpRequestDTO);
    }

    @Test
    @WithUserDetails("zonal-admin")
    public void testValidate() throws Exception {
    	
    	HttpHeaders headers=new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE,SET_COOKIE_STRING);
    	
	when(responseEntity.getBody())
		.thenReturn(mapper.writeValueAsString(loginSuccessResponse));	
	
	when(responseEntity.getHeaders()).thenReturn(headers);
	when(restTemplate.exchange(
		baseUri.concat(authmanagerUri).concat(pwdUri),
		HttpMethod.POST,
		new HttpEntity<RequestWrapper<UserRequestDTO>>(
			loginSuccessRequest),
		String.class)).thenReturn(responseEntity);

	String successRequestString = mapper
		.writeValueAsString(loginSuccessRequest);
	MvcResult mvcResult = mockMvc
		.perform(post(VALIDATE)
			.contentType(MediaType.APPLICATION_JSON)
			.content(successRequestString))
		.andExpect(status().isOk())
		.andReturn();
	
	ResponseWrapper<UserResponseDTO> responseWrapper = mapper.readValue(
		mvcResult.getResponse().getContentAsString(),
		new TypeReference<ResponseWrapper<UserResponseDTO>>() {
		});
	
	UserResponseDTO userResponse = responseWrapper.getResponse();
	assertTrue(userResponse.getStatus().equals(SUCCESS_MESSAGE));
	assertTrue(userResponse.getMessage().equals(LOGIN_SUCCESS_MESSAGE));
    }

    @Test
    @WithUserDetails("zonal-admin")
    public void testInValidate() throws Exception {
	when(responseEntity.getBody())
		.thenReturn(mapper.writeValueAsString(logoutSuccessResponse));
	when(restTemplate.exchange(
		baseUri.concat(authmanagerUri).concat(invalidateTokenUri),
		HttpMethod.POST,
		new HttpEntity<String>(null, null),
		String.class)).thenReturn(responseEntity);
    }
    
    @Test
    @WithUserDetails("zonal-admin")
    public void testSendOTP() throws Exception {
    	when(responseEntity.getBody())
    		.thenReturn(mapper.writeValueAsString(loginSuccessResponse));
    	when(restTemplate.exchange(
    		baseUri.concat(authmanagerUri).concat(sendOTPUri),
    		HttpMethod.POST,
    		new HttpEntity<RequestWrapper<OtpUserDTO>>(
    				sendOtpRequest),
    		String.class)).thenReturn(responseEntity);

    	String sendOtpRequestString = mapper
    		.writeValueAsString(sendOtpRequest);
    			mockMvc
    		.perform(post(SEND_OTP)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(sendOtpRequestString))
    		.andExpect(status().isOk());
        }
    
    @Test
    @WithUserDetails("zonal-admin")
    public void testValidateOTP() throws Exception {
    	
    	HttpHeaders headers=new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE,SET_COOKIE_STRING);
    	
    	when(responseEntity.getBody())
    		.thenReturn(mapper.writeValueAsString(loginSuccessResponse));
    	when(responseEntity.getHeaders()).thenReturn(headers);
    	when(restTemplate.exchange(
    		baseUri.concat(authmanagerUri).concat(validateOTPUri),
    		HttpMethod.POST,
    		new HttpEntity<RequestWrapper<UserOtpDTO>>(
    				validateOtpRequest),
    		String.class)).thenReturn(responseEntity);

    	String validateOtpRequestString = mapper
    		.writeValueAsString(validateOtpRequest);
    			mockMvc
    		.perform(post(VALIDATE_OTP)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(validateOtpRequestString))
    		.andExpect(status().isOk());
        }

}
