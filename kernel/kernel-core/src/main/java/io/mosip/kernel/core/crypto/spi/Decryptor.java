package io.mosip.kernel.core.crypto.spi;

/**
 * Decryptor factory interface for security
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public interface Decryptor<K,P,S,T> {

	/**
	 * Asymmetric Decrypt with private key
	 * 
	 * @param privateKey          key for decryption
	 * @param data                data for decryption
	 * @param mosipSecurityMethod security method for processing
	 * @return Processed array
	 */
	byte[] asymmetricPrivateDecrypt(K privateKey, byte[] data, T mosipSecurityMethod);

	/**
	 * Asymmetric Decrypt with public key
	 * 
	 * @param publicKey           key for decryption
	 * @param data                data for decryption
	 * @param mosipSecurityMethod security method for processing
	 * @return Processed array
	 */
	byte[] asymmetricPublicDecrypt(P publicKey, byte[] data, T mosipSecurityMethod);

	/**
	 * Symmetric Decrypt with key
	 * 
	 * @param key                 key for decryption
	 * @param data                data for decryption
	 * @param mosipSecurityMethod security method for processing
	 */
	byte[] symmetricDecrypt(S key, byte[] data, T mosipSecurityMethod);

}