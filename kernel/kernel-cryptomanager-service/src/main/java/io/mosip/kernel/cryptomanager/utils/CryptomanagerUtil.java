/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptomanager.utils;

import static java.util.Arrays.copyOfRange;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.cryptomanager.constant.CryptomanagerErrorCode;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerPublicKeyResponseDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerSymmetricKeyRequestDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerSymmetricKeyResponseDto;

/**
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Component
public class CryptomanagerUtil {

	/**
	 * 
	 */
	@Value("${mosip.kernel.keygenerator.asymmetric-algorithm-name}")
	private String asymmetricAlgorithmName;

	/**
	 * 
	 */
	@Value("${mosip.kernel.keygenerator.symmetric-algorithm-name}")
	private String symmetricAlgorithmName;

	/**
	 * 
	 */
	@Value("${mosip.kernel.keymanager-service-publickey-url}")
	private String getPublicKeyUrl;

	/**
	 * 
	 */
	@Value("${mosip.kernel.keymanager-service-decrypt-url}")
	private String decryptSymmetricKeyUrl;
	
	/**
	 * 
	 */
	@Value("${mosip.kernel.data-key-splitter}")
	private String keySplitter;

	/**
	 * Data Mapper instance.
	 */
	@Autowired
	private DataMapper dataMapper;
	
	/**
	 * 
	 */
	private RestTemplate restTemplate;

	/**
	 * @param builder
	 */
	@Autowired
	public CryptomanagerUtil(RestTemplateBuilder builder) {
		this.restTemplate = builder.build();
	}
	
	
	/**
	 * @param cryptoRequestDto
	 * @return
	 */
	public PublicKey getPublicKey(CryptomanagerRequestDto cryptoRequestDto) {
		PublicKey key = null;
		Map<String, String> uriParams = new HashMap<>();
		uriParams.put("applicationId", cryptoRequestDto.getApplicationId());
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getPublicKeyUrl)
		        .queryParam("timeStamp", cryptoRequestDto.getTimeStamp())
		        .queryParam("referenceId", cryptoRequestDto.getReferenceId());
		try {
			KeymanagerPublicKeyResponseDto keyManagerResponseDto = restTemplate.getForObject(builder.buildAndExpand(uriParams).toUri(),KeymanagerPublicKeyResponseDto.class);
			key = KeyFactory.getInstance(asymmetricAlgorithmName).generatePublic(new X509EncodedKeySpec(CryptomanagerUtil.decodeBase64(keyManagerResponseDto.getPublicKey())));
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
	 * @param cryptoRequestDto
	 * @return
	 */
	public SecretKey getDecryptedSymmetricKey(CryptomanagerRequestDto cryptoRequestDto) {
		
		KeymanagerSymmetricKeyRequestDto keyManagerSymmetricKeyRequestDto= new KeymanagerSymmetricKeyRequestDto();
		dataMapper.map(cryptoRequestDto, keyManagerSymmetricKeyRequestDto,new KeymanagerSymmetricKeyConverter());
		KeymanagerSymmetricKeyResponseDto keyManagerSymmetricKeyResponseDto = restTemplate.postForObject(decryptSymmetricKeyUrl,keyManagerSymmetricKeyRequestDto,KeymanagerSymmetricKeyResponseDto.class);
		byte[] symmetricKey=decodeBase64(keyManagerSymmetricKeyResponseDto.getSymmetricKey());
		return new SecretKeySpec(symmetricKey, 0,symmetricKey.length, symmetricAlgorithmName);
	}

	/**
	 * @param data
	 * @param key
	 * @return
	 */
	public byte[] combineByteArray(byte[] data, byte[] key) {
		byte[] keySplitterBytes =keySplitter.getBytes();
		byte[] combinedArray = new byte[key.length + keySplitterBytes.length + data.length];
		System.arraycopy(key, 0, combinedArray, 0, key.length);
		System.arraycopy(keySplitterBytes, 0, combinedArray, key.length,keySplitterBytes.length);
		System.arraycopy(data, 0, combinedArray,key.length + keySplitterBytes.length, data.length);
		return combinedArray;
	}
	
	/**
	 * @param cryptoRequestDto
	 * @param keyDemiliterIndex
	 * @param cipherKeyandDataLength
	 * @param keySplitterLength
	 * @param keySplitterFirstByte
	 * @return
	 */
	public int getSplitterIndex(byte[] encryptedData, int keyDemiliterIndex,
			 final int keySplitterLength, final byte keySplitterFirstByte) {
		
		for (byte data:encryptedData) {
			if (data == keySplitterFirstByte) {
				final String keySplit = new String(copyOfRange(encryptedData, keyDemiliterIndex, keyDemiliterIndex + keySplitterLength));
				if (keySplitter.equals(keySplit)) {
					break;
				}
			}
			keyDemiliterIndex++;
		}
		return keyDemiliterIndex;
	}
	
	/**
	 * @param data
	 * @return
	 */
	public static String encodeBase64(byte[] data) {
		return Base64.encodeBase64URLSafeString(data);
	}
	
	/**
	 * @param data
	 * @return
	 */
	public static byte[] decodeBase64(String data) {
		return Base64.decodeBase64(data);
	}

}
