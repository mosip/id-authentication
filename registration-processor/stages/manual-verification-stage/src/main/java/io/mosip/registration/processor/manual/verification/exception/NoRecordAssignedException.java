package io.mosip.registration.processor.manual.verification.exception;
	
import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * The Class NoRecordAssignedException.
 */
public class NoRecordAssignedException extends BaseUncheckedException {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new no record assigned exception.
	 *
	 * @param code the code
	 * @param message the message
	 */
	public NoRecordAssignedException(String code, String message){
		super(code, message);
	}

}
