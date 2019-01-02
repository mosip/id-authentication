/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.transliteration.errorcode.ErrorCodes;

/**
 * This class defines the IllegalParamException
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
public class MandatoryFieldRequiredException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -620822827826136129L;

	public MandatoryFieldRequiredException() {
		super();
	}

	public MandatoryFieldRequiredException(String message) {
		super(ErrorCodes.PRG_TRL_APP_002.getCode(), message);
	}

}
