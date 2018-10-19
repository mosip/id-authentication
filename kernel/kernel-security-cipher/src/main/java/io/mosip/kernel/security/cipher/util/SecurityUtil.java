/*
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.security.cipher.util;

import io.mosip.kernel.security.cipher.constant.MosipSecurityExceptionCodeConstants;
import io.mosip.kernel.security.cipher.constant.MosipSecurityMethod;
import io.mosip.kernel.security.cipher.exception.MosipInvalidDataException;
import io.mosip.kernel.security.cipher.exception.MosipNullDataException;
import io.mosip.kernel.security.cipher.exception.MosipNullMethodException;

/**
 * Utility class for security
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class SecurityUtil {

	/**
	 * Constructor for this class
	 */
	private SecurityUtil() {

	}

	/**
	 * This method verifies mosip security method
	 * 
	 * @param mosipSecurityMethod
	 *            mosipSecurityMethod given by user
	 */
	public static void checkMethod(MosipSecurityMethod mosipSecurityMethod) {
		if (mosipSecurityMethod == null) {
			throw new MosipNullMethodException(
					MosipSecurityExceptionCodeConstants.MOSIP_NULL_METHOD_EXCEPTION);
		}
	}

	/**
	 * Verify if data is null or empty
	 * 
	 * @param data
	 *            data provided by user
	 */
	public static void verifyData(byte[] data) {
		if (data == null) {
			throw new MosipNullDataException(
					MosipSecurityExceptionCodeConstants.MOSIP_NULL_DATA_EXCEPTION);
		} else if (data.length == 0) {
			throw new MosipInvalidDataException(
					MosipSecurityExceptionCodeConstants.MOSIP_NULL_DATA_EXCEPTION);
		}
	}

}
