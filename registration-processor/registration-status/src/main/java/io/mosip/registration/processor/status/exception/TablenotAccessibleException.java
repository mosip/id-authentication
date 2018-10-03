package io.mosip.registration.processor.status.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import io.mosip.registration.processor.status.exception.utils.RegistrationStatusErrorCodes;

/**
 * TablenotAccessibleException occurs when system is not able to access enrolment status table.
 *
 */
public class TablenotAccessibleException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	public TablenotAccessibleException() {
		super();
	}

	public TablenotAccessibleException(String message) {
		super(RegistrationStatusErrorCodes.IIS_EPU_ATU_ENROLMENT_STATUS_TABLE_NOTACCESSIBLE, message);
	}

	public TablenotAccessibleException(String message, Throwable cause) {
		super(RegistrationStatusErrorCodes.IIS_EPU_ATU_ENROLMENT_STATUS_TABLE_NOTACCESSIBLE + EMPTY_SPACE, message, cause);
	}
}