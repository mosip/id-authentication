package io.mosip.registration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.exception.utils.RegistrationErrorCodes;

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
		super(RegistrationErrorCodes.IIS_EPU_ATU_REGISTRATION_TABLE_NOTACCESSIBLE, message);
	}

	public TablenotAccessibleException(String message, Throwable cause) {
		super(RegistrationErrorCodes.IIS_EPU_ATU_REGISTRATION_TABLE_NOTACCESSIBLE + EMPTY_SPACE, message, cause);
	}
}