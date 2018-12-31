package io.mosip.preregistration.datasync.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.datasync.errorcodes.ErrorCodes;

/**
 * RecordNotFoundForDateRange Exception
 * 
 * @author M1043226
 *
 */
public class RecordNotFoundForDateRange extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public RecordNotFoundForDateRange(String msg) {
		super(ErrorCodes.PRG_DATA_SYNC_001.toString(), msg);
	}

	public RecordNotFoundForDateRange(String msg, Throwable cause) {
		super(ErrorCodes.PRG_DATA_SYNC_001.toString(), msg, cause);
	}

	public RecordNotFoundForDateRange(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public RecordNotFoundForDateRange(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

}