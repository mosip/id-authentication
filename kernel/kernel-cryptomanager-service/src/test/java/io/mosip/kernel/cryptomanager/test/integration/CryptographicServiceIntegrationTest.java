package io.mosip.kernel.cryptomanager.test.integration;

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
import java.time.ZoneId;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.cryptomanager.dto.CryptoEncryptRequestDto;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;
import io.mosip.kernel.cryptomanager.dto.KeyManagerEncryptResponseDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerPublicKeyResponseDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerSymmetricKeyResponseDto;
import io.mosip.kernel.cryptomanager.test.CryptoManagerTestBootApplication;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

@SpringBootTest(classes = CryptoManagerTestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class CryptographicServiceIntegrationTest {

	@Value("${mosip.kernel.keymanager-service-publickey-url}")
	private String publicKeyUrl;

	@Value("${mosip.kernel.keymanager-service-decrypt-url}")
	private String symmetricKeyUrl;

	@Value("${mosip.kernel.keymanager-service-encrypt-url}")
	private String encryptUrl;

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

	private UriComponentsBuilder builder;

	private Map<String, String> uriParams;

	private CryptomanagerRequestDto requestDto;

	private RequestWrapper<CryptomanagerRequestDto> requestWrapper;

	private static final String ID = "mosip.crypto.service";
	private static final String VERSION = "V1.0";

	@Before
	public void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		requestWrapper = new RequestWrapper<>();
		requestWrapper.setId(ID);
		requestWrapper.setVersion(VERSION);
		requestWrapper.setRequesttime(LocalDateTime.now(ZoneId.of("UTC")));

		keyPair = generator.getAsymmetricKey();
		server = MockRestServiceServer.bindTo(restTemplate).build();
		uriParams = new HashMap<>();
		uriParams.put("applicationId", "REGISTRATION");
		builder = UriComponentsBuilder.fromUriString(publicKeyUrl).queryParam("timeStamp", "2018-12-06T12:07:44.403Z")
				.queryParam("referenceId", "ref123");
	}

	// @WithUserDetails("reg-processor")
	@Test
	public void testEncrypt() throws Exception {
		KeymanagerPublicKeyResponseDto keymanagerPublicKeyResponseDto = new KeymanagerPublicKeyResponseDto(
				CryptoUtil.encodeBase64(keyPair.getPublic().getEncoded()), LocalDateTime.now(),
				LocalDateTime.now().plusDays(100));
		ResponseWrapper<KeymanagerPublicKeyResponseDto> response = new ResponseWrapper<>();
		response.setResponse(keymanagerPublicKeyResponseDto);
		server.expect(requestTo(builder.buildAndExpand(uriParams).toUriString()))
				.andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));

		/* Request body START */
		requestDto = new CryptomanagerRequestDto();
		requestWrapper.setRequest(requestDto);

		/* Set value in CryptomanagerRequestDto */
		requestDto.setApplicationId("REGISTRATION");
		requestDto.setData("dXJ2aWw");
		requestDto.setReferenceId("ref123");
		requestDto.setTimeStamp(DateUtils.parseToLocalDateTime("2018-12-06T12:07:44.403Z"));
		/*
		 * String requestBody =
		 * "{\"applicationId\": \"REGISTRATION\",\"data\": \"dXJ2aWw\",\"referenceId\": \"ref123\",\"timeStamp\": \"2018-12-06T12:07:44.403Z\"}"
		 * ;
		 */
		String requestBody = objectMapper.writeValueAsString(requestWrapper);
		/* Request body END */

		MvcResult result = mockMvc
				.perform(post("/encrypt").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andReturn();

		ResponseWrapper<?> responseWrapper = objectMapper.readValue(result.getResponse().getContentAsString(),
				ResponseWrapper.class);
		/*
		 * CryptomanagerResponseDto cryptomanagerResponseDto = objectMapper
		 * .readValue(result.getResponse().getContentAsString(),
		 * CryptomanagerResponseDto.class);
		 */
		CryptomanagerResponseDto cryptomanagerResponseDto = objectMapper.readValue(
				objectMapper.writeValueAsString(responseWrapper.getResponse()), CryptomanagerResponseDto.class);

		assertThat(cryptomanagerResponseDto.getData(), isA(String.class));
	}

	// @WithUserDetails("reg-processor")
	@Test
	public void testDecrypt() throws Exception {
		KeymanagerSymmetricKeyResponseDto keymanagerSymmetricKeyResponseDto = new KeymanagerSymmetricKeyResponseDto(
				CryptoUtil.encodeBase64(generator.getSymmetricKey().getEncoded()));
		ResponseWrapper<KeymanagerSymmetricKeyResponseDto> response = new ResponseWrapper<>();
		response.setResponse(keymanagerSymmetricKeyResponseDto);
		server.expect(requestTo(symmetricKeyUrl))
				.andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));
		when(decryptor.symmetricDecrypt(Mockito.any(), Mockito.any())).thenReturn("dXJ2aWw".getBytes());

		/* Request body START */
		requestDto = new CryptomanagerRequestDto();
		requestWrapper.setRequest(requestDto);

		/* Set value in CryptomanagerRequestDto */
		requestDto.setApplicationId("uoiuoi");
		requestDto.setData("dXJ2aWwjS0VZX1NQTElUVEVSI3Vydmls");
		requestDto.setReferenceId("ref123");
		requestDto.setTimeStamp(DateUtils.parseToLocalDateTime("2018-12-06T12:07:44.403Z"));
		String requestBody = objectMapper.writeValueAsString(requestWrapper);
		/* Request body END */

		/*
		 * String requestBody =
		 * "{\"applicationId\": \"uoiuoi\",\"data\": \"dXJ2aWwjS0VZX1NQTElUVEVSI3Vydmls\",\"referenceId\": \"ref123\",\"timeStamp\": \"2018-12-06T12:07:44.403Z\"}"
		 * ;
		 */
		MvcResult result = mockMvc
				.perform(post("/decrypt").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andReturn();
		ResponseWrapper<?> responseWrapper = objectMapper.readValue(result.getResponse().getContentAsString(),
				ResponseWrapper.class);
		/*
		 * CryptomanagerResponseDto cryptomanagerResponseDto = objectMapper
		 * .readValue(result.getResponse().getContentAsString(),
		 * CryptomanagerResponseDto.class);
		 */
		CryptomanagerResponseDto cryptomanagerResponseDto = objectMapper.readValue(
				objectMapper.writeValueAsString(responseWrapper.getResponse()), CryptomanagerResponseDto.class);

		assertThat(cryptomanagerResponseDto.getData(), isA(String.class));
	}

	@Test
	public void testEncryptPrivateKey() throws Exception {
		KeyManagerEncryptResponseDto keyManagerEncryptResponseDto = new KeyManagerEncryptResponseDto();
		keyManagerEncryptResponseDto.setEncryptedData("ABRTE43M-wer3-53u");
		ResponseWrapper<KeyManagerEncryptResponseDto> response = new ResponseWrapper<>();
		response.setResponse(keyManagerEncryptResponseDto);
		server.expect(requestTo(encryptUrl))
				.andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));
		RequestWrapper<CryptoEncryptRequestDto> reqWrapper = new RequestWrapper<>();
		CryptoEncryptRequestDto cryptoEncryptRequestDto = new CryptoEncryptRequestDto();
		cryptoEncryptRequestDto.setApplicationId("artvvfd");
		cryptoEncryptRequestDto.setData("AbRCee-0eexcvsRe");
		cryptoEncryptRequestDto.setReferenceId("REG");
		cryptoEncryptRequestDto.setTimeStamp("2018-12-06T12:07:44.403Z");
		reqWrapper.setId(ID);
		reqWrapper.setVersion(VERSION);
		reqWrapper.setRequesttime(DateUtils.parseToLocalDateTime("2018-12-06T12:07:44.403Z"));
		reqWrapper.setRequest(cryptoEncryptRequestDto);
		String requestBody = objectMapper.writeValueAsString(reqWrapper);
		mockMvc.perform(post("/private/encrypt").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk()).andReturn();

	}

}
