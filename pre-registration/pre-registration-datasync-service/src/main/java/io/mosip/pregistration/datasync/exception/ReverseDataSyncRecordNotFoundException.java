package io.mosip.pregistration.datasync.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.pregistration.datasync.errorcodes.ErrorCodes;

/**
 * RecordNotFoundException
 * 
 * @author M1046129
 *
 */
public class ReverseDataSyncRecordNotFoundException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public ReverseDataSyncRecordNotFoundException(String message) {
		super(ErrorCodes.PRG_REVESE_DATA_SYNC_001.toString(), message);
	}

	public ReverseDataSyncRecordNotFoundException(String message, Throwable cause) {
		super(ErrorCodes.PRG_REVESE_DATA_SYNC_001.toString(),message, cause);
	}
}
