package io.mosip.authentication.common.service.impl;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.impl.OTPAuthServiceImpl;
import io.mosip.authentication.common.service.integration.OTPManager;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.repository.VIDRepository;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import reactor.ipc.netty.http.HttpResources;

/**
 * 
 * @author Dinesh Karuppiah
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
public class OTPAuthServiceTest {

	@InjectMocks
	private OTPAuthServiceImpl otpauthserviceimpl;

	@InjectMocks
	private MatchInputBuilder matchInputBuilder;

	@InjectMocks
	private IdInfoFetcherImpl idInfoFetcherImpl;


	@Autowired
	Environment env;

	@Mock
	private AutnTxnRepository repository;

	@Mock
	private VIDRepository vidrepository;

	@Mock
	OTPManager otpmanager;

	@InjectMocks
	private IdInfoHelper idInfoHelper;

	@Before
	public void before() {
		ReflectionTestUtils.setField(otpauthserviceimpl, "env", env);
		ReflectionTestUtils.setField(otpauthserviceimpl, "matchInputBuilder", matchInputBuilder);
		ReflectionTestUtils.setField(matchInputBuilder, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(matchInputBuilder, "idInfoFetcher", idInfoFetcherImpl);
		ReflectionTestUtils.setField(otpauthserviceimpl, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(idInfoHelper, "environment", env);
	}

	private AuthRequestDTO otpAuthRequestDTO = new AuthRequestDTO();

	/**
	 * To close the http resources
	 */
	@AfterClass
	public static void afterClass() {
		HttpResources.reset();
	}

	@Test(expected = IdValidationFailedException.class)
	public void TestIDDataValidationException() throws IdAuthenticationBusinessException {
		AuthRequestDTO authreqdto = new AuthRequestDTO();
		authreqdto.setRequestTime("2019-02-18T18:17:48.923+05:30");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setOtp(true);
		authreqdto.setRequestedAuth(authType);
		RequestDTO request = new RequestDTO();
		request.setOtp("123455");
		authreqdto.setRequest(request);
		otpauthserviceimpl.authenticate(authreqdto, "1234567890", Collections.emptyMap(), "123456");
	}

	@Test
	public void TestValidValidateOtp() throws IdAuthenticationBusinessException {
		AuthRequestDTO authreqdto = new AuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setOtp(true);
		authreqdto.setRequestedAuth(authType);
		authreqdto.setTransactionID("1234567890");
		authreqdto.setRequestTime("2019-02-18T18:17:48.923+05:30");
		RequestDTO request = new RequestDTO();
		request.setOtp("123456");
		authreqdto.setRequest(request);
		List<AutnTxn> autntxnList = new ArrayList<AutnTxn>();
		AutnTxn authtxn = new AutnTxn();
		authtxn.setId("test");
		autntxnList.add(authtxn);
		List<String> valueList = new ArrayList<>();
		valueList.add("1234567890");
		Mockito.when(vidrepository.findVIDByUIN(Mockito.anyString(), Mockito.any())).thenReturn(valueList);
		Mockito.when(repository.findByUinorVid(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.any(), Mockito.any())).thenReturn(valueList);
		Mockito.when(otpmanager.validateOtp(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
		AuthStatusInfo authStatusInfo = otpauthserviceimpl.authenticate(authreqdto, "1234567890",
				Collections.emptyMap(), "123456");
		assertNotNull(authStatusInfo);
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestValidValidateOtpFailure() throws IdAuthenticationBusinessException {
		AuthRequestDTO authreqdto = new AuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setOtp(true);
		authreqdto.setRequestedAuth(authType);
		authreqdto.setTransactionID("1234567890");
		authreqdto.setRequestTime("2019-02-18T18:17:48.923+05:30");
		RequestDTO request = new RequestDTO();
		request.setOtp("123456");
		authreqdto.setRequest(request);
		List<AutnTxn> autntxnList = new ArrayList<AutnTxn>();
		AutnTxn authtxn = new AutnTxn();
		authtxn.setId("test");
		autntxnList.add(authtxn);
		List<String> valueList = new ArrayList<>();
		valueList.add("1234567890");
		Mockito.when(vidrepository.findVIDByUIN(Mockito.anyString(), Mockito.any())).thenReturn(valueList);
		Mockito.when(repository.findByUinorVid(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.any(), Mockito.any())).thenReturn(valueList);
		Mockito.when(otpmanager.validateOtp(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
		AuthStatusInfo authStatusInfo = otpauthserviceimpl.authenticate(authreqdto, "1234567890",
				Collections.emptyMap(), "123456");
		assertNotNull(authStatusInfo);
	}

	@Test(expected = IdValidationFailedException.class)
	public void TestInvalidKey() throws IdAuthenticationBusinessException {
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("application.id", "");
		ReflectionTestUtils.setField(otpauthserviceimpl, "env", mockenv);
		AuthRequestDTO authreqdto = new AuthRequestDTO();
		authreqdto.setRequestTime("2019-02-18T18:17:48.923+05:30");
		RequestDTO request = new RequestDTO();
		request.setOtp("123456");
		authreqdto.setRequest(request);
		Mockito.when(otpmanager.validateOtp(Mockito.any(), Mockito.any())).thenReturn(true);
		otpauthserviceimpl.authenticate(authreqdto, "", Collections.emptyMap(), "123456");
	}

	/**
	 * method to test IDDatavalidation Exception for IDA
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test
	public void Test_InvalidTxnId() throws IdAuthenticationBusinessException {
		List<AutnTxn> autntxnList = new ArrayList<AutnTxn>();
		autntxnList.add(null);
		List<String> valueList = new ArrayList<>();
		valueList.add("1234567890");
		Mockito.when(repository.findByUinorVid(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.any(), Mockito.any())).thenReturn(valueList);
		assertTrue(otpauthserviceimpl.validateTxnId("", "", "", "2019-02-18T18:17:48.923+05:30"));
	}

	/**
	 * To test the Transaction id with UIN
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test
	public void Test_validTxnId() throws IdAuthenticationBusinessException {
		AutnTxn autntxn = new AutnTxn();
		autntxn.setRequestTrnId("TXN001");

		List<AutnTxn> autntxnList = new ArrayList<AutnTxn>();
		autntxnList.add(autntxn);
		List<String> valueList = new ArrayList<>();
		valueList.add("1234567890");
		Mockito.when(repository.findByUinorVid(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.any(), Mockito.any())).thenReturn(valueList);
		assertTrue(otpauthserviceimpl.validateTxnId("232323", "1234567890", "1234567890",
				"2019-02-18T18:17:48.923+05:30"));
	}

	@Test(expected = IDDataValidationException.class)
	public void TestOtpDatavalidationException() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setOtp(null);
		authRequestDTO.setRequest(request);
		String uin = null;
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		String partnerId = null;
		otpauthserviceimpl.authenticate(authRequestDTO, uin, idInfo, partnerId);
	}

	@Test(expected = IdValidationFailedException.class)
	public void TestOtpbyVID() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("1234567890");
		authRequestDTO.setIndividualIdType(IdType.VID.getType());
		RequestDTO request = new RequestDTO();
		request.setOtp("123456");
		authRequestDTO.setRequest(request);
		String uin = null;
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		String partnerId = null;
		otpauthserviceimpl.authenticate(authRequestDTO, uin, idInfo, partnerId);
	}

	/**
	 * To Test the Value id null
	 * 
	 */

	@Test
	public void TestisEmpty_withNull() {
		String txnId = null;
		assertTrue(otpauthserviceimpl.isEmpty(txnId));
	}

	/**
	 * To test the Value is Empty
	 */
	@Test
	public void TestisEmpty_withEmpty() {
		String txnId = "";
		assertTrue(otpauthserviceimpl.isEmpty(txnId));
	}

	/**
	 * To test the value with empty
	 */
	@Test
	public void TestisEmpty_withEmptyString() {
		String txnId = "  ";
		assertTrue(otpauthserviceimpl.isEmpty(txnId));
	}

	/**
	 * To test Valid Transaction-id
	 */

	@Test
	public void TestisEmpty_withValidString() {
		String txnId = "TXN00001";
		assertFalse(otpauthserviceimpl.isEmpty(txnId));
	}

	@Ignore
	@Test(expected = IDDataValidationException.class)
	public void TestOtpisNotPresent() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		otpauthserviceimpl.authenticate(authRequestDTO, "", Collections.emptyMap(), "123456");
	}

	/**
	 * Validate the OTP Request
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Ignore
	@Test
	public void TestValidateOtp_ValidRequest() throws IdAuthenticationBusinessException {
		AutnTxn autntxn = new AutnTxn();
		autntxn.setRequestTrnId("TXN00001");
		List<AutnTxn> autntxnList = new ArrayList<AutnTxn>();
		autntxnList.add(autntxn);
		List<String> valueList = new ArrayList<>();
		valueList.add("1234567890");
		otpAuthRequestDTO.setTransactionID("TXN00001");
		otpAuthRequestDTO.setId("mosip.identity.auth");
		otpAuthRequestDTO.setIndividualId("426789089018");
		otpAuthRequestDTO.setRequestTime("2019-02-18T18:17:48.923+05:30");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setOtp(true);
		otpAuthRequestDTO.setRequestedAuth(authType);
		Mockito.when(repository.findByUinorVid(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.any(), Mockito.any())).thenReturn(valueList);
		Mockito.when(vidrepository.findVIDByUIN(Mockito.anyString(), Mockito.any())).thenReturn(valueList);
		AuthStatusInfo authStatus = otpauthserviceimpl.authenticate(otpAuthRequestDTO, "45345435345",
				Collections.emptyMap(), "123456");
		assertFalse(authStatus.isStatus());
	}

	/**
	 * 
	 * Throw Custom IdAuthenticationBusinessException class
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Ignore
	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidValidateOtp() throws IdAuthenticationBusinessException {
		AutnTxn autntxn = new AutnTxn();
		autntxn.setRequestTrnId("TXN00001");
		List<AutnTxn> autntxnList = new ArrayList<AutnTxn>();
		autntxnList.add(autntxn);
		List<String> valueList = new ArrayList<>();
		valueList.add("1234567890");
		otpAuthRequestDTO.setTransactionID("TXN00001");
		otpAuthRequestDTO.setId("mosip.identity.auth");
		otpAuthRequestDTO.setRequestTime("2019-02-18T18:17:48.923+05:30");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setOtp(true);
		otpAuthRequestDTO.setRequestedAuth(authType);
		Mockito.when(vidrepository.findVIDByUIN(Mockito.anyString(), Mockito.any())).thenReturn(valueList);
		AuthStatusInfo authStatus = otpauthserviceimpl.authenticate(otpAuthRequestDTO, "45345435345",
				Collections.emptyMap(), "123456");
		assertFalse(authStatus.isStatus());
	}

}

