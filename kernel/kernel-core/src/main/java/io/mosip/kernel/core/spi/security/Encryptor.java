package io.mosip.kernel.core.spi.security;

/**
 * Encryptor factory interface for security
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public interface Encryptor<K,P,S,T> {

	/**
	 * Asymmetric Encrypt with private key
	 * 
	 * @param privateKey          key for encryption
	 * @param data                data for encryption
	 * @param mosipSecurityMethod security method for processing
	 * @return Processed array
	 */
	byte[] asymmetricPrivateEncrypt(K privateKey, byte[] data, T mosipSecurityMethod);

	/**
	 * Asymmetric Encrypt with public key
	 * 
	 * @param publicKey           key for encryption
	 * @param data                data for encryption
	 * @param mosipSecurityMethod security method for processing
	 * @return Processed array
	 */
	byte[] asymmetricPublicEncrypt(P publicKey, byte[] data, T mosipSecurityMethod);

	/**
	 * Symmetric Encrypt with key
	 * 
	 * @param key                 key for encryption
	 * @param data                data for encryption
	 * @param mosipSecurityMethod security method for processing
	 * @return Processed array
	 */
	byte[] symmetricEncrypt(S key, byte[] data, T mosipSecurityMethod);

}