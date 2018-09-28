package org.mosip.kernel.otpmanagerservice.generator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mosip.kernel.otpmanagerservice.dto.OtpGeneratorRequestDto;
import org.mosip.kernel.otpmanagerservice.dto.OtpGeneratorResponseDto;
import org.mosip.kernel.otpmanagerservice.entity.OtpEntity;
import org.mosip.kernel.otpmanagerservice.repository.OtpRepository;
import org.mosip.kernel.otpmanagerservice.service.impl.OtpGeneratorServiceImpl;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class GeneratorServiceTest {
	@Mock
	private OtpRepository repository;

	@InjectMocks
	private OtpGeneratorServiceImpl service;

	@Test
	public void testWhenKeyFreezed() throws Exception {
		LocalDateTime validationTime = LocalDateTime.now().minus(1, ChronoUnit.MINUTES);
		OtpGeneratorRequestDto otpDto = new OtpGeneratorRequestDto();
		OtpGeneratorResponseDto responseDto = new OtpGeneratorResponseDto();
		responseDto.setOtp("null");
		responseDto.setStatus("BLOCKED_USER");
		otpDto.setKey("testKey");
		String key = "testKey";
		OtpEntity entity = new OtpEntity();
		entity.setValidationTime(validationTime);
		entity.setOtpStatus("KEY_FREEZED");
		given(repository.findById(OtpEntity.class, key)).willReturn(entity);
		service.getOtp(otpDto);
		verify(repository, times(1)).findById(OtpEntity.class, key);
	}

	@Test
	public void testWhenBlockedUser() throws Exception {
		OtpGeneratorRequestDto otpDto = new OtpGeneratorRequestDto();
		OtpGeneratorResponseDto responseDto = new OtpGeneratorResponseDto();
		responseDto.setOtp("null");
		responseDto.setStatus("BLOCKED_USER");
		otpDto.setKey("testKey");
		String key = "testKey";
		given(repository.findById(OtpEntity.class, key)).willReturn(null);
		service.getOtp(otpDto);
		verify(repository, times(1)).findById(OtpEntity.class, key);
	}
}
