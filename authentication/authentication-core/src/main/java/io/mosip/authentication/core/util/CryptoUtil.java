package io.mosip.authentication.core.util;

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
		return io.mosip.kernel.core.util.CryptoUtil.combineByteArray(data, key, keySplitter);
	}

	/**
	 * Encodes to BASE64
	 * 
	 * @param data data to encode
	 * @return encoded data
	 */
	public static String encodeBase64(byte[] data) {
		return io.mosip.kernel.core.util.CryptoUtil.encodeToPlainBase64(data);
	}
	
	/**
	 * Encodes to BASE64 URL Safe
	 * 
	 * @param data data to encode
	 * @return encoded data
	 */
	public static String encodeBase64Url(byte[] data) {
		return io.mosip.kernel.core.util.CryptoUtil.encodeToURLSafeBase64(data);

	}

	/**
	 * Decodes from BASE64
	 * 
	 * @param data data to decode
	 * @return decoded data
	 */
	public static byte[] decodeBase64(String data) {
		return io.mosip.kernel.core.util.CryptoUtil.decodePlainBase64(data);

	}
	
	/**
	 * Decodes from BASE64 URL
	 * 
	 * @param data data to decode
	 * @return decoded data
	 */
	public static byte[] decodeBase64Url(String data) {
		return io.mosip.kernel.core.util.CryptoUtil.decodeURLSafeBase64(data);
	}

	
}