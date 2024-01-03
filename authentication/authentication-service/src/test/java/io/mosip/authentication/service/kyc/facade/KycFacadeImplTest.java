/**
 * 
 */
package io.mosip.authentication.service.kyc.facade;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.impl.*;
import io.mosip.authentication.common.service.repository.IdentityBindingCertificateRepository;
import io.mosip.authentication.common.service.util.KeyBindedTokenMatcherUtil;
import io.mosip.authentication.core.indauth.dto.*;
import io.mosip.kernel.keymanagerservice.util.KeymanagerUtil;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.jose4j.jws.JsonWebSignature;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.manager.IdAuthFraudAnalysisEventManager;
import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.facade.AuthFacadeImpl;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.patrner.PartnerServiceImpl;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.TestObjectWithMetadata;
import io.mosip.authentication.common.service.validator.AuthFiltersValidator;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.idrepository.core.dto.AuthtypeStatus;
import reactor.util.function.Tuples;

import javax.security.auth.x500.X500Principal;

/**
 * @author Dinesh Karuppiah.T
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class})
@Import(EnvUtil.class)
public class KycFacadeImplTest {

	@InjectMocks
	private KycFacadeImpl kycFacade;

	@InjectMocks
	private AuthFacadeImpl authFacadeImpl;

	@InjectMocks
	private IdInfoHelper idInfoHelper;

	@Autowired
	EnvUtil env;

	@Mock
	private BioAuthServiceImpl bioAuthService;
	
	@Mock
	private OTPAuthServiceImpl otpAuthService;

	@Mock
	private TokenIdManager tokenIdManager;

	@Mock
	private KycService kycService;
	
	@Mock
	private AuditHelper auditHelper;
	
	@Mock
	private IdService<AutnTxn> idService;

	@Mock
	private IdaUinHashSaltRepo uinHashSaltRepo;
	
	@Mock
	private AuthtypeStatusImpl authTypeStatus;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Mock
	private PartnerServiceImpl partnerService;
	
	@Mock
	private NotificationService notificationService;
	
	@Mock
	private AuthFiltersValidator authFiltersValidator;
	
	@Mock
	private AuthTransactionHelper authTransactionHelper;
	
	@Mock
	private IdAuthSecurityManager securityManager;
	
	@Mock
	private IdAuthFraudAnalysisEventManager fraudEventManager;

	@InjectMocks
	private KeyBindedTokenAuthServiceImpl keyBindedTokenAuthService;

	@InjectMocks
	private MatchInputBuilder matchInputBuilder;

	@InjectMocks
	private IdInfoFetcherImpl idInfoFetcher;

	@InjectMocks
	private KeyBindedTokenMatcherUtil keyBindedTokenMatcherUtil;

	@Mock
	private IdentityBindingCertificateRepository identityBindingCertificateRepository;

	@Mock
	private KeymanagerUtil keymanagerUtil;

	private KeyPair keyPair;
	private String audienceId = "test-ida-binding";


	@Before
	public void beforeClass() throws NoSuchAlgorithmException {
		ReflectionTestUtils.setField(kycFacade, "authFacade", authFacadeImpl);
		ReflectionTestUtils.setField(kycFacade, "authFacade", authFacadeImpl);
		ReflectionTestUtils.setField(kycFacade, "idService", idService);
		ReflectionTestUtils.setField(kycFacade, "env", env);
		ReflectionTestUtils.setField(kycFacade, "mapper", mapper);
		ReflectionTestUtils.setField(kycFacade, "securityManager", securityManager);
		ReflectionTestUtils.setField(kycFacade, "partnerService", partnerService);
		ReflectionTestUtils.setField(authFacadeImpl, "securityManager", securityManager);
		ReflectionTestUtils.setField(authFacadeImpl, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(authFacadeImpl, "env", env);
		ReflectionTestUtils.setField(authFacadeImpl, "bioAuthService", bioAuthService);
		ReflectionTestUtils.setField(authFacadeImpl, "partnerService", partnerService);
		ReflectionTestUtils.setField(authFacadeImpl, "authFiltersValidator", authFiltersValidator);
		ReflectionTestUtils.setField(authFacadeImpl, "authTransactionHelper", authTransactionHelper);
		ReflectionTestUtils.setField(authFacadeImpl, "idService", idService);
		ReflectionTestUtils.setField(authFacadeImpl, "otpAuthService", otpAuthService);
		ReflectionTestUtils.setField(authFacadeImpl, "keyBindedTokenAuthService", keyBindedTokenAuthService);
		ReflectionTestUtils.setField(partnerService, "mapper", mapper);

		ReflectionTestUtils.setField(idInfoFetcher, "keyBindedTokenMatcherUtil", keyBindedTokenMatcherUtil);
		ReflectionTestUtils.setField(matchInputBuilder, "idInfoFetcher", idInfoFetcher);
		ReflectionTestUtils.setField(keyBindedTokenAuthService, "matchInputBuilder", matchInputBuilder);
		ReflectionTestUtils.setField(keyBindedTokenAuthService, "idInfoHelper", idInfoHelper);

		ReflectionTestUtils.setField(keyBindedTokenMatcherUtil, "audienceId", audienceId);

		KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
		gen.initialize(2048);
		keyPair = gen.generateKeyPair();
	}

	@Ignore
	@Test
	public void authenticateIndividualTokenTest() throws Exception {
		String partnerData = "{\"policyId\":\"21\",\"policyName\":\"policy 1635497343191\",\"policyDescription\":\"Auth Policy\",\"policyStatus\":true,\"partnerId\":\"1635497344579\",\"partnerName\":\"1635497344579\",\"certificateData\":\"data\",\"policyExpiresOn\":\"2022-12-11T06:12:52.994Z\",\"apiKeyExpiresOn\":\"2022-12-11T06:12:52.994Z\",\"mispExpiresOn\":\"2022-12-11T06:12:52.994Z\",\"policy\":{\"allowedAuthTypes\":[{\"authType\":\"keybindedtoken\",\"authSubType\":\"wla\",\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":\"\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":\"\",\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}],\"authTokenType\":\"Partner\"}}";
		PartnerPolicyResponseDTO partnerPolicyResponseDTO = mapper.readValue(partnerData, PartnerPolicyResponseDTO.class);
		Optional<PartnerPolicyResponseDTO> policyForPartner = Optional.of(partnerPolicyResponseDTO);

		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idRepo.put("response", response);
		HashMap<Object, Object> identity = new HashMap<>();
		identity.put("UIN", Long.valueOf(uin));
		response.put("identity", identity );
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);

		X509Certificate x509Certificate = getCertificate();
		String wlaToken = signJwt(uin, x509Certificate, true);

		KycAuthRequestDTO authRequestDTO = new KycAuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		KycRequestDTO requestDTO = new KycRequestDTO();
		KeyBindedTokenDTO keyBindedTokenDTO = new KeyBindedTokenDTO();
		keyBindedTokenDTO.setToken(wlaToken);
		keyBindedTokenDTO.setFormat("jwt");
		keyBindedTokenDTO.setType("WLA");
		requestDTO.setKeyBindedTokens(Arrays.asList(keyBindedTokenDTO));
		authRequestDTO.setRequest(requestDTO);

		HashMap<String, Object> reqMetadata = new HashMap<>();
		reqMetadata.put("AuthTransactionBuilder", AuthTransactionBuilder.newInstance());
		reqMetadata.put("123456"+"12345", partnerPolicyResponseDTO);
		authRequestDTO.setMetadata(reqMetadata);

		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getToken(idRepo)).thenReturn(uin);
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(securityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(securityManager.hash(Mockito.anyString())).thenReturn("id-vid-hash");
		Mockito.when(partnerService.getPolicyForPartner("123456","12345", authRequestDTO.getMetadata())).thenReturn(policyForPartner);
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString())).thenReturn("234567890");
		Mockito.when(keymanagerUtil.convertToCertificate(Mockito.anyString())).thenReturn(x509Certificate);

		List<Object[]> result = new ArrayList<>();
		result.add(new String[] {"cert-thumbprint", "WLA", getPemData(x509Certificate)});
		Mockito.when(identityBindingCertificateRepository.findAllByIdVidHashAndCertNotExpired(Mockito.anyString(), LocalDateTime.now())).thenReturn(result);
		assertEquals(true, kycFacade.authenticateIndividual(authRequestDTO, true, "123456", "12345", new TestObjectWithMetadata()).getResponse().isAuthStatus());
	}

	private X509Certificate getCertificate() throws Exception {
		X509V3CertificateGenerator generator = new X509V3CertificateGenerator();
		X500Principal dnName = new X500Principal("CN=Test");
		generator.setSubjectDN(dnName);
		generator.setIssuerDN(dnName); // use the same
		generator.setNotBefore(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
		generator.setNotAfter(new Date(System.currentTimeMillis() + 2 * 365 * 24 * 60 * 60 * 1000));
		generator.setPublicKey(keyPair.getPublic());
		generator.setSignatureAlgorithm("SHA256WITHRSA");
		generator.setSerialNumber(new BigInteger(String.valueOf(System.currentTimeMillis())));
		return generator.generate(keyPair.getPrivate());
	}

	private String getPemData(Object anyObject) throws IOException {
		StringWriter stringWriter = new StringWriter();
		try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
			pemWriter.writeObject(anyObject);
			pemWriter.flush();
			return stringWriter.toString();
		}
	}

	private String signJwt(String individualId, X509Certificate certificate, boolean addSha256Thumbprint) throws Exception {
		long epochInSeconds = ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond();
		JSONObject payload = new JSONObject();
		payload.put("iss", "test-app");
		payload.put("aud", audienceId);
		payload.put("sub", individualId);
		payload.put("iat", epochInSeconds);
		payload.put("exp", epochInSeconds+3600);

		JsonWebSignature jwSign = new JsonWebSignature();
		jwSign.setKeyIdHeaderValue(certificate.getSerialNumber().toString(10));
		if(addSha256Thumbprint) {
			jwSign.setX509CertSha256ThumbprintHeaderValue(certificate);
		}
		jwSign.setPayload(payload.toJSONString());
		jwSign.setAlgorithmHeaderValue("RS256");
		jwSign.setKey(keyPair.getPrivate());
		jwSign.setDoKeyValidation(false);
		return jwSign.getCompactSerialization();
	}

	@Test
	@Ignore
	public void authenticateIndividualTest() throws IdAuthenticationBusinessException, IdAuthenticationDaoException, Exception {
		String partnerData = "{\"policyId\":\"21\",\"policyName\":\"policy 1635497343191\",\"policyDescription\":\"Auth Policy\",\"policyStatus\":true,\"partnerId\":\"1635497344579\",\"partnerName\":\"1635497344579\",\"certificateData\":\"data\",\"policyExpiresOn\":\"2022-12-11T06:12:52.994Z\",\"apiKeyExpiresOn\":\"2022-12-11T06:12:52.994Z\",\"mispExpiresOn\":\"2022-12-11T06:12:52.994Z\",\"policy\":{\"allowedAuthTypes\":[{\"authType\":\"otp\",\"authSubType\":\"\",\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":\"\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":\"\",\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}],\"authTokenType\":\"Partner\"}}";
		PartnerPolicyResponseDTO partnerPolicyResponseDTO = mapper.readValue(partnerData, PartnerPolicyResponseDTO.class);
		Optional<PartnerPolicyResponseDTO> policyForPartner = Optional.of(partnerPolicyResponseDTO);

		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idRepo.put("response", response);
		HashMap<Object, Object> identity = new HashMap<>();
		identity.put("UIN", Long.valueOf(uin));
		response.put("identity", identity );
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		List<BioIdentityInfoDTO> bioDataList = new ArrayList<BioIdentityInfoDTO>();
		BioIdentityInfoDTO bioIdInfoDto1 = new BioIdentityInfoDTO();
		DataDTO dataDto1 = new DataDTO();
		dataDto1.setBioSubType("LEFT");
		dataDto1.setBioType("Iris");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("9149795");
		digitalId1.setMake("eyecool");
		dataDto1.setDigitalId(digitalId1);
		dataDto1.setDomainUri("dev.mosip.net");
		dataDto1.setPurpose("Registration");
		dataDto1.setQualityScore(70f);
		dataDto1.setRequestedScore(90f);
		bioIdInfoDto1.setData(dataDto1);
		bioIdInfoDto1.setHash("12341");
		bioIdInfoDto1.setSessionKey("Testsessionkey1");
		bioIdInfoDto1.setSpecVersion("Spec1.1.0");
		bioIdInfoDto1.setThumbprint("testvalue1");
		bioDataList.add(bioIdInfoDto1);
		
		IdentityDTO identitydto = new IdentityDTO();
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(bioDataList);
		requestDTO.setOtp("111111");
		authRequestDTO.setRequest(requestDTO);
		
		HashMap<String, Object> reqMetadata = new HashMap<>();
		reqMetadata.put("AuthTransactionBuilder", AuthTransactionBuilder.newInstance());
		reqMetadata.put("123456"+"12345", partnerPolicyResponseDTO);
		authRequestDTO.setMetadata(reqMetadata);
		
		EKycAuthResponseDTO authResponseDTO = new EKycAuthResponseDTO();
		EKycResponseDTO res = new EKycResponseDTO();
		res.setKycStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		HashMap<String, Object> resMetadata = new HashMap<>();
		resMetadata.put("IDENTITY_DATA", idRepo);
		HashMap<String, Object> idInfoMetadata = new HashMap<>();
		idInfoMetadata.put("identity", new ArrayList<>());
		IdentityInfoDTO regId = new IdentityInfoDTO();
		regId.setLanguage(null);
		regId.setValue("1234567890");
		List<IdentityInfoDTO> regIdList = new ArrayList<IdentityInfoDTO>();
		regIdList.add(regId);
		idInfoMetadata.put("registrationId", regIdList);
		IdentityInfoDTO uin1 = new IdentityInfoDTO();
		uin1.setLanguage(null);
		uin1.setValue("274390482564");
		List<IdentityInfoDTO> uinList = new ArrayList<IdentityInfoDTO>();
		uinList.add(uin1);
		idInfoMetadata.put("uin", uinList);
		resMetadata.put("IDENTITY_INFO", idInfoMetadata);
//		authResponseDTO.setMetadata(resMetadata);
		
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getToken(idRepo)).thenReturn(uin);
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(securityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(partnerService.getPolicyForPartner("123456","12345", authRequestDTO.getMetadata())).thenReturn(policyForPartner);
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString())).thenReturn("234567890");
		Mockito.when(otpAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(),"123456")).thenReturn(authStatusInfo);
		Mockito.when(bioAuthService.authenticate(authRequestDTO, uin, idInfo, "123456", true)).thenReturn(authStatusInfo);
		assertEquals(authResponseDTO.getResponse().isKycStatus(), kycFacade.authenticateIndividual(authRequestDTO, true, "123456", "12345", new TestObjectWithMetadata()).getResponse().isAuthStatus());
	}
	
	@Test
	@Ignore
	public void authenticateIndividualTest_kycAttribHasPhoto() throws IdAuthenticationBusinessException, IdAuthenticationDaoException, Exception {
		String partnerData = "{\"policyId\":\"21\",\"policyName\":\"policy 1635497343191\",\"policyDescription\":\"Auth Policy\",\"policyStatus\":true,\"partnerId\":\"1635497344579\",\"partnerName\":\"1635497344579\",\"certificateData\":\"data\",\"policyExpiresOn\":\"2022-12-11T06:12:52.994Z\",\"apiKeyExpiresOn\":\"2022-12-11T06:12:52.994Z\",\"mispExpiresOn\":\"2022-12-11T06:12:52.994Z\",\"policy\":{\"allowedAuthTypes\":[{\"authType\":\"otp\",\"authSubType\":\"\",\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":\"\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":\"\",\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}],\"authTokenType\":\"Partner\"}}";
		PartnerPolicyResponseDTO partnerPolicyResponseDTO = mapper.readValue(partnerData, PartnerPolicyResponseDTO.class);
		Optional<PartnerPolicyResponseDTO> policyForPartner = Optional.of(partnerPolicyResponseDTO);

		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idRepo.put("response", response);
		HashMap<Object, Object> identity = new HashMap<>();
		identity.put("UIN", Long.valueOf(uin));
		response.put("identity", identity );
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		
		EkycAuthRequestDTO authRequestDTO = new EkycAuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		List<BioIdentityInfoDTO> bioDataList = new ArrayList<BioIdentityInfoDTO>();
		BioIdentityInfoDTO bioIdInfoDto1 = new BioIdentityInfoDTO();
		DataDTO dataDto1 = new DataDTO();
		dataDto1.setBioSubType("LEFT");
		dataDto1.setBioType("Iris");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("9149795");
		digitalId1.setMake("eyecool");
		dataDto1.setDigitalId(digitalId1);
		dataDto1.setDomainUri("dev.mosip.net");
		dataDto1.setPurpose("Registration");
		dataDto1.setQualityScore(70f);
		dataDto1.setRequestedScore(90f);
		bioIdInfoDto1.setData(dataDto1);
		bioIdInfoDto1.setHash("12341");
		bioIdInfoDto1.setSessionKey("Testsessionkey1");
		bioIdInfoDto1.setSpecVersion("Spec1.1.0");
		bioIdInfoDto1.setThumbprint("testvalue1");
		bioDataList.add(bioIdInfoDto1);
		
		IdentityDTO identitydto = new IdentityDTO();
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(bioDataList);
		requestDTO.setOtp("111111");
		authRequestDTO.setRequest(requestDTO);
		
		HashMap<String, Object> reqMetadata = new HashMap<>();
		reqMetadata.put("AuthTransactionBuilder", AuthTransactionBuilder.newInstance());
		reqMetadata.put("123456"+"12345", partnerPolicyResponseDTO);
		authRequestDTO.setMetadata(reqMetadata);
		
		EKycAuthResponseDTO authResponseDTO = new EKycAuthResponseDTO();
		EKycResponseDTO res = new EKycResponseDTO();
		res.setKycStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		HashMap<String, Object> resMetadata = new HashMap<>();
		resMetadata.put("IDENTITY_DATA", idRepo);
		HashMap<String, Object> idInfoMetadata = new HashMap<>();
		idInfoMetadata.put("identity", new ArrayList<>());
		IdentityInfoDTO regId = new IdentityInfoDTO();
		regId.setLanguage(null);
		regId.setValue("1234567890");
		List<IdentityInfoDTO> regIdList = new ArrayList<IdentityInfoDTO>();
		regIdList.add(regId);
		idInfoMetadata.put("registrationId", regIdList);
		IdentityInfoDTO uin1 = new IdentityInfoDTO();
		uin1.setLanguage(null);
		uin1.setValue("274390482564");
		List<IdentityInfoDTO> uinList = new ArrayList<IdentityInfoDTO>();
		uinList.add(uin1);
		idInfoMetadata.put("uin", uinList);
		resMetadata.put("IDENTITY_INFO", idInfoMetadata);
//		authResponseDTO.setMetadata(resMetadata);
		
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getToken(idRepo)).thenReturn(uin);
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(securityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(partnerService.getPolicyForPartner("123456","12345", authRequestDTO.getMetadata())).thenReturn(policyForPartner);
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString())).thenReturn("234567890");
		Mockito.when(otpAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(),"123456")).thenReturn(authStatusInfo);
		Mockito.when(bioAuthService.authenticate(authRequestDTO, uin, idInfo, "123456", true)).thenReturn(authStatusInfo);
		assertEquals(authResponseDTO.getResponse().isKycStatus(), kycFacade.authenticateIndividual(authRequestDTO, true, "123456", "12345", new TestObjectWithMetadata()).getResponse().isAuthStatus());
	}
	
	@Test
	@Ignore
	public void kycAuthenticateIndividualTest() throws IdAuthenticationBusinessException, IdAuthenticationDaoException, Exception {
		String partnerData = "{\"policyId\":\"21\",\"policyName\":\"policy 1635497343191\",\"policyDescription\":\"Auth Policy\",\"policyStatus\":true,\"partnerId\":\"1635497344579\",\"partnerName\":\"1635497344579\",\"certificateData\":\"data\",\"policyExpiresOn\":\"2022-12-11T06:12:52.994Z\",\"apiKeyExpiresOn\":\"2022-12-11T06:12:52.994Z\",\"mispExpiresOn\":\"2022-12-11T06:12:52.994Z\",\"policy\":{\"allowedAuthTypes\":[{\"authType\":\"otp\",\"authSubType\":\"\",\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":\"\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":\"\",\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}],\"authTokenType\":\"Partner\"}}";
		PartnerPolicyResponseDTO partnerPolicyResponseDTO = mapper.readValue(partnerData, PartnerPolicyResponseDTO.class);
		Optional<PartnerPolicyResponseDTO> policyForPartner = Optional.of(partnerPolicyResponseDTO);

		Map<String, Object> idRepo = new HashMap<>();
		String uin = "274390482564";
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idRepo.put("response", response);
		HashMap<Object, Object> identity = new HashMap<>();
		identity.put("UIN", Long.valueOf(uin));
		response.put("identity", identity );
		AuthStatusInfo authStatusInfo = new AuthStatusInfo();
		authStatusInfo.setStatus(true);
		authStatusInfo.setErr(Collections.emptyList());
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		
		AuthRequestDTO authRequestDTO = new EkycAuthRequestDTO();
		authRequestDTO.setIndividualId("274390482564");
		authRequestDTO.setIndividualIdType(IdType.UIN.getType());
		authRequestDTO.setId("IDA");
		authRequestDTO.setTransactionID("1234567890");
		authRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		List<BioIdentityInfoDTO> bioDataList = new ArrayList<BioIdentityInfoDTO>();
		BioIdentityInfoDTO bioIdInfoDto1 = new BioIdentityInfoDTO();
		DataDTO dataDto1 = new DataDTO();
		dataDto1.setBioSubType("LEFT");
		dataDto1.setBioType("Iris");
		DigitalId digitalId1 = new DigitalId();
		digitalId1.setSerialNo("9149795");
		digitalId1.setMake("eyecool");
		dataDto1.setDigitalId(digitalId1);
		dataDto1.setDomainUri("dev.mosip.net");
		dataDto1.setPurpose("Registration");
		dataDto1.setQualityScore(70f);
		dataDto1.setRequestedScore(90f);
		bioIdInfoDto1.setData(dataDto1);
		bioIdInfoDto1.setHash("12341");
		bioIdInfoDto1.setSessionKey("Testsessionkey1");
		bioIdInfoDto1.setSpecVersion("Spec1.1.0");
		bioIdInfoDto1.setThumbprint("testvalue1");
		bioDataList.add(bioIdInfoDto1);
		
		IdentityDTO identitydto = new IdentityDTO();
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setDemographics(identitydto);
		requestDTO.setBiometrics(bioDataList);
		requestDTO.setOtp("111111");
		authRequestDTO.setRequest(requestDTO);
		
		HashMap<String, Object> reqMetadata = new HashMap<>();
		reqMetadata.put("AuthTransactionBuilder", AuthTransactionBuilder.newInstance());
		reqMetadata.put("123456"+"12345", partnerPolicyResponseDTO);
		authRequestDTO.setMetadata(reqMetadata);
		
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("234567890");
		authResponseDTO.setResponse(res);
//		authResponseDTO.setResponseTime(ZonedDateTime.now()
//				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		HashMap<String, Object> resMetadata = new HashMap<>();
		resMetadata.put("IDENTITY_DATA", idRepo);
		HashMap<String, Object> idInfoMetadata = new HashMap<>();
		idInfoMetadata.put("identity", new ArrayList<>());
		IdentityInfoDTO regId = new IdentityInfoDTO();
		regId.setLanguage(null);
		regId.setValue("1234567890");
		List<IdentityInfoDTO> regIdList = new ArrayList<IdentityInfoDTO>();
		regIdList.add(regId);
		idInfoMetadata.put("registrationId", regIdList);
		IdentityInfoDTO uin1 = new IdentityInfoDTO();
		uin1.setLanguage(null);
		uin1.setValue("274390482564");
		List<IdentityInfoDTO> uinList = new ArrayList<IdentityInfoDTO>();
		uinList.add(uin1);
		idInfoMetadata.put("uin", uinList);
		resMetadata.put("IDENTITY_INFO", idInfoMetadata);
//		authResponseDTO.setMetadata(resMetadata);
		
		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anySet())).thenReturn(idRepo);
		Mockito.when(idService.getToken(idRepo)).thenReturn(uin);
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(securityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(partnerService.getPolicyForPartner("123456","12345", authRequestDTO.getMetadata())).thenReturn(policyForPartner);
		Mockito.when(tokenIdManager.generateTokenId(Mockito.anyString(), Mockito.anyString())).thenReturn("234567890");
		Mockito.when(otpAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(),"123456")).thenReturn(authStatusInfo);
		Mockito.when(bioAuthService.authenticate(authRequestDTO, uin, idInfo, "123456", true)).thenReturn(authStatusInfo);
		assertEquals(authResponseDTO.getResponse(), kycFacade.authenticateIndividual(authRequestDTO, true, "123456", "12345", new TestObjectWithMetadata()).getResponse());
	}
	
	
	@Test
	@Ignore
	public void processKycAuthValid() throws IdAuthenticationBusinessException, JsonProcessingException {
		Map<String, Object> mapData = new HashMap<>();
		mapData.put("uin", "863537");
		
		EkycAuthRequestDTO kycAuthRequestDTO = new EkycAuthRequestDTO();
		Map<String, Object> kycReqMetadata = new HashMap<>();
		Set<String> langs = new HashSet<>();
		langs.add("eng");
		kycReqMetadata.put("KYC_LANGUAGES", langs);
		kycReqMetadata.put("PARTNER_CERTIFICATE", "certdata");
		kycAuthRequestDTO.setIndividualIdType(IdType.UIN.getType());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setTransactionID("1234567890");
		kycAuthRequestDTO.setMetadata(kycReqMetadata);
		
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
		RequestDTO request = new RequestDTO();
		kycAuthRequestDTO.setIndividualId("5134256294");
		request.setOtp("456789");
		request.setDemographics(idDTO);
		kycAuthRequestDTO.setRequest(request);
		
		EKycAuthResponseDTO kycAuthResponseDTO = new EKycAuthResponseDTO();
		EKycResponseDTO kycResponseDTO = new EKycResponseDTO();
		kycResponseDTO.setAuthToken("2345678");
		kycResponseDTO.setKycStatus(Boolean.TRUE);
		kycResponseDTO.setIdentity("id data");
		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthResponseDTO.setTransactionID("34567");
		kycAuthResponseDTO.setErrors(null);
		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthResponseDTO.setResponse(kycResponseDTO);
		
		Map<String, Object> authResMetadata = new HashMap<>();
		Map<String, Object> idData = new HashMap<>();
		String uin = "274390482564";
		idData.put("uin", uin);
		idData.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idData.put("response", response); 
		authResMetadata.put("IDENTITY_DATA",idData);
		authResMetadata.put("AutnTxn", new AutnTxn());
		HashMap<String, Object> idInfoMetadata = new HashMap<>();
		idInfoMetadata.put("identity", new ArrayList<>());
		IdentityInfoDTO regId = new IdentityInfoDTO();
		regId.setLanguage(null);
		regId.setValue("1234567890");
		List<IdentityInfoDTO> regIdList = new ArrayList<IdentityInfoDTO>();
		regIdList.add(regId);
		idInfoMetadata.put("registrationId", regIdList);
		IdentityInfoDTO uin1 = new IdentityInfoDTO();
		uin1.setLanguage(null);
		uin1.setValue("274390482564");
		List<IdentityInfoDTO> uinList = new ArrayList<IdentityInfoDTO>();
		uinList.add(uin1);
		idInfoMetadata.put("uin", uinList);
		authResMetadata.put("IDENTITY_INFO", idInfoMetadata);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("2345678");
		authResponseDTO.setResponse(res);
//		authResponseDTO.setResponseTime(ZonedDateTime.now()
//				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authResponseDTO.setErrors(null);
		authResponseDTO.setTransactionID("123456789");
		authResponseDTO.setVersion("1.0");
//		authResponseDTO.setMetadata(authResMetadata);

		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet())).thenReturn(mapData);
		Mockito.when(securityManager.encryptData(Mockito.any(), Mockito.any())).thenReturn(Tuples.of("", "",""));
		Mockito.when(kycService.retrieveKycInfo(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(kycResponseDTO);
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(securityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(authTypeStatus.fetchAuthtypeStatus(Mockito.anyString())).thenReturn(new ArrayList<AuthtypeStatus>());
		Mockito.when(idService.getToken(idData)).thenReturn(uin);
		assertEquals(kycAuthResponseDTO.getResponse(),kycFacade.processEKycAuth(kycAuthRequestDTO, authResponseDTO, "123456", authResMetadata).getResponse());
	}
	
	@Test
	@Ignore
	public void processKycAuth_EncryptionException() throws IdAuthenticationBusinessException, JsonProcessingException {
		Map<String, Object> mapData = new HashMap<>();
		mapData.put("uin", "863537");
		
		EkycAuthRequestDTO kycAuthRequestDTO = new EkycAuthRequestDTO();
		Map<String, Object> kycReqMetadata = new HashMap<>();
		Set<String> langs = new HashSet<>();
		langs.add("eng");
		kycReqMetadata.put("KYC_LANGUAGES", langs);
		kycReqMetadata.put("PARTNER_CERTIFICATE", "certdata");
		kycAuthRequestDTO.setIndividualIdType(IdType.UIN.getType());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setTransactionID("1234567890");
		kycAuthRequestDTO.setMetadata(kycReqMetadata);
		
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
		RequestDTO request = new RequestDTO();
		kycAuthRequestDTO.setIndividualId("5134256294");
		request.setOtp("456789");
		request.setDemographics(idDTO);
		kycAuthRequestDTO.setRequest(request);
		
		EKycAuthResponseDTO kycAuthResponseDTO = new EKycAuthResponseDTO();
		EKycResponseDTO kycResponseDTO = new EKycResponseDTO();
		kycResponseDTO.setAuthToken("2345678");
		kycResponseDTO.setKycStatus(Boolean.TRUE);
		kycResponseDTO.setIdentity("id data");
		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthResponseDTO.setTransactionID("34567");
		kycAuthResponseDTO.setErrors(null);
		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthResponseDTO.setResponse(kycResponseDTO);
		
		Map<String, Object> authResMetadata = new HashMap<>();
		Map<String, Object> idData = new HashMap<>();
		String uin = "274390482564";
		idData.put("uin", uin);
		idData.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idData.put("response", response); 
		authResMetadata.put("IDENTITY_DATA",idData);
		authResMetadata.put("AutnTxn", new AutnTxn());
		HashMap<String, Object> idInfoMetadata = new HashMap<>();
		idInfoMetadata.put("identity", new ArrayList<>());
		IdentityInfoDTO regId = new IdentityInfoDTO();
		regId.setLanguage(null);
		regId.setValue("1234567890");
		List<IdentityInfoDTO> regIdList = new ArrayList<IdentityInfoDTO>();
		regIdList.add(regId);
		idInfoMetadata.put("registrationId", regIdList);
		IdentityInfoDTO uin1 = new IdentityInfoDTO();
		uin1.setLanguage(null);
		uin1.setValue("274390482564");
		List<IdentityInfoDTO> uinList = new ArrayList<IdentityInfoDTO>();
		uinList.add(uin1);
		idInfoMetadata.put("uin", uinList);
		authResMetadata.put("IDENTITY_INFO", idInfoMetadata);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("2345678");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authResponseDTO.setErrors(null);
		authResponseDTO.setTransactionID("123456789");
		authResponseDTO.setVersion("1.0");
//		authResponseDTO.setMetadata(authResMetadata);

		Mockito.when(idService.processIdType(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean(), Mockito.anySet())).thenReturn(mapData);
		Mockito.when(securityManager.encryptData(Mockito.any(), Mockito.any())).thenReturn(Tuples.of("", "",""));
		Mockito.when(kycService.retrieveKycInfo(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(kycResponseDTO);
		Mockito.when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("2344");
		Mockito.when(securityManager.getUser()).thenReturn("ida_app_user");
		Mockito.when(authTypeStatus.fetchAuthtypeStatus(Mockito.anyString())).thenReturn(new ArrayList<AuthtypeStatus>());
		Mockito.when(idService.getToken(idData)).thenReturn(uin);
		when(securityManager.encryptData(Mockito.any(), anyString())).thenThrow(new IdAuthenticationBusinessException());
		try {
			kycFacade.processEKycAuth(kycAuthRequestDTO, authResponseDTO, "123456", authResMetadata);
		} catch (IdAuthenticationBusinessException e) {
			assertEquals(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), e.getErrorCode());
		}
	}

	@Test(expected = IdAuthenticationBusinessException.class)
	@Ignore
	public void processKycAuthException1() throws IdAuthenticationBusinessException, JsonProcessingException {
		EkycAuthRequestDTO kycAuthRequestDTO = new EkycAuthRequestDTO();
		Map<String, Object> kycReqMetadata = new HashMap<>();
		Set<String> langs = new HashSet<>();
		langs.add("eng");
		kycReqMetadata.put("KYC_LANGUAGES", langs);
		kycAuthRequestDTO.setIndividualIdType(IdType.UIN.getType());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setVersion("1.1");
		kycAuthRequestDTO.setRequestTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthRequestDTO.setId("id");
		kycAuthRequestDTO.setTransactionID("1234567890");
		kycAuthRequestDTO.setMetadata(kycReqMetadata);
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
		RequestDTO request = new RequestDTO();
		request.setOtp("456789");
		request.setDemographics(idDTO);
		kycAuthRequestDTO.setIndividualId("5134256294");
		kycAuthRequestDTO.setRequest(request);
		
		EKycAuthResponseDTO kycAuthResponseDTO = new EKycAuthResponseDTO();
		EKycResponseDTO kycResponseDTO = new EKycResponseDTO();
		kycResponseDTO.setAuthToken("2345678");
		kycResponseDTO.setKycStatus(Boolean.TRUE);
		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthResponseDTO.setTransactionID("34567");
		kycAuthResponseDTO.setErrors(null);
		kycAuthResponseDTO.setResponseTime(ZonedDateTime.now().format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		kycAuthResponseDTO.setResponse(kycResponseDTO);
		
		Map<String, Object> authResMetadata = new HashMap<>();
		Map<String, Object> idData = new HashMap<>();
		String uin = "274390482564";
		idData.put("uin", uin);
		idData.put("registrationId", "1234567890");
		HashMap<Object, Object> response = new HashMap<>();
		idData.put("response", response); 
		authResMetadata.put("IDENTITY_DATA",idData);
		authResMetadata.put("AutnTxn", new AutnTxn());
		HashMap<String, Object> idInfoMetadata = new HashMap<>();
		idInfoMetadata.put("identity", new ArrayList<>());
		IdentityInfoDTO regId = new IdentityInfoDTO();
		regId.setLanguage(null);
		regId.setValue("1234567890");
		List<IdentityInfoDTO> regIdList = new ArrayList<IdentityInfoDTO>();
		regIdList.add(regId);
		idInfoMetadata.put("registrationId", regIdList);
		IdentityInfoDTO uin1 = new IdentityInfoDTO();
		uin1.setLanguage(null);
		uin1.setValue("274390482564");
		List<IdentityInfoDTO> uinList = new ArrayList<IdentityInfoDTO>();
		uinList.add(uin1);
		idInfoMetadata.put("uin", uinList);
		authResMetadata.put("IDENTITY_INFO", idInfoMetadata);
		AuthResponseDTO authResponseDTO = new AuthResponseDTO();
		ResponseDTO res = new ResponseDTO();
		res.setAuthStatus(Boolean.TRUE);
		res.setAuthToken("2345678");
		authResponseDTO.setResponse(res);
		authResponseDTO.setResponseTime(ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(EnvUtil.getDateTimePattern())).toString());
		authResponseDTO.setErrors(null);
		authResponseDTO.setTransactionID("123456789");
		authResponseDTO.setVersion("1.0");
//		authResponseDTO.setMetadata(authResMetadata);
		
		Mockito.when(kycService.retrieveKycInfo(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(kycResponseDTO);
		Mockito.when(idService.getIdHash(Mockito.any())).thenReturn("Zld6TjJjNllKYzExNjBFUUZrbmdzYnJMelRJQ1BY");
		doThrow(new IDDataValidationException()).when(auditHelper).audit((AuditModules) any(),
				(AuditEvents) any(), anyString(), (IdType) any(), anyString());
		kycFacade.processEKycAuth(kycAuthRequestDTO, authResponseDTO, "123456", authResMetadata);
	}

}
