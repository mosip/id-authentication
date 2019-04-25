package io.mosip.kernel.smsnotification.test.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.smsnotification.service.impl.SmsNotificationServiceImpl;
import io.mosip.kernel.smsnotification.test.SmsNotificationTestBootApplication;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = { SmsNotificationTestBootApplication.class })
public class ExceptionHandlerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	SmsNotificationServiceImpl service;

	@WithUserDetails("individual")
	@Test
	public void emptyContactNumberTest() throws Exception {
		String json = "{\"number\":\"\",\"message\":\"hello..your otp is 342891\"}";
		mockMvc.perform(post("/sms/send").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@WithUserDetails("individual")
	@Test
	public void nullContactNumberTest() throws Exception {
		String json = "{\"number\":null,\"message\":\"hello..your otp is 342891\"}";
		mockMvc.perform(post("/sms/send").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@WithUserDetails("individual")
	@Test
	public void nullMessageTest() throws Exception {
		String json = "{\"number\":\"8987672341\",\"message\":null}";
		mockMvc.perform(post("/sms/send").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@WithUserDetails("individual")
	@Test
	public void emptyMessageTest() throws Exception {
		String json = "{\"number\":\"\",\"message\":\"\"}";
		mockMvc.perform(post("/sms/send").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@WithUserDetails("individual")
	@Test
	public void contactNumberLengthTest() throws Exception {
		String json = "{\"number\":\"678\",\"message\":\"\"}";
		mockMvc.perform(post("/sms/send").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@WithUserDetails("individual")
	@Test
	public void invalidContactNumberTest() throws Exception {
		String json = "{\"number\":\"sdjnjkdfj\",\"message\":\"\"}";
		mockMvc.perform(post("/sms/send").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}
}
