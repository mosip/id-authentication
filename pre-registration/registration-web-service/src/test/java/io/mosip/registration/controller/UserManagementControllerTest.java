package io.mosip.registration.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.mosip.registration.constants.StatusCodes;
import io.mosip.registration.controller.UserManagementController;
import io.mosip.registration.dto.UserDto;
import io.mosip.registration.service.UserManagementService;

@RunWith(SpringRunner.class)
@WebMvcTest(UserManagementController.class)
public class UserManagementControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserManagementService userManagementService;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private String userName = "9900806086";

	@Test
	public void userSuccessLogin() throws Exception {
		logger.debug("user login Success test case");
		Map<String, StatusCodes> responseMap = new HashMap<>();
		responseMap.put("ok", StatusCodes.USER_OTP_GENERATED);

		Mockito.when(userManagementService.userLogin(Mockito.anyString())).thenReturn(responseMap);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/login")
				.param("userName", userName).accept(MediaType.ALL_VALUE);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@Test
	public void userFailedLogin() throws Exception {
		logger.debug("user login failed test case");

		Map<String, StatusCodes> responseMap = new HashMap<>();
		responseMap.put("error", StatusCodes.USER_OTP_GENERATION_FAILED);

		Mockito.when(userManagementService.userLogin(Mockito.anyString())).thenReturn(responseMap);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v0.1/pre-registration/login")
				.param("userName", userName).accept(MediaType.ALL_VALUE);
		mockMvc.perform(requestBuilder).andExpect(status().isNotAcceptable());
	}

	@Test
	public void userSuccessValidation() throws Exception {
		logger.debug("user validate Success test case");
		Map<String, StatusCodes> responseMap = new HashMap<>();
		responseMap.put("ok", StatusCodes.OTP_VALIDATION_SUCESSFUL);
		JSONObject json = new JSONObject();
		json.put("otp", 323223);
		String str = json.toString();
		
		Mockito.when(userManagementService.userValidation(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(responseMap);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/login")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
				.param("userName", userName).content(str);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@Test
	public void userFailedValidation() throws Exception {
		logger.debug("user validate failed test case");
		Map<String, StatusCodes> responseMap = new HashMap<>();
		responseMap.put("error", StatusCodes.OTP_VALIDATION_FAILED);
		JSONObject json = new JSONObject();
		json.put("otp", 323223);
		String str = json.toString();
		
		Mockito.when(userManagementService.userValidation(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(responseMap);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v0.1/pre-registration/login")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
				.param("userName", userName).content(str);

		mockMvc.perform(requestBuilder).andExpect(status().isNotAcceptable());
	}

	@Test
	public void userSuccessUpdation() throws Exception {
		logger.debug("user update Success test case");
		JSONObject json = new JSONObject();
		UserDto mockuserDto = new UserDto();
		mockuserDto.setUserName("9988905333");
		json.put("userName", mockuserDto.getUserName());
		String str = json.toString();
		Map<String, StatusCodes> responseMap = new HashMap<>();
		responseMap.put("ok", StatusCodes.USER_UPDATED);

		Mockito.when(userManagementService.userUpdation(Mockito.anyString(), Mockito.any(UserDto.class)))
				.thenReturn(responseMap);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/v0.1/pre-registration/user/")
				.contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON)
				.param("userName", userName).content(str);

		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}

	@Test
	public void userFailedUpdation() throws Exception {
		logger.debug("user update failed test case");
		JSONObject json = new JSONObject();
		UserDto mockuserDto = new UserDto();
		mockuserDto.setUserName("998890533");
		json.put("userName", mockuserDto.getUserName());
		String str = json.toString();
		Map<String, StatusCodes> responseMap = new HashMap<>();
		responseMap.put("error", StatusCodes.USER_UPDATION_FAILED);

		Mockito.when(userManagementService.userUpdation(Mockito.anyString(), Mockito.any(UserDto.class)))
				.thenReturn(responseMap);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/v0.1/pre-registration/user/")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8").accept(MediaType.APPLICATION_JSON_VALUE)
				.param("oldData", userName).content(str);

		mockMvc.perform(requestBuilder).andExpect(status().is4xxClientError());
	}
}
