package io.mosip.kernel.smsnotifier.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.smsnotifier.dto.SmsRequestDto;
import io.mosip.kernel.smsnotifier.dto.SmsResponseDto;
import io.mosip.kernel.smsnotifier.service.impl.SmsNotifierServiceImpl;

@RunWith(SpringRunner.class)
public class SmsNotifierControllerTest {

	@Mock
	private SmsNotifierServiceImpl service;

	@InjectMocks
	private SmsNotifierController controller;

	@Test
	public void sendSmsTest() throws Exception {

		SmsRequestDto requestDto = new SmsRequestDto();
		requestDto.setMessage("hello..sir your otp is 787962");
		requestDto.setNumber("9889874642");
		
		SmsResponseDto responseDto = new SmsResponseDto();
		responseDto.setMessage("Sms Request Sent");
		responseDto.setStatus("success");
		
		ResponseEntity<SmsResponseDto> result = new ResponseEntity<>(responseDto, HttpStatus.OK);

		given(service.sendSmsNotification(requestDto.getNumber(), requestDto.getMessage())).willReturn(responseDto);

		assertThat(controller.sendSms(requestDto), is(result));

	}

}