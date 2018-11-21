package io.mosip.kernel.core.crypto.spi;

/**
 * Encryptor factory interface for security
 * @param <K> the type of private key
 * @param <P> the type of public key
 * @param <S> the type of symmetric key
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public interface Encryptor<K,P,S> {

	/**
	 * Asymmetric Encrypt with private key
	 * 
	 * @param privateKey          key for encryption
	 * @param data                data for encryption
	 * @return Processed array
	 */
	byte[] asymmetricPrivateEncrypt(K privateKey, byte[] data);

	/**
	 * Asymmetric Encrypt with public key
	 * 
	 * @param publicKey           key for encryption
	 * @param data                data for encryption
	 * @return Processed array
	 */
	byte[] asymmetricPublicEncrypt(P publicKey, byte[] data);

	/**
	 * Symmetric Encrypt with key
	 * 
	 * @param key                 key for encryption
	 * @param data                data for encryption
	 * @return Processed array
	 */
	byte[] symmetricEncrypt(S key, byte[] data);

}