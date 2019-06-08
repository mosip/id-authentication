package io.mosip.registration.mdm.util;

import java.util.Collection;

public class MosioBioDeviceHelperUtil {

	/**
	 * Checks the given collection is not empty
	 * 
	 * @param values
	 * @return
	 */
	public static boolean isListNotEmpty(Collection<?> values) {
		return values != null && !values.isEmpty();
	}
}
