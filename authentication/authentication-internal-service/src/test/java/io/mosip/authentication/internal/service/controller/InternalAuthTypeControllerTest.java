package io.mosip.authentication.internal.service.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.impl.AuthtypeStatusImpl;
import io.mosip.authentication.common.service.impl.IdServiceImpl;
import io.mosip.authentication.common.service.repository.AuthLockRepository;
import io.mosip.authentication.core.authtype.dto.AuthtypeResponseDto;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.internal.service.validator.AuthtypeStatusValidator;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class InternalAuthTypeControllerTest {

	@InjectMocks
	InternalRetrieveAuthTypeController authTypeController;

	@InjectMocks
	private AuthtypeStatusValidator authtypeStatusValidator;

	@InjectMocks
	private AuthtypeStatusImpl authtypeStatusImpl;

	@Mock
	WebDataBinder binder;

	@Autowired
	Environment environment;

	@Mock
	private UinValidatorImpl uinValidator;

	@Mock
	private VidValidatorImpl vidValidator;

	@Mock
	private IdServiceImpl idService;

	@Mock
	private AuthLockRepository authLockRepository;
	
	@Mock
	private AuditHelper auditHelper;
	
	@Before
	public void before() {
		ReflectionTestUtils.invokeMethod(authTypeController, "initBinder", binder);
		ReflectionTestUtils.setField(authTypeController, "authtypeStatusValidator", authtypeStatusValidator);
		ReflectionTestUtils.setField(authTypeController, "authtypeStatusService", authtypeStatusImpl);
		ReflectionTestUtils.setField(authTypeController, "environment", environment);
		ReflectionTestUtils.setField(authTypeController, "auditHelper", auditHelper);
		ReflectionTestUtils.setField(authtypeStatusValidator, "env", environment);
	}

	@Test
	public void TestgetValidAuthTypeStatus() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		Map<String, Object> value = new HashMap<>();
		value.put("uin", "9172985031");
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(value);

		List<Object[]> valuelist = new ArrayList<>();
		Object[] authtypeLockStatus = new Object[] {"bio-FMR", "y"};
		valuelist.add(authtypeLockStatus);
		
		Mockito.when(authLockRepository.findByUinHash(Mockito.anyString())).thenReturn(valuelist);
		ResponseEntity<AuthtypeResponseDto> authTypeStatus = authTypeController.getAuthTypeStatus(IdType.UIN.getType(),
				"9172985031");
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void TestIdValidationException() throws IDDataValidationException, IdAuthenticationAppException {
		authTypeController.getAuthTypeStatus(IdType.UIN.getType(), "");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestIdAuthenticationBusinessException()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		Map<String, Object> value = new HashMap<>();
		value.put("uin", "9172985031");
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean())).thenThrow(
				new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN.getErrorCode(),
						IdAuthenticationErrorConstants.INVALID_UIN.getErrorMessage()));

		List<Object[]> valuelist = new ArrayList<>();
		Object[] authtypeLockStatus = new Object[] {"bio-FMR", "y"};
		valuelist.add(authtypeLockStatus);
	
		
		Mockito.when(authLockRepository.findByUinHash(Mockito.anyString())).thenReturn(valuelist);
		try {
			authTypeController.getAuthTypeStatus(IdType.UIN.getType(), "9172985031");
		} catch (IdAuthenticationAppException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN, e);
		}

	}

}
