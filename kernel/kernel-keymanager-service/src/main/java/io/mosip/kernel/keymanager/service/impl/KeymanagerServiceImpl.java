package io.mosip.kernel.keymanager.service.impl;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.keymanager.spi.SofthsmKeystore;
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

	@Autowired
	SofthsmKeystore softhsmKeystore;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.keymanager.service.KeymanagerService#getPublicKey(java.lang.
	 * String, java.time.LocalDateTime, java.util.Optional)
	 */
	@Override
	public byte[] getPublicKey(String appId, LocalDateTime timeStamp, Optional<String> machineId) {

		List<String> allAlias = softhsmKeystore.getAllAlias();

		allAlias.forEach(alias -> {
			Key key = softhsmKeystore.getKey(alias);
			System.out.println(alias + "," + key);
		});

		byte[] publicKey = "urvil".getBytes();

		return publicKey;
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
