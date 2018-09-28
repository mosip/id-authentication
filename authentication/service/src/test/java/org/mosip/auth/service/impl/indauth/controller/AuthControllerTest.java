package org.mosip.auth.service.impl.indauth.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.dto.indauth.AuthRequestDTO;
import org.mosip.auth.core.dto.indauth.AuthResponseDTO;
import org.mosip.auth.core.exception.IdAuthenticationAppException;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.service.dao.UinRepository;
import org.mosip.auth.service.factory.AuditRequestFactory;
import org.mosip.auth.service.factory.RestRequestFactory;
import org.mosip.auth.service.helper.RestHelper;
import org.mosip.auth.service.impl.indauth.facade.AuthFacadeImpl;
import org.mosip.kernel.logger.appenders.MosipRollingFileAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;

/**
 * This code tests the AuthController 
 * @author Arun Bose
 */

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes= {TestContext.class, WebApplicationContext.class})
@TestPropertySource(value = { "classpath:audit.properties", "classpath:rest-services.properties", "classpath:log.properties" })
public class AuthControllerTest {
	
	@Mock
	RestHelper restHelper;
	
	@Autowired
	Environment env;
	
	@InjectMocks
	private RestRequestFactory  restFactory;
	
	@InjectMocks
	private AuditRequestFactory auditFactory;
	
	@Mock
	private AuthFacadeImpl authFacade;

	@InjectMocks
	private AuthController authController;
	


	Errors error = new BindException(AuthRequestDTO.class, "authReqDTO");
	
	@Mock
	private UinRepository uinRepository;
	
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
		//ReflectionTestUtils.setField(authController, "env", env);
		ReflectionTestUtils.setField(auditFactory, "env", env);
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.invokeMethod(restHelper, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.invokeMethod(auditFactory, "initializeLogger", mosipRollingFileAppender);
		ReflectionTestUtils.invokeMethod(authController, "initializeLogger", mosipRollingFileAppender);
	}
	
	/*
	 * 
	 * Errors in the AuthRequestValidator is handled here and exception is thrown
	 */
	@Test(expected=IdAuthenticationAppException.class)
	public void showRequestValidator() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		AuthRequestDTO authReqDTO=new AuthRequestDTO();
		Errors error = new BindException(authReqDTO, "authReqDTO");
		error.rejectValue("id", "errorCode", "defaultMessage");
		authController.authenticateApplication(authReqDTO, error);
		
	}
	
	@Test(expected=IdAuthenticationAppException.class)
	public void authenticationFailed() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		AuthRequestDTO authReqDTO=new AuthRequestDTO();
		Mockito.when(authFacade.authenticateApplicant(authReqDTO)).thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INACTIVE_UIN));
		authController.authenticateApplication(authReqDTO, error);
		
	}
  
	@Test
	public void authenticationSuccess() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		AuthRequestDTO authReqDTO=new AuthRequestDTO();
		Mockito.when(authFacade.authenticateApplicant(authReqDTO)).thenReturn(new AuthResponseDTO());
		authController.authenticateApplication(authReqDTO, error);
		
	}


}
