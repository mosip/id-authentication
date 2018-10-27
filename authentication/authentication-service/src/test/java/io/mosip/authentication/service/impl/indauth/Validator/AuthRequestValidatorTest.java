package io.mosip.authentication.service.impl.indauth.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.PersonalIdentityDataDTO;
import io.mosip.authentication.core.dto.indauth.PinDTO;
import io.mosip.authentication.core.dto.indauth.PinType;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.idauth.service.impl.IdAuthServiceImpl;
import io.mosip.authentication.service.impl.indauth.service.OTPAuthServiceImpl;
import io.mosip.authentication.service.impl.indauth.validator.AuthRequestValidator;
import io.mosip.kernel.idvalidator.exception.MosipInvalidIDException;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
import io.mosip.kernel.logger.logback.appender.MosipRollingFileAppender;

/**
 * This class validates the AuthRequestValidator
 * 
 * @author Arun Bose
 */

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class AuthRequestValidatorTest {

	@Mock
	private SpringValidatorAdapter validator;

	@Mock
	RestHelper restHelper;

	@Mock
	Errors error;

	@Autowired
	Environment env;
	
	@Mock
	private UinValidatorImpl uinValidator;
	
	@Mock
	private VidValidatorImpl vidValidator;

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
		ReflectionTestUtils.setField(auditFactory, "env", env);
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.setField(authRequestValidator, "env", env);
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
		authRequestDTO.setPii(new PersonalIdentityDataDTO());
		authRequestDTO.getPii().setPin(pinDTO);

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
		authRequestDTO.setPii(new PersonalIdentityDataDTO());
		authRequestDTO.getPii().setPin(pinDTO);

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
		authType.setPi(false);
		authType.setAd(false);
		authType.setPin(false);
		authType.setOtp(true);
		authRequestDTO.setAuthType(authType);
		authRequestDTO.setReqTime(new Date().toString());
		authRequestValidator.validate(authRequestDTO, error);
	}

	@Test
	public void validateCheckIdTypeRemAuth() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIdType(IdType.UIN.getType());
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setAuthType(authType);
		authRequestDTO.setReqTime(new Date().toString());
		authRequestValidator.validate(authRequestDTO, error);
	}

	@Test
	public void validateCheckIdTypeNoAuth() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIdType(IdType.UIN.getType());
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authType.setPi(false);
		authType.setAd(false);
		authType.setPin(false);
		authType.setOtp(false);
		authRequestDTO.setAuthType(authType);
		authRequestDTO.setReqTime(new Date().toString());
		authRequestValidator.validate(authRequestDTO, error);

	}

	@Test
	public void testValidUin() throws NoSuchMethodException, SecurityException {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdType(IdType.UIN.getType());
		authRequestDTO.setId("426789089018");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authType.setPi(false);
		authType.setAd(false);
		authType.setPin(false);
		authType.setOtp(true);
		authRequestDTO.setAuthType(authType);
		PinDTO pinDTO = new PinDTO();
		pinDTO.setType(PinType.OTP);
		pinDTO.setValue("123456");
		authRequestDTO.setPii(new PersonalIdentityDataDTO());
		authRequestDTO.getPii().setPin(pinDTO);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		authRequestDTO.setReqTime(format.format(new Date()));
		authRequestValidator.validate(authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInvalidUin() {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenThrow(new MosipInvalidIDException("code", "msg"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdType(IdType.UIN.getType());
		authRequestDTO.setId("2345678901231");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authType.setPi(false);
		authType.setAd(false);
		authType.setPin(false);
		authType.setOtp(true);
		authRequestDTO.setAuthType(authType);
		PinDTO pinDTO = new PinDTO();
		pinDTO.setType(PinType.OTP);
		pinDTO.setValue("123456");
		authRequestDTO.setReqTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()));
		authRequestDTO.setPii(new PersonalIdentityDataDTO());
		authRequestDTO.getPii().setPin(pinDTO);
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void testValidVid() {
		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenReturn(true);
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdType(IdType.VID.getType());
		authRequestDTO.setId("53718436135982061");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authType.setPi(false);
		authType.setAd(false);
		authType.setPin(false);
		authType.setOtp(true);
		authRequestDTO.setAuthType(authType);
		PinDTO pinDTO = new PinDTO();
		pinDTO.setType(PinType.OTP);
		pinDTO.setValue("123456");
		authRequestDTO.setPii(new PersonalIdentityDataDTO());
		authRequestDTO.getPii().setPin(pinDTO);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		authRequestDTO.setReqTime(format.format(new Date()));
		authRequestValidator.validate(authRequestDTO, errors);
		assertFalse(errors.hasErrors());
	}

	@Test
	public void testInvalidVid() {
		Mockito.when(vidValidator.validateId(Mockito.anyString())).thenThrow(new MosipInvalidIDException("code", "msg"));
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		authRequestDTO.setIdType(IdType.VID.getType());
		authRequestDTO.setId("5371843613598211");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authType.setPi(false);
		authType.setAd(false);
		authType.setPin(false);
		authType.setOtp(true);
		authRequestDTO.setAuthType(authType);
		PinDTO pinDTO = new PinDTO();
		pinDTO.setType(PinType.OTP);
		pinDTO.setValue("123456");
		authRequestDTO.setReqTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()));
		authRequestDTO.setPii(new PersonalIdentityDataDTO());
		authRequestDTO.getPii().setPin(pinDTO);
		authRequestDTO.setReqTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()));
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
		authType.setPi(false);
		authType.setAd(false);
		authType.setPin(false);
		authType.setOtp(true);
		authRequestDTO.setAuthType(authType);
		PinDTO pinDTO = new PinDTO();
		pinDTO.setType(PinType.OTP);
		pinDTO.setValue("123456");
		authRequestDTO.setReqTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()));
		authRequestDTO.setPii(new PersonalIdentityDataDTO());
		authRequestDTO.getPii().setPin(pinDTO);
		authRequestDTO.setReqTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()));
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
		authType.setPi(false);
		authType.setAd(false);
		authType.setPin(false);
		authType.setOtp(true);
		authRequestDTO.setAuthType(authType);
		PinDTO pinDTO = new PinDTO();
		pinDTO.setType(PinType.OTP);
		pinDTO.setValue("12345");
		authRequestDTO.setReqTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()));
		authRequestDTO.setPii(new PersonalIdentityDataDTO());
		authRequestDTO.getPii().setPin(pinDTO);
		authRequestDTO.setReqTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()));
		authRequestValidator.validate(authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void checkAuthRequestTest() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setAd(false);
		authType.setBio(false);
		authType.setFad(false);
		authType.setOtp(false);
		authType.setPi(false);
		authType.setPin(false);
		authRequestDTO.setAuthType(authType);
		Method checkAuthReq = authRequestValidator.getClass().getDeclaredMethod("checkAuthRequest", AuthRequestDTO.class, Errors.class);
		checkAuthReq.setAccessible(true);
		checkAuthReq.invoke(authRequestValidator, authRequestDTO, errors);
		assertTrue(errors.hasErrors());
	}

}
