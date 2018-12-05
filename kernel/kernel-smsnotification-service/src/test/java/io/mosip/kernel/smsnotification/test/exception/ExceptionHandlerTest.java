package io.mosip.kernel.smsnotification.test.exception;

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

import io.mosip.kernel.smsnotification.SmsNotificationBootApplication;
import io.mosip.kernel.smsnotification.service.impl.SmsNotificationServiceImpl;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { SmsNotificationBootApplication.class })
public class ExceptionHandlerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	SmsNotificationServiceImpl service;

	@Test
	public void emptyContactNumberTest() throws Exception {
		String json = "{\"number\":\"\",\"message\":\"hello..your otp is 342891\"}";
		mockMvc.perform(post("/v1.0/sms/send").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotAcceptable());
	}

	@Test
	public void nullContactNumberTest() throws Exception {
		String json = "{\"number\":null,\"message\":\"hello..your otp is 342891\"}";
		mockMvc.perform(post("/v1.0/sms/send").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotAcceptable());
	}

	@Test
	public void nullMessageTest() throws Exception {
		String json = "{\"number\":\"8987672341\",\"message\":null}";
		mockMvc.perform(post("/v1.0/sms/send").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotAcceptable());
	}

	@Test
	public void emptyMessageTest() throws Exception {
		String json = "{\"number\":\"\",\"message\":\"\"}";
		mockMvc.perform(post("/v1.0/sms/send").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotAcceptable());
	}

	@Test
	public void contactNumberLengthTest() throws Exception {
		String json = "{\"number\":\"678\",\"message\":\"\"}";
		mockMvc.perform(post("/v1.0/sms/send").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotAcceptable());
	}

	@Test
	public void invalidContactNumberTest() throws Exception {
		String json = "{\"number\":\"sdjnjkdfj\",\"message\":\"\"}";
		mockMvc.perform(post("/v1.0/sms/send").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotAcceptable());
	}
}
