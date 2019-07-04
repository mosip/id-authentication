
/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.notification.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * This class defines the IllegalParamException
 * 
 * @author Sanober Noor
 * @since 1.0.0
 *
 */
@Getter
public class IllegalParamException extends BaseUncheckedException{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6810058264320216283L;
	private MainResponseDTO<?> mainResponseDto;
	/**
	 * @param msg
	 */
	public IllegalParamException(String msg,MainResponseDTO<?> response) {
		super("", msg);
		this.mainResponseDto=response;
	}

	/**
	 * @param errCode
	 * @param msg
	 */
	public IllegalParamException(String errCode, String msg,MainResponseDTO<?> response) {
		super(errCode, msg);
		this.mainResponseDto=response;
	}

	/**
	 * @param errCode
	 * @param msg
	 * @param cause
	 */
	public IllegalParamException(String errCode, String msg, Throwable cause,MainResponseDTO<?> response) {
		super(errCode, msg, cause);
		this.mainResponseDto=response;
	}

}

