package io.mosip.preregistration.login.test.controller;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.WebDataBinder;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import io.mosip.preregistration.core.common.dto.AuthNResponse;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.core.util.RequestValidator;
import io.mosip.preregistration.login.PreRegistartionLoginApplication;
import io.mosip.preregistration.login.config.LoginValidator;
import io.mosip.preregistration.login.controller.LoginController;
import io.mosip.preregistration.login.dto.OtpRequestDTO;
import io.mosip.preregistration.login.dto.User;
import io.mosip.preregistration.login.service.LoginService;
import io.mosip.preregistration.login.util.LoginCommonUtil;
import net.minidev.json.parser.ParseException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { PreRegistartionLoginApplication.class })
@AutoConfigureMockMvc
//@ConfigurationProperties("mosip.preregistration.login")
public class LoginControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private LoginService loginService;

	@MockBean
	private LoginCommonUtil loginCommonUtil;

	@Mock
	private RequestValidator loginValidator;

	@Autowired
	private LoginController controller;

	private HttpHeaders httpHeaders;

	private AuthNResponse authNResposne;
	
	@Mock
	private HttpServletResponse res;
	
	/** The Constant SENDOTP. */
	private static final String SENDOTP = "sendotp";
	
	
	/** The Constant VALIDATEOTP. */
	private static final String VALIDATEOTP = "validateotp";

	private MainResponseDTO<ResponseEntity<String>> serviceResponse;
	
	private MainRequestDTO<Object> loginRequest =new MainRequestDTO<>();

	private ResponseEntity<String> responseEntity;




	@Before
	public void setup() throws URISyntaxException, FileNotFoundException, ParseException {
		loginRequest.setId("mosip.pre-registration.login.sendotp");
		loginRequest.setVersion("v1");
		loginRequest.setRequesttime(new Date());
		ReflectionTestUtils.setField(controller, "loginValidator", loginValidator);
	}
	
	/**
	 * Test init binder.
	 */
	@Test
	public void testInitBinder() {
		controller.initBinder(Mockito.mock(WebDataBinder.class));
	}

	@Test
	public void sendOtpTest() throws Exception {
		MainResponseDTO<AuthNResponse> mainResponseDTO = new MainResponseDTO<>();
		Mockito.when(loginService.sendOTP(Mockito.any())).thenReturn(mainResponseDTO);
		MainRequestDTO<OtpRequestDTO> userOtpRequest= new MainRequestDTO<>();
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(userOtpRequest, "MainRequestDTO<OtpRequestDTO>");
		ResponseEntity<MainResponseDTO<AuthNResponse>> responseEntity = controller.sendOTP(userOtpRequest, errors);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	@Mock
	private ResponseWrapper responseWrapped;

	@Test
	public void validateWithUseridOtpTest() throws Exception {
		MainRequestDTO<User> userIdOtpRequest= new MainRequestDTO<>();
		loginRequest.setId("mosip.pre-registration.login.useridotp");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Set-Cookie", "AuthToken=MOSIP");
		authNResposne = new AuthNResponse("success", "success");
		responseEntity = new ResponseEntity<String>("Validation Successful", headers, HttpStatus.OK);
		serviceResponse = new MainResponseDTO<>();
		serviceResponse.setId("id");
		serviceResponse.setResponse(responseEntity);
		serviceResponse.setResponsetime("responseTime");
		Mockito.when(loginCommonUtil.requestBodyExchange(Mockito.any())).thenReturn(responseWrapped);
		Mockito.when(loginCommonUtil.requestBodyExchangeObject(Mockito.any(), Mockito.any())).thenReturn(authNResposne);
		//Mockito.when(loginCommonUtil.getConfig(Mockito.anyString())).thenReturn("mosip.io");
		Mockito.when(loginService.validateWithUserIdOtp(Mockito.any())).thenReturn(serviceResponse);
		
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(userIdOtpRequest, "MainRequestDTO<User>");
		ResponseEntity<MainResponseDTO<AuthNResponse>> responseEntity = controller.validateWithUserIdOtp(userIdOtpRequest, errors, res);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

	}

	@Test
	public void invalidateTokenTest() throws Exception {
		MainResponseDTO<AuthNResponse> serviceResponse = new MainResponseDTO<>();
		Mockito.when(loginService.invalidateToken(Mockito.any())).thenReturn(serviceResponse);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/invalidateToken")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(loginRequest.toString());
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@Test
	public void getConfigTest() throws Exception {
		MainResponseDTO<Map<String, String>> mainResponseDTO = new MainResponseDTO<>();
		Mockito.when(loginService.getConfig()).thenReturn(mainResponseDTO);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/config")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@Test
	public void refreshConfigTest() throws Exception {
		MainResponseDTO<String> mainResponseDTO = new MainResponseDTO<>();
		Mockito.when(loginService.refreshConfig()).thenReturn(mainResponseDTO);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/refreshconfig")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

}
