/*
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.core.security.algorithms;

import org.bouncycastle.crypto.engines.TwofishEngine;

import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.core.security.processor.SymmetricProcessor;

/**
 * TWOFISH Engine Initialization
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipTWOFISH extends SymmetricProcessor {
	/**
	 * Constructor for this class
	 */
	private MosipTWOFISH() {
	}

	/**
	 * TWOFISH Engine Initialization with PKCS7Padding
	 * 
	 * @param key  key for encryption/decryption
	 * @param data data for encryption/decryption
	 * @param mode if true process mode is Encrypt ,else process mode is Decrypt
	 * @return Processed array
	 * @throws MosipInvalidDataException if data is not valid in length,corrupted
	 * @throws MosipInvalidKeyException  if key is not valid in length,corrupted and
	 *                                   wrong
	 */
	public static byte[] twoFishWithCBCandPKCS7Padding(byte[] key, byte[] data, boolean mode)
			throws MosipInvalidDataException, MosipInvalidKeyException {
		return process(new TwofishEngine(), key, data, mode);
	}
}
