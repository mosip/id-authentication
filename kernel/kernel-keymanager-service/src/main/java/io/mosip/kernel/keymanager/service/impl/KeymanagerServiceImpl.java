package io.mosip.kernel.keymanager.service.impl;

import java.security.Key;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.keymanager.spi.KeymanagerInterface;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keymanager.entity.AliasMap;
import io.mosip.kernel.keymanager.repository.KeymanagerRepository;
import io.mosip.kernel.keymanager.service.KeymanagerService;

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
	public byte[] getPublicKey(String applicationId, LocalDateTime timeStamp, Optional<String> machineId) {

		List<String> allAlias = keymanagerInterface.getAllAlias();
		System.out.println("********");
		allAlias.forEach(alias -> {
			Key key = keymanagerInterface.getKey(alias);
			System.out.println(alias + "," + key);
		});
		System.out.println("********");

		PublicKey publicKey = null;
		String currentAlias = null;
		List<AliasMap> aliasMaps = keymanagerRepository.findByApplicationId(applicationId);

		if (aliasMaps.isEmpty()) {
			currentAlias = UUID.randomUUID().toString();
			createNewKeyPair(applicationId, currentAlias);
		} else {
			aliasMaps.sort((aliasMap1, aliasMap2) -> aliasMap2.getTimeStamp().compareTo(aliasMap1.getTimeStamp()));
			currentAlias = aliasMaps.get(0).getAlias();
			X509Certificate certificate = (X509Certificate) keymanagerInterface.getCertificate(currentAlias);
			try {
				certificate.checkValidity();
			} catch (CertificateExpiredException | CertificateNotYetValidException e) {
				currentAlias = UUID.randomUUID().toString();
				createNewKeyPair(applicationId, currentAlias);
			}
		}
		publicKey = keymanagerInterface.getPublicKey(currentAlias);
		return publicKey.getEncoded();
	}

	/**
	 * @param applicationId
	 * @param alias
	 */
	private void createNewKeyPair(String applicationId, String alias) {
		KeyPair keyPair = keyGenerator.getAsymmetricKey();
		keymanagerInterface.storeAsymmetricKey(keyPair, alias, 1);
		AliasMap aliasMap = new AliasMap();
		aliasMap.setAlias(alias);
		aliasMap.setApplicationId(applicationId);
		aliasMap.setTimeStamp(LocalDateTime.now());
		keymanagerRepository.create(aliasMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.keymanager.service.KeymanagerService#decryptSymmetricKey(java
	 * .lang.String, java.time.LocalDateTime, java.util.Optional, byte[])
	 */
	@Override
	public byte[] decryptSymmetricKey(String appId, LocalDateTime timeStamp, Optional<String> machineId,
			byte[] encryptedSymmetricKey) {

		byte[] decryptSymmetricKey = "urvil".getBytes();

		return decryptSymmetricKey;
	}
}
