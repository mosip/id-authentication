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
	 * The API method to encrypt the data using AES Algorithm. Then encrypts the AES
	 * Session Key using RSA and combine the RSA encrypted data and AES encrypted
	 * data.
	 * 
	 * @param dataToEncrypt
	 *            the data to be encrypted in bytes
	 * @return encrypted data as byte array
	 * @throws RegBaseCheckedException
	 *             if any error occurs during encryption
	 */
	byte[] encrypt(final byte[] dataToEncrypt) throws RegBaseCheckedException;
}
