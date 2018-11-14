package io.mosip.kernel.cryptography.utils;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.cryptography.constant.CryptographyConstant;
import io.mosip.kernel.cryptography.constant.CryptographyErrorCode;

@Component
public class CryptographyUtil {

	@Value("${mosip.kernel.keygenerator.bouncycastle.asymmetric-algorithm-name}")
	private String asymmetricAlgorithmName;

	@Value("${mosip.kernel.keygenerator.bouncycastle.symmetric-algorithm-name}")
	private String symmetricAlgorithmName;

	@Value("${mosip.kernel.keymanager-service-getPublickey-url}")
	private String getPublicKeyUrl;

	@Value("${mosip.kernel.keymanager-service-decryptSymmetricKey-url}")
	private String decryptSymmetricKeyUrl;

	private RestTemplate restTemplate;

	@Autowired
	public CryptographyUtil(RestTemplateBuilder builder) {
		this.restTemplate = builder.build();
	}

	public PublicKey getPublicKey(String applicationId,
			Optional<String> machineId, LocalDateTime timeStamp) {
		PublicKey key = null;
		Map<String, String> uriParams = new HashMap<>();
		uriParams.put("appId", applicationId);
		UriComponents uriComponents = UriComponentsBuilder
				.fromHttpUrl(getPublicKeyUrl)
                .queryParam("machineId", machineId)
				.queryParam("timeStamp", timeStamp).build();

		byte[] publicKey = restTemplate
				.getForObject(uriComponents.toUriString(), byte[].class,uriParams);
		try {
			key = KeyFactory.getInstance(asymmetricAlgorithmName)
					.generatePublic(new X509EncodedKeySpec(publicKey));
		} catch (InvalidKeySpecException e) {
			throw new InvalidKeyException(
					CryptographyErrorCode.INVALID_SPEC_PUBLIC_KEY
							.getErrorCode(),
					CryptographyErrorCode.INVALID_SPEC_PUBLIC_KEY
							.getErrorMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new io.mosip.kernel.core.exception.NoSuchAlgorithmException(
					CryptographyErrorCode.NO_SUCH_ALGORITHM_EXCEPTION
							.getErrorCode(),
					CryptographyErrorCode.NO_SUCH_ALGORITHM_EXCEPTION
							.getErrorMessage());
		}

		return key;
	}

	public SecretKey getDecryptedSymmetricKey(String applicationId,
			byte[] encryptedSymmetricKey, LocalDateTime timeStamp, Optional<String> machineId) {
		Map<String, String> uriParams = new HashMap<>();
		uriParams.put("appId", applicationId);
		UriComponents uriComponents = UriComponentsBuilder
				.fromHttpUrl(decryptSymmetricKeyUrl)
				.queryParam("machineId", machineId)
				.queryParam("timeStamp", timeStamp).build();
		byte[] decryptedSymmetricKey = restTemplate.postForObject(uriComponents.toUriString(), encryptedSymmetricKey, byte[].class,uriParams);
		return new SecretKeySpec(decryptedSymmetricKey, 0,
				decryptedSymmetricKey.length, symmetricAlgorithmName);
	}

	public byte[] combineByteArray(byte[] data, byte[] key) {
		byte[] keySplitter = CryptographyConstant.KEY_SPLITTER.getValue()
				.getBytes();
		byte[] combinedArray = new byte[key.length + keySplitter.length
				+ data.length];
		System.arraycopy(key, 0, combinedArray, 0, key.length);
		System.arraycopy(keySplitter, 0, combinedArray, key.length,
				keySplitter.length);
		System.arraycopy(data, 0, combinedArray,
				key.length + keySplitter.length, data.length);
		return combinedArray;
	}

}
