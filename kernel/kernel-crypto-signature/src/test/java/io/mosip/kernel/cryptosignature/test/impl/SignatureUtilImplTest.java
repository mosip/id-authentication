package io.mosip.kernel.cryptosignature.test.impl;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.signatureutil.exception.ParseResponseException;
import io.mosip.kernel.core.signatureutil.exception.SignatureUtilClientException;
import io.mosip.kernel.core.signatureutil.exception.SignatureUtilException;
import io.mosip.kernel.core.signatureutil.model.SignatureResponse;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.cryptosignature.dto.PublicKeyResponse;
import io.mosip.kernel.cryptosignature.dto.SignatureRequestDto;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

@SpringBootTest
@RunWith(SpringRunner.class)

public class SignatureUtilImplTest {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${mosip.kernel.signature.signature-request-id}")
	private String syncDataRequestId;

	@Value("${mosip.kernel.signature.signature-version-id}")
	private String syncDataVersionId;

	@Value("${mosip.kernel.keymanager-service-sign-url}")
	private String encryptUrl;

	@Value("${mosip.kernel.keymanager-service-publickey-url}")
	private String getPublicKeyUrl;

	@Value("${mosip.sign.applicationid:KERNEL}")
	private String signApplicationid;

	@Value("${mosip.sign.refid:SIGN}")
	private String signRefid;

	@Autowired
	private ObjectMapper objectMapper;

	private MockRestServiceServer server;

	@Autowired
	private KeyGenerator generator;

	@Autowired
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;

	@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	@Autowired
	private SignatureUtil signingUtil;

	private KeyPair keyPair;

	private SignatureRequestDto cryptoManagerRequestDto;

	private RequestWrapper<SignatureRequestDto> requestWrapper;

	private UriComponentsBuilder builder;

	private Map<String, String> uriParams;

	@Before
	public void setUp() {
		server = MockRestServiceServer.bindTo(restTemplate).build();
		cryptoManagerRequestDto = new SignatureRequestDto();
		cryptoManagerRequestDto.setApplicationId("KERNEL");
		cryptoManagerRequestDto.setReferenceId("KER");
		cryptoManagerRequestDto.setData("MOSIP");
		cryptoManagerRequestDto.setTimeStamp(DateUtils.getUTCCurrentDateTimeString());
		requestWrapper = new RequestWrapper<>();

		keyPair = generator.getAsymmetricKey();
		server = MockRestServiceServer.bindTo(restTemplate).build();
		uriParams = new HashMap<>();
		uriParams.put("applicationId", "KERNEL");
		builder = UriComponentsBuilder.fromUriString(getPublicKeyUrl)
				.queryParam("timeStamp", "2019-09-09T09:09:09.000Z").queryParam("referenceId", "SIGN");

	}

	@Test
	public void signResponseData() throws JsonProcessingException {

		requestWrapper.setId(syncDataRequestId);
		requestWrapper.setVersion(syncDataVersionId);
		requestWrapper.setRequest(cryptoManagerRequestDto);
		SignatureResponse signatureResponse = new SignatureResponse();
		signatureResponse.setData(
				"jxAq1SysvWKK78C-2TduZDn2ACJLXReYjM4rWsd2KBSVat_wFxU5D_tiNUvI7gZ9hEGZbhcnQ5n0z8TsAMD3VYFc8WBVIjGsskE7ijhlVHjP3wsP4G1llj0eWcwLAido9K5iwSeeGbT7bJzsiVJTsqtZKRvHFj8gBW0T76jpviri2joYxJY3xD7f2HiA0dbVHzUiD5D8NkYZmQwlYMTeSNoHPYn2hq4Bt22YAjdIlQNNTxlUu1XM7P7eR-unRXXPsl9wDw6Gl1xzgN3SOE-WqmI3oIq61JvZiXhi_SKIt_RqMwymUHmTlb1MQfGB32ip6nPR1xdU3ArGRAuvYnmIGA");

		ResponseWrapper<SignatureResponse> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(signatureResponse);
		String response = objectMapper.writeValueAsString(responseWrapper);
		server.expect(requestTo(encryptUrl))
				.andRespond(withSuccess().body(response).contentType(MediaType.APPLICATION_JSON));

		signingUtil.sign("MOSIP", DateUtils.getUTCCurrentDateTimeString());
	}

	@Test(expected = SignatureUtilClientException.class)
	public void signResponseDataErrorTest() throws JsonProcessingException {

		requestWrapper.setId(syncDataRequestId);
		requestWrapper.setVersion(syncDataVersionId);
		requestWrapper.setRequest(cryptoManagerRequestDto);

		ResponseWrapper<ServiceError> responseWrapper = new ResponseWrapper<>();
		List<ServiceError> errors = new ArrayList<>();
		ServiceError serviceError = new ServiceError("KER-CRY-001", "No Such algorithm is supported");
		errors.add(serviceError);
		responseWrapper.setErrors(errors);

		String response = objectMapper.writeValueAsString(responseWrapper);
		server.expect(requestTo(encryptUrl))
				.andRespond(withSuccess().body(response).contentType(MediaType.APPLICATION_JSON));

		signingUtil.sign("MOSIP", DateUtils.getUTCCurrentDateTimeString());
	}

	@Test(expected = ParseResponseException.class)
	public void signResponseDataExceptionTest() throws JsonProcessingException {

		String response = "{\"id\": \"string\",\"version\": \"string\",\"responsetime\": \"2019-04-06T12:52:32.450Z\",\"metadata\": null,\"response\": {\"data\": \"n7AvMtZ_nHb2AyD9IrXfA6sG9jc8IEgmkIYN2pVFaJ9Qw8v1JEMgneL0lVR-},\"errors\": null}";
		server.expect(requestTo(encryptUrl))
				.andRespond(withSuccess().body(response).contentType(MediaType.APPLICATION_JSON));

		signingUtil.sign("MOSIP", DateUtils.getUTCCurrentDateTimeString());
	}

	@Test(expected = SignatureUtilClientException.class)
	public void signResponseDataClientTest() throws JsonProcessingException {
		ResponseWrapper<List<ServiceError>> responseWrapper = new ResponseWrapper<>();
		ServiceError serviceError = new ServiceError("KER-KYM-004", "No such alias found---->sdasd-dsfsdf-sdfdsf");
		List<ServiceError> serviceErrors = new ArrayList<>();
		serviceErrors.add(serviceError);
		responseWrapper.setErrors(serviceErrors);
		String response = objectMapper.writeValueAsString(responseWrapper);
		server.expect(requestTo(encryptUrl))
				.andRespond(withBadRequest().body(response).contentType(MediaType.APPLICATION_JSON));

		signingUtil.sign("MOSIP", DateUtils.getUTCCurrentDateTimeString());
	}

	@Test(expected = SignatureUtilException.class)
	public void signResponseDataClientServiceErrorTest() throws JsonProcessingException {
		ResponseWrapper<List<ServiceError>> responseWrapper = new ResponseWrapper<>();
		List<ServiceError> serviceErrors = new ArrayList<>();
		responseWrapper.setErrors(serviceErrors);
		String response = objectMapper.writeValueAsString(responseWrapper);
		server.expect(requestTo(encryptUrl))
				.andRespond(withBadRequest().body(response).contentType(MediaType.APPLICATION_JSON));

		signingUtil.sign("MOSIP", DateUtils.getUTCCurrentDateTimeString());
	}

	@Test
	public void validateWithPublicKeyTest() throws InvalidKeySpecException, NoSuchAlgorithmException {

		PrivateKey privateKey = keyPair.getPrivate();
		byte[] hashedData = HMACUtils.generateHash("admin".getBytes());
		byte[] encryptedData = encryptor.asymmetricPrivateEncrypt(privateKey, hashedData);
		boolean isVerfied = signingUtil.validateWithPublicKey(CryptoUtil.encodeBase64(encryptedData), "admin",
				CryptoUtil.encodeBase64(keyPair.getPublic().getEncoded()));
		assertTrue(isVerfied);
	}

	@Test
	public void validateMethodTest() throws InvalidKeySpecException, NoSuchAlgorithmException, JsonProcessingException {
		PublicKeyResponse keymanagerPublicKeyResponseDto = new PublicKeyResponse("alias",
				CryptoUtil.encodeBase64(keyPair.getPublic().getEncoded()), LocalDateTime.now(),
				LocalDateTime.now().plusDays(100));
		ResponseWrapper<PublicKeyResponse> response = new ResponseWrapper<>();
		response.setResponse(keymanagerPublicKeyResponseDto);
		server.expect(requestTo(builder.buildAndExpand(uriParams).toUriString()))
				.andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));
		PrivateKey privateKey = keyPair.getPrivate();
		byte[] hashedData = HMACUtils.generateHash("signedData".getBytes());
		byte[] encryptedData = encryptor.asymmetricPrivateEncrypt(privateKey, hashedData);
		boolean isVerfied = signingUtil.validate(CryptoUtil.encodeBase64(encryptedData), "signedData",
				"2019-09-09T09:09:09.000Z");
		assertTrue(isVerfied);
	}

	@Test(expected = SignatureUtilException.class)
	public void validateMethodExceptionTest()
			throws InvalidKeySpecException, NoSuchAlgorithmException, JsonProcessingException {
		PublicKeyResponse keymanagerPublicKeyResponseDto = new PublicKeyResponse("alias",
				CryptoUtil.encodeBase64(keyPair.getPublic().getEncoded()), LocalDateTime.now(),
				LocalDateTime.now().plusDays(100));
		ResponseWrapper<PublicKeyResponse> response = new ResponseWrapper<>();
		response.setResponse(keymanagerPublicKeyResponseDto);
		server.expect(requestTo(builder.buildAndExpand(uriParams).toUriString())).andRespond(withServerError());
		signingUtil.validate("dfdsfdsfdsfds", "signedData", "2019-09-09T09:09:09.000Z");
	}

	@Test(expected = ParseResponseException.class)
	public void validateIOExceptionTest()
			throws InvalidKeySpecException, NoSuchAlgorithmException, JsonProcessingException {

		server.expect(requestTo(builder.buildAndExpand(uriParams).toUriString())).andRespond(withSuccess().body(
				"{ \"id\": null, \"version\": null, \"responsetime\": \"2019-04-25T16:58:11.344Z\", \"metadata\": null, \"response\": {\"alias\":\"alias\", \"publicKey\": \"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtCR2L_MwUv4ctfGulWf4ZoWkSyBHbfkVtE_xAmzzIDWHP1V5hGxg8jt8hLtYYFwBNj4l_PTZGkblcVg-IePHilmQiVDptTVVA2PGtwRdud7QL4xox8RXmIf-xa-JmP2E804iVM-Ki8aPf1yuxXNUwLxZsflFww73lc-SGVUHupD8Os0qNZbbJl0BYioNG4WmPMHy3WJ-7jGN0HEV-9E18yf_enR0YewUmUI6Rxxb606-w8iQyWfSJq6UOfFmH5WAn-oTOoTIwg_fBxXuG_FlDoNWs6N5JtI18BMsUQA_GQZJct6TyXcBNUrcBYhZERvPlRGqIOoTl-T2sPJ5ST9eswIDAQAB\", \"issuedAt\": \"2019-04-09T05:51:17.334\", \"expiryAt\": \"2020-04-09T05:51:17.334\" , \"errors\": null }"));
		signingUtil.validate("dfdsfdsfdsfds", "signedData", "2019-09-09T09:09:09.000Z");
	}
}
