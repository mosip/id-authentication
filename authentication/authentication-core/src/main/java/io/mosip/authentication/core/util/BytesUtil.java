package io.mosip.authentication.core.util;

import java.util.Arrays;

public final class BytesUtil {

	private BytesUtil() {
	}

	// Function to insert n 0s in the
	// beginning of the given string
	static byte[] prependZeros(byte[] str, int n) {
		byte[] newBytes = new byte[str.length + n];
		int i = 0;
		for (; i < n; i++) {
			newBytes[i] = 0;
		}
		
		for(int j = 0;i < newBytes.length; i++, j++) {
			newBytes[i] = str[j];
		}
		
		return newBytes;
	}

	// Function to return the XOR
	// of the given strings
	public static byte[] getXOR(String a, String b) {
		byte[] aBytes = a.getBytes();
		byte[] bBytes = b.getBytes();
		// Lengths of the given strings
		int aLen = aBytes.length;
		int bLen = bBytes.length;

		// Make both the strings of equal lengths
		// by inserting 0s in the beginning
		if (aLen > bLen) {
			bBytes = prependZeros(bBytes, aLen - bLen);
		} else if (bLen > aLen) {
			aBytes = prependZeros(aBytes, bLen - aLen);
		}

		// Updated length
		int len = Math.max(aLen, bLen);
		byte[] xorBytes = new byte[len];

		// To store the resultant XOR
		for (int i = 0; i < len; i++) {
			xorBytes[i] = (byte)(aBytes[i] ^ bBytes[i]);
		}
		return xorBytes;
	}
	
	/**
	 * Gets the last bytes.
	 *
	 * @param timestamp the timestamp
	 * @param lastBytesNum the last bytes num
	 * @return the last bytes
	 */
	public static byte[] getLastBytes(byte[] xorBytes, int lastBytesNum) {
		assert(xorBytes.length >= lastBytesNum);
		return Arrays.copyOfRange(xorBytes, xorBytes.length - lastBytesNum, xorBytes.length);
	}
}
