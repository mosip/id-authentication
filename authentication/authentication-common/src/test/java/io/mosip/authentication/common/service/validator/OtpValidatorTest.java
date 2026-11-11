package io.mosip.authentication.common.service.validator;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class})
@Import(EnvUtil.class)
public class OtpValidatorTest {

	@InjectMocks
	private OTPRequestValidator otpRequestValidator;
	
	@Autowired
	EnvUtil envUtil;
	
	
	
	@BeforeEach
	public void before() {
//		ReflectionTestUtils.setField(envUtil, "env", env);
//		envUtil.init();
	}
	
	@Test
	public void validateReqTimeNull() {
		OtpRequestDTO otpRequestDTO = createOTPRequest();
		otpRequestDTO.setRequestTime(null);
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "otpRequestDTO");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
		errors.getAllErrors().stream().forEach(err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("requestTime"))));
	}
	
	@Test
	public void validateReqTimeEmpty() {
		OtpRequestDTO otpRequestDTO = createOTPRequest();
		otpRequestDTO.setRequestTime("");
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "otpRequestDTO");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
		errors.getAllErrors().stream().forEach(err -> assertTrue(Stream.of(err.getArguments()).anyMatch(error -> error.equals("requestTime"))));
	}
	
	@Test
	public void validateReqTimeFuture() {
		OtpRequestDTO otpRequestDTO = createOTPRequest();
		otpRequestDTO.setRequestTime(Instant.now().plus(2, ChronoUnit.DAYS).atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "otpRequestDTO");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-001")));
	}
	
	@Test
	public void validateReqTimePast() {
		OtpRequestDTO otpRequestDTO = createOTPRequest();
		otpRequestDTO.setRequestTime(Instant.now().minus(2, ChronoUnit.DAYS).atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "otpRequestDTO");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-001")));
	}
	
	@Test
	public void validateReqTimeInvalid() {
		OtpRequestDTO otpRequestDTO = createOTPRequest();
		otpRequestDTO.setRequestTime(new Date().toString());
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "otpRequestDTO");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-009")));
	}
	
	@Test
	public void validateTxnIdNull() {
		OtpRequestDTO otpRequestDTO = createOTPRequest();
		otpRequestDTO.setTransactionID(null);
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "otpRequestDTO");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
	}
	
	@Test
	public void validateTxnIdEmpty() {
		OtpRequestDTO otpRequestDTO = createOTPRequest();
		otpRequestDTO.setTransactionID("");
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "otpRequestDTO");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-006")));
	}
	
	@Test
	public void validateTxnIdInvalid() {
		OtpRequestDTO otpRequestDTO = createOTPRequest();
		otpRequestDTO.setTransactionID(new Date().toString());
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "otpRequestDTO");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-009")));
	}
	
	private OtpRequestDTO createOTPRequest() {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		otpRequestDTO.setId("mosip.identity.otp");
		otpRequestDTO.setIndividualId("3926509647");
		otpRequestDTO.setIndividualIdType("UIN");
		otpRequestDTO.setOtpChannel(Collections.singletonList("email"));
		otpRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		otpRequestDTO.setTransactionID("1234567890");
		otpRequestDTO.setVersion("1.0");
		return otpRequestDTO;
	}
}
