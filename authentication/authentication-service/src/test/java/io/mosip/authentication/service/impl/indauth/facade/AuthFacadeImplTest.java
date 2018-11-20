package io.mosip.authentication.service.impl.indauth.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.KycAuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.KycAuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.KycInfo;
import io.mosip.authentication.core.dto.indauth.KycResponseDTO;
import io.mosip.authentication.core.dto.indauth.KycType;
import io.mosip.authentication.core.dto.indauth.PinInfo;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.service.impl.id.service.impl.IdAuthServiceImpl;
import io.mosip.authentication.service.impl.id.service.impl.IdInfoServiceImpl;
import io.mosip.authentication.service.impl.indauth.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.service.impl.indauth.service.KycServiceImpl;
import io.mosip.authentication.service.impl.indauth.service.OTPAuthServiceImpl;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoHelper;
import io.mosip.authentication.service.integration.IdTemplateManager;

// TODO: Auto-generated Javadoc
/**
 * The class validates AuthFacadeImpl.
 *
 * @author Arun Bose
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class AuthFacadeImplTest {

	/** The env. */
	@Autowired
	Environment env;

	/** The auth facade impl. */
	@InjectMocks
	private AuthFacadeImpl authFacadeImpl;

	/** The id auth service impl. */
	@Mock
	private IdAuthServiceImpl idAuthServiceImpl;

	/** The otp auth service impl. */
	@Mock
	private OTPAuthServiceImpl otpAuthServiceImpl;

	@Mock
	private KycServiceImpl kycService;

	@InjectMocks
	private DemoHelper demoHelper;

	@Mock
	private IdInfoServiceImpl idInfoServiceImpl;

	@InjectMocks
	private IdTemplateManager idTemplateManager;

	@Autowired
	private MessageSource messageSource;

	/**
	 * Before.
	 */
	@Before
	public void before() {
		ReflectionTestUtils.setField(authFacadeImpl, "idAuthService", idAuthServiceImpl);
		ReflectionTestUtils.setField(authFacadeImpl, "otpService", otpAuthServiceImpl);
		ReflectionTestUtils.setField(authFacadeImpl, "kycService", kycService);
		ReflectionTestUtils.setField(kycService, "demoHelper", demoHelper);
		ReflectionTestUtils.setField(kycService, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(kycService, "idInfoServiceImpl", idInfoServiceImpl);
		ReflectionTestUtils.setField(kycService, "messageSource", messageSource);
		ReflectionTestUtils.setField(kycService, "env", env);

	}

	/**
	 * This class tests the authenticateApplicant method where it checks the IdType
	 * and AuthType.
	 *
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 * @throws IdAuthenticationDaoException
	 */
	@Ignore
	@Test
	public void authenticateApplicantTest() throws IdAuthenticationBusinessException, IdAuthenticationDaoException {
		String refId = "1234";
		boolean authStatus = false;
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		authResponseDTO.setStatus("n");
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIdvIdType(IdType.UIN.getType());
		authRequestDTO.setId("1234567");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setOtp(true);
		authRequestDTO.setAuthType(authTypeDTO);
		Mockito.when(idAuthServiceImpl.validateUIN(Mockito.any())).thenReturn(refId);
		Mockito.when(otpAuthServiceImpl.validateOtp(authRequestDTO, refId))
				.thenReturn(AuthStatusInfoBuilder.newInstance().setStatus(authStatus).build());
		authFacadeImpl.authenticateApplicant(authRequestDTO);
	}

	/**
	 * This class tests the processAuthType (OTP) method where otp validation
	 * failed.
	 *
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	@Test
	public void processAuthTypeTestFail() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authRequestDTO.setAuthType(authType);
		authRequestDTO.getAuthType().setOtp(false);
		List<AuthStatusInfo> authStatusList = authFacadeImpl.processAuthType(authRequestDTO, "1233");

		assertTrue(authStatusList.stream().noneMatch(
				status -> status.getUsageDataBits().contains(AuthUsageDataBit.USED_OTP) || status.isStatus()));
	}

	/**
	 * This class tests the processAuthType (OTP) method where otp validation gets
	 * successful.
	 *
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */

	@Test
	public void processAuthTypeTestSuccess() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setOtp(true);
		authRequestDTO.setAuthType(authTypeDTO);
		Mockito.when(otpAuthServiceImpl.validateOtp(authRequestDTO, "1242")).thenReturn(AuthStatusInfoBuilder
				.newInstance().setStatus(true).addAuthUsageDataBits(AuthUsageDataBit.USED_OTP).build());
		List<AuthStatusInfo> authStatusList = authFacadeImpl.processAuthType(authRequestDTO, "1242");
		assertTrue(authStatusList.stream().anyMatch(
				status -> status.getUsageDataBits().contains(AuthUsageDataBit.USED_OTP) && status.isStatus()));
	}

	/**
	 * This class tests the processIdtype where UIN is passed and gets successful.
	 *
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	@Test
	public void processIdtypeUINSuccess() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIdvIdType(IdType.UIN.getType());
		String refId = "1234";
		Mockito.when(idAuthServiceImpl.validateUIN(Mockito.any())).thenReturn(refId);
		String referenceId = authFacadeImpl.processIdType(authRequestDTO);
		assertEquals(referenceId, refId);
	}

	/**
	 * This class tests the processIdtype where VID is passed and gets successful.
	 *
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	@Test
	public void processIdtypeVIDSuccess() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIdvIdType(IdType.VID.getType());
		String refId = "1234";
		Mockito.when(idAuthServiceImpl.validateVID(Mockito.any())).thenReturn(refId);
		String referenceId = authFacadeImpl.processIdType(authRequestDTO);
		assertEquals(referenceId, refId);
	}

	/**
	 * This class tests the processIdtype where UIN is passed and gets failed.
	 *
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */

	@Test(expected = IdAuthenticationBusinessException.class)
	public void processIdtypeUINFailed() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIdvIdType(IdType.UIN.getType());
		String refId = "1234";
		IdValidationFailedException idException = new IdValidationFailedException(
				IdAuthenticationErrorConstants.INVALID_UIN);
		Mockito.when(idAuthServiceImpl.validateUIN(Mockito.any())).thenThrow(idException);
		String referenceId = authFacadeImpl.processIdType(authRequestDTO);
		// assertEquals(referenceId,refId);
	}

	/**
	 * This class tests the processIdtype where VID is passed and gets failed.
	 *
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */

	@Test(expected = IdAuthenticationBusinessException.class)
	public void processIdtypeVIDFailed() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIdvIdType(IdType.VID.getType());
		String refId = "1234";
		IdValidationFailedException idException = new IdValidationFailedException(
				IdAuthenticationErrorConstants.INVALID_VID);
		Mockito.when(idAuthServiceImpl.validateVID(Mockito.any())).thenThrow(idException);
		authFacadeImpl.processIdType(authRequestDTO);

	}
	@Ignore
	@Test
	public void processKycAuthValid() throws IdAuthenticationBusinessException {
		KycAuthRequestDTO kycAuthRequestDTO = new KycAuthRequestDTO();
		kycAuthRequestDTO.setConsentReq(true);
		kycAuthRequestDTO.setEPrintReq(true);
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVer("1.1");
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIdvIdType(IdType.UIN.getType());
		authRequestDTO.setIdvId("234567890123");
		ZoneOffset offset = ZoneOffset.MAX;
		authRequestDTO.setReqTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setVer("1.1");
		authRequestDTO.setMuaCode("1234567890");
		authRequestDTO.setTxnID("1234567890");
		authRequestDTO.setReqHmac("zdskfkdsnj");
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPersonalIdentity(true);
		authTypeDTO.setOtp(true);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("FR");
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setIdentity(idDTO);
		authRequestDTO.setAuthType(authTypeDTO);
		authRequestDTO.setRequest(reqDTO);
		kycAuthRequestDTO.setAuthRequest(authRequestDTO);
		kycAuthRequestDTO.setEKycAuthType("O");
		PinInfo pinInfo = new PinInfo();
		pinInfo.setType("OTP");
		pinInfo.setValue("123456");
		List<PinInfo> otplist = new ArrayList<>();
		otplist.add(pinInfo);
		authRequestDTO.setPinInfo(otplist);
		KycInfo info = new KycInfo();
		info.setEPrint("y");
		info.setIdvId("234567890123");
		info.setIdentity(null);
		String refId = "12343457";
		// Mockito.when(idAuthServiceImpl.validateUIN(Mockito.any())).thenReturn(refId);
		Mockito.when(kycService.retrieveKycInfo(Mockito.anyString(), Mockito.any(KycType.class), Mockito.any(),
				Mockito.any())).thenReturn(info);

		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
		kycAuthResponseDTO.setResTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthResponseDTO.setStatus("Y");
		kycAuthResponseDTO.setTxnID("34567");
		kycAuthResponseDTO.setErr(null);
		KycResponseDTO response = new KycResponseDTO();
		response.setAuth(null);
		response.setKyc(null);
		kycAuthResponseDTO.setResponse(response);
		kycAuthResponseDTO.setTtl("2");
		kycAuthResponseDTO.getResponse().setKyc(info);
		kycAuthResponseDTO.setTtl(env.getProperty("ekyc.ttl.hours"));
		authFacadeImpl.processKycAuth(kycAuthRequestDTO);
	}

}
