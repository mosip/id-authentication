package io.mosip.authentication.service.impl.otpgen.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
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

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.otpgen.facade.OTPFacade;
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
	OTPFacade otpFacade;
	@Mock
	Date date;
	@Mock
	IdAuthenticationBusinessException idAuthenticationBusinessException;
	@Mock
	IdAuthenticationAppException idAuthenticationAppException;
	@Mock
	WebDataBinder binder;

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
		Mockito.when(otpFacade.generateOtp(otpRequestDto)).thenReturn(otpResponseDTO);
		OtpResponseDTO expactedresponse = otpController.generateOTP(otpRequestDto, result);

		assertEquals(otpResponseDTO.getStatus(), expactedresponse.getStatus());
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
		otpController.generateOTP(otpRequestDto, result);
		assertEquals(true, result.hasErrors());
	}

	@Ignore
	@Test(expected = IdAuthenticationAppException.class)
	public void testConstraintVoilation() throws IdAuthenticationAppException {
		boolean hasError = true;
		otpRequestDto = getOtpRequestDTO();
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
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage());
		idAuthenticationAppException = new IdAuthenticationAppException(
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage(),
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
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage());
		idAuthenticationAppException = new IdAuthenticationAppException(
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage(),
				idAuthenticationBusinessException);

		Set<ConstraintViolation<OtpRequestDTO>> violations = validator.validate(otpRequestDto);
		assertTrue(violations.isEmpty());

		Mockito.when(result.hasErrors()).thenReturn(hasError);
		Mockito.when(otpFacade.generateOtp(otpRequestDto)).thenThrow(idAuthenticationBusinessException);
		Mockito.when(otpController.generateOTP(otpRequestDto, result)).thenThrow(idAuthenticationAppException);

	}

	@Test(expected = IdAuthenticationAppException.class)
	public void testGenerateOtpDataValidationException() throws IdAuthenticationAppException {
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO.class, "OtpRequestDTO");
		errors.reject("errorCode");
		otpController.generateOTP(new OtpRequestDTO(), errors);
	}

	// =========================================================
	// ************ Helping Method *****************************
	// =========================================================

	private OtpRequestDTO getOtpRequestDTO() {
		OtpRequestDTO otpRequestDto = new OtpRequestDTO();
		otpRequestDto.setMuaCode("1234567890");
		otpRequestDto.setIdvIdType(IdType.UIN.getType());
		// otpRequestDto.setRequestTime(new Date());
		otpRequestDto.setTxnID("1234567890");
		otpRequestDto.setIdvId("1234567890");
		otpRequestDto.setVer("1.0");

		return otpRequestDto;
	}

	private OtpResponseDTO getOtpResponseDTO() {
		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		otpResponseDTO.setStatus("OTP_GENERATED");
		otpResponseDTO.setResTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));

		return otpResponseDTO;
	}
}