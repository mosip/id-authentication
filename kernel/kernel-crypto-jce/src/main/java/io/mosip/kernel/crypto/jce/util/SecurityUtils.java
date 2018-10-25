/*
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.jce.util;

import io.mosip.kernel.crypto.jce.constant.MosipSecurityExceptionCodeConstant;
import io.mosip.kernel.crypto.jce.constant.MosipSecurityMethod;
import io.mosip.kernel.crypto.jce.exception.MosipInvalidDataException;
import io.mosip.kernel.crypto.jce.exception.MosipNullDataException;
import io.mosip.kernel.crypto.jce.exception.MosipNullMethodException;

/**
 * Utility class for security
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class SecurityUtils {

	/**
	 * Constructor for this class
	 */
	private SecurityUtils() {

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
					MosipSecurityExceptionCodeConstant.MOSIP_NULL_METHOD_EXCEPTION);
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
					MosipSecurityExceptionCodeConstant.MOSIP_NULL_DATA_EXCEPTION);
		} else if (data.length == 0) {
			throw new MosipInvalidDataException(
					MosipSecurityExceptionCodeConstant.MOSIP_NULL_DATA_EXCEPTION);
		}
	}

}
