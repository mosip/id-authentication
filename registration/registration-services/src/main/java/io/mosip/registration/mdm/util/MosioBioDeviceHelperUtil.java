package io.mosip.registration.mdm.util;

import java.util.Collection;

/**
 * 
 * This is the helper class for the MosiopBioDeviceManager
 * @author Balamurugan
 *
 */
public class MosioBioDeviceHelperUtil {

	/**
	 * Checks the given collection is not empty
	 * 
	 * @param values - the collection to be checked
	 * @return boolean - is not empty or empty
	 */
	public static boolean isListNotEmpty(Collection<?> values) {
		return values != null && !values.isEmpty();
	}
}
