package io.mosip.preregistration.datasync.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.datasync.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class DocumentGetDetailsException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public DocumentGetDetailsException(String msg) {
		super(ErrorCodes.PRG_DATA_SYNC_008.toString(), msg);
	}

	public DocumentGetDetailsException(String msg, Throwable cause) {
		super(ErrorCodes.PRG_DATA_SYNC_008.toString(), msg, cause);
	}

	public DocumentGetDetailsException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public DocumentGetDetailsException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
