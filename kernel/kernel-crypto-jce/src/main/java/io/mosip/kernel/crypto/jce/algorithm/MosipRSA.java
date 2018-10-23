/*
 * 
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.jce.algorithm;

import java.security.Key;

import io.mosip.kernel.crypto.jce.constant.MosipSecurityMethod;
import io.mosip.kernel.crypto.jce.processor.AsymmetricProcessor;

/**
 * RSA Engine Initialization
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipRSA extends AsymmetricProcessor {
	/**
	 * Constructor for this class
	 */
	private MosipRSA() {

	}
	/**
	 * RSA Engine Initialization with PKCS1Padding
	 * 
	 * @param key
	 *            Key for encryption/decryption
	 * @param data
	 *            Data for encryption/decryption
	 * @param mode
	 *            process mode for operation either Encrypt or Decrypt
	 * @return Processed array
	 */
	public static byte[] rsaWithPKCS1Padding(Key key, byte[] data, int mode) {
		return process(MosipSecurityMethod.RSA_WITH_PKCS1PADDING, key, data,
				mode);
	}
}
