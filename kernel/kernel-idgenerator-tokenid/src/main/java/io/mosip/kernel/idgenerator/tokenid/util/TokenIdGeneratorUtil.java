package io.mosip.kernel.idgenerator.tokenid.util;

import org.springframework.stereotype.Component;

/**
 * Class that provides utilities for Token ID Generation.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Component
public class TokenIdGeneratorUtil {
	/**
	 * Private constructor to avoid instantiation.
	 */
	private TokenIdGeneratorUtil() {

	}

	/**
	 * Method to convert the alphanumeric hash to numeric.
	 * 
	 * @param hexInput
	 *            the hex string.
	 * @return the numeric string.
	 */
	public static String encodeHexToNumeric(String hexInput) {
		StringBuilder numericHex = new StringBuilder();
		for (int i = 0; i < hexInput.length(); i++) {
			numericHex.append(Integer.parseInt(Character.toString(hexInput.charAt(i)), 16) % 10);
		}
		return numericHex.toString();
	}

	/**
	 * Method to compress the generated numeric hash to specified length.
	 * 
	 * @param shaNumberEncodedHash
	 *            the numeric hash string.
	 * @param tokenIdLength
	 *            the length of token ID.
	 * @return the resultant token ID.
	 */
	public static String compressHash(String shaNumberEncodedHash, int tokenIdLength) {
		StringBuilder token = new StringBuilder();
		if (shaNumberEncodedHash.length() < tokenIdLength) {
			token.append(shaNumberEncodedHash);
			token.append(shaNumberEncodedHash.substring(0, (tokenIdLength - shaNumberEncodedHash.length()) + 1));
		} else {
			token.append(shaNumberEncodedHash.substring(0, tokenIdLength));
		}
		return token.toString();
	}
}
