package io.mosip.authentication.internal.service.integration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import io.mosip.authentication.common.service.entity.KeyAlias;
import io.mosip.authentication.common.service.repository.KeyAliasRepository;
import io.mosip.authentication.common.service.repository.KeyStoreRepository;
import io.mosip.authentication.core.exception.CryptoException;
import io.mosip.authentication.core.exception.NoUniqueAliasException;
import io.mosip.authentication.core.spi.keymanager.service.KeymanagerService;
import io.mosip.authentication.internal.service.impl.KeymanagerServiceImpl;
import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.keymanager.spi.KeyStore;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keygenerator.bouncycastle.constant.KeyGeneratorExceptionConstant;
import io.mosip.kernel.keymanager.softhsm.util.CertificateUtility;
import io.netty.handler.ssl.SslContextBuilder;

/**
 * 
 * @author Nagarjuna
 *
 */

@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@WebMvcTest
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@PrepareForTest({ WebClient.class, SslContextBuilder.class })
public class KeymanagerServiceImplTest {
	
	KeymanagerServiceImpl keymanagerService;
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private KeyStore keyStore;

	@MockBean
	private KeyAliasRepository keyAliasRepository;
	
	@MockBean
	private KeyStoreRepository keyStoreRepository;
	
	@MockBean
	private KeyGenerator keyGenerator;

	/**
	 * {@link CryptoCoreSpec} instance for cryptographic functionalities.
	 */
	@MockBean
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> cryptoCore;

	private static BouncyCastleProvider provider;
	
	@Mock
	private PublicKey publicKey;

	@Mock
	private PrivateKey privateKey;
	
	private SecretKey secretKey;

	private PrivateKeyEntry privateKeyEntry;

	private KeyPair key;
	private ObjectMapper mapper;
	private List<KeyAlias> keyalias;
	private Optional<io.mosip.authentication.common.service.entity.KeyStore> dbKeyStore;
	
	@Before
	public void init() {
		keymanagerService = new KeymanagerServiceImpl();
		initi();
		ReflectionTestUtils.setField(keymanagerService, "keyAliasRepository", keyAliasRepository);
		ReflectionTestUtils.setField(keymanagerService, "keyGenerator", keyGenerator);
		ReflectionTestUtils.setField(keymanagerService, "keyStore", keyStore);
		ReflectionTestUtils.setField(keymanagerService, "cryptoCore", cryptoCore);
		ReflectionTestUtils.setField(keymanagerService, "keyStoreRepository", keyStoreRepository);
		mapper = new ObjectMapper();
		keyalias = new ArrayList<>();
		dbKeyStore = Optional.empty();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}
	
	private void setupMultipleKeyAlias() {
		keyalias = new ArrayList<>();
		keyalias.add(new KeyAlias("alias-one", "applicationId", "referenceId", LocalDateTime.of(2010, 1, 1, 12, 00),
				LocalDateTime.of(2011, 1, 1, 12, 00), "status","SYSTEM",LocalDateTime.of(2010, 1, 1, 12, 00),null,null,false,null));
		keyalias.add(new KeyAlias("alias-two", "applicationId", "referenceId", LocalDateTime.of(2010, 1, 1, 12, 00),
				LocalDateTime.of(2011, 1, 1, 12, 00), "status","SYSTEM",LocalDateTime.of(2010, 1, 1, 12, 00),null,null,false,null));
	}
	
	private void setupMultipleCurrentKeyAlias() {
		keyalias = new ArrayList<>();
		keyalias.add(new KeyAlias("alias-one", "applicationId", "referenceId", LocalDateTime.of(2010, 1, 1, 12, 00),
				LocalDateTime.of(2019, 1, 1, 12, 00), "status","SYSTEM",LocalDateTime.of(2010, 1, 1, 12, 00),null,null,false,null));
		keyalias.add(new KeyAlias("alias-two", "applicationId", "referenceId", LocalDateTime.of(2010, 1, 1, 12, 00),
				LocalDateTime.of(2019, 1, 1, 12, 00), "status","SYSTEM",LocalDateTime.of(2010, 1, 1, 12, 00),null,null,false,null));
	}
	
	private void setupSingleCurrentKeyAlias() {
		keyalias = new ArrayList<>();
		keyalias.add(new KeyAlias("alias-one", "applicationId", "referenceId", LocalDateTime.of(2010, 1, 1, 12, 00),
				LocalDateTime.of(2019, 1, 1, 12, 00), "status","SYSTEM",LocalDateTime.of(2010, 1, 1, 12, 00),null,null,false,null));
	}

	private void setupSingleKeyAlias() {
		keyalias = new ArrayList<>();
		keyalias.add(new KeyAlias("alias", "applicationId", "referenceId", LocalDateTime.of(2010, 1, 1, 12, 00),
				LocalDateTime.of(2011, 1, 1, 12, 00), "status","SYSTEM",LocalDateTime.of(2010, 1, 1, 12, 00),null,null,false,null));
	}
	
	private void setupDBKeyStore() {
		dbKeyStore = Optional.of(new io.mosip.authentication.common.service.entity.KeyStore("db-alias",
				"test-public-key".getBytes(), "test-private#KEY_SPLITTER#-key".getBytes(), "alias","SYSTEM",LocalDateTime.of(2010, 1, 1, 12, 00),null,null,false,null));
	}
	
	private void setupKey() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		key = keyGen.generateKeyPair();		
		X509Certificate x509Certificate = CertificateUtility.generateX509Certificate(key, "mosip", "mosip", "mosip",
				"india", LocalDateTime.of(2010, 1, 1, 12, 00), LocalDateTime.of(2011, 1, 1, 12, 00));
		X509Certificate[] chain = new X509Certificate[1];
		chain[0] = x509Certificate;
		privateKeyEntry = new PrivateKeyEntry(key.getPrivate(), chain);
		
		javax.crypto.KeyGenerator keyGenerator = javax.crypto.KeyGenerator.getInstance("AES",provider);
		keyGenerator.init(256);
		secretKey = keyGenerator.generateKey();
	}
	
	private static BouncyCastleProvider initi() {
		provider = new BouncyCastleProvider();
		Security.addProvider(provider);
		return provider;
	}
	
	@Test
	public void getPublicKeyFromHSMMultipleAlias() throws Exception {
		setupMultipleKeyAlias();
		setupKey();		
		org.mockito.Mockito.when(keyGenerator.getAsymmetricKey()).thenReturn(key);
		org.mockito.Mockito.doNothing().when(keyStore).storeAsymmetricKey(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any());
		org.mockito.Mockito.when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		org.mockito.Mockito.when(keyStore.getPublicKey(Mockito.any())).thenReturn(publicKey);
		org.mockito.Mockito.when(keyGenerator.getSymmetricKey()).thenReturn(secretKey);
		org.mockito.Mockito.when(cryptoCore.symmetricEncrypt(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		org.mockito.Mockito.when(cryptoCore.asymmetricEncrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		keymanagerService.getPublicKey("applicationId", "2017-01-01T12:00:00.000Z", Optional.of("referenceId"));		
	}
	
	@Test(expected = NoUniqueAliasException.class)
	public void getPublicKeyFromHSMMultipleAlias_S01() throws Exception {
		setupMultipleCurrentKeyAlias();
		setupKey();		
		org.mockito.Mockito.when(keyGenerator.getAsymmetricKey()).thenReturn(key);
		org.mockito.Mockito.doNothing().when(keyStore).storeAsymmetricKey(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any());
		org.mockito.Mockito.when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		org.mockito.Mockito.when(keyStore.getPublicKey(Mockito.any())).thenReturn(publicKey);
		org.mockito.Mockito.when(keyGenerator.getSymmetricKey()).thenReturn(secretKey);
		org.mockito.Mockito.when(cryptoCore.symmetricEncrypt(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		org.mockito.Mockito.when(cryptoCore.asymmetricEncrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		keymanagerService.getPublicKey("applicationId", "2017-01-01T12:00:00.000Z", Optional.of("referenceId"));		
	}
	
	@Test(expected = NoUniqueAliasException.class)
	public void getPublicKeyFromHSMMultipleAlias_S02() throws Exception {
		setupSingleCurrentKeyAlias();
		setupKey();		
		org.mockito.Mockito.when(keyGenerator.getAsymmetricKey()).thenReturn(key);
		org.mockito.Mockito.doNothing().when(keyStore).storeAsymmetricKey(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any());
		org.mockito.Mockito.when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		org.mockito.Mockito.when(keyStore.getPublicKey(Mockito.any())).thenReturn(publicKey);
		org.mockito.Mockito.when(keyGenerator.getSymmetricKey()).thenReturn(secretKey);
		org.mockito.Mockito.when(cryptoCore.symmetricEncrypt(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		org.mockito.Mockito.when(cryptoCore.asymmetricEncrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		keymanagerService.getPublicKey("applicationId", "2017-01-01T12:00:00.000Z", Optional.of("referenceId"));		
	}
	
	@Test
	public void getPublicKeyFromHSMMultipleAlias_S03() throws Exception {
		setupSingleCurrentKeyAlias();
		setupKey();		
		setupDBKeyStore();
		org.mockito.Mockito.when(keyGenerator.getAsymmetricKey()).thenReturn(key);
		org.mockito.Mockito.doNothing().when(keyStore).storeAsymmetricKey(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any());
		org.mockito.Mockito.when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		org.mockito.Mockito.when(keyStore.getPublicKey(Mockito.any())).thenReturn(publicKey);
		org.mockito.Mockito.when(keyGenerator.getSymmetricKey()).thenReturn(secretKey);
		org.mockito.Mockito.when(cryptoCore.symmetricEncrypt(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		org.mockito.Mockito.when(cryptoCore.asymmetricEncrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		org.mockito.Mockito.when(keyStoreRepository.findByAlias(Mockito.any())).thenReturn(dbKeyStore);
		keymanagerService.getPublicKey("applicationId", "2017-01-01T12:00:00.000Z", Optional.of("referenceId"));		
	}
	
	@Test(expected = CryptoException.class)
	public void getPublicKeyFromHSMMultipleAlias_S001() throws Exception {
		setupMultipleKeyAlias();
		setupKey();		
		org.mockito.Mockito.when(keyGenerator.getAsymmetricKey()).thenReturn(key);
		org.mockito.Mockito.doNothing().when(keyStore).storeAsymmetricKey(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any());
		org.mockito.Mockito.when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		org.mockito.Mockito.when(keyStore.getPublicKey(Mockito.any())).thenReturn(publicKey);
		org.mockito.Mockito.when(keyGenerator.getSymmetricKey()).thenReturn(secretKey);		
		keymanagerService.getPublicKey("applicationId", "2017-01-01T12:00:00.000Z", Optional.of("referenceId"));		
	}
	
	@Test
	public void getPublicKey_refIdEmpty() throws Exception {
		setupMultipleKeyAlias();
		setupKey();		
		org.mockito.Mockito.when(keyGenerator.getAsymmetricKey()).thenReturn(key);
		org.mockito.Mockito.doNothing().when(keyStore).storeAsymmetricKey(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any());
		org.mockito.Mockito.when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		org.mockito.Mockito.when(keyStore.getPublicKey(Mockito.any())).thenReturn(publicKey);
		org.mockito.Mockito.when(keyGenerator.getSymmetricKey()).thenReturn(secretKey);		
		org.mockito.Mockito.when(cryptoCore.symmetricEncrypt(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		org.mockito.Mockito.when(cryptoCore.asymmetricEncrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		keymanagerService.getPublicKey("applicationId", "2017-01-01T12:00:00.000Z", Optional.empty());		
	}
	
	@Test
	public void getPublicKey_refIdEmpty_001() throws Exception {
		setupSingleCurrentKeyAlias();
		setupKey();		
		org.mockito.Mockito.when(keyGenerator.getAsymmetricKey()).thenReturn(key);
		org.mockito.Mockito.doNothing().when(keyStore).storeAsymmetricKey(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any());
		org.mockito.Mockito.when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		org.mockito.Mockito.when(keyStore.getPublicKey(Mockito.any())).thenReturn(publicKey);
		org.mockito.Mockito.when(keyGenerator.getSymmetricKey()).thenReturn(secretKey);		
		org.mockito.Mockito.when(cryptoCore.symmetricEncrypt(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		org.mockito.Mockito.when(cryptoCore.asymmetricEncrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		keymanagerService.getPublicKey("applicationId", "2017-01-01T12:00:00.000Z", Optional.empty());		
	}
	
	@Test(expected = NoUniqueAliasException.class)
	public void getPublicKey_refIdEmpty_002() throws Exception {
		setupMultipleCurrentKeyAlias();
		setupKey();		
		org.mockito.Mockito.when(keyGenerator.getAsymmetricKey()).thenReturn(key);
		org.mockito.Mockito.doNothing().when(keyStore).storeAsymmetricKey(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any());
		org.mockito.Mockito.when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		org.mockito.Mockito.when(keyStore.getPublicKey(Mockito.any())).thenReturn(publicKey);
		org.mockito.Mockito.when(keyGenerator.getSymmetricKey()).thenReturn(secretKey);		
		org.mockito.Mockito.when(cryptoCore.symmetricEncrypt(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		org.mockito.Mockito.when(cryptoCore.asymmetricEncrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		keymanagerService.getPublicKey("applicationId", "2017-01-01T12:00:00.000Z", Optional.empty());		
	}
	
	@Test
	public void getPublicKey_refIdEmptyString() throws Exception {
		setupMultipleKeyAlias();
		setupKey();		
		org.mockito.Mockito.when(keyGenerator.getAsymmetricKey()).thenReturn(key);
		org.mockito.Mockito.doNothing().when(keyStore).storeAsymmetricKey(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any());
		org.mockito.Mockito.when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		org.mockito.Mockito.when(keyStore.getPublicKey(Mockito.any())).thenReturn(publicKey);
		org.mockito.Mockito.when(keyGenerator.getSymmetricKey()).thenReturn(secretKey);		
		org.mockito.Mockito.when(cryptoCore.symmetricEncrypt(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		org.mockito.Mockito.when(cryptoCore.asymmetricEncrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		keymanagerService.getPublicKey("applicationId", "2017-01-01T12:00:00.000Z", Optional.of(""));		
	}
	
	@Test(expected = NoUniqueAliasException.class)
	public void getSignPublicKey_S001() throws NoSuchAlgorithmException {
		setupSingleKeyAlias();
		setupKey();
		org.mockito.Mockito.when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		org.mockito.Mockito.when(cryptoCore.sign(Mockito.any(), Mockito.any())).thenReturn("");
		org.mockito.Mockito.when(keyStore.getAsymmetricKey(Mockito.any())).thenReturn(privateKeyEntry);
		keymanagerService.getSignPublicKey("applicationId", "2017-01-01T12:00:00.000Z", Optional.of("referenceId"));
	}
	
	@Test
	public void getSignPublicKey_S002() throws NoSuchAlgorithmException {
		setupSingleCurrentKeyAlias();
		setupKey();
		org.mockito.Mockito.when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		org.mockito.Mockito.when(cryptoCore.sign(Mockito.any(), Mockito.any())).thenReturn("");
		org.mockito.Mockito.when(keyStore.getAsymmetricKey(Mockito.any())).thenReturn(privateKeyEntry);
		keymanagerService.getSignPublicKey("applicationId", "2017-01-01T12:00:00.000Z", Optional.of("referenceId"));
	}
	
	@Test(expected = NoUniqueAliasException.class)
	public void getSignPublicKey_S003() throws NoSuchAlgorithmException {
		setupMultipleCurrentKeyAlias();
		setupKey();
		org.mockito.Mockito.when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		org.mockito.Mockito.when(cryptoCore.sign(Mockito.any(), Mockito.any())).thenReturn("");
		org.mockito.Mockito.when(keyStore.getAsymmetricKey(Mockito.any())).thenReturn(privateKeyEntry);
		keymanagerService.getSignPublicKey("applicationId", "2017-01-01T12:00:00.000Z", Optional.of("referenceId"));
	}
	
	@Test
	public void getSignPublicKey_S004() throws NoSuchAlgorithmException {
		setupSingleCurrentKeyAlias();
		setupKey();
		org.mockito.Mockito.when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		org.mockito.Mockito.when(cryptoCore.sign(Mockito.any(), Mockito.any())).thenReturn("");
		org.mockito.Mockito.when(keyStore.getAsymmetricKey(Mockito.any())).thenReturn(privateKeyEntry);
		keymanagerService.getSignPublicKey("applicationId", "2017-01-01T12:00:00.000Z", Optional.of(""));
	}
	
	@Test
	public void getSignPublicKey_S005() throws NoSuchAlgorithmException {
		setupSingleCurrentKeyAlias();
		setupKey();
		org.mockito.Mockito.when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		org.mockito.Mockito.when(cryptoCore.sign(Mockito.any(), Mockito.any())).thenReturn("");
		org.mockito.Mockito.when(keyStore.getAsymmetricKey(Mockito.any())).thenReturn(privateKeyEntry);
		keymanagerService.getSignPublicKey("applicationId", "2017-01-01T12:00:00.000Z", Optional.empty());
	}

}

