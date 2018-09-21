package org.mosip.registration.processor.service.packet.encryption.rsa;

import java.security.PublicKey;

/**
 * RSAEncryption is used to encrypt the data
 * 
 * @author YASWANTH S
 * @since 1.0.0
 */
public interface RSAEncryption {

	/**
	 * aes encrypted bytes has been encrypted by rsa public key
	 * @param sessionKey has to be encrypted
	 * @param publicKey for encryption
	 * @return rsa encrypted byte[]
	 */
	byte[] encrypt(byte[] sessionKey, PublicKey publicKey);
}
