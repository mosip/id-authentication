package io.mosip.authentication.internal.service.controller;

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.impl.IdServiceImpl;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.core.autntxn.dto.AutnTxnRequestDto;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.internal.service.validator.AuthTxnValidator;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class InternalAuthTransactionTest {

	@InjectMocks
	InternalAuthTxnController internalAuthTxnController;

	@Mock
	private AutnTxnRepository authtxnRepo;

	@InjectMocks
	private AuthTxnValidator authTxnValidator;

	@Autowired
	private Environment environment;

	@Mock
	WebDataBinder binder;

	@Mock
	private UinValidatorImpl uinValidator;

	@Mock
	private VidValidatorImpl vidValidator;

	@Mock
	private IdServiceImpl idService;

	@Before
	public void before() {
		ReflectionTestUtils.invokeMethod(internalAuthTxnController, "initBinder", binder);
		ReflectionTestUtils.setField(internalAuthTxnController, "authTxnValidator", authTxnValidator);
		ReflectionTestUtils.setField(authTxnValidator, "env", environment);
	}

	@Test
	public void testSupportTrue() {
		assertTrue(authTxnValidator.supports(AutnTxnRequestDto.class));
	}

	@Test
	public void TestgetTxnDetails() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		Map<String, Object> value = new HashMap<>();
		value.put("uin", "9172985031");
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(value);
		List<AutnTxn> valueList = getAuthTxnList();
		Mockito.when(authtxnRepo.findByPagableUinorVid(Mockito.anyString(), Mockito.any())).thenReturn(valueList);
		internalAuthTxnController.getAuthTxnDetails(IdType.UIN.getType(), "9172985031", 1, 10);
	}

	@Test
	public void TestgetTxnDetailswithZeroValues()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		Map<String, Object> value = new HashMap<>();
		value.put("uin", "9172985031");
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(value);
		List<AutnTxn> valueList = getAuthTxnList();
		Mockito.when(authtxnRepo.findByPagableUinorVid(Mockito.anyString(), Mockito.any())).thenReturn(valueList);
		internalAuthTxnController.getAuthTxnDetails(IdType.UIN.getType(), "9172985031", 0, 0);
	}

	@Test
	public void TestgetTxnDetailswithNullValues()
			throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		Map<String, Object> value = new HashMap<>();
		value.put("uin", "9172985031");
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(value);
		List<AutnTxn> valueList = getAuthTxnList();
		Mockito.when(authtxnRepo.findByPagableUinorVid(Mockito.anyString(), Mockito.any())).thenReturn(valueList);
		internalAuthTxnController.getAuthTxnDetails(IdType.UIN.getType(), "9172985031", null, null);
	}

	@Test
	public void TestIdrepoListisNull() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		Map<String, Object> value = new HashMap<>();
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(value);
		List<AutnTxn> valueList = getAuthTxnList();
		Mockito.when(authtxnRepo.findByPagableUinorVid(Mockito.anyString(), Mockito.any())).thenReturn(valueList);
		internalAuthTxnController.getAuthTxnDetails(IdType.UIN.getType(), "9172985031", null, null);
	}
	
	@Test
	public void TestIdrepoListwithoutUIN() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		Map<String, Object> value = new HashMap<>();
		value.put("invalid", "invalidvalue");
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(value);
		List<AutnTxn> valueList = getAuthTxnList();
		Mockito.when(authtxnRepo.findByPagableUinorVid(Mockito.anyString(), Mockito.any())).thenReturn(valueList);
		internalAuthTxnController.getAuthTxnDetails(IdType.UIN.getType(), "9172985031", null, null);
	}

	@Test
	public void TestIdrepoListisEmpty() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		Map<String, Object> value = new HashMap<>();
		value.put("uin", "9172985031");
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(value);
		List<AutnTxn> valueList = getAuthTxnList();
		Mockito.when(authtxnRepo.findByPagableUinorVid(Mockito.anyString(), Mockito.any())).thenReturn(valueList);
		internalAuthTxnController.getAuthTxnDetails(IdType.UIN.getType(), "9172985031", null, null);
	}

	@Test(expected = IdAuthenticationAppException.class)
	public void TestIdAppException() throws IDDataValidationException, IdAuthenticationAppException {
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(false);
		internalAuthTxnController.getAuthTxnDetails(IdType.UIN.getType(), "", 1, 10);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestBusinessException() throws IdAuthenticationAppException, IdAuthenticationBusinessException {
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenThrow(new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(),
						IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage()));
		try {
			internalAuthTxnController.getAuthTxnDetails(IdType.UIN.getType(), "9172985031", 1, 10);
		} catch (IdAuthenticationAppException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE, e);
		}
	}

	private List<AutnTxn> getAuthTxnList() {
		List<AutnTxn> valueList = new ArrayList<>();
		AutnTxn autnTxn = new AutnTxn();
		autnTxn.setRequestTrnId("1234567890");
		autnTxn.setRequestDTtimes(null);
		autnTxn.setAuthTypeCode("UIN");
		autnTxn.setStatusCode("Y");
		autnTxn.setStatusComment("success");
		autnTxn.setRefIdType("test");
		valueList.add(autnTxn);
		return valueList;
	}

}
