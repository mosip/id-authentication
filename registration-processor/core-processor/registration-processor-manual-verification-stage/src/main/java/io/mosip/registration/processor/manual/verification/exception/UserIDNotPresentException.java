package io.mosip.registration.processor.manual.verification.exception;
import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * The Class UserIDNotPresentException.
 */
public class UserIDNotPresentException extends BaseUncheckedException{
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new file not present exception.
	 *
	 * @param code the code
	 * @param message the message
	 */
	public UserIDNotPresentException(String code, String message) {
		super(code, message);
	}
}
