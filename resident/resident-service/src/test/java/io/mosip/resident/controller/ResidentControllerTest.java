/**
 * 
 */
package io.mosip.resident.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.resident.ResidentTestBootApplication;
import io.mosip.resident.dto.AuthLockRequestDto;
import io.mosip.resident.dto.RequestWrapper;
import io.mosip.resident.dto.ResponseDTO;
import io.mosip.resident.service.ResidentService;
import io.mosip.resident.validator.RequestValidator;

/**
 * @author Sowmya Ujjappa Banakar
 * @author Jyoti Prakash Nayak
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ResidentTestBootApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
public class ResidentControllerTest {

	@MockBean
	private ResidentService residentService;

	@MockBean
	private RequestValidator validator;

	@InjectMocks
	ResidentController residentController;

	RequestWrapper<AuthLockRequestDto> authLockRequest;

	/** The array to json. */
	private String regStatusToJson;
	

	/** The mock mvc. */
	@Autowired
	private MockMvc mockMvc;

	@Before
	public void setUp() {
		authLockRequest = new RequestWrapper<AuthLockRequestDto>();
		authLockRequest.setRequest(new AuthLockRequestDto());

		Gson gson = new GsonBuilder().serializeNulls().create();
		regStatusToJson = gson.toJson(authLockRequest);
	}

	@Test
	public void testRequestAuthLockSuccess() throws Exception {
		ResponseDTO responseDto = new ResponseDTO();
		responseDto.setStatus("success");
		doNothing().when(validator).validateAuthLockRequest(authLockRequest);
		Mockito.doReturn(responseDto).when(residentService).reqAauthLock(ArgumentMatchers.any());

		this.mockMvc.perform(post("/req/auth-lock").contentType(MediaType.APPLICATION_JSON).content(regStatusToJson))
				.andExpect(status().isOk()).andExpect(jsonPath("$.response.status", is("success")));
	}
	
	@Test
	public void testRequestAuthLockBadRequest() throws Exception {
		ResponseDTO responseDto = new ResponseDTO();
		doNothing().when(validator).validateAuthLockRequest(authLockRequest);
		Mockito.doReturn(responseDto).when(residentService).reqAauthLock(ArgumentMatchers.any());

		MvcResult result=this.mockMvc.perform(post("/req/auth-lock").contentType(MediaType.APPLICATION_JSON).content(""))
				.andExpect(status().isOk()).andReturn();
		assertTrue(result.getResponse().getContentAsString().contains("RES-500"));
	}
	
	
}
