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

import io.mosip.authentication.common.factory.AuditRequestFactory;
import io.mosip.authentication.common.factory.RestRequestFactory;
import io.mosip.authentication.common.helper.RestHelper;
import io.mosip.authentication.common.service.impl.indauth.facade.AuthFacadeImpl;
import io.mosip.authentication.common.service.impl.indauth.validator.AuthRequestValidator;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.KycAuthRequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;

/**
 * This code tests the AuthController
 * 
 * @author Arun Bose
 * 
 * @author Prem Kumar
 */

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class AuthControllerTest {

	@Mock
	private RestHelper restHelper;

	@Autowired
	Environment env;

	@InjectMocks
	private RestRequestFactory restFactory;

	@InjectMocks
	private AuditRequestFactory auditFactory;

	@Mock
	private AuthFacadeImpl authFacade;

	@InjectMocks
	private AuthController authController;

	@Mock
	WebDataBinder binder;

	@InjectMocks
	private AuthRequestValidator authRequestValidator;

	Errors error = new BindException(AuthRequestDTO.class, "authReqDTO");
	Errors errors = new BindException(KycAuthRequestDTO.class, "kycAuthReqDTO");

//	/** The Kyc Service */
//	@Mock
//	private KycServiceImpl kycService;

	@Before
	public void before() {
		ReflectionTestUtils.setField(auditFactory, "env", env);
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.invokeMethod(authController, "initAuthRequestBinder", binder);
		ReflectionTestUtils.setField(authController, "authFacade", authFacade);
		ReflectionTestUtils.setField(authFacade, "env", env);
	}

	/*
	 * 
	 * Errors in the AuthRequestValidator is handled here and exception is thrown
	 */
	@Test(expected = IdAuthenticationAppException.class)
	public void showRequestValidator()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authReqDTO = new AuthRequestDTO();
		Errors error = new BindException(authReqDTO, "authReqDTO");
		error.rejectValue("id", "errorCode", "defaultMessage");
		authController.authenticateApplication(authReqDTO, error, "123456", "123456");

	}

	@Test(expected = IdAuthenticationAppException.class)
	public void authenticationFailed()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authReqDTO = new AuthRequestDTO();
		Mockito.when(authFacade.authenticateApplicant(authReqDTO, true, "123456"))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UIN_DEACTIVATED));
		authController.authenticateApplication(authReqDTO, error, "123456", "123456");

	}

	@Test
	public void authenticationSuccess()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException, IdAuthenticationDaoException {
		AuthRequestDTO authReqDTO = new AuthRequestDTO();
		Mockito.when(authFacade.authenticateApplicant(authReqDTO, true, "123456")).thenReturn(new AuthResponseDTO());
		authController.authenticateApplication(authReqDTO, error, "123456", "123456");

	}

}
