package io.mosip.authentication.common.service.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.validator.IdAuthValidator;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;

/**
 * The Class IdAuthValidatorTest.
 * 
 * @author Manoj SP
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class IdAuthValidatorTest {


	private static final String INDIVIDUAL_ID_TYPE = "individualIdType";

	private static final String INDIVIDUAL_ID = "individualId";

	private static final String REQUEST_TIME = "requestTime";

	private static final String TRANSACTION_ID = "transactionID";

	/** The uin validator. */
	@Mock
	UinValidatorImpl uinValidator;

	/** The vid validator. */
	@Mock
	VidValidatorImpl vidValidator;

	@Autowired
	Environment env;

	/** The validator. */
	IdAuthValidator validator = new IdAuthValidator() {

		@Override
		public void validate(Object target, Errors errors) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean supports(Class<?> clazz) {
			// TODO Auto-generated method stub
			return false;
		}
	};

	/** The errors. */
	Errors errors;

	/** The request. */
	OtpRequestDTO request = new OtpRequestDTO();

	AuthRequestDTO authReq = new AuthRequestDTO();

	/**
	 * Setup.
	 */
	@Before
	public void setup() {
		ReflectionTestUtils.setField(validator, "env", env);
		errors = new BeanPropertyBindingResult(request, "IdAuthValidator");
		ReflectionTestUtils.setField(validator, "uinValidator", uinValidator);
		ReflectionTestUtils.setField(validator, "vidValidator", vidValidator);
	}

	/**
	 * Test null id.
	 */
	@Test
	public void testNullId() {
		validator.validateId(null, errors);
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
					error.getDefaultMessage());
		});
	}

	/**
	 * Test null idv id.
	 */
	@Test
	public void testNullIdvId() {
		validator.validateIdvId(null, "UIN", errors, INDIVIDUAL_ID);
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
					error.getDefaultMessage());
			assertEquals(INDIVIDUAL_ID, ((FieldError) error).getField());
		});
	}

	/**
	 * Test null id type.
	 */
	@Test
	public void testNullIdType() {
		validator.validateIdvId("1234", null, errors, INDIVIDUAL_ID_TYPE);
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
					error.getDefaultMessage());
			assertEquals(INDIVIDUAL_ID_TYPE, ((FieldError) error).getField());
		});
	}

	/**
	 * Test incorrect id type.
	 */
	@Test
	public void testIncorrectIdType() {
		validator.validateIdvId("1234", "e", errors, INDIVIDUAL_ID);
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
					error.getDefaultMessage());
			assertEquals(INDIVIDUAL_ID_TYPE, ((FieldError) error).getField());
		});
	}

	@Test
	public void testNotConfiguredIdType() {
		MockEnvironment mockEnv = new MockEnvironment();
		mockEnv.merge((ConfigurableEnvironment) env);
		mockEnv.setProperty("request.idtypes.allowed", "UIN");
		ReflectionTestUtils.setField(validator, "env", mockEnv);
		validator.validateIdvId("1234", "VID", errors, INDIVIDUAL_ID);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-015")));
	}

	
	/**
	 * Test invalid UIN.
	 */
	@Test
	public void testInvalidUIN() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("", ""));
		validator.validateIdvId("1234", "UIN", errors, INDIVIDUAL_ID);
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdAuthenticationErrorConstants.INVALID_UIN.getErrorCode(), error.getCode());
			assertEquals(IdAuthenticationErrorConstants.INVALID_UIN.getErrorMessage(), error.getDefaultMessage());
			assertEquals(INDIVIDUAL_ID, ((FieldError) error).getField());
		});
	}

	/**
	 * Test invalid VID.
	 */
	@Test
	public void testInvalidVID() {
		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenThrow(new InvalidIDException("", ""));
		validator.validateIdvId("1234", "VID", errors, INDIVIDUAL_ID);
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdAuthenticationErrorConstants.INVALID_VID.getErrorCode(), error.getCode());
			assertEquals(IdAuthenticationErrorConstants.INVALID_VID.getErrorMessage(), error.getDefaultMessage());
			assertEquals(INDIVIDUAL_ID, ((FieldError) error).getField());
		});
	}

	/**
	 * Test null txn id.
	 */
	@Test
	public void testNullTxnId() {
		validator.validateTxnId(null, errors, TRANSACTION_ID);
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
					error.getDefaultMessage());
			assertEquals(TRANSACTION_ID, ((FieldError) error).getField());
		});
	}

	/**
	 * Test invalid txn id.
	 */
	@Test
	public void testInvalidTxnId() {
		validator.validateTxnId("1234", errors, TRANSACTION_ID);
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(),
					error.getDefaultMessage());
			assertEquals(TRANSACTION_ID, ((FieldError) error).getField());
		});
	}

	/**
	 * Test null req time.
	 */
	@Test
	public void testNullReqTime() {
		validator.validateReqTime(null, errors, REQUEST_TIME);
		errors.getAllErrors().forEach(error -> {
			assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorCode(), error.getCode());
			assertEquals(IdAuthenticationErrorConstants.MISSING_INPUT_PARAMETER.getErrorMessage(),
					error.getDefaultMessage());
			assertEquals(REQUEST_TIME, ((FieldError) error).getField());
		});
	}

	/**
	 * test ConsentRequired
	 */
	@Test
	public void testvalidateConsentReq_True() {

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setConsentObtained(true);
		Errors error = new BeanPropertyBindingResult(authReq, "IdAuthValidator");
		validator.validateConsentReq(authRequestDTO, error);
		assertFalse(error.hasErrors());
	}

	/**
	 * test ConsentRequired
	 */
	@Test
	public void testvalidateConsentReq_False() {

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setConsentObtained(false);
		Errors error = new BeanPropertyBindingResult(authReq, "IdAuthValidator");
		validator.validateConsentReq(authRequestDTO, error);
		assertTrue(error.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-012")));
	}

	/**
	 * test Future Time
	 */
	@Test
	public void testRequestTime_Invalid() {
		String reqTime = null;
		validator.validateReqTime(reqTime, errors, REQUEST_TIME);
		assertTrue(errors.hasErrors());
	}

	/**
	 * test Future Time
	 */
	@Test
	public void testRequestTime_Valid() {
		String reqTime = null;
		reqTime = Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN))).toString();
		validator.validateReqTime(reqTime, errors, REQUEST_TIME);
		assertFalse(errors.hasErrors());
	}

	/**
	 * test Future Time
	 */
	@Test
	public void testRequestTime_Invalid_TimeFormat() {
		String reqTime = null;
		reqTime = Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")).toString();
		validator.validateReqTime(reqTime, errors, REQUEST_TIME);
		//assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-001")));
		assertTrue(errors.hasErrors());
	}

	/**
	 * test Future Time
	 */
	@Test
	public void testFutureTime_Invalid() {
		validator.validateReqTime(Instant.now().plus(Period.ofDays(1)).toString(), errors, REQUEST_TIME);
		assertTrue(errors.getAllErrors().stream().anyMatch(err -> err.getCode().equals("IDA-MLC-001")));
	}
}
