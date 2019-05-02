package io.mosip.kernel.cryptomanager.test.util;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerPublicKeyResponseDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerSymmetricKeyResponseDto;
import io.mosip.kernel.cryptomanager.exception.ParseResponseException;
import io.mosip.kernel.cryptomanager.test.CryptoManagerTestBootApplication;
import io.mosip.kernel.cryptomanager.utils.CryptomanagerUtil;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

@SpringBootTest(classes = CryptoManagerTestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class CryptographicUtilTest {

	@Value("${mosip.kernel.keymanager-service-publickey-url}")
	private String publicKeyUrl;

	@Value("${mosip.kernel.keymanager-service-decrypt-url}")
	private String symmetricKeyUrl;

	@Autowired
	private CryptomanagerUtil cryptomanagerUtil;

	@MockBean
	private ObjectMapper objectMapper;

	@Autowired
	private KeyGenerator generator;

	@Autowired
	private RestTemplate restTemplate;

	@MockBean
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;

	private KeyPair keyPair;

	private MockRestServiceServer server;

	private UriComponentsBuilder builder;

	private Map<String, String> uriParams;

	private ObjectMapper map;

	@Before
	public void setUp() {
		map = new ObjectMapper();
		keyPair = generator.getAsymmetricKey();
		server = MockRestServiceServer.bindTo(restTemplate).build();
		uriParams = new HashMap<>();
		uriParams.put("applicationId", "REGISTRATION");
		builder = UriComponentsBuilder.fromUriString(publicKeyUrl).queryParam("timeStamp", "2018-12-06T12:07:44.403Z")
				.queryParam("referenceId", "ref123");
	}

	@Test(expected = ParseResponseException.class)
	public void testEncrypt() throws Exception {
		KeymanagerPublicKeyResponseDto keymanagerPublicKeyResponseDto = new KeymanagerPublicKeyResponseDto(
				CryptoUtil.encodeBase64(keyPair.getPublic().getEncoded()), LocalDateTime.now(),
				LocalDateTime.now().plusDays(100));
		server.expect(requestTo(builder.buildAndExpand(uriParams).toUriString())).andRespond(
				withSuccess(map.writeValueAsString(keymanagerPublicKeyResponseDto), MediaType.APPLICATION_JSON));
		when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(KeymanagerPublicKeyResponseDto.class)))
				.thenThrow(new IOException("IOEXCEPTION"));
		cryptomanagerUtil.getPublicKey(new CryptomanagerRequestDto("REGISTRATION", "ref123",
				LocalDateTime.parse("2018-12-06T12:07:44.403"), "dXJ2aWw","ykrkpgjjtChlVdvDNJJEnQ"));
	}

	@Test(expected = ParseResponseException.class)
	public void testDecrypt() throws Exception {
		KeymanagerSymmetricKeyResponseDto keymanagerSymmetricKeyResponseDto = new KeymanagerSymmetricKeyResponseDto(
				CryptoUtil.encodeBase64(generator.getSymmetricKey().getEncoded()));
		server.expect(requestTo(symmetricKeyUrl)).andRespond(
				withSuccess(map.writeValueAsString(keymanagerSymmetricKeyResponseDto), MediaType.APPLICATION_JSON));
		when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(KeymanagerSymmetricKeyResponseDto.class)))
				.thenThrow(new IOException("IOEXCEPTION"));
		when(decryptor.symmetricDecrypt(Mockito.any(), Mockito.any())).thenReturn("dXJ2aWw".getBytes());
		cryptomanagerUtil.getDecryptedSymmetricKey(new CryptomanagerRequestDto("REGISTRATION", "ref123",
				LocalDateTime.parse("2018-12-06T12:07:44.403"), "dXJ2aWwjS0VZX1NQTElUVEVSI3Vydmls","ykrkpgjjtChlVdvDNJJEnQ"));
	}

}
