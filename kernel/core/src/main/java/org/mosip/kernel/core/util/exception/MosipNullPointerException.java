package org.mosip.kernel.core.util.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Exception to be thrown when a null argument found.
 * 
 * @author Ritesh Sinha
 * @author Sagar Mahapatra
 * @author Priya Soni
 * @since 1.0.0
 */
public class MosipNullPointerException extends BaseUncheckedException {

	/** Serializable version Id. */
	private static final long serialVersionUID = 784321102100630614L;

	/**
	 * @param arg0
	 *            Error Code Corresponds to Particular Exception
	 * @param arg1
	 *            Message providing the specific context of the error.
	 * @param arg2
	 *            Cause of exception
	 */
	public MosipNullPointerException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);

	}

}
