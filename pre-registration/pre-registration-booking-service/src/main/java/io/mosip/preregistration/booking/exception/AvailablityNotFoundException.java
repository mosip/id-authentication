package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class AvailablityNotFoundException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;

	public AvailablityNotFoundException(String msg) {
		super("", msg);
	}

	public AvailablityNotFoundException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public AvailablityNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public AvailablityNotFoundException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public AvailablityNotFoundException() {
		super();
	}
}
