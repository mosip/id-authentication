package io.mosip.registration.service.security;

import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * The {@code RSAEncryptionService} Accepts AES session key as bytes and encrypt
 * it by using RSA algorithm.
 * 
 * It is an asymmetric cryptographic algorithm. Asymmetric means that there are
 * two different keys. This is also called public key cryptography, because one
 * of the keys can be given to anyone. The other key must be kept private.
 * 
 * @author YASWANTH S
 * @author Balaji Sridharan
 *
 */
public interface RSAEncryptionService {

	/**
	 * Encrypts the AES Session Key using RSA encryption algorithm
	 * 
	 * RSA involves a public key and private key. The public key can be known to
	 * everyone; it is used to encrypt messages. Messages encrypted using the public
	 * key can only be decrypted with the private key.
	 * 
	 * @param sessionKey {@code byte[]} has to be encrypted as bytes
	 * 
	 * @return {@code byte[]} rsaEncryptedBytes has encrypted by rsa
	 * 
	 * @throws RegBaseCheckedException {@code
	 *             RegBaseCheckedException} if any occurs
	 *             while encrypting
	 */
	byte[] encrypt(final byte[] sessionKey) throws RegBaseCheckedException;
}
