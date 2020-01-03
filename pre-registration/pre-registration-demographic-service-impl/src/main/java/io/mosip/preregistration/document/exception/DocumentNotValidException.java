/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.document.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * This class defines the DocumentNotValidException that occurs when document is
 * invalid
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */

@Getter
public class DocumentNotValidException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5252109871704396987L;
	
	private MainResponseDTO<?> response;

	/**
	 * Default constructor
	 */
	public DocumentNotValidException() {
		super();

	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 * @param cause
	 *            pass Error cause
	 */
	public DocumentNotValidException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 */
	public DocumentNotValidException(String errorCode, String message) {
		super(errorCode, message);
	}
	
	/**
	 * 
	 * @param errorCode
	 *    	     pass Error code
	 * @param message
	 *           pass Error Message
	 * @param response
	 * 			 pass response
	 */
	public DocumentNotValidException(String errorCode, String message,MainResponseDTO<?> response) {
		super(errorCode, message);
		this.response=response;
	}

}
