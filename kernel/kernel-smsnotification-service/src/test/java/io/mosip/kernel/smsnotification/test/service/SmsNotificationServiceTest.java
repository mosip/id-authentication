package io.mosip.kernel.smsnotification.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.smsnotification.dto.SmsResponseDto;
import io.mosip.kernel.smsnotification.dto.SmsServerResponseDto;
import io.mosip.kernel.smsnotification.exception.InvalidNumberException;
import io.mosip.kernel.smsnotification.service.impl.SmsNotificationServiceImpl;
import io.mosip.kernel.smsnotification.test.SmsNotificationTestBootApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SmsNotificationTestBootApplication.class })
public class SmsNotificationServiceTest {

	@Autowired
	SmsNotificationServiceImpl service;

	@MockBean
	RestTemplate restTemplate;

	@Test
	public void sendSmsNotificationTest() {

		SmsServerResponseDto serverResponse = new SmsServerResponseDto();
		serverResponse.setType("success");
		SmsResponseDto dto = new SmsResponseDto();
		dto.setStatus(serverResponse.getType());
		dto.setMessage("Sms Request Sent");

		when(restTemplate.postForEntity(Mockito.anyString(), Mockito.eq(Mockito.any()), Object.class))
				.thenReturn(new ResponseEntity<>(serverResponse, HttpStatus.OK));

		assertThat(service.sendSmsNotification("8987876473", "your otp is 4646"), is(dto));

	}

	@Test(expected = InvalidNumberException.class)
	public void invalidContactNumberTest() {
		service.sendSmsNotification("jsbchb", "hello your otp is 45373");
	}

	@Test(expected = InvalidNumberException.class)
	public void contactNumberMinimumThresholdTest() {
		service.sendSmsNotification("78978976", "hello your otp is 45373");
	}

	@Test(expected = InvalidNumberException.class)
	public void contactNumberMaximumThresholdTest() {
		service.sendSmsNotification("7897897458673484376", "hello your otp is 45373");
	}

}
