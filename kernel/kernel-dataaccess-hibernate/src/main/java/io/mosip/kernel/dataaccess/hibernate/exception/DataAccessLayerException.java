package io.mosip.kernel.dataaccess.hibernate.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.dataaccess.hibernate.constant.HibernateErrorCode;

/**
 * Custom class for DataAccessLayerException
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class DataAccessLayerException extends BaseUncheckedException {

	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = 5074628123959874252L;

	/**
	 * Constructor for DataAccessLayerException
	 * 
	 * @param errorCode
	 *            The errorcode
	 * @param errorMessage
	 *            The errormessage
	 * @param cause
	 *            The cause
	 */
	public DataAccessLayerException(HibernateErrorCode errorCode, String errorMessage, Throwable cause) {
		super(errorCode.getErrorCode(), errorMessage, cause);
	}
}
