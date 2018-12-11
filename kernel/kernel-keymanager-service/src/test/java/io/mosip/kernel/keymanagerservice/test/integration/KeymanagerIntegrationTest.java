package io.mosip.kernel.keymanagerservice.test.integration;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.keymanager.spi.KeyStore;
import io.mosip.kernel.keymanagerservice.constant.KeymanagerConstant;
import io.mosip.kernel.keymanagerservice.dto.SymmetricKeyRequestDto;
import io.mosip.kernel.keymanagerservice.entity.KeyAlias;
import io.mosip.kernel.keymanagerservice.entity.KeyPolicy;
import io.mosip.kernel.keymanagerservice.repository.KeyAliasRepository;
import io.mosip.kernel.keymanagerservice.repository.KeyPolicyRepository;
import io.mosip.kernel.keymanagerservice.repository.KeyStoreRepository;
import io.mosip.kernel.keymanagerservice.util.KeymanagerUtil;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class KeymanagerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private KeyStore keyStore;

	@MockBean
	private KeyAliasRepository keyAliasRepository;

	@MockBean
	private KeyPolicyRepository keyPolicyRepository;

	@MockBean
	private KeyStoreRepository keyStoreRepository;

	@MockBean
	private Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;

	@Mock
	private PublicKey publicKey;

	@Mock
	private PrivateKey privateKey;

	@SpyBean
	private KeymanagerUtil keymanagerUtil;

	private KeyPair key;
	private ObjectMapper mapper;
	private List<KeyAlias> keyalias;
	private Optional<KeyPolicy> keyPolicy;
	private Optional<io.mosip.kernel.keymanagerservice.entity.KeyStore> dbKeyStore;

	@Before
	public void init() {
		mapper = new ObjectMapper();
		keyalias = new ArrayList<>();
		keyPolicy = Optional.empty();
		dbKeyStore = Optional.empty();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
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
	}

	@Test
	public void getPublicKeyFromHSMMultipleAlias() throws Exception {
		setupMultipleKeyAlias();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		MvcResult result = mockMvc.perform(get("/v1.0/publickey/1?timeStamp=2010-01-01T12:00"))
				.andExpect(status().is(500)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void getPublicKeyFromHSMMultipleAliasReference() throws Exception {
		setupMultipleKeyAlias();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		MvcResult result = mockMvc.perform(get("/v1.0/publickey/1?referenceId= &timeStamp=2010-01-01T12:00"))
				.andExpect(status().is(500)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void getPublicKeyFromHSMSingleAlias() throws Exception {
		setupSingleKeyAlias();
		when(keyStore.getPublicKey(Mockito.any())).thenReturn(publicKey);
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		MvcResult result = mockMvc.perform(get("/v1.0/publickey/1?timeStamp=2011-01-01T12:00"))
				.andExpect(status().is(200)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void getPublicKeyFromHSMEmptyAliasException() throws Exception {
		when(keyStore.getPublicKey(Mockito.any())).thenReturn(publicKey);
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(keyPolicyRepository.findByApplicationId(Mockito.any())).thenReturn(keyPolicy);
		MvcResult result = mockMvc.perform(get("/v1.0/publickey/1?timeStamp=2010-05-01T10:00"))
				.andExpect(status().is(500)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void getPublicKeyFromHSMEmptyAlias() throws Exception {
		setupExpiryPolicy();
		setupSingleKeyAlias();
		when(keyStore.getPublicKey(Mockito.any())).thenReturn(publicKey);
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(keyPolicyRepository.findByApplicationId(Mockito.any())).thenReturn(keyPolicy);

		MvcResult result = mockMvc.perform(get("/v1.0/publickey/1?timeStamp=2009-05-01T10:00"))
				.andExpect(status().is(200)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void getPublicKeyFromHSMEmptyAliasNotOverlapping() throws Exception {
		setupExpiryPolicy();
		setupSingleKeyAlias();
		when(keyStore.getPublicKey(Mockito.any())).thenReturn(publicKey);
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(keyPolicyRepository.findByApplicationId(Mockito.any())).thenReturn(keyPolicy);

		MvcResult result = mockMvc.perform(get("/v1.0/publickey/1?timeStamp=2001-05-01T10:00"))
				.andExpect(status().is(200)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void getPublicKeyFromDBMultipleAlias() throws Exception {
		setupMultipleKeyAlias();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		MvcResult result = mockMvc.perform(get("/v1.0/publickey/REGISTRATION?referenceId=1&timeStamp=2010-05-01T10:00"))
				.andExpect(status().is(500)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void getPublicKeyFromDBSingleAliasException() throws Exception {
		setupSingleKeyAlias();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(keyStoreRepository.findByAlias(Mockito.any())).thenReturn(dbKeyStore);
		MvcResult result = mockMvc.perform(get("/v1.0/publickey/REGISTRATION?referenceId=1&timeStamp=2010-01-01T12:00"))
				.andExpect(status().is(500)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void getPublicKeyFromDBSingleAlias() throws Exception {
		setupSingleKeyAlias();
		setupDBKeyStore();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(keyStoreRepository.findByAlias(Mockito.any())).thenReturn(dbKeyStore);
		MvcResult result = mockMvc.perform(get("/v1.0/publickey/REGISTRATION?referenceId=1&timeStamp=2010-05-01T10:00"))
				.andExpect(status().is(200)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void getPublicKeyFromDBEmptyAliasCryptoException() throws Exception {
		setupExpiryPolicy();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(keyStoreRepository.findByAlias(Mockito.any())).thenReturn(dbKeyStore);
		when(keyPolicyRepository.findByApplicationId(Mockito.any())).thenReturn(keyPolicy);
		MvcResult result = mockMvc.perform(get("/v1.0/publickey/REGISTRATION?referenceId=1&timeStamp=2010-05-01T10:00"))
				.andExpect(status().is(500)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void getPublicKeyFromDBEmptyAlias() throws Exception {
		setupExpiryPolicy();
		setupKey();
		doReturn(key.getPrivate().getEncoded()).when(keymanagerUtil).encryptKey(Mockito.any(), Mockito.any());
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(keyStoreRepository.findByAlias(Mockito.any())).thenReturn(dbKeyStore);
		when(keyPolicyRepository.findByApplicationId(Mockito.any())).thenReturn(keyPolicy);
		MvcResult result = mockMvc.perform(get("/v1.0/publickey/REGISTRATION?referenceId=1&timeStamp=2010-05-01T10:00"))
				.andExpect(status().is(200)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void decryptSymmetricKeyException() throws Exception {
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		SymmetricKeyRequestDto symmetricKeyRequestDto = new SymmetricKeyRequestDto("applicationId",
				LocalDateTime.parse("2010-05-01 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "", "");
		String content = mapper.writeValueAsString(symmetricKeyRequestDto);
		MvcResult result = mockMvc
				.perform(post("/v1.0/symmetrickey").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().is(500)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void decryptSymmetricKey() throws Exception {
		setupSingleKeyAlias();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(decryptor.asymmetricPrivateDecrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		SymmetricKeyRequestDto symmetricKeyRequestDto = new SymmetricKeyRequestDto("applicationId",
				LocalDateTime.parse("2010-05-01 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), null, "");
		String content = mapper.writeValueAsString(symmetricKeyRequestDto);
		MvcResult result = mockMvc
				.perform(post("/v1.0/symmetrickey").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().is(201)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void decryptSymmetricKeyWithReferenceIdException() throws Exception {
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(decryptor.asymmetricPrivateDecrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		SymmetricKeyRequestDto symmetricKeyRequestDto = new SymmetricKeyRequestDto("applicationId",
				LocalDateTime.parse("2010-05-01 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "referenceId",
				"");
		String content = mapper.writeValueAsString(symmetricKeyRequestDto);
		MvcResult result = mockMvc
				.perform(post("/v1.0/symmetrickey").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().is(500)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void decryptSymmetricKeyWithReferenceIdMultipleAliasException() throws Exception {
		setupMultipleKeyAlias();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(decryptor.asymmetricPrivateDecrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		SymmetricKeyRequestDto symmetricKeyRequestDto = new SymmetricKeyRequestDto("applicationId",
				LocalDateTime.parse("2010-05-01 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "referenceId",
				"");
		String content = mapper.writeValueAsString(symmetricKeyRequestDto);
		MvcResult result = mockMvc
				.perform(post("/v1.0/symmetrickey").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().is(500)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void decryptSymmetricKeyWithReferenceIdDBException() throws Exception {
		setupSingleKeyAlias();
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(decryptor.asymmetricPrivateDecrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		SymmetricKeyRequestDto symmetricKeyRequestDto = new SymmetricKeyRequestDto("applicationId",
				LocalDateTime.parse("2010-05-01 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "referenceId",
				"");
		String content = mapper.writeValueAsString(symmetricKeyRequestDto);
		MvcResult result = mockMvc
				.perform(post("/v1.0/symmetrickey").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().is(500)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void decryptSymmetricKeyWithReferenceIdCryptoException() throws Exception {
		setupSingleKeyAlias();
		setupDBKeyStore();
		when(keyStoreRepository.findByAlias(Mockito.any())).thenReturn(dbKeyStore);
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(decryptor.asymmetricPrivateDecrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		doReturn("".getBytes()).when(keymanagerUtil).decryptKey(Mockito.any(), Mockito.any());
		SymmetricKeyRequestDto symmetricKeyRequestDto = new SymmetricKeyRequestDto("applicationId",
				LocalDateTime.parse("2010-05-01 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "referenceId",
				"");
		String content = mapper.writeValueAsString(symmetricKeyRequestDto);
		MvcResult result = mockMvc
				.perform(post("/v1.0/symmetrickey").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().is(500)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void decryptSymmetricKeyWithReferenceId() throws Exception {
		setupSingleKeyAlias();
		setupDBKeyStore();
		setupKey();
		when(keyStoreRepository.findByAlias(Mockito.any())).thenReturn(dbKeyStore);
		when(keyAliasRepository.findByApplicationIdAndReferenceId(Mockito.any(), Mockito.any())).thenReturn(keyalias);
		when(decryptor.asymmetricPrivateDecrypt(Mockito.any(), Mockito.any())).thenReturn("".getBytes());
		doReturn(key.getPrivate().getEncoded()).when(keymanagerUtil).decryptKey(Mockito.any(), Mockito.any());
		SymmetricKeyRequestDto symmetricKeyRequestDto = new SymmetricKeyRequestDto("applicationId",
				LocalDateTime.parse("2010-05-01 12:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "referenceId",
				"");
		String content = mapper.writeValueAsString(symmetricKeyRequestDto);
		MvcResult result = mockMvc
				.perform(post("/v1.0/symmetrickey").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(status().is(201)).andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

}