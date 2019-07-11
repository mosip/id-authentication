/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * This class defines the MissingRequestParameterException
 * 
 * @author Ravi C Balaji
 * @since 1.0.0
 * 
 */

@Getter
public class MissingRequestParameterException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	private MainResponseDTO<?> mainResponseDTO;

	/**
	 * @param msg
	 */
	public MissingRequestParameterException(String msg,MainResponseDTO<?> response) {
		super("", msg);
		this.mainResponseDTO=response;
	}

	/**
	 * @param errCode
	 * @param msg
	 */
	public MissingRequestParameterException(String errCode, String msg,MainResponseDTO<?> response) {
		super(errCode, msg);
		this.mainResponseDTO=response;
	}

	/**
	 * @param errCode
	 * @param msg
	 * @param cause
	 */
	public MissingRequestParameterException(String errCode, String msg, Throwable cause,MainResponseDTO<?> response) {
		super(errCode, msg, cause);
		this.mainResponseDTO=response;
	}
}
