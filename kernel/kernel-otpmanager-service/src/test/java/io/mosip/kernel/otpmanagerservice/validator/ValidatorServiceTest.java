package io.mosip.kernel.otpmanagerservice.validator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.otpmanagerservice.entity.OtpEntity;
import io.mosip.kernel.otpmanagerservice.exceptionhandler.MosipOtpInvalidArgumentExceptionHandler;
import io.mosip.kernel.otpmanagerservice.repository.OtpRepository;
import io.mosip.kernel.otpmanagerservice.service.impl.OtpValidatorServiceImpl;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@TestPropertySource("classpath:/test.application.properties")
public class ValidatorServiceTest {
	@Mock
	private OtpRepository repository;

	@InjectMocks
	private OtpValidatorServiceImpl service;

	@Test
	public void testWhenOtpUnusedandZeroAttemptCount() throws Exception {
		String key = "testKey";
		String otp = "1234";
		OtpEntity entity = new OtpEntity();
		entity.setGeneratedOtp(otp);
		entity.setKeyId(key);
		entity.setNumOfAttempt(0);
		entity.setOtpStatus("OTP_UNUSED");
		entity.setValidationTime(LocalDateTime.now());
		given(repository.findById(OtpEntity.class, key)).willReturn(entity);
		service.validateOtp(key, otp);
		verify(repository, times(1)).findById(OtpEntity.class, key);
	}

	@Test(expected = MosipOtpInvalidArgumentExceptionHandler.class)
	public void testForException() throws Exception {
		String key = "testKey";
		String otp = "1234";
		given(repository.findById(OtpEntity.class, key)).willReturn(null);
		service.validateOtp(key, otp);
		verify(repository, times(1)).findById(OtpEntity.class, key);
	}

	@Test
	public void testWhenOTPUnusedAndLastAttemptCount() throws Exception {
		String key = "testKey";
		String otp = "1234";
		OtpEntity entity = new OtpEntity();
		entity.setGeneratedOtp(otp);
		entity.setKeyId(key);
		entity.setNumOfAttempt(3);
		entity.setOtpStatus("OTP_UNUSED");
		entity.setValidationTime(LocalDateTime.now());
		given(repository.findById(OtpEntity.class, key)).willReturn(entity);
		service.validateOtp(key, otp);
		verify(repository, times(1)).findById(OtpEntity.class, key);
	}

	@Test
	public void testWhenKeyFreezedForSameOTP() throws Exception {
		String key = "testKey";
		String otp = "1234";
		OtpEntity entity = new OtpEntity();
		entity.setGeneratedOtp(otp);
		entity.setKeyId(key);
		entity.setNumOfAttempt(0);
		entity.setOtpStatus("KEY_FREEZED");
		entity.setValidationTime(LocalDateTime.now().minus(4, ChronoUnit.MINUTES));
		given(repository.findById(OtpEntity.class, key)).willReturn(entity);
		service.validateOtp(key, otp);
		verify(repository, times(1)).findById(OtpEntity.class, key);
	}

	@Test
	public void testWhenKeyFreezedForDifferentOTP() throws Exception {
		String key = "testKey";
		String otp = "1234";
		OtpEntity entity = new OtpEntity();
		entity.setGeneratedOtp("2546");
		entity.setKeyId(key);
		entity.setNumOfAttempt(0);
		entity.setOtpStatus("KEY_FREEZED");
		entity.setValidationTime(LocalDateTime.now().minus(4, ChronoUnit.MINUTES));
		given(repository.findById(OtpEntity.class, key)).willReturn(entity);
		service.validateOtp(key, otp);
		verify(repository, times(1)).findById(OtpEntity.class, key);
	}
}
