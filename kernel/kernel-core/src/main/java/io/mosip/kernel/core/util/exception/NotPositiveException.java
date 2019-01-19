package io.mosip.kernel.core.util.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception to be thrown when the argument is negative.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public class NotPositiveException extends BaseUncheckedException {

	/** Serializable version Id. */
	private static final long serialVersionUID = 764722202100630634L;

	/**
	 * @param arg0
	 *            Error Code Corresponds to Particular Exception
	 * @param arg1
	 *            Message providing the specific context of the error.
	 * @param arg2
	 *            Cause of exception
	 */
	public NotPositiveException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);

	}

}
