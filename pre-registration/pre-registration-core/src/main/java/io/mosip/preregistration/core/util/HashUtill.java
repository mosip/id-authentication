package io.mosip.preregistration.core.util;

import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.core.util.HashUtils;


/**
 * @author Sanober Noor
 *@since 1.0.0 
 */
public class HashUtill {
	
	private HashUtill() {
	}

	public static String hashUtill(byte[] bytes) {
		return HMACUtils.digestAsPlainText(HMACUtils.generateHash(bytes));
	}
	
	public static boolean isHashEqual(byte[] source, byte[] target) {
		return new HashUtils().isDigestEqual(source, target);
	}
}
