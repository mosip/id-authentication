/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptomanager.service.impl;

import static java.util.Arrays.copyOfRange;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerResponseDto;
import io.mosip.kernel.cryptomanager.service.CryptomanagerService;
import io.mosip.kernel.cryptomanager.utils.CryptomanagerUtil;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

/** Service Implementation for {@link CryptomanagerService} interface
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Service
public class CryptomanagerServiceImpl implements CryptomanagerService {

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
	 * {@link CryptomanagerUtil} instance
	 */
	@Autowired
	CryptomanagerUtil cryptoUtil; 
	
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
	public CryptomanagerResponseDto encrypt(CryptomanagerRequestDto cryptoRequestDto) {
		SecretKey secretKey=keyGenerator.getSymmetricKey();
		final byte[] encryptedData=encryptor.symmetricEncrypt(secretKey, CryptoUtil.decodeBase64(cryptoRequestDto.getData()));
		PublicKey publicKey=cryptoUtil.getPublicKey(cryptoRequestDto);
		final byte[] encryptedSymmetricKey=encryptor.asymmetricPublicEncrypt(publicKey, secretKey.getEncoded());
		CryptomanagerResponseDto cryptoResponseDto= new CryptomanagerResponseDto();
		cryptoResponseDto.setData(CryptoUtil.encodeBase64(CryptoUtil.combineByteArray(encryptedData, encryptedSymmetricKey,keySplitter)));
		return cryptoResponseDto;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.cryptography.service.CryptographyService#decrypt(io.mosip.kernel.cryptography.dto.CryptographyRequestDto)
	 */
	@Override
	public CryptomanagerResponseDto decrypt(CryptomanagerRequestDto cryptoRequestDto) {
		int keyDemiliterIndex = 0;
		byte[] encryptedHybridData = CryptoUtil.decodeBase64(cryptoRequestDto.getData());
		final int keySplitterLength = keySplitter.length();
		keyDemiliterIndex = CryptoUtil.getSplitterIndex(encryptedHybridData, keyDemiliterIndex,
				keySplitterLength,keySplitter);
        byte[] encryptedKey = copyOfRange(encryptedHybridData, 0, keyDemiliterIndex);
		byte[] encryptedData = copyOfRange(encryptedHybridData, keyDemiliterIndex + keySplitterLength,
				encryptedHybridData.length);
		cryptoRequestDto.setData(CryptoUtil.encodeBase64(encryptedKey));
		SecretKey decryptedSymmetricKey=cryptoUtil.getDecryptedSymmetricKey(cryptoRequestDto);
		CryptomanagerResponseDto cryptoResponseDto= new CryptomanagerResponseDto();
		cryptoResponseDto.setData(CryptoUtil.encodeBase64(decryptor.symmetricDecrypt(decryptedSymmetricKey, encryptedData)));
		return cryptoResponseDto;
	}

	
	
	
}
