/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.service.impl;

import static java.util.Arrays.copyOfRange;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.crypto.dto.CryptoRequestDto;
import io.mosip.kernel.crypto.dto.CryptoResponseDto;
import io.mosip.kernel.crypto.service.CryptoService;
import io.mosip.kernel.crypto.utils.CryptoUtil;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

/** Service Implementation for {@link CryptoService} interface
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Service
public class CryptoServiceImpl implements CryptoService {

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
	 * {@link CryptoUtil} instance
	 */
	@Autowired
	CryptoUtil cryptoUtil; 
	
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
	public CryptoResponseDto encrypt(CryptoRequestDto cryptoRequestDto) {
		SecretKey secretKey=keyGenerator.getSymmetricKey();
		final byte[] encryptedData=encryptor.symmetricEncrypt(secretKey, CryptoUtil.decodeBase64(cryptoRequestDto.getData()));
		PublicKey publicKey=cryptoUtil.getPublicKey(cryptoRequestDto);
		final byte[] encryptedSymmetricKey=encryptor.asymmetricPublicEncrypt(publicKey, secretKey.getEncoded());
		CryptoResponseDto cryptoResponseDto= new CryptoResponseDto();
		cryptoResponseDto.setData(CryptoUtil.encodeBase64(cryptoUtil.combineByteArray(encryptedData, encryptedSymmetricKey)));
		return cryptoResponseDto;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.cryptography.service.CryptographyService#decrypt(io.mosip.kernel.cryptography.dto.CryptographyRequestDto)
	 */
	@Override
	public CryptoResponseDto decrypt(CryptoRequestDto cryptoRequestDto) {
		int keyDemiliterIndex = 0;
		byte[] encryptedHybridData = CryptoUtil.decodeBase64(cryptoRequestDto.getData());
		final int keySplitterLength = keySplitter.length();
		keyDemiliterIndex = cryptoUtil.getSplitterIndex(encryptedHybridData, keyDemiliterIndex,
				keySplitterLength, keySplitter.getBytes()[0]);
        byte[] encryptedKey = copyOfRange(encryptedHybridData, 0, keyDemiliterIndex);
		byte[] encryptedData = copyOfRange(encryptedHybridData, keyDemiliterIndex + keySplitterLength,
				encryptedHybridData.length);
		cryptoRequestDto.setData(CryptoUtil.encodeBase64(encryptedKey));
		SecretKey decryptedSymmetricKey=cryptoUtil.getDecryptedSymmetricKey(cryptoRequestDto);
		CryptoResponseDto cryptoResponseDto= new CryptoResponseDto();
		cryptoResponseDto.setData(CryptoUtil.encodeBase64(decryptor.symmetricDecrypt(decryptedSymmetricKey, encryptedData)));
		return cryptoResponseDto;
	}

	
	
	
}
