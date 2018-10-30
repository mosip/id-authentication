/*
 * 
 * 
 * 
 * 
 * 
 */
package io.mosip.kernel.crypto.jce.util;

import io.mosip.kernel.core.crypto.exception.InvalidDataException;
import io.mosip.kernel.core.crypto.exception.NullDataException;
import io.mosip.kernel.core.crypto.exception.NullMethodException;
import io.mosip.kernel.crypto.jce.constant.SecurityExceptionCodeConstant;
import io.mosip.kernel.crypto.jce.constant.SecurityMethod;

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
	public static void checkMethod(SecurityMethod mosipSecurityMethod) {
		if (mosipSecurityMethod == null) {
			throw new NullMethodException(
					SecurityExceptionCodeConstant.MOSIP_NULL_METHOD_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NULL_METHOD_EXCEPTION.getErrorMessage());
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
			throw new NullDataException(
					SecurityExceptionCodeConstant.MOSIP_NULL_DATA_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NULL_DATA_EXCEPTION.getErrorMessage());
		} else if (data.length == 0) {
			throw new InvalidDataException(
					SecurityExceptionCodeConstant.MOSIP_NULL_DATA_EXCEPTION.getErrorCode(),
					SecurityExceptionCodeConstant.MOSIP_NULL_DATA_EXCEPTION.getErrorMessage());
		}
	}

}
