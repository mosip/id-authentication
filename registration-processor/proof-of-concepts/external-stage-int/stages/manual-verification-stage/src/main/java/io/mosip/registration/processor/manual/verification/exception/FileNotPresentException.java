package io.mosip.registration.processor.manual.verification.exception;
	
import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * The Class FileNotPresentException.
 */
public class FileNotPresentException extends BaseUncheckedException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new file not present exception.
	 *
	 * @param code the code
	 * @param message the message
	 */
	public FileNotPresentException(String code, String message){
		super(code, message);
	}

}
