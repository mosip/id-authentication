package io.mosip.kernel.cryptography.utils;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

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
import io.mosip.kernel.cryptography.dto.CryptographyRequestDto;
import io.mosip.kernel.cryptography.dto.KeyManagerResponseDto;

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
		this.restTemplate=builder.build();
	}

	public PublicKey getPublicKey(CryptographyRequestDto cryptographyRequestDto) {
        PublicKey key = null;
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(getPublicKeyUrl)
                .queryParam("applicationId", cryptographyRequestDto.getApplicationId())
                .queryParam("machineId", cryptographyRequestDto.getMachineId())
                .queryParam("timeStamp", cryptographyRequestDto.getTimeStamp().toString()).build();
      
		KeyManagerResponseDto keyManagerResponseDto = restTemplate
				.getForObject(uriComponents.toUriString(), KeyManagerResponseDto.class);
    try {
			key = KeyFactory.getInstance(asymmetricAlgorithmName).generatePublic(new X509EncodedKeySpec(
							keyManagerResponseDto.getKey()));
		} catch (InvalidKeySpecException e) {
			throw new InvalidKeyException(CryptographyErrorCode.INVALID_SPEC_PUBLIC_KEY.getErrorCode(),
					CryptographyErrorCode.INVALID_SPEC_PUBLIC_KEY.getErrorMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new io.mosip.kernel.core.exception.NoSuchAlgorithmException(CryptographyErrorCode.NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					CryptographyErrorCode.NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}

		return key;
	}

    public SecretKey getDecryptedSymmetricKey(CryptographyRequestDto cryptographyRequestDto) {
    	KeyManagerResponseDto keyManagerResponseDto=restTemplate.postForObject(decryptSymmetricKeyUrl,cryptographyRequestDto, KeyManagerResponseDto.class);
		return new SecretKeySpec(keyManagerResponseDto.getKey(), 0, keyManagerResponseDto.getKey().length, symmetricAlgorithmName);
	}
	
	
	public byte[] combineByteArray(byte[] data, byte[] key) {
		byte[] keySplitter=CryptographyConstant.KEY_SPLITTER.getValue().getBytes();
		byte[] combinedArray = new byte[key.length+keySplitter.length+data.length];
		System.arraycopy(key, 0, combinedArray, 0, key.length);
		System.arraycopy(keySplitter, 0, combinedArray, key.length, keySplitter.length);
		System.arraycopy(data, 0, combinedArray, key.length+keySplitter.length, data.length);
		return combinedArray;
	}
}
