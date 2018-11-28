package io.mosip.registration.processor.status.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;

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
		super(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getCode(), message);
	}

	public TablenotAccessibleException(String message, Throwable cause) {
		super(PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getCode() + EMPTY_SPACE, message, cause);
	}
}