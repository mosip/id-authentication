package io.mosip.kernel.smsnotifier.exception.test;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.core.util.exception.MosipJsonParseException;
import io.mosip.kernel.smsnotifier.SmsNotifierApplication;
import io.mosip.kernel.smsnotifier.service.impl.SmsNotifierServiceImpl;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { SmsNotifierApplication.class })
@TestPropertySource("classpath:application.properties")

public class ExceptionHandlerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	SmsNotifierServiceImpl service;

	@Value("${SmsApi}")
	String api;

	@Value("${SmsAuthkey}")
	String authkey;

	@Value("${SmsCountryCode}")
	String countryCode;

	@Value("${SmsSender}")
	String senderId;

	@Value("${SmsRoute}")
	String route;

	@Test
	public void emptyContactNumberTest() throws Exception {
		String json = "{\"number\":\"\",\"message\":\"hello..your otp is 342891\"}";
		mockMvc.perform(post("/notifier/sms").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotAcceptable());
	}

	@Test
	public void nullContactNumberTest() throws Exception {
		String json = "{\"number\":null,\"message\":\"hello..your otp is 342891\"}";
		mockMvc.perform(post("/notifier/sms").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotAcceptable());
	}

	@Test
	public void nullMessageTest() throws Exception {
		String json = "{\"number\":\"8987672341\",\"message\":null}";
		mockMvc.perform(post("/notifier/sms").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotAcceptable());
	}

	@Test
	public void emptyMessageTest() throws Exception {
		String json = "{\"number\":\"\",\"message\":\"\"}";
		mockMvc.perform(post("/notifier/sms").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotAcceptable());
	}

	@Test
	public void controllerExceptionTest() throws Exception {
		when(service.sendSmsNotification(Mockito.any(), Mockito.any())).thenThrow(MosipJsonParseException.class);
		String json = "{\"number\":\"8987672341\",\"message\":\"hello..your otp is 342891\"}";

		mockMvc.perform(post("/notifier/sms").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotAcceptable());
	}

}
