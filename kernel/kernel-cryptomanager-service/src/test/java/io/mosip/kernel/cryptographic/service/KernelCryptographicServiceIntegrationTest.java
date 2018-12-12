package io.mosip.kernel.cryptographic.service;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;

import javax.crypto.SecretKey;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.cryptomanager.KernelCryptomanagerBootApplication;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerPublicKeyResponseDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerSymmetricKeyResponseDto;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

@SpringBootTest(classes = KernelCryptomanagerBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class KernelCryptographicServiceIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private KeyGenerator generator;

	@Autowired
	private RestTemplate restTemplate;

	@MockBean
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;

	private KeyPair keyPair;

	private MockRestServiceServer server;

	@Before
	public void setUp() {
		keyPair = generator.getAsymmetricKey();
		server = MockRestServiceServer.bindTo(restTemplate).build();
	}

	@Test
	public void testEncrypt() throws Exception {
		KeymanagerPublicKeyResponseDto keymanagerPublicKeyResponseDto = new KeymanagerPublicKeyResponseDto(
				CryptoUtil.encodeBase64(keyPair.getPublic().getEncoded()), LocalDateTime.now(),
				LocalDateTime.now().plusDays(100));
		server.expect(requestTo(
				"http://localhost:8088/keymanager/v1.0/publickey/REGISTRATION?timeStamp=2018-12-06T12:07:44.403&referenceId=ref123"))
				.andRespond(withSuccess(objectMapper.writeValueAsString(keymanagerPublicKeyResponseDto),
						MediaType.APPLICATION_JSON));
		String requestBody = "{\"applicationId\": \"REGISTRATION\",\"data\": \"dXJ2aWw\",\"referenceId\": \"ref123\",\"timeStamp\": \"2018-12-06T12:07:44.403Z\"}";
		MvcResult result = mockMvc
				.perform(post("/v1.0/encrypt").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andReturn();
		CryptomanagerResponseDto cryptomanagerResponseDto = objectMapper
				.readValue(result.getResponse().getContentAsString(), CryptomanagerResponseDto.class);
		assertThat(cryptomanagerResponseDto.getData(), isA(String.class));
	}

	@Test
	public void testDecrypt() throws Exception {
		KeymanagerSymmetricKeyResponseDto keymanagerSymmetricKeyResponseDto = new KeymanagerSymmetricKeyResponseDto(
				CryptoUtil.encodeBase64(generator.getSymmetricKey().getEncoded()));
		server.expect(requestTo("http://localhost:8088/keymanager/v1.0/symmetricKey")).andRespond(withSuccess(
				objectMapper.writeValueAsString(keymanagerSymmetricKeyResponseDto), MediaType.APPLICATION_JSON));
		when(decryptor.symmetricDecrypt(Mockito.any(), Mockito.any())).thenReturn("dXJ2aWw".getBytes());
		String requestBody = "{\"applicationId\": \"uoiuoi\",\"data\": \"dXJ2aWwjS0VZX1NQTElUVEVSI3Vydmls\",\"referenceId\": \"ref123\",\"timeStamp\": \"2018-12-06T12:07:44.403Z\"}";
		MvcResult result = mockMvc
				.perform(post("/v1.0/decrypt").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andReturn();
		CryptomanagerResponseDto cryptomanagerResponseDto = objectMapper
				.readValue(result.getResponse().getContentAsString(), CryptomanagerResponseDto.class);
		assertThat(cryptomanagerResponseDto.getData(), isA(String.class));
	}

}
