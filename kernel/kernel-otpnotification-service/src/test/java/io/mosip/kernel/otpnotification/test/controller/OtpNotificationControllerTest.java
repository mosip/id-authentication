package io.mosip.kernel.otpnotification.test.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.otpnotification.OtpNotificationBootApplication;
import io.mosip.kernel.otpnotification.dto.OtpNotificationRequestDto;
import io.mosip.kernel.otpnotification.dto.OtpNotificationResponseDto;
import io.mosip.kernel.otpnotification.service.impl.OtpNotificationServiceImpl;

/**
 * The controller test class for otp notification.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { OtpNotificationBootApplication.class })
public class OtpNotificationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private OtpNotificationServiceImpl service;

	@Test
	public void sendOtpNotificationTest() throws Exception {
		List<String> notificationTypes = new ArrayList<>();
		notificationTypes.add("sms");
		OtpNotificationResponseDto response = new OtpNotificationResponseDto();
		response.setStatus("success");
		response.setMessage("Otp notification sent successfully");
		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		request.setEmailBodyTemplate("YOUR LOGIN OTP IS $otp");
		request.setEmailId("abc@gmail.com");
		request.setEmailSubjectTemplate("OTP ALERT");
		request.setMobileNumber("8999898989");
		request.setNotificationTypes(notificationTypes);
		request.setSmsTemplate("YOUR LOGIN OTP IS $otp");
		String json = mapper.writeValueAsString(request);
		when(service.sendOtpNotification(request)).thenReturn(response);
		mockMvc.perform(post("/v1.0/otp/send").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(jsonPath("$.status", is("success")));

	}

}
