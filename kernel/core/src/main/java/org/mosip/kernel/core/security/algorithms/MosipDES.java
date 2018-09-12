/*
 * 
 * 
 * 
 * 
 * 
 */
package org.mosip.kernel.core.security.algorithms;

import org.bouncycastle.crypto.engines.DESEngine;
import org.mosip.kernel.core.security.exception.MosipInvalidDataException;
import org.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import org.mosip.kernel.core.security.processor.SymmetricProcessor;

/**
 * DES Initialization for Mosip
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class MosipDES extends SymmetricProcessor {
	/**
	 * Constructor for this class
	 */
	private MosipDES() {
	}

	/**
	 * DES Engine Initialization with PKCS7Padding
	 * 
	 * @param key
	 *            Key for encryption/decryption
	 * @param data
	 *            Data for encryption/decryption
	 * @param mode
	 *            If true process mode is Encrypt ,else process mode is Decrypt
	 * @return Processed array
	 * @throws MosipInvalidDataException
	 *             If data is not valid in length,corrupted
	 * @throws MosipInvalidKeyException
	 *             If key is not valid in length,corrupted and wrong
	 */
	public static byte[] desWithCBCandPKCS7Padding(byte[] key, byte[] data, boolean mode)
			throws MosipInvalidDataException, MosipInvalidKeyException {
		return process(new DESEngine(), key, data, mode);
	}

}
