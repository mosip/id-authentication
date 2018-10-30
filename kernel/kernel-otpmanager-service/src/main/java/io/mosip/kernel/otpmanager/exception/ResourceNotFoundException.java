package io.mosip.kernel.otpmanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**Exception class when the resource required is not found.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @version 1.0.0
 *
 */
public class ResourceNotFoundException extends BaseUncheckedException {

	/**
	 * Generated ID.
	 */
	private static final long serialVersionUID = -6525741819333966907L;

	/**
	 * Constructor for MosipNullArgumentException class.
	 * 
	 * @param errorCode
	 *            the error code
	 * @param errorMessage
	 *            the error message
	 */
	public ResourceNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}
