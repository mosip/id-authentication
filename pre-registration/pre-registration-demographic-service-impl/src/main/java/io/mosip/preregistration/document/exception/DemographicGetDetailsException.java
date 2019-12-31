package io.mosip.preregistration.document.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;


/**
 * @author M1046129
 *
 */

@Getter
public class DemographicGetDetailsException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;
	
	private MainResponseDTO<?> response;
	
	/**
	 * @param errorCode
	 *            pass Error code
	 * @param errorMessage
	 *            pass Error Message
	 */

	public DemographicGetDetailsException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

	/**
	 * @param errorCode
	 *            pass Error code
	 * @param message
	 *            pass Error Message
	 * @param cause
	 *            pass Error cause
	 */
	public DemographicGetDetailsException(String errorCode, String errorMessage, Throwable cause) {
		super(errorCode, errorMessage, cause);
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
	public DemographicGetDetailsException(String errorCode, String message,MainResponseDTO<?> response) {
		super(errorCode, message);
		this.response=response;
	}

}
