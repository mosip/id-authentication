package io.mosip.kernel.smsnotification.test.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.smsnotification.dto.SmsRequestDto;
import io.mosip.kernel.smsnotification.dto.SmsResponseDto;
import io.mosip.kernel.smsnotification.service.impl.SmsNotificationServiceImpl;
import io.mosip.kernel.smsnotification.test.SmsNotificationTestBootApplication;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(classes = { SmsNotificationTestBootApplication.class })
public class SmsNotificationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	SmsNotificationServiceImpl service;

	@WithUserDetails("individual")
	@Test
	public void controllerTest() throws Exception {

		SmsResponseDto responseDto = new SmsResponseDto();

		responseDto.setStatus("success");

		SmsRequestDto requestDto = new SmsRequestDto();
		requestDto.setMessage("hello..your otp is 342891");
		requestDto.setNumber("8987672341");

		RequestWrapper<SmsRequestDto> reqWrapperDTO = new RequestWrapper<>();
		reqWrapperDTO.setId("ID");
		reqWrapperDTO.setMetadata(null);
		reqWrapperDTO.setRequest(requestDto);
		reqWrapperDTO.setRequesttime(LocalDateTime.now());
		reqWrapperDTO.setVersion("v1.0");
		String json = objectMapper.writeValueAsString(reqWrapperDTO);

		when(service.sendSmsNotification("8987672341", "hello..your otp is 342891")).thenReturn(responseDto);

		mockMvc.perform(post("/sms/send").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(jsonPath("$.response.status", is("success")));
	}

}