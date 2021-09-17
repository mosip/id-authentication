package io.mosip.authentication.core.util;

import java.util.Base64;

/**
 * Crypto Util for common methods in various module
 * 
 * @author Loganathan Sekar
 *
 */
public class CryptoUtil {

	/**
	 * Private Constructor for this class
	 */
	private CryptoUtil() {

	}

	/**
	 * Combine data,key and key splitter
	 * 
	 * @param data        encrypted Data
	 * @param key         encrypted Key
	 * @param keySplitter keySplitter
	 * @return byte array consisting data,key and key splitter
	 */
	public static byte[] combineByteArray(byte[] data, byte[] key, String keySplitter) {
		byte[] keySplitterBytes = keySplitter.getBytes();
		byte[] combinedArray = new byte[key.length + keySplitterBytes.length + data.length];
		System.arraycopy(key, 0, combinedArray, 0, key.length);
		System.arraycopy(keySplitterBytes, 0, combinedArray, key.length, keySplitterBytes.length);
		System.arraycopy(data, 0, combinedArray, key.length + keySplitterBytes.length, data.length);
		return combinedArray;
	}

	/**
	 * Encodes to BASE64
	 * 
	 * @param data data to encode
	 * @return encoded data
	 */
	public static String encodeBase64(byte[] data) {
		return Base64.getEncoder().encodeToString(data);
	}
	
	/**
	 * Encodes to BASE64 URL Safe
	 * 
	 * @param data data to encode
	 * @return encoded data
	 */
	public static String encodeBase64Url(byte[] data) {
		return Base64.getUrlEncoder().encodeToString(data);
	}

	/**
	 * Encodes to BASE64 String
	 * 
	 * @param data data to encode
	 * @return encoded data
	 */
	public static String encodeToBase64String(byte[] data) {
		return Base64.getEncoder().encodeToString(data);
	}

	/**
	 * Decodes from BASE64
	 * 
	 * @param data data to decode
	 * @return decoded data
	 */
	public static byte[] decodeBase64(String data) {
		return Base64.getDecoder().decode(data.getBytes());
	}
	
	/**
	 * Decodes from BASE64 URL
	 * 
	 * @param data data to decode
	 * @return decoded data
	 */
	public static byte[] decodeBase64Url(String data) {
		return Base64.getUrlDecoder().decode(data.getBytes());
	}

	
}