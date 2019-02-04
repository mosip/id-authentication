package io.mosip.preregistration.batchjobservices.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.mosip.preregistration.batchjobservices.service.ConsumedStatusService;
import io.mosip.preregistration.batchjobservices.service.ExpiredStatusService;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;

@RunWith(SpringRunner.class)
@WebMvcTest(BatchServiceControllerTest.class)
public class BatchServiceControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ConsumedStatusService consumedService;
	
	@MockBean
	private ExpiredStatusService expiredService;
	
	@Test
	public void consumedAppointmentsTest() throws Exception {
		MainResponseDTO<String> response = new MainResponseDTO<>();
		Mockito.when(consumedService.demographicConsumedStatus()).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/v0.1/pre-registration/batch/state/consumedStatus")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	public void expiredAppointmentsTest() throws Exception {
		MainResponseDTO<String> response = new MainResponseDTO<>();
		Mockito.when(consumedService.demographicConsumedStatus()).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/v0.1/pre-registration/batch/state/expiredStatus")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
}
