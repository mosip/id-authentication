package io.mosip.authentication.common.service.facade;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.common.service.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.AuthtypeStatusImpl;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.impl.match.DemoMatchType;
import io.mosip.authentication.common.service.impl.notification.NotificationServiceImpl;
import io.mosip.authentication.common.service.integration.IdTemplateManager;
import io.mosip.authentication.common.service.integration.NotificationManager;
import io.mosip.authentication.common.service.integration.OTPManager;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.repository.ApiKeyDataRepository;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.repository.MispLicenseDataRepository;
import io.mosip.authentication.common.service.repository.PartnerDataRepository;
import io.mosip.authentication.common.service.repository.PartnerMappingRepository;
import io.mosip.authentication.common.service.repository.PolicyDataRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.TestObjectWithMetadata;
import io.mosip.authentication.common.service.validator.AuthFiltersValidator;
import io.mosip.authentication.core.constant.AuthTokenType;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.EkycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.indauth.dto.ResponseDTO;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.partner.dto.PolicyDTO;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.service.BioAuthService;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.idrepository.core.dto.AuthtypeStatus;
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
@Import(EnvUtil.class)
@TestPropertySource("classpath:application.properties")
public class AuthFacadeImplTest {


	/** The auth facade impl. */
	@InjectMocks
	private AuthFacadeImpl authFacadeImpl;

	@Mock
	private AuthFacadeImpl authFacadeMock;

	/** The env. */
	@Autowired
	private EnvUtil env;

	/** The otp auth service impl. */
	@Mock
	private OTPAuthService otpAuthService;

	/** The IdAuthService */
	@Mock
	private IdService<AutnTxn> idService;
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
	private OTPManager otpManager;

	@Mock
	private BioAuthService bioAuthService;

	@Mock
	private AutnTxnRepository autntxnrepository;

	@Mock
	private TokenIdManager tokenIdManager;

	@Mock
	private IdaUinHashSaltRepo uinHashSaltRepo;

	@Mock
	private IdAuthSecurityManager idAuthSecurityManager;

	@Mock
	private AuthtypeStatusImpl authTypeStatus;

	@Mock
	private AuthTransactionHelper authTransactionHelper;

	@Mock
	PartnerService partnerService;

	@Autowired
	ObjectMapper mapper;

	@Mock
	private PartnerMappingRepository partnerMappingRepo;

	@Mock
	private PartnerDataRepository partnerDataRepo;

	@Mock
	private PolicyDataRepository policyDataRepo;

	@Mock
	private ApiKeyDataRepository apiKeyRepo;

	@Mock
	private MispLicenseDataRepository mispLicDataRepo;

	@Mock
	private AuthFiltersValidator authFiltersValidator;

	/**
	 * Before.
	 */
	@Before
	public void before() {
		ReflectionTestUtils.setField(authFacadeImpl, "otpAuthService", otpAuthService);
		ReflectionTestUtils.setField(authFacadeImpl, "tokenIdManager", tokenIdManager);
		ReflectionTestUtils.setField(authFacadeImpl, "securityManager", idAuthSecurityManager);
		ReflectionTestUtils.setField(authFacadeImpl, "bioAuthService", bioAuthService);
		ReflectionTestUtils.setField(authFacadeImpl, "authTransactionHelper", authTransactionHelper);
		ReflectionTestUtils.setField(authFacadeImpl, "env", env);
		ReflectionTestUtils.setField(authFacadeImpl, "notificationService", notificationService);
		ReflectionTestUtils.setField(notificationService, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(notificationService, "notificationManager", notificationManager);
		ReflectionTestUtils.setField(authFacadeImpl, "partnerService", partnerService);

		EnvUtil.setAuthTokenRequired(true);
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
		authRequestDTO.setRequestTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
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
		authRequestDTO.setMetadata(Collections.singletonMap(AuthTransactionBuilder.class.getSimpleName(),
				AuthTransactionBuilder.newInstance()));
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idRepo.put("response", response);
		HashMap<Object, Object> identity = new HashMap<>();
		identity.put("UIN", Long.valueOf(uin));
		response.put("identity", identity);
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(otpAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
				.thenReturn(authStatusInfo);
		Mockito.when(idService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(),
				Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anySet()))
				.thenReturn(repoDetails());
		// Mockito.when(IdInfoFetcher.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(idService.getToken(idRepo)).thenReturn(uin);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());

		// Mockito.when(IdInfoFetcher.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn("247334310780728918141754192454591343");
		Mockito.when(bioAuthService.authenticate(Mockito.any(), Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(), Mockito.anyBoolean()))
		.thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenReturn("test");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(authTypeStatus.fetchAuthtypeStatus(Mockito.anyString()))
				.thenReturn(new ArrayList<AuthtypeStatus>());
		AuthResponseDTO authenticateIndividual = authFacadeImpl.authenticateIndividual(authRequestDTO, true, "123456", "12345", true,
				new TestObjectWithMetadata());
		assertTrue(authenticateIndividual.getResponse().isAuthStatus());

	}
	
	@Test
	public void authenticateIndividualTest_AuthTokenRequiredFalse() throws IdAuthenticationBusinessException, IdAuthenticationDaoException,
			NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException {

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
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
		authRequestDTO.setMetadata(Collections.singletonMap(AuthTransactionBuilder.class.getSimpleName(),
				AuthTransactionBuilder.newInstance()));
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idRepo.put("response", response);
		HashMap<Object, Object> identity = new HashMap<>();
		identity.put("UIN", Long.valueOf(uin));
		response.put("identity", identity);
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(otpAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
				.thenReturn(authStatusInfo);
		Mockito.when(idService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(),
				Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anySet()))
				.thenReturn(repoDetails());
		// Mockito.when(IdInfoFetcher.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(idService.getToken(idRepo)).thenReturn(uin);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());

		// Mockito.when(IdInfoFetcher.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn("247334310780728918141754192454591343");
		Mockito.when(bioAuthService.authenticate(Mockito.any(), Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(), Mockito.anyBoolean()))
		.thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenReturn("test");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(authTypeStatus.fetchAuthtypeStatus(Mockito.anyString()))
				.thenReturn(new ArrayList<AuthtypeStatus>());
		
		EnvUtil.setAuthTokenRequired(false);
		AuthResponseDTO authenticateIndividual = authFacadeImpl.authenticateIndividual(authRequestDTO, true, "123456", "12345", true,
				new TestObjectWithMetadata());
		assertTrue(authenticateIndividual.getResponse().isAuthStatus());

	}
	@Test
	public void authenticateIndividualTest_AuthTokenTypeRandom() throws IdAuthenticationBusinessException, IdAuthenticationDaoException,
	NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
	InvocationTargetException, IOException {
		
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
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
		authRequestDTO.setMetadata(Collections.singletonMap(AuthTransactionBuilder.class.getSimpleName(),
				AuthTransactionBuilder.newInstance()));
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idRepo.put("response", response);
		HashMap<Object, Object> identity = new HashMap<>();
		identity.put("UIN", Long.valueOf(uin));
		response.put("identity", identity);
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(otpAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
		.thenReturn(authStatusInfo);
		Mockito.when(idService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(),
				Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anySet()))
		.thenReturn(repoDetails());
		// Mockito.when(IdInfoFetcher.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(idService.getToken(idRepo)).thenReturn(uin);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		
		// Mockito.when(IdInfoFetcher.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString()))
		.thenReturn("247334310780728918141754192454591343");
		Mockito.when(bioAuthService.authenticate(Mockito.any(), Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(), Mockito.anyBoolean()))
		.thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any(), Mockito.any()))
		.thenReturn("test");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(authTypeStatus.fetchAuthtypeStatus(Mockito.anyString()))
		.thenReturn(new ArrayList<AuthtypeStatus>());
		
		String authTokenType = AuthTokenType.RANDOM.getType();
		PartnerPolicyResponseDTO parnerPolicyRespDTo = new PartnerPolicyResponseDTO();
		PolicyDTO policy = new PolicyDTO();
		policy.setAuthTokenType(authTokenType);
		parnerPolicyRespDTo.setPolicy(policy);
		Mockito.when(partnerService.getPolicyForPartner(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(Optional.of(parnerPolicyRespDTo));
		AuthResponseDTO authenticateIndividual = authFacadeImpl.authenticateIndividual(authRequestDTO, true, "123456", "12345", true,
				new TestObjectWithMetadata());
		assertTrue(authenticateIndividual.getResponse().isAuthStatus());
		
	}
	
	@Test
	public void authenticateIndividualTest_AuthTokenTypePartner() throws IdAuthenticationBusinessException, IdAuthenticationDaoException,
	NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
	InvocationTargetException, IOException {
		
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
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
		authRequestDTO.setMetadata(Collections.singletonMap(AuthTransactionBuilder.class.getSimpleName(),
				AuthTransactionBuilder.newInstance()));
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idRepo.put("response", response);
		HashMap<Object, Object> identity = new HashMap<>();
		identity.put("UIN", Long.valueOf(uin));
		response.put("identity", identity);
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(otpAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
		.thenReturn(authStatusInfo);
		Mockito.when(idService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(),
				Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anySet()))
		.thenReturn(repoDetails());
		// Mockito.when(IdInfoFetcher.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(idService.getToken(idRepo)).thenReturn(uin);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		
		// Mockito.when(IdInfoFetcher.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString()))
		.thenReturn("247334310780728918141754192454591343");
		Mockito.when(bioAuthService.authenticate(Mockito.any(), Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(), Mockito.anyBoolean()))
		.thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any(), Mockito.any()))
		.thenReturn("test");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(authTypeStatus.fetchAuthtypeStatus(Mockito.anyString()))
		.thenReturn(new ArrayList<AuthtypeStatus>());
		
		String authTokenType =  AuthTokenType.PARTNER.getType();
		PartnerPolicyResponseDTO parnerPolicyRespDTo = new PartnerPolicyResponseDTO();
		PolicyDTO policy = new PolicyDTO();
		policy.setAuthTokenType(authTokenType);
		parnerPolicyRespDTo.setPolicy(policy);
		Mockito.when(partnerService.getPolicyForPartner(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(Optional.of(parnerPolicyRespDTo));
		AuthResponseDTO authenticateIndividual = authFacadeImpl.authenticateIndividual(authRequestDTO, true, "123456", "12345", true,
				new TestObjectWithMetadata());
		assertTrue(authenticateIndividual.getResponse().isAuthStatus());
		
	}
	
	@Test
	public void authenticateIndividualTest_AuthTokenTypePolicy() throws IdAuthenticationBusinessException, IdAuthenticationDaoException,
	NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
	InvocationTargetException, IOException {
		
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
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
		authRequestDTO.setMetadata(Collections.singletonMap(AuthTransactionBuilder.class.getSimpleName(),
				AuthTransactionBuilder.newInstance()));
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idRepo.put("response", response);
		HashMap<Object, Object> identity = new HashMap<>();
		identity.put("UIN", Long.valueOf(uin));
		response.put("identity", identity);
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(otpAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
		.thenReturn(authStatusInfo);
		Mockito.when(idService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(),
				Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anySet()))
		.thenReturn(repoDetails());
		// Mockito.when(IdInfoFetcher.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(idService.getToken(idRepo)).thenReturn(uin);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		
		// Mockito.when(IdInfoFetcher.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString()))
		.thenReturn("247334310780728918141754192454591343");
		Mockito.when(bioAuthService.authenticate(Mockito.any(), Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(), Mockito.anyBoolean()))
		.thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any(), Mockito.any()))
		.thenReturn("test");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(authTypeStatus.fetchAuthtypeStatus(Mockito.anyString()))
		.thenReturn(new ArrayList<AuthtypeStatus>());
		
		String authTokenType =  AuthTokenType.POLICY.getType();
		PartnerPolicyResponseDTO parnerPolicyRespDTo = new PartnerPolicyResponseDTO();
		PolicyDTO policy = new PolicyDTO();
		policy.setAuthTokenType(authTokenType);
		parnerPolicyRespDTo.setPolicy(policy);
		Mockito.when(partnerService.getPolicyForPartner(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(Optional.of(parnerPolicyRespDTo));
		AuthResponseDTO authenticateIndividual = authFacadeImpl.authenticateIndividual(authRequestDTO, true, "123456", "12345", true,
				new TestObjectWithMetadata());
		assertTrue(authenticateIndividual.getResponse().isAuthStatus());
		
	}
	@Test
	public void authenticateIndividualTest_AuthTokenTypePolicyGroup() throws IdAuthenticationBusinessException, IdAuthenticationDaoException,
	NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
	InvocationTargetException, IOException {
		
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
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
		authRequestDTO.setMetadata(Collections.singletonMap(AuthTransactionBuilder.class.getSimpleName(),
				AuthTransactionBuilder.newInstance()));
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idRepo.put("response", response);
		HashMap<Object, Object> identity = new HashMap<>();
		identity.put("UIN", Long.valueOf(uin));
		response.put("identity", identity);
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(otpAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
		.thenReturn(authStatusInfo);
		Mockito.when(idService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(),
				Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anySet()))
		.thenReturn(repoDetails());
		// Mockito.when(IdInfoFetcher.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(idService.getToken(idRepo)).thenReturn(uin);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		
		// Mockito.when(IdInfoFetcher.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString()))
		.thenReturn("247334310780728918141754192454591343");
		Mockito.when(bioAuthService.authenticate(Mockito.any(), Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(), Mockito.anyBoolean()))
		.thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any(), Mockito.any()))
		.thenReturn("test");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(authTypeStatus.fetchAuthtypeStatus(Mockito.anyString()))
		.thenReturn(new ArrayList<AuthtypeStatus>());
		
		String authTokenType =  AuthTokenType.POLICY_GROUP.getType();
		PartnerPolicyResponseDTO parnerPolicyRespDTo = new PartnerPolicyResponseDTO();
		PolicyDTO policy = new PolicyDTO();
		policy.setAuthTokenType(authTokenType);
		parnerPolicyRespDTo.setPolicy(policy);
		Mockito.when(partnerService.getPolicyForPartner(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap())).thenReturn(Optional.of(parnerPolicyRespDTo));
		AuthResponseDTO authenticateIndividual = authFacadeImpl.authenticateIndividual(authRequestDTO, true, "123456", "12345", true,
				new TestObjectWithMetadata());
		assertTrue(authenticateIndividual.getResponse().isAuthStatus());
		
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void authenticateIndividualTest_exception() throws IdAuthenticationBusinessException, IdAuthenticationDaoException,
	NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
	InvocationTargetException, IOException {
		
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		
		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
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
		authRequestDTO.setMetadata(Collections.singletonMap(AuthTransactionBuilder.class.getSimpleName(),
				AuthTransactionBuilder.newInstance()));
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idRepo.put("response", response);
		HashMap<Object, Object> identity = new HashMap<>();
		identity.put("UIN", Long.valueOf(uin));
		response.put("identity", identity);
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(otpAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
		.thenReturn(authStatusInfo);
		Mockito.when(idService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(),
				Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anySet()))
		.thenReturn(repoDetails());
		// Mockito.when(IdInfoFetcher.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(idService.getToken(idRepo)).thenReturn(uin);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		
		// Mockito.when(IdInfoFetcher.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString()))
		.thenReturn("247334310780728918141754192454591343");
		Mockito.when(bioAuthService.authenticate(authRequestDTO, uin, idInfo, "123456", true))
		.thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any(), Mockito.any()))
		.thenReturn("test");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(authTypeStatus.fetchAuthtypeStatus(Mockito.anyString()))
		.thenReturn(new ArrayList<AuthtypeStatus>());
		
		Mockito.doThrow(new IdAuthenticationFilterException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS))
				.when(authFiltersValidator).validateAuthFilters(Mockito.any(), Mockito.any(), Mockito.any());
		AuthResponseDTO authenticateIndividual = authFacadeImpl.authenticateIndividual(authRequestDTO, true, "123456", "12345", true,
				new TestObjectWithMetadata());
		
	}
	
	@Test
	public void internalAuthenticateIndividualTest() throws IdAuthenticationBusinessException, IdAuthenticationDaoException,
			NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException {

		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
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
		authRequestDTO.setMetadata(Collections.singletonMap(AuthTransactionBuilder.class.getSimpleName(),
				AuthTransactionBuilder.newInstance()));
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idRepo.put("response", response);
		HashMap<Object, Object> identity = new HashMap<>();
		identity.put("UIN", Long.valueOf(uin));
		response.put("identity", identity);
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(bioAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
				.thenReturn(authStatusInfo);
		Mockito.when(idService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(),
				Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anySet()))
				.thenReturn(repoDetails());
		// Mockito.when(IdInfoFetcher.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(idService.getToken(idRepo)).thenReturn(uin);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());

		// Mockito.when(IdInfoFetcher.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn("247334310780728918141754192454591343");
		Mockito.when(bioAuthService.authenticate(Mockito.any(), Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenReturn("test");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(authTypeStatus.fetchAuthtypeStatus(Mockito.anyString()))
				.thenReturn(new ArrayList<AuthtypeStatus>());
		AuthResponseDTO authenticateIndividual = authFacadeImpl.authenticateIndividual(authRequestDTO, false, "123456", "12345", true,
				new TestObjectWithMetadata());
		assertTrue(authenticateIndividual.getResponse().isAuthStatus());

	}
	
	@Test
	public void kycAuthenticateIndividualTest() throws IdAuthenticationBusinessException, IdAuthenticationDaoException,
			NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException {

		EkycAuthRequestDTO authRequestDTO = new EkycAuthRequestDTO();
		authRequestDTO.setAllowedKycAttributes(List.of("fullName", "photo"));
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
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
		authRequestDTO.setMetadata(Collections.singletonMap(AuthTransactionBuilder.class.getSimpleName(),
				AuthTransactionBuilder.newInstance()));
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idRepo.put("response", response);
		HashMap<Object, Object> identity = new HashMap<>();
		identity.put("UIN", Long.valueOf(uin));
		response.put("identity", identity);
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(otpAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
				.thenReturn(authStatusInfo);
		Mockito.when(idService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(),
				Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anySet()))
				.thenReturn(repoDetails());
		// Mockito.when(IdInfoFetcher.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(idService.getToken(idRepo)).thenReturn(uin);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());

		// Mockito.when(IdInfoFetcher.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn("247334310780728918141754192454591343");
		Mockito.when(bioAuthService.authenticate(Mockito.any(), Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(), Mockito.anyBoolean()))
		.thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenReturn("test");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(authTypeStatus.fetchAuthtypeStatus(Mockito.anyString()))
				.thenReturn(new ArrayList<AuthtypeStatus>());
		AuthResponseDTO authenticateIndividual = authFacadeImpl.authenticateIndividual(authRequestDTO, true, "123456", "12345", true,
				new TestObjectWithMetadata());
		assertTrue(authenticateIndividual.getResponse().isAuthStatus());

	}
	
	@Test
	public void kycAuthenticateIndividualTest_no_photo() throws IdAuthenticationBusinessException, IdAuthenticationDaoException,
			NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, IOException {

		EkycAuthRequestDTO authRequestDTO = new EkycAuthRequestDTO();
		authRequestDTO.setAllowedKycAttributes(List.of("fullName"));
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
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
		authRequestDTO.setMetadata(Collections.singletonMap(AuthTransactionBuilder.class.getSimpleName(),
				AuthTransactionBuilder.newInstance()));
		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idRepo.put("response", response);
		HashMap<Object, Object> identity = new HashMap<>();
		identity.put("UIN", Long.valueOf(uin));
		response.put("identity", identity);
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(otpAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
				.thenReturn(authStatusInfo);
		Mockito.when(idService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(),
				Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anySet()))
				.thenReturn(repoDetails());
		// Mockito.when(IdInfoFetcher.getIdInfo(Mockito.any())).thenReturn(idInfo);
		Mockito.when(idService.getToken(idRepo)).thenReturn(uin);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());

		// Mockito.when(IdInfoFetcher.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn("247334310780728918141754192454591343");
		Mockito.when(bioAuthService.authenticate(Mockito.any(), Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(), Mockito.anyBoolean()))
		.thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenReturn("test");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(authTypeStatus.fetchAuthtypeStatus(Mockito.anyString()))
				.thenReturn(new ArrayList<AuthtypeStatus>());
		AuthResponseDTO authenticateIndividual = authFacadeImpl.authenticateIndividual(authRequestDTO, true, "123456", "12345", true,
				new TestObjectWithMetadata());
		assertTrue(authenticateIndividual.getResponse().isAuthStatus());

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
		authRequestDTO.setId("1234567");

		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setId("id");
		authRequestDTO.setVersion("1.1");
		IdentityInfoDTO idInfoDTO = new IdentityInfoDTO();
		idInfoDTO.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO.setValue("John");
		IdentityInfoDTO idInfoDTO1 = new IdentityInfoDTO();
		idInfoDTO1.setLanguage(EnvUtil.getMandatoryLanguages());
		idInfoDTO1.setValue("Mike");
		List<IdentityInfoDTO> idInfoList = new ArrayList<>();
		idInfoList.add(idInfoDTO);
		idInfoList.add(idInfoDTO1);
		authRequestDTO.setTransactionID("1234567890");
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		List<AuthStatusInfo> authStatusList = ReflectionTestUtils.invokeMethod(authFacadeImpl, "processAuthType",
				authRequestDTO, idInfo, "1233", true, "247334310780728918141754192454591343", "123456",
				AuthTransactionBuilder.newInstance(), "Zld6TjJjNllKYzExNjBFUUZrbmdzYnJMelRJQ1BY");

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
		authRequestDTO.setRequestTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
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
		fingerValue.setData(dataDTOFinger);
		BioIdentityInfoDTO fingerValue2 = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger2 = new DataDTO();
		dataDTOFinger2.setBioValue("");
		dataDTOFinger2.setBioSubType("Thumb");
		dataDTOFinger2.setBioType(BioAuthType.FGR_IMG.getType());
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
		authRequestDTO.setMetadata(Collections.singletonMap("metadata", "{}"));
		Mockito.when(otpAuthService.authenticate(authRequestDTO, "1242", Collections.emptyMap(), "123456"))
				.thenReturn(AuthStatusInfoBuilder.newInstance().setStatus(true).build());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
		List<AuthStatusInfo> authStatusList = ReflectionTestUtils.invokeMethod(authFacadeImpl, "processAuthType",
				authRequestDTO, idInfo, "1242", true, "247334310780728918141754192454591343", "123456",
				AuthTransactionBuilder.newInstance(), "Zld6TjJjNllKYzExNjBFUUZrbmdzYnJMelRJQ1BY");
		assertTrue(authStatusList.stream().anyMatch(status -> status.isStatus()));
	}

	@Test
	public void processAuthTypeTestFailure() throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("1234567");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());

		RequestDTO reqDTO = new RequestDTO();
		reqDTO.setOtp("456789");
		authRequestDTO.setRequest(reqDTO);
		authRequestDTO.setId("1234567");
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(otpAuthService.authenticate(authRequestDTO, "1242", Collections.emptyMap(), "123456"))
				.thenReturn(AuthStatusInfoBuilder.newInstance().setStatus(true).build());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		List<AuthStatusInfo> authStatusList = ReflectionTestUtils.invokeMethod(authFacadeImpl, "processAuthType",
				authRequestDTO, idInfo, "1242", false, "247334310780728918141754192454591343", "123456",
				AuthTransactionBuilder.newInstance(), "Zld6TjJjNllKYzExNjBFUUZrbmdzYnJMelRJQ1BY");
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

	@Test
	public void testProcessBioAuthType() throws IdAuthenticationBusinessException, IOException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
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
		String token = "2743904825641";
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
		Mockito.when(otpAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
				.thenReturn(authStatusInfo);
		Mockito.when(idService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(),
				Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anySet()))
				.thenReturn(repoDetails());
		// Mockito.when(IdInfoFetcher.getIdInfo(Mockito.any())).thenReturn(idInfo);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);

		authResponseDTO.setResponseTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());

		// Mockito.when(IdInfoFetcher.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
//		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString()))
//				.thenReturn("247334310780728918141754192454591343");
		Mockito.when(bioAuthService.authenticate(authRequestDTO, uin, idInfo, "123456")).thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenReturn("test");
		AuthTransactionBuilder authTxnBuilder = AuthTransactionBuilder.newInstance();
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "saveAndAuditBioAuthTxn", authRequestDTO, token, IdType.UIN,
				true, "247334310780728918141754192454591343", true, "123", authTxnBuilder, "Zld6TjJjNllKYzExNjBFUUZrbmdzYnJMelRJQ1BY");
	}

	@Test
	public void testProcessBioAuthTypeFinImg() throws IdAuthenticationBusinessException, IOException {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());

		BioIdentityInfoDTO fingerValue = new BioIdentityInfoDTO();
		DataDTO dataDTOFinger = new DataDTO();
		dataDTOFinger.setBioValue("finger");
		dataDTOFinger.setBioSubType("Thumb");
		dataDTOFinger.setBioType(BioAuthType.FGR_IMG.getType());
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
		Mockito.when(otpAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(), "123456"))
				.thenReturn(authStatusInfo);
		Mockito.when(idService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean(), Mockito.anyBoolean(),
				Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anySet()))
				.thenReturn(repoDetails());
		// Mockito.when(IdInfoFetcher.getIdInfo(Mockito.any())).thenReturn(idInfo);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);

		authResponseDTO.setResponseTime(
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());

		// Mockito.when(IdInfoFetcher.getIdInfo(repoDetails())).thenReturn(idInfo);
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.NAME, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo)).thenReturn("mosip");
		Mockito.when(idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo)).thenReturn("mosip");
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn("247334310780728918141754192454591343");
		Mockito.when(bioAuthService.authenticate(authRequestDTO, uin, idInfo, "123456")).thenReturn(authStatusInfo);
		Mockito.when(idTemplateManager.applyTemplate(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenReturn("test");
		AuthTransactionBuilder authTxnBuilder = AuthTransactionBuilder.newInstance();
		ReflectionTestUtils.invokeMethod(authFacadeImpl, "saveAndAuditBioAuthTxn", authRequestDTO, "123", IdType.UIN,
				true, "247334310780728918141754192454591343", true, "1234", authTxnBuilder, "Zld6TjJjNllKYzExNjBFUUZrbmdzYnJMelRJQ1BY");
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidOTPviaAuth() throws Throwable {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		RequestDTO request = new RequestDTO();
		request.setOtp("111111");
		authRequestDTO.setRequest(request);
		authRequestDTO.setIndividualId("794138547620");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setMetadata(Collections.singletonMap("metadata", "{}"));
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(otpAuthService.authenticate(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED));
		try {
			ReflectionTestUtils.invokeMethod(authFacadeImpl, "processOTPAuth", authRequestDTO, "863537", true,
					authStatusList, IdType.UIN, "247334310780728918141754192454591343", "123456",
					AuthTransactionBuilder.newInstance(), "Zld6TjJjNllKYzExNjBFUUZrbmdzYnJMelRJQ1BY");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}

	}

	@Test(expected = IdAuthenticationBusinessException.class)
	public void TestInvalidOTPviaAuthwithActionMessage() throws Throwable {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("794138547620");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setIndividualId("426789089018");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authRequestDTO.setMetadata(Collections.singletonMap("metadata", "{}"));
		RequestDTO request = new RequestDTO();
		request.setOtp("111111");
		authRequestDTO.setRequest(request);
		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		Mockito.when(otpAuthService.authenticate(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN));
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(idAuthSecurityManager.getUser()).thenReturn("ida_app_user");
		try {
			ReflectionTestUtils.invokeMethod(authFacadeImpl, "processOTPAuth", authRequestDTO, "863537", true,
					authStatusList, IdType.UIN, "247334310780728918141754192454591343", "123456",
					AuthTransactionBuilder.newInstance(), "Zld6TjJjNllKYzExNjBFUUZrbmdzYnJMelRJQ1BY");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}

	}

	private Map<String, Object> repoDetails() {
		Map<String, Object> map = new HashMap<>();
		map.put("uin", "863537");
		return map;
	}
}
