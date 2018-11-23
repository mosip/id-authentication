package io.mosip.authentication.service.impl.indauth.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
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

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.PinDTO;
import io.mosip.authentication.core.dto.indauth.PinInfo;
import io.mosip.authentication.core.dto.indauth.PinType;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.entity.UinEntity;
import io.mosip.authentication.service.integration.OTPManager;
import io.mosip.authentication.service.repository.AutnTxnRepository;
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
	private OTPAuthServiceImpl authserviceimpl;

	@Autowired
	Environment env;

	@Mock
	private AutnTxnRepository repository;

	UinEntity uinentity = new UinEntity();

	@Mock
	OTPManager otpmanager;

	@Before
	public void before() {
		ReflectionTestUtils.setField(authserviceimpl, "env", env);
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
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setOtp(true);
		authreqdto.setAuthType(authType);
		List<PinInfo> pinInfolist = new ArrayList<>();
		PinInfo pinInfo = new PinInfo();
		pinInfo.setType(PinType.OTP.getType());
		pinInfo.setValue("123456");
		pinInfolist.add(pinInfo);
		authreqdto.setPinInfo(pinInfolist);
		authserviceimpl.validateOtp(authreqdto, "1234567890");
	}

	@Test
	public void TestValidValidateOtp() throws IdAuthenticationBusinessException {
		AuthRequestDTO authreqdto = new AuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setOtp(true);
		authreqdto.setAuthType(authType);
		authreqdto.setTxnID("1234567890");
		authreqdto.setMuaCode("1234567890");
		List<PinInfo> pinInfolist = new ArrayList<>();
		PinInfo pinInfo = new PinInfo();
		pinInfo.setType(PinType.OTP.getType());
		pinInfo.setValue("123456");
		pinInfolist.add(pinInfo);
		authreqdto.setPinInfo(pinInfolist);
		List<AutnTxn> autntxnList = new ArrayList<AutnTxn>();
		AutnTxn authtxn = new AutnTxn();
		authtxn.setId("test");
		autntxnList.add(authtxn);
		Mockito.when(repository.findAllByRequestTrnIdAndRefId(Mockito.any(), Mockito.any())).thenReturn(autntxnList);
		AuthStatusInfo authStatusInfo = authserviceimpl.validateOtp(authreqdto, "1234567890");
		assertNotNull(authStatusInfo);
	}

	@Test(expected = IdValidationFailedException.class)
	public void TestInvalidKey() throws IdAuthenticationBusinessException {
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("application.id", "");
		ReflectionTestUtils.setField(authserviceimpl, "env", mockenv);
		AuthRequestDTO authreqdto = new AuthRequestDTO();
		List<PinInfo> pinInfolist = new ArrayList<>();
		PinInfo pinInfo = new PinInfo();
		pinInfo.setType(PinType.OTP.getType());
		pinInfo.setValue("123456");
		pinInfolist.add(pinInfo);
		authreqdto.setPinInfo(pinInfolist);
		Mockito.when(otpmanager.validateOtp(Mockito.any(), Mockito.any())).thenReturn(true);
		authserviceimpl.validateOtp(authreqdto, "");
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
		Mockito.when(repository.findAllByRequestTrnIdAndRefId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(autntxnList);
		assertFalse(authserviceimpl.validateTxnId("", ""));
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
		Mockito.when(repository.findAllByRequestTrnIdAndRefId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(autntxnList);
		assertTrue(authserviceimpl.validateTxnId("232323", "234234"));
	}

	/**
	 * To Test the Value id null
	 * 
	 */

	@Test
	public void TestisEmpty_withNull() {
		String txnId = null;
		assertTrue(authserviceimpl.isEmpty(txnId));
	}

	/**
	 * To test the Value is Empty
	 */
	@Test
	public void TestisEmpty_withEmpty() {
		String txnId = "";
		assertTrue(authserviceimpl.isEmpty(txnId));
	}

	/**
	 * To test the value with empty
	 */
	@Test
	public void TestisEmpty_withEmptyString() {
		String txnId = "  ";
		assertTrue(authserviceimpl.isEmpty(txnId));
	}

	/**
	 * To test Valid Transaction-id
	 */

	@Test
	public void TestisEmpty_withValidString() {
		String txnId = "TXN00001";
		assertFalse(authserviceimpl.isEmpty(txnId));
	}

	/**
	 * Validate the OTP Request
	 * 
	 * @throws IdAuthenticationBusinessException
	 */
	@Test
	public void TestValidateOtp_ValidRequest() throws IdAuthenticationBusinessException {
//		AutnTxn autntxn = new AutnTxn();
//		autntxn.setRequestTxnId("TXN001");
//		List<AutnTxn> autntxnList = new ArrayList<AutnTxn>();
//		autntxnList.add(autntxn);
//		Mockito.when(repository.findAllByRequestTxnIdAndUin(Mockito.anyString(), Mockito.anyString()))
//				.thenReturn(autntxnList);
//		otpAuthRequestDTO.setTxnID("1234567890");
//		otpAuthRequestDTO.setMuaCode("ASA000000011");
//		otpAuthRequestDTO.setTxnID("TXN00001");
//		otpAuthRequestDTO.setId("1134034024034");
//		otpAuthRequestDTO.setMuaCode("AUA0001");
//		PinDTO pindto = new PinDTO();
//		pindto.setType(PinType.OTP);
//		pindto.setValue("23232323");
//		otpAuthRequestDTO.se(new PersonalIdentityDataDTO());
//		otpAuthRequestDTO.getPii().setPin(pindto);
//		assertFalse(authserviceimpl.validateOtp(otpAuthRequestDTO, "45345435345").isStatus());
	}

	/**
	 * 
	 * Throw Custom IdAuthenticationBusinessException class
	 * 
	 * @throws IdAuthenticationBusinessException
	 */

//	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidValidateOtp() throws IdAuthenticationBusinessException {
//		OTPAuthServiceImpl authservice = Mockito.mock(OTPAuthServiceImpl.class);
//		Mockito.when(authservice.validateOtp(Mockito.any(), Mockito.anyString()))
//				.thenThrow(new IdAuthenticationBusinessException(
//						IdAuthenticationErrorConstants.KERNEL_OTP_VALIDATION_REQUEST_FAILED));
//		otpAuthRequestDTO.setTxnID("1234567890");
//		otpAuthRequestDTO.setMuaCode("ASA000000011");
//		otpAuthRequestDTO.setTxnID("TXN00001");
//		otpAuthRequestDTO.setId("1134034024034");
//		otpAuthRequestDTO.setMuaCode("AUA0001");
//		PinDTO pindto = new PinDTO();
//		pindto.setType(PinType.OTP);
//		pindto.setValue("23232323");
//		otpAuthRequestDTO.setPii(new PersonalIdentityDataDTO());
//		otpAuthRequestDTO.getPii().setPin(pindto);
//		authservice.validateOtp(otpAuthRequestDTO, "");
	}

	/**
	 * Test auth service with validate OTP
	 * 
	 * @throws IdAuthenticationBusinessException
	 */

//	@Ignore
//	@Test(expected = IDDataValidationException.class)
	@Test
	public void TEst_isEMptynull() throws IdAuthenticationBusinessException {
//		OTPAuthServiceImpl authservice = Mockito.mock(OTPAuthServiceImpl.class);
//		Mockito.when(authservice.isEmpty(Mockito.any())).thenReturn(true);
//		otpAuthRequestDTO.setTxnID("1234567890");
//		otpAuthRequestDTO.setMuaCode("ASA000000011");
//		otpAuthRequestDTO.setTxnID("TXN00001");
//		otpAuthRequestDTO.setId("1134034024034");
//		otpAuthRequestDTO.setMuaCode("AUA0001");
//		PinDTO pindto = new PinDTO();
//		pindto.setType(PinType.OTP);
//		pindto.setValue("23232323");
//		otpAuthRequestDTO.setPii(new PersonalIdentityDataDTO());
//		otpAuthRequestDTO.getPii().setPin(pindto);
//		authservice.validateOtp(otpAuthRequestDTO, "34545");
	}

}
