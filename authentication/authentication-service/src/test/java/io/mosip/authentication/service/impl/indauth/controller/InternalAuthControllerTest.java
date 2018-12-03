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
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.service.factory.AuditRequestFactory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.DateHelper;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.indauth.facade.AuthFacadeImpl;
import io.mosip.authentication.service.impl.indauth.validator.InternalAuthRequestValidator;
import io.mosip.authentication.service.repository.UinRepository;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class InternalAuthControllerTest {

	@InjectMocks
	InternalAuthController authController;

	@Mock
	WebDataBinder binder;

	@Mock
	AuthFacadeImpl authfacade;

	@Mock
	private KycService kycService;

	@Mock
	private RestHelper restHelper;

	@Autowired
	Environment env;

	@InjectMocks
	private RestRequestFactory restFactory;

	@InjectMocks
	private AuditRequestFactory auditFactory;
	
	@Mock
	private UinRepository uinRepository;
	
	@InjectMocks
	private InternalAuthRequestValidator internalAuthRequestValidator;

	@InjectMocks
	private DateHelper datehelper;
	
	Errors error = new BindException(AuthRequestDTO.class, "authReqDTO");

	@Before
	public void before() {
		ReflectionTestUtils.setField(auditFactory, "env", env);
		ReflectionTestUtils.setField(restFactory, "env", env);
		ReflectionTestUtils.invokeMethod(authController, "initBinder", binder);
		ReflectionTestUtils.setField(authController, "authFacade", authfacade);
		ReflectionTestUtils.setField(authfacade, "env", env);
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void showAuthenticateTspValidator()
			throws IdAuthenticationAppException, IdAuthenticationDaoException, IdAuthenticationBusinessException {

		AuthRequestDTO authReqestsDTO = new AuthRequestDTO();
		Errors error = new BindException(authReqestsDTO, "authReqDTO");
		error.rejectValue("id", "errorCode", "defaultMessage");
		authController.authenticateTsp(authReqestsDTO, error);
	}

	@Test
	public void auhtenticationTspSuccess()
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IdAuthenticationAppException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		Mockito.when(authfacade.authenticateApplicant(authReqestDTO, false)).thenReturn(new AuthResponseDTO());
		Errors error = new BindException(authReqestDTO, "authReqDTO");
		authController.authenticateTsp(authReqestDTO, error);
	}

	@Test
	public void auhtenticationTspvalid()
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IdAuthenticationAppException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		Mockito.when(authfacade.authenticateApplicant(authReqestDTO, false)).thenReturn(new AuthResponseDTO());
		authController.authenticateTsp(authReqestDTO, error);
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void TestAuthIdException()
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException, IdAuthenticationAppException {
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		Mockito.when(authfacade.authenticateApplicant(authReqestDTO, false))
				.thenThrow(new IDDataValidationException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED));
		authController.authenticateTsp(authReqestDTO, error);
	}
	
	@Test(expected = IdAuthenticationAppException.class)
	public void TestAuthIdException2()
		throws IdAuthenticationBusinessException, IdAuthenticationAppException, IdAuthenticationDaoException{
		AuthRequestDTO authReqestDTO = new AuthRequestDTO();
		Mockito.when(authfacade.authenticateApplicant(authReqestDTO, false))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.AUTHENTICATION_FAILED));
		authController.authenticateTsp(authReqestDTO, error);
	}

}
