package io.mosip.kernel.core.exception;

/**
 * Base class for arithmetic exceptions.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @since 1.0.0
 */
public class ArithmeticException extends BaseUncheckedException {

	/** Serializable version Id. */
	private static final long serialVersionUID = 834721102100630614L;

	/**
	 * @param arg0
	 *            Error Code Corresponds to Particular Exception
	 * @param arg1
	 *            Message providing the specific context of the error.
	 * @param arg2
	 *            Cause of exception
	 */
	public ArithmeticException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);

	}

}
