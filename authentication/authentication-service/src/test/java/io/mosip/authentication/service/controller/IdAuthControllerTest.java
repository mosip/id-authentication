package io.mosip.authentication.service.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.authentication.common.service.cache.MasterDataCache;
import io.mosip.authentication.common.service.cache.PartnerServiceCache;
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
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.authentication.service.IdAuthenticationTestApplication;
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
	
	@Mock
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

	@Autowired
	private MockMvc mockMvc;
	
	Gson gson = new GsonBuilder().serializeNulls().create();

	String reqJson;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
		AuthRequestDTO authReqDto = new AuthRequestDTO();
		reqJson = gson.toJson(authReqDto);
		Mockito.when(uinValidator.validateId(Mockito.anyString())).thenReturn(true);
	}

	@Test
	public void testBiometricAuthentication() throws Exception {
		Boolean authStatus = true;
		mockMvc.perform(MockMvcRequestBuilders.post("/testLicenseKey/testPartnerId/testAPIKey").contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(reqJson.getBytes())).andExpect(status().isOk());
	}

}
