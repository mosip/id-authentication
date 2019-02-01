package io.mosip.kernel.otpnotification.test.service;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.otpnotification.OtpNotificationBootApplication;
import io.mosip.kernel.otpnotification.dto.NotifierResponseDto;
import io.mosip.kernel.otpnotification.dto.OtpNotificationRequestDto;
import io.mosip.kernel.otpnotification.dto.OtpNotificationResponseDto;
import io.mosip.kernel.otpnotification.dto.OtpResponseDto;
import io.mosip.kernel.otpnotification.exception.OtpNotificationInvalidArgumentException;
import io.mosip.kernel.otpnotification.exception.OtpNotifierServiceException;
import io.mosip.kernel.otpnotification.service.impl.OtpNotificationServiceImpl;

/**
 * The service test class for otp notification.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { OtpNotificationBootApplication.class })
public class OtpNotificationServiceTest {

	@MockBean
	private RestTemplate restTemplate;

	@Autowired
	private OtpNotificationServiceImpl service;

	@Test
	public void sendOtpNotificationTest() {
		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		OtpResponseDto response = new OtpResponseDto();
		List<String> notificationFlag = new ArrayList<>();
		notificationFlag.add("sms");
		notificationFlag.add("email");
		request.setNotificationTypes(notificationFlag);
		request.setEmailBodyTemplate("Otp $otp");
		request.setEmailId("abc@gmail.com");
		request.setEmailSubjectTemplate("OTP ALERT");
		request.setMobileNumber("8989898998");
		request.setSmsTemplate("OTP $otp");
		response.setOtp("344234");
		response.setStatus("Generated_succefully");
		when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
				Mockito.eq(OtpResponseDto.class)))
						.thenReturn(new ResponseEntity<OtpResponseDto>(response, HttpStatus.OK));

		NotifierResponseDto smsResponse = new NotifierResponseDto();
		smsResponse.setStatus("success");
		when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
				Mockito.eq(NotifierResponseDto.class)))
						.thenReturn(new ResponseEntity<NotifierResponseDto>(smsResponse, HttpStatus.OK));

		assertThat(service.sendOtpNotification(request), isA(OtpNotificationResponseDto.class));

	}

	@Test(expected = OtpNotifierServiceException.class)
	public void sendOtpNotificationInvalidNotificationTypeExceptionTest() {
		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		OtpResponseDto response = new OtpResponseDto();
		List<String> notificationFlag = new ArrayList<>();
		notificationFlag.add("sme");
		notificationFlag.add("email");
		response.setOtp("344234");
		response.setStatus("Generated_succefully");
		request.setNotificationTypes(notificationFlag);
		request.setEmailBodyTemplate("Otp $otp");
		request.setEmailId("abc@gmail.com");
		request.setEmailSubjectTemplate("OTP ALERT");
		request.setMobileNumber("8989898998");
		request.setSmsTemplate("OTP $otp");

		service.sendOtpNotification(request);

	}

	@Test
	public void sendOtpNotificationSmsTypeTest() {

		OtpResponseDto response = new OtpResponseDto();
		List<String> notificationFlag = new ArrayList<>();
		notificationFlag.add("sms");
		response.setOtp("344234");
		response.setStatus("Generated_succefully");
		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		request.setNotificationTypes(notificationFlag);
		request.setEmailBodyTemplate("Otp $otp");
		request.setEmailId("abc@gmail.com");
		request.setEmailSubjectTemplate("OTP ALERT");
		request.setMobileNumber("8989898998");
		request.setSmsTemplate("OTP $otp");
		when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
				Mockito.eq(OtpResponseDto.class)))
						.thenReturn(new ResponseEntity<OtpResponseDto>(response, HttpStatus.OK));

		NotifierResponseDto smsResponse = new NotifierResponseDto();
		smsResponse.setStatus("success");
		when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
				Mockito.eq(NotifierResponseDto.class)))
						.thenReturn(new ResponseEntity<NotifierResponseDto>(smsResponse, HttpStatus.OK));

		assertThat(service.sendOtpNotification(request), isA(OtpNotificationResponseDto.class));
	}

	@Test
	public void sendOtpNotificationEmailTypeTest() {
		OtpResponseDto response = new OtpResponseDto();
		List<String> notificationFlag = new ArrayList<>();
		notificationFlag.add("email");
		response.setOtp("344234");
		response.setStatus("Generated_succefully");
		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		request.setNotificationTypes(notificationFlag);
		request.setEmailBodyTemplate("Otp $otp");
		request.setEmailId("abc@gmail.com");
		request.setEmailSubjectTemplate("OTP ALERT");
		request.setMobileNumber("8989898998");
		request.setSmsTemplate("OTP $otp");
		when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
				Mockito.eq(OtpResponseDto.class)))
						.thenReturn(new ResponseEntity<OtpResponseDto>(response, HttpStatus.OK));

		NotifierResponseDto smsResponse = new NotifierResponseDto();
		smsResponse.setStatus("success");
		when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
				Mockito.eq(NotifierResponseDto.class)))
						.thenReturn(new ResponseEntity<NotifierResponseDto>(smsResponse, HttpStatus.OK));

		assertThat(service.sendOtpNotification(request), isA(OtpNotificationResponseDto.class));
	}

	@Test(expected = OtpNotificationInvalidArgumentException.class)
	public void sendOtpNotificationNullEmailBodyExceptionTest() {

		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		List<String> notificationFlag = new ArrayList<>();
		notificationFlag.add("email");
		request.setNotificationTypes(notificationFlag);
		request.setEmailBodyTemplate(null);
		request.setEmailId("abc@gmail.com");
		request.setEmailSubjectTemplate("OTP ALERT");
		request.setMobileNumber("8989898998");
		request.setSmsTemplate("OTP $otp");
		service.sendOtpNotification(request);
	}

	@Test(expected = OtpNotificationInvalidArgumentException.class)
	public void sendOtpNotificationEmptyEmailBodyExceptionTest() {

		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		List<String> notificationFlag = new ArrayList<>();
		notificationFlag.add("email");
		request.setNotificationTypes(notificationFlag);
		request.setEmailBodyTemplate("");
		request.setEmailId("abc@gmail.com");
		request.setEmailSubjectTemplate("OTP ALERT");
		request.setMobileNumber("8989898998");
		request.setSmsTemplate("OTP $otp");
		service.sendOtpNotification(request);
	}

	@Test(expected = OtpNotificationInvalidArgumentException.class)
	public void sendOtpNotificationNullSmsTemplateExceptionTest() {

		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		List<String> notificationFlag = new ArrayList<>();
		notificationFlag.add("sms");
		request.setNotificationTypes(notificationFlag);
		request.setEmailBodyTemplate("your Otp $otp");
		request.setEmailId("abc@gmail.com");
		request.setEmailSubjectTemplate("OTP ALERT");
		request.setMobileNumber("8989898998");
		request.setSmsTemplate(null);
		service.sendOtpNotification(request);
	}

	@Test(expected = OtpNotificationInvalidArgumentException.class)
	public void sendOtpNotificationEmptySmsTemplateExceptionTest() {

		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		List<String> notificationFlag = new ArrayList<>();
		notificationFlag.add("sms");
		request.setNotificationTypes(notificationFlag);
		request.setEmailBodyTemplate("your Otp $otp");
		request.setEmailId("abc@gmail.com");
		request.setEmailSubjectTemplate("OTP ALERT");
		request.setMobileNumber("8989898998");
		request.setSmsTemplate("");
		service.sendOtpNotification(request);
	}

	@Test(expected = OtpNotificationInvalidArgumentException.class)
	public void sendOtpNotificationNullEmailSubjectExceptionTest() {

		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		List<String> notificationFlag = new ArrayList<>();
		notificationFlag.add("email");
		request.setNotificationTypes(notificationFlag);
		request.setEmailBodyTemplate("your otp $otp");
		request.setEmailId("abc@gmail.com");
		request.setEmailSubjectTemplate(null);
		request.setMobileNumber("8989898998");
		request.setSmsTemplate("OTP $otp");
		service.sendOtpNotification(request);
	}

	@Test(expected = OtpNotificationInvalidArgumentException.class)
	public void sendOtpNotificationEmptyEmailSubjectExceptionTest() {

		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		List<String> notificationFlag = new ArrayList<>();
		notificationFlag.add("email");
		request.setNotificationTypes(notificationFlag);
		request.setEmailBodyTemplate("your otp $otp");
		request.setEmailId("abc@gmail.com");
		request.setEmailSubjectTemplate("");
		request.setMobileNumber("8989898998");
		request.setSmsTemplate("OTP $otp");
		service.sendOtpNotification(request);
	}

	@Test(expected = OtpNotificationInvalidArgumentException.class)
	public void sendOtpNotificationNullSmsNumberExceptionTest() {

		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		List<String> notificationFlag = new ArrayList<>();
		notificationFlag.add("sms");
		request.setNotificationTypes(notificationFlag);
		request.setEmailBodyTemplate("your Otp $otp");
		request.setEmailId("abc@gmail.com");
		request.setEmailSubjectTemplate("OTP ALERT");
		request.setMobileNumber(null);
		request.setSmsTemplate("your otp $otp");
		service.sendOtpNotification(request);
	}

	@Test(expected = OtpNotificationInvalidArgumentException.class)
	public void sendOtpNotificationEmptySmsNumberExceptionTest() {

		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		List<String> notificationFlag = new ArrayList<>();
		notificationFlag.add("sms");
		request.setNotificationTypes(notificationFlag);
		request.setEmailBodyTemplate("your Otp $otp");
		request.setEmailId("abc@gmail.com");
		request.setEmailSubjectTemplate("OTP ALERT");
		request.setMobileNumber("");
		request.setSmsTemplate("your otp $otp");
		service.sendOtpNotification(request);
	}

	@Test(expected = OtpNotificationInvalidArgumentException.class)
	public void sendOtpNotificationNullEmailIdExceptionTest() {

		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		List<String> notificationFlag = new ArrayList<>();
		notificationFlag.add("email");
		request.setNotificationTypes(notificationFlag);
		request.setEmailBodyTemplate("your Otp $otp");
		request.setEmailId(null);
		request.setEmailSubjectTemplate("OTP ALERT");
		request.setMobileNumber("8989898989");
		request.setSmsTemplate("your otp $otp");
		service.sendOtpNotification(request);
	}

	@Test(expected = OtpNotificationInvalidArgumentException.class)
	public void sendOtpNotificationEmptyEmailIdExceptionTest() {

		OtpNotificationRequestDto request = new OtpNotificationRequestDto();
		List<String> notificationFlag = new ArrayList<>();
		notificationFlag.add("email");
		request.setNotificationTypes(notificationFlag);
		request.setEmailBodyTemplate("your Otp $otp");
		request.setEmailId("");
		request.setEmailSubjectTemplate("OTP ALERT");
		request.setMobileNumber("8989898989");
		request.setSmsTemplate("your otp $otp");
		service.sendOtpNotification(request);
	}
}
