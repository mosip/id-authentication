package io.mosip.kernel.keymanagerservice.service.impl;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.crypto.spi.Decryptor;
import io.mosip.kernel.core.keymanager.spi.KeymanagerInterface;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keymanagerservice.dto.KeyResponseDto;
import io.mosip.kernel.keymanagerservice.entity.AliasMap;
import io.mosip.kernel.keymanagerservice.repository.KeymanagerRepository;
import io.mosip.kernel.keymanagerservice.service.KeymanagerService;

/**
 * This class provides the implementation for the methods of KeymanagerService
 * interface.
 *
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Service
public class KeymanagerServiceImpl implements KeymanagerService {

	/**
	 * Keystore to handles and store cryptographic keys.
	 */
	@Autowired
	KeymanagerInterface keymanagerInterface;

	/**
	 * KeyGenerator instance to generate asymmetric key pairs
	 */
	@Autowired
	KeyGenerator keyGenerator;

	/**
	 * Decryptor instance to decrypt data
	 */
	@Autowired
	Decryptor<PrivateKey, PublicKey, SecretKey> decryptor;

	/**
	 * KeyGenerator instance to generate asymmetric key pairs
	 */
	@Autowired
	KeymanagerRepository keymanagerRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.keymanager.service.KeymanagerService#getPublicKey(java.lang.
	 * String, java.time.LocalDateTime, java.util.Optional)
	 */
	@Override
	public KeyResponseDto getPublicKey(String applicationId, LocalDateTime timeStamp, String machineId) {

		KeyResponseDto keyResponseDto = new KeyResponseDto();
		String alias;
		List<AliasMap> aliasMaps;
		aliasMaps = keymanagerRepository.findByApplicationIdAndMachineId(applicationId, machineId);

		if (aliasMaps.isEmpty()) {
			alias = UUID.randomUUID().toString();
			createNewKeyPair(applicationId, machineId, alias);
		} else {

			aliasMaps.sort((aliasMap1, aliasMap2) -> aliasMap2.getTimeStamp().compareTo(aliasMap1.getTimeStamp()));
			alias = aliasMaps.get(0).getAlias();
			X509Certificate certificate = (X509Certificate) keymanagerInterface.getCertificate(alias);
			try {
				certificate.checkValidity();
			} catch (CertificateExpiredException | CertificateNotYetValidException e) {
				alias = UUID.randomUUID().toString();
				createNewKeyPair(applicationId, machineId, alias);
			}
		}
		PublicKey publicKey = keymanagerInterface.getPublicKey(alias);
		keyResponseDto.setKey(publicKey.getEncoded());
		return keyResponseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.keymanager.service.KeymanagerService#decryptSymmetricKey(java
	 * .lang.String, java.time.LocalDateTime, java.util.Optional, byte[])
	 */
	@Override
	public KeyResponseDto decryptSymmetricKey(String applicationId, LocalDateTime timeStamp, String machineId,
			byte[] encryptedSymmetricKey) {

		String alias;
		List<AliasMap> aliasMaps;
		KeyResponseDto keyResponseDto = new KeyResponseDto();
		aliasMaps = keymanagerRepository.findByApplicationIdAndMachineIdAndTimeStampLessThanEqual(applicationId,
				machineId, timeStamp);
		aliasMaps.sort((aliasMap1, aliasMap2) -> aliasMap2.getTimeStamp().compareTo(aliasMap1.getTimeStamp()));
		aliasMaps.forEach(action -> System.out.println(action));
		alias = aliasMaps.get(0).getAlias();
		PrivateKey privateKey = keymanagerInterface.getPrivateKey(alias);
		byte[] decryptedSymmetricKey = decryptor.asymmetricPrivateDecrypt(privateKey, encryptedSymmetricKey);
		keyResponseDto.setKey(decryptedSymmetricKey);
		return keyResponseDto;
	}

	/**
	 * @param applicationId
	 * @param machineId
	 * @param alias
	 */
	private void createNewKeyPair(String applicationId, String machineId, String alias) {
		KeyPair keyPair = keyGenerator.getAsymmetricKey();
		keymanagerInterface.storeAsymmetricKey(keyPair, alias, 1);
		AliasMap aliasMap = new AliasMap();
		aliasMap.setAlias(alias);
		aliasMap.setApplicationId(applicationId);
		aliasMap.setMachineId(machineId);
		aliasMap.setTimeStamp(LocalDateTime.now());
		keymanagerRepository.create(aliasMap);
	}
}
