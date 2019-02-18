/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.transliteration.errorcode.ErrorCodes;

/**
 * This class defines the MandatoryFieldRequiredException
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
public class MandatoryFieldRequiredException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -620822827826136129L;

	public MandatoryFieldRequiredException(String msg) {
		super("", msg);
	}

	public MandatoryFieldRequiredException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public MandatoryFieldRequiredException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}

	public MandatoryFieldRequiredException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public MandatoryFieldRequiredException() {
		super();
	}

}
