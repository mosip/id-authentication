package io.mosip.preregistration.translitration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.translitration.errorcode.ErrorCodes;

public class MandatoryFieldRequiredException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -620822827826136129L;

	public MandatoryFieldRequiredException() {
		super();
	}

	public MandatoryFieldRequiredException(String message) {
		super(ErrorCodes.PRG_TRL_002.toString(), message);
	}

}
