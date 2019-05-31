package io.mosip.registration.processor.manual.verification.exception;
	
import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * The Class InvalidUpdateException.
 */
public class InvalidUpdateException extends BaseUncheckedException{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new invalid update exception.
	 *
	 * @param code the code
	 * @param message the message
	 */
	public InvalidUpdateException(String code, String message){
		super(code, message);
	}
}
