/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptography.service.impl;

import static java.util.Arrays.copyOfRange;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.cryptography.dto.CryptographyRequestDto;
import io.mosip.kernel.cryptography.dto.CryptographyResponseDto;
import io.mosip.kernel.cryptography.service.CryptographyService;
import io.mosip.kernel.cryptography.utils.CryptographyUtil;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

/** Service Implementation for {@link CryptographyService} interface
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Service
public class CryptographyServiceImpl implements CryptographyService {

	/**
	 * KeySplitter for splitting key and data
	 */
	@Value("${mosip.kernel.data-key-splitter}")
	private String keySplitter;
	
	/**
	 * {@link KeyGenerator} instance
	 */
	@Autowired
	KeyGenerator keyGenerator;
	
	/**
	 * {@link CryptographyUtil} instance
	 */
	@Autowired
	CryptographyUtil cryptographyUtil; 
	
	/**
	 * {@link Encryptor} instance
	 */
	@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;
	
	/**
	 * {@link Decryptor} instance
	 */
	@Autowired 
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;
	
    

	/* (non-Javadoc)
	 * @see io.mosip.kernel.cryptography.service.CryptographyService#encrypt(io.mosip.kernel.cryptography.dto.CryptographyRequestDto)
	 */
	@Override
	public CryptographyResponseDto encrypt(
			CryptographyRequestDto cryptographyRequestDto) {
		SecretKey secretKey=keyGenerator.getSymmetricKey();
		final byte[] encryptedData=encryptor.symmetricEncrypt(secretKey, cryptographyRequestDto.getData());
		PublicKey publicKey=cryptographyUtil.getPublicKey(cryptographyRequestDto);
		final byte[] encryptedSymmetricKey=encryptor.asymmetricPublicEncrypt(publicKey, secretKey.getEncoded());
		CryptographyResponseDto cryptographyResponseDto= new CryptographyResponseDto();
		cryptographyResponseDto.setData(cryptographyUtil.combineByteArray(encryptedData, encryptedSymmetricKey));
		return cryptographyResponseDto;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.cryptography.service.CryptographyService#decrypt(io.mosip.kernel.cryptography.dto.CryptographyRequestDto)
	 */
	@Override
	public CryptographyResponseDto decrypt(
			CryptographyRequestDto cryptographyRequestDto) {
		int keyDemiliterIndex = 0;
		final int cipherKeyandDataLength = cryptographyRequestDto.getData().length;
		final int keySplitterLength = keySplitter.length();
		final byte keySplitterFirstByte = keySplitter.getBytes()[0];

		keyDemiliterIndex = cryptographyUtil.getSplitterIndex(cryptographyRequestDto, keyDemiliterIndex,
				keySplitterLength, keySplitterFirstByte);

		byte[] encryptedKey = copyOfRange(cryptographyRequestDto.getData(), 0, keyDemiliterIndex);
		byte[] encryptedData = copyOfRange(cryptographyRequestDto.getData(), keyDemiliterIndex + keySplitterLength,
				cipherKeyandDataLength);
		
		cryptographyRequestDto.setData(encryptedKey);
		SecretKey decryptedSymmetricKey=cryptographyUtil.getDecryptedSymmetricKey(cryptographyRequestDto);
		CryptographyResponseDto cryptographyResponseDto= new CryptographyResponseDto();
		cryptographyResponseDto.setData(decryptor.symmetricDecrypt(decryptedSymmetricKey, encryptedData));
		return cryptographyResponseDto;
	}

	
	
	
}
