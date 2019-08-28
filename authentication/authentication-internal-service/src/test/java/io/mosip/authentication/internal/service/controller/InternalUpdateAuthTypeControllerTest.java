package io.mosip.authentication.internal.service.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.authtype.status.service.AuthTypeStatusDto;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.internal.service.impl.UpdateAuthtypeStatusServiceImpl;
import io.mosip.authentication.internal.service.validator.UpdateAuthtypeStatusValidator;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class InternalUpdateAuthTypeControllerTest {

	@InjectMocks
	private InternalUpdateAuthTypeController internalUpdateAuthTypeController;

	@InjectMocks
	private UpdateAuthtypeStatusValidator updateAuthtypeStatusValidator;

	@Autowired
	Environment environment;

	@Mock
	WebDataBinder binder;

	@Mock
	private UinValidatorImpl uinValidator;

	@Mock
	private VidValidatorImpl vidValidator;

	@Mock
	private IdService<AutnTxn> idService;

	@InjectMocks
	private UpdateAuthtypeStatusServiceImpl updateAuthtypeStatusService;

	@Before
	public void before() {
		ReflectionTestUtils.invokeMethod(internalUpdateAuthTypeController, "initBinder", binder);
		ReflectionTestUtils.setField(internalUpdateAuthTypeController, "environment", environment);
		ReflectionTestUtils.setField(internalUpdateAuthTypeController, "updateAuthtypeStatusService",
				updateAuthtypeStatusService);
	}

	@Test
	public void TestupdateAuthtypeStatus() throws IdAuthenticationAppException {
		AuthTypeStatusDto authTypeStatusDto = new AuthTypeStatusDto();
		Errors errors = new BeanPropertyBindingResult(authTypeStatusDto, "authTypeStatusDto");
		internalUpdateAuthTypeController.updateAuthtypeStatus(authTypeStatusDto, errors);
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void TestIdValidationException() throws IdAuthenticationAppException {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(false);
		AuthTypeStatusDto authTypeStatusDto = new AuthTypeStatusDto();
		Errors errors = new BeanPropertyBindingResult(authTypeStatusDto, "authTypeStatusDto");
		errors.rejectValue("id", "errorCode", "defaultMessage");
		internalUpdateAuthTypeController.updateAuthtypeStatus(authTypeStatusDto, errors);
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void TestIdBusinessException() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		AuthTypeStatusDto authTypeStatusDto = new AuthTypeStatusDto();
		Errors errors = new BeanPropertyBindingResult(authTypeStatusDto, "authTypeStatusDto");
		Mockito.when(idService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN));
		internalUpdateAuthTypeController.updateAuthtypeStatus(authTypeStatusDto, errors);

	}
}
