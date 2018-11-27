package io.mosip.authentication.service.impl.indauth.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import io.mosip.authentication.core.dto.indauth.IdentityValue;
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
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdInfoService;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.id.service.impl.IdAuthServiceImpl;
import io.mosip.authentication.service.impl.indauth.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.service.impl.indauth.service.KycServiceImpl;
import io.mosip.authentication.service.impl.indauth.service.OTPAuthServiceImpl;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.authentication.service.integration.NotificationManager;
import io.mosip.authentication.service.integration.OTPManager;

// TODO: Auto-generated Javadoc
/**
 * The class validates AuthFacadeImpl.
 *
 * @author Arun Bose
 * 
 * 
 * @author Prem Kumar
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class AuthFacadeImplTest {



	private static final String STATUS_SUCCESS = "Y";
	/** The auth facade impl. */
	@InjectMocks
	private AuthFacadeImpl authFacadeImpl;
	/** The env. */
	@Autowired
	private Environment env;

	/** The id auth service impl. */
	@Mock
	private IdAuthServiceImpl idAuthServiceImpl;
	@InjectMocks
	private KycServiceImpl kycServiceImpl;

	/** The otp auth service impl. */
	@Mock
	private OTPAuthServiceImpl otpAuthServiceImpl;
	@Mock
	private IdAuthService idAuthService;

	@Mock
	private KycService kycService;

	@Mock
	private DemoHelper demoHelper;

	@Mock
	private IdInfoService idInfoService;
	@Mock
	private DemoAuthService demoAuthService;

	@InjectMocks
	private IdTemplateManager idTemplateManager;

	@Mock
	private IDAMappingConfig idMappingConfig;

	@InjectMocks
	NotificationManager notificationManager;

	@InjectMocks
	private RestRequestFactory restRequestFactory;

	@InjectMocks
	private RestHelper restHelper;
	
	@Mock
	private MessageSource messageSource;
	
	@Mock
	private OTPManager otpManager;

	/**
	 * Before.
	 */
	@Before
	public void before() {
		ReflectionTestUtils.setField(authFacadeImpl, "idAuthService", idAuthServiceImpl);
		ReflectionTestUtils.setField(authFacadeImpl, "otpService", otpAuthServiceImpl);
		ReflectionTestUtils.setField(authFacadeImpl, "kycService", kycService);
		ReflectionTestUtils.setField(authFacadeImpl, "env", env);
		ReflectionTestUtils.setField(kycServiceImpl, "demoHelper", demoHelper);
		ReflectionTestUtils.setField(authFacadeImpl, "demoHelper", demoHelper);
		ReflectionTestUtils.setField(kycServiceImpl, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(kycServiceImpl, "env", env);
		ReflectionTestUtils.setField(kycServiceImpl, "idAuthService", idAuthService);
		ReflectionTestUtils.setField(kycServiceImpl, "messageSource", messageSource);
		ReflectionTestUtils.setField(authFacadeImpl, "notificationManager", notificationManager);



	}

	/**
	 * This class tests the authenticateApplicant method where it checks the IdType
	 * and AuthType.
	 *
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 * @throws IdAuthenticationDaoException
	 */
	
	@Test
	public void authenticateApplicantTest() throws IdAuthenticationBusinessException, IdAuthenticationDaoException {
	//	String refId = "1234";
		String refId = "8765";
		boolean authStatus = true;
		//AuthResponseDTO authResponseDTO = new AuthResponseDTO();
	//	authResponseDTO.setStatus("Y");
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIdvIdType(IdType.UIN.getType());
		authRequestDTO.setId("1234567");
		authRequestDTO.setReqTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setOtp(true);
		authRequestDTO.setAuthType(authTypeDTO);
		Mockito.when(idAuthServiceImpl.validateUIN(Mockito.any())).thenReturn(refId);
		Mockito.when(otpAuthServiceImpl.validateOtp(authRequestDTO, refId))
				.thenReturn(AuthStatusInfoBuilder.newInstance().setStatus(authStatus).build());
		

		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);

		String ismaskRequired = env.getProperty("uin.masking.required");
		Mockito.when(idInfoService.getIdInfo(refId)).thenReturn(idInfo);
		Optional<String> uinOpt = Optional.of("426789089018");
		Mockito.when(idAuthServiceImpl.getUIN(refId)).thenReturn(uinOpt);

		String email = "abcd";
		String mobileNumber = "7890754";

		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		authResponseDTO.setStatus("y");
		
		authResponseDTO.setResTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		ReflectionTestUtils.setField(notificationManager, "environment", env);
		ReflectionTestUtils.setField(notificationManager, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(notificationManager, "restRequestFactory", restRequestFactory);
		ReflectionTestUtils.setField(restRequestFactory, "env", env);
		ReflectionTestUtils.setField(notificationManager, "restHelper", restHelper);

		ReflectionTestUtils.setField(authFacadeImpl, "demoHelper", demoHelper);
		ReflectionTestUtils.setField(demoHelper, "environment", env);
		ReflectionTestUtils.setField(demoHelper, "idMappingConfig", idMappingConfig);
		ReflectionTestUtils.setField(authFacadeImpl, "notificationManager", notificationManager);
		ReflectionTestUtils.setField(authFacadeImpl, "env", env);

		IdentityValue identityValue = new IdentityValue("en", "mosip");
		Mockito.when(idInfoService.getIdInfo(refId)).thenReturn(idInfo);
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.NAME_PRI, idInfo)).thenReturn(identityValue);
		identityValue.setLanguage("en");
		identityValue.setValue("Prem.Kumar4@mindtree.com");
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.EMAIL, idInfo)).thenReturn(identityValue);
		identityValue.setLanguage("en");
		identityValue.setValue("1234567890");
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.PHONE, idInfo)).thenReturn(identityValue);
		
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "sendAuthNotification", authRequestDTO, 
				 refId, authResponseDTO);
		
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
		//Mockito.when(idAuthServiceImpl.validateUIN(Mockito.any())).thenReturn(refId);
		//Mockito.when(idAuthService.getUIN(refId)).thenReturn( Optional.of("426789089018"));
		Mockito.when(kycService.retrieveKycInfo(refId, KycType.LIMITED, kycAuthRequestDTO.isEPrintReq(),
				kycAuthRequestDTO.isConsentReq())).thenReturn(info);

		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
		kycAuthResponseDTO.setResTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		kycAuthResponseDTO.setStatus(STATUS_SUCCESS);
		kycAuthResponseDTO.setTxnID("34567");
		kycAuthResponseDTO.setErr(null);
		KycResponseDTO response = new KycResponseDTO();
		response.setAuth(null);
		response.setKyc(null);
		kycAuthResponseDTO.setResponse(response);
		kycAuthResponseDTO.setTtl("2");
		kycAuthResponseDTO.getResponse().setKyc(info);
		kycAuthResponseDTO.setTtl(env.getProperty("ekyc.ttl.hours"));
		AuthResponseDTO authResponseDTO=new AuthResponseDTO();
		authResponseDTO.setResTime(new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date()));
		assertNotNull(authFacadeImpl.processKycAuth(kycAuthRequestDTO, authResponseDTO));
		
	}

	/**
	 * 
	 * Test Method is for checking the Success Case for SendAuthNotification Method.
	 * 
	 * @throws IdAuthenticationBusinessException
	 * @throws IdAuthenticationDaoException
	 */
	@Ignore
	@Test
	public void testSendAuthNotificationSuccess()
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException {

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

		String refId = "8765";

		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);

		String ismaskRequired = env.getProperty("uin.masking.required");
		Mockito.when(idInfoService.getIdInfo(refId)).thenReturn(idInfo);
		Optional<String> uinOpt = Optional.of("426789089018");
		Mockito.when(idAuthServiceImpl.getUIN(refId)).thenReturn(uinOpt);

		String email = "abcd";
		String mobileNumber = "7890754";

		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		authResponseDTO.setStatus("y");
		authResponseDTO.setResTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()));
		ReflectionTestUtils.setField(notificationManager, "environment", env);
		ReflectionTestUtils.setField(notificationManager, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(notificationManager, "restRequestFactory", restRequestFactory);
		ReflectionTestUtils.setField(restRequestFactory, "env", env);
		ReflectionTestUtils.setField(notificationManager, "restHelper", restHelper);

		ReflectionTestUtils.setField(authFacadeImpl, "demoHelper", demoHelper);
		ReflectionTestUtils.setField(demoHelper, "environment", env);
		ReflectionTestUtils.setField(demoHelper, "idMappingConfig", idMappingConfig);
		ReflectionTestUtils.setField(authFacadeImpl, "notificationManager", notificationManager);
		ReflectionTestUtils.setField(authFacadeImpl, "env", env);

		IdentityValue identityValue = new IdentityValue("en", "mosip");
		Mockito.when(idInfoService.getIdInfo(refId)).thenReturn(idInfo);
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.NAME_PRI, idInfo)).thenReturn(identityValue);
		identityValue.setLanguage("en");
		identityValue.setValue("Prem.Kumar4@mindtree.com");
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.EMAIL, idInfo)).thenReturn(identityValue);
		identityValue.setLanguage("en");
		identityValue.setValue("8056365346");
		Mockito.when(demoHelper.getEntityInfo(DemoMatchType.PHONE, idInfo)).thenReturn(identityValue);

		ReflectionTestUtils.invokeMethod(authFacadeImpl, "sendAuthNotification", authRequestDTO, 
				refId, authResponseDTO);
	}
	
	
	
	/**
	 * 
	 * 
	 * Test Method for AuthenticateTsp
	 * 
	 */
	@Test
	public void testAuthenticateTsp() {
		AuthRequestDTO authRequestDTO=new AuthRequestDTO();
		authRequestDTO.setTxnID("2345678");
		String resTime = new SimpleDateFormat(env.getProperty("datetime.pattern")).format(new Date());
		AuthResponseDTO authResponseTspDto = new AuthResponseDTO();
		authResponseTspDto.setStatus(STATUS_SUCCESS);
		authResponseTspDto.setErr(Collections.emptyList());
		authResponseTspDto.setResTime(resTime);
		authResponseTspDto.setTxnID(authRequestDTO.getTxnID());
		assertNotNull(authFacadeImpl.authenticateTsp(authRequestDTO));
	}

}
