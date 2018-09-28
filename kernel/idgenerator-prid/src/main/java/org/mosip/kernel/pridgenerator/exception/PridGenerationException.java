package org.mosip.kernel.pridgenerator.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;
import org.springframework.stereotype.Component;

/**
 * @author M1037462
 * @see 1.0.0
 *
 */
@Component
public class PridGenerationException extends BaseUncheckedException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;





	/**
	 * No Argument Constructor
	 */
	public PridGenerationException() {
		super();
	}





	/**
	 * Constructor the initialize PreIdGenerationException
	 * 
	 * @param errorCode
	 *            for this exception
	 * 
	 * @param errorMessage
	 *            for this exception
	 */
	public PridGenerationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
