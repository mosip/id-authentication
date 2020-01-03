/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.document.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * This class defines the DocumentFailedToCopyException that occurs when the
 * document fails to copy
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */

@Getter
public class DocumentFailedToCopyException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7303748392658525834L;
	
	private MainResponseDTO<?> response;

	/**
	 * Default constructor
	 */
	public DocumentFailedToCopyException() {
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
	public DocumentFailedToCopyException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 */
	public DocumentFailedToCopyException(String errorCode, String message) {
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
	public DocumentFailedToCopyException(String errorCode, String message,MainResponseDTO<?> response) {
		super(errorCode, message);
		this.response=response;
	}

}
