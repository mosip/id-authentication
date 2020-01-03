/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.document.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * This class defines the DocumentVirusScanException that occurs when the virus
 * scan for a document fails
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */

@Getter
public class DocumentVirusScanException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8143377803310016937L;
	
	private MainResponseDTO<?> response;

	/**
	 * Default constructor
	 */
	public DocumentVirusScanException() {
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
	public DocumentVirusScanException(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 */
	public DocumentVirusScanException(String errorCode, String message) {
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
	public DocumentVirusScanException(String errorCode, String message,MainResponseDTO<?> response) {
		super(errorCode, message);
		this.response=response;
	}
}
