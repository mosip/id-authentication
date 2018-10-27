package io.mosip.registration.core.exceptions;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import org.springframework.stereotype.Component;

/**
 * @author M1037717
 * @see 1.0.0
 *
 */
@Component
public class GroupidGenerationException extends BaseUncheckedException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;





	/**
	 * No Argument Constructor
	 */
	public GroupidGenerationException() {
		super();
	}





	/**
	 * Constructor the initialize GroupidGenerationException
	 * 
	 * @param errorCode
	 *            for this exception
	 * 
	 * @param errorMessage
	 *            for this exception
	 */
	public GroupidGenerationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
