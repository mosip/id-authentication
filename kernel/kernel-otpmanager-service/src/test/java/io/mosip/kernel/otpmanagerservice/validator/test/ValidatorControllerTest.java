package io.mosip.kernel.otpmanagerservice.validator.test;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.otpmanagerservice.controller.OtpValidatorController;
import io.mosip.kernel.otpmanagerservice.service.impl.OtpValidatorServiceImpl;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@TestPropertySource("classpath:/test.application.properties")
public class ValidatorControllerTest {
	@Mock
	private OtpValidatorServiceImpl service;

	@InjectMocks
	private OtpValidatorController controller;

	@Test
	public void testForOtpValidation() throws Exception {
		String key = "testKey";
		String otp = "1234";
		given(service.validateOtp(key, otp)).willReturn(true);
		controller.validateOtp(key, otp);
		verify(service, times(1)).validateOtp(key, otp);

	}
}
