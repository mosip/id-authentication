package io.mosip.kernel.smsnotifier.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import io.mosip.kernel.smsnotifier.SmsNotifierApplication;
import io.mosip.kernel.smsnotifier.service.impl.SmsNotifierServiceImpl;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { SmsNotifierApplication.class })
public class SmsNotifierControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	SmsNotifierServiceImpl service;

	@Test
	public void integrationTest() throws Exception {

		String json = "{\"number\":\"8987672341\",\"message\":\"hello..your otp is 342891\"}";

		mockMvc.perform(post("/smsnotifier/texts").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isAccepted());
	}
}
