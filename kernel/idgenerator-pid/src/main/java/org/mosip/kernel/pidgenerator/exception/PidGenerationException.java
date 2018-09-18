package org.mosip.kernel.pidgenerator.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.springframework.stereotype.Component;

/**
 * @author Kishan rathore
 * @see 1.0.0
 *
 */
@Component
public class PidGenerationException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * No Argument Constructor
	 */
	public PidGenerationException() {
		super();
		
	}

	/**
	 * Constructor the initialize PreIdGenerationException
	 * 
	 * @param errorCode for this exception
	 * 
	 * @param errorMessage for this exception
	 */
	public PidGenerationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
		
	}
	
	

}
