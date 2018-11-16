package io.mosip.kernel.keymanager.service.impl;

import java.security.KeyPair;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.keymanager.spi.KeymanagerInterface;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
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
	public byte[] getPublicKey(String appId, LocalDateTime timeStamp, Optional<String> machineId) {

		String alias = appId;

		keymanagerRepository.findByApplicationId(alias);

		KeyPair keyPair = keyGenerator.getAsymmetricKey();

		keymanagerInterface.storeAsymmetricKey(keyPair, alias, 365);

		PublicKey publicKey = keymanagerInterface.getPublicKey(alias);

		return publicKey.getEncoded();
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
