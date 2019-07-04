package io.mosip.preregistration.batchjobservices.test.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.mosip.preregistration.batchjobservices.service.ConsumedStatusService;
import io.mosip.preregistration.batchjobservices.service.ExpiredStatusService;
import io.mosip.preregistration.batchjobservices.test.BatchJobApplicationTest;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;

@SpringBootTest(classes = { BatchJobApplicationTest.class })
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class BatchServiceControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ConsumedStatusService consumedService;
	
	@MockBean
	private ExpiredStatusService expiredService;
	
	@Test
	@WithUserDetails("PRE_REGISTRATION_ADMIN")
	public void consumedAppointmentsTest() throws Exception {
		MainResponseDTO<String> response = new MainResponseDTO<>();
		Mockito.when(consumedService.demographicConsumedStatus()).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/consumedStatus")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
	
	@Test
	@WithUserDetails("PRE_REGISTRATION_ADMIN")
	public void expiredAppointmentsTest() throws Exception {
		MainResponseDTO<String> response = new MainResponseDTO<>();
		Mockito.when(consumedService.demographicConsumedStatus()).thenReturn(response);
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.put("/expiredStatus")
				.contentType(MediaType.APPLICATION_JSON_VALUE).characterEncoding("UTF-8")
				.accept(MediaType.APPLICATION_JSON_VALUE);
		mockMvc.perform(requestBuilder).andExpect(status().isOk());
	}
}
