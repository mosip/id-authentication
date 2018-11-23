/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptography.utils;

import static java.util.Arrays.copyOfRange;

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

import io.mosip.kernel.core.crypto.exception.InvalidKeyException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.cryptography.constant.CryptographyErrorCode;
import io.mosip.kernel.cryptography.dto.CryptographyRequestDto;
import io.mosip.kernel.cryptography.dto.KeyManagerPublicKeyRequestDto;
import io.mosip.kernel.cryptography.dto.KeyManagerResponseDto;
import io.mosip.kernel.cryptography.dto.KeyManagerSymmetricKeyRequestDto;

/**
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Component
public class CryptographyUtil {

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
	public CryptographyUtil(RestTemplateBuilder builder) {
		this.restTemplate = builder.build();
	}
	
	
	/**
	 * @param cryptographyRequestDto
	 * @return
	 */
	public PublicKey getPublicKey(
			CryptographyRequestDto cryptographyRequestDto) {
		PublicKey key = null;
		KeyManagerPublicKeyRequestDto keyManagerPublicKeyRequestDto = new KeyManagerPublicKeyRequestDto();
	    dataMapper.map(cryptographyRequestDto,keyManagerPublicKeyRequestDto, new KeyManagerPublicKeyConverter());
        KeyManagerResponseDto keyManagerResponseDto = restTemplate.postForObject(getPublicKeyUrl, keyManagerPublicKeyRequestDto,KeyManagerResponseDto.class);
		try {
			key = KeyFactory.getInstance(asymmetricAlgorithmName).generatePublic(new X509EncodedKeySpec(keyManagerResponseDto.getKey()));
		} catch (InvalidKeySpecException e) {
			throw new InvalidKeyException(
					CryptographyErrorCode.INVALID_SPEC_PUBLIC_KEY.getErrorCode(),
					CryptographyErrorCode.INVALID_SPEC_PUBLIC_KEY.getErrorMessage());
		} catch (NoSuchAlgorithmException e) {
			throw new io.mosip.kernel.core.exception.NoSuchAlgorithmException(
					CryptographyErrorCode.NO_SUCH_ALGORITHM_EXCEPTION.getErrorCode(),
					CryptographyErrorCode.NO_SUCH_ALGORITHM_EXCEPTION.getErrorMessage());
		}
		return key;
	}

	
	/**
	 * @param cryptographyRequestDto
	 * @return
	 */
	public SecretKey getDecryptedSymmetricKey(CryptographyRequestDto cryptographyRequestDto) {
		
		KeyManagerSymmetricKeyRequestDto keyManagerSymmetricKeyRequestDto= new KeyManagerSymmetricKeyRequestDto();
		dataMapper.map(cryptographyRequestDto, keyManagerSymmetricKeyRequestDto,new KeyManagerSymmetricKeyConverter());
		KeyManagerResponseDto keyManagerResponseDto = restTemplate.postForObject(decryptSymmetricKeyUrl,keyManagerSymmetricKeyRequestDto,KeyManagerResponseDto.class);
		return new SecretKeySpec(keyManagerResponseDto.getKey(), 0,
				keyManagerResponseDto.getKey().length, symmetricAlgorithmName);
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
	 * @param cryptographyRequestDto
	 * @param keyDemiliterIndex
	 * @param cipherKeyandDataLength
	 * @param keySplitterLength
	 * @param keySplitterFirstByte
	 * @return
	 */
	public int getSplitterIndex(CryptographyRequestDto cryptographyRequestDto, int keyDemiliterIndex,
			 final int keySplitterLength, final byte keySplitterFirstByte) {
		
		for (byte data:cryptographyRequestDto.getData()) {
			if (data == keySplitterFirstByte) {
				final String keySplit = new String(copyOfRange(cryptographyRequestDto.getData(), keyDemiliterIndex, keyDemiliterIndex + keySplitterLength));
				if (keySplitter.equals(keySplit)) {
					break;
				}
			}
			keyDemiliterIndex++;
		}
		return keyDemiliterIndex;
	}

}
