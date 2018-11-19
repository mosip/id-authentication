/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.cryptography.service.impl;

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

/**
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Service
public class CryptographyServiceImpl implements CryptographyService {

	/**
	 * 
	 */
	@Value("${mosip.kernel.packet-key-splitter}")
	private String keySplitter;
	
	/**
	 * 
	 */
	@Autowired
	KeyGenerator keyGenerator;
	
	/**
	 * 
	 */
	@Autowired
	CryptographyUtil cryptographyUtil; 
	
	/**
	 * 
	 */
	@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;
	
	/**
	 * 
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
		byte[] encryptedData=encryptor.symmetricEncrypt(secretKey, cryptographyRequestDto.getData());
		PublicKey publicKey=cryptographyUtil.getPublicKey(cryptographyRequestDto);
		byte[] encryptedSymmetricKey=encryptor.asymmetricPublicEncrypt(publicKey, secretKey.getEncoded());
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
		String[] splitedData=new String(cryptographyRequestDto.getData()).split(keySplitter);
		cryptographyRequestDto.setData(splitedData[0].getBytes());
		SecretKey decryptedSymmetricKey=cryptographyUtil.getDecryptedSymmetricKey(cryptographyRequestDto);
		CryptographyResponseDto cryptographyResponseDto= new CryptographyResponseDto();
		cryptographyResponseDto.setData(decryptor.symmetricDecrypt(decryptedSymmetricKey, splitedData[1].getBytes()));
		return cryptographyResponseDto;
	}
}
