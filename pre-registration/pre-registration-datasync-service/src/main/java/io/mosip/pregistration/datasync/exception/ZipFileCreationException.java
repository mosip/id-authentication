package io.mosip.pregistration.datasync.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.pregistration.datasync.errorcodes.ErrorCodes;

/**
 * RecordNotFoundException
 * 
 * @author M1046129
 *
 */
public class ZipFileCreationException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;
//
//	public RecordNotFoundException(String errorCodes) {
//		super(errorCodes, errorCodes);
//	}
	
	public ZipFileCreationException(String message) {
		super(ErrorCodes.PRG_DATA_SYNC_005.toString(), message);
	}

	public ZipFileCreationException(String message, Throwable cause) {
		super(ErrorCodes.PRG_DATA_SYNC_005.toString(),message, cause);
	}
}
