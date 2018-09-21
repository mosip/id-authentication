package org.mosip.auth.core.util;

import java.util.Base64;

/**
 * The Class OTPUtil.
 *
 * @author Manoj SP
 */
public final class OTPUtil {

	private OTPUtil() {
	}

	/**
	 * Generate key.
	 *
	 * @param productId
	 *            the product id
	 * @param tokenId
	 *            the token id or ref id
	 * @param txnId
	 *            the txn id
	 * @param auaCode
	 *            the aua code or TSP code
	 * @return the encoded key
	 */
	public static String generateKey(String productId, String refId, String txnId, String auaCode) {
		return productId.concat("_").concat(Base64.getEncoder().encodeToString(refId.getBytes())).concat("_")
				.concat(txnId).concat("_").concat(auaCode);
	}
}