package io.mosip.authentication.otp.service.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.impl.IdServiceImpl;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.otp.dto.OtpResponseDTO;
import io.mosip.authentication.core.spi.otp.service.OTPService;
import io.mosip.authentication.otp.service.controller.OTPController;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * Test functionality
 *
 * @author Rakesh Roshan
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class OTPControllerTest {

	/** Mock the objects */
	@Autowired
	Environment env;
	@Mock
	OtpRequestDTO otpRequestDto;
	@Mock
	OtpResponseDTO otpResponseDTO;
	@Mock
	Logger LOGGER;
	@Mock
	BindingResult result;
	@Mock
	private OTPService otpService;
	@Mock
	Date date;

	@Mock
	IdAuthenticationBusinessException idAuthenticationBusinessException;
	@Mock
	IdAuthenticationAppException idAuthenticationAppException;
	@Mock
	WebDataBinder binder;

	@Mock
	IdServiceImpl idServiceImpl;

	@Mock
	AuditHelper auditHelper;

	/** inject the mocked object */
	@InjectMocks
	OTPController otpController;

	private static Validator validator;

	@Before
	public void before() {
		ReflectionTestUtils.invokeMethod(otpController, "initBinder", binder);
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
		Mockito.when(otpService.generateOtp(Mockito.any(), Mockito.any())).thenReturn(otpResponseDTO);
		otpController.generateOTP(otpRequestDto, result, "TEST0000001", "TEST0000001");

	}

	@Ignore
	@Test(expected = IdAuthenticationAppException.class)
	public void testBindResultHasError() throws IdAuthenticationBusinessException, IdAuthenticationAppException {

		// Given
		boolean hasError = true;
		otpRequestDto = getOtpRequestDTO();
		otpResponseDTO = getOtpResponseDTO();

		Set<ConstraintViolation<OtpRequestDTO>> violations = validator.validate(otpRequestDto);
		assertTrue(violations.isEmpty());
		Mockito.when(result.hasErrors()).thenReturn(hasError);
		otpController.generateOTP(otpRequestDto, result, "TEST0000001", "TEST0000001");
		assertEquals(true, result.hasErrors());
	}

	@Ignore
	@Test(expected = IdAuthenticationAppException.class)
	public void testConstraintVoilation() throws IdAuthenticationAppException, IDDataValidationException {
		boolean hasError = true;
		otpRequestDto = getOtpRequestDTO();
		otpResponseDTO = getOtpResponseDTO();

		Set<ConstraintViolation<OtpRequestDTO>> violations = validator.validate(otpRequestDto);
		assertEquals(violations.size(), 1);
		Mockito.when(result.hasErrors()).thenReturn(hasError);
		otpController.generateOTP(otpRequestDto, result, "TEST0000001", "TEST0000001");
		assertEquals(true, result.hasErrors());
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void testOtpGeneratedIsFalse() throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		// Given
		boolean hasError = false;
		otpRequestDto = getOtpRequestDTO();
		otpResponseDTO = getOtpResponseDTO();
		idAuthenticationBusinessException = new IdAuthenticationBusinessException(
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage());
		idAuthenticationAppException = new IdAuthenticationAppException(
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage(),
				idAuthenticationBusinessException);

		Set<ConstraintViolation<OtpRequestDTO>> violations = validator.validate(otpRequestDto);
		assertTrue(violations.isEmpty());

		Mockito.when(result.hasErrors()).thenReturn(hasError);
		Mockito.when(otpService.generateOtp(otpRequestDto, "TEST0000001")).thenThrow(idAuthenticationBusinessException);
		Mockito.when(otpController.generateOTP(otpRequestDto, result, "TEST0000001", "TEST0000001"))
				.thenThrow(idAuthenticationAppException);
		otpController.generateOTP(otpRequestDto, result, "TEST0000001", "TEST0000001");
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void testOtpGenerationHasError() throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		// Given
		boolean hasError = false;
		otpRequestDto = getOtpRequestDTO();
		otpResponseDTO = getOtpResponseDTO();
		idAuthenticationBusinessException = new IdAuthenticationBusinessException(
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage());
		idAuthenticationAppException = new IdAuthenticationAppException(
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage(),
				idAuthenticationBusinessException);

		Set<ConstraintViolation<OtpRequestDTO>> violations = validator.validate(otpRequestDto);
		assertTrue(violations.isEmpty());

		Mockito.when(result.hasErrors()).thenReturn(hasError);
		Mockito.when(otpService.generateOtp(otpRequestDto, "TEST0000001")).thenThrow(idAuthenticationBusinessException);
		Mockito.when(otpController.generateOTP(otpRequestDto, result, "TEST0000001", "TEST0000001"))
				.thenThrow(idAuthenticationAppException);

	}

	@Test(expected = IdAuthenticationAppException.class)
	public void testGenerateOtpDataValidationException()
			throws IdAuthenticationAppException, IDDataValidationException {
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO.class, "OtpRequestDTO");
		errors.reject("errorCode");
		otpController.generateOTP(new OtpRequestDTO(), errors, "TEST0000001", "TEST0000001");
	}

	@Test
	public void TestValidOtpRequest() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		OtpRequestDTO otpRequestDTO = new OtpRequestDTO();
		otpRequestDTO.setId("mosip.identity.otp");
		otpRequestDTO.setIndividualId("274390482564");
		otpRequestDTO.setIndividualIdType(IdType.UIN.getType());
		List<String> otpChannel = new ArrayList<>();
		otpChannel.add("email");
		otpChannel.add("mobile");
		otpRequestDTO.setOtpChannel(otpChannel);
		otpRequestDTO.setRequestTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));
		otpRequestDTO.setVersion("1.0");
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO.class, "OtpRequestDTO");
		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		List<AuthError> autherror=new ArrayList<>();
		otpResponseDTO.setErrors(autherror);
		otpResponseDTO.setResponseTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Mockito.when(otpService.generateOtp(Mockito.any(), Mockito.any())).thenReturn(otpResponseDTO);

		otpController.generateOTP(otpRequestDTO, errors, "121212", "232323");
	}

	// =========================================================
	// ************ Helping Method *****************************
	// =========================================================

	private OtpRequestDTO getOtpRequestDTO() {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		// otpRequestDto.setRequestTime(new Date());
		otpRequestDto.setTransactionID("1234567890");
		// otpRequestDto.setVer("1.0");

		return otpRequestDto;
	}

	private OtpResponseDTO getOtpResponseDTO() {
		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		otpResponseDTO.setResponseTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));
		return otpResponseDTO;
	}
}