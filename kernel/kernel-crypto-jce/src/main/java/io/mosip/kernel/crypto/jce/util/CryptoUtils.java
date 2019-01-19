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
import io.mosip.kernel.crypto.jce.constant.SecurityExceptionCodeConstant;

/**
 * Utility class for crypto
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
public class CryptoUtils {

	/**
	 * Constructor for this class
	 */
	private CryptoUtils() {

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
