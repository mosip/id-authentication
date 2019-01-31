/**
 * 
 */
package io.mosip.kernel.otpnotification.test.exception;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.otpnotification.OtpNotificationBootApplication;
import io.mosip.kernel.otpnotification.dto.OtpNotificationRequestDto;
import io.mosip.kernel.otpnotification.service.impl.OtpNotificationServiceImpl;
import io.mosip.kernel.otpnotification.utils.OtpNotificationUtil;

/**
 * @author Ritesh Sinha
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { OtpNotificationBootApplication.class })
@AutoConfigureMockMvc
public class OtpNotificationExceptionTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper mapper;

	@MockBean
	private OtpNotificationServiceImpl service;

	@MockBean
	private OtpNotificationUtil util;

	@MockBean
	private TemplateManager templateManager;

	@Test
	public void emptyNotificationTypeTest() throws Exception {
		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		List<String> notificationTypes = new ArrayList<>();
		request.setNotificationTypes(notificationTypes);
		request.setEmailBodyTemplate("YOUR LOGIN OTP IS $otph");
		request.setEmailId("abc@gmail.com");
		request.setEmailSubjectTemplate("OTP ALERT");
		request.setMobileNumber("8999898989");
		request.setSmsTemplate("YOUR LOGIN OTP IS $otp");
		String json = mapper.writeValueAsString(request);
		mockMvc.perform(post("/v1.0/otpnotification/send").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@Test
	public void clientErrorExceptionTest() throws Exception {

		List<String> notificationTypes = new ArrayList<>();
		notificationTypes.add("sms");
		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		request.setEmailBodyTemplate("YOUR LOGIN OTP IS $otp");
		request.setEmailId("abc@gmail.com");
		request.setMobileNumber("8989898989");
		request.setEmailSubjectTemplate("OTP ALERT");
		request.setNotificationTypes(notificationTypes);
		request.setSmsTemplate("YOUR LOGIN OTP IS $otp");
		String json = mapper.writeValueAsString(request);
		when(service.sendOtpNotification(request))
						.thenThrow(new HttpClientErrorException(HttpStatus.OK, "number cannot be null or blank"));
		mockMvc.perform(post("/v1.0/otpnotification/send").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());

	}

	@Test
	public void OtpNotifierServiceExceptionTest() throws Exception {
		List<String> notificationTypes = new ArrayList<>();
		notificationTypes.add("sms");
		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		request.setEmailBodyTemplate("YOUR LOGIN OTP IS $otp");
		request.setEmailId("abc@gmail.com");
		request.setMobileNumber("8989898989");
		request.setEmailSubjectTemplate("OTP ALERT");
		request.setNotificationTypes(notificationTypes);
		request.setSmsTemplate("YOUR LOGIN OTP IS $otp");
		String json = mapper.writeValueAsString(request);
		when(templateManager.merge(Mockito.any(),Mockito.any())).thenThrow(IOException.class);
		mockMvc.perform(post("/v1.0/otpnotification/send").contentType(MediaType.APPLICATION_JSON).content(json))
		.andExpect(status().isOk());
	}

}
