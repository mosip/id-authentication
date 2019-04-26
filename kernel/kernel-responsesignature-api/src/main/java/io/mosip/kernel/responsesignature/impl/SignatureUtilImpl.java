package io.mosip.kernel.responsesignature.impl;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.signatureutil.dto.CryptoManagerResponseDto;
import io.mosip.kernel.core.signatureutil.exception.ParseResponseException;
import io.mosip.kernel.core.signatureutil.exception.SignatureUtilClientException;
import io.mosip.kernel.core.signatureutil.exception.SignatureUtilException;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.responsesignature.constant.SigningDataErrorCode;
import io.mosip.kernel.responsesignature.dto.CryptoManagerRequestDto;
import io.mosip.kernel.responsesignature.dto.KeymanagerPublicKeyResponseDto;

/**
 * SignatureUtilImpl implements {@link SignatureUtil} .
 * 
 * @author Srinivasan
 * @since 1.0.0
 */
@Component
public class SignatureUtilImpl implements SignatureUtil {

	/** The sync data request id. */
	@Value("${mosip.kernel.signature.signature-request-id}")
	private String syncDataRequestId;

	/** The sync data version id. */
	@Value("${mosip.kernel.signature.signature-version-id}")
	private String syncDataVersionId;

	/** The encrypt url. */
	@Value("${mosip.kernel.signature.cryptomanager-encrypt-url}")
	private String encryptUrl;

	/** The get public key url. */
	@Value("${mosip.kernel.keymanager-service-publickey-url}")
	private String getPublicKeyUrl;

	/** The rest template. */
	@Autowired
	RestTemplate restTemplate;

	/** The object mapper. */
	@Autowired
	private ObjectMapper objectMapper;

	/** The signed header. */
	@Value("${mosip.sign.header:response-signature}")
	private String signedHeader;

	/** The sign applicationid. */
	@Value("${mosip.sign.applicationid:KERNEL}")
	private String signApplicationid;

	/** The sign refid. */
	@Value("${mosip.sign.refid:KER}")
	private String signRefid;

	/** The decryptor. */
	@Autowired
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;

	/** The encryptor. */
	@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	/** The key gen. */
	@Autowired
	KeyGenerator keyGen;

	/**
	 * Sign response.
	 *
	 * @param response
	 *            the response
	 * @return the string
	 */
	@Override
	public CryptoManagerResponseDto signResponse(String response) {
		byte[] responseByteArray = HMACUtils.generateHash(response.getBytes());
		CryptoManagerRequestDto cryptoManagerRequestDto = new CryptoManagerRequestDto();
		cryptoManagerRequestDto.setApplicationId(signApplicationid);
		cryptoManagerRequestDto.setReferenceId(signRefid);
		cryptoManagerRequestDto.setData(CryptoUtil.encodeBase64(responseByteArray));
		cryptoManagerRequestDto.setTimeStamp(DateUtils.getUTCCurrentDateTimeString());
		RequestWrapper<CryptoManagerRequestDto> requestWrapper = new RequestWrapper<>();
		requestWrapper.setId(syncDataRequestId);
		requestWrapper.setVersion(syncDataVersionId);
		requestWrapper.setRequest(cryptoManagerRequestDto);
		ResponseEntity<String> responseEntity = null;

		try {
			responseEntity = restTemplate.postForEntity(encryptUrl, requestWrapper, String.class);
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			List<ServiceError> validationErrorsList = ExceptionUtils.getServiceErrorList(ex.getResponseBodyAsString());

			if (!validationErrorsList.isEmpty()) {
				throw new SignatureUtilClientException(validationErrorsList);
			} else {
				throw new SignatureUtilException(SigningDataErrorCode.REST_CLIENT_EXCEPTION.getErrorCode(),
						SigningDataErrorCode.REST_CLIENT_EXCEPTION.getErrorMessage());
			}
		}
		List<ServiceError> validationErrorsList = null;
		validationErrorsList = ExceptionUtils.getServiceErrorList(responseEntity.getBody());

		if (!validationErrorsList.isEmpty()) {
			throw new SignatureUtilClientException(validationErrorsList);
		}
		CryptoManagerResponseDto cryptoManagerResponseDto = null;
		ResponseWrapper<CryptoManagerResponseDto> responseObject;
		try {

			responseObject = objectMapper.readValue(responseEntity.getBody(),
					new TypeReference<ResponseWrapper<CryptoManagerResponseDto>>() {
					});

			cryptoManagerResponseDto = responseObject.getResponse();
			cryptoManagerResponseDto.setResponseTime(responseObject.getResponsetime());
		} catch (IOException | NullPointerException exception) {
			throw new ParseResponseException(SigningDataErrorCode.RESPONSE_PARSE_EXCEPTION.getErrorCode(),
					SigningDataErrorCode.RESPONSE_PARSE_EXCEPTION.getErrorMessage());
		}

		return cryptoManagerResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.signatureutil.spi.SignatureUtil#validateWithPublicKey(
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean validateWithPublicKey(String responseSignature, String responseBody, String publicKey)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		byte[] syncDataBytearray = HMACUtils.generateHash(responseBody.getBytes());
		String actualHash = CryptoUtil.encodeBase64(syncDataBytearray);
		System.out.println("Actual Hash: " + actualHash);
		PublicKey key = null;
		key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(CryptoUtil.decodeBase64(publicKey)));
		byte[] decodedEncryptedData = CryptoUtil.decodeBase64(responseSignature);
		byte[] hashedEncodedData = decryptor.asymmetricPublicDecrypt(key, decodedEncryptedData);
		String signedHash = new String(hashedEncodedData);
		System.out.println("Signed Hash: " + signedHash);
		return signedHash.equals(actualHash);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.signatureutil.spi.SignatureUtil#validateWithPublicKey(
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public boolean validate(String responseSignature, String responseBody, String responsetime)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		Map<String, String> uriParams = new HashMap<>();
		ResponseEntity<String> keyManagerResponse = null;
		uriParams.put("applicationId", signApplicationid);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://qa.mosip.io/v1/keymanager/publickey/KERNEL")
				.queryParam("timeStamp", responsetime).queryParam("referenceId", signRefid);

		try {
			keyManagerResponse = restTemplate.exchange(builder.buildAndExpand(uriParams).toUri(), HttpMethod.GET, null,
					String.class);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			throw new SignatureUtilException(SigningDataErrorCode.REST_CLIENT_EXCEPTION.getErrorCode(),
					SigningDataErrorCode.REST_CLIENT_EXCEPTION.getErrorMessage());
		}
		String keyResponseBody = keyManagerResponse.getBody();
		ExceptionUtils.getServiceErrorList(responseBody);
		KeymanagerPublicKeyResponseDto keyManagerResponseDto = null;
		ResponseWrapper<?> keyResponseWrp;
		try {
			keyResponseWrp = objectMapper.readValue(keyResponseBody, ResponseWrapper.class);
			objectMapper.registerModule(new JavaTimeModule());
			keyManagerResponseDto = objectMapper.readValue(
					objectMapper.writeValueAsString(keyResponseWrp.getResponse()),
					KeymanagerPublicKeyResponseDto.class);
		} catch (IOException e) {
			throw new SignatureUtilException(SigningDataErrorCode.RESPONSE_PARSE_EXCEPTION.getErrorCode(),
					SigningDataErrorCode.RESPONSE_PARSE_EXCEPTION.getErrorMessage());
		}
		byte[] syncDataBytearray = HMACUtils.generateHash(responseBody.getBytes());
		String actualHash = CryptoUtil.encodeBase64(syncDataBytearray);
		System.out.println("Actual Hash: " + actualHash);
		PublicKey key = null;
		key = KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(CryptoUtil.decodeBase64(keyManagerResponseDto.getPublicKey())));
		byte[] decodedEncryptedData = CryptoUtil.decodeBase64(responseSignature);
		byte[] hashedEncodedData = decryptor.asymmetricPublicDecrypt(key, decodedEncryptedData);
		String signedHash = new String(hashedEncodedData);
		System.out.println("Signed Hash: " + signedHash);
		return signedHash.equals(actualHash);
	}

}
