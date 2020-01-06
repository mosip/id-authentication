package io.mosip.kernel.keymanagerservice.test.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.keymanager.spi.KeyStore;
import io.mosip.kernel.keymanager.softhsm.util.CertificateUtility;
import io.mosip.kernel.keymanagerservice.constant.KeymanagerConstant;
import io.mosip.kernel.keymanagerservice.dto.PublicKeyResponse;
import io.mosip.kernel.keymanagerservice.dto.SignatureRequestDto;
import io.mosip.kernel.keymanagerservice.dto.SignatureResponseDto;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyRequestDto;
import io.mosip.kernel.keymanagerservice.entity.KeyAlias;
import io.mosip.kernel.keymanagerservice.entity.KeyPolicy;
import io.mosip.kernel.keymanagerservice.repository.KeyAliasRepository;
import io.mosip.kernel.keymanagerservice.repository.KeyPolicyRepository;
import io.mosip.kernel.keymanagerservice.repository.KeyStoreRepository;
import io.mosip.kernel.keymanagerservice.test.KeymanagerTestBootApplication;
import io.mosip.kernel.keymanagerservice.util.KeymanagerUtil;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@SpringBootTest(classes = { KeymanagerTestBootApplication.class })
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class KeymanagerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private KeyStore keyStore;

	@MockBean
	private KeyAliasRepository keyAliasRepository;

	@MockBean
	private KeyPolicyRepository keyPolicyRepository;

	@MockBean
	private KeyStoreRepository keyStoreRepository;

	/**
	 * {@link CryptoCoreSpec} instance for cryptographic functionalities.
	 */
	@MockBean
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> cryptoCore;

	@Mock
	private PublicKey publicKey;

	@Mock
	private PrivateKey privateKey;
	
    private PrivateKeyEntry privateKeyEntry;
	
	@SpyBean
	private KeymanagerUtil keymanagerUtil;

	private KeyPair key;
	private ObjectMapper mapper;
	private List<KeyAlias> keyalias;
	private Optional<KeyPolicy> keyPolicy;
	private Optional<io.mosip.kernel.keymanagerservice.entity.KeyStore> dbKeyStore;

	private static final String ID = "mosip.crypto.service";
	private static final String VERSION = "V1.0";

	private RequestWrapper<SymmetricKeyRequestDto> requestWrapper;

	@Before
	public void init() {
		mapper = new ObjectMapper();
		keyalias = new ArrayList<>();
		keyPolicy = Optional.empty();
		dbKeyStore = Optional.empty();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		requestWrapper = new RequestWrapper<>();
		requestWrapper.setId(ID);
		requestWrapper.setVersion(VERSION);
		requestWrapper.setRequesttime(LocalDateTime.now(ZoneId.of("UTC")));
	}

	private void setupMultipleKeyAlias() {
		keyalias = new ArrayList<>();
		keyalias.add(new KeyAlias("alias-one", "applicationId", "referenceId", LocalDateTime.of(2010, 1, 1, 12, 00),
				LocalDateTime.of(2011, 1, 1, 12, 00), "status"));
		keyalias.add(new KeyAlias("alias-two", "applicationId", "referenceId", LocalDateTime.of(2010, 1, 1, 12, 00),
				LocalDateTime.of(2011, 1, 1, 12, 00), "status"));

	}

	private void setupSingleKeyAlias() {
		keyalias = new ArrayList<>();
		keyalias.add(new KeyAlias("alias", "applicationId", "referenceId", LocalDateTime.of(2010, 1, 1, 12, 00),
				LocalDateTime.of(2011, 1, 1, 12, 00), "status"));

	}

	private void setupExpiryPolicy() {
		keyPolicy = Optional.of(new KeyPolicy("applicationId", 365, true));
	}

	private void setupDBKeyStore() {
		dbKeyStore = Optional.of(new io.mosip.kernel.keymanagerservice.entity.KeyStore("db-alias",
				"test-public-key".getBytes(), "test-private#KEY_SPLITTER#-key".getBytes(), "alias"));
	}

	private void setupKey() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KeymanagerConstant.RSA);
		keyGen.initialize(1024);
		key = keyGen.generateKeyPair();
		X509Certificate x509Certificate=CertificateUtility.generateX509Certificate(key, "mosip", "mosip", "mosip", "india", LocalDateTime.of(2010, 1, 1, 12, 00), LocalDateTime.of(2011, 1, 1, 12, 00));
		X509Certificate[] chain = new X509Certificate[1];
		chain[0]=x509Certificate;
		privateKeyEntry=new PrivateKeyEntry(key.getPrivate(), chain);
	}

	@WithUserDetails("reg-processor")
	@Test
	public void getPublicKeyFromHSMMultipleAlias() throws Exception {
		setupMultipleKeyAlias();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);

		MvcResult result = mockMvc.perform(get("/publickey/1?timeStamp=2010-01-01T12:00:00.000Z"))
				.andExpect(status().is(200)).andReturn();
		// System.out.println(result.getResponse().getContentAsString());
	}

	@WithUserDetails("reg-processor")
	@Test
	public void getPublicKeyFromHSMMultipleAliasReference() throws Exception {
		setupMultipleKeyAlias();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		MvcResult result = mockMvc.perform(get("/publickey/1?referenceId= &timeStamp=2010-01-01T12:00:00.000Z"))
				.andExpect(status().is(200)).andReturn();

		// System.out.println(result.getResponse().getContentAsString());
	}

	@WithUserDetails("reg-processor")
	@Test
	public void getPublicKeyFromHSMSingleAlias() throws Exception {
		setupSingleKeyAlias();
		when(keyStore.getPublicKey(Mockito.any())).thenReturn(publicKey);
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		MvcResult result = mockMvc.perform(get("/publickey/1?timeStamp=2011-01-01T12:00:00.000Z"))
				.andExpect(status().is(200)).andReturn();
		// System.out.println(result.getResponse().getContentAsString());
	}

	@WithUserDetails("reg-processor")
	@Test
	public void getPublicKeyFromHSMEmptyAliasException() throws Exception {
		when(keyStore.getPublicKey(Mockito.any())).thenReturn(publicKey);
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(keyPolicyRepository.findByApplicationId(Mockito.any())).thenReturn(keyPolicy);
		MvcResult result = mockMvc.perform(get("/publickey/1?timeStamp=2010-05-01T10:00:00.000Z"))
				.andExpect(status().is(200)).andReturn();

	}

	@WithUserDetails("reg-processor")
	@Test
	public void getPublicKeyFromHSMEmptyAlias() throws Exception {
		setupExpiryPolicy();
		setupSingleKeyAlias();
		when(keyStore.getPublicKey(Mockito.any())).thenReturn(publicKey);
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(keyPolicyRepository.findByApplicationId(Mockito.any())).thenReturn(keyPolicy);

		MvcResult result = mockMvc.perform(get("/publickey/1?timeStamp=2009-05-01T10:00:00.000Z"))
				.andExpect(status().is(200)).andReturn();
		// System.out.println(result.getResponse().getContentAsString());
	}

	@WithUserDetails("reg-processor")
	@Test
	public void getPublicKeyFromHSMEmptyAliasNotOverlapping() throws Exception {
		setupExpiryPolicy();
		setupSingleKeyAlias();
		when(keyStore.getPublicKey(Mockito.any())).thenReturn(publicKey);
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(keyPolicyRepository.findByApplicationId(Mockito.any())).thenReturn(keyPolicy);

		MvcResult result = mockMvc.perform(get("/publickey/1?timeStamp=2001-05-01T10:00:00.000Z"))
				.andExpect(status().is(200)).andReturn();
		// System.out.println(result.getResponse().getContentAsString());
	}

	@WithUserDetails("reg-processor")
	@Test
	public void getPublicKeyFromDBMultipleAlias() throws Exception {
		setupMultipleKeyAlias();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		MvcResult result = mockMvc
				.perform(get("/publickey/REGISTRATION?referenceId=1&timeStamp=2010-05-01T10:00:00.000Z"))
				.andExpect(status().is(200)).andReturn();
		// System.out.println(result.getResponse().getContentAsString());
	}

	@WithUserDetails("reg-processor")
	@Test
	public void getPublicKeyFromDBSingleAliasException() throws Exception {
		setupSingleKeyAlias();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(keyStoreRepository.findByAlias(Mockito.any())).thenReturn(dbKeyStore);
		MvcResult result = mockMvc
				.perform(get("/publickey/REGISTRATION?referenceId=1&timeStamp=2010-01-01T12:00:00.000Z"))
				.andExpect(status().is(200)).andReturn();
		// System.out.println(result.getResponse().getContentAsString());
	}

	@WithUserDetails("reg-processor")
	@Test
	public void getPublicKeyFromDBSingleAlias() throws Exception {
		setupSingleKeyAlias();
		setupDBKeyStore();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(keyStoreRepository.findByAlias(Mockito.any())).thenReturn(dbKeyStore);
		MvcResult result = mockMvc
				.perform(get("/publickey/REGISTRATION?referenceId=1&timeStamp=2010-05-01T10:00:00.000Z"))
				.andExpect(status().is(200)).andReturn();
		// System.out.println(result.getResponse().getContentAsString());
	}

	@WithUserDetails("reg-processor")
	@Test
	public void getPublicKeyFromDBEmptyAliasCryptoException() throws Exception {
		setupExpiryPolicy();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(keyStoreRepository.findByAlias(Mockito.any())).thenReturn(dbKeyStore);
		when(keyPolicyRepository.findByApplicationId(Mockito.any())).thenReturn(keyPolicy);
		MvcResult result = mockMvc
				.perform(get("/publickey/REGISTRATION?referenceId=1&timeStamp=2010-05-01T10:00:00.000Z"))
				.andExpect(status().is(500)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@WithUserDetails("reg-processor")
	@Test
	public void getPublicKeyFromDBEmptyAlias() throws Exception {
		setupExpiryPolicy();
		setupKey();
		doReturn(key.getPrivate().getEncoded()).when(keymanagerUtil).encryptKey(Mockito.any(), Mockito.any());
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(keyStoreRepository.findByAlias(Mockito.any())).thenReturn(dbKeyStore);
		when(keyPolicyRepository.findByApplicationId(Mockito.any())).thenReturn(keyPolicy);
		MvcResult result = mockMvc
				.perform(get("/publickey/REGISTRATION?referenceId=1&timeStamp=2010-05-01T10:00:00.000Z"))
				.andExpect(status().is(200)).andReturn();
		// System.out.println(result.getResponse().getContentAsString());
	}

	@WithUserDetails("reg-processor")
	@Test
	public void decryptSymmetricKeyException() throws Exception {
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		SymmetricKeyRequestDto symmetricKeyRequestDto = new SymmetricKeyRequestDto("applicationId",
				LocalDateTime.parse("2010-05-01 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "", "");
		requestWrapper.setRequest(symmetricKeyRequestDto);
		String content = mapper.writeValueAsString(requestWrapper);
		MvcResult result = mockMvc.perform(post("/decrypt").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().is(200)).andReturn();

		// System.out.println(result.getResponse().getContentAsString());
	}

	@WithUserDetails("reg-processor")
	@Test
	public void decryptSymmetricKey() throws Exception {
		setupSingleKeyAlias();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(cryptoCore.asymmetricDecrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		SymmetricKeyRequestDto symmetricKeyRequestDto = new SymmetricKeyRequestDto("applicationId",
				LocalDateTime.parse("2010-05-01 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), null, "");
		requestWrapper.setRequest(symmetricKeyRequestDto);
		String content = mapper.writeValueAsString(requestWrapper);
		MvcResult result = mockMvc.perform(post("/decrypt").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().is(200)).andReturn();
		// System.out.println(result.getResponse().getContentAsString());
	}

	@WithUserDetails("reg-processor")
	@Test
	public void decryptSymmetricKeyWithReferenceIdException() throws Exception {
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(cryptoCore.asymmetricDecrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		SymmetricKeyRequestDto symmetricKeyRequestDto = new SymmetricKeyRequestDto("applicationId",
				LocalDateTime.parse("2010-05-01 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "referenceId",
				"");
		requestWrapper.setRequest(symmetricKeyRequestDto);
		String content = mapper.writeValueAsString(requestWrapper);
		MvcResult result = mockMvc.perform(post("/decrypt").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().is(200)).andReturn();

		// System.out.println(result.getResponse().getContentAsString());
	}

	@WithUserDetails("reg-processor")
	@Test
	public void decryptSymmetricKeyWithReferenceIdMultipleAliasException() throws Exception {
		setupMultipleKeyAlias();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(cryptoCore.asymmetricDecrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		SymmetricKeyRequestDto symmetricKeyRequestDto = new SymmetricKeyRequestDto("applicationId",
				LocalDateTime.parse("2010-05-01 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "referenceId",
				"");
		requestWrapper.setRequest(symmetricKeyRequestDto);
		String content = mapper.writeValueAsString(requestWrapper);
		MvcResult result = mockMvc.perform(post("/decrypt").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().is(200)).andReturn();
		// System.out.println(result.getResponse().getContentAsString());
	}

	@WithUserDetails("reg-processor")
	@Test
	public void decryptSymmetricKeyWithReferenceIdDBException() throws Exception {
		setupSingleKeyAlias();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(cryptoCore.asymmetricDecrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		SymmetricKeyRequestDto symmetricKeyRequestDto = new SymmetricKeyRequestDto("applicationId",
				LocalDateTime.parse("2010-05-01 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "referenceId",
				"");
		requestWrapper.setRequest(symmetricKeyRequestDto);
		String content = mapper.writeValueAsString(requestWrapper);
		MvcResult result = mockMvc.perform(post("/decrypt").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().is(200)).andReturn();
		// System.out.println(result.getResponse().getContentAsString());
	}

	@WithUserDetails("reg-processor")
	@Test
	public void decryptSymmetricKeyWithReferenceIdCryptoException() throws Exception {
		setupSingleKeyAlias();
		setupDBKeyStore();
		when(keyStoreRepository.findByAlias(Mockito.any())).thenReturn(dbKeyStore);
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(cryptoCore.asymmetricDecrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		doReturn("".getBytes()).when(keymanagerUtil).decryptKey(Mockito.any(), Mockito.any());
		SymmetricKeyRequestDto symmetricKeyRequestDto = new SymmetricKeyRequestDto("applicationId",
				LocalDateTime.parse("2010-05-01 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "referenceId",
				"");
		requestWrapper.setRequest(symmetricKeyRequestDto);
		String content = mapper.writeValueAsString(requestWrapper);
		MvcResult result = mockMvc.perform(post("/decrypt").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().is(200)).andReturn();
		// System.out.println(result.getResponse().getContentAsString());
	}

	@WithUserDetails("reg-processor")
	@Test
	public void decryptSymmetricKeyWithReferenceId() throws Exception {
		setupSingleKeyAlias();
		setupDBKeyStore();
		setupKey();
		when(keyStoreRepository.findByAlias(Mockito.any())).thenReturn(dbKeyStore);
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(cryptoCore.asymmetricDecrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		doReturn(key.getPrivate().getEncoded()).when(keymanagerUtil).decryptKey(Mockito.any(), Mockito.any());
		SymmetricKeyRequestDto symmetricKeyRequestDto = new SymmetricKeyRequestDto("applicationId",
				LocalDateTime.parse("2010-05-01 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "referenceId",
				"");
		requestWrapper.setRequest(symmetricKeyRequestDto);
		String content = mapper.writeValueAsString(requestWrapper);
		MvcResult result = mockMvc.perform(post("/decrypt").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().is(200)).andReturn();
		// System.out.println(result.getResponse().getContentAsString());
	}


	@WithUserDetails("reg-processor")
	@Test
	public void encryptWithReferenceId() throws Exception {
		
		setupDBKeyStore();
		setupSingleKeyAlias();
		setupKey();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(cryptoCore.sign(Mockito.any(), Mockito.any())).thenReturn("");
		when(keyStore.getAsymmetricKey(Mockito.any())).thenReturn(privateKeyEntry);
		
		doReturn(key.getPrivate().getEncoded()).when(keymanagerUtil).decryptKey(Mockito.any(), Mockito.any());
		SignatureRequestDto encryptDataRequestDto = new SignatureRequestDto();
		encryptDataRequestDto.setApplicationId("applicationId");
		encryptDataRequestDto.setData("AMert334-edrtda");
		encryptDataRequestDto.setReferenceId("referenceId");
		encryptDataRequestDto.setTimeStamp("2010-05-01T12:00:00.00Z");
		RequestWrapper<SignatureRequestDto> encryptRequestWrapper = new RequestWrapper<>();
		encryptRequestWrapper.setId(ID);
		encryptRequestWrapper.setVersion(VERSION);
		encryptRequestWrapper.setRequest(encryptDataRequestDto);

		String content = mapper.writeValueAsString(encryptRequestWrapper);
		MvcResult result = mockMvc.perform(post("/sign").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().is(200)).andReturn();
		
		ResponseWrapper<?> responseWrapper = objectMapper.readValue(result.getResponse().getContentAsString(),
				ResponseWrapper.class);
		SignatureResponseDto signatureResponseDto = objectMapper.readValue(
				objectMapper.writeValueAsString(responseWrapper.getResponse()), SignatureResponseDto.class);

		assertThat(signatureResponseDto.getData(), isA(String.class));
	}
	
	@WithUserDetails("reg-processor")
	@Test
	public void getSignPublicKeyFromHSMMultipleAliasReference() throws Exception {
		setupSingleKeyAlias();
		setupKey();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(cryptoCore.sign(Mockito.any(), Mockito.any())).thenReturn("");
		when(keyStore.getAsymmetricKey(Mockito.any())).thenReturn(privateKeyEntry);
		MvcResult result = mockMvc.perform(get("/publickey/KERNEL?referenceId=SIGN&timeStamp=2010-01-01T12:00:00.000Z"))
				.andExpect(status().is(200)).andReturn();

		ResponseWrapper<?> responseWrapper = objectMapper.readValue(result.getResponse().getContentAsString(),
				ResponseWrapper.class);
		PublicKeyResponse<String> publicKeyResponse = objectMapper.readValue(
				objectMapper.writeValueAsString(responseWrapper.getResponse()), new TypeReference<PublicKeyResponse<String>>(){});

		assertThat(publicKeyResponse.getPublicKey(), isA(String.class));
	}
	

	@WithUserDetails("reg-processor")
	@Test
	public void encryptWithMultipleAliasReferenceId() throws Exception {
		
		setupDBKeyStore();
		setupMultipleKeyAlias();
		setupKey();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(cryptoCore.sign(Mockito.any(), Mockito.any())).thenReturn("");
		when(keyStore.getAsymmetricKey(Mockito.any())).thenReturn(privateKeyEntry);
		
		doReturn(key.getPrivate().getEncoded()).when(keymanagerUtil).decryptKey(Mockito.any(), Mockito.any());
		SignatureRequestDto encryptDataRequestDto = new SignatureRequestDto();
		encryptDataRequestDto.setApplicationId("applicationId");
		encryptDataRequestDto.setData("AMert334-edrtda");
		encryptDataRequestDto.setReferenceId("referenceId");
		encryptDataRequestDto.setTimeStamp("2010-05-01T12:00:00.00Z");
		RequestWrapper<SignatureRequestDto> encryptRequestWrapper = new RequestWrapper<>();
		encryptRequestWrapper.setId(ID);
		encryptRequestWrapper.setVersion(VERSION);
		encryptRequestWrapper.setRequest(encryptDataRequestDto);

		String content = mapper.writeValueAsString(encryptRequestWrapper);
		MvcResult result = mockMvc.perform(post("/sign").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().is(200)).andReturn();
		
		ResponseWrapper<SignatureResponseDto> responseWrapper=objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseWrapper<SignatureResponseDto>>(){});
	    assertThat(responseWrapper.getErrors().get(0).getErrorCode(),is("KER-KMS-003"));
	}
	
	@WithUserDetails("reg-processor")
	@Test
	public void encryptWithEmptyAliasReferenceId() throws Exception {
		
		setupDBKeyStore();
		setupMultipleKeyAlias();
		setupKey();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(cryptoCore.sign(Mockito.any(), Mockito.any())).thenReturn("");
		when(keyStore.getAsymmetricKey(Mockito.any())).thenReturn(privateKeyEntry);
		
		doReturn(key.getPrivate().getEncoded()).when(keymanagerUtil).decryptKey(Mockito.any(), Mockito.any());
		SignatureRequestDto encryptDataRequestDto = new SignatureRequestDto();
		encryptDataRequestDto.setApplicationId("applicationId");
		encryptDataRequestDto.setData("AMert334-edrtda");
		encryptDataRequestDto.setReferenceId("referenceId");
		encryptDataRequestDto.setTimeStamp("2019-05-01T12:00:00.00Z");
		RequestWrapper<SignatureRequestDto> encryptRequestWrapper = new RequestWrapper<>();
		encryptRequestWrapper.setId(ID);
		encryptRequestWrapper.setVersion(VERSION);
		encryptRequestWrapper.setRequest(encryptDataRequestDto);

		String content = mapper.writeValueAsString(encryptRequestWrapper);
		MvcResult result = mockMvc.perform(post("/sign").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().is(200)).andReturn();
		
		ResponseWrapper<SignatureResponseDto> responseWrapper=objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ResponseWrapper<SignatureResponseDto>>(){});
	    assertThat(responseWrapper.getErrors().get(0).getErrorCode(),is("KER-KMS-003"));
	}

}