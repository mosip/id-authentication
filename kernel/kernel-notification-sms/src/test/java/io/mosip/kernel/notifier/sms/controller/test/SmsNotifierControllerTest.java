package io.mosip.kernel.notifier.sms.controller.test;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.notifier.sms.SmsNotifierApplication;
import io.mosip.kernel.notifier.sms.dto.SmsResponseDto;
import io.mosip.kernel.notifier.sms.service.impl.SmsNotifierServiceImpl;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { SmsNotifierApplication.class })
public class SmsNotifierControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	SmsNotifierServiceImpl service;

	@Test
	public void controllerTest() throws Exception {

		SmsResponseDto responseDto = new SmsResponseDto();

		responseDto.setStatus("success");

		String json = "{\"number\":\"8987672341\",\"message\":\"hello..your otp is 342891\"}";

		when(service.sendSmsNotification("8987672341", "hello..your otp is 342891")).thenReturn(responseDto);

		mockMvc.perform(post("/notifier/sms").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isAccepted()).andExpect(jsonPath("$.status", is("success")));
	}

}
