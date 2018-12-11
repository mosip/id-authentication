package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

public class MasterDataNotAvailableException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2782261618399872549L;

	public MasterDataNotAvailableException(String message) {
		super(ErrorCodes.PRG_BOOK_RCI_020.toString(), message);
	}

	public MasterDataNotAvailableException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public MasterDataNotAvailableException(String errorMessage, Throwable rootCause) {
		super(ErrorCodes.PRG_BOOK_RCI_020.toString(), errorMessage, rootCause);
	}

	public MasterDataNotAvailableException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}
}
