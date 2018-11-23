package io.mosip.pregistration.datasync.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.pregistration.datasync.errorcodes.ErrorCodes;

/**
 * ReverseDataSyncRecordNotFoundException
 * 
 * @author M1046129
 *
 */
public class ReverseDataFailedToStoreException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public ReverseDataFailedToStoreException(String message) {
		super(ErrorCodes.PRG_REVESE_DATA_SYNC_001.toString(), message);
	}

/*	public ReverseDataFailedToStoreException(String message, Throwable cause) {
		super(ErrorCodes.PRG_REVESE_DATA_SYNC_001.toString(),message, cause);
	}
*/
	public ReverseDataFailedToStoreException() {
		// TODO Auto-generated constructor stub
	}
}
