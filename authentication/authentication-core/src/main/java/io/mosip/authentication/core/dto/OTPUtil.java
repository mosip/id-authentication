package io.mosip.authentication.core.dto;

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
	 * Generate otp key to trigger otp.
	 * 
	 * @param productId productId
	 * @param idvId     individual id
	 * @param txnId     txnId
	 * @param partnerId   partnerId
	 * @return generated key.
	 */
	public static String generateKey(String productId, String idvId, String txnId, String partnerId) {
		return productId.concat("_").concat(Base64.getEncoder().encodeToString(idvId.getBytes())).concat("_")
				.concat(txnId).concat("_").concat(partnerId);
	}
}