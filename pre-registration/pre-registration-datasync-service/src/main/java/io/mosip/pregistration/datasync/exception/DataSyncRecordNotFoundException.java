package io.mosip.pregistration.datasync.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.pregistration.datasync.errorcodes.ErrorCodes;

/**
 * DataSyncRecordNotFoundException
 * 
 * @author M1046129
 *
 */
public class DataSyncRecordNotFoundException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;
	
	public DataSyncRecordNotFoundException(String message) {
		super(ErrorCodes.PRG_DATA_SYNC_004.toString(), message);
	}

/*	public DataSyncRecordNotFoundException(String message, Throwable cause) {
		super(ErrorCodes.PRG_DATA_SYNC_004.toString(),message, cause);
	}*/
}
