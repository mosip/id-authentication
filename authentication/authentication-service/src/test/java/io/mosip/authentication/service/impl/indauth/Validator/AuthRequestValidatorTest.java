package io.mosip.authentication.service.impl.indauth.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.PinDTO;
import io.mosip.authentication.core.dto.indauth.PinType;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.idauth.service.impl.IdAuthServiceImpl;
import io.mosip.authentication.service.impl.indauth.service.OTPAuthServiceImpl;
import io.mosip.authentication.service.impl.indauth.validator.AuthRequestValidator;
import io.mosip.kernel.logger.appender.MosipRollingFileAppender;

/**
 * This class validates the AuthRequestValidator
 * 
 * @author Arun Bose
 */

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@TestPropertySource(value = { "classpath:ValidationMessages.properties", "classpath:audit.properties",
		"classpath:rest-services.properties", "classpath:log.properties" })

public class AuthRequestValidatorTest {

	@Mock
	private SpringValidatorAdapter validator;

	@Mock
	RestHelper restHelper;

	@Mock
	Errors error;

	@Autowired
	Environment env;

	@InjectMocks
	MosipRollingFileAppender idaRollingFileAppender;

	@InjectMocks
	private RestRequestFactory restFactory;

	@InjectMocks
	private AuditRequestFactory auditFactory;

	@InjectMocks
	private AuthRequestValidator authRequestValidator;

	@Mock
	private IdAuthServiceImpl idAuthServiceImpl;

	@Mock
	private OTPAuthServiceImpl otpAuthServiceImpl;

	@Before
	public void before() {
		MosipRollingFileAppender mosipRollingFileAppender = new MosipRollingFileAppender();
		mosipRollingFileAppender.setAppenderName(env.getProperty("log4j.appender.Appender"));
		mosipRollingFileAppender.setFileName(env.getProperty("log4j.appender.Appender.file"));
		mosipRollingFileAppender.setFileNamePattern(env.getProperty("log4j.appender.Appender.filePattern"));
		mosipRollingFileAppender.setMaxFileSize(env.getProperty("log4j.appender.Appender.maxFileSize"));
		mosipRollingFileAppender.setTotalCap(env.getProperty("log4j.appender.Appender.totalCap"));
		mosipRollingFileAppender.setMaxHistory(10);
		mosipRollingFileAppender.setImmediateFlush(true);
		mosipRollingFileAppender.setPrudent(true);
		ReflectionTestUtils.setField(auditFactory, "env", env);
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.setField(authRequestValidator, "env", env);
		ReflectionTestUtils.invokeMethod(authRequestValidator, "initializeLogger", mosipRollingFileAppender);
		/*
		 * ReflectionTestUtils.invokeMethod(restHelper, "initializeLogger",
		 * mosipRollingFileAppender); ReflectionTestUtils.invokeMethod(auditFactory,
		 * "initializeLogger", mosipRollingFileAppender);
		 * ReflectionTestUtils.invokeMethod(authRequestValidator, "initializeLogger",
		 * mosipRollingFileAppender);
		 */
		/*
		 * ReflectionTestUtils.setField(authRequestValidator, "auditFactory",
		 * auditFactory); ReflectionTestUtils.setField(authRequestValidator,
		 * "restFactory", restFactory);
		 */
	}
	
	@Test
	public void testSupportTrue() {
		assertTrue(authRequestValidator.supports(AuthRequestDTO.class));
	}
	
	@Test
	public void testSupportFalse() {
		assertFalse(authRequestValidator.supports(AuthRequestValidator.class));
	}

	/*
	 * 
	 * This method checks the otp parameters present and failed
	 * 
	 */
	@Test
	public void checkOTPAuthValidatepinDTOFail() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();

		// error.rejectValue(IdAuthenticationErrorConstants.NO_PINTYPE.getErrorCode(),
		// env.getProperty("mosip.ida.validation.message.AuthRequest.PinType"));
		authRequestValidator.checkOTPAuth(authRequestDTO, error);

	}
	/*
	 * 
	 * This method checks the otp parameters present and checks for otp value and
	 * failed
	 */

	@Test
	public void checkOTPAuthValidatepinValueFail() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		PinDTO pinDTO = new PinDTO();
		pinDTO.setType(PinType.OTP);
		authRequestDTO.setPinDTO(pinDTO);

		// error.rejectValue(IdAuthenticationErrorConstants.NO_PINTYPE.getErrorCode(),
		// env.getProperty("mosip.ida.validation.message.AuthRequest.PinType"));
		authRequestValidator.checkOTPAuth(authRequestDTO, error);

	}

	/*
	 * 
	 * This method checks the otp parameters present and checks for otp value and
	 * failed
	 */

	@Test
	public void checkOTPAuthValidatepinTypeFail() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		PinDTO pinDTO = new PinDTO();
		authRequestDTO.setPinDTO(pinDTO);

		// error.rejectValue(IdAuthenticationErrorConstants.NO_PINTYPE.getErrorCode(),
		// env.getProperty("mosip.ida.validation.message.AuthRequest.PinType"));
		authRequestValidator.checkOTPAuth(authRequestDTO, error);

	}

	@Test
	public void validateCheckIdTypeOtp() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIdType(IdType.UIN.getType());
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authType.setId(false);
		authType.setAd(false);
		authType.setPin(false);
		authType.setOtp(true);
		authRequestDTO.setAuthType(authType);
		authRequestValidator.validate(authRequestDTO, error);
	}

	@Test
	public void validateCheckIdTypeRemAuth() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIdType(IdType.UIN.getType());
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setAuthType(authType);
		authRequestValidator.validate(authRequestDTO, error);
	}

	@Test
	public void validateCheckIdTypeNoAuth() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIdType(IdType.UIN.getType());
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authType.setId(false);
		authType.setAd(false);
		authType.setPin(false);
		authType.setOtp(false);
		authRequestDTO.setAuthType(authType);
		authRequestValidator.validate(authRequestDTO, error);

	}

	@Test
	public void testValidUin() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdType(IdType.UIN.getType());
		authRequestDTO.setId("234567890124");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authType.setId(false);
		authType.setAd(false);
		authType.setPin(false);
		authType.setOtp(true);
		authRequestDTO.setAuthType(authType);
		PinDTO pinDTO = new PinDTO();
		pinDTO.setType(PinType.OTP);
		pinDTO.setValue("123456");
		authRequestDTO.setPinDTO(pinDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInvalidUin() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdType(IdType.UIN.getType());
		authRequestDTO.setId("234567890123");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authType.setId(false);
		authType.setAd(false);
		authType.setPin(false);
		authType.setOtp(true);
		authRequestDTO.setAuthType(authType);
		PinDTO pinDTO = new PinDTO();
		pinDTO.setType(PinType.OTP);
		pinDTO.setValue("123456");
		authRequestDTO.setPinDTO(pinDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidVid() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdType(IdType.VID.getType());
		authRequestDTO.setId("5371843613598206");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authType.setId(false);
		authType.setAd(false);
		authType.setPin(false);
		authType.setOtp(true);
		authRequestDTO.setAuthType(authType);
		PinDTO pinDTO = new PinDTO();
		pinDTO.setType(PinType.OTP);
		pinDTO.setValue("123456");
		authRequestDTO.setPinDTO(pinDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInvalidVid() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdType(IdType.VID.getType());
		authRequestDTO.setId("5371843613598211");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authType.setId(false);
		authType.setAd(false);
		authType.setPin(false);
		authType.setOtp(true);
		authRequestDTO.setAuthType(authType);
		PinDTO pinDTO = new PinDTO();
		pinDTO.setType(PinType.OTP);
		pinDTO.setValue("123456");
		authRequestDTO.setPinDTO(pinDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}
	
	@Test
	public void testInvalidIdType() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdType("abcd");
		authRequestDTO.setId("5371843613598211");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authType.setId(false);
		authType.setAd(false);
		authType.setPin(false);
		authType.setOtp(true);
		authRequestDTO.setAuthType(authType);
		PinDTO pinDTO = new PinDTO();
		pinDTO.setType(PinType.OTP);
		pinDTO.setValue("123456");
		authRequestDTO.setPinDTO(pinDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}
	
	@Test
	public void testInvalidOTPLength() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdType("abcd");
		authRequestDTO.setId("5371843613598211");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authType.setId(false);
		authType.setAd(false);
		authType.setPin(false);
		authType.setOtp(true);
		authRequestDTO.setAuthType(authType);
		PinDTO pinDTO = new PinDTO();
		pinDTO.setType(PinType.OTP);
		pinDTO.setValue("12345");
		authRequestDTO.setPinDTO(pinDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

}
