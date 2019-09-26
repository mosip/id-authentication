package io.mosip.kernel.cryptomanager.test.util;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auth.adapter.exception.AuthNException;
import io.mosip.kernel.auth.adapter.exception.AuthZException;
import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerPublicKeyResponseDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerSymmetricKeyResponseDto;
import io.mosip.kernel.cryptomanager.exception.CryptoManagerSerivceException;
import io.mosip.kernel.cryptomanager.exception.KeymanagerServiceException;
import io.mosip.kernel.cryptomanager.exception.ParseResponseException;
import io.mosip.kernel.cryptomanager.test.CryptoManagerTestBootApplication;
import io.mosip.kernel.cryptomanager.util.CryptomanagerUtils;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

@SpringBootTest(classes = CryptoManagerTestBootApplication.class)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class CryptographicUtilTest {

	@Value("${mosip.kernel.keymanager-service-publickey-url}")
	private String publicKeyUrl;

	@Value("${mosip.kernel.keymanager-service-decrypt-url}")
	private String symmetricKeyUrl;
	
	private String malformedURL="localhost:9090/malformedURL";

	@Autowired
	private CryptomanagerUtils cryptomanagerUtil;

	@MockBean
	private ObjectMapper objectMapper;

	@Autowired
	private KeyGenerator generator;

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * {@link CryptoCoreSpec} instance for cryptographic functionalities.
	 */
	@MockBean
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> cryptoCore;

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
				LocalDateTime.parse("2018-12-06T12:07:44.403"), "dXJ2aWw","ykrkpgjjtChlVdvDNJJEnQ","VGhpcyBpcyBzYW1wbGUgYWFk"));
	}

	@Test(expected = ParseResponseException.class)
	public void testDecrypt() throws Exception {
		KeymanagerSymmetricKeyResponseDto keymanagerSymmetricKeyResponseDto = new KeymanagerSymmetricKeyResponseDto(
				CryptoUtil.encodeBase64(generator.getSymmetricKey().getEncoded()));
		server.expect(requestTo(symmetricKeyUrl)).andRespond(
				withSuccess(map.writeValueAsString(keymanagerSymmetricKeyResponseDto), MediaType.APPLICATION_JSON));
		when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(KeymanagerSymmetricKeyResponseDto.class)))
				.thenThrow(new IOException("IOEXCEPTION"));
		when(cryptoCore.symmetricDecrypt(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn("dXJ2aWw".getBytes());
		cryptomanagerUtil.getDecryptedSymmetricKey(new CryptomanagerRequestDto("REGISTRATION", "ref123",
				LocalDateTime.parse("2018-12-06T12:07:44.403"), "dXJ2aWwjS0VZX1NQTElUVEVSI3Vydmls","ykrkpgjjtChlVdvDNJJEnQ","VGhpcyBpcyBzYW1wbGUgYWFk"));
	}
	
	
	@Test(expected=BadCredentialsException.class)
	public void testAuthenticationDecrypt() throws Exception {
		server.expect(requestTo(symmetricKeyUrl)).andRespond(
				withUnauthorizedRequest());
		when(cryptoCore.symmetricDecrypt(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn("dXJ2aWw".getBytes());
		cryptomanagerUtil.getDecryptedSymmetricKey(new CryptomanagerRequestDto("REGISTRATION", "ref123",
				LocalDateTime.parse("2018-12-06T12:07:44.403"), "dXJ2aWwjS0VZX1NQTElUVEVSI3Vydmls","ykrkpgjjtChlVdvDNJJEnQ","VGhpcyBpcyBzYW1wbGUgYWFk"));
	}
	
	@Test(expected=AccessDeniedException.class)
	public void testAuthorizationDecrypt() throws Exception {
		server.expect(requestTo(symmetricKeyUrl)).andRespond(
				withStatus(HttpStatus.FORBIDDEN));
		when(cryptoCore.symmetricDecrypt(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn("dXJ2aWw".getBytes());
		cryptomanagerUtil.getDecryptedSymmetricKey(new CryptomanagerRequestDto("REGISTRATION", "ref123",
				LocalDateTime.parse("2018-12-06T12:07:44.403"), "dXJ2aWwjS0VZX1NQTElUVEVSI3Vydmls","ykrkpgjjtChlVdvDNJJEnQ","VGhpcyBpcyBzYW1wbGUgYWFk"));
	}
	
	@Test(expected=AuthNException.class)
	public void testAuthenticationWithValidationListDecrypt() throws Exception {
		ResponseWrapper<?> responseWrapper= new ResponseWrapper<>();
		List<ServiceError> errors = new ArrayList<>();
		ServiceError error= new ServiceError("KER-CRY-401", "Authentication failed");
		errors.add(error);
		responseWrapper.setErrors(errors);
		server.expect(requestTo(symmetricKeyUrl)).andRespond(
				withUnauthorizedRequest().body(map.writeValueAsString(responseWrapper)));
		when(cryptoCore.symmetricDecrypt(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn("dXJ2aWw".getBytes());
		cryptomanagerUtil.getDecryptedSymmetricKey(new CryptomanagerRequestDto("REGISTRATION", "ref123",
				LocalDateTime.parse("2018-12-06T12:07:44.403"), "dXJ2aWwjS0VZX1NQTElUVEVSI3Vydmls","ykrkpgjjtChlVdvDNJJEnQ","VGhpcyBpcyBzYW1wbGUgYWFk"));
	}
	
	@Test(expected=AuthZException.class)
	public void testAuthWithValidationListDecrypt() throws Exception {
		ResponseWrapper<?> responseWrapper= new ResponseWrapper<>();
		List<ServiceError> errors = new ArrayList<>();
		ServiceError error= new ServiceError("KER-CRY-403", "Authorization failed");
		errors.add(error);
		responseWrapper.setErrors(errors);
		server.expect(requestTo(symmetricKeyUrl)).andRespond(
				withStatus(HttpStatus.FORBIDDEN).body(map.writeValueAsString(responseWrapper)));
		when(cryptoCore.symmetricDecrypt(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn("dXJ2aWw".getBytes());
		cryptomanagerUtil.getDecryptedSymmetricKey(new CryptomanagerRequestDto("REGISTRATION", "ref123",
				LocalDateTime.parse("2018-12-06T12:07:44.403"), "dXJ2aWwjS0VZX1NQTElUVEVSI3Vydmls","ykrkpgjjtChlVdvDNJJEnQ","VGhpcyBpcyBzYW1wbGUgYWFk"));
	}
	
	@Test(expected=CryptoManagerSerivceException.class)
	public void testInternelServerException() throws Exception {
		server.expect(requestTo(symmetricKeyUrl)).andRespond(
				withServerError());
	   when(cryptoCore.symmetricDecrypt(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn("dXJ2aWw".getBytes());
		cryptomanagerUtil.getDecryptedSymmetricKey(new CryptomanagerRequestDto("REGISTRATION", "ref123",
				LocalDateTime.parse("2018-12-06T12:07:44.403"), "dXJ2aWwjS0VZX1NQTElUVEVSI3Vydmls","ykrkpgjjtChlVdvDNJJEnQ","VGhpcyBpcyBzYW1wbGUgYWFk"));
	}
	
	@Test(expected=KeymanagerServiceException.class)
	public void testInternelServerWithValidationListException() throws Exception {
		ResponseWrapper<?> responseWrapper= new ResponseWrapper<>();
		List<ServiceError> errors = new ArrayList<>();
		ServiceError error= new ServiceError("KER-CRY-500", "Internal Server Error");
		errors.add(error);
		responseWrapper.setErrors(errors);
		server.expect(requestTo(symmetricKeyUrl)).andRespond(
				withServerError().body(map.writeValueAsString(responseWrapper)));
	    when(cryptoCore.symmetricDecrypt(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn("dXJ2aWw".getBytes());
		cryptomanagerUtil.getDecryptedSymmetricKey(new CryptomanagerRequestDto("REGISTRATION", "ref123",
				LocalDateTime.parse("2018-12-06T12:07:44.403"), "dXJ2aWwjS0VZX1NQTElUVEVSI3Vydmls","ykrkpgjjtChlVdvDNJJEnQ","VGhpcyBpcyBzYW1wbGUgYWFk"));
	}
	
	@Test(expected=KeymanagerServiceException.class)
	public void testPublicKeyInternelServerWithValidationListException() throws Exception {
		ResponseWrapper<?> responseWrapper= new ResponseWrapper<>();
		List<ServiceError> errors = new ArrayList<>();
		ServiceError error= new ServiceError("KER-CRY-500", "Internal Server Error");
		errors.add(error);
		responseWrapper.setErrors(errors);
		server.expect(requestTo(builder.buildAndExpand(uriParams).toUriString())).andRespond(
				withServerError().body(map.writeValueAsString(responseWrapper)));
		when(objectMapper.readValue(Mockito.anyString(), Mockito.eq(KeymanagerPublicKeyResponseDto.class)))
				.thenThrow(new IOException("IOEXCEPTION"));
		cryptomanagerUtil.getPublicKey(new CryptomanagerRequestDto("REGISTRATION", "ref123",
				LocalDateTime.parse("2018-12-06T12:07:44.403"), "dXJ2aWw","ykrkpgjjtChlVdvDNJJEnQ","VGhpcyBpcyBzYW1wbGUgYWFk"));
	}

}
