/**
 * 
 */
package io.mosip.kernel.core.util;

import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.core.util.constant.HMACUtilConstants;

/**
 * This class defines the HMAC Util to be used in MOSIP Project. The HMAC Util
 * is implemented using desired methods of MessageDigest class of java security
 * package
 * 
 * @author Omsaieswar Mulaklauri
 * 
 * @since 1.0.0
 */
public final class HMACUtils {
	/**
	 * SHA-256 Algorithm
	 */
	private static final String HMAC_ALGORITHM_NAME = "SHA-256";

	/**
	 * Message digests are secure one-way hash functions that take arbitrary-sized
	 * data and output a fixed-length hash value
	 */
	private static MessageDigest messageDigest;

	/**
	 * Performs a digest using the specified array of bytes.
	 * 
	 * @param bytes
	 *            bytes to be hash generation
	 * @return byte[] generated hash bytes
	 */
	public static byte[] generateHash(final byte[] bytes) {
		return messageDigest.digest(bytes);
	}

	/**
	 * Updates the digest using the specified byte
	 * 
	 * @param bytes
	 *            updates the digest using the specified byte
	 */
	public static void update(final byte[] bytes) {
		messageDigest.update(bytes);
	}

	/**
	 * Return the whole update digest
	 * 
	 * @return byte[] updated hash bytes
	 */
	public static byte[] updatedHash() {
		return messageDigest.digest();
	}

	/**
	 * Return the digest as a plain text
	 * 
	 * @param bytes
	 *            digest bytes
	 * @return String converted digest as plain text
	 */
	public static String digestAsPlainText(final byte[] bytes) {
		return DatatypeConverter.printHexBinary(bytes).toUpperCase();
	}

	/**
	 * Creates a message digest with the specified algorithm name.
	 *
	 * @param algorithm
	 *            the standard name of the digest algorithm.
	 * 
	 * @throws NoSuchAlgorithmException
	 *             if specified algorithm went wrong
	 * @description loaded messageDigest with specified algorithm
	 */
	static {
		try {
			messageDigest = messageDigest != null ? messageDigest : MessageDigest.getInstance(HMAC_ALGORITHM_NAME);
		} catch (java.security.NoSuchAlgorithmException exception) {
			throw new NoSuchAlgorithmException(HMACUtilConstants.MOSIP_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorCode(),
					HMACUtilConstants.MOSIP_NO_SUCH_ALGORITHM_ERROR_CODE.getErrorMessage(), exception.getCause());
		}
	}

	/*
	 * No object initialization.
	 */
	private HMACUtils() {
	}
}
