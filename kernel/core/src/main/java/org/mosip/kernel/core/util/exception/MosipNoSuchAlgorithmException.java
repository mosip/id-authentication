package org.mosip.kernel.core.util.exception;

import org.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Base class for all preconditions violation exceptions.
 * 
 * @author Omsaieswar Mulakaluri
 * @since 1.0.0
 */
public class MosipNoSuchAlgorithmException extends BaseUncheckedException {

	/** Serializable version Id. */
	private static final long serialVersionUID = 1L;

	/**
	 * @param arg0
	 *            Error Code Corresponds to Particular Exception
	 * @param arg1
	 *            Message providing the specific context of the error.
	 * @param arg2
	 *            Cause of exception
	 */
	public MosipNoSuchAlgorithmException(String arg0, String arg1, Throwable arg2) {
		super(arg0, arg1, arg2);

	}

}
