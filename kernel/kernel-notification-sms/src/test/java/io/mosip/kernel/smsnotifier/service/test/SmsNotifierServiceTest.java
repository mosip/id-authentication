package io.mosip.kernel.smsnotifier.service.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.util.exception.MosipIOException;
import io.mosip.kernel.core.util.exception.MosipJsonMappingException;
import io.mosip.kernel.core.util.exception.MosipJsonParseException;
import io.mosip.kernel.smsnotifier.SmsNotifierApplication;
import io.mosip.kernel.smsnotifier.constant.SmsPropertyConstants;
import io.mosip.kernel.smsnotifier.dto.SmsResponseDto;
import io.mosip.kernel.smsnotifier.dto.SmsServerResponseDto;
import io.mosip.kernel.smsnotifier.exception.MosipHttpClientException;
import io.mosip.kernel.smsnotifier.service.impl.SmsNotifierServiceImpl;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { SmsNotifierApplication.class })
@TestPropertySource("classpath:application.properties")
public class SmsNotifierServiceTest {

	@Autowired
	SmsNotifierServiceImpl service;

	@MockBean
	RestTemplateBuilder restTemplateBuilder;

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
	public void sendSmsNotificationTest() throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {

		UriComponentsBuilder sms = UriComponentsBuilder.fromHttpUrl(api)
				.queryParam(SmsPropertyConstants.AUTH_KEY.getProperty(), authkey)
				.queryParam(SmsPropertyConstants.SMS_MESSAGE.getProperty(), "your otp is 4646")
				.queryParam(SmsPropertyConstants.ROUTE.getProperty(), route)
				.queryParam(SmsPropertyConstants.SENDER_ID.getProperty(), senderId)
				.queryParam(SmsPropertyConstants.RECIPIENT_NUMBER.getProperty(), "8987876473")
				.queryParam(SmsPropertyConstants.COUNTRY_CODE.getProperty(), countryCode);

		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

		SmsServerResponseDto serverResponse = new SmsServerResponseDto();
		serverResponse.setType("success");
		SmsResponseDto dto = new SmsResponseDto();
		dto.setStatus(serverResponse.getType());
		dto.setMessage("Sms Request Sent");
		when(restTemplateBuilder.build()).thenReturn(restTemplate);

		when(restTemplate.getForEntity(sms.toUriString(), SmsServerResponseDto.class))
				.thenReturn(new ResponseEntity<>(serverResponse, HttpStatus.OK));

		assertThat(service.sendSmsNotification("8987876473", "your otp is 4646"), is(dto));

	}

	@Test(expected = MosipHttpClientException.class)
	public void sendSmsNotificationExceptionFirstTest()
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {
		UriComponentsBuilder sms = UriComponentsBuilder.fromHttpUrl(api)
				.queryParam(SmsPropertyConstants.AUTH_KEY.getProperty(), authkey)
				.queryParam(SmsPropertyConstants.SMS_MESSAGE.getProperty(), "your otp is 4646")
				.queryParam(SmsPropertyConstants.ROUTE.getProperty(), route)
				.queryParam(SmsPropertyConstants.SENDER_ID.getProperty(), senderId)
				.queryParam(SmsPropertyConstants.RECIPIENT_NUMBER.getProperty(), "8987876473")
				.queryParam(SmsPropertyConstants.COUNTRY_CODE.getProperty(), countryCode);

		RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
		when(restTemplateBuilder.build()).thenReturn(restTemplate);
		when(restTemplate.getForEntity(sms.toUriString(), SmsServerResponseDto.class))
				.thenThrow(MosipHttpClientException.class);
		service.sendSmsNotification("8987876473", "your otp is 4646");
	}

}
