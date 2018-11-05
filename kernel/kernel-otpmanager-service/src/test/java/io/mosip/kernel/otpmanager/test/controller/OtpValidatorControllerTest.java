package io.mosip.kernel.otpmanager.test.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.otpmanager.controller.OtpValidatorController;
import io.mosip.kernel.otpmanager.dto.OtpValidatorResponseDto;
import io.mosip.kernel.otpmanager.service.impl.OtpValidatorServiceImpl;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class OtpValidatorControllerTest {
	@Mock
	private OtpValidatorServiceImpl service;

	@InjectMocks
	private OtpValidatorController controller;

	@Test
	public void testOtpValidationController() throws Exception {
		String key = "testKey";
		String otp = "1234";
		OtpValidatorResponseDto responseDto = new OtpValidatorResponseDto();
		responseDto.setMessage("VALIDATION UNSUCCESSFUL");
		responseDto.setStatus("false");
		ResponseEntity<OtpValidatorResponseDto> validationResponseEntity = new ResponseEntity<>(responseDto,
				HttpStatus.OK);
		given(service.validateOtp(key, otp)).willReturn(validationResponseEntity);
		controller.validateOtp(key, otp);
		verify(service, times(1)).validateOtp(key, otp);
	}
}
