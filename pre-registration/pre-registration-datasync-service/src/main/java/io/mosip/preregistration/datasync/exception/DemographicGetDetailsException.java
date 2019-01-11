package io.mosip.preregistration.datasync.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.datasync.errorcodes.ErrorCodes;

/**
 * @author M1046129
 *
 */
public class DemographicGetDetailsException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public DemographicGetDetailsException(String msg) {
		super(ErrorCodes.PRG_DATA_SYNC_007.toString(), msg);
	}

	public DemographicGetDetailsException(String msg, Throwable cause) {
		super(ErrorCodes.PRG_DATA_SYNC_007.toString(), msg, cause);
	}

	public DemographicGetDetailsException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public DemographicGetDetailsException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}
