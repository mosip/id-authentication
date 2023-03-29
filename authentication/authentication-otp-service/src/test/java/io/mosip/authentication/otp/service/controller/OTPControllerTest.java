package io.mosip.authentication.otp.service.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

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
import org.springframework.context.annotation.Import;
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
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.impl.IdServiceImpl;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.TestHttpServletRequest;
import io.mosip.authentication.common.service.validator.OTPRequestValidator;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.otp.dto.OtpResponseDTO;
import io.mosip.authentication.core.spi.otp.service.OTPService;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * Test functionality
 *
 * @author Rakesh Roshan
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@Import(EnvUtil.class)
public class OTPControllerTest {

	/** Mock the objects */
	@Autowired
	EnvUtil env;
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
	
	@Mock
	AuthTransactionHelper authTransactionHelper;
	
	@Mock
	PartnerService partnerService;

	/** inject the mocked object */
	@InjectMocks
	OTPController otpController;
	
	@Mock
	private IdTypeUtil idTypeUtil;

	@Mock
	private OTPRequestValidator otpRequestValidator;

	@Mock
	private IdAuthSecurityManager securityManager;

	private static Validator validator;

	@Before
	public void before() throws IdAuthenticationBusinessException {
		ReflectionTestUtils.invokeMethod(otpController, "initBinder", binder);
		ReflectionTestUtils.setField(otpController, "otpRequestValidator", otpRequestValidator);
		when(idTypeUtil.getIdType(Mockito.any())).thenReturn(IdType.UIN);
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
		Mockito.when(otpService.generateOtp(Mockito.any(), Mockito.anyString(), Mockito.any())).thenReturn(otpResponseDTO);
		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("Zld6TjJjNllKYzExNjBFUUZrbmdzYnJMelRJQ1BY");
		otpController.generateOTP(otpRequestDto, result, "TEST0000001", "TEST0000001","TEST0000001", new TestHttpServletRequest());

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
		otpController.generateOTP(otpRequestDto, result, "TEST0000001", "TEST0000001","TEST0000001", new TestHttpServletRequest());
		assertEquals(true, result.hasErrors());
	}

	@Ignore
	@Test(expected = IdAuthenticationAppException.class)
	public void testConstraintVoilation() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		boolean hasError = true;
		otpRequestDto = getOtpRequestDTO();
		otpResponseDTO = getOtpResponseDTO();

		Set<ConstraintViolation<OtpRequestDTO>> violations = validator.validate(otpRequestDto);
		assertEquals(violations.size(), 1);
		Mockito.when(result.hasErrors()).thenReturn(hasError);
		otpController.generateOTP(otpRequestDto, result, "TEST0000001", "TEST0000001","TEST0000001", new TestHttpServletRequest());
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
		TestHttpServletRequest requestWithMetadata = new TestHttpServletRequest();
		Mockito.when(otpService.generateOtp(otpRequestDto, "TEST0000001", requestWithMetadata)).thenThrow(idAuthenticationBusinessException);
		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("Zld6TjJjNllKYzExNjBFUUZrbmdzYnJMelRJQ1BY");
		otpController.generateOTP(otpRequestDto, result, "TEST0000001", "TEST0000001","TEST0000001", requestWithMetadata);
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
		TestHttpServletRequest requestWithMetadata = new TestHttpServletRequest();
		Mockito.when(otpService.generateOtp(otpRequestDto, "TEST0000001", requestWithMetadata)).thenThrow(idAuthenticationBusinessException);
		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("Zld6TjJjNllKYzExNjBFUUZrbmdzYnJMelRJQ1BY");
		otpController.generateOTP(otpRequestDto, result, "TEST0000001", "TEST0000001","TEST0000001", requestWithMetadata);

	}

	@Test(expected = IdAuthenticationAppException.class)
	public void testGenerateOtpDataValidationException()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO.class, "OtpRequestDTO");
		errors.reject("errorCode");
		Mockito.when(authTransactionHelper.createDataValidationException(Mockito.any(), Mockito.any(), Mockito.any()))
			.thenReturn(new IdAuthenticationAppException());
		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("Zld6TjJjNllKYzExNjBFUUZrbmdzYnJMelRJQ1BY");
		otpController.generateOTP(new OtpRequestDTO(), errors, "TEST0000001", "TEST0000001","TEST0000001", new TestHttpServletRequest());
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
		otpRequestDTO.setRequestTime(new SimpleDateFormat(EnvUtil.getDateTimePattern()).format(new Date()));
		otpRequestDTO.setVersion("1.0");
		Errors errors = new BeanPropertyBindingResult(OtpRequestDTO.class, "OtpRequestDTO");
		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		List<AuthError> autherror=new ArrayList<>();
		otpResponseDTO.setErrors(autherror);
		otpResponseDTO.setResponseTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		Mockito.when(otpService.generateOtp(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(otpResponseDTO);
		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("Zld6TjJjNllKYzExNjBFUUZrbmdzYnJMelRJQ1BY");
		otpController.generateOTP(otpRequestDTO, errors, "121212", "232323","TEST0000001", new TestHttpServletRequest());
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
		otpResponseDTO.setResponseTime(new SimpleDateFormat(EnvUtil.getDateTimePattern()).format(new Date()));
		return otpResponseDTO;
	}
}