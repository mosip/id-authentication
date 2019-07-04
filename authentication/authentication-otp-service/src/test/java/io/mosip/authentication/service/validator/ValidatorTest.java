package io.mosip.authentication.service.validator;

import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.otp.service.validator.OTPRequestValidator;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class})
public class ValidatorTest {

	@InjectMocks
	private OTPRequestValidator otpRequestValidator;
	
	@Autowired
	Environment env;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(otpRequestValidator, "env", env);
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
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(otpRequestDTO, "otpRequestDTO");
		otpRequestValidator.validate(otpRequestDTO, errors);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-001")));
	}
	
	@Test
	public void validateReqTimePast() {
		OtpRequestDTO otpRequestDTO = createOTPRequest();
		otpRequestDTO.setRequestTime(Instant.now().minus(2, ChronoUnit.DAYS).atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
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
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		otpRequestDTO.setTransactionID("1234567890");
		otpRequestDTO.setVersion("1.0");
		return otpRequestDTO;
	}
}
