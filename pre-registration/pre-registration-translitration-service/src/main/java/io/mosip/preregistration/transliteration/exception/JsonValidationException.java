/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * This class defines the JsonValidationException
 * 
 * @author Rajath KR
 * @since 1.0.0
 * 
 */

@Getter
public class JsonValidationException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private MainResponseDTO<?> mainResponseDto;

	/**
	 * @param msg
	 */
	public JsonValidationException(String msg,MainResponseDTO<?> response) {
		super("", msg);
		this.mainResponseDto=response;
	}

	/**
	 * @param errCode
	 * @param msg
	 */
	public JsonValidationException(String errCode, String msg,MainResponseDTO<?> response) {
		super(errCode, msg);
		this.mainResponseDto=response;
	}

	/**
	 * @param errCode
	 * @param msg
	 * @param cause
	 */
	public JsonValidationException(String errCode, String msg, Throwable cause,MainResponseDTO<?> response) {
		super(errCode, msg, cause);
		this.mainResponseDto=response;
	}

}
