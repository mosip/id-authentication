package io.mosip.registration.service.security;

import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * The {@code AESEncryptionService} is to encrypt the data using AES algorithm
 * 
 * AES is an iterative rather than Feistel cipher. It is based on
 * ‘substitution–permutation network’. It comprises of a series of linked
 * operations, some of which involve replacing inputs by specific outputs
 * (substitutions) and others involve shuffling bits around (permutations)
 * 
 * @author Balaji Sridharan
 * 
 */
public interface AESEncryptionService {

	/**
	 * The API method to encrypt the data using AES Algorithm. Then encrypts the AES
	 * Session Key using RSA and combine the RSA encrypted data and AES encrypted
	 * data.
	 * 
	 *<p> The key size used for an AES cipher specifies the number of transformation
	 * rounds that convert the input, called the plain text, into the final output,
	 * called the cipher text.</p>
	 * 
	 * @param dataToEncrypt {@code byte[]} the data to be encrypted in bytes
	 * 
	 * @return {@code byte[]} encrypted data as byte array
	 * 
	 * @throws RegBaseCheckedException {@code
	 *             RegBaseCheckedException} if any error
	 *             occurs during encryption
	 */
	byte[] encrypt(final byte[] dataToEncrypt) throws RegBaseCheckedException;
}
