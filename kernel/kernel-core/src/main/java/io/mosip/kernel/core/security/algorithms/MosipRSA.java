/*
 * 
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.core.security.algorithms;

import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

import io.mosip.kernel.core.security.exception.MosipInvalidDataException;
import io.mosip.kernel.core.security.exception.MosipInvalidKeyException;
import io.mosip.kernel.core.security.processor.AsymmetricProcessor;

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
	 * Hybrid RSA Engine Initialization with PKCS1Padding
	 * 
	 * @param key  key for encryption/decryption
	 * @param data data for encryption/decryption
	 * @param mode if true process mode is Encrypt ,else process mode is Decrypt
	 * @return Processed array
	 * @throws MosipInvalidDataException if data is not valid in length,corrupted
	 * @throws MosipInvalidKeyException  if key is not valid in length,corrupted and
	 *                                   wrong
	 */
	public static byte[] hybridRsaAesWithPKCS1Padding(AsymmetricKeyParameter key, byte[] data, boolean mode)
			throws MosipInvalidDataException, MosipInvalidKeyException {
		return processHybrid(new PKCS1Encoding(new RSAEngine()), key, data, mode);
	}

	/**
	 * Hybrid RSA Engine Initialization with OAEP(MD5AndMGF1)Padding
	 * 
	 * @param key  Key for encryption/decryption
	 * @param data Data for encryption/decryption
	 * @param mode If true process mode is Encrypt ,else process mode is Decrypt
	 * @return Processed array
	 * @throws MosipInvalidDataException If data is not valid in length,corrupted
	 * @throws MosipInvalidKeyException  If key is not valid in length,corrupted and
	 *                                   wrong
	 */
	public static byte[] hybridRsaAesWithOAEPWithMD5AndMGF1Padding(AsymmetricKeyParameter key, byte[] data,
			boolean mode) throws MosipInvalidDataException, MosipInvalidKeyException {
		return processHybrid(new OAEPEncoding(new RSAEngine(), new MD5Digest()), key, data, mode);
	}

	/**
	 * Hybrid RSA Engine Initialization with OAEP(SHA3512AndMGF1)Padding
	 * 
	 * @param key  Key for encryption/decryption
	 * @param data Data for encryption/decryption
	 * @param mode If true process mode is Encrypt ,else process mode is Decrypt
	 * @return Processed array
	 * @throws MosipInvalidDataException If data is not valid in length,corrupted
	 * @throws MosipInvalidKeyException  If key is not valid in length,corrupted and
	 *                                   wrong
	 */
	public static byte[] hybridRsaAesWithOAEPWithSHA3512AndMGF1Padding(AsymmetricKeyParameter key, byte[] data,
			boolean mode) throws MosipInvalidDataException, MosipInvalidKeyException {
		return processHybrid(new OAEPEncoding(new RSAEngine(), new SHA3Digest(512)), key, data, mode);
	}

	/**
	 * RSA Engine Initialization with PKCS1Padding
	 * 
	 * @param key  Key for encryption/decryption
	 * @param data Data for encryption/decryption
	 * @param mode If true process mode is Encrypt ,else process mode is Decrypt
	 * @return Processed array
	 * @throws MosipInvalidDataException If data is not valid in length,corrupted
	 * @throws MosipInvalidKeyException  If key is not valid in length,corrupted and
	 *                                   wrong
	 */
	public static byte[] rsaWithPKCS1Padding(AsymmetricKeyParameter key, byte[] data, boolean mode)
			throws MosipInvalidDataException, MosipInvalidKeyException {
		return process(new PKCS1Encoding(new RSAEngine()), key, data, mode);
	}

	/**
	 * RSA Engine Initialization with OAEP(MD5AndMGF1)Padding
	 * 
	 * @param key  Key for encryption/decryption
	 * @param data Data for encryption/decryption
	 * @param mode If true process mode is Encrypt ,else process mode is Decrypt
	 * @return Processed array
	 * @throws MosipInvalidDataException If data is not valid in length,corrupted
	 * @throws MosipInvalidKeyException  If key is not valid in length,corrupted and
	 *                                   wrong
	 */
	public static byte[] rsaWithOAEPWithMD5AndMGF1Padding(AsymmetricKeyParameter key, byte[] data, boolean mode)
			throws MosipInvalidDataException, MosipInvalidKeyException {
		return process(new OAEPEncoding(new RSAEngine(), new MD5Digest()), key, data, mode);
	}

	/**
	 * RSA Engine Initialization with OAEP(SHA3512AndMGF1)Padding
	 * 
	 * @param key  Key for encryption/decryption
	 * @param data Data for encryption/decryption
	 * @param mode If true process mode is Encrypt ,else process mode is Decrypt
	 * @return Processed array
	 * @throws MosipInvalidDataException If data is not valid in length,corrupted
	 * @throws MosipInvalidKeyException  If key is not valid in length,corrupted and
	 *                                   wrong
	 */
	public static byte[] rsaWithOAEPWithSHA3512AndMGF1Padding(AsymmetricKeyParameter key, byte[] data, boolean mode)
			throws MosipInvalidDataException, MosipInvalidKeyException {
		return process(new OAEPEncoding(new RSAEngine(), new SHA3Digest(512)), key, data, mode);
	}

}
