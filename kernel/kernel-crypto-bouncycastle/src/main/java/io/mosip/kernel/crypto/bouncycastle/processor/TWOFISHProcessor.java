/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.bouncycastle.processor;

import org.bouncycastle.crypto.engines.TwofishEngine;

/**
 * TWOFISH Engine Initialization
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class TWOFISHProcessor extends SymmetricProcessor {
	/**
	 * Constructor for this class
	 */
	private TWOFISHProcessor() {
	}

	/**
	 * TWOFISH Engine Initialization with PKCS7Padding
	 * 
	 * @param key  key for encryption/decryption
	 * @param data data for encryption/decryption
	 * @param mode if true process mode is Encrypt ,else process mode is Decrypt
	 * @return Processed array
	 */
	public static byte[] twoFishWithCBCandPKCS7Padding(byte[] key, byte[] data, boolean mode) {
		return process(new TwofishEngine(), key, data, mode);
	}
}
