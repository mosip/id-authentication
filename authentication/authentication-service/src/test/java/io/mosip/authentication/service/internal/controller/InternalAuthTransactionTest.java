package io.mosip.authentication.service.internal.controller;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.impl.AuthTxnServiceImpl;
import io.mosip.authentication.common.service.impl.IdServiceImpl;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.autntxn.dto.AutnTxnRequestDto;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.authentication.core.util.IdValidationUtil;
import io.mosip.authentication.service.internal.validator.AuthTxnValidator;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@Import(EnvUtil.class)
public class InternalAuthTransactionTest {

	@InjectMocks
	InternalAuthTxnController internalAuthTxnController;

	@Mock
	private AutnTxnRepository authtxnRepo;

	@InjectMocks
	private AuthTxnServiceImpl authTxnService;

	@InjectMocks
	private AuthTxnValidator authTxnValidator;

	@Autowired
	private EnvUtil environment;
	
	@Mock
	private IdAuthSecurityManager securityManager;

	@Mock
	WebDataBinder binder;

	@Mock
	private IdValidationUtil idValidator; 

	@Mock
	private IdServiceImpl idService;
	
	@Autowired
	EnvUtil env;

	@Mock
	private IdaUinHashSaltRepo uinHashSaltRepo;
	
	@Mock
	private AuditHelper auditHelper;
	
	@InjectMocks
	private IdTypeUtil idTypeUtil;

	@BeforeEach
	public void before() throws IdAuthenticationBusinessException {
		ReflectionTestUtils.invokeMethod(internalAuthTxnController, "initBinder", binder);
		ReflectionTestUtils.setField(internalAuthTxnController, "authTxnValidator", authTxnValidator);
		ReflectionTestUtils.setField(internalAuthTxnController, "authTxnService", authTxnService);
		ReflectionTestUtils.setField(internalAuthTxnController, "environment", environment);
		ReflectionTestUtils.setField(internalAuthTxnController, "auditHelper", auditHelper);
		ReflectionTestUtils.setField(authTxnService, "authtxnRepo", authtxnRepo);
		ReflectionTestUtils.setField(internalAuthTxnController, "idTypeUtil", idTypeUtil);
		when(securityManager.hash(Mockito.any())).thenReturn("1234");

	}

	@Test
	public void testSupportTrue() {
		assertTrue(authTxnValidator.supports(AutnTxnRequestDto.class));
	}

	@Test
	public void TestgetTxnDetails() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenReturn(true);
		Map<String, Object> value = new HashMap<>();
		value.put("uin", "9172985031");
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
				.thenReturn(value);
		List<AutnTxn> valueList = getAuthTxnList();
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("1234");
		Mockito.when(authtxnRepo.findByToken(Mockito.anyString(), Mockito.any())).thenReturn(valueList);
		internalAuthTxnController.getAuthTxnDetails(IdType.UIN.getType(), "9172985031", 1, 10);
	}

	@Test
	public void TestgetTxnDetailswithZeroValues()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenReturn(true);
		Map<String, Object> value = new HashMap<>();
		value.put("uin", "9172985031");
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
				.thenReturn(value);
		List<AutnTxn> valueList = getAuthTxnList();
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("1234");
		Mockito.when(authtxnRepo.findByToken(Mockito.anyString(), Mockito.any())).thenReturn(valueList);
		internalAuthTxnController.getAuthTxnDetails(IdType.UIN.getType(), "9172985031", 1, null);
	}

	@Test
	public void TestgetTxnDetailswithNullValues()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenReturn(true);
		Map<String, Object> value = new HashMap<>();
		value.put("uin", "9172985031");
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
				.thenReturn(value);
		List<AutnTxn> valueList = getAuthTxnList();
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("1234");
		Mockito.when(authtxnRepo.findByToken(Mockito.anyString(), Mockito.any())).thenReturn(valueList);
		internalAuthTxnController.getAuthTxnDetails(IdType.UIN.getType(), "9172985031", null, null);
	}

	@Test
	public void TestIdrepoListisNull() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenReturn(true);
		Map<String, Object> value = new HashMap<>();
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
				.thenReturn(value);
		List<AutnTxn> valueList = getAuthTxnList();
		Mockito.when(authtxnRepo.findByToken(Mockito.anyString(), Mockito.any())).thenReturn(valueList);
		internalAuthTxnController.getAuthTxnDetails(IdType.UIN.getType(), "9172985031", null, null);
	}

	@Test
	public void TestIdrepoListwithoutUIN() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenReturn(true);
		Map<String, Object> value = new HashMap<>();
		value.put("invalid", "invalidvalue");
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
				.thenReturn(value);
		List<AutnTxn> valueList = getAuthTxnList();
		Mockito.when(authtxnRepo.findByToken(Mockito.anyString(), Mockito.any())).thenReturn(valueList);
		internalAuthTxnController.getAuthTxnDetails(IdType.UIN.getType(), "9172985031", null, null);
	}

	@Test
	public void TestIdrepoListisEmpty() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenReturn(true);
		Map<String, Object> value = new HashMap<>();
		value.put("uin", "9172985031");
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
				.thenReturn(value);
		List<AutnTxn> valueList = getAuthTxnList();
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("1234");
		Mockito.when(authtxnRepo.findByToken(Mockito.anyString(), Mockito.any())).thenReturn(valueList);
		internalAuthTxnController.getAuthTxnDetails(IdType.UIN.getType(), "9172985031", null, null);
	}

	@Test
	public void TestIdAppException() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationAppException.class, () -> {
		Mockito.when(idValidator.validateUIN(Mockito.anyString())).thenReturn(false);
		internalAuthTxnController.getAuthTxnDetails(IdType.UIN.getType(), "", 1, 10);
			});
	}

	@Test
	public void TestBusinessException() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		assertThrows(IdAuthenticationBusinessException.class, () -> {
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet()))
				.thenThrow(new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
						IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage()));
		try {
			internalAuthTxnController.getAuthTxnDetails(IdType.UIN.getType(), "9172985031", 1, 10);
		} catch (IdAuthenticationAppException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE, e);
		}
			});
	}

	private List<AutnTxn> getAuthTxnList() {
		List<AutnTxn> valueList = new ArrayList<>();
		AutnTxn autnTxn = new AutnTxn();
		autnTxn.setRequestTrnId("1234567890");
		autnTxn.setRequestDTimes(null);
		autnTxn.setAuthTypeCode("UIN");
		autnTxn.setStatusCode("Y");
		autnTxn.setStatusComment("success");
		autnTxn.setRefIdType("test");
		autnTxn.setEntityName("Test");
		valueList.add(autnTxn);
		return valueList;
	}

}
