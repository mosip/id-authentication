/*
 * 
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.jce.algorithm;

import javax.crypto.SecretKey;

import io.mosip.kernel.crypto.jce.constant.SecurityMethod;
import io.mosip.kernel.crypto.jce.processor.SymmetricProcessor;

/**
 * AES Initialization for Mosip
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class AES extends SymmetricProcessor {

	/**
	 * Constructor for this class
	 */
	private AES() {
	}

	/**
	 * AES Engine Initialization with PKCS7Padding
	 * 
	 * @param key
	 *            key for encryption/decryption
	 * @param data
	 *            data for encryption/decryption
	 * @param mode
	 *            process mode for operation either Encrypt or Decrypt
	 * @return Processed array
	 */
	public static byte[] aesWithCBCandPKCS5Padding(SecretKey key, byte[] data,
			int mode) {
		return process(SecurityMethod.AES_WITH_CBC_AND_PKCS5PADDING, key,
				data, mode);
	}
}
