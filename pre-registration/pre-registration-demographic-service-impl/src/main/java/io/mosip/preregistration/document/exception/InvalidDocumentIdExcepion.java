/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.document.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * This class defines the DocumentFailedToUploadException that occurs when
 * document upload fails
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 * 
 */

@Getter
public class InvalidDocumentIdExcepion extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7303748392658525834L;
	
	private MainResponseDTO<?> response;

	/**
	 * Default constructor
	 */
	public InvalidDocumentIdExcepion() {
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
	public InvalidDocumentIdExcepion(String errorCode, String message, Throwable cause) {
		super(errorCode, message, cause);
	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 */
	public InvalidDocumentIdExcepion(String errorCode, String message) {
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
	public InvalidDocumentIdExcepion(String errorCode, String message,MainResponseDTO<?> response) {
		super(errorCode, message);
		this.response=response;
	}

}
