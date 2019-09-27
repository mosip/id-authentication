package io.mosip.preregistration.login.test.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

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
import org.springframework.web.bind.WebDataBinder;

import io.mosip.preregistration.core.common.dto.AuthNResponse;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.ResponseWrapper;
import io.mosip.preregistration.login.PreRegistartionLoginApplication;
import io.mosip.preregistration.login.config.LoginValidator;
import io.mosip.preregistration.login.controller.LoginController;
import io.mosip.preregistration.login.service.LoginService;
import io.mosip.preregistration.login.util.LoginCommonUtil;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;


@RunWith(SpringRunner.class)
@SpringBootTest(classes= {PreRegistartionLoginApplication.class})
@AutoConfigureMockMvc
@ConfigurationProperties("mosip.preregistration.login")
public class LoginControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private LoginService authService;
	
	@MockBean
	private LoginCommonUtil authCommonUtil;
	
	@Mock
	private LoginValidator loginValidator;
	
	@InjectMocks
	private LoginController controller;
	
	
	private HttpHeaders httpHeaders;
	
	
	private AuthNResponse authNResposne;
	
	
	private MainResponseDTO<ResponseEntity<String>> serviceResponse;
	
	private ResponseEntity<String> responseEntity;
	
	private Object jsonObject = null;
	

	/**
	 * Test init binder.
	 */
	@Test
	public void testInitBinder() {
		controller.initBinder(Mockito.mock(WebDataBinder.class));
	}
	
	private Object jsonObject1 = null;
	@Before
	public void setup() throws URISyntaxException, FileNotFoundException, ParseException {
		
		ReflectionTestUtils.setField(controller, "loginValidator", loginValidator);
		ClassLoader classLoader = getClass().getClassLoader();
		JSONParser parser = new JSONParser();

		URI sendOtpUri = new URI(
				classLoader.getResource("sendOtp.json").getFile().trim().replaceAll("\\u0020", "%20"));
		File file = new File(sendOtpUri.getPath());
		jsonObject = parser.parse(new FileReader(file));
		
		URI validateuserIdOtpUri = new URI(
				classLoader.getResource("useridOtp.json").getFile().trim().replaceAll("\\u0020", "%20"));
		File file1 = new File(validateuserIdOtpUri.getPath());
		jsonObject1 = parser.parse(new FileReader(file1));

	}
	
	@Test
	public void sendOtpTest() throws Exception {
		MainResponseDTO<AuthNResponse> mainResponseDTO=new MainResponseDTO<>();
		Mockito.when(authService.sendOTP(Mockito.any())).thenReturn(mainResponseDTO);
		 RequestBuilder requestBuilder=MockMvcRequestBuilders
				 .post("/sendOtp")
				 .contentType(MediaType.APPLICATION_JSON_VALUE)
				 .characterEncoding("UTF-8")
				 .accept(MediaType.APPLICATION_JSON_VALUE)
				 .content(jsonObject.toString());
		 mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Mock
	private ResponseWrapper responseWrapped;
	@Test
	public void validateWithUseridOtpTest() throws Exception {
		//AuthNResponse authNResposne=new AuthNResponse();
		//MainResponseDTO<AuthNResponse> mainResponseDTO=new MainResponseDTO<>();
		HttpHeaders headers=new HttpHeaders();
		headers.add("Set-Cookie","AuthToken=MOSIP");
		authNResposne=new AuthNResponse("success","success");
		responseEntity=new ResponseEntity<String>("Validation Successful", headers, HttpStatus.OK);
		serviceResponse=new MainResponseDTO<>();
		serviceResponse.setId("id");
		serviceResponse.setResponse(responseEntity);
		serviceResponse.setResponsetime("responseTime");
		Mockito.when(authCommonUtil.requestBodyExchange(Mockito.any())).thenReturn(responseWrapped);
		Mockito.when(authCommonUtil.requestBodyExchangeObject(Mockito.any(), Mockito.any())).thenReturn(authNResposne);
		
		Mockito.when(authService.validateWithUserIdOtp(Mockito.any())).thenReturn(serviceResponse);		
		 RequestBuilder requestBuilder=MockMvcRequestBuilders
				 .post("/validateOtp")
				 .contentType(MediaType.APPLICATION_JSON_VALUE)
				 .characterEncoding("UTF-8")
				 .accept(MediaType.APPLICATION_JSON_VALUE)
				 .content(jsonObject1.toString());
		 mockMvc.perform(requestBuilder).andExpect(status().isOk());
		
	}
	
	@Test
	public void invalidateTokenTest() throws Exception {
		MainResponseDTO<AuthNResponse> serviceResponse=new MainResponseDTO<>();
		Mockito.when(authService.invalidateToken(Mockito.any())).thenReturn(serviceResponse);
		 RequestBuilder requestBuilder=MockMvcRequestBuilders
				 .post("/invalidateToken")
				 .contentType(MediaType.APPLICATION_JSON_VALUE)
				 .characterEncoding("UTF-8")
				 .accept(MediaType.APPLICATION_JSON_VALUE)
				 .content(jsonObject.toString());
		 mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	public void getConfigTest() throws Exception {
		MainResponseDTO<Map<String,String>> mainResponseDTO=new MainResponseDTO<>();
		Mockito.when(authService.getConfig()).thenReturn(mainResponseDTO);
		 RequestBuilder requestBuilder=MockMvcRequestBuilders
				 .get("/config")
				 .contentType(MediaType.APPLICATION_JSON_VALUE)
				 .characterEncoding("UTF-8")
				 .accept(MediaType.APPLICATION_JSON_VALUE);
		 mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	public void refreshConfigTest() throws Exception {
		MainResponseDTO<String> mainResponseDTO=new MainResponseDTO<>();
		Mockito.when(authService.refreshConfig()).thenReturn(mainResponseDTO);
		 RequestBuilder requestBuilder=MockMvcRequestBuilders
				 .get("/refreshconfig")
				 .contentType(MediaType.APPLICATION_JSON_VALUE)
				 .characterEncoding("UTF-8")
				 .accept(MediaType.APPLICATION_JSON_VALUE);
		 mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
}
