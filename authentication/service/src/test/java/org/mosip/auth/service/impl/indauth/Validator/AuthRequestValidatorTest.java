package org.mosip.auth.service.impl.indauth.Validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mosip.auth.core.dto.indauth.AuthRequestDTO;
import org.mosip.auth.core.dto.indauth.AuthTypeDTO;
import org.mosip.auth.core.dto.indauth.IdType;
import org.mosip.auth.core.dto.indauth.PinDTO;
import org.mosip.auth.core.dto.indauth.PinType;
import org.mosip.auth.service.factory.AuditRequestFactory;
import org.mosip.auth.service.factory.RestRequestFactory;
import org.mosip.auth.service.helper.RestHelper;
import org.mosip.auth.service.impl.idauth.service.impl.IdAuthServiceImpl;
import org.mosip.auth.service.impl.indauth.service.OTPAuthServiceImpl;
import org.mosip.auth.service.impl.indauth.validator.AuthRequestValidator;
import org.mosip.kernel.core.spi.logging.MosipLogger;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.context.WebApplicationContext;

/**
 * This class validates the AuthRequestValidator
 * @author Arun Bose
 */


@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes= {TestContext.class, WebApplicationContext.class})
@TestPropertySource(value = { "classpath:ValidationMessages.properties", "classpath:audit.properties", "classpath:rest-services.properties", "classpath:log.properties" })

public class AuthRequestValidatorTest {
	
	@Mock
	private SpringValidatorAdapter validator;

	@Mock
	RestHelper restHelper;
	
	@Mock
	Errors error;
	
	@Autowired
	Environment env;
	
	
	private MosipLogger logger;

	@InjectMocks
	MosipRollingFileAppender idaRollingFileAppender;

	@InjectMocks
	private RestRequestFactory  restFactory;
	
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
		/*ReflectionTestUtils.invokeMethod(restHelper, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.invokeMethod(auditFactory, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.invokeMethod(authRequestValidator, "initializeLogger", mosipRollingFileAppender);*/
		/*ReflectionTestUtils.setField(authRequestValidator, "auditFactory", auditFactory);
		ReflectionTestUtils.setField(authRequestValidator, "restFactory", restFactory);*/
	}

	/*
	 * 
	 * This method checks the otp parameters present and failed
	 * 
	 */
	@Test
	public void checkOTPAuthValidatepinDTOFail() {
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
	    
	    //error.rejectValue(IdAuthenticationErrorConstants.NO_PINTYPE.getErrorCode(),
				//env.getProperty("mosip.ida.validation.message.AuthRequest.PinType"));
	    authRequestValidator.checkOTPAuth(authRequestDTO, error);
	    
	}
	/*
	 * 
	 * This method checks the otp parameters present and checks for otp value and failed
	 */
  	
	@Test
	public void checkOTPAuthValidatepinValueFail() {
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		PinDTO pinDTO =new PinDTO();
		pinDTO.setType(PinType.OTP);
		authRequestDTO.setPinDTO(pinDTO);
		
	    //error.rejectValue(IdAuthenticationErrorConstants.NO_PINTYPE.getErrorCode(),
				//env.getProperty("mosip.ida.validation.message.AuthRequest.PinType"));
	    authRequestValidator.checkOTPAuth(authRequestDTO, error);
	    
	}
	
	
	
	
	/*
	 * 
	 * This method checks the otp parameters present and checks for otp value and failed
	 */
  	
	@Test
	public void checkOTPAuthValidatepinTypeFail() {
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		PinDTO pinDTO =new PinDTO();
		authRequestDTO.setPinDTO(pinDTO);
		
	    //error.rejectValue(IdAuthenticationErrorConstants.NO_PINTYPE.getErrorCode(),
				//env.getProperty("mosip.ida.validation.message.AuthRequest.PinType"));
	    authRequestValidator.checkOTPAuth(authRequestDTO, error);
	    
	}
	
	@Test
	  public void validateCheckIdTypeOtp() {
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		authRequestDTO.setIdType(IdType.UIN);
		AuthTypeDTO authType=new AuthTypeDTO();
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
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		authRequestDTO.setIdType(IdType.UIN);
		AuthTypeDTO authType=new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setAuthType(authType);
		authRequestValidator.validate(authRequestDTO, error);
	}
	
	@Test
	  public void validateCheckIdTypeNoAuth() {
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		authRequestDTO.setIdType(IdType.UIN);
		AuthTypeDTO authType=new AuthTypeDTO();
		authType.setBio(false);
		authType.setId(false);
		authType.setAd(false);
		authType.setPin(false);
		authType.setOtp(false);
		authRequestDTO.setAuthType(authType);
		authRequestValidator.validate(authRequestDTO, error);
		
	 }
	
  	
}
