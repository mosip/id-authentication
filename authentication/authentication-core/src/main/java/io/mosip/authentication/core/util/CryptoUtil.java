package io.mosip.authentication.core.util;

import java.util.Base64;
import java.util.Base64.Encoder;

import io.mosip.kernel.core.util.EmptyCheckUtils;

/**
 * Crypto Util for common methods in various module
 * 
 * @author Loganathan Sekar
 *
 */
public class CryptoUtil {

	private static final byte[] EMPTY_BYTES = new byte[0];
	private static final Encoder URL_ENCODER_WITHOUT_PADDING = Base64.getUrlEncoder().withoutPadding();

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
		if (EmptyCheckUtils.isNullEmpty(data)) {
			return null;
		}
		return Base64.getEncoder().encodeToString(data);
	}
	
	/**
	 * Encodes to BASE64 URL Safe
	 * 
	 * @param data data to encode
	 * @return encoded data
	 */
	public static String encodeBase64Url(byte[] data) {
		if (EmptyCheckUtils.isNullEmpty(data)) {
			return null;
		}
		return URL_ENCODER_WITHOUT_PADDING.encodeToString(data);
	}

	/**
	 * Decodes from BASE64
	 * 
	 * @param data data to decode
	 * @return decoded data
	 */
	public static byte[] decodeBase64(String data) {
		if (EmptyCheckUtils.isNullEmpty(data)) {
			return EMPTY_BYTES;
		}
		return Base64.getDecoder().decode(data.getBytes());
	}
	
	/**
	 * Decodes from BASE64 URL
	 * 
	 * @param data data to decode
	 * @return decoded data
	 */
	public static byte[] decodeBase64Url(String data) {
		if (EmptyCheckUtils.isNullEmpty(data)) {
			return EMPTY_BYTES;
		}
		return Base64.getUrlDecoder().decode(data.getBytes());
	}

	
}