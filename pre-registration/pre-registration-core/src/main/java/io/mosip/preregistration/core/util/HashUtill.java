package io.mosip.preregistration.core.util;

import io.mosip.kernel.core.util.HMACUtils;


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
}
