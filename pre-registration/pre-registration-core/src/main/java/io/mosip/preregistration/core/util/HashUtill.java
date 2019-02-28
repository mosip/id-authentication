package io.mosip.preregistration.core.util;

import io.mosip.kernel.core.util.HMACUtils;


/**
 * @author Sanober Noor
 *
 */
public class HashUtill {
	
	private HashUtill() {
	}

	public static byte[]  hashUtill(byte[] bytes) {
		return HMACUtils.generateHash(bytes);
	}
}
