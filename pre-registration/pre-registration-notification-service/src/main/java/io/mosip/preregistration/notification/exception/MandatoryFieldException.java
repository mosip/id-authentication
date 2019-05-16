package io.mosip.preregistration.notification.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author Sanober Noor
 *@since 1.0.0
 */

@Getter
public class MandatoryFieldException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1298682891599963309L;
	private final MainResponseDTO<?> mainResponseDTO;

	public MandatoryFieldException(String msg,MainResponseDTO<?> response) {
		super("", msg);
		this.mainResponseDTO=response;
	}

	public MandatoryFieldException(String msg, Throwable cause,MainResponseDTO<?> response) {
		super("", msg, cause);
		this.mainResponseDTO=response;
	}

	public MandatoryFieldException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResponseDTO=response;
	}

	public MandatoryFieldException(String errorCode, String errorMessage, Throwable rootCause,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, rootCause);
		this.mainResponseDTO=response;
	}

	
}
