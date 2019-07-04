/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * This class defines the UnSupportedLanguageException
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */

@Getter
public class UnSupportedLanguageException extends BaseUncheckedException {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private MainResponseDTO<?> mainResponseDTO;

	/**
	 * @param msg
	 */
	public UnSupportedLanguageException(String msg,MainResponseDTO<?> response) {
		super("", msg);
		this.mainResponseDTO=response;
	}

	/**
	 * @param errCode
	 * @param msg
	 */
	public UnSupportedLanguageException(String errCode, String msg,MainResponseDTO<?> response) {
		super(errCode, msg);
		this.mainResponseDTO=response;
	}

	/**
	 * @param errCode
	 * @param msg
	 * @param cause
	 */
	public UnSupportedLanguageException(String errCode, String msg, Throwable cause,MainResponseDTO<?> response) {
		super(errCode, msg, cause);
		this.mainResponseDTO=response;
	}
}
