package io.mosip.preregistration.auth.test.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.mosip.preregistration.auth.controller.AuthController;
import io.mosip.preregistration.auth.dto.MainResponseDTO;
import io.mosip.preregistration.auth.service.AuthService;
import io.mosip.preregistration.auth.util.AuthCommonUtil;
import io.mosip.preregistration.core.common.dto.AuthNResponse;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;


@RunWith(SpringRunner.class)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private AuthService authService;
	
	@MockBean
	private AuthCommonUtil authCommonUtil;
	
	
	private HttpHeaders httpHeaders;
	
	
	private AuthNResponse authNResposne;
	
	
	private MainResponseDTO<ResponseEntity<String>> serviceResponse;
	
	private ResponseEntity<String> responseEntity;
	
	private Object jsonObject = null;
	
	private Object jsonObject1 = null;
	@Before
	public void setup() throws URISyntaxException, FileNotFoundException, ParseException {
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
				 .post("/sendotp")
				 .contentType(MediaType.APPLICATION_JSON_VALUE)
				 .characterEncoding("UTF-8")
				 .accept(MediaType.APPLICATION_JSON_VALUE)
				 .content(jsonObject.toString());
		 mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	public void validateWithUseridOtpTest() throws Exception {
		//AuthNResponse authNResposne=new AuthNResponse();
		//MainResponseDTO<AuthNResponse> mainResponseDTO=new MainResponseDTO<>();
		HttpHeaders headers=new HttpHeaders();
		headers.add("Set-Cookie","AuthToken=MOSIP");
		authNResposne=new AuthNResponse("success");
		responseEntity=new ResponseEntity<String>("Validation Successful", headers, HttpStatus.OK);
		serviceResponse=new MainResponseDTO<>();
		serviceResponse.setId("id");
		serviceResponse.setResponse(responseEntity);
		serviceResponse.setResponsetime("responseTime");
		
		
		Mockito.when(authService.validateWithUserIdOtp(Mockito.any())).thenReturn(serviceResponse);		
		 RequestBuilder requestBuilder=MockMvcRequestBuilders
				 .post("/useridotp")
				 .contentType(MediaType.APPLICATION_JSON_VALUE)
				 .characterEncoding("UTF-8")
				 .accept(MediaType.APPLICATION_JSON_VALUE)
				 .content(jsonObject1.toString());
		 mockMvc.perform(requestBuilder).andExpect(status().isOk());
		
	}
	
	@Test
	public void invalidateTokenTest() throws Exception {
		AuthNResponse serviceResponse=new AuthNResponse();
		Mockito.when(authService.invalidateToken(Mockito.any())).thenReturn(serviceResponse);
		 RequestBuilder requestBuilder=MockMvcRequestBuilders
				 .post("/invalidatetoken")
				 .contentType(MediaType.APPLICATION_JSON_VALUE)
				 .characterEncoding("UTF-8")
				 .accept(MediaType.APPLICATION_JSON_VALUE)
				 .content(jsonObject.toString());
		 mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
}
