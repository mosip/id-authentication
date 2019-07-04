package io.mosip.authentication.service.staticpin.controller;

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

import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.staticpin.service.StaticPinService;
import io.mosip.authentication.core.staticpin.dto.StaticPinRequestDTO;
import io.mosip.authentication.core.staticpin.dto.StaticPinResponseDTO;
import io.mosip.authentication.staticpin.service.controller.StaticPinController;
import io.mosip.authentication.staticpin.service.validator.StaticPinRequestValidator;

/**
 * 
 * This Test Class tests the StaticPinController class.
 * 
 * @author Prem Kumar
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class StaticPinControllerTest {

	/** The Static Pin Facade */
	@Mock
	private StaticPinService staticPinService;

	/** The Static Pin Request Validator */
	@InjectMocks
	private StaticPinRequestValidator staticPinRequestValidator;

	/** The Static Pin Controller */
	@InjectMocks
	private StaticPinController staticPinController;

	/** The WebDataBinder */
	@Mock
	WebDataBinder binder;

	@Mock
	AuditHelper auditHelper;

	@Autowired
	private Environment environment;

	Errors error = new BindException(StaticPinRequestDTO.class, "staticPinRequestDTO");

	@Before
	public void before() {
		ReflectionTestUtils.setField(staticPinController, "staticPinService", staticPinService);
		ReflectionTestUtils.invokeMethod(staticPinController, "initBinder", binder);
		ReflectionTestUtils.setField(staticPinController, "env", environment);
	}

	/*
	 * 
	 * Errors in the StaticPinRequestValidator is handled here and exception is
	 * thrown
	 */
	@Test(expected = IdAuthenticationAppException.class)
	public void showStaticPinRequestValidator() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		StaticPinRequestDTO dto = new StaticPinRequestDTO();
		Errors error = new BindException(dto, "staticPinRequestDTO");
		error.rejectValue("id", "errorCode", "testErrorMessage");
		staticPinController.storeSpin(dto, error);

	}

	@Test
	public void testController_Succes() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		StaticPinRequestDTO dto = new StaticPinRequestDTO();
		Mockito.when(staticPinService.storeSpin(dto)).thenReturn(new StaticPinResponseDTO());
		staticPinController.storeSpin(dto, error);
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void testController_Failure_DataValidation()
			throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		StaticPinRequestDTO dto = new StaticPinRequestDTO();
		Mockito.when(staticPinService.storeSpin(dto))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PIN_NOT_STORED));
		staticPinController.storeSpin(dto, error);
	}
}
