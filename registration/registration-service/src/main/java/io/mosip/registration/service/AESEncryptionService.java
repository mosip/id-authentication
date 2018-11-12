package io.mosip.registration.service;

import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * API interface to encrypt the data using AES algorithm
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface AESEncryptionService {

	/**
	 * The API method to encrypt the data using AES Algorithm
	 * 
	 * @param dataToEncrypt
	 * @return encrypted data as byte array
	 * @throws RegBaseCheckedException
	 */
	byte[] encrypt(final byte[] dataToEncrypt) throws RegBaseCheckedException;
}
