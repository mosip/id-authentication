package io.mosip.authentication.service.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.authentication.common.service.cache.MasterDataCache;
import io.mosip.authentication.common.service.cache.PartnerServiceCache;
import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.integration.KeyManager;
import io.mosip.authentication.common.service.repository.AuthLockRepository;
import io.mosip.authentication.common.service.repository.AutnTxnRepository;
import io.mosip.authentication.common.service.repository.IdentityCacheRepository;
import io.mosip.authentication.common.service.repository.OtpTxnRepository;
import io.mosip.authentication.common.service.repository.StaticPinRepository;
import io.mosip.authentication.common.service.repository.UinEncryptSaltRepo;
import io.mosip.authentication.common.service.repository.UinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.DataDTO;
import io.mosip.authentication.core.indauth.dto.DigitalId;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.RequestDTO;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.authentication.service.IdAuthenticationTestApplication;
import io.mosip.kernel.biosdk.provider.factory.BioAPIFactory;
import io.mosip.kernel.biosdk.provider.spi.iBioProviderApi;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.cryptomanager.service.impl.CryptomanagerServiceImpl;
import io.mosip.kernel.cryptomanager.util.CryptomanagerUtils;
import io.mosip.kernel.idgenerator.tokenid.impl.TokenIdGeneratorImpl;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
import io.mosip.kernel.keymanager.hsm.impl.KeyStoreImpl;
import io.mosip.kernel.keymanagerservice.helper.KeymanagerDBHelper;
import io.mosip.kernel.keymanagerservice.service.impl.KeymanagerServiceImpl;
import io.mosip.kernel.keymanagerservice.util.KeymanagerUtil;
import io.mosip.kernel.partnercertservice.helper.PartnerCertManagerDBHelper;
import io.mosip.kernel.partnercertservice.service.impl.PartnerCertificateManagerServiceImpl;
import io.mosip.kernel.pinvalidator.impl.PinValidatorImpl;
import io.mosip.kernel.signature.service.impl.SignatureServiceImpl;
import io.mosip.kernel.zkcryptoservice.service.impl.ZKCryptoManagerServiceImpl;

/**
 * Resident IdAuth controller test class.
 * 
 * @author Ritik Jain
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = IdAuthenticationTestApplication.class)
@AutoConfigureMockMvc
public class IdAuthControllerTest {
	

	@MockBean
	@Qualifier("selfTokenRestTemplate")
	private RestTemplate residentRestTemplate;
	
	@Mock
	private PartnerService partnerService;
	
	@SpyBean
	private AuthTransactionHelper authTransactionHelper;
	
	/** The auth request validator. */
	@SpyBean
	private AuthRequestValidator authRequestValidator;

	/** The auth facade. */
	@SpyBean
	private AuthFacade authFacade;
	
	@MockBean
	private AuditHelper auditHelper;
	
	@SpyBean
	private IdTypeUtil idTypeUtil;

	@InjectMocks
	private AuthController authController;
	
	/** The master data cache. */
	@MockBean
	private MasterDataCache masterDataCache;

	/** The partner service cache. */
	@MockBean
	private PartnerServiceCache partnerServiceCache;
	
	@MockBean
	private UinValidatorImpl uinValidator;
	
	@MockBean
	private VidValidatorImpl vidValidator;
	
	@MockBean
	private IdAuthSecurityManager idAuthSecurityManager;
	
	@MockBean
	private OtpTxnRepository otpTxnRepository;
	
	@MockBean
	private PinValidatorImpl pinValidatorImpl;
	
	@MockBean
	private AutnTxnRepository autnTxnRepository;
	
	@MockBean
	private IdentityCacheRepository identityCacheRepository;
	
	@MockBean
	private StaticPinRepository staticPinRepository;
	
	@MockBean
	private AuthLockRepository authLockRepository;
	
	@MockBean
	private UinEncryptSaltRepo uinEncryptSaltRepo;
	
	@MockBean
	private UinHashSaltRepo uinHashSaltRepo;
	
	@SpyBean 
	private SimpleCacheManager cacheManager;
	
	@MockBean
	private KeyManager keyManager;
	
	@MockBean
	private CryptomanagerServiceImpl cryptomanagerServiceImpl;
	
	@MockBean
	private KeyStoreImpl keyStoreImpl;
	
	@MockBean
	private KeymanagerUtil keymanagerUtil;
	
	@MockBean
	private KeymanagerServiceImpl keymanagerServiceImpl;
	
	@MockBean
	private CryptomanagerUtils cryptomanagerUtils;
	
	@MockBean
	private ZKCryptoManagerServiceImpl zkCryptoManagerServiceImpl;
	
	@MockBean
	private SignatureServiceImpl signatureServiceImpl;
	
	@MockBean
	private KeymanagerDBHelper keymanagerDBHelper;
	
	@MockBean
	private PartnerCertificateManagerServiceImpl partnerCertificateManagerServiceImpl;
	
	@MockBean
	private PartnerCertManagerDBHelper partnerCertManagerDBHelper;
	
	@MockBean
	private ThreadPoolTaskScheduler threadPoolTaskScheduler;
	
	@MockBean
	private TokenIdGeneratorImpl tokenIdGeneratorImpl;
	
	@MockBean
	private BioAPIFactory bioAPIFactory;
	
	@MockBean
	private NotificationService notificationService;
	
	@Autowired
	private Environment env;

	@Autowired
	private MockMvc mockMvc;
	
	Gson gson = new GsonBuilder().serializeNulls().create();

	String reqJson;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
	}

	@Test
	public void testBiometricAuthenticationFingerImage() throws Exception {
		Boolean authStatus = true;
		AuthRequestDTO authReqDto = new AuthRequestDTO();
		String requestTime = DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime());
		authReqDto.setRequestTime(requestTime);
		authReqDto.setTransactionID("1234567890");
		authReqDto.setDomainUri("http://dummy.domain");
		authReqDto.setEnv("Staging");
		authReqDto.setId(env.getProperty("mosip.ida.api.ids.auth"));
		AuthTypeDTO authTypeDto = new AuthTypeDTO();
		authTypeDto.setBio(true);
		authReqDto.setRequestedAuth(authTypeDto);
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setTimestamp(requestTime);
		List<BioIdentityInfoDTO> bios = new ArrayList<>();
		BioIdentityInfoDTO bio1 = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		data.setTimestamp(requestTime);
		data.setBioType("Finger");
		data.setBioSubType("Right Thumb");
		data.setBioValue("cmlnaHQgdGh1bWI=");
		
		data.setDomainUri("http://dummy.domain");
		data.setEnv("Staging");
		DigitalId digitalId = new DigitalId();
		digitalId.setDateTime(requestTime);
		digitalId.setDeviceProvider("TEST");
		digitalId.setDeviceProviderId("TEST.TCI322");
		digitalId.setDeviceSubType("Right Thumb");
		digitalId.setMake("eyecool");
		digitalId.setModel("TESTMODEL");
		digitalId.setType("Finger");
		digitalId.setSerialNo("123456");
		data.setDigitalId(digitalId);
		data.setDeviceCode("3f1dc192-9595-4d48-a2ed-7407760ba917");
		data.setDeviceServiceVersion("SB.WIN.001");
		
		authReqDto.setConsentObtained(true);
		bio1.setData(data);
		bios.add(bio1);
		requestDTO.setBiometrics(bios);
		authReqDto.setRequest(requestDTO);
		String individualId = "9876543210";
		authReqDto.setIndividualId(individualId);
		reqJson = gson.toJson(authReqDto);
		Mockito.when(idAuthSecurityManager.hash(individualId)).thenReturn(individualId);
		Mockito.when(identityCacheRepository.existsById(individualId)).thenReturn(true);
		IdentityEntity identityValue = new IdentityEntity();
		Map<String, Object> bioMap = new HashMap<>();
		bioMap.put("Finger_Right Thumb", "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n"
				+ "<BIR xmlns=\"http://standards.iso.org/iso-iec/19785/-3/ed-2/\">\r\n"
				+ "    <BIRInfo>\r\n"
				+ "        <Integrity>false</Integrity>\r\n"
				+ "    </BIRInfo>\r\n"
				+ "    <BIR>\r\n"
				+ "        <Version>\r\n"
				+ "            <Major>1</Major>\r\n"
				+ "            <Minor>1</Minor>\r\n"
				+ "        </Version>\r\n"
				+ "        <CBEFFVersion>\r\n"
				+ "            <Major>1</Major>\r\n"
				+ "            <Minor>1</Minor>\r\n"
				+ "        </CBEFFVersion>\r\n"
				+ "        <BIRInfo>\r\n"
				+ "            <Integrity>false</Integrity>\r\n"
				+ "        </BIRInfo>\r\n"
				+ "        <BDBInfo>\r\n"
				+ "            <Index>11d5c6dc-1bc2-4eda-a7bf-4a3639b29e01</Index>\r\n"
				+ "            <Format>\r\n"
				+ "                <Organization>Mosip</Organization>\r\n"
				+ "                <Type>7</Type>\r\n"
				+ "            </Format>\r\n"
				+ "            <CreationDate>2021-08-11T10:32:48.124194800Z</CreationDate>\r\n"
				+ "            <Type>Finger</Type>\r\n"
				+ "            <Subtype>Right Thumb</Subtype>\r\n"
				+ "            <Level>Raw</Level>\r\n"
				+ "            <Purpose>Enroll</Purpose>\r\n"
				+ "            <Quality>\r\n"
				+ "                <Algorithm>\r\n"
				+ "                    <Organization>HMAC</Organization>\r\n"
				+ "                    <Type>SHA-256</Type>\r\n"
				+ "                </Algorithm>\r\n"
				+ "                <Score>80</Score>\r\n"
				+ "            </Quality>\r\n"
				+ "        </BDBInfo>\r\n"
				+ "        <BDB>cmlnaHQgdGh1bWI=</BDB>\r\n"
				+ "        <SB>cmlnaHQgdGh1bWI=</SB>\r\n"
				+ "        <Others>\r\n"
				+ "            <Key>EXCEPTION</Key>\r\n"
				+ "            <Value>false</Value>\r\n"
				+ "        </Others>\r\n"
				+ "        <Others>\r\n"
				+ "            <Key>RETRIES</Key>\r\n"
				+ "            <Value>1</Value>\r\n"
				+ "        </Others>\r\n"
				+ "        <Others>\r\n"
				+ "            <Key>SDK_SCORE</Key>\r\n"
				+ "            <Value>0.0</Value>\r\n"
				+ "        </Others>\r\n"
				+ "        <Others>\r\n"
				+ "            <Key>FORCE_CAPTURED</Key>\r\n"
				+ "            <Value>false</Value>\r\n"
				+ "        </Others>\r\n"
				+ "        <Others>\r\n"
				+ "            <Key>SPEC_VERSION</Key>\r\n"
				+ "            <Value>0.9.5</Value>\r\n"
				+ "        </Others>\r\n"
				+ "    </BIR>\r\n"
				+ "</BIR>\r\n"
				+ "");
		Map<String, Object> demoMap = new HashMap<>();
		IdentityInfoDTO name = new IdentityInfoDTO();
		name.setLanguage("eng");
		name.setValue("my name");
		demoMap.put("fullName", name);
		demoMap.put("email", "abc@def.com");
		demoMap.put("phone", "9887765443");
		byte[] biometricData = gson.toJson(bioMap).getBytes();
		byte[] demographicData = gson.toJson(demoMap).getBytes("utf-8");
		identityValue.setBiometricData(biometricData);
		identityValue.setDemographicData(demographicData);
		identityValue.setToken("11223344");
		Mockito.when(identityCacheRepository.getOne(individualId)).thenReturn(identityValue);
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		
		iBioProviderApi mockBioProviderApi = Mockito.mock(iBioProviderApi.class);
		when(bioAPIFactory.getBioProvider(Mockito.any(), Mockito.any())).thenReturn(mockBioProviderApi);
		when(mockBioProviderApi.verify(any(), any(), any(), any())).thenReturn(true);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/testLicenseKey/testPartnerId/testAPIKey").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(reqJson.getBytes()))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.response", is(notNullValue())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.authStatus", is(true)));
	}
	
	@Test
	public void testBiometricAuthenticationFingerMinutiae() throws Exception {
		Boolean authStatus = true;
		AuthRequestDTO authReqDto = new AuthRequestDTO();
		String requestTime = DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime());
		authReqDto.setRequestTime(requestTime);
		authReqDto.setTransactionID("1234567890");
		authReqDto.setDomainUri("http://dummy.domain");
		authReqDto.setEnv("Staging");
		authReqDto.setId(env.getProperty("mosip.ida.api.ids.auth"));
		AuthTypeDTO authTypeDto = new AuthTypeDTO();
		authTypeDto.setBio(true);
		authReqDto.setRequestedAuth(authTypeDto);
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setTimestamp(requestTime);
		List<BioIdentityInfoDTO> bios = new ArrayList<>();
		BioIdentityInfoDTO bio1 = new BioIdentityInfoDTO();
		DataDTO data = new DataDTO();
		data.setTimestamp(requestTime);
		data.setBioType("Finger");
		data.setBioSubType("Right Thumb");
		data.setBioValue("cmlnaHQgdGh1bWI=");
		
		data.setDomainUri("http://dummy.domain");
		data.setEnv("Staging");
		DigitalId digitalId = new DigitalId();
		digitalId.setDateTime(requestTime);
		digitalId.setDeviceProvider("TEST");
		digitalId.setDeviceProviderId("TEST.TCI322");
		digitalId.setDeviceSubType("Right Thumb");
		digitalId.setMake("eyecool");
		digitalId.setModel("TESTMODEL");
		digitalId.setType("Finger");
		digitalId.setSerialNo("123456");
		data.setDigitalId(digitalId);
		data.setDeviceCode("3f1dc192-9595-4d48-a2ed-7407760ba917");
		data.setDeviceServiceVersion("SB.WIN.001");
		
		authReqDto.setConsentObtained(true);
		bio1.setData(data);
		bios.add(bio1);
		requestDTO.setBiometrics(bios);
		authReqDto.setRequest(requestDTO);
		String individualId = "9876543210";
		authReqDto.setIndividualId(individualId);
		reqJson = gson.toJson(authReqDto);
		Mockito.when(idAuthSecurityManager.hash(individualId)).thenReturn(individualId);
		Mockito.when(identityCacheRepository.existsById(individualId)).thenReturn(true);
		IdentityEntity identityValue = new IdentityEntity();
		Map<String, Object> bioMap = new HashMap<>();
		bioMap.put("Finger_Right Thumb", "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n"
				+ "<BIR xmlns=\"http://standards.iso.org/iso-iec/19785/-3/ed-2/\">\r\n"
				+ "    <BIRInfo>\r\n"
				+ "        <Integrity>false</Integrity>\r\n"
				+ "    </BIRInfo>\r\n"
				+ "    <BIR>\r\n"
				+ "        <Version>\r\n"
				+ "            <Major>1</Major>\r\n"
				+ "            <Minor>1</Minor>\r\n"
				+ "        </Version>\r\n"
				+ "        <CBEFFVersion>\r\n"
				+ "            <Major>1</Major>\r\n"
				+ "            <Minor>1</Minor>\r\n"
				+ "        </CBEFFVersion>\r\n"
				+ "        <BIRInfo>\r\n"
				+ "            <Integrity>false</Integrity>\r\n"
				+ "        </BIRInfo>\r\n"
				+ "        <BDBInfo>\r\n"
				+ "            <Index>11d5c6dc-1bc2-4eda-a7bf-4a3639b29e01</Index>\r\n"
				+ "            <Format>\r\n"
				+ "                <Organization>Mosip</Organization>\r\n"
				+ "                <Type>2</Type>\r\n"
				+ "            </Format>\r\n"
				+ "            <CreationDate>2021-08-11T10:32:48.124194800Z</CreationDate>\r\n"
				+ "            <Type>Finger</Type>\r\n"
				+ "            <Subtype>Right Thumb</Subtype>\r\n"
				+ "            <Level>Processed</Level>\r\n"
				+ "            <Purpose>Enroll</Purpose>\r\n"
				+ "            <Quality>\r\n"
				+ "                <Algorithm>\r\n"
				+ "                    <Organization>HMAC</Organization>\r\n"
				+ "                    <Type>SHA-256</Type>\r\n"
				+ "                </Algorithm>\r\n"
				+ "                <Score>80</Score>\r\n"
				+ "            </Quality>\r\n"
				+ "        </BDBInfo>\r\n"
				+ "        <BDB>cmlnaHQgdGh1bWI=</BDB>\r\n"
				+ "        <SB>cmlnaHQgdGh1bWI=</SB>\r\n"
				+ "        <Others>\r\n"
				+ "            <Key>EXCEPTION</Key>\r\n"
				+ "            <Value>false</Value>\r\n"
				+ "        </Others>\r\n"
				+ "        <Others>\r\n"
				+ "            <Key>RETRIES</Key>\r\n"
				+ "            <Value>1</Value>\r\n"
				+ "        </Others>\r\n"
				+ "        <Others>\r\n"
				+ "            <Key>SDK_SCORE</Key>\r\n"
				+ "            <Value>0.0</Value>\r\n"
				+ "        </Others>\r\n"
				+ "        <Others>\r\n"
				+ "            <Key>FORCE_CAPTURED</Key>\r\n"
				+ "            <Value>false</Value>\r\n"
				+ "        </Others>\r\n"
				+ "        <Others>\r\n"
				+ "            <Key>SPEC_VERSION</Key>\r\n"
				+ "            <Value>0.9.5</Value>\r\n"
				+ "        </Others>\r\n"
				+ "    </BIR>\r\n"
				+ "</BIR>\r\n"
				+ "");
		Map<String, Object> demoMap = new HashMap<>();
		IdentityInfoDTO name = new IdentityInfoDTO();
		name.setLanguage("eng");
		name.setValue("my name");
		demoMap.put("fullName", name);
		demoMap.put("email", "abc@def.com");
		demoMap.put("phone", "9887765443");
		byte[] biometricData = gson.toJson(bioMap).getBytes();
		byte[] demographicData = gson.toJson(demoMap).getBytes("utf-8");
		identityValue.setBiometricData(biometricData);
		identityValue.setDemographicData(demographicData);
		identityValue.setToken("11223344");
		Mockito.when(identityCacheRepository.getOne(individualId)).thenReturn(identityValue);
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
		
		iBioProviderApi mockBioProviderApi = Mockito.mock(iBioProviderApi.class);
		when(bioAPIFactory.getBioProvider(Mockito.any(), Mockito.any())).thenReturn(mockBioProviderApi);
		when(mockBioProviderApi.verify(any(), any(), any(), any())).thenReturn(true);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/testLicenseKey/testPartnerId/testAPIKey").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(reqJson.getBytes()))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("$.response", is(notNullValue())))
		.andExpect(MockMvcResultMatchers.jsonPath("$.response.authStatus", is(true)));
	}

}
