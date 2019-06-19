package io.mosip.kernel.cryptosignature.impl;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.signatureutil.exception.SignatureUtilClientException;
import io.mosip.kernel.core.signatureutil.exception.SignatureUtilException;
import io.mosip.kernel.core.signatureutil.model.SignatureResponse;
import io.mosip.kernel.core.signatureutil.spi.SignatureUtil;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.cryptosignature.constant.SigningDataErrorCode;
import io.mosip.kernel.cryptosignature.dto.PublicKeyResponse;
import io.mosip.kernel.cryptosignature.dto.SignatureRequestDto;
import io.mosip.kernel.cryptosignature.exception.ExceptionHandler;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

/**
 * SignatureUtilImpl implements {@link SignatureUtil} .
 * 
 * @author Srinivasan
 * @author Urvil Joshi
 * @author Raj Jha 
 * @since 1.0.0
 */
@Component
public class SignatureUtilImpl implements SignatureUtil {

	@Value("${mosip.kernel.keygenerator.asymmetric-algorithm-name}")
	private String asymmetricAlgorithmName;

	/** The sync data request id. */
	@Value("${mosip.kernel.signature.signature-request-id}")
	private String signDataRequestId;

	/** The sync data version id. */
	@Value("${mosip.kernel.signature.signature-version-id}")
	private String signDataVersionId;

	/** The encrypt url. */
	@Value("${mosip.kernel.keymanager-service-sign-url}")
	private String signUrl;

	/** The get public key url. */
	@Value("${mosip.kernel.keymanager-service-publickey-url}")
	private String getPublicKeyUrl;

	/** The rest template. */
	@Autowired
	RestTemplate restTemplate;

	/** The object mapper. */
	@Autowired
	private ObjectMapper objectMapper;



	/** The sign applicationid. */
	@Value("${mosip.sign.applicationid:KERNEL}")
	private String signApplicationid;

	/** The sign refid. */
	@Value("${mosip.sign.refid:SIGN}")
	private String signRefid;

	private static final String RESPONSE_SOURCE = "Keymanager";

	/** The decryptor. */
	@Autowired
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;

	/** The encryptor. */
	@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;

	/** The key gen. */
	@Autowired
	KeyGenerator keyGen;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.signatureutil.spi.SignatureUtil#validateWithPublicKey(
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean validateWithPublicKey(String signature, String data, String publickey)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		byte[] syncDataBytearray = HMACUtils.generateHash(data.getBytes());
		String actualHash = CryptoUtil.encodeBase64(syncDataBytearray);
		PublicKey key = KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(CryptoUtil.decodeBase64(publickey)));
		byte[] decodedEncryptedData = CryptoUtil.decodeBase64(signature);
		byte[] hashedEncodedData = decryptor.asymmetricPublicDecrypt(key, decodedEncryptedData);
		String signedHash = CryptoUtil.encodeBase64(hashedEncodedData);
		return signedHash.equals(actualHash);
	}

	@Override
	public SignatureResponse sign(String response, String timestamp) {
		String responseHash = CryptoUtil.encodeBase64(HMACUtils.generateHash(response.getBytes()));
		SignatureRequestDto signatureRequestDto = new SignatureRequestDto();
		signatureRequestDto.setApplicationId(signApplicationid);
		signatureRequestDto.setReferenceId(signRefid);
		signatureRequestDto.setData(responseHash);
		signatureRequestDto.setTimeStamp(timestamp);
		RequestWrapper<SignatureRequestDto> requestWrapper = new RequestWrapper<>();
		requestWrapper.setId(signDataRequestId);
		requestWrapper.setVersion(signDataVersionId);
		requestWrapper.setRequest(signatureRequestDto);
		ResponseEntity<String> responseEntity = null;

		try {
			responseEntity = restTemplate.postForEntity(signUrl, requestWrapper, String.class);
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			List<ServiceError> validationErrorsList = ExceptionUtils.getServiceErrorList(ex.getResponseBodyAsString());

			ExceptionHandler.authExceptionHandler(ex, validationErrorsList, RESPONSE_SOURCE);

			if (!validationErrorsList.isEmpty()) {
				throw new SignatureUtilClientException(validationErrorsList);
			} else {
				throw new SignatureUtilException(SigningDataErrorCode.REST_CRYPTO_CLIENT_EXCEPTION.getErrorCode(),
						SigningDataErrorCode.REST_CRYPTO_CLIENT_EXCEPTION.getErrorMessage());
			}
		}
		ExceptionHandler.throwExceptionIfExist(responseEntity);
		SignatureResponse signatureResponse = ExceptionHandler.getResponse(objectMapper, responseEntity,
				SignatureResponse.class);
		signatureResponse.setData(signatureResponse.getData());
		signatureResponse.setTimestamp(DateUtils.convertUTCToLocalDateTime(timestamp));
		return signatureResponse;
	}

	@Override
	public boolean validate(String signature, String actualData, String timestamp)
			throws InvalidKeySpecException, NoSuchAlgorithmException {

		ResponseEntity<String> response = null;
		Map<String, String> uriParams = new HashMap<>();
		uriParams.put("applicationId", signApplicationid);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getPublicKeyUrl)
				.queryParam("timeStamp", timestamp).queryParam("referenceId", signRefid);
		try {
			response = restTemplate.exchange(builder.buildAndExpand(uriParams).toUri(), HttpMethod.GET, null,
					String.class);
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			List<ServiceError> validationErrorsList = ExceptionUtils.getServiceErrorList(ex.getResponseBodyAsString());

			ExceptionHandler.authExceptionHandler(ex, validationErrorsList, RESPONSE_SOURCE);

			if (!validationErrorsList.isEmpty()) {
				throw new SignatureUtilClientException(validationErrorsList);
			} else {
				throw new SignatureUtilException(SigningDataErrorCode.REST_CRYPTO_CLIENT_EXCEPTION.getErrorCode(),
						SigningDataErrorCode.REST_CRYPTO_CLIENT_EXCEPTION.getErrorMessage());
			}

		}
		ExceptionHandler.throwExceptionIfExist(response);
		PublicKeyResponse publicKeyResponse = ExceptionHandler.getResponse(objectMapper, response,
				PublicKeyResponse.class);
		PublicKey publicKey = KeyFactory.getInstance(asymmetricAlgorithmName)
				.generatePublic(new X509EncodedKeySpec(CryptoUtil.decodeBase64(publicKeyResponse.getPublicKey())));
		String decryptedSignature = CryptoUtil
				.encodeBase64(decryptor.asymmetricPublicDecrypt(publicKey, CryptoUtil.decodeBase64(signature)));
		String actualDataHash = CryptoUtil.encodeBase64(HMACUtils.generateHash(actualData.getBytes()));
		return decryptedSignature.equals(actualDataHash);
	}
}
