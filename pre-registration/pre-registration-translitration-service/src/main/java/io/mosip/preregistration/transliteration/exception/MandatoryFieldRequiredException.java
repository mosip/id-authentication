/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.transliteration.errorcode.ErrorCodes;
import lombok.Getter;

/**
 * This class defines the MandatoryFieldRequiredException
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */

@Getter
public class MandatoryFieldRequiredException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -620822827826136129L;
	
	private MainResponseDTO<?> mainResponseDTO;

	public MandatoryFieldRequiredException(String msg,MainResponseDTO<?> response) {
		super("", msg);
		this.mainResponseDTO=response;
	}

	public MandatoryFieldRequiredException(String msg, Throwable cause,MainResponseDTO<?> response) {
		super("", msg, cause);
		this.mainResponseDTO=response;
	}

	public MandatoryFieldRequiredException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}

	public MandatoryFieldRequiredException(String errorCode, String errorMessage, Throwable rootCause,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, rootCause);
		this.mainResponseDTO=response;
	}

	public MandatoryFieldRequiredException() {
		super();
	}

}
