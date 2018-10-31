package io.mosip.registration.test.util.rsa;

import java.security.PrivateKey;

/**
 * RSADecryption is used to decrypt the data
 * 
 * @author YASWANTH S
 *
 * @since 1.0.0
 */
public interface RSADecryption {

	/**
	 * rsa encrypted bytes has been decrypted by rsa private key
	 * @param rsaEncryptedBytes fhas to be decryption
	 * @param privateKey for decryption
	 * @return rsa decrypted byte[]
	 */
	byte[] decryptRsaEncryptedBytes(byte[] rsaEncryptedBytes, PrivateKey privateKey);
}
