package io.mosip.kernel.masterdata.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;

/**
 * Custom Exception Class in case of error while fetching document category or
 * Language code is not found
 * 
 * @author Neha
 * @author Uday Kumar
 * 
 * @since 1.0.0
 *
 */
public class DocumentCategoryNotFoundException extends BaseUncheckedException {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	/**
	 * Constructor the initialize Handler exception
	 * 
	 * @param errorCode
	 *            The errorcode for this exception
	 * @param errorMessage
	 *            The error message for this exception
	 */
	public DocumentCategoryNotFoundException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}

}
