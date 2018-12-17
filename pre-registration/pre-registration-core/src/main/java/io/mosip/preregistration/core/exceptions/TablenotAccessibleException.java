package io.mosip.preregistration.core.exceptions;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.exceptions.util.PreIssuanceExceptionCodes;

/**
 * TablenotAccessibleException occurs when system is not able to access registration table.
 *
 */
public class TablenotAccessibleException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public TablenotAccessibleException() {
		super();
	}

	public TablenotAccessibleException(String message) {
		super(PreIssuanceExceptionCodes.TABLE_NOT_FOUND_EXCEPTION, message);
	}

	public TablenotAccessibleException(String message, Throwable cause) {
		super(PreIssuanceExceptionCodes.TABLE_NOT_FOUND_EXCEPTION+ EMPTY_SPACE, message, cause);
	}
	
	public TablenotAccessibleException(String errorCode,String message) {
		super(errorCode, message);
	}
	
	public TablenotAccessibleException(String errorCode,String message, Throwable cause) {
		super(errorCode, message, cause);
	}
}