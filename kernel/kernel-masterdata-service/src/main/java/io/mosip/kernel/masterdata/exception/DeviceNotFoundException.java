
package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom Exception Class thrown exception when related recode is not in table
 *
 * 
 * @author Megha Tanga
 * @since 1.0.0
 */
public class DeviceNotFoundException extends BaseUncheckedException {

	/**
	 * Unique id for serialization
	 */
	private static final long serialVersionUID = -3556229489431119187L;

	/**
	 * Constructor for this class
	 * 
	 * @param errorCode
	 *            unique exception code
	 * @param errorMessage
	 *            exception message
	 */
	public DeviceNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}

