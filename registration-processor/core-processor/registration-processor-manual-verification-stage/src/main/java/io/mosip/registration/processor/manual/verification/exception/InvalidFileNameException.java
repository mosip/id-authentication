package io.mosip.registration.processor.manual.verification.exception;
	
import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * The Class InvalidFileNameException.
 */
public class InvalidFileNameException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new invalid file name exception.
	 *
	 * @param code the code
	 * @param message the message
	 */
	public InvalidFileNameException(String code, String message){
		super(code, message);
	}

}
