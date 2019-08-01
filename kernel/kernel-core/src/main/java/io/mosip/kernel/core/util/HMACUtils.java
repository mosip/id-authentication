/**
 * 
 */
package io.mosip.kernel.core.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;

import io.mosip.kernel.core.exception.NoSuchAlgorithmException;
import io.mosip.kernel.core.util.constant.HMACUtilConstants;

/**
 * This class defines the HMAC Util to be used in MOSIP Project. The HMAC Util
 * is implemented using desired methods of MessageDigest class of java security
 * package
 * 
 * @author Omsaieswar Mulaklauri
 * @author Urvil Joshi
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
	 * @param bytes bytes to be hash generation
	 * @return byte[] generated hash bytes
	 */
	public static synchronized byte[] generateHash(final byte[] bytes) {
		return messageDigest.digest(bytes);
	}

	/**
	 * Updates the digest using the specified byte
	 * 
	 * @param bytes updates the digest using the specified byte
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
	 * Return the digest as a plain text with Salt
	 * 
	 * @param bytes digest bytes
	 * @param salt  digest bytes
	 * @return String converted digest as plain text
	 */
	public static synchronized String digestAsPlainTextWithSalt(final byte[] bytes, final byte[] salt) {
		messageDigest.update(bytes);
		messageDigest.update(salt);
		return DatatypeConverter.printHexBinary(messageDigest.digest());
	}

	/**
	 * Return the digest as a plain text
	 * 
	 * @param bytes digest bytes
	 * @return String converted digest as plain text
	 */
	public static synchronized String digestAsPlainText(final byte[] bytes) {
		return DatatypeConverter.printHexBinary(bytes).toUpperCase();
	}

	/**
	 * Creates a message digest with the specified algorithm name.
	 *
	 * @param algorithm the standard name of the digest algorithm.
	 * 
	 * @throws NoSuchAlgorithmException if specified algorithm went wrong
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

	/**
	 * Generate Random Salt (with default 16 bytes of length).
	 * 
	 * @return Random Salt
	 */
	public static byte[] generateSalt() {
		SecureRandom random = new SecureRandom();
		byte[] randomBytes = new byte[16];
		random.nextBytes(randomBytes);
		return randomBytes;
	}

	/**
	 * Generate Random Salt (with given length)
	 * 
	 * @param bytes length of random salt
	 * @return Random Salt of given length
	 */
	public static byte[] generateSalt(int bytes) {
		SecureRandom random = new SecureRandom();
		byte[] randomBytes = new byte[bytes];
		random.nextBytes(randomBytes);
		return randomBytes;
	}

	/**
	 * Encodes to BASE64 String
	 * 
	 * @param data data to encode
	 * @return encoded data
	 */
	public static String encodeBase64String(byte[] data) {
		return Base64.encodeBase64String(data);
	}

	/**
	 * Decodes from BASE64
	 * 
	 * @param data data to decode
	 * @return decoded data
	 */
	public static byte[] decodeBase64(String data) {
		return Base64.decodeBase64(data);
	}

	/*
	 * No object initialization.
	 */
	private HMACUtils() {
	}
	
	public static void main(String arg[])
	{
		String salt ="NPCA--9rCf76rRgaSeLGdg";
		System.out.println(encode("mosip",27500,salt.getBytes()));
	}
	
	 private static String encode(String rawPassword, int iterations, byte[] salt) {
	        KeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), Base64.decodeBase64(salt), iterations, 512);

	        try {
	            byte[] key = getSecretKeyFactory().generateSecret(spec).getEncoded();
	            return Base64.encodeBase64String(key);
	        } catch (InvalidKeySpecException e) {
	            throw new RuntimeException("Credential could not be encoded", e);
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new RuntimeException(e);
	        }
	    }
	
	
	private static SecretKeyFactory getSecretKeyFactory() throws java.security.NoSuchAlgorithmException {
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("PBKDF2 algorithm not found", e);
        }
    }
}
