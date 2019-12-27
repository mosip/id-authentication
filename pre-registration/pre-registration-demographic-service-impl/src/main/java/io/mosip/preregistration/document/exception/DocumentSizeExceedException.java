/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.document.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * This class defines the DocumentSizeExceedException that occurs when the
 * document size exceeds the limit
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */

@Getter
public class DocumentSizeExceedException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4879473387592007255L;
	
	private MainResponseDTO<?> response;

	/**
	 * Default constructor
	 */
	public DocumentSizeExceedException() {
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
	public DocumentSizeExceedException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 */
	public DocumentSizeExceedException(String errorCode, String message) {
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
	public DocumentSizeExceedException(String errorCode, String message,MainResponseDTO<?> response) {
		super(errorCode, message);
		this.response=response;
	}

}
