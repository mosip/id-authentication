package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

public class IncorrectVersionException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4105951551016957593L;

	public IncorrectVersionException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_007.toString(), message);
	}
}
