package io.mosip.authentication.common.service.facade;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
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
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.helper.RestHelperImpl;
import io.mosip.authentication.common.service.impl.AuthtypeStatusImpl;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.impl.notification.NotificationServiceImpl;
import io.mosip.authentication.common.service.integration.IdRepoManager;
import io.mosip.authentication.common.service.integration.IdTemplateManager;
import io.mosip.authentication.common.service.integration.NotificationManager;
import io.mosip.authentication.common.service.integration.OTPManager;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.repository.UinEncryptSaltRepo;
import io.mosip.authentication.common.service.repository.UinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthTransactionManager;
import io.mosip.authentication.core.authtype.dto.AuthtypeStatus;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.service.BioAuthService;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.authentication.core.spi.indauth.service.PinAuthService;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

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
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, TemplateManagerBuilderImpl.class })

public class AuthFacadeImplTest {

	private static final String MOSIP_SECONDARY_LANGUAGE = "mosip.secondary-language";
	private static final String MOSIP_PRIMARY_LANGUAGE = "mosip.primary-language";
	/** The auth facade impl. */
	@InjectMocks
	private AuthFacadeImpl authFacadeImpl;
	@Mock
	private AuthFacadeImpl authFacadeMock;
	/** The env. */
	@Autowired
	private Environment env;

	/** The otp auth service impl. */
	@Mock
	private OTPAuthService otpAuthServiceImpl;
	/** The IdAuthService */
	@Mock
	private IdService<AutnTxn> idAuthService;
	/** The KycService **/
	@Mock
	private KycService kycService;

	@Mock
	private AuditHelper auditHelper;

	/** The IdInfoHelper **/
	@Mock
	private IdInfoHelper idInfoHelper;

	@Mock
	private IdInfoFetcher idInfoFetcher;

	/** The IdRepoService **/
	@Mock
	private IdService<?> idInfoService;
	/** The DemoAuthService **/
	@Mock
	private DemoAuthService demoAuthService;

	@Mock
	private IDAMappingConfig idMappingConfig;

	@InjectMocks
	NotificationServiceImpl notificationService;

	@Mock
	NotificationManager notificationManager;

	@Mock
	private IdTemplateManager idTemplateManager;

	@InjectMocks
	private RestRequestFactory restRequestFactory;

	@InjectMocks
	private RestHelperImpl restHelper;

	@InjectMocks
	private OTPManager otpManager;

	@Mock
	private BioAuthService bioAuthService;

	@Mock
	private IdRepoManager idRepoManager;

	@Mock
	private AutnTxnRepository autntxnrepository;

	@Mock
	private PinAuthService pinAuthService;

	@Mock
	private TokenIdManager tokenIdManager;
	
	@Mock
	private UinEncryptSaltRepo uinEncryptSaltRepo;
	
	@Mock
	private UinHashSaltRepo uinHashSaltRepo;
	
	@Mock
	private IdAuthTransactionManager idAuthTransactionManager;
	
	@Mock
	private AuthtypeStatusImpl authTypeStatus;

	/**
	 * Before.
	 */
	@Before
	public void before() {
		ReflectionTestUtils.setField(authFacadeImpl, "otpService", otpAuthServiceImpl);
		ReflectionTestUtils.setField(authFacadeImpl, "tokenIdManager", tokenIdManager);
		ReflectionTestUtils.setField(authFacadeImpl, "uinEncryptSaltRepo", uinEncryptSaltRepo);
		ReflectionTestUtils.setField(authFacadeImpl, "uinHashSaltRepo", uinHashSaltRepo);
		ReflectionTestUtils.setField(authFacadeImpl, "transactionManager", idAuthTransactionManager);
		ReflectionTestUtils.setField(authFacadeImpl, "authTypeStatusService", authTypeStatus);
		ReflectionTestUtils.setField(authFacadeImpl, "pinAuthService", pinAuthService);
		ReflectionTestUtils.setField(authFacadeImpl, "bioAuthService", bioAuthService);
		ReflectionTestUtils.setField(authFacadeImpl, "env", env);
		ReflectionTestUtils.setField(restRequestFactory, "env", env);
		ReflectionTestUtils.setField(authFacadeImpl, "notificationService", notificationService);
		ReflectionTestUtils.setField(notificationService, "env", env);
		ReflectionTestUtils.setField(notificationService, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(notificationService, "notificationManager", notificationManager);
		ReflectionTestUtils.setField(idInfoHelper, "environment", env);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idMappingConfig);
	}

	/**
	 * This class tests the authenticateIndividual method where it checks the IdType
	 * and DemoAuthType.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 * @throws IdAuthenticationDaoException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */

	@Test
	public void authenticateIndividualTest() throws IdAuthenticationBusinessException, IdAuthenticationDaoException,
			NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException {

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setRequestedAuth(authType);

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
		dataDTOFinger.setDeviceProviderID("1234567890");
		fingerValue.setData(dataDTOFinger);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTOIris = new DataDTO();
		dataDTOIris.setBioValue("iris img");
		dataDTOIris.setBioSubType("left");
		dataDTOIris.setBioType(BioAuthType.IRIS_IMG.getType());
		irisValue.setData(dataDTOIris);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFace = new DataDTO();
		dataDTOFace.setBioValue("face img");
		dataDTOFace.setBioSubType("Thumb");
		dataDTOFace.setBioType(BioAuthType.FACE_IMG.getType());
		faceValue.setData(dataDTOFace);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(otpAuthServiceImpl.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
				.thenReturn(authStatusInfo);
		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(idRepo);
		Mockito.when(idAuthService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(repoDetails());
		Mockito.when(idInfoService.getIdInfo(Mockito.any())).thenReturn(idInfo);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setStaticToken("234567890");
		authResponseDTO.setResponse(res);

		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());

		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn("247334310780728918141754192454591343");
		Mockito.when(bioAuthService.authenticate(authRequestDTO, uin, idInfo, "123456")).thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any())).thenReturn("test");
		Mockito.when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthTransactionManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(authTypeStatus.fetchAuthtypeStatus(Mockito.anyString(), Mockito.anyString())).thenReturn(new ArrayList<AuthtypeStatus>());
		authFacadeImpl.authenticateIndividual(authRequestDTO, true, "123456");

	}

	/**
	 * This class tests the processAuthType (OTP) method where otp validation
	 * failed.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */

	@Test
	public void processAuthTypeTestFail() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setOtp(false);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		authRequestDTO.setId("1234567");

		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("1.1");
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(env.getProperty(MOSIP_PRIMARY_LANGUAGE));
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(env.getProperty(MOSIP_SECONDARY_LANGUAGE));
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		authRequestDTO.setRequest(reqDTO);
		authRequestDTO.setTransactionID("1234567890");
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		List<AuthStatusInfo> authStatusList = ReflectionTestUtils.invokeMethod(authFacadeImpl, "processAuthType",
				authRequestDTO, idInfo, "1233", true, "247334310780728918141754192454591343", "123456");

		assertTrue(authStatusList.stream().noneMatch(status -> status.isStatus()));
	}

	/**
	 * This class tests the processAuthType (OTP) method where otp validation gets
	 * successful.
	 *
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Test
	public void processAuthTypeTestSuccess() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("1234567");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setOtp(true);
		authTypeDTO.setBio(true);
		authTypeDTO.setDemo(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage("EN");
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage("fre");
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		IdentityDTO idDTO = new IdentityDTO();
		idDTO.setName(idInfoList);
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
		dataDTOFinger.setDeviceProviderID("1234567890");
		fingerValue.setData(dataDTOFinger);
		BioIdentityInfoDTO fingerValue2 = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger2 = new DataDTO();
		dataDTOFinger2.setBioValue("");
		dataDTOFinger2.setBioSubType("Thumb");
		dataDTOFinger2.setBioType(BioAuthType.FGR_IMG.getType());
		dataDTOFinger2.setDeviceProviderID("1234567890");
		fingerValue2.setData(dataDTOFinger2);
		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(fingerValue2);

		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setDemographics(idDTO);
		reqDTO.setBiometrics(fingerIdentityInfoDtoList);
		reqDTO.setOtp("456789");
		authRequestDTO.setRequest(reqDTO);
		authRequestDTO.setId("1234567");
		Mockito.when(otpAuthServiceImpl.authenticate(authRequestDTO, "1242", Collections.emptyMap(), "123456"))
				.thenReturn(AuthStatusInfoBuilder.newInstance().setStatus(true).build());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthTransactionManager.getUser()).thenReturn("ida_app_user");
		List<AuthStatusInfo> authStatusList = ReflectionTestUtils.invokeMethod(authFacadeImpl, "processAuthType",
				authRequestDTO, idInfo, "1242", true, "247334310780728918141754192454591343", "123456");
		assertTrue(authStatusList.stream().anyMatch(status -> status.isStatus()));
	}

	@Test
	public void processAuthTypeTestFailure() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("1234567");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setOtp(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);

		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setOtp("456789");
		authRequestDTO.setRequest(reqDTO);
		authRequestDTO.setId("1234567");
		Mockito.when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthTransactionManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(otpAuthServiceImpl.authenticate(authRequestDTO, "1242", Collections.emptyMap(), "123456"))
				.thenReturn(AuthStatusInfoBuilder.newInstance().setStatus(true).build());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		List<AuthStatusInfo> authStatusList = ReflectionTestUtils.invokeMethod(authFacadeImpl, "processAuthType",
				authRequestDTO, idInfo, "1242", false, "247334310780728918141754192454591343", "123456");
		assertTrue(authStatusList.stream().anyMatch(status -> status.isStatus()));
	}

//	@Test
//	public void testGetAuditEvent() {
//		ReflectionTestUtils.invokeMethod(authFacadeImpl, "getAuditEvent", true);
//	}

//	@Test
//	public void testGetAuditEventInternal() {
//		ReflectionTestUtils.invokeMethod(authFacadeImpl, "getAuditEvent", false);
//	}

	@Ignore
	@Test
	public void testProcessBioAuthType() throws IdAuthenticationBusinessException, IOException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setRequestedAuth(authType);

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
		dataDTOFinger.setDeviceProviderID("1234567890");
		fingerValue.setData(dataDTOFinger);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTOIris = new DataDTO();
		dataDTOIris.setBioValue("iris img");
		dataDTOIris.setBioSubType("left");
		dataDTOIris.setBioType(BioAuthType.IRIS_IMG.getType());
		irisValue.setData(dataDTOIris);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFace = new DataDTO();
		dataDTOFace.setBioValue("face img");
		dataDTOFace.setBioSubType("Thumb");
		dataDTOFace.setBioType(BioAuthType.FACE_IMG.getType());
		faceValue.setData(dataDTOFace);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(otpAuthServiceImpl.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
				.thenReturn(authStatusInfo);
		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(idRepo);
		Mockito.when(idAuthService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(repoDetails());
		Mockito.when(idInfoService.getIdInfo(Mockito.any())).thenReturn(idInfo);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setStaticToken("234567890");
		authResponseDTO.setResponse(res);

		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());

		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn("247334310780728918141754192454591343");
		Mockito.when(bioAuthService.authenticate(authRequestDTO, uin, idInfo, "123456")).thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any())).thenReturn("test");
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "saveAndAuditBioAuthTxn", authRequestDTO, true, uin,
				IdType.UIN, true, "247334310780728918141754192454591343");
	}

	@Ignore
	@Test
	public void testProcessBioAuthTypeFinImg() throws IdAuthenticationBusinessException, IOException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(true);
		authRequestDTO.setRequestedAuth(authType);

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
		dataDTOFinger.setDeviceProviderID("1234567890");
		fingerValue.setData(dataDTOFinger);
		BioIdentityInfoDTO irisValue = new BioIdentityInfoDTO();
		DataDTO dataDTOIris = new DataDTO();
		dataDTOIris.setBioValue("iris img");
		dataDTOIris.setBioSubType("left");
		dataDTOIris.setBioType(BioAuthType.IRIS_IMG.getType());
		irisValue.setData(dataDTOIris);
		BioIdentityInfoDTO faceValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFace = new DataDTO();
		dataDTOFace.setBioValue("face img");
		dataDTOFace.setBioSubType("Thumb");
		dataDTOFace.setBioType(BioAuthType.FACE_IMG.getType());
		faceValue.setData(dataDTOFace);

		List<BioIdentityInfoDTO> fingerIdentityInfoDtoList = new ArrayList<BioIdentityInfoDTO>();
		fingerIdentityInfoDtoList.add(fingerValue);
		fingerIdentityInfoDtoList.add(irisValue);
		fingerIdentityInfoDtoList.add(faceValue);

		IdentityDTO identitydto = new IdentityDTO();

		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(fingerIdentityInfoDtoList);
		authRequestDTO.setRequest(requestDTO);
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(otpAuthServiceImpl.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
				.thenReturn(authStatusInfo);
		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(idRepo);
		Mockito.when(idAuthService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(repoDetails());
		Mockito.when(idInfoService.getIdInfo(Mockito.any())).thenReturn(idInfo);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setStaticToken("234567890");
		authResponseDTO.setResponse(res);

		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());

		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn("247334310780728918141754192454591343");
		Mockito.when(bioAuthService.authenticate(authRequestDTO, uin, idInfo, "123456")).thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any())).thenReturn("test");
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "saveAndAuditBioAuthTxn", authRequestDTO, true, uin,
				IdType.UIN, true, "247334310780728918141754192454591343");
	}

	@Test
	public void testProcessPinDetails_pinValidationStatusNull() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("mosip.identity.auth");
		String uin = "794138547620";
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPin(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String pin = null;
		RequestDTO request = new RequestDTO();
		authRequestDTO.setIndividualId("5134256294");
		request.setStaticPin(pin);
		authRequestDTO.setRequest(request);
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(idAuthService.processIdType(IdType.UIN.getType(), uin, false)).thenReturn(idRepo);
		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(repoDetails());

		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		AuthStatusInfo pinValidationStatus = new AuthStatusInfo();
		pinValidationStatus.setStatus(true);
		pinValidationStatus.setErr(Collections.emptyList());
		Mockito.when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthTransactionManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(pinAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
				.thenReturn(pinValidationStatus);
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "processPinAuth", authRequestDTO, uin, authStatusList,
				IdType.UIN, "247334310780728918141754192454591343", "123456");

	}

	@Test
	public void testProcessPinDetails_pinValidationStatusNotNull() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("mosip.identity.auth");
		String uin = "794138547620";
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPin(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String pin = "456789";
		RequestDTO request = new RequestDTO();
		authRequestDTO.setIndividualId("5134256294");
		request.setStaticPin(pin);
		authRequestDTO.setRequest(request);
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(idAuthService.processIdType(IdType.UIN.getType(), uin, false)).thenReturn(idRepo);
		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(repoDetails());

		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		AuthStatusInfo pinValidationStatus = new AuthStatusInfo();
		pinValidationStatus.setStatus(true);
		pinValidationStatus.setErr(Collections.emptyList());
		Mockito.when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthTransactionManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(pinAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
				.thenReturn(pinValidationStatus);
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "processPinAuth", authRequestDTO, uin, authStatusList,
				IdType.UIN, "247334310780728918141754192454591343", "123456");

	}

	@Test
	public void testProcessPinDetails_pinValidationStatus_false() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("794138547620");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("mosip.identity.auth");
		String uin = "794138547620";
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		AuthTypeDTO authTypeDTO = new AuthTypeDTO();
		authTypeDTO.setPin(true);
		authRequestDTO.setRequestedAuth(authTypeDTO);
		String pin = "456789";
		RequestDTO request = new RequestDTO();
		authRequestDTO.setIndividualId("5134256294");
		request.setStaticPin(pin);
		authRequestDTO.setRequest(request);
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(idAuthService.processIdType(IdType.UIN.getType(), uin, false)).thenReturn(idRepo);
		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(repoDetails());

		Mockito.when(idInfoService.getIdInfo(repoDetails())).thenReturn(idInfo);
		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		AuthStatusInfo pinValidationStatus = new AuthStatusInfo();
		pinValidationStatus.setStatus(true);
		pinValidationStatus.setErr(Collections.emptyList());
		pinValidationStatus.setStatus(false);
		pinValidationStatus.setErr(Collections.emptyList());
		Mockito.when(pinAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
				.thenReturn(pinValidationStatus);
		Mockito.when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthTransactionManager.getUser()).thenReturn("ida_app_user");
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "processPinAuth", authRequestDTO, uin, authStatusList,
				IdType.UIN, "247334310780728918141754192454591343", "123456");

	}

	@Test
	public void TestInvalidOTPviaAuth() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("794138547620");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setOtp(true);
		authRequestDTO.setRequestedAuth(requestedAuth);
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		Mockito.when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthTransactionManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(otpAuthServiceImpl.authenticate(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED));
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "processOTPAuth", authRequestDTO, "863537", true,
				authStatusList, IdType.UIN, "247334310780728918141754192454591343", "123456");

	}

	@Test
	public void TestInvalidOTPviaAuthwithActionMessage() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("794138547620");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		AuthTypeDTO requestedAuth = new AuthTypeDTO();
		requestedAuth.setOtp(true);
		authRequestDTO.setIndividualId("426789089018");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		authRequestDTO.setRequestedAuth(requestedAuth);
		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		Mockito.when(otpAuthServiceImpl.authenticate(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN));
		Mockito.when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthTransactionManager.getUser()).thenReturn("ida_app_user");
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "processOTPAuth", authRequestDTO, "863537", true,
				authStatusList, IdType.UIN, "247334310780728918141754192454591343", "123456");

	}

	private Map<String, Object> repoDetails() {
		Map<String, Object> map = new HashMap<>();
		map.put("uin", "863537");
		return map;
	}
}
