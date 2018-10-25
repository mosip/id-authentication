/*
 * 
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.bouncycastle.algorithm;

import org.bouncycastle.crypto.engines.AESEngine;

import io.mosip.kernel.crypto.bouncycastle.processor.SymmetricProcessor;

/**
 * AES Initialization for Mosip
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipAES extends SymmetricProcessor {

	/**
	 * Constructor for this class
	 */
	private MosipAES() {
	}

	/**
	 * AES Engine Initialization with PKCS7Padding
	 * 
	 * @param key  key for encryption/decryption
	 * @param data data for encryption/decryption
	 * @param mode if true process mode is Encrypt ,else process mode is Decrypt
	 * @return Processed array
	 */
	public static byte[] aesWithCBCandPKCS7Padding(byte[] key, byte[] data, boolean mode) {
		return process(new AESEngine(), key, data, mode);
	}
}
