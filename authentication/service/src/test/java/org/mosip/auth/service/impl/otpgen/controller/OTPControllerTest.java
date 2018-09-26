package org.mosip.auth.service.impl.otpgen.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.dto.indauth.IDType;
import org.mosip.auth.core.dto.otpgen.OtpRequestDTO;
import org.mosip.auth.core.dto.otpgen.OtpResponseDTO;
import org.mosip.auth.core.exception.IdAuthenticationAppException;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.spi.otpgen.facade.OTPFacade;
import org.mosip.kernel.core.logging.MosipLogger;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindingResult;

/**
 * Test functionality 
 *
 * @author Rakesh Roshan
 */
@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(value = OTPController.class, secure = false)
@TestPropertySource(value = "classpath:log.properties")
public class OTPControllerTest {

	/** Mock the objects */
	@Mock
	Environment env;
	@Mock
	OtpRequestDTO otpRequestDto;
	@Mock
	OtpResponseDTO otpResponseDTO;
	@Mock
	MosipLogger LOGGER;
	@Mock
	BindingResult result;
	@Mock
	OTPFacade otpFacade;
	@Mock
	Date date;
	@Mock
	IdAuthenticationBusinessException idAuthenticationBusinessException;
	@Mock
	IdAuthenticationAppException idAuthenticationAppException;

	/** inject the mocked object */
	@InjectMocks
	OTPController otpController;

	private static Validator validator;

	@Before
	public void before() {
		/*MosipRollingFileAppender mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName(env.getProperty("log4j.appender.Appender"));
		mosipRollingFileAppender.setFileName(env.getProperty("log4j.appender.Appender.file"));
		mosipRollingFileAppender.setFileNamePattern(env.getProperty("log4j.appender.Appender.filePattern"));
		mosipRollingFileAppender.setMaxFileSize(env.getProperty("log4j.appender.Appender.maxFileSize"));
		mosipRollingFileAppender.setTotalCap(env.getProperty("log4j.appender.Appender.totalCap"));
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);
		
		ReflectionTestUtils.invokeMethod(otpController, "initializeLogger", mosipRollingFileAppender);*/
	}

	@BeforeClass
	public static void setUp() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Test
	public void testGeneratedOtp() throws IdAuthenticationBusinessException, IdAuthenticationAppException {

		// Given
		boolean hasError = false;
		otpRequestDto = getOtpRequestDTO();
		otpResponseDTO = getOtpResponseDTO();
		date = new Date();

		Set<ConstraintViolation<OtpRequestDTO>> violations = validator.validate(otpRequestDto);
		assertTrue(violations.isEmpty());

		Mockito.when(result.hasErrors()).thenReturn(hasError);
		Mockito.when(otpFacade.generateOtp(otpRequestDto)).thenReturn(otpResponseDTO);
		OtpResponseDTO expactedresponse = otpController.generateOTP(otpRequestDto, result);

		assertEquals(otpResponseDTO.getStatus(), expactedresponse.getStatus());
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void testBindResultHasError() throws IdAuthenticationBusinessException, IdAuthenticationAppException {

		// Given
		boolean hasError = true;
		otpRequestDto = getOtpRequestDTO();
		otpResponseDTO = getOtpResponseDTO();

		Set<ConstraintViolation<OtpRequestDTO>> violations = validator.validate(otpRequestDto);
		assertTrue(violations.isEmpty());
		Mockito.when(result.hasErrors()).thenReturn(hasError);
		otpController.generateOTP(otpRequestDto, result);
		assertEquals(true, result.hasErrors());
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void testConstraintVoilation() throws IdAuthenticationAppException {
		boolean hasError = true;
		otpRequestDto = getOtpRequestDTO();
		otpRequestDto.setAsaLicenseKey("54645");
		otpResponseDTO = getOtpResponseDTO();

		Set<ConstraintViolation<OtpRequestDTO>> violations = validator.validate(otpRequestDto);
		assertEquals(violations.size(), 1);
		Mockito.when(result.hasErrors()).thenReturn(hasError);
		otpController.generateOTP(otpRequestDto, result);
		assertEquals(true, result.hasErrors());
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void testOtpGeneratedIsFalse() throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		// Given
		boolean hasError = false;
		otpRequestDto = getOtpRequestDTO();
		otpResponseDTO = getOtpResponseDTO();
		idAuthenticationBusinessException = new IdAuthenticationBusinessException(
				IdAuthenticationErrorConstants.OTP_GENERATION_REQUEST_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_REQUEST_FAILED.getErrorMessage());
		idAuthenticationAppException = new IdAuthenticationAppException(
				IdAuthenticationErrorConstants.OTP_GENERATION_REQUEST_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_REQUEST_FAILED.getErrorMessage(),
				idAuthenticationBusinessException);

		Set<ConstraintViolation<OtpRequestDTO>> violations = validator.validate(otpRequestDto);
		assertTrue(violations.isEmpty());

		Mockito.when(result.hasErrors()).thenReturn(hasError);
		Mockito.when(otpFacade.generateOtp(otpRequestDto)).thenThrow(idAuthenticationBusinessException);
		Mockito.when(otpController.generateOTP(otpRequestDto, result)).thenThrow(idAuthenticationAppException);
		otpController.generateOTP(otpRequestDto, result);
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void testOtpGenerationHasError() throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		// Given
		boolean hasError = false;
		otpRequestDto = getOtpRequestDTO();
		otpResponseDTO = getOtpResponseDTO();
		idAuthenticationBusinessException = new IdAuthenticationBusinessException(
				IdAuthenticationErrorConstants.OTP_GENERATION_REQUEST_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_REQUEST_FAILED.getErrorMessage());
		idAuthenticationAppException = new IdAuthenticationAppException(
				IdAuthenticationErrorConstants.OTP_GENERATION_REQUEST_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_REQUEST_FAILED.getErrorMessage(),
				idAuthenticationBusinessException);

		Set<ConstraintViolation<OtpRequestDTO>> violations = validator.validate(otpRequestDto);
		assertTrue(violations.isEmpty());

		Mockito.when(result.hasErrors()).thenReturn(hasError);
		Mockito.when(otpFacade.generateOtp(otpRequestDto)).thenThrow(idAuthenticationBusinessException);
		Mockito.when(otpController.generateOTP(otpRequestDto, result)).thenThrow(idAuthenticationAppException);

	}

	// =========================================================
	// ************ Helping Method *****************************
	// =========================================================

	private OtpRequestDTO getOtpRequestDTO() {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setAsaLicenseKey("1234567890");
		otpRequestDto.setAuaCode("1234567890");
		otpRequestDto.setIdType(IDType.UIN);
		// otpRequestDto.setRequestTime(new Date());
		otpRequestDto.setTxnID("1234567890");
		otpRequestDto.setUniqueID("1234567890");
		otpRequestDto.setVersion("1.0");

		return otpRequestDto;
	}

	private OtpResponseDTO getOtpResponseDTO() {
		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		otpResponseDTO.setStatus("OTP_GENERATED");
		otpResponseDTO.setResponseTime(new Date());

		return otpResponseDTO;
	}
}