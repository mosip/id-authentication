package org.mosip.kernel.core.util.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Base class for all preconditions violation exceptions.
 * 
 * @author Ritesh Sinha
 * @author Sidhant Agarwal
 * @author Sagar Mahapatra
 * @author Ravi Balaji
 * @author Priya Soni
 * @since 1.0.0
 */
public class MosipIllegalArgumentException extends BaseUncheckedException {
	/** Serializable version Id. */
	private static final long serialVersionUID = 924722202110630628L;

	/**
	 * @param arg0
	 *            Error Code Corresponds to Particular Exception
	 * @param arg1
	 *            Message providing the specific context of the error.
	 * @param arg2
	 *            Cause of exception
	 */
	public MosipIllegalArgumentException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);

	}

}
