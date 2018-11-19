package io.mosip.kernel.core.auditmanager.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom Exception Class in case of Handler Exception in Audit manager
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class AuditManagerException extends BaseUncheckedException {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = 8621530697947108810L;

	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode
	 *            The errorcode for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public AuditManagerException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
