package io.mosip.kernel.licensekeygenerator.misp.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Class to define exception when length of generated license key is less than
 * specified length.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
public class LengthNotSameException extends BaseUncheckedException {
	/**
	 * Generated Serialized version ID.
	 */
	private static final long serialVersionUID = 6843084952477606194L;

	/**
	 * Constructor with errorCode, errorMessage that invokes the super class
	 * constructor.
	 * 
	 * @param errorCode
	 *            the error code.
	 * @param errorMessage
	 *            the error message.
	 */
	public LengthNotSameException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
