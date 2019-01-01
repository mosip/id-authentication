package io.mosip.authentication.core.util;

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
	 * @param productId productId
	 * @param refId     refId
	 * @param txnId     txnId
	 * @param auaCode   auaCode
	 * @return generated key.
	 */
	public static String generateKey(String productId, String refId, String txnId, String auaCode) {
		return productId.concat("_").concat(Base64.getEncoder().encodeToString(refId.getBytes())).concat("_")
				.concat(txnId).concat("_").concat(auaCode);
	}
}