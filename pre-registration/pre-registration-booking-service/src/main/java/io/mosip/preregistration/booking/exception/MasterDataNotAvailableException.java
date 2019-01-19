package io.mosip.preregistration.booking.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;

public class MasterDataNotAvailableException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2782261618399872549L;

	public MasterDataNotAvailableException(String msg) {
		super("", msg);
	}

	public MasterDataNotAvailableException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public MasterDataNotAvailableException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public MasterDataNotAvailableException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public MasterDataNotAvailableException() {
		super();
	}
}
