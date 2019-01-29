package io.mosip.registration.service;

import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * Accepts AES session key as bytes and encrypt it by using RSA algorithm
 * 
 * @author YASWANTH S
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface RSAEncryptionService {

	/**
	 * Encrypts the AES Session Key using RSA encryption algorithm
	 * 
	 * @param sessionKey
	 *            has to be encrypted
	 * @return rsaEncryptedBytes has encrypted by rsa
	 * @throws RegBaseCheckedException
	 */
	byte[] encrypt(final byte[] sessionKey) throws RegBaseCheckedException;
}
