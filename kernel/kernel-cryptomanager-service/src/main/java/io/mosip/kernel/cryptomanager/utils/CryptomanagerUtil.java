/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptomanager.utils;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.cryptomanager.constant.CryptomanagerErrorCode;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerPublicKeyResponseDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerSymmetricKeyRequestDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerSymmetricKeyResponseDto;

/**
 * Util class for this project
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Component
public class CryptomanagerUtil {

	/**
	 *  Asymmetric Algorithm Name
	 */
	@Value("${mosip.kernel.keygenerator.asymmetric-algorithm-name}")
	private String asymmetricAlgorithmName;

	/**
	 * Symmetric Algorithm Name
	 */
	@Value("${mosip.kernel.keygenerator.symmetric-algorithm-name}")
	private String symmetricAlgorithmName;

	/**
	 * Keymanager URL to Get PublicKey
	 */
	@Value("${mosip.kernel.keymanager-service-publickey-url}")
	private String getPublicKeyUrl;

	/**
	 * Keymanager URL to Decrypt Symmetric key
	 */
	@Value("${mosip.kernel.keymanager-service-decrypt-url}")
	private String decryptSymmetricKeyUrl;
	
	/**
	 * Key Splitter
	 */
	@Value("${mosip.kernel.data-key-splitter}")
	private String keySplitter;

	/**
	 * {@link DataMapper} instance.
	 */
	@Autowired
	private DataMapper dataMapper;
	
	/**
	 * {@link RestTemplate} instance
	 */
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * Calls Key-Manager-Service to get public key of an application
	 * 
	 * @param cryptomanagerRequestDto
	 *            {@link CryptomanagerRequestDto} instance
	 * @return {@link PublicKey} returned by Key Manager Service
	 */
	public PublicKey getPublicKey(CryptomanagerRequestDto cryptomanagerRequestDto) {
		PublicKey key = null;
		Map<String, String> uriParams = new HashMap<>();
		uriParams.put("applicationId", cryptomanagerRequestDto.getApplicationId());
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getPublicKeyUrl)
		        .queryParam("timeStamp", cryptomanagerRequestDto.getTimeStamp())
		        .queryParam("referenceId", cryptomanagerRequestDto.getReferenceId());
		try {
			KeymanagerPublicKeyResponseDto keyManagerResponseDto = restTemplate
					.getForObject(builder.buildAndExpand(uriParams).toUri(), KeymanagerPublicKeyResponseDto.class);
			key = KeyFactory.getInstance(asymmetricAlgorithmName).generatePublic(
					new X509EncodedKeySpec(CryptoUtil.decodeBase64(keyManagerResponseDto.getPublicKey())));
		} catch (InvalidKeySpecException e) {
			throw new InvalidKeyException(
					CryptomanagerErrorCode.INVALID_SPEC_PUBLIC_KEY.getErrorCode(),
					CryptomanagerErrorCode.INVALID_SPEC_PUBLIC_KEY.getErrorMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new io.mosip.kernel.core.exception.NoSuchAlgorithmException(
					CryptomanagerErrorCode.NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					CryptomanagerErrorCode.NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
		return key;
	}

	
	/**
	 * Calls Key-Manager-Service to decrypt symmetric key
	 * 
	 * @param cryptomanagerRequestDto
	 *            {@link CryptomanagerRequestDto} instance
	 * @return Decrypted {@link SecretKey} from Key Manager Service
	 */
    public SecretKey getDecryptedSymmetricKey(CryptomanagerRequestDto cryptomanagerRequestDto) {
		KeymanagerSymmetricKeyRequestDto keyManagerSymmetricKeyRequestDto = new KeymanagerSymmetricKeyRequestDto();
		dataMapper.map(cryptomanagerRequestDto, keyManagerSymmetricKeyRequestDto,new KeymanagerSymmetricKeyConverter());
		KeymanagerSymmetricKeyResponseDto keyManagerSymmetricKeyResponseDto = restTemplate.postForObject(
				decryptSymmetricKeyUrl, keyManagerSymmetricKeyRequestDto, KeymanagerSymmetricKeyResponseDto.class);
		byte[] symmetricKey = CryptoUtil.decodeBase64(keyManagerSymmetricKeyResponseDto.getSymmetricKey());
		return new SecretKeySpec(symmetricKey, 0, symmetricKey.length, symmetricAlgorithmName);
    }

}
