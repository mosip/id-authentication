package io.mosip.kernel.cryptography.service.impl;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.crypto.spi.Encryptor;
import io.mosip.kernel.cryptography.constant.CryptographyConstant;
import io.mosip.kernel.cryptography.dto.CryptographyResponseDto;
import io.mosip.kernel.cryptography.service.CryptographyService;
import io.mosip.kernel.cryptography.utils.CryptographyUtil;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

@Service
public class CryptographyServiceImpl implements CryptographyService {

	@Autowired
	KeyGenerator keyGenerator;
	
	@Autowired
	CryptographyUtil cryptographyUtil; 
	
	@Autowired
	Encryptor<PrivateKey, PublicKey, SecretKey> encryptor;
	
	@Autowired 
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;
	
    @Override
	public CryptographyResponseDto encrypt(String applicationId, byte[] data,
			LocalDateTime timeStamp, Optional<String> machineId) {
		SecretKey secretKey=keyGenerator.getSymmetricKey();
		byte[] encryptedData=encryptor.symmetricEncrypt(secretKey, data);
		PublicKey publicKey=cryptographyUtil.getPublicKey(applicationId,machineId,timeStamp);
		byte[] encryptedSymmetricKey=encryptor.asymmetricPublicEncrypt(publicKey, secretKey.getEncoded());
		CryptographyResponseDto cryptographyResponseDto=new CryptographyResponseDto();
		cryptographyResponseDto.setData(cryptographyUtil.combineByteArray(encryptedData, encryptedSymmetricKey));
		return cryptographyResponseDto;
	}

	@Override
	public CryptographyResponseDto decrypt(String applicationId, byte[] data,
			LocalDateTime timeStamp, Optional<String> machineId) {
		String[] splitedData=new String(data).split(CryptographyConstant.KEY_SPLITTER.getValue());
		SecretKey decryptedSymmetricKey=cryptographyUtil.getDecryptedSymmetricKey(applicationId,splitedData[0].getBytes(),timeStamp,machineId);
		CryptographyResponseDto cryptographyResponseDto= new CryptographyResponseDto();
	    cryptographyResponseDto.setData(decryptor.symmetricDecrypt(decryptedSymmetricKey, splitedData[1].getBytes()));	
		return cryptographyResponseDto;
	}

}
