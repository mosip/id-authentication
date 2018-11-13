package io.mosip.authentication.service.impl.indauth.controller;

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
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.indauth.facade.AuthFacadeImpl;
import io.mosip.authentication.service.repository.UinRepository;

/**
 * This code tests the AuthController 
 * @author Arun Bose
 */

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes= {TestContext.class, WebApplicationContext.class})
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
	
	@Mock
	WebDataBinder binder;

	Errors error = new BindException(AuthRequestDTO.class, "authReqDTO");
	
	@Mock
	private UinRepository uinRepository;
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(auditFactory, "env", env);
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.invokeMethod(authController, "initAuthRequestBinder", binder);
	}
	
	/*
	 * 
	 * Errors in the AuthRequestValidator is handled here and exception is thrown
	 */
	@Test(expected=IdAuthenticationAppException.class)
	public void showRequestValidator() throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authReqDTO=new AuthRequestDTO();
		Errors error = new BindException(authReqDTO, "authReqDTO");
		error.rejectValue("id", "errorCode", "defaultMessage");
		authController.authenticateApplication(authReqDTO, error);
		
	}
	
	@Test(expected=IdAuthenticationAppException.class)
	public void authenticationFailed() throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authReqDTO=new AuthRequestDTO();
		Mockito.when(authFacade.authenticateApplicant(authReqDTO)).thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UIN_DEACTIVATED));
		authController.authenticateApplication(authReqDTO, error);
		
	}
  
	@Test
	public void authenticationSuccess() throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authReqDTO=new AuthRequestDTO();
		Mockito.when(authFacade.authenticateApplicant(authReqDTO)).thenReturn(new AuthResponseDTO());
		authController.authenticateApplication(authReqDTO, error);
		
	}


}
