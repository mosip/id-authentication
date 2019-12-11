/**
 * 
 */
package io.mosip.resident.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.resident.ResidentTestBootApplication;
import io.mosip.resident.constant.IdType;
import io.mosip.resident.dto.AuthLockOrUnLockRequestDto;
import io.mosip.resident.dto.EuinRequestDTO;
import io.mosip.resident.dto.RequestWrapper;
import io.mosip.resident.dto.ResidentReprintRequestDto;
import io.mosip.resident.dto.ResidentReprintResponseDto;
import io.mosip.resident.dto.ResponseDTO;
import io.mosip.resident.dto.ResponseWrapper;
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

	RequestWrapper<AuthLockOrUnLockRequestDto> authLockRequest;
	RequestWrapper<EuinRequestDTO> euinRequest;

	/** The array to json. */
	private String authLockRequestToJson;
	private String euinRequestToJson;
	private Gson gson;

	/** The mock mvc. */
	@Autowired
	private MockMvc mockMvc;

	@Before
	public void setUp() {
		authLockRequest = new RequestWrapper<AuthLockOrUnLockRequestDto>();
		authLockRequest.setRequest(new AuthLockOrUnLockRequestDto());
		euinRequest = new RequestWrapper<EuinRequestDTO>();
		euinRequest.setRequest(new EuinRequestDTO("1234567890", "1234567890", "UIN", "UIN", "4567"));
		gson = new GsonBuilder().serializeNulls().create();
		authLockRequestToJson = gson.toJson(authLockRequest);
		euinRequestToJson = gson.toJson(euinRequest);
	}

	@Test
	public void testRequestAuthLockSuccess() throws Exception {
		ResponseDTO responseDto = new ResponseDTO();
		responseDto.setStatus("success");
		doNothing().when(validator).validateAuthLockRequest(Mockito.any());
		Mockito.doReturn(responseDto).when(residentService).reqAauthTypeStatusUpdate(Mockito.any(), Mockito.any());

		this.mockMvc
				.perform(post("/req/auth-lock").contentType(MediaType.APPLICATION_JSON).content(authLockRequestToJson))
				.andExpect(status().isOk()).andExpect(jsonPath("$.response.status", is("success")));
	}

	@Test
	public void testRequestAuthLockBadRequest() throws Exception {
		ResponseDTO responseDto = new ResponseDTO();
		doNothing().when(validator).validateAuthLockRequest(Mockito.any());
		Mockito.doReturn(responseDto).when(residentService).reqAauthTypeStatusUpdate(Mockito.any(), Mockito.any());

		MvcResult result = this.mockMvc
				.perform(post("/req/auth-lock").contentType(MediaType.APPLICATION_JSON).content(""))
				.andExpect(status().isOk()).andReturn();
		assertTrue(result.getResponse().getContentAsString().contains("RES-500"));
	}

	@Test
	public void testRequestEuinSuccess() throws Exception {
		doNothing().when(validator).validateEuinRequest(Mockito.any());
		Mockito.doReturn(new byte[10]).when(residentService).reqEuin(Mockito.any());

		MvcResult result = this.mockMvc
				.perform(post("/req/euin").contentType(MediaType.APPLICATION_JSON).content(euinRequestToJson))
				.andExpect(status().isOk()).andReturn();
		assertEquals("application/pdf", result.getResponse().getContentType());
	}

	@Test
	public void testRequestEuinBadRequest() throws Exception {

		MvcResult result = this.mockMvc.perform(post("/req/euin").contentType(MediaType.APPLICATION_JSON).content(""))
				.andExpect(status().isOk()).andReturn();
		assertTrue(result.getResponse().getContentAsString().contains("RES-500"));
	}

	@Test
	public void testReprintUINSuccess() throws Exception {
		ResidentReprintResponseDto reprintResp = new ResidentReprintResponseDto();
		reprintResp.setRegistrationId("123456789");
		reprintResp.setMessage("Notification sent");
		ResponseWrapper<ResidentReprintResponseDto> response = new ResponseWrapper<>();
		response.setResponse(reprintResp);

		Mockito.when(residentService.reqPrintUin(Mockito.any())).thenReturn(reprintResp);

		RequestWrapper<ResidentReprintRequestDto> requestWrapper = new RequestWrapper<>();
		ResidentReprintRequestDto request = new ResidentReprintRequestDto();
		request.setIndividualId("3527812406");
		request.setIndividualIdType(IdType.UIN);
		request.setOtp("1234");
		request.setTransactionID("9876543210");
		requestWrapper.setRequest(request);
		requestWrapper.setId(",osip.resident.reprint");
		requestWrapper.setVersion("1.0");
		requestWrapper.setRequesttime(DateUtils.getUTCCurrentDateTimeString());

		Gson gson = new GsonBuilder().serializeNulls().create();
		String requestAsString = gson.toJson(requestWrapper);

		this.mockMvc.perform(post("/req/print-uin").contentType(MediaType.APPLICATION_JSON).content(requestAsString))
				.andExpect(status().isOk());
	}

}
