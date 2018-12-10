package io.mosip.kernel.core.util;

import static java.util.Arrays.copyOfRange;

import org.apache.commons.codec.binary.Base64;

public class CryptoUtil {

	/**
	 * @param data
	 * @param key
	 * @return
	 */
	public static byte[] combineByteArray(byte[] data, byte[] key,
			String keySplitter) {
		byte[] keySplitterBytes = keySplitter.getBytes();
		byte[] combinedArray = new byte[key.length + keySplitterBytes.length
				+ data.length];
		System.arraycopy(key, 0, combinedArray, 0, key.length);
		System.arraycopy(keySplitterBytes, 0, combinedArray, key.length,
				keySplitterBytes.length);
		System.arraycopy(data, 0, combinedArray,
				key.length + keySplitterBytes.length, data.length);
		return combinedArray;
	}

	/**
	 * @param cryptoRequestDto
	 * @param keyDemiliterIndex
	 * @param cipherKeyandDataLength
	 * @param keySplitterLength
	 * @param keySplitterFirstByte
	 * @return
	 */
	public static int getSplitterIndex(byte[] encryptedData,
			int keyDemiliterIndex, String keySplitter) {
		final byte keySplitterFirstByte = keySplitter.getBytes()[0];
		final int keySplitterLength = keySplitter.length();
		for (byte data : encryptedData) {
			if (data == keySplitterFirstByte) {
				final String keySplit = new String(
						copyOfRange(encryptedData, keyDemiliterIndex,
								keyDemiliterIndex + keySplitterLength));
				if (keySplitter.equals(keySplit)) {
					break;
				}
			}
			keyDemiliterIndex++;
		}
		return keyDemiliterIndex;
	}

	/**
	 * @param data
	 * @return
	 */
	public static String encodeBase64(byte[] data) {
		return Base64.encodeBase64URLSafeString(data);
	}

	/**
	 * @param data
	 * @return
	 */
	public static byte[] decodeBase64(String data) {
		return Base64.decodeBase64(data);
	}

}
