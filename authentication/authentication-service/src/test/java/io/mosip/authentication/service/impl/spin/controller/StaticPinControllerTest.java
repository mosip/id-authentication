package io.mosip.authentication.service.impl.spin.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.spin.facade.StaticPinFacade;
import io.mosip.authentication.service.impl.spin.validator.StaticPinRequestValidator;
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
	private StaticPinFacade staticPinFacade;
	
	/** The Static Pin Request Validator  */
	@InjectMocks
	private StaticPinRequestValidator staticPinRequestValidator;
	
	/** The Static Pin Controller */ 
	@InjectMocks
	private StaticPinController staticPinController;
	
	/** The WebDataBinder */
	@Mock
	WebDataBinder binder;
	
	Errors error = new BindException(StaticPinRequestDTO.class, "staticPinRequestDTO");
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(staticPinController, "staticPinFacade", staticPinFacade);
		ReflectionTestUtils.invokeMethod(staticPinController, "initBinder", binder);
	}
	/*
	 * 
	 * Errors in the StaticPinRequestValidator is handled here and exception is thrown
	 */
	@Test(expected = IdAuthenticationAppException.class)
	public void showStaticPinRequestValidator()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException{
		StaticPinRequestDTO dto = new StaticPinRequestDTO();
		Errors error = new BindException(dto, "staticPinRequestDTO");
		error.rejectValue("id", "errorCode", "testErrorMessage");
		staticPinController.storeSpin(dto, error);

	}
	@Test
	public void testController_Succes() throws  IdAuthenticationAppException, IdAuthenticationBusinessException{
		StaticPinRequestDTO dto=new StaticPinRequestDTO();
		Mockito.when(staticPinFacade.storeSpin(dto)).thenReturn(new StaticPinResponseDTO());
		staticPinController.storeSpin(dto, error);
	}
	
	@Test(expected = IdAuthenticationAppException.class)
	public void testController_Failure_DataValidation() throws IdAuthenticationBusinessException, IdAuthenticationAppException
	{
		StaticPinRequestDTO dto=new StaticPinRequestDTO();
		Mockito.when(staticPinFacade.storeSpin(dto)).thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.STATICPIN_NOT_STORED_PINVAUE));
		staticPinController.storeSpin(dto, error);
	}
}
